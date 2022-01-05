package jw.pianoplayer.commands;


import jw.pianoplayer.gui.PianoPanelUI;
import jw.pianoplayer.services.SettingsService;
import jw.spigot_fluent_api.dependency_injection.InjectionManager;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.fluent_plugin.FluentPlugin;
import jw.spigot_fluent_api.simple_commands.SimpleCommand;
import jw.spigot_fluent_api.simple_commands.enums.AccessType;
import jw.spigot_fluent_api.simple_commands.enums.ArgumentType;
import org.bukkit.entity.Player;

@SpigotBean(lazyLoad = false)
public class CommandProvider {
    private final SimpleCommand rootCommand;
    private final SettingsService settingsService;

    public CommandProvider(SettingsService settingsService) {
        this.settingsService = settingsService;
        rootCommand = getRootCommand();
        rootCommand.register();
    }

    private SimpleCommand getRootCommand() {
        return SimpleCommand
                .newCommand("piano")
                .addPermissions("jw.piano")
                .setUsageMessage("Can be use both by player and console")
                .setDescription("Use to open GUI panel")
                .onPlayerExecute(event ->
                {
                    var player = event.getPlayerSender();
                    var gui = InjectionManager.getObjectPlayer(PianoPanelUI.class, player.getUniqueId());
                    gui.open(player);
                }).build();
    }
}
