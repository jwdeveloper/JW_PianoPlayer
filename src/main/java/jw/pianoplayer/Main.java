package jw.pianoplayer;

import jw.pianoplayer.commands.PianoCommand2;
import jw.pianoplayer.data.Settings;
import jw.spigot_fluent_api.dependency_injection.InjectionManager;
import jw.spigot_fluent_api.dependency_injection.InjectionType;
import jw.spigot_fluent_api.initialization.FluentPlugin;
import jw.spigot_fluent_api.initialization.FluentPluginConfiguration;
import jw.spigot_fluent_api.utilites.ClassTypeUtility;
import org.bukkit.command.CommandSender;

public final class Main extends FluentPlugin {

    @Override
    protected void OnConfiguration(FluentPluginConfiguration configuration) {
     //   configuration.useDependencyInjection();
        configuration.runInDebug();
    }

    @Override
    protected void OnFluentPluginEnable() {
        FluentPlugin.logInfo("Siema2");
        InjectionManager.Instance();
        InjectionManager.register(InjectionType.TRANSIENT, Settings.class);
        InjectionManager.register(InjectionType.TRANSIENT, CommandSender.class);

        var types = ClassTypeUtility.getClassesInPackage(PianoCommand2.class.getPackage().getName());
        for(var t :types)
        {
            FluentPlugin.logInfo(t.getName()+" type");
        }


    }

    @Override
    protected void OnFluentPluginDisable() {

    }

}
