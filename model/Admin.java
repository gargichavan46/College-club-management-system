package com.collegeclubmanagement.model;

import java.io.Serializable;

/**
 * Model class representing an Admin user.
 * Owned by shared backend (Riya), used by Admin GUI (Charvi).
 */
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    private int adminId;
    private String username;
    private String password;
    private String fullName;
    private String email;

    public Admin() {
    }

    public Admin(int adminId, String username, String password, String fullName, String email) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }

    public Admin(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Admin [adminId=" + adminId + ", username=" + username + ", fullName=" + fullName + ", email=" + email + "]";
    }
}
