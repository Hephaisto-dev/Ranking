package fr.worldofnation.ranking.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.massivecraft.factions.FactionListComparator;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.UPlayer;
import fr.worldofnation.ranking.Ranking;
import org.bukkit.entity.Player;
import world.nations.Core;
import world.nations.stats.data.FactionData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RankingManager {

    private Ranking main = Ranking.getInstance();
    private Core core = Core.getPlugin();


    public void addFaction() throws SQLException {

        for(Faction f : getColl("factions_faction@default").getAll()) {
            String name = f.getName();
            {
                String query = "SELECT * FROM rankhebdo WHERE faction='"+ name +"'";
                PreparedStatement stmnt = this.main.getConnection().prepareStatement(query);
                ResultSet result = stmnt.executeQuery(query);
                if (!result.next()) {
                    int activite;
                    double gestion, economie, militaire, technologie, construction, total, bourse, membre;
                    String sql = "INSERT INTO rankhebdo VALUES(0,'" + name +"'," + 0 +"," + 0 +"," + 0 +"," + 0 +"," + 0+ "," + "0000-00-00 00:00:00" + "," + "0000-00-00 00:00:00" +","+ 0+","+ 0+","+ 0+","+0+");";
                    PreparedStatement stmnt2 = this.main.getConnection().prepareStatement(sql);
                    int result2 = stmnt2.executeUpdate(sql);

                }
            }
        }

    }

    public void updateFactionName(String name, String oldname) throws SQLException {
        String query = "SELECT * FROM rankhebdo WHERE faction=?"; //oldname
        //Check all faction
        PreparedStatement stmnt = this.main.getConnection().prepareStatement(query);
        stmnt.setString(1, "");
        int result = stmnt.executeUpdate();
        if(result == 0)
        {
            String q = "UPDATE rankhebdo SET faction = '" + name + "' WHERE faction = '" + oldname + "'";
            PreparedStatement stmnt2 = this.main.getConnection().prepareStatement(query);
            int result2 = stmnt2.executeUpdate();

            return;
        }

    }

    public void rankingAllFaction() throws SQLException {
        String query = "SELECT * FROM rankhebdo ORDER BY total DESC"; //oldname
        //Check all faction
        PreparedStatement stmnt = this.main.getConnection().prepareStatement(query);
        int result = stmnt.executeUpdate();
        if(result == 0)
        {

        }
        else {
            System.out.println(result);
        }
    }

    public String showFactionRanking()
    {
        return "";
    }

    public int calculatePoint() throws SQLException {
        int points = 0 , bourse = 0, membre = 0;

        for(Faction f : getColl("factions_faction@default").getAll())
        {
            //Gestion
            String query = "SELECT membre FROM rankhebdo WHERE faction=?";
            PreparedStatement stmnt = this.main.getConnection().prepareStatement(query);
            stmnt.setString(1, f.getName());
            ResultSet result = stmnt.executeQuery();
            if(!result.next())
            {
                membre = result.getInt(0);
            }
            if(f.getUPlayers().size() < membre)
            {
                points = points - (membre - f.getUPlayers().size());
            }
            else if(f.getUPlayers().size() > membre)
            {
                for(int i = 0; i < 6 ; i++)
                {
                    points = points + 1;
                }

            }

            if(f.getUPlayers().size() < 5)
            {
                points = points - 5;
            }
            points = (int) (((f.getPower() / f.getPowerMax()) *3) + points);
            boolean recruit = false, member = false, officer = false;
            float ptgestion = 0;
            for(UPlayer pl : f.getUPlayers())
            {
                if(pl.getRole() == Rel.RECRUIT && recruit == false)
                {
                    recruit = true;
                    points = (int) (points + 0.25);
                    ptgestion = (float) (ptgestion + 0.25);
                }
                else if(pl.getRole() == Rel.MEMBER && member == false)
                {
                    member = false;
                    points = (int) (points + 0.25);
                    ptgestion = (float) (ptgestion + 0.25);

                }
                else if(pl.getRole() == Rel.OFFICER && officer == false)
                {
                    officer = true;
                    points = (int) (points + 0.25);
                    ptgestion = (float) (ptgestion + 0.25);

                }
                else if(pl.getRole() == Rel.LEADER)
                {
                    points = (int) (points + 0.25);
                    ptgestion = (float) (ptgestion + 0.25);

                }
            }

            //Gestion
           // FactionData data = this.main.getStatsManager().getFaction(f.getName());
            FactionData data = Core.getPlugin().getStatsManager().getFaction(f.getName());

            //Economie
            points = (int) ((core.getEconomyManager().getBalance(f.getName()) / 500000) * 5);

            String query2 = "SELECT bourse FROM rankhebdo WHERE faction=?";
            PreparedStatement stmnt2 = this.main.getConnection().prepareStatement(query2);
            stmnt.setString(1, f.getName());
            ResultSet result2 = stmnt2.executeQuery();
            if(!result2.next())
            {
                bourse = result2.getInt(0);
            }
            points = (int) ((((core.getEconomyManager().getBalance(f.getName()) - bourse) / 200000) * 5) + points);
            //bourse = (int) core.getEconomyManager().getBalance(f.getName());
            int p_eco = (int) ((((core.getEconomyManager().getBalance(f.getName()) - bourse) / 200000) * 5) + points);
            //Economie

            //Militaire
            double p_mili = 0;

            if(getFactionAllies(getColl("factions_faction@default"), f.getName()).size() > 0)
            {
                for(int i = 0; i < 4; i++)
                {
                    points = (int) (points + 0.5);
                    p_mili = p_mili + 0.5;
                }
            }

            if(getFactionEnemy(getColl("factions_faction@default"), f.getName()).size() > 0)
            {
                for(int i = 0; i < 6; i++)
                {
                    points = (points + 1);
                    p_mili = p_mili + 1;

                }
            }
            if(data.getRatio() >= 15)
            {
                points = points + 3;
                p_mili = p_mili + 3;

            }
            else if(data.getRatio() >= 5)
            {
                points = points + 2;
                p_mili = p_mili + 2;

            }
            else if(data.getRatio() > 0)
            {
                points = points + 1;
                p_mili = p_mili + 1;

            }

            int kdr = Integer.parseInt(data.getKDR());
            if(Integer.parseInt(data.getKDR()) > 2)
            {
                points = points + 3;
                p_mili = p_mili + 3;

            }
            else if(Integer.parseInt(data.getKDR())< 2 && kdr >= 1.5)
            {
                points = points + 2;
                p_mili = p_mili + 2;

            }
            else if(Integer.parseInt(data.getKDR())< 1.5 && kdr >= 1)
            {
                points = points + 1;
                p_mili = p_mili + 1;

            }
            else if(Integer.parseInt(data.getKDR())< 0 && kdr >= -1)
            {
                points = points - 1;
                p_mili = p_mili - 1;

            }
            points = (int)((data.getScorezone() / 500) * 4) + points;
            //Militaire

            String date_create = "0000-00-00 00:00:00";

            String sql = "UPDATE rankbedo SET activity = 0, management = "+ ptgestion + ", economy = " + p_eco + ", military = " + p_mili + ", total = " + points + ", bourse = " + bourse+ ",created_at =  " + date_create + " updaed_at = + 0000-00-00 00:00:00 "+"WHERE name ='"+ f.getName() + "';";

            //String sql = "INSERT INTO rankhebdo VALUES(0,'" + f.getName() +"'," + 0 +"," + ptgestion +"," + p_eco +"," + p_mili +"," + 0+","+ 0+","+ points+","+ bourse+","+0+");";
            PreparedStatement stmnt22 = this.main.getConnection().prepareStatement(sql);
            int result22 = stmnt22.executeUpdate(sql);
        }



        //Inséré nombre de points
        return points;

    }

    public static ArrayList<Faction> getFactionColl(Player p ) {
        ArrayList<Faction> factionList = new ArrayList<Faction>(FactionColls.get().get(p).getAll(null, FactionListComparator.get()));

        return factionList;
    }

    public static FactionColl getColl(String name) {
        for(FactionColl coll : FactionColls.get().getColls()) {
            if(coll.getName().equalsIgnoreCase(name))
                return coll;
        }

        return null;
    }

    public static ArrayList<Faction> getFactionAllies(FactionColl coll, String name) {

        Faction targetFaction = coll.getByName(name);

        ArrayList<Faction> allyFac = new ArrayList<Faction>();

        for(Faction fac : coll.getAll()) {
            if(fac.getName().contains("SafeZone") || fac.getName().contains("Wilderness") || fac.getName().contains("WarZone"))
                continue;

            if(fac.getRelationTo(targetFaction) == Rel.ALLY)
                allyFac.add(fac);

        }
        return allyFac;


    }

    public static ArrayList<Faction> getFactionEnemy(FactionColl coll, String name) {

        Faction targetFaction = coll.getByName(name);

        ArrayList<Faction> allyFac = new ArrayList<Faction>();

        for(Faction fac : coll.getAll()) {
            if(fac.getName().contains("SafeZone") || fac.getName().contains("Wilderness") || fac.getName().contains("WarZone"))
                continue;

            if(fac.getRelationTo(targetFaction) == Rel.ENEMY)
                allyFac.add(fac);

        }
        return allyFac;


    }

}


