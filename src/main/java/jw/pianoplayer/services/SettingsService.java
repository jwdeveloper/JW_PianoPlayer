package jw.pianoplayer.services;

import jw.pianoplayer.data.Settings;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.utilites.binding.Observable;
import jw.spigot_fluent_api.utilites.files.FileUtility;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

@SpigotBean(lazyLoad = false)
@Getter
@Setter
public class SettingsService {
    private Observable<Integer> volumeBind;
    private Observable<Boolean> isPianoPlacedBind;
    private Observable<Boolean> isPlayingBind;
    private Observable<Boolean> isLightEnableBind;
    private Observable<String> lastPlayedMidiBind;
    private Observable<Material> keyWhitePressBind;
    private Observable<Material> keyWhiteReleaseBind;
    
    private Observable<Material> keyDarkPressBind;

    private Observable<Material> keyDarkReleaseBind;
    private Observable<Location> locationBind;


    private final Settings settings;

    public SettingsService(Settings settings) {
        this.settings = settings;
        volumeBind = new Observable<>(settings, "volume");
        isPianoPlacedBind = new Observable<>(settings, "isPianoPlaced");
        isPlayingBind = new Observable<>(settings, "isPlaying");
        isLightEnableBind = new Observable<>(settings, "isLightEnable");
        lastPlayedMidiBind = new Observable<>(settings, "lastPlayedMidi");
        keyWhitePressBind = new Observable<>(settings, "keyWhitePress");
        keyWhiteReleaseBind = new Observable<>(settings, "keyWhiteRelease");
        keyDarkPressBind = new Observable<>(settings, "keyDarkPress");
        keyDarkReleaseBind = new Observable<>(settings, "keyDarkRelease");
        locationBind = new Observable<>(settings, "location");

    }

    public String midiFilesPath() {
        return FileUtility.pluginsPath();
    }
}
