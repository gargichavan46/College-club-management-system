package com.collegeclubmanagement.dao;

import com.collegeclubmanagement.database.DBConnection;
import com.collegeclubmanagement.model.Club;
import com.collegeclubmanagement.model.ClubMember;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Access Object for Club Membership management.
 * Owned by shared backend (Riya).
 */
public class ClubMemberDAO {

    private static final Map<Integer, ClubMember> fallbackMemberships = new ConcurrentHashMap<>();

    static {
        long now = System.currentTimeMillis();
        long day = 86400000L;
        fallbackMemberships.put(501, new ClubMember(501, 1001, "Aarav Sharma", 101, "Coding Club", new Date(now - 30 * day), "ACTIVE"));
        fallbackMemberships.put(502, new ClubMember(502, 1002, "Diya Patel", 101, "Coding Club", new Date(now - 25 * day), "ACTIVE"));
        fallbackMemberships.put(503, new ClubMember(503, 1003, "Rohan Verma", 102, "Robotics Society", new Date(now - 45 * day), "ACTIVE"));
        fallbackMemberships.put(504, new ClubMember(504, 1004, "Sneha Reddy", 103, "Cultural Troupe", new Date(now - 15 * day), "ACTIVE"));
        fallbackMemberships.put(505, new ClubMember(505, 1005, "Kabir Joshi", 104, "Sports Club", new Date(now - 60 * day), "ACTIVE"));
        fallbackMemberships.put(506, new ClubMember(506, 1006, "Tanvi Deshmukh", 105, "Literary & Debating", new Date(now - 10 * day), "ACTIVE"));
        fallbackMemberships.put(507, new ClubMember(507, 1007, "Manish Kulkarni", 106, "Photography Club", new Date(now - 5 * day), "ACTIVE"));
        fallbackMemberships.put(508, new ClubMember(508, 1001, "Aarav Sharma", 102, "Robotics Society", new Date(now - 40 * day), "ACTIVE"));
        fallbackMemberships.put(509, new ClubMember(509, 1002, "Diya Patel", 103, "Cultural Troupe", new Date(now - 70 * day), "INACTIVE"));
    }

    private final ClubDAO clubDAO = new ClubDAO();

    public List<ClubMember> findAll() {
        String sql = "SELECT m.membership_id, m.student_id, s.name AS student_name, " +
                     "m.club_id, c.club_name, m.join_date, m.status " +
                     "FROM club_members m " +
                     "JOIN students s ON m.student_id = s.student_id " +
                     "JOIN clubs c ON m.club_id = c.club_id " +
                     "ORDER BY m.membership_id ASC";

        List<ClubMember> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToClubMember(rs));
            }
            return list;
        } catch (Exception e) {
            System.out.println("[ClubMemberDAO] DB findAll fallback: " + e.getMessage());
            List<ClubMember> res = new ArrayList<>(fallbackMemberships.values());
            res.sort((a, b) -> Integer.compare(a.getMembershipId(), b.getMembershipId()));
            return res;
        }
    }

    public List<ClubMember> findByStudentId(int studentId) {
        String sql = "SELECT m.membership_id, m.student_id, s.name AS student_name, " +
                     "m.club_id, c.club_name, m.join_date, m.status " +
                     "FROM club_members m " +
                     "JOIN students s ON m.student_id = s.student_id " +
                     "JOIN clubs c ON m.club_id = c.club_id " +
                     "WHERE m.student_id = ? " +
                     "ORDER BY m.membership_id ASC";

        List<ClubMember> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToClubMember(rs));
                }
            }
            return list;
        } catch (Exception e) {
            for (ClubMember cm : fallbackMemberships.values()) {
                if (cm.getStudentId() == studentId) {
                    list.add(cm);
                }
            }
            return list;
        }
    }

    public List<ClubMember> findByClubId(int clubId) {
        String sql = "SELECT m.membership_id, m.student_id, s.name AS student_name, " +
                     "m.club_id, c.club_name, m.join_date, m.status " +
                     "FROM club_members m " +
                     "JOIN students s ON m.student_id = s.student_id " +
                     "JOIN clubs c ON m.club_id = c.club_id " +
                     "WHERE m.club_id = ? " +
                     "ORDER BY m.membership_id ASC";

        List<ClubMember> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToClubMember(rs));
                }
            }
            return list;
        } catch (Exception e) {
            for (ClubMember cm : fallbackMemberships.values()) {
                if (cm.getClubId() == clubId) {
                    list.add(cm);
                }
            }
            return list;
        }
    }

    public boolean deactivateMembership(int membershipId) {
        String sqlUpdateMember = "UPDATE club_members SET status = 'INACTIVE' WHERE membership_id = ? AND status = 'ACTIVE'";
        String sqlUpdateClub = "UPDATE clubs SET current_members = GREATEST(0, current_members - 1) " +
                               "WHERE club_id = (SELECT club_id FROM club_members WHERE membership_id = ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psUpdateClub = conn.prepareStatement(sqlUpdateClub);
                 PreparedStatement psUpdateMember = conn.prepareStatement(sqlUpdateMember)) {

                psUpdateClub.setInt(1, membershipId);
                psUpdateClub.executeUpdate();

                psUpdateMember.setInt(1, membershipId);
                int rows = psUpdateMember.executeUpdate();

                conn.commit();

                // Update in fallback cache
                updateFallbackDeactivation(membershipId);
                return rows > 0;
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        } catch (Exception e) {
            System.out.println("[ClubMemberDAO] DB deactivate fallback: " + e.getMessage());
            return updateFallbackDeactivation(membershipId);
        }
    }

    private boolean updateFallbackDeactivation(int membershipId) {
        ClubMember cm = fallbackMemberships.get(membershipId);
        if (cm != null && !"INACTIVE".equalsIgnoreCase(cm.getStatus())) {
            cm.setStatus("INACTIVE");
            Club club = clubDAO.findById(cm.getClubId());
            if (club != null && club.getCurrentMembers() > 0) {
                club.setCurrentMembers(club.getCurrentMembers() - 1);
                clubDAO.update(club);
            }
            return true;
        }
        return false;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM club_members";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            return fallbackMemberships.size();
        }
        return fallbackMemberships.size();
    }

    private ClubMember mapResultSetToClubMember(ResultSet rs) throws Exception {
        return new ClubMember(
                rs.getInt("membership_id"),
                rs.getInt("student_id"),
                rs.getString("student_name"),
                rs.getInt("club_id"),
                rs.getString("club_name"),
                rs.getDate("join_date"),
                rs.getString("status")
        );
    }
}
