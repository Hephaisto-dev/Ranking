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
        double l = faction.getUPlayers().stream()
                .map(this::retrieveOnlineTime)
                .map(this::toHours)
                .filter(hours -> hours >= minHours)
                .count() * 1.0 / faction.getUPlayers().size();
        OptionalDouble optionalDouble = plugin.getConfig()
                .getConfigurationSection("criteria.activity.points-per-percent").getKeys(false)
                .stream()
                .mapToDouble(Double::parseDouble)
                .filter(percent -> l >= percent)
                .max();
        if (optionalDouble.isPresent()) {
            return plugin.getConfig().getInt("criteria.activity.points-per-percent." +
                    optionalDouble.getAsDouble());
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
