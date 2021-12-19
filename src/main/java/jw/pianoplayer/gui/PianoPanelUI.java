package jw.pianoplayer.gui;

import jw.spigot_fluent_api.dependency_injection.InjectionType;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.gui.button.button_observer.ButtonObserverUI;
import jw.spigot_fluent_api.gui.implementation.accept_ui.AcceptUI;
import jw.spigot_fluent_api.gui.implementation.chest_ui.ChestUI;
import jw.spigot_fluent_api.gui.implementation.picker_list_ui.FilePickerUI;
import jw.spigot_fluent_api.gui.implementation.picker_list_ui.MaterialPickerUI;
import jw.spigot_fluent_api.initialization.FluentPlugin;
import jw.spigot_fluent_api.utilites.messages.Emoticons;
import jw.spigot_fluent_api.utilites.messages.MessageBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@SpigotBean(injectionType = InjectionType.TRANSIENT)
public class PianoPanelUI extends ChestUI {
    private final PianoPanelController pianoPanelController;

    public PianoPanelUI(PianoPanelController pianoPanelController) {
        super("Piano panel", 6);
        this.pianoPanelController = pianoPanelController;
        this.pianoPanelController.setGui(this);
    }


    @Override
    protected void onOpen(Player player)
    {
        setTitle(new MessageBuilder()
                .space(10)
                .color(ChatColor.DARK_GREEN)
                .text(Emoticons.music)
                .bold(" MIDI player ")
                .text(Emoticons.music)
                .toString());
    }

    @Override
    public void onInitialize() {
        setBorderMaterial(Material.LIME_STAINED_GLASS_PANE);

        var materialPicker = new MaterialPickerUI("Select material");
        materialPicker.addBlockFilter();
        materialPicker.applyFilters();
        materialPicker.setParent(this);

        var filePicker = new FilePickerUI("/plugins/JW_PianoPlayer", 6);
        filePicker.setExtensions("mid","midi");
        filePicker.applyFilters();
        filePicker.setPath(FluentPlugin.getPath());
        filePicker.setParent(this);

        var acceptUI = new AcceptUI("Are you sure about that?",3);
        acceptUI.setParent(this);

        var createPianoBtn = ButtonObserverUI.builder()
                .setTitle(titleFormatter("Create piano keyboard"))
                .setLocation(0, 4)
                .addObserver(pianoPanelController.createPianoObserver(acceptUI))
                .buildAndAdd(this);

        var whiteKeyPressed = ButtonObserverUI.builder()
                .setTitle(titleFormatter("White key pressing material"))
                .setLocation(4, 1)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.keyWhitePressObserver(materialPicker))
                .buildAndAdd(this);

        var whiteKeyReleased = ButtonObserverUI.builder()
                .setTitle(titleFormatter("White key releasing material"))
                .setLocation(4, 3)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.keyWhiteReleaseObserver(materialPicker))
                .buildAndAdd(this);

        var blackKeyPressed = ButtonObserverUI.builder()
                .setTitle(titleFormatter("Black key pressing material"))
                .setLocation(4, 5)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.keyDarkPressObserver(materialPicker))
                .buildAndAdd(this);

        var blackKeyReleased = ButtonObserverUI.builder()
                .setTitle(titleFormatter("Black key releasing material"))
                .setLocation(4, 7)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.keyDarkReleaseBindObserver(materialPicker))
                .buildAndAdd(this);

        var playButton = ButtonObserverUI.builder()
                .setTitle(titleFormatter("Is piano playing"))
                .setLocation(2, 2)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.isPianoPlayingObserver())
                .buildAndAdd(this);

        var teleportButton = ButtonObserverUI.builder()
                .setTitle(titleFormatter("Teleport to piano"))
                .setLocation(3, 4)
                .setMaterial(Material.ENDER_PEARL)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.teleportButtonObserver())
                .buildAndAdd(this);

        var selectMidiFileButton = ButtonObserverUI.builder()
                .setTitle(titleFormatter("MIDI file"))
                .setLocation(2, 4)
                .setMaterial(Material.NOTE_BLOCK)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.selectMidiFileButtonObserver(filePicker))
                .buildAndAdd(this);

        var volumeButton = ButtonObserverUI.builder()
                .setTitle(titleFormatter("Volume"))
                .setLocation(2, 6)
                .setMaterial(Material.MUSIC_DISC_STRAD)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.volumeButtonObserver())
                .buildAndAdd(this);

        var lightButton = ButtonObserverUI.builder()
                .setTitle(titleFormatter("Lighting"))
                .setLocation(3, 2)
                .setMaterial(Material.LIGHT)
                .addObserver(pianoPanelController.isPianoCreatedObserver())
                .addObserver(pianoPanelController.lightButtonObserver())
                .buildAndAdd(this);
    }

    private String titleFormatter(String value)
    {
          return new MessageBuilder().color(ChatColor.GREEN).inBrackets(value).toString();
    }
}
