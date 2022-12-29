package fr.hephaisto.ranking.calculation.calculators;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.money.Money;
import fr.hephaisto.ranking.Ranking;
import fr.hephaisto.ranking.calculation.AbstractCalculator;
import world.nations.Core;

public class EconomyCalculator extends AbstractCalculator {
    public EconomyCalculator(Ranking plugin) {
        super(plugin);
    }

    @Override
    public double compute(Faction faction) {
        double points = 0;
        double ratioPointsMoney = plugin.getConfig().getDouble("criteria.economy.ratio-points-money");
        double maxPointsMoney = plugin.getConfig().getDouble("criteria.economy.max-points-money");
        double ratioPointsWeekProfit = plugin.getConfig().getDouble("criteria.economy.ratio-points-week-profit");
        double maxPointsWeekProfit = plugin.getConfig().getDouble("criteria.economy.max-points-week-profit");
        double balance = Core.getPlugin().getEconomyManager().getBalance(faction.getName());
        points += Math.min(Money.get(faction) * ratioPointsMoney, maxPointsMoney);
        points += Math.min((balance - getLastBalance(faction)) * ratioPointsWeekProfit, maxPointsWeekProfit);
        return points;
    }

    private double getLastBalance(Faction faction) {
        return plugin.getDb().getBourse(faction);
    }

    @Override
    public String getConfigKey() {
        return "economy";
    }
}
