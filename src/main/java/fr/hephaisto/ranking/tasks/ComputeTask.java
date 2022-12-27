package fr.hephaisto.ranking.tasks;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;
import org.bukkit.scheduler.BukkitRunnable;
import java.time.LocalDateTime;

public class ComputeTask extends BukkitRunnable {
    private final Faction faction;
    private final LocalDateTime lastUpdate;
    private final Ranking ranking;

    public ComputeTask(Faction faction, LocalDateTime lastUpdate, Ranking ranking) {
        this.faction = faction;
        this.lastUpdate = lastUpdate;
        this.ranking = ranking;
    }

    @Override
    public void run() {
        ranking.getCalculatorManager().compute(faction);
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
}
