package com.collegeclubmanagement.ui;

import com.collegeclubmanagement.model.Club;
import com.collegeclubmanagement.service.ClubService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Manage Clubs Screen with JTable, search, filter, add, edit, and deactivate capabilities.
 * Part of Admin Management (Charvi).
 */
public class ManageClubsFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final AdminDashboard parentDashboard;
    private final ClubService clubService;

    private JTable clubsTable;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cmbCategoryFilter;
    private JComboBox<String> cmbStatusFilter;

    // Visual theme colors
    private static final Color PRIMARY_COLOR = new Color(30, 58, 138); // Deep Navy
    private static final Color ACCENT_COLOR = new Color(37, 99, 235);  // Royal Blue
    private static final Color DANGER_COLOR = new Color(220, 38, 38);  // Crimson Red
    private static final Color DARK_BTN_BG = new Color(51, 65, 85);    // Slate Dark
    private static final Color LIGHT_BTN_BG = new Color(226, 232, 240);// Light Slate
    private static final Color BG_COLOR = new Color(248, 250, 252);     // Light Slate Background
    private static final Color TEXT_DARK = new Color(15, 23, 42);      // Dark Text
    private static final Color BORDER_COLOR = new Color(203, 213, 225);// Border

    private static final String[] COLUMN_NAMES = {
            "Club ID", "Club Name", "Category", "Capacity", "Current Members", "Available Seats", "Status"
    };

    public ManageClubsFrame() {
        this(null, new ClubService());
    }

    public ManageClubsFrame(AdminDashboard parentDashboard, ClubService clubService) {
        this.parentDashboard = parentDashboard;
        this.clubService = (clubService != null) ? clubService : new ClubService();
        initializeUI();
        loadClubsData();
    }

    private void initializeUI() {
        setTitle("Manage Clubs - College Club Management System");
        setSize(1020, 640);
        setMinimumSize(new Dimension(880, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                goBack();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(14, 22, 14, 22));

        JLabel lblTitle = new JLabel("Club Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        // High contrast Back to Dashboard Button on Navy Header
        JButton btnBack = createStyledButton("← Back to Dashboard", Color.WHITE, PRIMARY_COLOR);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.addActionListener(e -> goBack());

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Filter & Search Toolbar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(4, 14, 4, 14)
        ));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(TEXT_DARK);
        filterPanel.add(lblSearch);

        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(160, 32));
        txtSearch.addActionListener(e -> applyFilters());
        filterPanel.add(txtSearch);

        JButton btnSearch = createStyledButton("Search", ACCENT_COLOR, Color.WHITE);
        btnSearch.addActionListener(e -> applyFilters());
        filterPanel.add(btnSearch);

        filterPanel.add(Box.createHorizontalStrut(15));
        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCat.setForeground(TEXT_DARK);
        filterPanel.add(lblCat);

        cmbCategoryFilter = new JComboBox<>(new String[]{
                "All Categories", "Technical", "Cultural", "Sports", "Literary", "Arts", "Academic", "Social"
        });
        cmbCategoryFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbCategoryFilter.setPreferredSize(new Dimension(130, 30));
        cmbCategoryFilter.addActionListener(e -> applyFilters());
        filterPanel.add(cmbCategoryFilter);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(TEXT_DARK);
        filterPanel.add(lblStatus);

        cmbStatusFilter = new JComboBox<>(new String[]{"All", "ACTIVE", "INACTIVE"});
        cmbStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbStatusFilter.setPreferredSize(new Dimension(95, 30));
        cmbStatusFilter.addActionListener(e -> applyFilters());
        filterPanel.add(cmbStatusFilter);

        JButton btnReset = createStyledButton("Reset", LIGHT_BTN_BG, TEXT_DARK);
        btnReset.addActionListener(e -> resetFilters());
        filterPanel.add(btnReset);

        // Table Container Panel
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(12, 20, 10, 20));
        tableContainer.setBackground(BG_COLOR);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clubsTable = new JTable(tableModel);
        clubsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clubsTable.setRowHeight(30);
        clubsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clubsTable.setGridColor(BORDER_COLOR);
        clubsTable.setShowGrid(true);

        JTableHeader tableHeader = clubsTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(241, 245, 249));
        tableHeader.setForeground(new Color(15, 23, 42));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));

        // Center align numerical columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        clubsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Club ID
        clubsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Capacity
        clubsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Current Members
        clubsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Available Seats

        // Custom Status Renderer (Green for ACTIVE, Red for INACTIVE)
        clubsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected && value != null) {
                    if ("ACTIVE".equalsIgnoreCase(value.toString())) {
                        setForeground(new Color(22, 163, 74));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setForeground(new Color(220, 38, 38));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(clubsTable);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // Action Buttons at Bottom with Rich, Clear Styling
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        actionPanel.setBackground(BG_COLOR);
        actionPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JButton btnAdd = createStyledButton("+ Add Club", ACCENT_COLOR, Color.WHITE);
        btnAdd.addActionListener(e -> openAddClub());

        JButton btnEdit = createStyledButton("Edit Club", DARK_BTN_BG, Color.WHITE);
        btnEdit.addActionListener(e -> openEditClub());

        JButton btnDeactivate = createStyledButton("Deactivate Club", DANGER_COLOR, Color.WHITE);
        btnDeactivate.addActionListener(e -> handleDeactivateClub());

        JButton btnDetails = createStyledButton("View Details", LIGHT_BTN_BG, TEXT_DARK);
        btnDetails.addActionListener(e -> viewClubDetails());

        JButton btnRefresh = createStyledButton("Refresh", LIGHT_BTN_BG, TEXT_DARK);
        btnRefresh.addActionListener(e -> loadClubsData());

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDeactivate);
        actionPanel.add(btnDetails);
        actionPanel.add(btnRefresh);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(filterPanel, BorderLayout.NORTH);
        centerWrapper.add(tableContainer, BorderLayout.CENTER);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Creates a custom high-contrast styled button that renders reliably across all Look & Feels.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setUI(new BasicButtonUI()); // Bypasses native Windows Look and Feel background override
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1, true),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)
        ));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (bgColor.equals(Color.WHITE)) {
                    btn.setBackground(new Color(241, 245, 249));
                } else {
                    btn.setBackground(bgColor.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    public void loadClubsData() {
        try {
            List<Club> clubs = clubService.getAllClubs();
            populateTable(clubs);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load clubs: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Club> clubs) {
        tableModel.setRowCount(0);
        for (Club c : clubs) {
            tableModel.addRow(new Object[]{
                    c.getClubId(),
                    c.getClubName(),
                    c.getCategory(),
                    c.getCapacity(),
                    c.getCurrentMembers(),
                    c.getAvailableSeats(),
                    c.getStatus()
            });
        }
    }

    private void applyFilters() {
        String keyword = txtSearch.getText().trim();
        String selectedCategory = (String) cmbCategoryFilter.getSelectedItem();
        String selectedStatus = (String) cmbStatusFilter.getSelectedItem();

        try {
            List<Club> filtered;
            if (!keyword.isEmpty()) {
                filtered = clubService.searchClubs(keyword);
                if (selectedCategory != null && !selectedCategory.startsWith("All")) {
                    filtered.removeIf(c -> !selectedCategory.equalsIgnoreCase(c.getCategory()));
                }
                if (selectedStatus != null && !selectedStatus.equalsIgnoreCase("All")) {
                    filtered.removeIf(c -> !selectedStatus.equalsIgnoreCase(c.getStatus()));
                }
            } else {
                filtered = clubService.filterClubs(selectedCategory, selectedStatus);
            }
            populateTable(filtered);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Filter error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFilters() {
        txtSearch.setText("");
        cmbCategoryFilter.setSelectedIndex(0);
        cmbStatusFilter.setSelectedIndex(0);
        loadClubsData();
    }

    private void openAddClub() {
        AddClubFrame addFrame = new AddClubFrame(this, clubService);
        addFrame.setVisible(true);
    }

    private void openEditClub() {
        int selectedRow = clubsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a club from the table to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clubId = (int) tableModel.getValueAt(selectedRow, 0);
        Club club = clubService.getClubById(clubId);
        if (club == null) {
            JOptionPane.showMessageDialog(this, "Club not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EditClubFrame editFrame = new EditClubFrame(this, clubService, club);
        editFrame.setVisible(true);
    }

    private void handleDeactivateClub() {
        int selectedRow = clubsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a club to deactivate.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clubId = (int) tableModel.getValueAt(selectedRow, 0);
        String clubName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);

        if ("INACTIVE".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this,
                    "The club '" + clubName + "' is already inactive.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to deactivate this club?\n(" + clubName + " - ID: " + clubId + ")",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = clubService.deactivateClub(clubId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Club '" + clubName + "' has been successfully deactivated.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadClubsData();
                    if (parentDashboard != null) {
                        parentDashboard.refreshStatistics();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Could not deactivate club.",
                            "Failure",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error during deactivation: " + ex.getMessage(),
                        "System Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewClubDetails() {
        int selectedRow = clubsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a club to view its details.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clubId = (int) tableModel.getValueAt(selectedRow, 0);
        Club c = clubService.getClubById(clubId);
        if (c == null) return;

        String details = String.format(
                "Club ID: %d\n" +
                "Club Name: %s\n" +
                "Category: %s\n" +
                "Faculty Coordinator: %s\n" +
                "Student Coordinator: %s\n" +
                "Total Capacity: %d\n" +
                "Current Members: %d\n" +
                "Available Seats: %d\n" +
                "Status: %s\n\n" +
                "Description:\n%s",
                c.getClubId(),
                c.getClubName(),
                c.getCategory(),
                (c.getFacultyCoordinator() != null ? c.getFacultyCoordinator() : "N/A"),
                (c.getStudentCoordinator() != null ? c.getStudentCoordinator() : "N/A"),
                c.getCapacity(),
                c.getCurrentMembers(),
                c.getAvailableSeats(),
                c.getStatus(),
                (c.getDescription() != null ? c.getDescription() : "No description provided.")
        );

        JOptionPane.showMessageDialog(this, details, "Club Details - " + c.getClubName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void goBack() {
        if (parentDashboard != null) {
            parentDashboard.refreshStatistics();
            parentDashboard.setVisible(true);
        }
        this.dispose();
    }
}
