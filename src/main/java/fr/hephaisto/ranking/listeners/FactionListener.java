package fr.hephaisto.ranking.listeners;

import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsNameChange;
import fr.hephaisto.ranking.Ranking;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionListener implements Listener {
    private final Ranking ranking;

    public FactionListener(Ranking ranking) {
        this.ranking = ranking;
    }

    @EventHandler
    public void onFactionNameChange(EventFactionsNameChange event) {
        String old_name = event.getFaction().getName();
        String new_name = event.getNewName();
        ranking.getDb().updateFactionName(old_name, new_name);
    }

    @EventHandler
    public void onFactionCreate(EventFactionsCreate event) {
        String name = event.getFactionName();
        ranking.getDb().insertFaction(name);
    }

    @EventHandler
    public void onFactionDelete(EventFactionsDisband event){
        String name = event.getFaction().getName();
        ranking.getDb().deleteFaction(name);
    }
}
