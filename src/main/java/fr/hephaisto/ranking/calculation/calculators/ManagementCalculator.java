package fr.hephaisto.ranking.calculation.calculators;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import fr.hephaisto.ranking.Ranking;
import fr.hephaisto.ranking.calculation.AbstractCalculator;

import java.util.List;
import java.util.UUID;

public class ManagementCalculator extends AbstractCalculator {
    public ManagementCalculator(Ranking plugin) {
        super(plugin);
    }

    @Override
    public double compute(Faction faction) {
        double points = 0;
        int minMembers = plugin.getConfig().getInt("criteria.management.min-members");
        double pointsNotMinMember = plugin.getConfig().getDouble("criteria.management.points-not-min-member");
        double pointsPerLeave = plugin.getConfig().getDouble("criteria.management.points-per-leave");
        double pointsPerRecruit = plugin.getConfig().getDouble("criteria.management.points-per-recruit");
        double maxRecruitsPoints = plugin.getConfig().getDouble("criteria.management.max-recruits-points");
        double pointsPerRole = plugin.getConfig().getDouble("criteria.management.points-per-role");
        double powerMultiplier = plugin.getConfig().getDouble("criteria.management.power-multiplier");
        if (faction.getUPlayers().size() < minMembers) {
            points -= pointsNotMinMember;
        }
        List<UUID> oldPlayersUUID = plugin.getDb().getOldPlayers(faction);
        points -= countLeft(faction, oldPlayersUUID) * pointsPerLeave;
        points += Math.min(countJoin(faction, oldPlayersUUID) * pointsPerRecruit, maxRecruitsPoints);
        points += countRoles(faction) * pointsPerRole;
        points += (faction.getPower() / faction.getPowerMax()) * powerMultiplier;
        return Math.max(0, Math.min(points, 10));
    }

    private long countLeft(Faction faction, List<UUID> oldPlayersUUID) {
        return oldPlayersUUID.stream()
                .filter(playerUUID -> faction.getUPlayers().stream()
                        .noneMatch(uPlayer -> uPlayer.getUuid().equals(playerUUID)))
                .count();
    }

    private long countJoin(Faction faction, List<UUID> oldPlayersUUID) {
        return faction.getUPlayers().stream()
                .filter(uPlayer -> !oldPlayersUUID.contains(uPlayer.getUuid()))
                .count();
    }

    private long countRoles(Faction faction) {
        return faction.getUPlayers().stream()
                .map(UPlayer::getRole)
                .distinct()
                .filter(role -> role == Rel.RECRUIT ||
                        role == Rel.MEMBER ||
                        role == Rel.OFFICER ||
                        role == Rel.LEADER)
                .count();
    }

    @Override
    public String getConfigKey() {
        return "management";
    }
}
