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
        return 0;
    }
}
