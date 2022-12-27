package fr.hephaisto.ranking.calculation;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class CalculatorManager {
    private final List<Calculator> calculators;

    public CalculatorManager() {
        calculators = new ArrayList<>();
    }

    public void addCalculator(Calculator calculator) {
        calculators.add(calculator);
    }

    public void compute(Faction faction) {
        double totalScore = NumberUtils.floor(calculators.stream()
                .mapToDouble(calculator -> NumberUtils.floor(calculator.compute(faction)))
                .sum());
        //TODO update db
    }
}
