package jw.pianoplayer;


import jw.spigot_fluent_api.initialization.FluentPlugin;
import jw.spigot_fluent_api.initialization.FluentPluginConfiguration;


public final class Main extends FluentPlugin {

    @Override
    protected void OnConfiguration(FluentPluginConfiguration configuration) {
        configuration.useDependencyInjection();
        configuration.runInDebug();
    }

    @Override
    protected void OnFluentPluginEnable() {

    }

    @Override
    protected void OnFluentPluginDisable() {

    }

}
