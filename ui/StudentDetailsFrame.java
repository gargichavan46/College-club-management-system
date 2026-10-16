package com.collegeclubmanagement.ui;

import com.collegeclubmanagement.model.ClubMember;
import com.collegeclubmanagement.model.Student;
import com.collegeclubmanagement.service.ClubMemberService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Student Details Screen displaying student information and their enrolled clubs.
 * Part of Admin Management (Charvi).
 * Student ID is strictly read-only and immutable.
 */
public class StudentDetailsFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JFrame parentFrame;
    private final Student student;
    private final ClubMemberService clubMemberService;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(30, 58, 138); // Deep Navy
    private static final Color BG_COLOR = new Color(248, 250, 252);     // Light Slate
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    public StudentDetailsFrame(JFrame parentFrame, Student student, ClubMemberService clubMemberService) {
        this.parentFrame = parentFrame;
        this.student = student;
        this.clubMemberService = (clubMemberService != null) ? clubMemberService : new ClubMemberService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Student Details - " + (student != null ? student.getName() : ""));
        setSize(600, 620);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("Student Profile Details");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("View student information and enrolled clubs");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(226, 232, 240));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(lblSub);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Content: Info Card + Clubs Table
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Info Card
        JPanel infoCard = new JPanel(new GridLayout(3, 2, 16, 12));
        infoCard.setBackground(CARD_BG);
        infoCard.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 18, 14, 18)
        ));

        if (student != null) {
            infoCard.add(createDetailItem("Student ID (Locked)", String.valueOf(student.getStudentId())));
            infoCard.add(createDetailItem("Full Name", student.getName()));
            infoCard.add(createDetailItem("Email Address", student.getEmail()));
            infoCard.add(createDetailItem("Phone Number", student.getPhone()));
            infoCard.add(createDetailItem("Department", student.getDepartment()));
            infoCard.add(createDetailItem("Year of Study", "Year " + student.getYear()));
        }

        centerPanel.add(infoCard);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 2. Enrolled Clubs Sub-Section
        JLabel lblClubsHeading = new JLabel("Enrolled Club Memberships");
        lblClubsHeading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblClubsHeading.setForeground(TEXT_DARK);
        lblClubsHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblClubsHeading);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        String[] subColumns = {"Membership ID", "Club Name", "Join Date", "Status"};
        DefaultTableModel subModel = new DefaultTableModel(subColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        if (student != null) {
            List<ClubMember> memberships = clubMemberService.getMembershipsByStudent(student.getStudentId());
            for (ClubMember cm : memberships) {
                subModel.addRow(new Object[]{
                        cm.getMembershipId(),
                        cm.getClubName(),
                        cm.getJoinDate() != null ? cm.getJoinDate().toString() : "N/A",
                        cm.getStatus()
                });
            }
        }

        JTable subTable = new JTable(subModel);
        subTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTable.setRowHeight(26);
        subTable.setGridColor(BORDER_COLOR);
        subTable.setShowGrid(true);

        JTableHeader subHeader = subTable.getTableHeader();
        subHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subHeader.setBackground(new Color(241, 245, 249));

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        subTable.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        subTable.getColumnModel().getColumn(2).setCellRenderer(centerRender);
        subTable.getColumnModel().getColumn(3).setCellRenderer(centerRender);

        JScrollPane subScroll = new JScrollPane(subTable);
        subScroll.setPreferredSize(new Dimension(540, 140));
        subScroll.setBorder(new LineBorder(BORDER_COLOR, 1));
        subScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(subScroll);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        buttonPanel.setBackground(BG_COLOR);

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setBackground(PRIMARY_COLOR);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 34));
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnClose);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createDetailItem(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblValue = new JLabel(value != null ? value : "N/A");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValue.setForeground(TEXT_DARK);

        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(lblValue);

        return panel;
    }
}
