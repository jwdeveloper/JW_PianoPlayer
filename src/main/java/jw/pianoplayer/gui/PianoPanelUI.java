package jw.pianoplayer.gui;

import jw.spigot_fluent_api.dependency_injection.InjectionType;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.gui.button.button_observer.ButtonObserverUI;
import jw.spigot_fluent_api.gui.implementation.chest_ui.ChestUI;
import jw.spigot_fluent_api.gui.implementation.picker_list_ui.FilePickerUI;
import jw.spigot_fluent_api.gui.implementation.picker_list_ui.MaterialPickerUI;
import jw.spigot_fluent_api.utilites.messages.Emoticons;
import jw.spigot_fluent_api.utilites.messages.MessageBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@SpigotBean(injectionType = InjectionType.TRANSIENT)
public class PianoPanelUI extends ChestUI {
    private final PianoPanelController pianoPanelController;

    public PianoPanelUI(PianoPanelController pianoPanelController) {
        super("Piano panel", 6);
        this.pianoPanelController = pianoPanelController;
        this.pianoPanelController.setGui(this);
    }

    @Override
    public void onInitialize() {
        setBorderMaterial(Material.LIME_STAINED_GLASS_PANE);
        setTitle(new MessageBuilder().color(ChatColor.DARK_GREEN)
                .color(ChatColor.BOLD)
                .text(Emoticons.music)
                .text(" MIDI player ")
                .text(Emoticons.music)
                .toString());

        var materialPicker = new MaterialPickerUI("select key material", 6);
        materialPicker.addBlockFilter();
        materialPicker.applyFilters();

        var filePicker = new FilePickerUI("Select Midi file", 6);
        filePicker.addContentFilter(input ->
        {
            return input.contains(".mid") || input.contains(".midi");
        });
        filePicker.applyFilters();

        var createPianoBtn = ButtonObserverUI.builder()
                .setTitle("Create keyboard")
                .setLocation(0, 4)
                .addObserver(pianoPanelController.locationButtonObserver())
                .buildAndAdd(this);

        var whiteKeyPressed = ButtonObserverUI.builder()
                .setTitle("White key pressed")
                .setLocation(4, 1)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.keyWhitePressObserver(materialPicker))
                .buildAndAdd(this);

        var whiteKeyReleased = ButtonObserverUI.builder()
                .setTitle("White key released")
                .setLocation(4, 3)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.keyWhiteReleaseObserver(materialPicker))
                .buildAndAdd(this);

        var blackKeyPressed = ButtonObserverUI.builder()
                .setTitle("Black key pressed")
                .setLocation(4, 5)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.keyDarkPressObserver(materialPicker))
                .buildAndAdd(this);

        var blackKeyReleased = ButtonObserverUI.builder()
                .setTitle("Black key released")
                .setLocation(4, 7)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.keyDarkReleaseBindObserver(materialPicker))
                .buildAndAdd(this);

        var playButton = ButtonObserverUI.builder()
                .setTitle("Is playing")
                .setLocation(2, 2)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.playerButtonObserver())
                .buildAndAdd(this);

        var teleportButton = ButtonObserverUI.builder()
                .setTitle("Teleport to piano")
                .setLocation(3, 4)
                .setMaterial(Material.ENDER_PEARL)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.teleportButtonObserver())
                .buildAndAdd(this);

        var selectMidiFileButton = ButtonObserverUI.builder()
                .setTitle("Select MIDI file")
                .setLocation(2, 4)
                .setMaterial(Material.NOTE_BLOCK)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.selectMidiFileButtonObserver(filePicker))
                .buildAndAdd(this);

        var volumeButton = ButtonObserverUI.builder()
                .setTitle("Volume")
                .setLocation(2, 6)
                .setMaterial(Material.MUSIC_DISC_STRAD)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.volumeButtonObserver())
                .buildAndAdd(this);

        var lightButton = ButtonObserverUI.builder()
                .setTitle("Enable light")
                .setLocation(3, 2)
                .setMaterial(Material.LIGHT)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.lightButtonObserver())
                .buildAndAdd(this);
    }
}
