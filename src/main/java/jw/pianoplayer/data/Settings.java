package jw.pianoplayer.data;

import jw.spigot_fluent_api.data.Saveable;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

@SpigotBean(lazyLoad = false)
@Getter
@Setter
public class Settings implements Saveable
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


    @Override
    public boolean load() {
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }
}
