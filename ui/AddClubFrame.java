package com.collegeclubmanagement.ui;

import com.collegeclubmanagement.model.Club;
import com.collegeclubmanagement.service.ClubService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Add Club Form Screen for College Club Management System.
 * Part of Admin Management (Charvi).
 */
public class AddClubFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ManageClubsFrame parentFrame;
    private final ClubService clubService;

    private JTextField txtClubName;
    private JTextArea txtDescription;
    private JComboBox<String> cmbCategory;
    private JTextField txtFacultyCoord;
    private JTextField txtStudentCoord;
    private JTextField txtCapacity;
    private JComboBox<String> cmbStatus;

    private JButton btnAdd;
    private JButton btnClear;
    private JButton btnCancel;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(30, 58, 138); // Deep Navy
    private static final Color ACCENT_COLOR = new Color(37, 99, 235);  // Royal Blue
    private static final Color BG_COLOR = new Color(248, 250, 252);     // Light Slate
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    private static final String[] CATEGORIES = {
            "Technical", "Cultural", "Sports", "Literary", "Arts", "Academic", "Social"
    };

    public AddClubFrame(ManageClubsFrame parentFrame, ClubService clubService) {
        this.parentFrame = parentFrame;
        this.clubService = (clubService != null) ? clubService : new ClubService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Add New Club - College Club Management System");
        setSize(520, 640);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("Register New Club");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Fill in the club details below");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(226, 232, 240));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(lblSub);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Card in Center
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(CARD_BG);
        formCard.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 1. Club Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formCard.add(createFieldLabel("Club Name *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtClubName = new JTextField(18);
        txtClubName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(txtClubName, gbc);

        // 2. Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formCard.add(createFieldLabel("Category *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbCategory = new JComboBox<>(CATEGORIES);
        cmbCategory.setEditable(true);
        cmbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(cmbCategory, gbc);

        // 3. Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formCard.add(createFieldLabel("Description"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDescription = new JTextArea(3, 18);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setBorder(new LineBorder(BORDER_COLOR, 1));
        formCard.add(descScroll, gbc);

        // 4. Faculty Coordinator
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formCard.add(createFieldLabel("Faculty Coordinator"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtFacultyCoord = new JTextField(18);
        txtFacultyCoord.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(txtFacultyCoord, gbc);

        // 5. Student Coordinator
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formCard.add(createFieldLabel("Student Coordinator"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtStudentCoord = new JTextField(18);
        txtStudentCoord.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(txtStudentCoord, gbc);

        // 6. Capacity
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        formCard.add(createFieldLabel("Capacity *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtCapacity = new JTextField(18);
        txtCapacity.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(txtCapacity, gbc);

        // 7. Status
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        formCard.add(createFieldLabel("Status *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(cmbStatus, gbc);

        mainPanel.add(formCard, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 15));
        buttonPanel.setBackground(BG_COLOR);

        btnAdd = new JButton("Add Club");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(ACCENT_COLOR);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setPreferredSize(new Dimension(110, 35));
        btnAdd.addActionListener(this::handleAddClub);

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(TEXT_DARK);
        btnClear.setFocusPainted(false);
        btnClear.setPreferredSize(new Dimension(90, 35));
        btnClear.addActionListener(e -> clearForm());

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setBackground(new Color(241, 245, 249));
        btnCancel.setForeground(new Color(185, 28, 28));
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(90, 35));
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnCancel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private void handleAddClub(ActionEvent e) {
        String name = txtClubName.getText().trim();
        String description = txtDescription.getText().trim();
        Object catObj = cmbCategory.getSelectedItem();
        String category = (catObj != null) ? catObj.toString().trim() : "";
        String faculty = txtFacultyCoord.getText().trim();
        String student = txtStudentCoord.getText().trim();
        String capacityStr = txtCapacity.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();

        // 1. Validation: Club name required
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Club Name is required and cannot be empty.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtClubName.requestFocus();
            return;
        }

        // 2. Validation: Category required
        if (category.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Category is required and cannot be empty.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            cmbCategory.requestFocus();
            return;
        }

        // 3. Validation: Capacity must be numeric and > 0
        if (capacityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Capacity is required and cannot be empty.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtCapacity.requestFocus();
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Capacity must be greater than 0.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                txtCapacity.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Capacity must be a valid numeric integer.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtCapacity.requestFocus();
            return;
        }

        // Create Club Model
        Club newClub = new Club(name, description, category, faculty, student, capacity, status);

        try {
            // Call ClubService.addClub(...)
            boolean success = clubService.addClub(newClub);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Club '" + name + "' added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Return / refresh Manage Clubs
                if (parentFrame != null) {
                    parentFrame.loadClubsData();
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add club. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding club: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtClubName.setText("");
        txtDescription.setText("");
        cmbCategory.setSelectedIndex(0);
        txtFacultyCoord.setText("");
        txtStudentCoord.setText("");
        txtCapacity.setText("");
        cmbStatus.setSelectedIndex(0);
        txtClubName.requestFocus();
    }
}
