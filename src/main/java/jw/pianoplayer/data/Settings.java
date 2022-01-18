package jw.pianoplayer.data;

import jw.spigot_fluent_api.data.Saveable;
import jw.spigot_fluent_api.data.annotation.files.JsonFile;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.desing_patterns.dependecy_injection.annotations.Injection;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

@Injection(lazyLoad = false)
@JsonFile
@Data
public class Settings
{
    public Boolean isInfoBar = false;
    public Boolean isPlaying = false;
    public Boolean isLightEnable = true;
    public Boolean isPianoPlaced = false;
    public String lastPlayedMidi = "";
    public Integer volume = 100;
    public Material keyWhitePress = Material.QUARTZ_SLAB;
    public Material keyWhiteRelease = Material.QUARTZ_BLOCK;
    public Material keyDarkPress =  Material.POLISHED_BLACKSTONE_BRICK_SLAB;
    public Material keyDarkRelease = Material.POLISHED_BLACKSTONE;
    public Location location;
}
