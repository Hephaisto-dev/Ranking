package fr.hephaisto.ranking.listeners;

import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.UPlayerColl;
import com.massivecraft.factions.entity.UPlayerColls;
import fr.hephaisto.ranking.Ranking;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        savePlayerTime(player);
    }

    public void savePlayerTime(Player player) {
        if (!connectionTime.containsKey(player))
            return;
        long time = System.currentTimeMillis() - connectionTime.get(player);
        connectionTime.remove(player);
        for (UPlayerColl coll : UPlayerColls.get().getColls()) {
            Optional<UPlayer> first = coll.getAll(
                            uPlayer -> uPlayer.getUuid() != null && uPlayer.getUuid().equals(player.getUniqueId()))
                    .stream().findFirst();
            if (first.isPresent()) {
                plugin.getDb().updatePlayTime(first.get(), time);
                return;
            }
        }
    }

    public Map<Player, Long> getConnectionTime() {
        return connectionTime;
    }
}
