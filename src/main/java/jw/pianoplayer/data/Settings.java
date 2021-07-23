package jw.pianoplayer.data;

import jw.utilites.binding.BindingField;
import jw.utilites.files.FileHelper;
import org.bukkit.Location;
import org.bukkit.Material;

public class Settings
{
    public Boolean isPlaying = false;
    public Boolean isLightEnable = true;
    public Boolean isPianoPlaced = false;
    public String lastPlayedMidi = "";
    public Integer volume = 100;
    public Material keyWhitePress = Material.WHITE_WOOL;
    public Material keyWhiteRelese = Material.GREEN_WOOL;
    public Material keyDarkPress = Material.COAL_BLOCK;
    public Material keyDarkRelese = Material.BLACK_WOOL;

    public Location location;
    public BindingField<Integer> volumeBind = new BindingField<>("volume",this);
    public BindingField<Boolean> isPianoPlacedBind = new BindingField<>("isPianoPlaced",this);
    public BindingField<Boolean> isPlayingBind = new BindingField<>("isPlaying",this);
    public BindingField<Boolean> isLightEnableBind = new BindingField<>("isLightEnable",this);

    public BindingField<String> lastPlayedMidiBind = new BindingField<>("lastPlayedMidi",this);
    public BindingField<Material> keyWhitePressBind = new BindingField<>("keyWhitePress",this);
    public BindingField<Material> keyWhiteReleseBind = new BindingField<>("keyWhiteRelese",this);
    public BindingField<Material> keyDarkPressPressBind = new BindingField<>("keyDarkPress",this);
    public BindingField<Material> keyDarkReleseReleseBind = new BindingField<>("keyDarkRelese",this);
    public BindingField<Location> locationBind = new BindingField<>("location",this);


    public String midiFilePath()
    {
        return FileHelper.pluginPath();
    }
}
