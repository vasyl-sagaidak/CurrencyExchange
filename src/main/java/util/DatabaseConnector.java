package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnector {
//    private static final String URL = "jdbc:sqlite:data/database.db";
    private static final String URL = "jdbc:sqlite:C:/Users/Skogarren/projects/CurrencyExchange/data/database.db";

    private DatabaseConnector(){
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerPostgreSQLDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found!");
            return;
        }
    }

    public static void registerSQLiteDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found!");
            return;
        }
    }

    public static void registerMySqlDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("SQL JDBC Driver not found!");
            return;
        }
    }
}
