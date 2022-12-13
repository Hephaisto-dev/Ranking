package fr.worldofnation.ranking.listener;

import com.massivecraft.factions.event.EventFactionsNameChange;
import fr.worldofnation.ranking.Ranking;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;

public class RankingListener implements Listener
{
    private Ranking main = Ranking.getInstance();
    @EventHandler
    public void onFactionRename(EventFactionsNameChange e) throws SQLException {
        String old_name = e.getFaction().getName();
        String new_name = e.getNewName();
        this.main.getRm().updateFactionName(new_name, old_name);
    }
}
