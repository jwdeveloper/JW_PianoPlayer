package jw.pianoplayer.events;

import jw.pianoplayer.services.PianoPlayerService;
import jw.pianoplayer.services.SettingsService;
import jw.spigot_fluent_api.dependency_injection.SpigotBean;
import jw.spigot_fluent_api.fluent_events.EventBase;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.HashMap;
import java.util.function.Consumer;

@SpigotBean(lazyLoad = false)
public class PianoEventListener extends EventBase
{
    private final HashMap<Player, Consumer<Block>> playerConsumerHashMap;
    private final PianoPlayerService pianoPlayerService;
    private final SettingsService settingsService;

    public PianoEventListener(PianoPlayerService pianoPlayerService, SettingsService settingsService)
    {
        playerConsumerHashMap = new HashMap<>();
        this.pianoPlayerService = pianoPlayerService;
        this.settingsService =settingsService;
    }

    @Override
    public void onPluginStart(PluginEnableEvent event) {
        pianoPlayerService.createPiano(settingsService.getLocationBind().get());
    }

    @Override
    public void onPluginStop(PluginDisableEvent event)
    {
        pianoPlayerService.destroyPiano();
    }

    @EventHandler
    public void onPlayerClick(BlockBreakEvent event) {
        if (playerConsumerHashMap.containsKey(event.getPlayer())) {
            var action = playerConsumerHashMap.get(event.getPlayer());
            playerConsumerHashMap.remove(event.getPlayer());
            action.accept(event.getBlock());
            event.setCancelled(true);
        }
    }
    public void addPlayerBlockListener(Player player, Consumer<Block> consumer) {
        if (!playerConsumerHashMap.containsKey(player))
            playerConsumerHashMap.put(player, consumer);
    }
}
