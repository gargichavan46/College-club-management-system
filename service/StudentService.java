package com.collegeclubmanagement.service;

import com.collegeclubmanagement.dao.StudentDAO;
import com.collegeclubmanagement.model.Student;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Layer for Student operations.
 * Owned by shared backend (Riya).
 */
public class StudentService {

    private final StudentDAO studentDAO;
    private static final StudentService instance = new StudentService();

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public static StudentService getInstance() {
        return instance;
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    public Student getStudentById(int studentId) {
        return studentDAO.findById(studentId);
    }

    public int getTotalStudentsCount() {
        return studentDAO.countAll();
    }

    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStudents();
        }
        String term = keyword.trim().toLowerCase();
        return getAllStudents().stream()
                .filter(s -> String.valueOf(s.getStudentId()).contains(term) ||
                             (s.getName() != null && s.getName().toLowerCase().contains(term)) ||
                             (s.getEmail() != null && s.getEmail().toLowerCase().contains(term)) ||
                             (s.getDepartment() != null && s.getDepartment().toLowerCase().contains(term)) ||
                             (s.getPhone() != null && s.getPhone().contains(term)))
                .collect(Collectors.toList());
    }
}
