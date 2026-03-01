package project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * ==============================================================================
 * Project: Smart Snake Game
 * Module: Credits Dialog
 * Authors:
 *   - Mohammad Sufiyan Aasim (sufiyanaasim@outlook.com)
 *   - Fahad Bin Nasir (fahadabbasi17025@gmail.com)
 * ==============================================================================
 */
public class CreditsDialog extends JDialog {

    public CreditsDialog(JFrame parent) {
        super(parent, "Project Credits & Contributors", true);
        setSize(560, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(new Color(20, 22, 28));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(20, 22, 28));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setBackground(new Color(20, 22, 28));

        JLabel title = new JLabel("Smart Snake Game Credits");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(0, 229, 255)); // Neon Cyan
        headerPanel.add(title, BorderLayout.NORTH);

        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(530, 2));
        divider.setBackground(new Color(76, 141, 255));
        headerPanel.add(divider, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Developers Panel (Center)
        JPanel devPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        devPanel.setBackground(new Color(20, 22, 28));

        // Developer 1 Card
        JPanel card1 = createDeveloperCard(
            "Mohammad Sufiyan Aasim",
            "Lead MVC Architect",
            "sufiyanaasim@outlook.com",
            "https://github.com/SufiyanAasim"
        );

        // Developer 2 Card
        JPanel card2 = createDeveloperCard(
            "Fahad Bin Nasir",
            "AI & SQL Specialist",
            "fahadabbasi17025@gmail.com",
            "https://github.com/FahadBinNasir"
        );

        devPanel.add(card1);
        devPanel.add(card2);
        mainPanel.add(devPanel, BorderLayout.CENTER);

        // Bottom Action Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(20, 22, 28));

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBackground(new Color(25, 27, 34));
        btnClose.setBorder(BorderFactory.createLineBorder(new Color(110, 115, 125), 1));
        btnClose.setPreferredSize(new Dimension(100, 30));
        btnClose.setFocusable(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createDeveloperCard(String name, String role, String email, String githubUrl) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(25, 27, 34));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 55, 68), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(230, 235, 245));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRole = new JLabel(role);
        lblRole.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblRole.setForeground(new Color(76, 141, 255)); // Neon blue role
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblEmail = new JLabel(email);
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEmail.setForeground(new Color(150, 160, 175));
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblName);
        card.add(Box.createVerticalStrut(5));
        card.add(lblRole);
        card.add(Box.createVerticalStrut(10));
        card.add(lblEmail);
        card.add(Box.createVerticalStrut(15));

        JButton btnGitHub = new JButton("View GitHub");
        btnGitHub.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnGitHub.setForeground(Color.WHITE);
        btnGitHub.setBackground(new Color(35, 39, 48));
        btnGitHub.setBorder(BorderFactory.createLineBorder(new Color(57, 255, 20), 1)); // Neon green border
        btnGitHub.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGitHub.setFocusable(false);
        btnGitHub.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGitHub.setMaximumSize(new Dimension(130, 30));
        
        btnGitHub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(githubUrl));
                } catch (Exception ex) {
                    System.err.println("Could not open GitHub URL: " + ex.getMessage());
                }
            }
        });

        card.add(btnGitHub);
        return card;
    }
}
