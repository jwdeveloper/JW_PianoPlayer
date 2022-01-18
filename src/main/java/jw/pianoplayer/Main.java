package jw.pianoplayer;

import jw.pianoplayer.services.SettingsService;
import jw.spigot_fluent_api.dependency_injection.InjectionManager;
import jw.spigot_fluent_api.desing_patterns.dependecy_injection.FluentInjection;
import jw.spigot_fluent_api.fluent_plugin.FluentPlugin;
import jw.spigot_fluent_api.fluent_plugin.configuration.PluginConfiguration;
import jw.spigot_fluent_api.fluent_plugin.configuration.config.ConfigFile;
import jw.spigot_fluent_api.utilites.files.FileUtility;


public final class Main extends FluentPlugin {


    @Override
    protected void OnConfiguration(PluginConfiguration configuration, ConfigFile configFile) {
        configuration.useDataContext()
                .useDebugMode()
                .useMetrics(13849);
    }

    @Override
    protected void OnFluentPluginEnable() {
        var settingsService = FluentInjection.getInjection(SettingsService.class);
        FileUtility.ensureDirectory(settingsService.midiFilesPath());
    }

    @Override
    protected void OnFluentPluginDisable() {

    }
}
