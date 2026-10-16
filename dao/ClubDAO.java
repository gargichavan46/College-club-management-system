package com.collegeclubmanagement.dao;

import com.collegeclubmanagement.database.DBConnection;
import com.collegeclubmanagement.model.Club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data Access Object for Club management.
 * Owned by shared backend (Riya).
 */
public class ClubDAO {

    // Thread-safe fallback store when MySQL is offline
    private static final Map<Integer, Club> fallbackClubs = new ConcurrentHashMap<>();
    private static final AtomicInteger fallbackIdGenerator = new AtomicInteger(100);

    static {
        // Initialize rich sample data for development and testing
        addSampleClub(new Club(101, "Coding Club", "Promotes competitive programming and web development.", "Technical", "Dr. A. Sharma", "Rohan Mehta", 50, 42, "ACTIVE"));
        addSampleClub(new Club(102, "Robotics Society", "Hands-on robotics, IoT, and embedded circuits.", "Technical", "Prof. R. Verma", "Priya Singh", 40, 38, "ACTIVE"));
        addSampleClub(new Club(103, "Cultural Troupe", "Organizes music, dance, and cultural fests.", "Cultural", "Dr. S. Nair", "Ananya Das", 60, 45, "ACTIVE"));
        addSampleClub(new Club(104, "Sports Club", "Athletics, cricket, football, and badminton.", "Sports", "Mr. V. Patil", "Vikram Rao", 80, 60, "ACTIVE"));
        addSampleClub(new Club(105, "Literary & Debating", "Public speaking, Model UN, and creative writing.", "Literary", "Dr. K. Iyer", "Sneha Kapoor", 35, 20, "ACTIVE"));
        addSampleClub(new Club(106, "Photography Club", "Visual storytelling and camera techniques.", "Arts", "Prof. M. Joshi", "Arjun Nair", 30, 28, "ACTIVE"));
        addSampleClub(new Club(107, "Astronomy Club", "Stargazing and space sciences (Currently paused).", "Academic", "Dr. P. Sen", "Kavya Menon", 25, 12, "INACTIVE"));
    }

    private static void addSampleClub(Club club) {
        fallbackClubs.put(club.getClubId(), club);
        if (club.getClubId() > fallbackIdGenerator.get()) {
            fallbackIdGenerator.set(club.getClubId());
        }
    }

    public List<Club> findAll() {
        String sql = "SELECT club_id, club_name, description, category, faculty_coordinator, " +
                     "student_coordinator, capacity, current_members, status FROM clubs ORDER BY club_id ASC";
        List<Club> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToClub(rs));
            }
            return list;
        } catch (Exception e) {
            System.out.println("[ClubDAO] DB findAll fallback: " + e.getMessage());
            List<Club> res = new ArrayList<>(fallbackClubs.values());
            res.sort((a, b) -> Integer.compare(a.getClubId(), b.getClubId()));
            return res;
        }
    }

    public Club findById(int clubId) {
        String sql = "SELECT club_id, club_name, description, category, faculty_coordinator, " +
                     "student_coordinator, capacity, current_members, status FROM clubs WHERE club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClub(rs);
                }
            }
        } catch (Exception e) {
            return fallbackClubs.get(clubId);
        }
        return fallbackClubs.get(clubId);
    }

    public boolean insert(Club club) {
        String sql = "INSERT INTO clubs (club_name, description, category, faculty_coordinator, " +
                     "student_coordinator, capacity, current_members, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, club.getClubName());
            ps.setString(2, club.getDescription());
            ps.setString(3, club.getCategory());
            ps.setString(4, club.getFacultyCoordinator());
            ps.setString(5, club.getStudentCoordinator());
            ps.setInt(6, club.getCapacity());
            ps.setInt(7, club.getCurrentMembers());
            ps.setString(8, club.getStatus());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        club.setClubId(generatedKeys.getInt(1));
                    }
                }
                fallbackClubs.put(club.getClubId(), club);
                return true;
            }
        } catch (Exception e) {
            System.out.println("[ClubDAO] DB insert fallback: " + e.getMessage());
            int newId = fallbackIdGenerator.incrementAndGet();
            club.setClubId(newId);
            fallbackClubs.put(newId, club);
            return true;
        }
        return false;
    }

    public boolean update(Club club) {
        String sql = "UPDATE clubs SET club_name = ?, description = ?, category = ?, " +
                     "faculty_coordinator = ?, student_coordinator = ?, capacity = ?, status = ? " +
                     "WHERE club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, club.getClubName());
            ps.setString(2, club.getDescription());
            ps.setString(3, club.getCategory());
            ps.setString(4, club.getFacultyCoordinator());
            ps.setString(5, club.getStudentCoordinator());
            ps.setInt(6, club.getCapacity());
            ps.setString(7, club.getStatus());
            ps.setInt(8, club.getClubId());

            int rows = ps.executeUpdate();
            fallbackClubs.put(club.getClubId(), club);
            return rows > 0;
        } catch (Exception e) {
            System.out.println("[ClubDAO] DB update fallback: " + e.getMessage());
            fallbackClubs.put(club.getClubId(), club);
            return true;
        }
    }

    public boolean deactivate(int clubId) {
        String sql = "UPDATE clubs SET status = 'INACTIVE' WHERE club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubId);
            int rows = ps.executeUpdate();
            Club c = fallbackClubs.get(clubId);
            if (c != null) {
                c.setStatus("INACTIVE");
            }
            return rows > 0;
        } catch (Exception e) {
            System.out.println("[ClubDAO] DB deactivate fallback: " + e.getMessage());
            Club c = fallbackClubs.get(clubId);
            if (c != null) {
                c.setStatus("INACTIVE");
                return true;
            }
            return false;
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM clubs";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            return fallbackClubs.size();
        }
        return fallbackClubs.size();
    }

    public int countActive() {
        String sql = "SELECT COUNT(*) FROM clubs WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            int count = 0;
            for (Club c : fallbackClubs.values()) {
                if ("ACTIVE".equalsIgnoreCase(c.getStatus())) count++;
            }
            return count;
        }
        int count = 0;
        for (Club c : fallbackClubs.values()) {
            if ("ACTIVE".equalsIgnoreCase(c.getStatus())) count++;
        }
        return count;
    }

    private Club mapResultSetToClub(ResultSet rs) throws Exception {
        return new Club(
                rs.getInt("club_id"),
                rs.getString("club_name"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getString("faculty_coordinator"),
                rs.getString("student_coordinator"),
                rs.getInt("capacity"),
                rs.getInt("current_members"),
                rs.getString("status")
        );
    }
}
