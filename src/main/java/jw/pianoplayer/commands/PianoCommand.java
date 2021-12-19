package jw.pianoplayer.commands;


import jw.pianoplayer.gui.PianoPanelUI;
import jw.spigot_fluent_api.commands.FluentCommand;
import jw.spigot_fluent_api.dependency_injection.InjectionManager;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import org.bukkit.entity.Player;

@SpigotBean(lazyLoad = false)
public class PianoCommand extends FluentCommand
{
    public PianoCommand()
    {
        super("piano");
    }

    @Override
    protected void onInitialize() {

    }

    @Override
    protected void onPlayerInvoke(Player playerSender, String[] args) {
        var gui = InjectionManager.getObjectPlayer(PianoPanelUI.class, playerSender.getUniqueId());
        gui.open(playerSender);
    }

}
