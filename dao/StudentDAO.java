package com.collegeclubmanagement.dao;

import com.collegeclubmanagement.database.DBConnection;
import com.collegeclubmanagement.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Access Object for Student management.
 * Owned by shared backend (Riya).
 */
public class StudentDAO {

    private static final Map<Integer, Student> fallbackStudents = new ConcurrentHashMap<>();

    static {
        fallbackStudents.put(1001, new Student(1001, "Aarav Sharma", "aarav.sharma@college.edu", "9876543210", "Computer Science", 3));
        fallbackStudents.put(1002, new Student(1002, "Diya Patel", "diya.patel@college.edu", "9876543211", "Information Technology", 2));
        fallbackStudents.put(1003, new Student(1003, "Rohan Verma", "rohan.verma@college.edu", "9876543212", "Mechanical Engineering", 4));
        fallbackStudents.put(1004, new Student(1004, "Sneha Reddy", "sneha.reddy@college.edu", "9876543213", "Electronics & Comm", 1));
        fallbackStudents.put(1005, new Student(1005, "Kabir Joshi", "kabir.joshi@college.edu", "9876543214", "Civil Engineering", 3));
        fallbackStudents.put(1006, new Student(1006, "Tanvi Deshmukh", "tanvi.deshmukh@college.edu", "9876543215", "Computer Science", 2));
        fallbackStudents.put(1007, new Student(1007, "Manish Kulkarni", "manish.k@college.edu", "9876543216", "Data Science", 4));
    }

    public List<Student> findAll() {
        String sql = "SELECT student_id, name, email, phone, department, year FROM students ORDER BY student_id ASC";
        List<Student> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
            return list;
        } catch (Exception e) {
            System.out.println("[StudentDAO] DB findAll fallback: " + e.getMessage());
            List<Student> res = new ArrayList<>(fallbackStudents.values());
            res.sort((a, b) -> Integer.compare(a.getStudentId(), b.getStudentId()));
            return res;
        }
    }

    public Student findById(int studentId) {
        String sql = "SELECT student_id, name, email, phone, department, year FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (Exception e) {
            return fallbackStudents.get(studentId);
        }
        return fallbackStudents.get(studentId);
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            return fallbackStudents.size();
        }
        return fallbackStudents.size();
    }

    private Student mapResultSetToStudent(ResultSet rs) throws Exception {
        return new Student(
                rs.getInt("student_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("department"),
                rs.getInt("year")
        );
    }
}
