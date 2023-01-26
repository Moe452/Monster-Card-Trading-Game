package at.fhtw.sampleapp.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final DatabaseManager INSTANCE = new DatabaseManager();

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/monster_card_trading",
                    "postgres",
                    "123456789");
        } catch (SQLException e) {
            throw new DataAccessException("Datenbankverbindungsaufbau nicht erfolgreich", e);
        }
    }
}

//to use: Connection connection = DatabaseManager.getInstance().getConnection();