package fr.hephaisto.ranking.calculators;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;

public abstract class AbstractCalculator implements Calculator{
    protected final Ranking plugin;

    public AbstractCalculator(Ranking plugin) {
        this.plugin = plugin;
    }

    @Override
    public double compute(Faction faction) {
        return 0;
    }
}
