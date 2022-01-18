package jw.pianoplayer.commands;


import jw.pianoplayer.gui.PianoPanelUI;
import jw.pianoplayer.services.SettingsService;
import jw.spigot_fluent_api.dependency_injection.InjectionManager;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.desing_patterns.dependecy_injection.annotations.Inject;
import jw.spigot_fluent_api.desing_patterns.dependecy_injection.annotations.Injection;
import jw.spigot_fluent_api.fluent_commands.SimpleCommand;
import jw.spigot_fluent_api.fluent_commands.builders.FluentCommand;

@Injection(lazyLoad = false)
public class CommandProvider {
    private final SimpleCommand rootCommand;
    @Inject
    private  SettingsService settingsService;

    public CommandProvider() {
        rootCommand = getRootCommand();
        rootCommand.register();
    }

    private SimpleCommand getRootCommand() {
        return FluentCommand
                .create("piano")
                .addPermissions("jw.piano")
                .setUsageMessage("Can be use both by player and console")
                .setDescription("Use to open GUI panel")
                .nextStep()
                .nextStep()
                .onPlayerExecute(event ->
                {
                    var player = event.getPlayerSender();
                    var gui = InjectionManager.getObjectPlayer(PianoPanelUI.class, player.getUniqueId());
                    gui.open(player);
                }).nextStep().build();
    }
}
