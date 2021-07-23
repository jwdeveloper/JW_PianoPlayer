package jw.pianoplayer.piano;

import jw.pianoplayer.Main;
import jw.pianoplayer.data.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Light;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PianoKey implements PianoEvent
{
    private final Settings settings;
    private final List<Block> blocks;
    private final Plugin plugin;
    private Location location;
    private boolean isBlack = false;
    private Light lightBlockData;
    private Block lightBlock;
    public PianoKey(Settings settings,Location location,boolean isBlack)
    {
        this.plugin  = Main.getPlugin(Main.class);
        this.settings =settings;
        this.location = location;
        this.isBlack = isBlack;
        this.blocks = new ArrayList<>();
        lightBlock= location.clone().add(0,1,0).getBlock();
        lightBlock.setType(Material.LIGHT);
        lightBlockData = (Light)lightBlock.getBlockData();
        setLightLevel(0);
        LoadKey();
    }




    @Override
    public void OnKeyPress(boolean isDown, int number, int velocity, int channel)
    {
        if(isDown)
            OnPressDown(number,velocity,channel);
        else
            OnPressUp(number,velocity,channel);
    }

    private void OnPressDown(int number, int velocity, int channel)
    {
        Bukkit.getScheduler().runTask(plugin, () ->
        {
            if(settings.isLightEnable)
            {
                setLightLevel(15);
            }
            for(Block b:blocks)
            {
                b.setType(isBlack?settings.keyDarkPress:settings.keyWhitePress);
            }
        });
    }
    private void OnPressUp(int number, int velocity, int channel)
    {
        Bukkit.getScheduler().runTask(plugin, () ->
        {
            setLightLevel(0);
            for(Block b:blocks)
            {
                b.setType(isBlack?settings.keyDarkRelese:settings.keyWhiteRelese);
            }
        });
    }

    public void Reset()
    {
        OnPressUp(0,0,0);
        setLightLevel(0);
    }
    public void Reset(Material material)
    {
        OnPressUp(0,0,0);
        setLightLevel(0);
    }
    public void RemoveKey()
    {
        Bukkit.getScheduler().runTask(plugin, () ->
        {
            if(isBlack)
            {
                location.clone().add(1,-1,0).getBlock().setType(Material.AIR);
            }
            for(Block b:blocks)
            {
                b.setType(Material.AIR);
            }
            blocks.clear();
        });

    }

    private void LoadKey()
    {
        World world = location.getWorld();
        if(isBlack)
            location.add(new Vector(0,1,0));

        Location loc = location.clone();
        for(int i=0;i<4;i++)
        {
            if(isBlack)
            {
                if(i==0)
                {
                    world.getBlockAt(loc.add(0,-1,0)).setType(settings.keyWhiteRelese);
                    loc.add(0,1,0);
                }
                else
                {
                    world.getBlockAt(loc).setType(settings.keyDarkRelese);
                    blocks.add(world.getBlockAt(loc));
                }
            }
            else
            {
                world.getBlockAt(loc).setType(settings.keyWhiteRelese);
                blocks.add(world.getBlockAt(loc));
            }

            loc.add(new Vector(1,0,0));
        }
    }

    public void setLightLevel(int level)
    {
        lightBlockData.setLevel(level);
        lightBlock.setBlockData(lightBlockData);
    }

}
