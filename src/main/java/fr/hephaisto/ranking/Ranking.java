package fr.hephaisto.ranking;

import org.bukkit.plugin.java.JavaPlugin;

public final class Ranking extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
