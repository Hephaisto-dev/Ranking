package fr.hephaisto.ranking.sql;

import fr.hephaisto.ranking.Ranking;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Database {
    private final DbConnection dbConnection;
    private String table_name;

    public Database(Ranking plugin) {
        dbConnection = new DbConnection(new DbCredentials(
                plugin.getConfig().getString("database.host"),
                plugin.getConfig().getString("database.user"),
                plugin.getConfig().getString("database.password"),
                plugin.getConfig().getString("database.database"),
                plugin.getConfig().getInt("database.port")));
        table_name = plugin.getConfig().getString("database.table");
    }

    public boolean init() {
        try {
            final Connection connection = dbConnection.getConnection();
            if (connection == null) {
                Logger.getLogger("Ranking").severe("Database connection failed, please check your config.yml");
                return false;
            }
            // Check if table exists using connection metadata
            DatabaseMetaData metaData = connection.getMetaData();
            if (!metaData.getTables(null, null, table_name, null).next()) {
                Logger.getLogger("Ranking").severe("Table " + table_name + " doesn't exists");
                return false;
            }
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
