package fr.hephaisto.ranking.listeners;

import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class MemberFlowListener implements Listener {

    private List<UPlayer> joinedPlayers;
    private List<UPlayer> leftPlayers;

    @EventHandler
    public void onMembershipChange(EventFactionsMembershipChange event){
        if (event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.JOIN) {
            joinedPlayers.add(event.getUPlayer());
        }
        else if (event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.LEAVE) {
            leftPlayers.add(event.getUPlayer());
        }
    }
}
