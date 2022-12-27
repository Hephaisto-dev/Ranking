package fr.hephaisto.ranking.calculators;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;

public class ManagementCalculator implements Calculator{
    private final Ranking plugin;

    public ManagementCalculator(Ranking plugin) {
        this.plugin = plugin;
    }

    public int compute(Faction faction) {
        int minMembers = plugin.getConfig().getInt("criteria.management.min-members");
        int pointsNotMinMember = plugin.getConfig().getInt("criteria.management.points-not-min-member");
        double pointsPerLeave = plugin.getConfig().getDouble("criteria.management.points-per-leave");
        double pointsPerRecruit = plugin.getConfig().getDouble("criteria.management.points-per-recruit");
        int maxRecruits = plugin.getConfig().getInt("criteria.management.max-recruits");
        double pointsPerRole = plugin.getConfig().getDouble("criteria.management.points-per-role");
        double powerMultiplier = plugin.getConfig().getDouble("criteria.management.power-multiplier");
        return 0;
    }
}
