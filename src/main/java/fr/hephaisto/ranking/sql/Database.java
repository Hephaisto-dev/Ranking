package fr.hephaisto.ranking.sql;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.UPlayer;
import fr.hephaisto.ranking.Ranking;
import fr.hephaisto.ranking.calculation.Calculator;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
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
    private final List<String> ignoredFactions;

    public Database(Ranking plugin) {
        dbConnection = new DbConnection(new DbCredentials(plugin.getConfig().getString("database.host"),
                plugin.getConfig().getString("database.user"), plugin.getConfig().getString("database.password"),
                plugin.getConfig().getString("database.database"), plugin.getConfig().getInt("database.port")));
        factionTableName = plugin.getConfig().getString("database.table");
        columnsSection = plugin.getConfig().getConfigurationSection("database.columns");
        playersTableName = plugin.getConfig().getString("database.table-players");
        ignoredFactions = plugin.getConfig().getStringList("ignored-factions");
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
            String sql_request = "CREATE TABLE IF NOT EXISTS rankhebdo(id int(255) PRIMARY KEY AUTO_INCREMENT, " +
                    "faction varchar(255), activity float(53) default 0, management float(53) default 0, economy " +
                    "float(53) default 0, military float(53) default 0, technology float(53) default 0, " +
                    "created_at varchar(255), updated_at varchar(255), build float(53) default 0, bourse BIGINT default 0);";
            sql_request = sql_request.replace("rankhebdo", factionTableName);
            for (String key : columnsSection.getKeys(false))
                sql_request = sql_request.replace(key, columnsSection.getString(key));
            statement.executeUpdate(sql_request);
            //Players table
            sql_request = "CREATE TABLE IF NOT EXISTS rank_players (uuid VARCHAR(36) PRIMARY KEY, faction VARCHAR(255), " +
                    "time_played BIGINT DEFAULT 0);";
            sql_request = sql_request.replace("rank_players", playersTableName);
            statement.executeUpdate(sql_request);
            statement.close();
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

    public Map<Faction, LocalDateTime> getLastUpdatesByFactions() {
        Map<Faction, LocalDateTime> timestamps = new HashMap<>();
        try (PreparedStatement query = dbConnection.getConnection()
                .prepareStatement("SELECT * FROM " + factionTableName)) {
            try (ResultSet resultSet = query.executeQuery()) {
                Map<String, LocalDateTime> timestampsByFactionNames = new HashMap<>();
                if (resultSet.next()) {
                    do {
                        timestampsByFactionNames.put(resultSet.getString(columnsSection.getString("faction")),
                                resultSet.getTimestamp("updated_at").toLocalDateTime());
                    } while (resultSet.next());
                }
                List<Faction> createdFactions = new ArrayList<>();
                for (FactionColl coll : FactionColls.get().getColls()) {
                    for (Faction faction : coll.getAll()) {
                        if (ignoredFactions.contains(ChatColor.stripColor(faction.getName())))
                            continue;
                        if (!timestampsByFactionNames.containsKey(faction.getName())) {
                            createdFactions.add(faction);
                            timestamps.put(faction, LocalDateTime.now());
                        } else
                            timestamps.put(faction, timestampsByFactionNames.get(faction.getName()));
                    }
                }
                for (Faction faction : createdFactions) {
                    insertFaction(faction.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timestamps;
    }

    public void updateFactionName(String oldName, String newName) {
        try {
            PreparedStatement query = dbConnection.getConnection().prepareStatement(
                    "UPDATE " + factionTableName + " SET " + columnsSection.getString(
                            "faction") + " = ? WHERE " + columnsSection.getString("faction") + " = ?;");
            query.setString(1, newName);
            query.setString(2, oldName);
            query.executeUpdate();
            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getBourse(Faction faction) {
        try {
            PreparedStatement query = dbConnection.getConnection().prepareStatement(
                    "SELECT bourse FROM " + factionTableName + " WHERE " + columnsSection.getString(
                            "faction") + " = ?;");
            query.setString(1, faction.getName());
            ResultSet resultSet = query.executeQuery();
            if (resultSet.last()) {
                return resultSet.getInt("bourse");
            }
            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateFactionScore(Faction faction, Map<Calculator, Double> computedScore) {
        StringBuilder sql = new StringBuilder().append("INSERT INTO ").append(factionTableName).append(" (");
        computedScore.forEach((calculator, score) -> {
            sql.append(calculator.getConfigKey()).append(", ");
        });
        sql.append("faction, updated_at, bourse, created_at) VALUES (");
        computedScore.forEach((calculator, score) -> sql.append(score).append(", "));
        sql.append("?, ?, ?, ?)");
        try {
            PreparedStatement preparedStatement = dbConnection.getConnection().prepareStatement(sql.toString());
            preparedStatement.setString(1, faction.getName());
            preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setDouble(3, Core.getPlugin().getEconomyManager().getBalance(faction.getName()));
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayTime(UPlayer uPlayer, long time) {
        try {
            if (uPlayer.getFaction().isNone())
                return;
            PreparedStatement query = dbConnection.getConnection().prepareStatement(
                    "INSERT INTO " + playersTableName + " (uuid, time_played, faction) VALUES (?, ?, ?) ON DUPLICATE "
                            + "KEY UPDATE " + "time_played = time_played + VALUES(time_played);");
            query.setString(1, uPlayer.getUuid().toString());
            query.setLong(2, time);
            query.setString(3, uPlayer.getFactionName());
            query.executeUpdate();
            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getTimePlayed(UUID uuid) {
        try {
            PreparedStatement query = dbConnection.getConnection()
                    .prepareStatement("SELECT time_played FROM " + playersTableName + " WHERE uuid = ?;");
            query.setString(1, uuid.toString());
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("time_played");
            }
            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<UUID> getOldPlayers(Faction faction) {
        List<UUID> oldPlayers = new ArrayList<>();
        try {
            final String query = "SELECT uuid FROM " + playersTableName + " WHERE faction = ?;";
            PreparedStatement preparedStatement = dbConnection.getConnection().prepareStatement(query);
            preparedStatement.setString(1, faction.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                do {
                    oldPlayers.add(UUID.fromString(resultSet.getString("uuid")));
                } while (resultSet.next());
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oldPlayers;
    }

    public void insertFaction(String name) {
        try {
            PreparedStatement query = dbConnection.getConnection().prepareStatement(
                    "INSERT INTO " + factionTableName + " (" + columnsSection.getString(
                            "faction") + ", created_at, updated_at) VALUES (?, ?, ?);");
            query.setString(1, name);
            query.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            query.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            query.executeUpdate();
            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
