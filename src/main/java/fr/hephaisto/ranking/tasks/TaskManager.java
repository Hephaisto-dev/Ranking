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

    public void scheduleTask(ComputeTask task, boolean same) {
        LocalDateTime now = LocalDateTime.now();
        if (task.getLastUpdate().isBefore(now)) {
            LocalDateTime localDateTime = same ? now.with(
                    TemporalAdjusters.nextOrSame(DayOfWeek.of(plugin.getConfig().getInt("frequency.days")))) : now.with(
                    TemporalAdjusters.next(DayOfWeek.of(plugin.getConfig().getInt("frequency.days"))));
            LocalDateTime nextDateTimeOfCompute = localDateTime.withHour(plugin.getConfig().getInt("frequency.hours"))
                    .withMinute(plugin.getConfig().getInt("frequency.minutes")).withSecond(0).withNano(0);
            plugin.getLogger().info("Scheduling task for " + task.getFaction()
                    .getName() + " at " + nextDateTimeOfCompute + " (" + nextDateTimeOfCompute.getDayOfWeek() + ")");
            task.runTaskLaterAsynchronously(plugin, now.until(nextDateTimeOfCompute, ChronoUnit.SECONDS) * 20);
        }
    }

    public void scheduleTask(ComputeTask task) {
        scheduleTask(task, true);
    }
}
