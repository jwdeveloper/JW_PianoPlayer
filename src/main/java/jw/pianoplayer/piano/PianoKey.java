package jw.pianoplayer.piano;

import jw.pianoplayer.events.PianoEvent;
import jw.pianoplayer.services.SettingsService;
import jw.spigot_fluent_api.fluent_plugin.FluentPlugin;
import jw.spigot_fluent_api.fluent_tasks.FluentTasks;
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

public class PianoKey implements PianoEvent {

    private final SettingsService settingsService;
    private final List<Block> blocks;
    private final Plugin plugin;
    private final Location location;
    private final Light lightBlockData;
    private final Block lightBlock;
    private final boolean isBlack;

    public PianoKey(SettingsService settingsService, Location location, boolean isBlack) {
        this.settingsService = settingsService;
        this.location = location;
        this.isBlack = isBlack;
        this.blocks = new ArrayList<>();
        this.plugin = FluentPlugin.getPlugin();
        lightBlock = location.clone().add(0, 1, 0).getBlock();
        lightBlock.setType(Material.LIGHT);
        lightBlockData = (Light) lightBlock.getBlockData();
        setLightLevel(0);
        loadKey();
    }

    @Override
    public void onKeyPress(boolean isDown, int number, int velocity, int channel) {
        if (isDown)
            onPressDown(number, velocity, channel);
        else
            onPressUp(number, velocity, channel);
    }

    private void onPressDown(int number, int velocity, int channel) {
        Bukkit.getScheduler().runTask(plugin, () ->
        {
            if (settingsService.getIsLightEnableBind().get()) {
                setLightLevel(15);
            }
            for (Block b : blocks) {
                b.setType(isBlack ? settingsService.getKeyDarkPressBind().get() :  settingsService.getKeyWhitePressBind().get());
            }
        });
    }

    private void onPressUp(int number, int velocity, int channel) {
        FluentTasks.task(unused ->
        {
            setLightLevel(0);
            for (Block b : blocks)
            {
                b.setType(isBlack ? settingsService.getKeyDarkReleaseBind().get() : settingsService.getKeyWhiteReleaseBind().get());
            }
        });
    }

    public void removeKey() {
            if (isBlack) {
                location.clone().add(0, -1, 0).getBlock().setType(Material.AIR);
            }
            for (Block b : blocks) {
                b.setType(Material.AIR);
            }
            blocks.clear();
    }


    public void reset() {
        onPressUp(0, 0, 0);
        setLightLevel(0);

        if (isBlack)
        {
            var material = settingsService.getKeyWhiteReleaseBind().get();
            location.clone().add(0, -1, 0).getBlock().setType(material);
        }

    }
    private void loadKey() {
        World world = location.getWorld();
        if (isBlack)
            location.add(new Vector(0, 1, 0));

        Location loc = location.clone();
        for (int i = 0; i < 4; i++) {
            if (isBlack) {
                if (i == 0) {
                    world.getBlockAt(loc.add(0, -1, 0))
                            .setType(settingsService.getKeyWhiteReleaseBind().get());
                    loc.add(0, 1, 0);
                } else {
                    world.getBlockAt(loc)
                            .setType(settingsService.getKeyDarkReleaseBind().get());
                    blocks.add(world.getBlockAt(loc));
                }
            } else {
                world.getBlockAt(loc)
                        .setType(settingsService.getKeyWhiteReleaseBind().get());
                blocks.add(world.getBlockAt(loc));
            }

            loc.add(new Vector(1, 0, 0));
        }
    }

    public void setLightLevel(int level) {
        lightBlockData.setLevel(level);
        lightBlock.setBlockData(lightBlockData);
    }


}
