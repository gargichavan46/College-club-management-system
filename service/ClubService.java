package com.collegeclubmanagement.service;

import com.collegeclubmanagement.dao.ClubDAO;
import com.collegeclubmanagement.exception.ValidationException;
import com.collegeclubmanagement.model.Club;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Layer for Club management.
 * Owned by shared backend (Riya).
 */
public class ClubService {

    private final ClubDAO clubDAO;
    private static final ClubService instance = new ClubService();

    public ClubService() {
        this.clubDAO = new ClubDAO();
    }

    public ClubService(ClubDAO clubDAO) {
        this.clubDAO = clubDAO;
    }

    public static ClubService getInstance() {
        return instance;
    }

    public List<Club> getAllClubs() {
        return clubDAO.findAll();
    }

    public Club getClubById(int clubId) {
        return clubDAO.findById(clubId);
    }

    public boolean addClub(Club club) {
        validateClub(club);
        if (club.getStatus() == null || club.getStatus().trim().isEmpty()) {
            club.setStatus("ACTIVE");
        }
        return clubDAO.insert(club);
    }

    public static boolean addClubStatic(Club club) {
        return instance.addClub(club);
    }

    public boolean updateClub(Club club) {
        if (club == null || club.getClubId() <= 0) {
            throw new ValidationException("Invalid club ID for update.");
        }
        validateClub(club);

        Club existing = clubDAO.findById(club.getClubId());
        if (existing != null) {
            if (club.getCapacity() < existing.getCurrentMembers()) {
                throw new ValidationException("Cannot reduce club capacity to " + club.getCapacity() +
                        " because there are currently " + existing.getCurrentMembers() + " active members.");
            }
            // Preserve current members count
            club.setCurrentMembers(existing.getCurrentMembers());
        }

        return clubDAO.update(club);
    }

    public static boolean updateClubStatic(Club club) {
        return instance.updateClub(club);
    }

    public boolean deactivateClub(int clubId) {
        if (clubId <= 0) {
            throw new ValidationException("Invalid club ID.");
        }
        return clubDAO.deactivate(clubId);
    }

    public static boolean deactivateClubStatic(int clubId) {
        return instance.deactivateClub(clubId);
    }

    public int getTotalClubsCount() {
        return clubDAO.countAll();
    }

    public int getActiveClubsCount() {
        return clubDAO.countActive();
    }

    public List<Club> searchClubs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllClubs();
        }
        String term = keyword.trim().toLowerCase();
        return getAllClubs().stream()
                .filter(c -> String.valueOf(c.getClubId()).contains(term) ||
                             (c.getClubName() != null && c.getClubName().toLowerCase().contains(term)) ||
                             (c.getCategory() != null && c.getCategory().toLowerCase().contains(term)) ||
                             (c.getFacultyCoordinator() != null && c.getFacultyCoordinator().toLowerCase().contains(term)) ||
                             (c.getStudentCoordinator() != null && c.getStudentCoordinator().toLowerCase().contains(term)))
                .collect(Collectors.toList());
    }

    public List<Club> filterClubs(String category, String status) {
        List<Club> list = getAllClubs();
        return list.stream()
                .filter(c -> {
                    boolean matchCat = (category == null || category.equalsIgnoreCase("All") || category.equalsIgnoreCase("All Categories") || category.trim().isEmpty() ||
                            (c.getCategory() != null && c.getCategory().equalsIgnoreCase(category)));
                    boolean matchStatus = (status == null || status.equalsIgnoreCase("All") || status.trim().isEmpty() ||
                            (c.getStatus() != null && c.getStatus().equalsIgnoreCase(status)));
                    return matchCat && matchStatus;
                })
                .collect(Collectors.toList());
    }

    private void validateClub(Club club) {
        if (club == null) {
            throw new ValidationException("Club details cannot be null.");
        }
        if (club.getClubName() == null || club.getClubName().trim().isEmpty()) {
            throw new ValidationException("Club Name is required.");
        }
        if (club.getCategory() == null || club.getCategory().trim().isEmpty()) {
            throw new ValidationException("Club Category is required.");
        }
        if (club.getCapacity() <= 0) {
            throw new ValidationException("Capacity must be greater than 0.");
        }
    }
}
