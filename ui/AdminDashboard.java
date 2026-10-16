package com.collegeclubmanagement.ui;

import com.collegeclubmanagement.model.Admin;
import com.collegeclubmanagement.service.ClubMemberService;
import com.collegeclubmanagement.service.ClubService;
import com.collegeclubmanagement.service.StudentService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Admin Dashboard Screen for College Club Management System.
 * Part of Admin Management (Charvi).
 */
public class AdminDashboard extends JFrame {

    private static final long serialVersionUID = 1L;

    private Admin currentAdmin;

    // Service dependencies from Riya's backend
    private final ClubService clubService;
    private final StudentService studentService;
    private final ClubMemberService clubMemberService;

    // KPI Statistic Labels
    private JLabel lblTotalClubsVal;
    private JLabel lblActiveClubsVal;
    private JLabel lblTotalStudentsVal;
    private JLabel lblTotalMembershipsVal;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(30, 58, 138); // Deep Navy
    private static final Color BG_COLOR = new Color(248, 250, 252);     // Light Slate
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    public AdminDashboard() {
        this(new Admin(1, "admin", "admin123", "System Administrator", "admin@college.edu"));
    }

    public AdminDashboard(Admin admin) {
        this.currentAdmin = admin;
        this.clubService = new ClubService();
        this.studentService = new StudentService();
        this.clubMemberService = new ClubMemberService();
        initializeUI();
        refreshStatistics();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard - College Club Management System");
        setSize(900, 620);
        setMinimumSize(new Dimension(800, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleLogout();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(16, 25, 16, 25));

        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.setOpaque(false);

        JLabel lblTitle = new JLabel("College Club Management System");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        String adminName = (currentAdmin != null && currentAdmin.getFullName() != null)
                ? currentAdmin.getFullName() : "Administrator";
        JLabel lblAdminName = new JLabel("Logged in as: " + adminName + " (Admin)");
        lblAdminName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblAdminName.setForeground(new Color(203, 213, 225));

        headerLeft.add(lblTitle);
        headerLeft.add(Box.createRigidArea(new Dimension(0, 4)));
        headerLeft.add(lblAdminName);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        // High contrast buttons on top navy header
        JButton btnRefresh = createHeaderButton("↻ Refresh Stats", new Color(241, 245, 249), PRIMARY_COLOR);
        btnRefresh.addActionListener(e -> refreshStatistics());

        JButton btnLogout = createHeaderButton("Logout", new Color(220, 38, 38), Color.WHITE);
        btnLogout.addActionListener(e -> handleLogout());

        headerRight.add(btnRefresh);
        headerRight.add(btnLogout);

        headerPanel.add(headerLeft, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Content: Stats Cards + Management Buttons
        JPanel centerContent = new JPanel(new BorderLayout(0, 20));
        centerContent.setOpaque(false);
        centerContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Stats Section
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);

        lblTotalClubsVal = new JLabel("0", SwingConstants.CENTER);
        lblActiveClubsVal = new JLabel("0", SwingConstants.CENTER);
        lblTotalStudentsVal = new JLabel("0", SwingConstants.CENTER);
        lblTotalMembershipsVal = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(createStatCard("Total Clubs", lblTotalClubsVal, new Color(37, 99, 235)));
        statsPanel.add(createStatCard("Active Clubs", lblActiveClubsVal, new Color(22, 163, 74)));
        statsPanel.add(createStatCard("Total Students", lblTotalStudentsVal, new Color(147, 51, 234)));
        statsPanel.add(createStatCard("Memberships", lblTotalMembershipsVal, new Color(234, 88, 12)));

        centerContent.add(statsPanel, BorderLayout.NORTH);

        // Navigation Actions Section
        JPanel actionsSection = new JPanel(new BorderLayout());
        actionsSection.setOpaque(false);

        JLabel lblSection = new JLabel("Administrative Management");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblSection.setForeground(TEXT_DARK);
        lblSection.setBorder(new EmptyBorder(0, 0, 15, 0));
        actionsSection.add(lblSection, BorderLayout.NORTH);

        JPanel navGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        navGrid.setOpaque(false);

        JButton btnClubs = createNavButton("Manage Clubs", "Add, edit, deactivate & filter clubs", e -> openManageClubs());
        JButton btnStudents = createNavButton("Manage Students", "View registered students & profiles", e -> openManageStudents());
        JButton btnMemberships = createNavButton("Manage Memberships", "Search, filter & deactivate memberships", e -> openManageMemberships());

        navGrid.add(btnClubs);
        navGrid.add(btnStudents);
        navGrid.add(btnMemberships);

        actionsSection.add(navGrid, BorderLayout.CENTER);
        centerContent.add(actionsSection, BorderLayout.CENTER);

        mainPanel.add(centerContent, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JButton createHeaderButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setUI(new BasicButtonUI());
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLabel.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JButton createNavButton(String title, String description, java.awt.event.ActionListener listener) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(CARD_BG);
        button.setUI(new BasicButtonUI());
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(25, 20, 25, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY_COLOR);

        JLabel lblDesc = new JLabel("<html>" + description + "</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(TEXT_MUTED);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(lblTitle);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(lblDesc);

        button.add(content, BorderLayout.CENTER);
        button.addActionListener(listener);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(241, 245, 249));
                lblTitle.setForeground(new Color(37, 99, 235));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(CARD_BG);
                lblTitle.setForeground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    public void refreshStatistics() {
        try {
            int totalClubs = clubService.getTotalClubsCount();
            int activeClubs = clubService.getActiveClubsCount();
            int totalStudents = studentService.getTotalStudentsCount();
            int totalMemberships = clubMemberService.getTotalMembershipsCount();

            lblTotalClubsVal.setText(String.valueOf(totalClubs));
            lblActiveClubsVal.setText(String.valueOf(activeClubs));
            lblTotalStudentsVal.setText(String.valueOf(totalStudents));
            lblTotalMembershipsVal.setText(String.valueOf(totalMemberships));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to refresh statistics: " + e.getMessage(),
                    "Backend Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openManageClubs() {
        ManageClubsFrame frame = new ManageClubsFrame(this, clubService);
        frame.setVisible(true);
        this.setVisible(false);
    }

    private void openManageStudents() {
        ManageStudentsFrame frame = new ManageStudentsFrame(this, studentService, clubMemberService);
        frame.setVisible(true);
        this.setVisible(false);
    }

    private void openManageMemberships() {
        ManageMembershipsFrame frame = new ManageMembershipsFrame(this, clubMemberService, clubService);
        frame.setVisible(true);
        this.setVisible(false);
    }

    public void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of the admin panel?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.currentAdmin = null;
            SwingUtilities.invokeLater(() -> {
                AdminLoginFrame loginFrame = new AdminLoginFrame();
                loginFrame.setVisible(true);
            });
            this.dispose();
        }
    }
}
