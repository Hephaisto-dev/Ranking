package fr.hephaisto.ranking;

import fr.hephaisto.ranking.calculation.calculators.EconomyCalculator;
import fr.hephaisto.ranking.calculation.calculators.MilitaryCalculator;
import fr.hephaisto.ranking.listeners.FactionListener;
import fr.hephaisto.ranking.calculation.calculators.ActivityCalculator;
import fr.hephaisto.ranking.calculation.CalculatorManager;
import fr.hephaisto.ranking.calculation.calculators.ManagementCalculator;
import fr.hephaisto.ranking.listeners.PlayHoursListener;
import fr.hephaisto.ranking.sql.Database;
import fr.hephaisto.ranking.tasks.TaskManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ranking extends JavaPlugin {
    private Database database;
    private final CalculatorManager calculatorManager = new CalculatorManager(this);
    private TaskManager taskManager;
    private PlayHoursListener playHoursListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Init the database
        database = new Database(this);
        if (!database.init()) {
            getLogger().severe("Failed to connect to the database. Disabling the plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        setupCalculators();
        setupTasks();
        registerListeners();
    }

    private void setupCalculators() {
        calculatorManager.addCalculator(new ActivityCalculator(this));
        calculatorManager.addCalculator(new ManagementCalculator(this));
        calculatorManager.addCalculator(new MilitaryCalculator(this));
        calculatorManager.addCalculator(new EconomyCalculator(this));
    }

    private void setupTasks() {
        taskManager = new TaskManager(database.getLastUpdatesByFactions(), this);
        taskManager.scheduleTasks();
    }

    private void registerListeners() {
        playHoursListener = new PlayHoursListener(this);
        getServer().getPluginManager().registerEvents(playHoursListener, this);
        getServer().getPluginManager().registerEvents(new FactionListener(this), this);
    }

    @Override
    public void onDisable() {
        database.close();
    }

    public CalculatorManager getCalculatorManager() {
        return calculatorManager;
    }

    public Database getDb() {
        return database;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public PlayHoursListener getPlayHoursListener() {
        return playHoursListener;
    }
}
