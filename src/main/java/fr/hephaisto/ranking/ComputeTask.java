package fr.hephaisto.ranking;

import com.massivecraft.factions.entity.Faction;
import org.bukkit.scheduler.BukkitRunnable;
import java.time.LocalDateTime;

public class ComputeTask extends BukkitRunnable {
    private final Faction faction;
    private final LocalDateTime lastUpdate;

    public ComputeTask(Faction faction, LocalDateTime lastUpdate) {
        this.faction = faction;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public void run() {
        //TODO
        cancel();
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
}
