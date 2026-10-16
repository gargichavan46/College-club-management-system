package com.collegeclubmanagement.service;

import com.collegeclubmanagement.dao.ClubMemberDAO;
import com.collegeclubmanagement.model.ClubMember;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Layer for Club Membership management.
 * Owned by shared backend (Riya).
 */
public class ClubMemberService {

    private final ClubMemberDAO clubMemberDAO;
    private static final ClubMemberService instance = new ClubMemberService();

    public ClubMemberService() {
        this.clubMemberDAO = new ClubMemberDAO();
    }

    public ClubMemberService(ClubMemberDAO clubMemberDAO) {
        this.clubMemberDAO = clubMemberDAO;
    }

    public static ClubMemberService getInstance() {
        return instance;
    }

    public List<ClubMember> getAllMemberships() {
        return clubMemberDAO.findAll();
    }

    public List<ClubMember> getMembershipsByStudent(int studentId) {
        return clubMemberDAO.findByStudentId(studentId);
    }

    public List<ClubMember> getMembershipsByClub(int clubId) {
        return clubMemberDAO.findByClubId(clubId);
    }

    public boolean removeMembership(int membershipId) {
        return clubMemberDAO.deactivateMembership(membershipId);
    }

    public boolean deactivateMembership(int membershipId) {
        return clubMemberDAO.deactivateMembership(membershipId);
    }

    public int getTotalMembershipsCount() {
        return clubMemberDAO.countAll();
    }

    public List<ClubMember> searchMemberships(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMemberships();
        }
        String term = keyword.trim().toLowerCase();
        return getAllMemberships().stream()
                .filter(m -> String.valueOf(m.getMembershipId()).contains(term) ||
                             String.valueOf(m.getStudentId()).contains(term) ||
                             (m.getStudentName() != null && m.getStudentName().toLowerCase().contains(term)) ||
                             String.valueOf(m.getClubId()).contains(term) ||
                             (m.getClubName() != null && m.getClubName().toLowerCase().contains(term)) ||
                             (m.getStatus() != null && m.getStatus().toLowerCase().contains(term)))
                .collect(Collectors.toList());
    }

    public List<ClubMember> filterMemberships(String status) {
        if (status == null || status.equalsIgnoreCase("All") || status.trim().isEmpty()) {
            return getAllMemberships();
        }
        return getAllMemberships().stream()
                .filter(m -> m.getStatus() != null && m.getStatus().equalsIgnoreCase(status.trim()))
                .collect(Collectors.toList());
    }
}
