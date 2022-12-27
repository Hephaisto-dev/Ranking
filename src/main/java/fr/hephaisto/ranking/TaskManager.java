package fr.hephaisto.ranking;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final List<ComputeTask> factionsComputeTasks = new ArrayList<>();

    public TaskManager(Map<String, LocalDateTime> lastUpdatesByFactions) {
        lastUpdatesByFactions.forEach((factionName, lastUpdate) -> {
            for (FactionColl coll : FactionColls.get().getColls()) {
                Faction faction = coll.getByName(factionName);
                if (faction != null) {
                    factionsComputeTasks.add(new ComputeTask(faction, lastUpdate));
                }
            }
        });
    }

    public void scheduleTasks(Ranking ranking) {
        factionsComputeTasks.forEach(task -> task.runTaskLaterAsynchronously(ranking,
                ChronoUnit.SECONDS.between(LocalDateTime.now(),
                        task.getLastUpdate().plusDays(ranking.getConfig().getLong("frequency.days"))
                                .withHour(ranking.getConfig().getInt("frequency.hour"))
                                .withMinute(ranking.getConfig().getInt("frequency.minute")))
                        * 20));
    }
}
