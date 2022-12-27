package fr.hephaisto.ranking.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayHoursListener implements Listener {

    private final Map<Player, Integer> connectionTime = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        connectionTime.put(player, (int) (System.currentTimeMillis() / 1000));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        int time = (int) (System.currentTimeMillis() / 1000) - connectionTime.get(player);
        connectionTime.remove(player);
        // TODO put in the db the played time
    }
}
