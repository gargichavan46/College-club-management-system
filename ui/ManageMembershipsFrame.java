package com.collegeclubmanagement.ui;

import com.collegeclubmanagement.model.ClubMember;
import com.collegeclubmanagement.service.ClubMemberService;
import com.collegeclubmanagement.service.ClubService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Manage Memberships Screen for viewing and deactivating student-club memberships.
 * Part of Admin Management (Charvi).
 * Students = registered users; Memberships = relationship between students and clubs.
 */
public class ManageMembershipsFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final AdminDashboard parentDashboard;
    private final ClubMemberService clubMemberService;
    private final ClubService clubService;

    private JTable membershipsTable;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cmbStatusFilter;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(30, 58, 138); // Deep Navy
    private static final Color ACCENT_COLOR = new Color(37, 99, 235);  // Royal Blue
    private static final Color BG_COLOR = new Color(248, 250, 252);     // Light Slate
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    private static final String[] COLUMN_NAMES = {
            "Membership ID", "Student ID", "Student Name", "Club ID", "Club Name", "Join Date", "Membership Status"
    };

    public ManageMembershipsFrame() {
        this(null, new ClubMemberService(), new ClubService());
    }

    public ManageMembershipsFrame(AdminDashboard parentDashboard, ClubMemberService clubMemberService, ClubService clubService) {
        this.parentDashboard = parentDashboard;
        this.clubMemberService = (clubMemberService != null) ? clubMemberService : new ClubMemberService();
        this.clubService = (clubService != null) ? clubService : new ClubService();
        initializeUI();
        loadMembershipsData();
    }

    private void initializeUI() {
        setTitle("Manage Club Memberships - College Club Management System");
        setSize(980, 620);
        setMinimumSize(new Dimension(850, 500));
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
        headerPanel.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel lblTitle = new JLabel("Club Membership Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBack.setBackground(new Color(51, 65, 85));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> goBack());

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Toolbar: Search & Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(4, 12, 4, 12)
        ));

        filterPanel.add(new JLabel("Search (Student / Club):"));
        txtSearch = new JTextField(16);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSearch.setBackground(ACCENT_COLOR);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> applyFilter());
        filterPanel.add(btnSearch);

        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Status:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"All", "ACTIVE", "INACTIVE"});
        cmbStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbStatusFilter.addActionListener(e -> applyFilter());
        filterPanel.add(cmbStatusFilter);

        JButton btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReset.setBackground(new Color(241, 245, 249));
        btnReset.setForeground(new Color(15, 23, 42));
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbStatusFilter.setSelectedIndex(0);
            loadMembershipsData();
        });
        filterPanel.add(btnReset);

        mainPanel.add(filterPanel, BorderLayout.CENTER);

        // Table Panel
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(12, 20, 12, 20));
        tableContainer.setBackground(BG_COLOR);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        membershipsTable = new JTable(tableModel);
        membershipsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        membershipsTable.setRowHeight(30);
        membershipsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        membershipsTable.setGridColor(BORDER_COLOR);
        membershipsTable.setShowGrid(true);

        JTableHeader tableHeader = membershipsTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(241, 245, 249));
        tableHeader.setForeground(new Color(15, 23, 42));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        membershipsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Membership ID
        membershipsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Student ID
        membershipsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Club ID
        membershipsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Join Date

        // Custom Renderer for Status column
        membershipsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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

        JScrollPane scrollPane = new JScrollPane(membershipsTable);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottomPanel.setBackground(BG_COLOR);

        JButton btnDeactivate = new JButton("Remove / Deactivate Membership");
        btnDeactivate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeactivate.setBackground(new Color(220, 38, 38));
        btnDeactivate.setForeground(Color.WHITE);
        btnDeactivate.setFocusPainted(false);
        btnDeactivate.addActionListener(e -> handleDeactivateMembership());

        JButton btnView = new JButton("View Details");
        btnView.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnView.setBackground(new Color(241, 245, 249));
        btnView.setForeground(new Color(15, 23, 42));
        btnView.setFocusPainted(false);
        btnView.addActionListener(e -> viewMembershipDetails());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setBackground(new Color(241, 245, 249));
        btnRefresh.setForeground(new Color(15, 23, 42));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadMembershipsData());

        bottomPanel.add(btnDeactivate);
        bottomPanel.add(btnView);
        bottomPanel.add(btnRefresh);

        tableContainer.add(bottomPanel, BorderLayout.SOUTH);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(filterPanel, BorderLayout.NORTH);
        centerWrapper.add(tableContainer, BorderLayout.CENTER);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    /**
     * Loads memberships using ClubMemberService.
     */
    public void loadMembershipsData() {
        try {
            List<ClubMember> memberships = clubMemberService.getAllMemberships();
            populateTable(memberships);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load memberships: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<ClubMember> list) {
        tableModel.setRowCount(0);
        for (ClubMember m : list) {
            tableModel.addRow(new Object[]{
                    m.getMembershipId(),
                    m.getStudentId(),
                    m.getStudentName(),
                    m.getClubId(),
                    m.getClubName(),
                    (m.getJoinDate() != null ? m.getJoinDate().toString() : "N/A"),
                    m.getStatus()
            });
        }
    }

    private void applyFilter() {
        String keyword = txtSearch.getText().trim();
        String selectedStatus = (String) cmbStatusFilter.getSelectedItem();

        try {
            List<ClubMember> filtered;
            if (!keyword.isEmpty()) {
                filtered = clubMemberService.searchMemberships(keyword);
                if (selectedStatus != null && !selectedStatus.equalsIgnoreCase("All")) {
                    filtered.removeIf(m -> !selectedStatus.equalsIgnoreCase(m.getStatus()));
                }
            } else {
                filtered = clubMemberService.filterMemberships(selectedStatus);
            }
            populateTable(filtered);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Filter error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles removal / deactivation of a membership.
     * Uses ClubMemberService.removeMembership(...).
     * Available seats become available again automatically.
     */
    private void handleDeactivateMembership() {
        int selectedRow = membershipsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a membership record to remove/deactivate.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int membershipId = (int) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 2);
        String clubName = (String) tableModel.getValueAt(selectedRow, 4);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);

        if ("INACTIVE".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this,
                    "This membership is already inactive.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // As specified: Confirmation prompt before deactivating
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove/deactivate this membership?\n" +
                "Student: " + studentName + "\n" +
                "Club: " + clubName + "\n" +
                "(The club's seat will be restored automatically).",
                "Confirm Removal / Deactivation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Call ClubMemberService.removeMembership(...)
                boolean success = clubMemberService.removeMembership(membershipId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Membership deactivated successfully. A seat in '" + clubName + "' is now available.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadMembershipsData();
                    if (parentDashboard != null) {
                        parentDashboard.refreshStatistics();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Could not deactivate membership.",
                            "Failure",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deactivating membership: " + ex.getMessage(),
                        "System Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewMembershipDetails() {
        int selectedRow = membershipsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a membership from the table.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int membershipId = (int) tableModel.getValueAt(selectedRow, 0);
        int studentId = (int) tableModel.getValueAt(selectedRow, 1);
        String studentName = (String) tableModel.getValueAt(selectedRow, 2);
        int clubId = (int) tableModel.getValueAt(selectedRow, 3);
        String clubName = (String) tableModel.getValueAt(selectedRow, 4);
        Object joinDate = tableModel.getValueAt(selectedRow, 5);
        String status = (String) tableModel.getValueAt(selectedRow, 6);

        String details = String.format(
                "Membership ID: %d\n" +
                "----------------------------------------\n" +
                "Student ID: %d\n" +
                "Student Name: %s\n" +
                "----------------------------------------\n" +
                "Club ID: %d\n" +
                "Club Name: %s\n" +
                "----------------------------------------\n" +
                "Join Date: %s\n" +
                "Membership Status: %s",
                membershipId, studentId, studentName, clubId, clubName, joinDate, status
        );

        JOptionPane.showMessageDialog(this, details, "Membership Details - #" + membershipId, JOptionPane.INFORMATION_MESSAGE);
    }

    private void goBack() {
        if (parentDashboard != null) {
            parentDashboard.refreshStatistics();
            parentDashboard.setVisible(true);
        }
        this.dispose();
    }
}
