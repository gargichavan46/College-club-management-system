package com.collegeclubmanagement.model;

import java.io.Serializable;

/**
 * Model class representing a College Club.
 * Owned by shared backend (Riya), used by Admin GUI (Charvi) and Student GUI (Gargi).
 */
public class Club implements Serializable {
    private static final long serialVersionUID = 1L;

    private int clubId;
    private String clubName;
    private String description;
    private String category;
    private String facultyCoordinator;
    private String studentCoordinator;
    private int capacity;
    private int currentMembers;
    private String status; // "ACTIVE" or "INACTIVE"

    public Club() {
        this.status = "ACTIVE";
    }

    public Club(int clubId, String clubName, String description, String category,
                String facultyCoordinator, String studentCoordinator, int capacity,
                int currentMembers, String status) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.description = description;
        this.category = category;
        this.facultyCoordinator = facultyCoordinator;
        this.studentCoordinator = studentCoordinator;
        this.capacity = capacity;
        this.currentMembers = currentMembers;
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "ACTIVE";
    }

    public Club(String clubName, String description, String category,
                String facultyCoordinator, String studentCoordinator, int capacity,
                String status) {
        this(0, clubName, description, category, facultyCoordinator, studentCoordinator, capacity, 0, status);
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFacultyCoordinator() {
        return facultyCoordinator;
    }

    public void setFacultyCoordinator(String facultyCoordinator) {
        this.facultyCoordinator = facultyCoordinator;
    }

    public String getStudentCoordinator() {
        return studentCoordinator;
    }

    public void setStudentCoordinator(String studentCoordinator) {
        this.studentCoordinator = studentCoordinator;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentMembers() {
        return currentMembers;
    }

    public void setCurrentMembers(int currentMembers) {
        this.currentMembers = currentMembers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Calculates the remaining available seats in this club.
     * @return remaining capacity
     */
    public int getAvailableSeats() {
        return Math.max(0, capacity - currentMembers);
    }

    @Override
    public String toString() {
        return "Club [clubId=" + clubId + ", clubName=" + clubName + ", category=" + category +
                ", capacity=" + capacity + ", currentMembers=" + currentMembers +
                ", availableSeats=" + getAvailableSeats() + ", status=" + status + "]";
    }
}
