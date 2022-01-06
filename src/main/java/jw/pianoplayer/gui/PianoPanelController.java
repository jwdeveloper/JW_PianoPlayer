package jw.pianoplayer.gui;

import jw.pianoplayer.listeners.PianoListener;
import jw.pianoplayer.services.PianoPlayerService;
import jw.pianoplayer.services.SettingsService;
import jw.pianoplayer.utilites.AudioUtility;
import jw.spigot_fluent_api.dependency_injection.InjectionType;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.fluent_gui.button.button_observer.ButtonObserver;
import jw.spigot_fluent_api.fluent_gui.implementation.accept_ui.AcceptUI;
import jw.spigot_fluent_api.fluent_gui.implementation.picker_list_ui.FilePickerUI;
import jw.spigot_fluent_api.fluent_gui.implementation.picker_list_ui.MaterialPickerUI;
import jw.spigot_fluent_api.fluent_tasks.FluentTasks;
import jw.spigot_fluent_api.utilites.binding.Observable;
import jw.spigot_fluent_api.utilites.binding.implementation.BooleanButtonObserver;
import jw.spigot_fluent_api.utilites.messages.MessageBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;


@SpigotBean(injectionType = InjectionType.TRANSIENT)
public class PianoPanelController {
    private final PianoPlayerService pianoPlayerService;
    private final PianoListener pianoEventListener;
    private final SettingsService settingsService;
    private PianoPanelUI pianoPanelUI;

    public PianoPanelController(PianoPlayerService pianoPlayerService,
                                PianoListener pianoEventListener,
                                SettingsService settingsService) {
        this.pianoPlayerService = pianoPlayerService;
        this.pianoEventListener = pianoEventListener;
        this.settingsService = settingsService;
    }

    public void setGui(PianoPanelUI pianoPanelUI) {
        this.pianoPanelUI = pianoPanelUI;
    }

    public ButtonObserver<Location> createPianoObserver(AcceptUI acceptUI) {
        return ButtonObserver.<Location>builder()
                .withObserver(settingsService.getLocationBind())
                .onClick(event ->
                {
                    var isPianoPlaced = settingsService.getIsPianoPlacedBind().get();
                    if (isPianoPlaced) {
                        acceptUI.onUserChoice(value ->
                        {
                            if (value) {
                                pianoPlayerService.destroyPiano();
                                settingsService.getIsPianoPlacedBind().set(false);
                                settingsService.getLocationBind().set(null);
                            }
                            pianoPanelUI.open(event.getPlayer());
                        });
                        acceptUI.open(event.getPlayer(), new MessageBuilder()
                                .color(ChatColor.DARK_GRAY)
                                .bold("Are you sure about that?")
                                .toString());
                    } else {
                        pianoPanelUI.close();
                        event.getPlayer().sendMessage(ChatColor.BOLD + "Destroy block to set location");
                        pianoEventListener.addPlayerBlockListener(event.getPlayer(), (block) ->
                        {
                            settingsService.getIsPianoPlacedBind().set(true);
                            settingsService.getLocationBind().set(block.getLocation());
                            pianoPanelUI.open(event.getPlayer());
                            FluentTasks.task(unused ->
                            {
                                pianoPlayerService.createPiano(block.getLocation());
                            });
                        });
                    }
                })
                .onValueChange(event ->
                {
                    var button = event.getButton();
                    if (settingsService.getIsPianoPlacedBind().get()) {
                        button.setTitle(PianoMessages.destroyPianoMessage());
                        button.setMaterial(Material.BARRIER);
                    } else {
                        button.setTitle(PianoMessages.createPianoMessage());
                        button.setMaterial(Material.CRAFTING_TABLE);
                    }
                }).build();
    }

    public ButtonObserver<Boolean> isPianoPlayingObserver() {
        return ButtonObserver.<Boolean>builder()
                .withObserver(settingsService.getIsPlayingBind())
                .onClick(event ->
                {
                    if (!settingsService.getIsPianoPlacedBind().get()) {
                        pianoPanelUI.setTitle(PianoMessages.placePianoError());
                        return;
                    }
                    if (settingsService.getLastPlayedMidiBind().get().length() == 0) {

                        pianoPanelUI.setTitle(PianoMessages.setMidiFileError());
                        return;
                    }
                    if (event.getValue()) {
                        pianoPlayerService.stop();
                        event.getObserver().setValue(false);
                    } else {
                        var path = settingsService.midiFilesPath() + settingsService.getLastPlayedMidiBind().get();
                        var startPlaying = pianoPlayerService.play(path);
                        if(!startPlaying)
                            pianoPanelUI.setTitle(PianoMessages.playMidiFileError());
                        event.getObserver().setValue(startPlaying);
                    }

                })
                .onValueChange(event ->
                {
                    var button = event.getButton();
                    if (event.getValue()) {
                        button.setTitle(PianoMessages.playingStateStop());
                        button.setMaterial(Material.RED_WOOL);
                        pianoPanelUI.setBorderMaterial(Material.RED_STAINED_GLASS_PANE);
                    } else {
                        button.setTitle(PianoMessages.playingStatePlay());
                        button.setMaterial(Material.GREEN_WOOL);
                        pianoPanelUI.setBorderMaterial(Material.LIME_STAINED_GLASS_PANE);
                    }
                    pianoPanelUI.refreshBorder();
                }).build();
    }

