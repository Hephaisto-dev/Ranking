package fr.hephaisto.ranking.calculators;

import com.massivecraft.factions.entity.Faction;

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
        calculators.forEach(calculator -> calculator.compute(faction));
        //TODO update db
    }
}
