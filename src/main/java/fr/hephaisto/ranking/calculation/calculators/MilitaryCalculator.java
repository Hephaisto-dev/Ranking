package fr.hephaisto.ranking.calculation.calculators;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;
import fr.hephaisto.ranking.calculation.AbstractCalculator;
import world.nations.Core;
import world.nations.stats.data.FactionData;

import java.util.OptionalDouble;

public class MilitaryCalculator extends AbstractCalculator {
    public MilitaryCalculator(Ranking plugin) {
        super(plugin);
    }

    @Override
    public double compute(Faction faction) {
        double points = 0;
        double pointsPerAlly = plugin.getConfig().getDouble("criteria.military.points-per-ally");
        double maxPointsAllies = plugin.getConfig().getInt("criteria.military.max-points-allies");
        double pointsPerEnemy = plugin.getConfig().getDouble("criteria.military.points-per-enemy");
        double maxPointsEnemies = plugin.getConfig().getInt("criteria.military.max-points-enemies");
        double ratioPointsScoreZone = plugin.getConfig().getDouble("criteria.military.ratio-points-scorezone");
        FactionData factionData = Core.getPlugin().getStatsManager().getFaction(faction.getName());
        points += Math.min(getAllyCount(faction) * pointsPerAlly, maxPointsAllies);
        points += Math.min(getEnemyCount(faction) * pointsPerEnemy, maxPointsEnemies);
        points += getKDPoints(factionData);
        points += getAssaultPoints(factionData);
        points += factionData.getScorezone() * ratioPointsScoreZone;
        return points;
    }

    private double getKDPoints(FactionData factionData) {
        double ratioKD = getRatioKD(factionData);
        OptionalDouble optionalConfigKey = plugin.getConfig()
                .getConfigurationSection("criteria.military.points-per-kill-death-ratio")
                .getKeys(false)
                .stream()
                .mapToDouble(Double::parseDouble)
                .filter(ratio -> ratioKD >= ratio)
                .max();
        if (optionalConfigKey.isPresent()) {
            return plugin.getConfig().getInt("criteria.military.points-per-kill-death-ratio." +
                    optionalConfigKey.getAsDouble());
        }
        return 0d;
    }

    private double getRatioKD(FactionData factionData) {
        return (double) factionData.getKills() / (double) factionData.getDeaths();
    }

    private double getAssaultPoints(FactionData factionData) {
        double ratioAssault = getRatioAssault(factionData);
        OptionalDouble optionalConfigKey = plugin.getConfig()
                .getConfigurationSection("criteria.military.points-per-assault-ratio")
                .getKeys(false)
                .stream()
                .mapToDouble(Double::parseDouble)
                .filter(ratio -> ratioAssault >= ratio)
                .max();
        if (optionalConfigKey.isPresent()) {
            return plugin.getConfig().getInt("criteria.military.points-per-assault-ratio." +
                    optionalConfigKey.getAsDouble());
        }
        return 0d;
    }

    private double getRatioAssault(FactionData factionData) {
        return factionData.getRatio();
    }

    private double getAllyCount(Faction faction) {
        return faction.getRelationWishes().values().stream().filter(r -> r == Rel.ENEMY).count();
    }

    private double getEnemyCount(Faction faction) {
        return faction.getRelationWishes().values().stream().filter(r -> r == Rel.ALLY).count();
    }
}
