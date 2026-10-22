package com.peertutoring.ui;

import com.peertutoring.model.Student;
import com.peertutoring.service.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {
    private final AuthService authService = new AuthService();
    private final JTextField nameField = new JTextField(22);
    private final JTextField emailField = new JTextField(22);
    private final JComboBox<Integer> semesterBox = new JComboBox<>();
    private final JPasswordField passwordField = new JPasswordField(22);
    private final JPasswordField confirmField = new JPasswordField(22);
    private final JButton registerButton = new JButton("Create Account");

    public RegisterFrame() {
        setTitle("PeerTutoringSystems | Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 620); setMinimumSize(new Dimension(480, 580)); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(0, 18)); root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(28, 42, 28, 42));
        JLabel title = new JLabel("Create your account"); title.setFont(new Font("SansSerif", Font.BOLD, 26));
        JLabel subtitle = new JLabel("Join your campus learning community"); subtitle.setForeground(new Color(105,116,135));
        JPanel heading = new JPanel(); heading.setOpaque(false); heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS)); heading.add(title); heading.add(Box.createVerticalStrut(6)); heading.add(subtitle);
        JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false); GridBagConstraints c = new GridBagConstraints(); c.gridx=0; c.gridy=0; c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL; c.insets=new Insets(5,0,5,0);
        for (int i=1;i<=12;i++) semesterBox.addItem(i);
        addField(form,c,"Full name",nameField); addField(form,c,"Email address",emailField); addField(form,c,"Semester",semesterBox); addField(form,c,"Password (minimum 8 characters)",passwordField); addField(form,c,"Confirm password",confirmField);
        registerButton.setBackground(new Color(48,88,166)); registerButton.setForeground(Color.WHITE); registerButton.setFocusPainted(false); registerButton.setFont(new Font("SansSerif",Font.BOLD,14));
        JPanel bottom = new JPanel(); bottom.setOpaque(false); bottom.setLayout(new BoxLayout(bottom,BoxLayout.Y_AXIS)); registerButton.setAlignmentX(Component.LEFT_ALIGNMENT); registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE,42)); bottom.add(registerButton); bottom.add(Box.createVerticalStrut(12));
        JButton back = new JButton("Back to login"); back.setAlignmentX(Component.LEFT_ALIGNMENT); back.setBorderPainted(false); back.setContentAreaFilled(false); back.setForeground(new Color(48,88,166)); bottom.add(back);
        root.add(heading,BorderLayout.NORTH); root.add(form,BorderLayout.CENTER); root.add(bottom,BorderLayout.SOUTH); setContentPane(root);
        registerButton.addActionListener(e -> register()); back.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); }); getRootPane().setDefaultButton(registerButton);
    }
    private void addField(JPanel panel, GridBagConstraints c, String label, JComponent field) {
        JLabel l=new JLabel(label); l.setFont(new Font("SansSerif",Font.BOLD,13)); c.gridy++; panel.add(l,c); c.gridy++; field.setFont(new Font("SansSerif",Font.PLAIN,14)); field.setPreferredSize(new Dimension(300,36)); panel.add(field,c);
    }
    private void register() {
        String name=nameField.getText().trim(), email=emailField.getText().trim(); char[] p=passwordField.getPassword(), cp=confirmField.getPassword();
        if(name.isEmpty()||email.isEmpty()||p.length==0||cp.length==0){ JOptionPane.showMessageDialog(this,"Please complete all fields.","Missing information",JOptionPane.WARNING_MESSAGE); return; }
        if(p.length<8){ JOptionPane.showMessageDialog(this,"Password must contain at least 8 characters.","Invalid password",JOptionPane.WARNING_MESSAGE); return; }
        String password=new String(p), confirm=new String(cp); java.util.Arrays.fill(p,'\0'); java.util.Arrays.fill(cp,'\0'); registerButton.setEnabled(false);
        try { Student student=authService.register(name,email,(Integer)semesterBox.getSelectedItem(),password,confirm);
            JOptionPane.showMessageDialog(this,"Account created successfully. Please sign in.","Registration successful",JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true); dispose();
        } catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Could not register",JOptionPane.WARNING_MESSAGE); }
          catch (SQLException ex) { JOptionPane.showMessageDialog(this,"We couldn't save your account. Check the database connection and try again.","Database error",JOptionPane.ERROR_MESSAGE); }
        finally { passwordField.setText(""); confirmField.setText(""); registerButton.setEnabled(true); }
    }
}
