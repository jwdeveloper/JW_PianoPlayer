package jw.pianoplayer.events;

import jw.pianoplayer.Main;
import jw.pianoplayer.piano.PianoPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.HashMap;
import java.util.function.Consumer;

public class PianoEventListener implements Listener {


    private final HashMap<Player, Consumer<Block>> playerConsumerHashMap = new HashMap<>();

    @EventHandler
    public void OnPlayerClick(BlockBreakEvent blockBreakEvent) {
        if (playerConsumerHashMap.containsKey(blockBreakEvent.getPlayer())) {
            Consumer<Block> event = playerConsumerHashMap.get(blockBreakEvent.getPlayer());
            playerConsumerHashMap.remove(blockBreakEvent.getPlayer());
            blockBreakEvent.setCancelled(true);
            event.accept(blockBreakEvent.getBlock());
        }
    }
    @EventHandler
    public void OnPluginDisable(PluginDisableEvent pluginDisableEvent)
    {
         if(pluginDisableEvent.getPlugin() == Main.getPlugin(Main.class))
         {
             PianoPlayer player =   Main.getPlugin(Main.class).pianoPlayer;
             player.Stop();
             player.Destroy();
         }
    }
    public void AddPlayerBlockListener(Player player, Consumer<Block> consumer) {
        if (!playerConsumerHashMap.containsKey(player))
            playerConsumerHashMap.put(player, consumer);
    }
}
