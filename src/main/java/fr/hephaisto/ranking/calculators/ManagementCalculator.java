package fr.hephaisto.ranking.calculators;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;

public class ManagementCalculator extends AbstractCalculator{
    public ManagementCalculator(Ranking plugin) {
        super(plugin);
    }

    public double compute(Faction faction) {
        double points = 0;
        int minMembers = plugin.getConfig().getInt("criteria.management.min-members");
        int pointsNotMinMember = plugin.getConfig().getInt("criteria.management.points-not-min-member");
        double pointsPerLeave = plugin.getConfig().getDouble("criteria.management.points-per-leave");
        double pointsPerRecruit = plugin.getConfig().getDouble("criteria.management.points-per-recruit");
        int maxRecruitsPoints = plugin.getConfig().getInt("criteria.management.max-recruits-points");
        double pointsPerRole = plugin.getConfig().getDouble("criteria.management.points-per-role");
        double powerMultiplier = plugin.getConfig().getDouble("criteria.management.power-multiplier");
        if (faction.getUPlayers().size() < minMembers) {
            points -= pointsNotMinMember;
        }
        points -= countLeft(faction) * pointsPerLeave;
        points += Math.min(countRecruits(faction) * pointsPerRecruit, maxRecruitsPoints);
        points += countRoles(faction) * pointsPerRole;
        points += (faction.getPower() / faction.getPowerMax()) * powerMultiplier;
        return Math.max(0, points);
    }

    private int countLeft(Faction faction) {
        //TODO count left
        return 0;
    }

    private int countRecruits(Faction faction) {
        return faction.getUPlayersWhereRole(Rel.RECRUIT).size();
    }

    private long countRoles(Faction faction) {
        return faction.getUPlayers().stream()
                .filter(uPlayer -> uPlayer.getRole() == Rel.RECRUIT ||
                        uPlayer.getRole() == Rel.MEMBER ||
                        uPlayer.getRole() == Rel.OFFICER ||
                        uPlayer.getRole() == Rel.LEADER)
                .count();
    }
}
