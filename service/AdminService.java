package com.collegeclubmanagement.service;

import com.collegeclubmanagement.dao.AdminDAO;
import com.collegeclubmanagement.exception.ValidationException;
import com.collegeclubmanagement.model.Admin;

/**
 * Service Layer for Admin Authentication and operations.
 * Owned by shared backend (Riya).
 */
public class AdminService {

    private final AdminDAO adminDAO;
    private static final AdminService instance = new AdminService();

    public AdminService() {
        this.adminDAO = new AdminDAO();
    }

    public AdminService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    public static AdminService getInstance() {
        return instance;
    }

    /**
     * Authenticates the admin user.
     * Static convenience method matching AdminService.loginAdmin(...)
     */
    public static Admin loginAdmin(String username, String password) {
        return instance.login(username, password);
    }

    /**
     * Instance authentication method.
     */
    public Admin login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty.");
        }

        return adminDAO.authenticate(username.trim(), password);
    }

    public boolean authenticate(String username, String password) {
        return login(username, password) != null;
    }

    public Admin getAdminByUsername(String username) {
        return adminDAO.findByUsername(username);
    }
}
