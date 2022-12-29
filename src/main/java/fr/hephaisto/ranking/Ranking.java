package fr.hephaisto.ranking;

import fr.hephaisto.ranking.calculation.calculators.EconomyCalculator;
import fr.hephaisto.ranking.calculation.calculators.MilitaryCalculator;
import fr.hephaisto.ranking.listeners.FactionListener;
import fr.hephaisto.ranking.listeners.MemberFlowListener;
import fr.hephaisto.ranking.calculation.calculators.ActivityCalculator;
import fr.hephaisto.ranking.calculation.CalculatorManager;
import fr.hephaisto.ranking.calculation.calculators.ManagementCalculator;
import fr.hephaisto.ranking.listeners.PlayHoursListener;
import fr.hephaisto.ranking.sql.Database;
import fr.hephaisto.ranking.tasks.TaskManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ranking extends JavaPlugin {
    private Database database;
    private CalculatorManager calculatorManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Init the database
        database = new Database(this);
        if (!database.init()) {
            getServer().getPluginManager().disablePlugin(this);
        }

        setupTasks();
        registerListeners();
        setupCalculators();
    }

    private void setupCalculators() {
        calculatorManager = new CalculatorManager(this);
        calculatorManager.addCalculator(new ActivityCalculator(this));
        calculatorManager.addCalculator(new ManagementCalculator(this));
        calculatorManager.addCalculator(new MilitaryCalculator(this));
        calculatorManager.addCalculator(new EconomyCalculator(this));
    }

    private void setupTasks() {
        TaskManager taskManager = new TaskManager(database.getLastUpdatesByFactions(), this);
        taskManager.scheduleTasks();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayHoursListener(), this);
        getServer().getPluginManager().registerEvents(new MemberFlowListener(), this);
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
}
