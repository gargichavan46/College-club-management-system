package com.collegeclubmanagement.ui;

import com.collegeclubmanagement.model.Club;
import com.collegeclubmanagement.service.ClubService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Edit Club Screen for College Club Management System.
 * Part of Admin Management (Charvi).
 */
public class EditClubFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ManageClubsFrame parentFrame;
    private final ClubService clubService;
    private final Club existingClub;

    private JTextField txtClubId;
    private JTextField txtClubName;
    private JTextArea txtDescription;
    private JComboBox<String> cmbCategory;
    private JTextField txtFacultyCoord;
    private JTextField txtStudentCoord;
    private JTextField txtCapacity;
    private JLabel lblCurrentMembers;
    private JComboBox<String> cmbStatus;

    private JButton btnSave;
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

    public EditClubFrame(ManageClubsFrame parentFrame, ClubService clubService, Club club) {
        this.parentFrame = parentFrame;
        this.clubService = (clubService != null) ? clubService : new ClubService();
        this.existingClub = club;
        initializeUI();
        populateFields();
    }

    private void initializeUI() {
        setTitle("Edit Club - College Club Management System");
        setSize(540, 680);
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

        JLabel lblTitle = new JLabel("Edit Club Information");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Update club properties (Club ID cannot be changed)");
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

        // 1. Club ID (Disabled / Read-only - MUST NOT BE CHANGED)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formCard.add(createFieldLabel("Club ID (Locked)"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtClubId = new JTextField(18);
        txtClubId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtClubId.setEditable(false);
        txtClubId.setBackground(new Color(241, 245, 249));
        txtClubId.setToolTipText("Club ID is immutable and cannot be changed.");
        formCard.add(txtClubId, gbc);

        // 2. Club Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        formCard.add(createFieldLabel("Club Name *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtClubName = new JTextField(18);
        txtClubName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(txtClubName, gbc);

        // 3. Category
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        formCard.add(createFieldLabel("Category *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        cmbCategory = new JComboBox<>(CATEGORIES);
        cmbCategory.setEditable(true);
        cmbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(cmbCategory, gbc);

        // 4. Description
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35;
        formCard.add(createFieldLabel("Description"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtDescription = new JTextArea(3, 18);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setBorder(new LineBorder(BORDER_COLOR, 1));
        formCard.add(descScroll, gbc);

        // 5. Faculty Coordinator
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.35;
        formCard.add(createFieldLabel("Faculty Coordinator"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtFacultyCoord = new JTextField(18);
        txtFacultyCoord.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(txtFacultyCoord, gbc);

        // 6. Student Coordinator
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.35;
        formCard.add(createFieldLabel("Student Coordinator"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtStudentCoord = new JTextField(18);
        txtStudentCoord.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(txtStudentCoord, gbc);

        // 7. Capacity
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.35;
        formCard.add(createFieldLabel("Capacity *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtCapacity = new JTextField(18);
        txtCapacity.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(txtCapacity, gbc);

        // 8. Current Members Info (Read-only reference)
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.35;
        formCard.add(createFieldLabel("Current Active Members"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        lblCurrentMembers = new JLabel("0");
        lblCurrentMembers.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCurrentMembers.setForeground(new Color(71, 85, 105));
        formCard.add(lblCurrentMembers, gbc);

        // 9. Status
        gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0.35;
        formCard.add(createFieldLabel("Status *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formCard.add(cmbStatus, gbc);

        mainPanel.add(formCard, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 15));
        buttonPanel.setBackground(BG_COLOR);

        btnSave = new JButton("Save Changes");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(ACCENT_COLOR);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setPreferredSize(new Dimension(130, 35));
        btnSave.addActionListener(this::handleUpdateClub);

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setBackground(new Color(241, 245, 249));
        btnCancel.setForeground(new Color(185, 28, 28));
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(90, 35));
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
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

    private void populateFields() {
        if (existingClub != null) {
            txtClubId.setText(String.valueOf(existingClub.getClubId()));
            txtClubName.setText(existingClub.getClubName());
            txtDescription.setText(existingClub.getDescription() != null ? existingClub.getDescription() : "");
            cmbCategory.setSelectedItem(existingClub.getCategory());
            txtFacultyCoord.setText(existingClub.getFacultyCoordinator() != null ? existingClub.getFacultyCoordinator() : "");
            txtStudentCoord.setText(existingClub.getStudentCoordinator() != null ? existingClub.getStudentCoordinator() : "");
            txtCapacity.setText(String.valueOf(existingClub.getCapacity()));
            lblCurrentMembers.setText(String.valueOf(existingClub.getCurrentMembers()));
            cmbStatus.setSelectedItem(existingClub.getStatus());
        }
    }

    private void handleUpdateClub(ActionEvent e) {
        String name = txtClubName.getText().trim();
        String description = txtDescription.getText().trim();
        Object catObj = cmbCategory.getSelectedItem();
        String category = (catObj != null) ? catObj.toString().trim() : "";
        String faculty = txtFacultyCoord.getText().trim();
        String student = txtStudentCoord.getText().trim();
        String capacityStr = txtCapacity.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();

        // 1. Validation: Name required
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Club Name cannot be empty.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtClubName.requestFocus();
            return;
        }

        // 2. Validation: Category required
        if (category.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Category cannot be empty.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            cmbCategory.requestFocus();
            return;
        }

        // 3. Validation: Capacity numeric and > 0
        int newCapacity;
        try {
            newCapacity = Integer.parseInt(capacityStr);
            if (newCapacity <= 0) {
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

        // 4. Critical Business Rule: If reducing capacity below current membership count is not allowed, display error!
        int currentMembers = (existingClub != null) ? existingClub.getCurrentMembers() : 0;
        if (newCapacity < currentMembers) {
            JOptionPane.showMessageDialog(this,
                    "Cannot reduce club capacity to " + newCapacity +
                    " because there are currently " + currentMembers + " active members enrolled.\n" +
                    "Capacity must be at least " + currentMembers + ".",
                    "Capacity Reduction Error",
                    JOptionPane.ERROR_MESSAGE);
            txtCapacity.requestFocus();
            return;
        }

        // Update Club Object
        existingClub.setClubName(name);
        existingClub.setDescription(description);
        existingClub.setCategory(category);
        existingClub.setFacultyCoordinator(faculty);
        existingClub.setStudentCoordinator(student);
        existingClub.setCapacity(newCapacity);
        existingClub.setStatus(status);

        try {
            // Call ClubService.updateClub(...)
            boolean success = clubService.updateClub(existingClub);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Club '" + name + "' updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                if (parentFrame != null) {
                    parentFrame.loadClubsData();
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update club in the database.",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating club: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
