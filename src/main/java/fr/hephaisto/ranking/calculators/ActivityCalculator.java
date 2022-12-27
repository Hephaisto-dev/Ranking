package fr.hephaisto.ranking.calculators;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import fr.hephaisto.ranking.Ranking;

import java.util.OptionalDouble;

public class ActivityCalculator implements Calculator {
    private final Ranking plugin;

    public ActivityCalculator(Ranking plugin) {
        this.plugin = plugin;
    }

    public int compute(Faction faction) {
        double minHours = plugin.getConfig().getDouble("criteria.activity.min-hours");
        double percentMinPlayed = faction.getUPlayers().stream()
                .map(this::retrieveOnlineTime)
                .map(this::toHours)
                .filter(hours -> hours >= minHours)
                .count() * 1.0 / faction.getUPlayers().size();
        OptionalDouble optionalConfigPercent = plugin.getConfig()
                .getConfigurationSection("criteria.activity.points-per-percent").getKeys(false)
                .stream()
                .mapToDouble(Double::parseDouble)
                .filter(percent -> percentMinPlayed >= percent)
                .max();
        if (optionalConfigPercent.isPresent()) {
            return plugin.getConfig().getInt("criteria.activity.points-per-percent." +
                    optionalConfigPercent.getAsDouble());
        }
        return 0;
    }

    private long retrieveOnlineTime(UPlayer uPlayer) {
        //TDOO retrieve online time
        return 0;
    }

    private long toHours(long millis) {
        return millis / 3600000;
    }
}
