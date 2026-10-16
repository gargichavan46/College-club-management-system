package com.collegeclubmanagement.model;

import java.io.Serializable;

/**
 * Model class representing a Student.
 * Owned by shared backend (Riya), used by Admin GUI (Charvi) and Student GUI (Gargi).
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private int studentId;
    private String name;
    private String email;
    private String phone;
    private String department;
    private int year;

    public Student() {
    }

    public Student(int studentId, String name, String email, String phone, String department, int year) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.year = year;
    }

    public Student(String name, String email, String phone, String department, int year) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.year = year;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Student [studentId=" + studentId + ", name=" + name + ", email=" + email +
                ", phone=" + phone + ", department=" + department + ", year=" + year + "]";
    }
}
