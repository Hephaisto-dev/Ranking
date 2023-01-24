package fr.hephaisto.ranking.tasks;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;
import org.bukkit.scheduler.BukkitRunnable;
import java.time.LocalDateTime;

public class ComputeTask extends BukkitRunnable {
    private final Faction faction;
    private final LocalDateTime lastUpdate;
    private final Ranking plugin;

    public ComputeTask(Faction faction, LocalDateTime lastUpdate, Ranking plugin) {
        this.faction = faction;
        this.lastUpdate = lastUpdate;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getCalculatorManager().compute(faction);
        plugin.getLogger().info("Computed " + faction.getName());
        plugin.getTaskManager().scheduleTask(new ComputeTask(faction, LocalDateTime.now(), plugin), false);
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public Faction getFaction() {
        return faction;
    }
}
