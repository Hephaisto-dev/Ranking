package fr.hephaisto.ranking.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DbConnection {
    private final DbCredentials dbCredentials;
    private Connection connection;

    public DbConnection(DbCredentials credentials) {
        this.dbCredentials = credentials;
        connect();
    }

    private void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(dbCredentials.toURI(), dbCredentials.getUser(), dbCredentials.getPass());
            Logger.getLogger("Minecraft").info("Connection to database established");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            if (!connection.isClosed()) {
                connection.close();
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection != null && connection.isClosed())
            connect();
        return connection;
    }
}
