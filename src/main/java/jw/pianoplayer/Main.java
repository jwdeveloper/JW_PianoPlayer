package jw.pianoplayer;


import jw.pianoplayer.data.Settings;
import jw.pianoplayer.services.SettingsService;
import jw.spigot_fluent_api.dependency_injection.InjectionManager;
import jw.spigot_fluent_api.initialization.FluentPlugin;
import jw.spigot_fluent_api.initialization.FluentPluginConfiguration;
import jw.spigot_fluent_api.utilites.files.FileUtility;
import jw.spigot_fluent_api.utilites.files.json.JsonUtitlity;


public final class Main extends FluentPlugin {

    @Override
    protected void OnConfiguration(FluentPluginConfiguration configuration) {
        configuration.useDependencyInjection();
        configuration.runInDebug();
    }

    @Override
    protected void OnFluentPluginEnable() {
        var settingsService = InjectionManager.getObject(SettingsService.class);
        FileUtility.ensureDirectory(settingsService.midiFilesPath());
    }

    @Override
    protected void OnFluentPluginDisable() {

    }

}
