package fr.hephaisto.ranking.calculation;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;
import fr.hephaisto.ranking.utils.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculatorManager {
    private final List<Calculator> calculators;
    private final Ranking plugin;

    public CalculatorManager(Ranking plugin) {
        this.plugin = plugin;
        calculators = new ArrayList<>();
    }

    public void addCalculator(Calculator calculator) {
        calculators.add(calculator);
    }

    public void compute(Faction faction) {
        Map<Calculator, Double> computedScore = new HashMap<>();
        for (Calculator calculator : calculators) {
            double roundedScore = NumberUtils.floor(calculator.compute(faction));
            computedScore.put(calculator, roundedScore);
        }
        plugin.getDb().updateFactionScore(faction, computedScore);
    }
}
