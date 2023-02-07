package fr.hephaisto.ranking.calculation.calculators;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;
import fr.hephaisto.ranking.calculation.AbstractCalculator;
import world.nations.Core;
import world.nations.stats.data.FactionData;

import java.util.OptionalInt;

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
        double ratioPointsScoreZone = plugin.getConfig().getDouble("criteria.military.ratio-points-score-zone");
        FactionData factionData = Core.getPlugin().getStatsManager().getFaction(faction.getName());
        points += Math.min(getAllyCount(faction) * pointsPerAlly, maxPointsAllies);
        points += Math.min(getEnemyCount(faction) * pointsPerEnemy, maxPointsEnemies);
        points += getKDPoints(factionData);
        points += getAssaultPoints(factionData);
        points += factionData.getScorezone() * ratioPointsScoreZone;
        return Math.max(0, points);
    }

    private int getKDPoints(FactionData factionData) {
        int ratioKD = getRatioKD(factionData);
        OptionalInt optionalConfigKey = plugin.getConfig()
                .getConfigurationSection("criteria.military.points-per-kill-death-ratio")
                .getKeys(false)
                .stream()
                .mapToInt(Integer::parseInt)
                .filter(ratio -> ratioKD >= ratio)
                .max();
        if (optionalConfigKey.isPresent()) {
            return plugin.getConfig().getInt("criteria.military.points-per-kill-death-ratio." +
                    optionalConfigKey.getAsInt());
        }
        return 0;
    }

    private int getRatioKD(FactionData factionData) {
        return (int) (100 * (double) factionData.getKills() / (double) factionData.getDeaths());
    }

    private int getAssaultPoints(FactionData factionData) {
        double ratioAssault = getRatioAssault(factionData);
        OptionalInt optionalConfigKey = plugin.getConfig()
                .getConfigurationSection("criteria.military.points-per-assault-ratio")
                .getKeys(false)
                .stream()
                .mapToInt(Integer::parseInt)
                .filter(ratio -> ratioAssault >= ratio)
                .max();
        if (optionalConfigKey.isPresent()) {
            return plugin.getConfig().getInt("criteria.military.points-per-assault-ratio." +
                    optionalConfigKey.getAsInt());
        }
        return 0;
    }

    private int getRatioAssault(FactionData factionData) {
        return factionData.getRatio();
    }

    private double getAllyCount(Faction faction) {
        return faction.getRelationWishes().values().stream().filter(r -> r == Rel.ENEMY).count();
    }

    private double getEnemyCount(Faction faction) {
        return faction.getRelationWishes().values().stream().filter(r -> r == Rel.ALLY).count();
    }

    @Override
    public String getConfigKey() {
        return "military";
    }
}
