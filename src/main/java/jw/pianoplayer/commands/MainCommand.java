package jw.pianoplayer.commands;

import jw.commands.BetterCommandGUI;
import jw.gui.core.InventoryGUI;
import jw.pianoplayer.Main;
import jw.pianoplayer.data.Settings;
import jw.pianoplayer.gui.MenuGUI;
import jw.pianoplayer.piano.PianoPlayer;
import org.bukkit.entity.Player;

public class MainCommand extends BetterCommandGUI
{

    Settings settings;
    PianoPlayer pianoPlayer;

    public MainCommand()
    {
        super("piano");

        settings = Main.getPlugin(Main.class).dataManager.settings;
        pianoPlayer = Main.getPlugin(Main.class).pianoPlayer;
    }

    @Override
    public InventoryGUI SetInventoryGUI() {
        return new MenuGUI(pianoPlayer);
    }

    @Override
    public void Invoke(Player playerSender, String[] args) {

        MenuGUI menuGUI = (MenuGUI) this.GetGUI(playerSender);
        menuGUI.Open(playerSender,settings);
    }

    @Override
    public void OnInitialize() {

    }
}
