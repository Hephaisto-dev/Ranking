package fr.hephaisto.ranking.sql;

import com.massivecraft.factions.entity.Faction;
import fr.hephaisto.ranking.Ranking;
import fr.hephaisto.ranking.calculation.Calculator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import world.nations.Core;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class Database {
    private final DbConnection dbConnection;
    private final String factionTableName;
    private final ConfigurationSection columnsSection;
    private final String playersTableName;

    public Database(Ranking plugin) {
        dbConnection = new DbConnection(new DbCredentials(
                plugin.getConfig().getString("database.host"),
                plugin.getConfig().getString("database.user"),
                plugin.getConfig().getString("database.password"),
                plugin.getConfig().getString("database.database"),
                plugin.getConfig().getInt("database.port")));
        factionTableName = plugin.getConfig().getString("database.table");
        columnsSection = plugin.getConfig().getConfigurationSection("database.columns");
        playersTableName = plugin.getConfig().getString("database.table-players");
    }

    public boolean init() {
        try {
            final Connection connection = dbConnection.getConnection();
            if (connection == null) {
                Logger.getLogger("Ranking").severe("Database connection failed, please check your config.yml");
                return false;
            }
            // Create tables if not exists
            //Faction rank table
            Statement statement = connection.createStatement();
            String sql_request = "CREATE TABLE IF NOT EXISTS rankhebdo(id int(255) PRIMARY KEY AUTO_INCREMENT," +
                    " faction varchar(255), activity float(53), management float(53), economy float(53)," +
                    " military float(53), technology float(53), created_at varchar(255), updated_at varchar(255)," +
                    " build float(53), total float(53), bourse float(53);";
            sql_request = sql_request.replace("rankhebdo", factionTableName);
            for (String key : columnsSection.getKeys(false))
                sql_request = sql_request.replace(key, columnsSection.getString(key));
            statement.executeUpdate(sql_request);
            //Players table
            sql_request = "CREATE TABLE IF NOT EXISTS rank_players " +
                    "(uuid VARCHAR(36), faction VARCHAR(255), time_played BIGINT DEFAULT 0);";
            sql_request = sql_request.replace("rank_players", playersTableName);
            statement.executeUpdate(sql_request);
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
                    .prepareStatement("SELECT updated_at FROM " + factionTableName);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                timestamps.put(resultSet.getString(columnsSection.getString("faction")),
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
                    .prepareStatement("UPDATE " + factionTableName + " SET " + columnsSection.getString("faction") + " = ? WHERE " + columnsSection.getString("faction") + " = ?;");
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
                    .prepareStatement("SELECT bourse FROM " + factionTableName + " WHERE " +
                            columnsSection.getString("faction") + " = ?;");
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
                .append("UPDATE ").append(factionTableName).append(" SET ");
        computedScore.forEach((calculator, score) ->
                sql.append(columnsSection.getString(calculator.getConfigKey()))
                        .append(" = ")
                        .append(score).append(", "));
        sql.append("total = ").append(totalScore).append(", bourse = ")
                .append(Core.getPlugin().getEconomyManager().getBalance(faction.getName()))
                .append(", updated_at = ").append(new Timestamp(System.currentTimeMillis()))
                .append(" WHERE ").append(columnsSection.getString("faction")).append(" = ")
                .append(faction.getName()).append(";");
        try {
            dbConnection.getConnection().prepareStatement(sql.toString()).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayTime(Player player, long time) {
        try {
            PreparedStatement query = dbConnection.getConnection()
                    .prepareStatement("UPDATE ? SET time_played = time_played + ? WHERE uuid = ?;");
            query.setString(1, playersTableName);
            query.setLong(2, time);
            query.setString(3, player.getUniqueId().toString());
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getTimePlayed(UUID uuid) {
        try {
            PreparedStatement query = dbConnection.getConnection()
                    .prepareStatement("SELECT ? FROM ? WHERE uuid = ?;");
            query.setString(1, "time_played");
            query.setString(2, playersTableName);
            query.setString(3, uuid.toString());
            ResultSet resultSet = query.executeQuery();
            if(!resultSet.next())
            {
                return resultSet.getLong("time_played");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<UUID> getOldPlayers(Faction faction) {
        List<UUID> oldPlayers = new ArrayList<>();
        try {
            PreparedStatement query = dbConnection.getConnection()
                    .prepareStatement("SELECT ? FROM ? WHERE faction = ?;");
            query.setString(1, "uuid");
            query.setString(2, playersTableName);
            query.setString(3, faction.getName());
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()){
                oldPlayers.add(UUID.fromString(resultSet.getString("uuid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oldPlayers;
    }
}
