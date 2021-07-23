package jw.pianoplayer.gui;

import jw.gui.button.Button;
import jw.gui.examples.ChestGUI;
import jw.gui.examples.chestgui.bindingstrategies.BindingStrategyBuilder;
import jw.gui.examples.chestgui.bindingstrategies.examples.BlockStrategy;
import jw.gui.examples.chestgui.bindingstrategies.examples.BoolenBindStrategy;
import jw.gui.examples.chestgui.bindingstrategies.examples.FileStrategy;
import jw.pianoplayer.Main;
import jw.pianoplayer.data.Settings;
import jw.pianoplayer.piano.Audio;
import jw.pianoplayer.piano.PianoPlayer;
import jw.utilites.Emoticons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;

public class MenuGUI extends ChestGUI<Settings> {

    private final PianoPlayer pianoPlayer;

    public MenuGUI(PianoPlayer pianoPlayer) {
        super("Menu", 6);
        this.pianoPlayer = pianoPlayer;
    }

    @Override
    public void OnOpen(Player player) {
        this.SetTitle(ChatColor.DARK_GREEN+""+ChatColor.BOLD + Emoticons.star + " MIDI player " + Emoticons.star);
    }

    @Override
    public void OnInitialize() {

        this.DrawBorder(Material.LIME_STAINED_GLASS_PANE);
        BuildButton().SetName(ChatColor.BOLD + "Enable light")
                .SetPosition(3, 2)
                .SetMaterial(Material.LIGHT)
                .BindField(pianoExistsStrategy())
                .BindField(this.detail.isLightEnableBind)
                .BuildAndAdd();

        BuildButton().SetName(ChatColor.BOLD + "Teleport to piano")
                .SetPosition(3, 4)
                .SetMaterial(Material.ENDER_PEARL)
                .BindField(pianoExistsStrategy())
                .BindField(new BindingStrategyBuilder<Location>()
                        .setBindingFile(this.detail.locationBind)
                        .setOnValueChange((inventoryGUI, button, location) ->
                        {
                            if(location != null)
                            {
                                button.SetDescription(ChatColor.WHITE+"Location:",
                                        "X: "+location.getBlockX(),
                                        "Y: "+location.getBlockY(),
                                        "Z: "+location.getBlockZ()
                              );
                            }

                        })
                        .setOnClick((player1, button, bindingStrategy, currentValue) ->
                        {
                            if(this.detail.location != null)
                            {
                                player1.teleport(this.detail.location.clone().add(0,2,0));
                            }
                        })
                        .build())
                .BuildAndAdd();

        BuildButton()
                .SetName(ChatColor.BOLD + "Volume")
                .SetPosition(2, 6)
                .SetMaterial(Material.MUSIC_DISC_STRAD)
                .BindField(pianoExistsStrategy())
                .BindField(new BindingStrategyBuilder<>(this.detail.volumeBind)
                        .setOnValueChange( (inventoryGUI, button, newValue) ->
                        {
                             button.SetDescription("Value: "+newValue.toString());
                        })
                        .setOnClick((player1, button, bindingStrategy, currentValue) ->
                        {
                            int value = bindingStrategy.getValue();
                            value +=10;
                            if(value>100)
                              value = value%100;
                            bindingStrategy.setValue(value);
                            Audio.setApplicationVolume(value/100.0f);
                        })
                        .build())
                .BuildAndAdd();


        BuildButton().SetName(ChatColor.BOLD + "White key pressed")
                .SetPosition(4, 1)
                .BindField(pianoExistsStrategy())
                .BindField(this.detail.keyWhitePressBind)
                .BuildAndAdd();

        BuildButton().SetName(ChatColor.BOLD + "White key relesed")
                .SetPosition(4, 3)
                .BindField(pianoExistsStrategy())
                .BindField(this.detail.keyWhiteReleseBind)
                .BuildAndAdd();

        BuildButton().SetName(ChatColor.BOLD + "Black key pressed")
                .SetPosition(4, 5)
                .BindField(pianoExistsStrategy())
                .BindField(this.detail.keyDarkPressPressBind)
                .BuildAndAdd();

        BuildButton().SetName(ChatColor.BOLD + "Black key relesed")
                .SetPosition(4, 7)
                .BindField(pianoExistsStrategy())
                .BindField(this.detail.keyDarkReleseReleseBind)
                .BuildAndAdd();

        BuildButton().SetName(ChatColor.BOLD + "Select MIDI file")
                .SetMaterial(Material.NOTE_BLOCK)
                .SetPosition(2, 4)
                .BindField(pianoExistsStrategy())
                .BindField(new FileStrategy(this.detail.lastPlayedMidiBind, Material.MUSIC_DISC_FAR, this.detail.midiFilePath(), "mid", "midi"))
                .BuildAndAdd();

        BuildButton()
                .SetPosition(2, 2)
                .SetName(ChatColor.BOLD + "Is playing")
                .BindField(pianoExistsStrategy())
                .BindField(new BindingStrategyBuilder<Boolean>()
                        .setBindingFile(this.detail.isPlayingBind)
                        .setOnClick((player1, button, bindingStrategy, currentValue) ->
                        {
                            if (!detail.isPianoPlaced) {
                                this.SetTitle(ChatColor.RED + "     ! At first place piano !");
                                return;
                            }
                            if (detail.lastPlayedMidi.length() == 0) {
                                this.SetTitle(ChatColor.RED + "     ! Select Midi file !");
                                return;
                            }
                            if (currentValue)
                                pianoPlayer.Stop();
                            else
                                pianoPlayer.Play(detail.midiFilePath() + File.separator + detail.lastPlayedMidi);
                            bindingStrategy.setValue(!currentValue);
                        })
                        .setOnValueChange((inventoryGUI, button, newValue) ->
                        {
                            if (newValue) {
                                button.SetName("Stop");
                                button.setMaterial(Material.RED_WOOL);
                                inventoryGUI.DrawBorder(Material.RED_STAINED_GLASS_PANE);
                            } else {
                                button.SetName("Play");
                                button.setMaterial(Material.GREEN_WOOL);
                                inventoryGUI.DrawBorder(Material.LIME_STAINED_GLASS_PANE);
                            }
                            inventoryGUI.Refresh();
                        })
                        .build())
                .BuildAndAdd();


        BuildButton().SetName(ChatColor.BOLD + "Create keyboard")
                .SetPosition(0, 4)
                .BindField(new BindingStrategyBuilder<Location>()
                        .setBindingFile(this.detail.locationBind)
                        .setOnClick((player1, button, bindingStrategy, currentValue) ->
                        {
                            if (detail.isPianoPlaced)
                            {
                                pianoPlayer.Destroy();
                                this.detail.isPianoPlacedBind.set(false);
                                bindingStrategy.setValue(null);
                            } else
                            {
                                Close();
                                player.sendMessage("Destroy block to set location");
                                Main.getPlugin(Main.class).pianoEventListener.AddPlayerBlockListener(player, (block) ->
                                {
                                    bindingStrategy.setValue(block.getLocation());
                                    pianoPlayer.Create(block.getLocation());
                                    this.detail.isPianoPlacedBind.set(true);
                                    this.Open(player1);
                                });
                            }
                        })
                        .setOnValueChange((inventoryGUI, button, newValue) ->
                        {
                            if (detail.isPianoPlacedBind.get())
                            {
                                button.SetName(ChatColor.BOLD+"Destroy piano");
                                button.setMaterial(Material.BARRIER);
                            } else {
                                button.SetName(ChatColor.BOLD+"Create piano");
                                button.setMaterial(Material.CRAFTING_TABLE);
                            }
                        })
                        .build())
                .BuildAndAdd();
    }

    private BoolenBindStrategy pianoExistsStrategy()
    {
        BoolenBindStrategy boolenBindStrategy = new BoolenBindStrategy(detail.isPianoPlacedBind);
        boolenBindStrategy.onChangeEvent = (inventoryGUI, button1, newValue) ->
        {
            button1.setActive(newValue);
        };
        boolenBindStrategy.onClickEvent = (player1, button1, bindingStrategy, currentValue) ->
        {

        };
        return boolenBindStrategy;
    }
}
