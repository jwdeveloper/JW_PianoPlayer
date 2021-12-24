package jw.pianoplayer.gui;

import jw.pianoplayer.events.PianoEventListener;
import jw.pianoplayer.services.PianoPlayerService;
import jw.pianoplayer.services.SettingsService;
import jw.pianoplayer.utilites.AudioUtility;
import jw.spigot_fluent_api.dependency_injection.InjectionType;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.gui.button.button_observer.ButtonObserver;
import jw.spigot_fluent_api.gui.implementation.accept_ui.AcceptUI;
import jw.spigot_fluent_api.gui.implementation.picker_list_ui.FilePickerUI;
import jw.spigot_fluent_api.gui.implementation.picker_list_ui.MaterialPickerUI;
import jw.spigot_fluent_api.initialization.FluentPlugin;
import jw.spigot_fluent_api.tasks.FluentTasks;
import jw.spigot_fluent_api.utilites.binding.Observable;
import jw.spigot_fluent_api.utilites.messages.MessageBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;


@SpigotBean(injectionType = InjectionType.TRANSIENT)
public class PianoPanelController {
    private final PianoPlayerService pianoPlayerService;
    private final PianoEventListener pianoEventListener;
    private final SettingsService settingsService;
    private PianoPanelUI pianoPanelUI;

    public PianoPanelController(PianoPlayerService pianoPlayerService,
                                PianoEventListener pianoEventListener,
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
                .observable(settingsService.getLocationBind())
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
                .observable(settingsService.getIsPlayingBind())
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

    public ButtonObserver<Location> teleportButtonObserver() {
        return ButtonObserver.<Location>builder()
                .observable(settingsService.getLocationBind())
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

    public ButtonObserver<String> selectMidiFileButtonObserver(FilePickerUI filePickerUI) {
        return ButtonObserver.<String>builder()
                .observable(settingsService.getLastPlayedMidiBind())
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

    public ButtonObserver<Integer> volumeButtonObserver() {
        return ButtonObserver.<Integer>builder()
                .observable(settingsService.getVolumeBind())
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

    public ButtonObserver<Boolean> lightButtonObserver() {
        return ButtonObserver.<Boolean>builder()
                .observable(settingsService.getIsLightEnableBind())
                .onClick(event ->
                {
                    event.getObserver().setValue(!event.getValue());
                }).onValueChange(event ->
                {
                    if (event.getValue()) {
                        event.getButton().setDescription("Disable");
                        event.getButton().setHighlighted(true);
                    } else {
                        event.getButton().setDescription("Enable");
                        event.getButton().setHighlighted(false);
                    }
                })
                .build();
    }

    public ButtonObserver<Material> keyWhitePressObserver(MaterialPickerUI materialPickerUI) {
        return keyMaterialButtonObserver(materialPickerUI, settingsService.getKeyWhitePressBind());
    }

    public ButtonObserver<Material> keyWhiteReleaseObserver(MaterialPickerUI materialPickerUI) {
        return keyMaterialButtonObserver(materialPickerUI, settingsService.getKeyWhiteReleaseBind());
    }

    public ButtonObserver<Material> keyDarkPressObserver(MaterialPickerUI materialPickerUI) {
        return keyMaterialButtonObserver(materialPickerUI, settingsService.getKeyDarkPressBind());
    }

    public ButtonObserver<Material> keyDarkReleaseBindObserver(MaterialPickerUI materialPickerUI) {
        return keyMaterialButtonObserver(materialPickerUI, settingsService.getKeyDarkReleaseBind());
    }

    public ButtonObserver<Boolean> isPianoCreatedObserver() {
        return ButtonObserver.<Boolean>builder()
                .observable(settingsService.getIsPianoPlacedBind())
                .onValueChange(event ->
                {
                 //   event.getButton().setActive(event.getValue());
                }).build();
    }

    private ButtonObserver<Material> keyMaterialButtonObserver(MaterialPickerUI materialPickerUI, Observable<Material> observable) {
        return ButtonObserver.<Material>builder()
                .observable(observable)
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
