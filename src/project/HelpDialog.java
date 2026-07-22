package project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ==============================================================================
 * Project: Smart Snake
 * Module: HelpDialog (Custom dark-themed gameplay instructions dialog)
 * Authors:
 *   - Mohammad Sufiyan Aasim (sufiyanaasim@outlook.com / GitHub: SufiyanAasim)
 *   - Fahad Bin Nasir (fahadabbasi17025@gmail.com / GitHub: FahadBinNasir)
 * ==============================================================================
 */
public class HelpDialog extends JDialog {

    public HelpDialog(JFrame parent) {
        super(parent, "How to Play & System Guide", true);
        setSize(520, 440);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true); // Custom borderless dialog card

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(20, 22, 28));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 170, 0), 2), // Neon orange border
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setBackground(new Color(20, 22, 28));

        JLabel title = new JLabel("HOW TO PLAY & SYSTEM GUIDE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(255, 170, 0)); // Neon Orange
        headerPanel.add(title, BorderLayout.WEST);

        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(480, 2));
        divider.setBackground(new Color(255, 170, 0));
        headerPanel.add(divider, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content (Center)
        JEditorPane txtPane = new JEditorPane();
        txtPane.setContentType("text/html");
        txtPane.setEditable(false);
        txtPane.setBackground(new Color(25, 27, 34));
        txtPane.setText("<html><body style='font-family: Segoe UI; color: #D2D7E1; font-size: 11px; margin: 10px;'>"
            + "<h3 style='color: #00E5FF; margin-top: 0;'>🎮 Controls</h3>"
            + "<ul>"
            + "<li><b>UP / DOWN / LEFT / RIGHT</b> Arrow Keys: Change direction of the snake.</li>"
            + "<li><b>SPACE BAR</b>: Start Game (from overlays) or Restart when Game Over.</li>"
            + "<li><b>F11 Key</b>: Toggle Fullscreen borderless display mode.</li>"
            + "</ul>"
            + "<h3 style='color: #00E5FF;'>⚙️ Game Modes & Settings</h3>"
            + "<ul>"
            + "<li><b>Manual (Keyboard)</b>: Control the snake using standard keyboard arrow keys.</li>"
            + "<li><b>A* Pathfinder Autoplay</b>: Snake calculates paths automatically using A* and BFS heuristics.</li>"
            + "<li><b>Q-Learning Autoplay</b>: Uses a reinforcement learning Q-Table to navigate obstacles.</li>"
            + "<li><b>Border wrap (Portal)</b>: Snake wraps around coordinates instead of dying at screen borders.</li>"
            + "</ul>"
            + "<h3 style='color: #00E5FF;'>💡 Tips & Systems</h3>"
            + "<ul>"
            + "<li>Double click or maximize the window to resize, grid aspect ratio is fully preserved.</li>"
            + "<li>Score records are persistently saved to <b>scores.db</b> via SQLite.</li>"
            + "<li>Click <b>Scores</b> in the sidebar to review, search, delete, or export your match history as CSV.</li>"
            + "</ul>"
            + "</body></html>");

        JScrollPane scrollPane = new JScrollPane(txtPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 55, 68), 1));
        scrollPane.getViewport().setBackground(new Color(25, 27, 34));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Close button (Bottom)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(20, 22, 28));

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBackground(new Color(35, 39, 48));
        btnClose.setBorder(BorderFactory.createLineBorder(new Color(255, 170, 0), 1));
        btnClose.setPreferredSize(new Dimension(100, 30));
        btnClose.setFocusable(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnClose.setBackground(new Color(255, 170, 0));
                btnClose.setForeground(new Color(15, 17, 22));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnClose.setBackground(new Color(35, 39, 48));
                btnClose.setForeground(Color.WHITE);
            }
        });

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
}
