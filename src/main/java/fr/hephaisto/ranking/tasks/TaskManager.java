package fr.hephaisto.ranking.tasks;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import fr.hephaisto.ranking.Ranking;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final List<ComputeTask> factionsComputeTasks = new ArrayList<>();
    private final Ranking plugin;

    public TaskManager(Map<String, LocalDateTime> lastUpdatesByFactions, Ranking plugin) {
        lastUpdatesByFactions.forEach((factionName, lastUpdate) -> {
            for (FactionColl coll : FactionColls.get().getColls()) {
                Faction faction = coll.getByName(factionName);
                if (faction != null) {
                    factionsComputeTasks.add(new ComputeTask(faction, lastUpdate, plugin));
                }
            }
        });
        this.plugin = plugin;
    }

    public void scheduleTasks() {
        factionsComputeTasks.forEach(this::scheduleTask);
    }

    public void scheduleTask(ComputeTask task) {
        task.runTaskLaterAsynchronously(plugin,
                ChronoUnit.SECONDS.between(LocalDateTime.now(),
                        task.getLastUpdate().plusDays(plugin.getConfig().getLong("frequency.days"))
                                .withHour(plugin.getConfig().getInt("frequency.hour"))
                                .withMinute(plugin.getConfig().getInt("frequency.minute")))
                        * 20);
    }
}
