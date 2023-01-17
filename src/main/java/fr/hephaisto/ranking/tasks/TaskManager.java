package fr.hephaisto.ranking.tasks;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final List<ComputeTask> factionsComputeTasks = new ArrayList<>();
    private final Ranking plugin;

    public TaskManager(Map<Faction, LocalDateTime> lastUpdatesByFactions, Ranking plugin) {
        lastUpdatesByFactions.forEach(
                (faction, lastUpdate) -> factionsComputeTasks.add(new ComputeTask(faction, lastUpdate, plugin)));
        this.plugin = plugin;
    }

    public void scheduleTasks() {
        factionsComputeTasks.forEach(this::scheduleTask);
    }

    public void scheduleTask(ComputeTask task) {
        LocalDateTime now = LocalDateTime.now();
        if (task.getLastUpdate().isBefore(now)) {
            LocalDateTime localDateTime = now.withHour(plugin.getConfig().getInt("frequency.hour"))
                    .withMinute(plugin.getConfig().getInt("frequency.minute"));
            LocalDateTime nextDateTimeOfCompute = localDateTime.with(
                    TemporalAdjusters.next(DayOfWeek.of(plugin.getConfig().getInt("frequency.days"))));
            task.runTaskLaterAsynchronously(plugin, now.until(nextDateTimeOfCompute, ChronoUnit.SECONDS) * 20);
        }
    }
}
