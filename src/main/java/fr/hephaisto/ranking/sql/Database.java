package fr.hephaisto.ranking.sql;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;
import fr.hephaisto.ranking.calculation.Calculator;
import org.bukkit.configuration.ConfigurationSection;
import world.nations.Core;
import world.nations.stats.data.FactionData;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Database {
    private final DbConnection dbConnection;
    private final String table_name;
    private final ConfigurationSection columns_section;

    public Database(Ranking plugin) {
        dbConnection = new DbConnection(new DbCredentials(
                plugin.getConfig().getString("database.host"),
                plugin.getConfig().getString("database.user"),
                plugin.getConfig().getString("database.password"),
                plugin.getConfig().getString("database.database"),
                plugin.getConfig().getInt("database.port")));
        table_name = plugin.getConfig().getString("database.table");
        columns_section = plugin.getConfig().getConfigurationSection("database.columns");
    }

    public boolean init() {
        try {
            final Connection connection = dbConnection.getConnection();
            if (connection == null) {
                Logger.getLogger("Ranking").severe("Database connection failed, please check your config.yml");
                return false;
            }
            // Create table if not exists
            String sql_request = "CREATE TABLE IF NOT EXISTS rankhebdo(id int(255) PRIMARY KEY AUTO_INCREMENT," +
                    " faction varchar(255), activity float(53), management float(53), economy float(53)," +
                    " military float(53), technology float(53), created_at varchar(255), updated_at varchar(255)," +
                    " build float(53), total float(53), bourse float(53), member int(53));";
            sql_request = sql_request.replace("rankhebdo", table_name);
            for (String key : columns_section.getKeys(false))
                sql_request = sql_request.replace(key, columns_section.getString(key));

            PreparedStatement query = connection.prepareStatement(sql_request);
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void close() {
        try {
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, LocalDateTime> getLastUpdatesByFactions() {
        Map<String, LocalDateTime> timestamps = new HashMap<>();
        try {
            PreparedStatement query = dbConnection.getConnection()
                    .prepareStatement("SELECT updated_at FROM " + table_name);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                timestamps.put(resultSet.getString(columns_section.getString("faction")),
                        resultSet.getTimestamp("updated_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timestamps;
    }

    public void updateFactionName(String oldName, String newName) {
        try {
            PreparedStatement query = dbConnection.getConnection()
                    .prepareStatement("UPDATE " + table_name + " SET " + columns_section.getString("faction") + " = ? WHERE " + columns_section.getString("faction") + " = ?;");
            query.setString(1, newName);
            query.setString(2, oldName);
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getBourse(Faction faction) {
        try {
            PreparedStatement query = dbConnection.getConnection()
                    .prepareStatement("SELECT bourse FROM " + table_name + " WHERE " +
                            columns_section.getString("faction") + " = ?;");
            query.setString(1, faction.getName());
            ResultSet resultSet = query.executeQuery();
            if(!resultSet.next())
            {
                return resultSet.getInt(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateFactionScore(Faction faction, Map<Calculator, Double> computedScore, double totalScore) {
        StringBuilder sql = new StringBuilder()
                .append("UPDATE ").append(table_name).append(" SET ");
        computedScore.forEach((calculator, score) ->
                sql.append(columns_section.getString(calculator.getConfigKey()))
                        .append(" = ")
                        .append(score).append(", "));
        sql.append("total = ").append(totalScore).append(", bourse = ")
                .append(Core.getPlugin().getEconomyManager().getBalance(faction.getName()))
                .append(", updated_at = ").append(new Timestamp(System.currentTimeMillis()))
                .append(" WHERE ").append(columns_section.getString("faction")).append(" = ")
                .append(faction.getName()).append(";");
        try {
            dbConnection.getConnection().prepareStatement(sql.toString()).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
