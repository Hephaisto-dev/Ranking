package fr.hephaisto.ranking.calculators;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;

public class EconomyCalculator extends AbstractCalculator{
    public EconomyCalculator(Ranking plugin) {
        super(plugin);
    }

    public double compute(Faction faction) {
        return 0;
    }
}
