package com.collegeclubmanagement.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Shared Database Connection manager for MySQL JDBC.
 * Owned by shared backend (Riya).
 * Connects to database: college_club_db
 */
public class DBConnection {

    private static String URL = "jdbc:mysql://localhost:3306/college_club_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String USER = "root";
    private static String PASSWORD = "root";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        // Load properties if available
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                URL = props.getProperty("db.url", URL);
                USER = props.getProperty("db.user", USER);
                PASSWORD = props.getProperty("db.password", PASSWORD);
            }
        } catch (Exception ignored) {
        }

        // Register MySQL Driver
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] Warning: MySQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    private DBConnection() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets a new connection to MySQL database college_club_db.
     * @return Connection instance
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Safely closes a connection.
     * @param conn connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[DBConnection] Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Quick check if database connection can be established.
     * @return true if connected successfully
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
