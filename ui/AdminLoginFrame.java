package com.collegeclubmanagement.ui;

import com.collegeclubmanagement.model.Admin;
import com.collegeclubmanagement.service.AdminService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Admin Login Screen for College Club Management System.
 * Part of Admin Management (Charvi).
 */
public class AdminLoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnClear;
    private JButton btnExit;

    private final AdminService adminService;

    // Visual theme colors
    private static final Color PRIMARY_COLOR = new Color(30, 58, 138); // Deep Navy
    private static final Color ACCENT_COLOR = new Color(37, 99, 235);  // Royal Blue
    private static final Color BG_COLOR = new Color(248, 250, 252);     // Light Slate
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(15, 23, 42);

    public AdminLoginFrame() {
        this(new AdminService());
    }

    public AdminLoginFrame(AdminService adminService) {
        this.adminService = adminService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("College Club Management System - Admin Login");
        setSize(460, 480);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblTitle = new JLabel("Admin Portal");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("College Club Management System");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(226, 232, 240));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        headerPanel.add(lblSubtitle);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Form Card
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(new EmptyBorder(25, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label & Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(TEXT_DARK);
        cardPanel.add(lblUser, gbc);

        gbc.gridy = 1;
        txtUsername = new JTextField(18);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(280, 35));
        cardPanel.add(txtUsername, gbc);

        // Password Label & Field
        gbc.gridy = 2;
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(TEXT_DARK);
        cardPanel.add(lblPass, gbc);

        gbc.gridy = 3;
        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(280, 35));
        cardPanel.add(txtPassword, gbc);

        // Enter key in password field triggers login
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Bottom Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(BG_COLOR);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(ACCENT_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(100, 35));
        btnLogin.addActionListener(this::handleLoginAction);

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(TEXT_DARK);
        btnClear.setFocusPainted(false);
        btnClear.setPreferredSize(new Dimension(90, 35));
        btnClear.addActionListener(e -> clearFields());

        btnExit = new JButton("Exit");
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnExit.setBackground(new Color(241, 245, 249));
        btnExit.setForeground(new Color(185, 28, 28));
        btnExit.setFocusPainted(false);
        btnExit.setPreferredSize(new Dimension(90, 35));
        btnExit.addActionListener(e -> handleExitAction());

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnExit);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void handleLoginAction(ActionEvent e) {
        performLogin();
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your admin username.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your admin password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        try {
            // Admin authentication checked strictly via backend/database
            Admin admin = adminService.login(username, password);

            if (admin != null) {
                JOptionPane.showMessageDialog(this,
                        "Login successful! Welcome, " + admin.getFullName() + ".",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Open AdminDashboard and dispose login frame
                SwingUtilities.invokeLater(() -> {
                    AdminDashboard dashboard = new AdminDashboard(admin);
                    dashboard.setVisible(true);
                });
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid admin credentials! Please check your username and password.",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Login error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }

    private void handleExitAction() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the application?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        // Set System Look and Feel for native, clean UI rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            AdminLoginFrame frame = new AdminLoginFrame();
            frame.setVisible(true);
        });
    }
}
