package fr.hephaisto.ranking.commands;

import com.massivecraft.factions.entity.FactionColls;
import fr.hephaisto.ranking.Ranking;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClassementCommand implements CommandExecutor {
    private final Ranking plugin;

    public ClassementCommand(Ranking plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (commandSender.isOp()) {
                if (strings.length == 1) {
                    String factionName = strings[0];
                    FactionColls.get().getColls().forEach(factionColl -> factionColl.getAll().stream()
                            .filter(faction -> faction.getName().equalsIgnoreCase(factionName))
                            .forEach(faction -> plugin.getCalculatorManager().compute(faction)));
                }
            }
        }
        return true;
    }
}
