package fr.hephaisto.ranking.listeners;

import fr.hephaisto.ranking.Ranking;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayHoursListener implements Listener {

    private final Map<Player, Long> connectionTime = new HashMap<>();
    private final Ranking plugin;

    public PlayHoursListener(Ranking plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        connectionTime.put(player, System.currentTimeMillis());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        long time = System.currentTimeMillis() - connectionTime.get(player);
        connectionTime.remove(player);
        plugin.getDb().updatePlayTime(event.getPlayer(), time);
    }

    public Map<Player, Long> getConnectionTime() {
        return connectionTime;
    }
}
