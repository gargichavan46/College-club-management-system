package com.collegeclubmanagement.model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Model class representing a Club Membership (relationship between Student and Club).
 * Owned by shared backend (Riya), used by Admin GUI (Charvi) and Student GUI (Gargi).
 */
public class ClubMember implements Serializable {
    private static final long serialVersionUID = 1L;

    private int membershipId;
    private int studentId;
    private String studentName;
    private int clubId;
    private String clubName;
    private Date joinDate;
    private String status; // "ACTIVE" or "INACTIVE"

    public ClubMember() {
        this.status = "ACTIVE";
    }

    public ClubMember(int membershipId, int studentId, String studentName, int clubId,
                      String clubName, Date joinDate, String status) {
        this.membershipId = membershipId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.clubId = clubId;
        this.clubName = clubName;
        this.joinDate = joinDate;
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "ACTIVE";
    }

    public ClubMember(int studentId, String studentName, int clubId, String clubName,
                      Date joinDate, String status) {
        this(0, studentId, studentName, clubId, clubName, joinDate, status);
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClubMember [membershipId=" + membershipId + ", studentId=" + studentId +
                ", studentName=" + studentName + ", clubId=" + clubId + ", clubName=" + clubName +
                ", joinDate=" + joinDate + ", status=" + status + "]";
    }
}
