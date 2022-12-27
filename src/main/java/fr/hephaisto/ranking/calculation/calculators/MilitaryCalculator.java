package fr.hephaisto.ranking.calculation.calculators;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;
import fr.hephaisto.ranking.calculation.AbstractCalculator;

public class MilitaryCalculator extends AbstractCalculator {
    public MilitaryCalculator(Ranking plugin) {
        super(plugin);
    }

    @Override
    public double compute(Faction faction) {
        double points = 0;
        double pointsPerAlly = plugin.getConfig().getDouble("criteria.military.points-per-ally");
        double maxAllies = plugin.getConfig().getDouble("criteria.military.max-allies");
        double pointsPerEnemy = plugin.getConfig().getDouble("criteria.military.points-per-enemy");
        double maxEnemies = plugin.getConfig().getDouble("criteria.military.max-enemies");
        double ratioPointsScoreZone = plugin.getConfig().getDouble("criteria.military.ratio-points-scorezone");
        return 0;
    }
}
