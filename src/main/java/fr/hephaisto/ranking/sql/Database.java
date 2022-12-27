package fr.hephaisto.ranking.sql;

import fr.hephaisto.ranking.Ranking;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
            final String[] sql_request = {"CREATE TABLE IF NOT EXISTS rankhebdo(id int(255) PRIMARY KEY AUTO_INCREMENT," +
                    " faction varchar(255), activity int(255), management float(53), economy float(53)," +
                    " military float(53), technology float(53), created_at varchar(255), updated_at varchar(255)," +
                    " build float(53), total float(53), bourse float(53), member int(53));"
                            .replace("rankhebdo", table_name)};
            columns_section.getKeys(false).forEach(column ->
                    sql_request[0] = sql_request[0].replace(column, columns_section.getString(column)));
            PreparedStatement query = connection.prepareStatement(sql_request[0]);
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
}
