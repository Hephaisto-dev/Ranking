package fr.hephaisto.ranking.calculation.calculators;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import fr.hephaisto.ranking.Ranking;
import fr.hephaisto.ranking.calculation.AbstractCalculator;

import java.util.OptionalDouble;

public class ActivityCalculator extends AbstractCalculator {
    public ActivityCalculator(Ranking plugin) {
        super(plugin);
    }

    @Override
    public double compute(Faction faction) {
        double minHours = plugin.getConfig().getDouble("criteria.activity.min-hours");
        double percentMinPlayed = faction.getUPlayers().stream()
                .map(this::retrieveOnlineTime)
                .map(this::toHours)
                .filter(hours -> hours >= minHours)
                .count() * 1.0 / faction.getUPlayers().size();
        OptionalDouble optionalConfigKey = plugin.getConfig()
                .getConfigurationSection("criteria.activity.points-per-percent")
                .getKeys(false)
                .stream()
                .mapToDouble(Double::parseDouble)
                .filter(percent -> percentMinPlayed >= percent)
                .max();
        if (optionalConfigKey.isPresent()) {
            return plugin.getConfig().getInt("criteria.activity.points-per-percent." +
                    optionalConfigKey.getAsDouble());
        }
        return 0d;
    }

    private long retrieveOnlineTime(UPlayer uPlayer) {
        //TDOO retrieve online time
        return 0;
    }

    private long toHours(long millis) {
        return millis / 3600000;
    }
}
