package fr.hephaisto.ranking;

import fr.hephaisto.ranking.sql.Database;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ranking extends JavaPlugin {
    private Database database;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        database = new Database(this);
        if (!database.init()) {
            getServer().getPluginManager().disablePlugin(this);
        }

        TaskManager taskManager = new TaskManager(database.getLastUpdatesByFactions());
        taskManager.scheduleTasks(this);
    }

    @Override
    public void onDisable() {
        database.close();
    }
}