    public ButtonObserver<Location> teleportObserver() {
        return ButtonObserver.<Location>builder()
                .withObserver(settingsService.getLocationBind())
                .onClick(event ->
                {
                    if (event.getValue() == null || event.getPlayer() == null)
                        return;

                    var player = event.getPlayer();
                    var loc = event.getValue().clone();
                    loc.add(-10, 10, 44);
                    loc.setPitch(player.getLocation().getPitch());
                    loc.setYaw(player.getLocation().getYaw());
                    event.getPlayer().teleport(loc);
                }) .build();
    }

    public ButtonObserver<String> selectMidiFileObserver(FilePickerUI filePickerUI) {
        return ButtonObserver.<String>builder()
                .withObserver(settingsService.getLastPlayedMidiBind())
                .onClick(event ->
                {
                    filePickerUI.setPath(settingsService.midiFilesPath());
                    filePickerUI.setOnItemPicked((player, button) ->
                    {
                        String path = button.getDataContext();
                        event.getObserver().setValue(path);
                        pianoPanelUI.open(player);

                    });
                    filePickerUI.open(event.getPlayer());

                }).onValueChange(value ->
                {
                    var message = new MessageBuilder().color(ChatColor.BOLD);
                    if (value.getValue() == null || value.getValue() == "") {
                        message.color(ChatColor.RED)
                                .inBrackets("File not selected");
                    } else {
                        message.color(ChatColor.DARK_GREEN)
                                .inBrackets("Current file");
                    }
                    message.space()
                            .color(ChatColor.WHITE)
                            .text(value.getValue()).toString();

                    value.getButton().setDescription(message);
                }).build();
    }

    public ButtonObserver<Integer> volumeObserver() {
        return ButtonObserver.<Integer>builder()
                .withObserver(settingsService.getVolumeBind())
                .onClick(event ->
                {
                    var value = event.getObserver().getValue();
                    value += 10;
                    if (value > 100)
                        value = value % 100;
                    final int valueToSet = value;

                    settingsService.getVolumeBind().set(valueToSet);
                    FluentTasks.task(unused ->
                    {
                        AudioUtility.setApplicationVolume(valueToSet / 100.0f);
                    });
                }).onValueChange(event ->
                {
                    event.getButton().setDescription(PianoMessages.audioLevel(event.getValue()));
                }).build();
    }

    public ButtonObserver<Boolean> lightObserver() {
        return BooleanButtonObserver.create(settingsService.getIsLightEnableBind());
    }

    public ButtonObserver<Boolean> infoBarObserver() {
        return BooleanButtonObserver.create(settingsService.getIsInfoBarBind());
    }

    public ButtonObserver<Material> keyWhitePressObserver(MaterialPickerUI materialPickerUI) {
        return keyMaterialObserver(materialPickerUI, settingsService.getKeyWhitePressBind());
    }

    public ButtonObserver<Material> keyWhiteReleaseObserver(MaterialPickerUI materialPickerUI) {
        return keyMaterialObserver(materialPickerUI, settingsService.getKeyWhiteReleaseBind());
    }

    public ButtonObserver<Material> keyDarkPressObserver(MaterialPickerUI materialPickerUI) {
        return keyMaterialObserver(materialPickerUI, settingsService.getKeyDarkPressBind());
    }

    public ButtonObserver<Material> keyDarkReleaseBindObserver(MaterialPickerUI materialPickerUI) {
        return keyMaterialObserver(materialPickerUI, settingsService.getKeyDarkReleaseBind());
    }

    public ButtonObserver<Boolean> isPianoCreatedObserver() {
        return ButtonObserver.<Boolean>builder()
                .withObserver(settingsService.getIsPianoPlacedBind())
                .onValueChange(event ->
                {
                 //   event.getButton().setActive(event.getValue());
                }).build();
    }

    private ButtonObserver<Material> keyMaterialObserver(MaterialPickerUI materialPickerUI, Observable<Material> observable) {
        return ButtonObserver.<Material>builder()
                .withObserver(observable)
                .onClick(event ->
                {
                    materialPickerUI.setOnItemPicked((player, button) ->
                    {
                        Material material = button.getDataContext();
                        event.getObserver().setValue(material);
                        pianoPanelUI.open(player);

                        FluentTasks.task(unused ->
                        {
                            pianoPlayerService.refreshKeys();
                        });
                    });
                    materialPickerUI.open(event.getPlayer());
                }).onValueChange(event ->
                {
                    if (event.getValue() == null)
                        return;

                    event.getButton().setMaterial(event.getValue());
                }).build();

    }
}
