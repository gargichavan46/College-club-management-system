package com.collegeclubmanagement.ui;

import com.collegeclubmanagement.model.Student;
import com.collegeclubmanagement.service.ClubMemberService;
import com.collegeclubmanagement.service.StudentService;

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
 * Manage Students Screen for Admin to view registered students.
 * Part of Admin Management (Charvi).
 * Does NOT duplicate Gargi's registration/login system.
 */
public class ManageStudentsFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final AdminDashboard parentDashboard;
    private final StudentService studentService;
    private final ClubMemberService clubMemberService;

    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(30, 58, 138); // Deep Navy
    private static final Color ACCENT_COLOR = new Color(37, 99, 235);  // Royal Blue
    private static final Color BG_COLOR = new Color(248, 250, 252);     // Light Slate
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    private static final String[] COLUMN_NAMES = {
            "Student ID", "Name", "Email", "Phone", "Department", "Year"
    };

    public ManageStudentsFrame() {
        this(null, new StudentService(), new ClubMemberService());
    }

    public ManageStudentsFrame(AdminDashboard parentDashboard, StudentService studentService, ClubMemberService clubMemberService) {
        this.parentDashboard = parentDashboard;
        this.studentService = (studentService != null) ? studentService : new StudentService();
        this.clubMemberService = (clubMemberService != null) ? clubMemberService : new ClubMemberService();
        initializeUI();
        loadStudentsData();
    }

    private void initializeUI() {
        setTitle("Manage Registered Students - College Club Management System");
        setSize(920, 600);
        setMinimumSize(new Dimension(800, 480));
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

        JLabel lblTitle = new JLabel("Registered Students Directory");
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

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(4, 12, 4, 12)
        ));

        searchPanel.add(new JLabel("Search Students:"));
        txtSearch = new JTextField(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSearch.setBackground(ACCENT_COLOR);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> handleSearch());
        searchPanel.add(btnSearch);

        JButton btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReset.setBackground(new Color(241, 245, 249));
        btnReset.setForeground(new Color(15, 23, 42));
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadStudentsData();
        });
        searchPanel.add(btnReset);

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

        studentsTable = new JTable(tableModel);
        studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentsTable.setRowHeight(30);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.setGridColor(BORDER_COLOR);
        studentsTable.setShowGrid(true);

        JTableHeader tableHeader = studentsTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(241, 245, 249));
        tableHeader.setForeground(new Color(15, 23, 42));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        studentsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Student ID
        studentsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Year

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottomPanel.setBackground(BG_COLOR);

        JButton btnViewDetails = new JButton("View Student Details");
        btnViewDetails.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnViewDetails.setBackground(ACCENT_COLOR);
        btnViewDetails.setForeground(Color.WHITE);
        btnViewDetails.setFocusPainted(false);
        btnViewDetails.addActionListener(e -> openStudentDetails());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setBackground(new Color(241, 245, 249));
        btnRefresh.setForeground(new Color(15, 23, 42));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadStudentsData());

        bottomPanel.add(btnViewDetails);
        bottomPanel.add(btnRefresh);

        tableContainer.add(bottomPanel, BorderLayout.SOUTH);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(searchPanel, BorderLayout.NORTH);
        centerWrapper.add(tableContainer, BorderLayout.CENTER);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    /**
     * Loads all students using StudentService.getAllStudents().
     */
    public void loadStudentsData() {
        try {
            List<Student> students = studentService.getAllStudents();
            populateTable(students);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load students: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getName(),
                    s.getEmail(),
                    s.getPhone(),
                    s.getDepartment(),
                    s.getYear() + (s.getYear() == 1 ? "st Year" : (s.getYear() == 2 ? "nd Year" : (s.getYear() == 3 ? "rd Year" : "th Year")))
            });
        }
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        try {
            List<Student> results = studentService.searchStudents(keyword);
            populateTable(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Search error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openStudentDetails() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student from the table to view their details.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        Student student = studentService.getStudentById(studentId);

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StudentDetailsFrame detailsFrame = new StudentDetailsFrame(this, student, clubMemberService);
        detailsFrame.setVisible(true);
    }

    private void goBack() {
        if (parentDashboard != null) {
            parentDashboard.refreshStatistics();
            parentDashboard.setVisible(true);
        }
        this.dispose();
    }
}
