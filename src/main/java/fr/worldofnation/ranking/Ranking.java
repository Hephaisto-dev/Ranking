package fr.worldofnation.ranking;

import fr.worldofnation.ranking.Scheduler.Scheduler;
import fr.worldofnation.ranking.listener.RankingListener;
import fr.worldofnation.ranking.manager.RankingManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import world.nations.Core;
//import world.nations.stats.StatsManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Ranking extends JavaPlugin {

    private String separator = "&7&m------------------------------------";
    private String host,id,pass;
    @Getter
    public Connection connection;

    @Getter
    public static Ranking instance;

    @Getter private static Economy econ = null;
   // @Getter
    //private StatsManager statsManager;

    @Getter private RankingManager rm;

    public void onEnable() {
        instance = this;
        this.rm = new RankingManager();
        //this.statsManager = Core.getPlugin().getStatsManager();
        System.out.println(this.separator);
        System.out.println("");
        System.out.println("§eClassementHebdo §a§lENABLED");
        System.out.println("");
        System.out.println(this.separator);
        this.saveDefaultConfig();
        this.loadDatabase();
        this.setupEconomy();
        this.loadEvent();
        try {
            this.loadModule();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run()
            {
                try {
                    Scheduler.schedule();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }, 0L, 120L);
    }

    public void onDisable()
    {
        System.out.println(this.separator);
        System.out.println("");
        System.out.println("§eClassementHebdo §a§lDISABLED");
        System.out.println("");
        System.out.println(this.separator);
        this.unloadDatabase();
    }

    public void loadDatabase()
    {
        this.host = "jdbc:mysql://"+ this.getConfig().getString("db_host") +":3306/" + this.getConfig().getString("db_name");
        this.id = (String) this.getConfig().getString("db_user");
        this.pass= (String) this.getConfig().getString("db_pass");
        try
        {
            connection = DriverManager.getConnection(host,id,pass);
            System.out.println("§eConnexion §aOK");
            PreparedStatement query = connection.prepareStatement("CREATE TABLE IF NOT EXISTS rankhebdo(id int(255) PRIMARY KEY AUTO_INCREMENT, faction varchar(255), activity int(255), management float(53), economy float(53), military float(53), technology float(53),created_at varchar(255), updated_at varchar(255), build float(53), total float(53), bourse float(53), member int(53));");

            query.executeUpdate();
            System.out.println("§eTable §aOK");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unloadDatabase()
    {
        try
        {
            if(connection != null && !connection.isClosed())
            {
                this.connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadEvent()
    {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new RankingListener(), this);
    }

    public void loadModule() throws SQLException {
        getRm().addFaction();
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            System.out.println("Vault dependency not found.");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


}
