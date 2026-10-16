package com.collegeclubmanagement.dao;

import com.collegeclubmanagement.database.DBConnection;
import com.collegeclubmanagement.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Access Object for Admin authentication and management.
 * Owned by shared backend (Riya).
 */
public class AdminDAO {

    // Fallback in-memory storage if database connection is unavailable
    private static final Map<String, Admin> fallbackAdmins = new ConcurrentHashMap<>();

    static {
        fallbackAdmins.put("admin", new Admin(1, "admin", "admin123", "Chief Administrator", "admin@college.edu"));
        fallbackAdmins.put("charvi", new Admin(2, "charvi", "admin123", "Charvi (Admin)", "charvi@college.edu"));
    }

    public Admin authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        String sql = "SELECT admin_id, username, password, full_name, email FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                            rs.getInt("admin_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("email")
                    );
                }
            }
        } catch (Exception e) {
            // Log notice and try fallback admin store
            System.out.println("[AdminDAO] MySQL check failed (" + e.getMessage() + "), using fallback credentials store.");
            Admin fallback = fallbackAdmins.get(username.trim().toLowerCase());
            if (fallback != null && fallback.getPassword().equals(password)) {
                return fallback;
            }
        }
        return null;
    }

    public Admin findByUsername(String username) {
        if (username == null) return null;
        String sql = "SELECT admin_id, username, password, full_name, email FROM admins WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                            rs.getInt("admin_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("email")
                    );
                }
            }
        } catch (Exception e) {
            return fallbackAdmins.get(username.trim().toLowerCase());
        }
        return null;
    }
}
