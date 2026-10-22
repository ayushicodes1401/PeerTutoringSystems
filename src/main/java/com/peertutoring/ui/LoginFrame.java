package com.peertutoring.ui;

import com.peertutoring.model.Student;
import com.peertutoring.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

/** Login screen for the Student Peer Tutoring and Learning Support System. */
public class LoginFrame extends JFrame {

    private static final Color BACKGROUND = new Color(245, 247, 251);
    private static final Color PRIMARY = new Color(48, 88, 166);
    private static final Color TEXT = new Color(35, 45, 65);
    private static final Color MUTED = new Color(105, 116, 135);

    private final AuthService authService;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginFrame() {
        authService = new AuthService();
        emailField = new JTextField(22);
        passwordField = new JPasswordField(22);
        loginButton = new JButton("Sign In");

        setTitle("PeerTutoringSystems | Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 560);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);

        buildUI();
        getRootPane().setDefaultButton(loginButton);
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(BACKGROUND);

        JPanel brandingPanel = new JPanel();
        brandingPanel.setLayout(new BoxLayout(brandingPanel, BoxLayout.Y_AXIS));
        brandingPanel.setBackground(PRIMARY);
        brandingPanel.setBorder(new EmptyBorder(55, 35, 45, 30));

        JLabel brandTitle = new JLabel(
                "<html><div style='width:250px'>Peer Tutoring<br>&amp; Learning</div></html>");
        brandTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandDescription = new JLabel(
                "<html><div style='width:260px; line-height:1.6'>"
                        + "Learn together. Share knowledge. Grow together "
                        + "with your fellow students.</div></html>");
        brandDescription.setFont(new Font("SansSerif", Font.PLAIN, 15));
        brandDescription.setForeground(new Color(225, 234, 250));
        brandDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandingPanel.add(Box.createVerticalGlue());
        brandingPanel.add(brandTitle);
        brandingPanel.add(Box.createVerticalStrut(20));
        brandingPanel.add(brandDescription);
        brandingPanel.add(Box.createVerticalGlue());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(35, 35, 35, 35));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Welcome back");
        heading.setFont(new Font("SansSerif", Font.BOLD, 27));
        heading.setForeground(TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your student account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        styleTextField(emailField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        styleTextField(passwordField);

        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(PRIMARY);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.addActionListener(e -> handleLogin());

        JLabel registerPrompt = new JLabel(
                "<html>New here? <font color='#3058A6'><b>Create an account</b></font></html>");
        registerPrompt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        registerPrompt.setForeground(MUTED);
        registerPrompt.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerPrompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerPrompt.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openRegistration();
            }
        });

        form.add(heading);
        form.add(Box.createVerticalStrut(8));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(30));
        form.add(createLabel("Email address"));
        form.add(Box.createVerticalStrut(8));
        form.add(emailField);
        form.add(Box.createVerticalStrut(18));
        form.add(createLabel("Password"));
        form.add(Box.createVerticalStrut(8));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(26));
        form.add(loginButton);
        form.add(Box.createVerticalStrut(22));
        form.add(registerPrompt);

        formPanel.add(form);
        root.add(brandingPanel);
        root.add(formPanel);
        setContentPane(root);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(new Color(250, 251, 253));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 222, 233)),
                new EmptyBorder(8, 10, 8, 10)));
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        char[] passwordChars = passwordField.getPassword();

        if (email.isEmpty() || passwordChars.length == 0) {
            Arrays.fill(passwordChars, '\0');
            JOptionPane.showMessageDialog(this,
                    "Please enter both your email and password.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String password = new String(passwordChars);
        Arrays.fill(passwordChars, '\0');
        loginButton.setEnabled(false);

        try {
            Optional<Student> result = authService.login(email, password);
            if (result.isPresent()) {
                Student student = result.get();
                passwordField.setText("");
                DashboardFrame dashboard = new DashboardFrame(student);
                dashboard.setVisible(true);
                dispose();
            } else {
                passwordField.setText("");
                JOptionPane.showMessageDialog(this,
                        "The email or password is incorrect. Please try again.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "We couldn't connect to the database. Please try again later.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Login Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            password = null;
            loginButton.setEnabled(true);
        }
    }

    private void openRegistration() {
        RegisterFrame registerFrame = new RegisterFrame();
        registerFrame.setLocationRelativeTo(this);
        registerFrame.setVisible(true);
        dispose();
    }
}
