package jw.pianoplayer.gui;

import jw.pianoplayer.events.PianoEventListener;
import jw.pianoplayer.services.PianoPlayerService;
import jw.pianoplayer.services.SettingsService;
import jw.pianoplayer.utilites.AudioUtility;
import jw.spigot_fluent_api.dependency_injection.InjectionType;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.gui.button.button_observer.ButtonObserver;
import jw.spigot_fluent_api.gui.implementation.picker_list_ui.FilePickerUI;
import jw.spigot_fluent_api.gui.implementation.picker_list_ui.MaterialPickerUI;
import jw.spigot_fluent_api.utilites.binding.Observable;
import org.bukkit.ChatColor;
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

    public ButtonObserver<Location> locationButtonObserver() {
        return ButtonObserver.<Location>builder()
                .observable(settingsService.getLocationBind())
                .onClick(event ->
                {
                    if (settingsService.getIsPlayingBind().get()) {
                        pianoPlayerService.destroyPiano();
                        settingsService.getIsPianoPlacedBind().set(false);
                        settingsService.getLocationBind().set(null);
                        return;
                    }

                    pianoPanelUI.close();
                    event.getPlayer().sendMessage(ChatColor.BOLD + "Destroy block to set location");
                    pianoEventListener.addPlayerBlockListener(event.getPlayer(), (block) ->
                    {
                        settingsService.getLocationBind().set(block.getLocation());
                        pianoPlayerService.createPiano(block.getLocation());
                        settingsService.getIsPianoPlacedBind().set(true);
                        pianoPanelUI.open(event.getPlayer());
                    });
                })
                .onValueChange(event ->
                {
                    var button = event.getButton();
                    if (settingsService.getIsPlayingBind().get()) {
                        button.setTitle(ChatColor.BOLD + "Destroy piano");
                        button.setMaterial(Material.BARRIER);
                    } else {
                        button.setTitle(ChatColor.BOLD + "Create piano");
                        button.setMaterial(Material.CRAFTING_TABLE);
                    }
                }).build();
    }

    public ButtonObserver<Boolean> playerButtonObserver() {
        return ButtonObserver.<Boolean>builder()
                .observable(settingsService.getIsPianoPlacedBind())
                .onClick(event ->
                {
                    if (!settingsService.getIsPianoPlacedBind().get()) {
                        pianoPanelUI.setTitle(ChatColor.RED + "     ! At first place piano !");
                        return;
                    }
                    if (settingsService.getLastPlayedMidiBind().get().length() == 0) {
                        pianoPanelUI.setTitle(ChatColor.RED + "     ! Select Midi file !");
                        return;
                    }
                    if (event.getValue())
                        pianoPlayerService.stop();
                    else {
                        var path = settingsService.midiFilesPath() + File.separator + settingsService.getLastPlayedMidiBind().get();
                        pianoPlayerService.play(path);
                    }

                    settingsService.getIsPianoPlacedBind().set(!event.getValue());
                })
                .onValueChange(event ->
                {
                    var button = event.getButton();
                    if (event.getValue()) {
                        button.setTitle("Stop");
                        button.setMaterial(Material.RED_WOOL);
                        pianoPanelUI.setBorderMaterial(Material.RED_STAINED_GLASS_PANE);
                    } else {
                        button.setTitle("Play");
                        button.setMaterial(Material.GREEN_WOOL);
                        pianoPanelUI.setBorderMaterial(Material.LIME_STAINED_GLASS_PANE);
                    }
                    pianoPanelUI.refresh();
                }).build();
    }

    public ButtonObserver<Location> teleportButtonObserver() {
        return ButtonObserver.<Location>builder()
                .observable(settingsService.getLocationBind())
                .onClick(event ->
                {
                    if (event.getValue() == null)
                        return;

                    var location = event.getValue();
                    event.getButton().setDescription(ChatColor.WHITE + "Location:",
                            "X: " + location.getBlockX(),
                            "Y: " + location.getBlockY(),
                            "Z: " + location.getBlockZ()
                    );
                })
                .onValueChange(event ->
                {
                    if (event.getValue() == null)
                        return;
                    event.getPlayer()
                            .teleport(event
                                    .getValue()
                                    .clone()
                                    .add(0, 2, 0));
                }).build();
    }

    public ButtonObserver<String> selectMidiFileButtonObserver(FilePickerUI filePickerUI) {
        return ButtonObserver.<String>builder()
                .observable(settingsService.getLastPlayedMidiBind())
                .onClick(event ->
                {
                    filePickerUI.setOnItemPicked((player, button) ->
                    {
                        String path = button.getDataContext();
                        event.getObserver().setValue(path);
                        pianoPanelUI.open(player);
                    });
                    filePickerUI.open(event.getPlayer());

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
                    event.getObserver().setValue(value);
                    AudioUtility.setApplicationVolume(value / 100.0f);
                }).onValueChange(event ->
                {
                    event.getButton().setDescription("Level: " + event.getValue().toString());
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
                        event.getButton().setDescription("Disable light");
                    } else {
                        event.getButton().setDescription("Enable light");
                    }
                })
                .build();
    }

    public ButtonObserver<Material> keyWhitePressObserver(MaterialPickerUI materialPickerUI)
    {
        return keyMaterialButtonObserver(materialPickerUI, settingsService.getKeyWhitePressBind());
    }

    public ButtonObserver<Material> keyWhiteReleaseObserver(MaterialPickerUI materialPickerUI)
    {
        return keyMaterialButtonObserver(materialPickerUI, settingsService.getKeyWhiteReleaseBind());
    }

    public ButtonObserver<Material> keyDarkPressObserver(MaterialPickerUI materialPickerUI)
    {
        return keyMaterialButtonObserver(materialPickerUI, settingsService.getKeyDarkPressBind());
    }

    public ButtonObserver<Material> keyDarkReleaseBindObserver(MaterialPickerUI materialPickerUI)
    {
        return keyMaterialButtonObserver(materialPickerUI, settingsService.getKeyDarkReleaseBind());
    }

    public ButtonObserver<Boolean> isPianoCreatedObserver()
    {
        return ButtonObserver.<Boolean>builder()
                .observable(settingsService.getIsPianoPlacedBind())
                .onValueChange(event ->
                {
                  event.getButton().setActive(event.getValue());
                }).build();
    }

    private ButtonObserver<Material> keyMaterialButtonObserver(MaterialPickerUI materialPickerUI, Observable<Material> observable)
    {
        return ButtonObserver.<Material>builder()
                .observable(observable)
                .onClick(event ->
                {
                    materialPickerUI.setOnItemPicked((player, button) ->
                    {
                        Material material = button.getDataContext();
                        event.getObserver().setValue(material);
                        pianoPanelUI.open(player);
                    });
                    materialPickerUI.open(event.getPlayer());
                }).build();
    }
}
