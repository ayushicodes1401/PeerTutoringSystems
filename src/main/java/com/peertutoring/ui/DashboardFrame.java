package com.peertutoring.ui;

import com.peertutoring.model.Student;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private final Student student;
    private static final Color PRIMARY = new Color(48,88,166);
    public DashboardFrame(Student student) {
        if(student==null) throw new IllegalArgumentException("Authenticated student is required.");
        this.student=student; setTitle("PeerTutoringSystems | Dashboard"); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setSize(900,600); setMinimumSize(new Dimension(760,520)); setLocationRelativeTo(null);
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(new Color(245,247,251));
        JPanel top=new JPanel(new BorderLayout()); top.setBackground(Color.WHITE); top.setBorder(new EmptyBorder(18,28,18,28)); JLabel brand=new JLabel("PeerTutoringSystems"); brand.setFont(new Font("SansSerif",Font.BOLD,21)); brand.setForeground(PRIMARY); JButton logout=new JButton("Log out"); logout.addActionListener(e->{ int choice=JOptionPane.showConfirmDialog(this,"Are you sure you want to log out?","Log out",JOptionPane.YES_NO_OPTION); if(choice==JOptionPane.YES_OPTION){new LoginFrame().setVisible(true);dispose();} }); top.add(brand,BorderLayout.WEST); top.add(logout,BorderLayout.EAST);
        JPanel content=new JPanel(new BorderLayout(0,22)); content.setOpaque(false); content.setBorder(new EmptyBorder(30,35,35,35));
        JLabel welcome=new JLabel("Welcome, "+student.getFullName()+"!"); welcome.setFont(new Font("SansSerif",Font.BOLD,28)); JLabel sub=new JLabel("What would you like to do today?"); sub.setForeground(new Color(105,116,135)); JPanel intro=new JPanel(); intro.setOpaque(false); intro.setLayout(new BoxLayout(intro,BoxLayout.Y_AXIS)); intro.add(welcome); intro.add(Box.createVerticalStrut(8)); intro.add(sub); content.add(intro,BorderLayout.NORTH);
        JPanel cards=new JPanel(new GridLayout(2,2,18,18)); cards.setOpaque(false); cards.add(makeCard("Find a tutor","Explore subjects and connect with student tutors.","Search Tutors",()->new TutorSearchFrame(student).setVisible(true))); cards.add(makeCard("My sessions","Review requests, track status, and manage sessions.","View Sessions",()->new SessionRequestsFrame(student).setVisible(true))); cards.add(makeCard("Offer tutoring","Share subjects you can help other students learn.","Manage Subjects",()->new OfferTutoringFrame(student).setVisible(true))); cards.add(makeCard("My account","Signed in as: "+student.getEmail()+"\nSemester: "+student.getSemester(),"Account Details",()->JOptionPane.showMessageDialog(this,"Name: "+student.getFullName()+"\nEmail: "+student.getEmail()+"\nSemester: "+student.getSemester(),"My account",JOptionPane.INFORMATION_MESSAGE)));
        content.add(cards,BorderLayout.CENTER); root.add(top,BorderLayout.NORTH); root.add(content,BorderLayout.CENTER); setContentPane(root);
    }
    private JPanel makeCard(String title,String description,String action,Runnable callback){ JPanel p=new JPanel(); p.setBackground(Color.WHITE); p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225,230,239)),new EmptyBorder(18,18,18,18))); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); JLabel h=new JLabel(title); h.setFont(new Font("SansSerif",Font.BOLD,18)); JLabel d=new JLabel("<html><div style='width:260px'>"+description.replace("\n","<br>")+"</div></html>"); d.setForeground(new Color(105,116,135)); d.setBorder(new EmptyBorder(10,0,18,0)); JButton b=new JButton(action); b.setBackground(PRIMARY); b.setForeground(Color.WHITE); b.setFocusPainted(false); b.addActionListener(e->callback.run()); p.add(h);p.add(d);p.add(b);return p; }
}
