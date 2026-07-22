package project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * ==============================================================================
 * Project: Smart Snake
 * Module: NameInputDialog (Custom dark-themed scoreboard prompt dialog)
 * Authors:
 *   - Mohammad Sufiyan Aasim (sufiyanaasim@outlook.com / GitHub: SufiyanAasim)
 *   - Fahad Bin Nasir (fahadabbasi17025@gmail.com / GitHub: FahadBinNasir)
 * ==============================================================================
 */
public class NameInputDialog extends JDialog {
    private final JTextField txtName;
    private String playerName = "Guest";
    private boolean saved = false;

    public NameInputDialog(JFrame parent, int score) {
        super(parent, "Score Record", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true); // Sleek borderless dialog card

        // Main panel with custom border
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(20, 22, 28));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(76, 141, 255), 2), // Neon blue border outline
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Message
        JLabel lblMsg = new JLabel("<html><div style='text-align: center; color: #E6EBF5; font-family: Segoe UI; font-size: 13px;'>"
            + "Game Over! You scored <font color='#39FF14'><b>" + score + "</b></font> points.<br>"
            + "Enter your name for the Scoreboard:</div></html>");
        lblMsg.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblMsg, BorderLayout.NORTH);

        // Input Field
        txtName = new JTextField("Player");
        txtName.setBackground(new Color(30, 32, 40));
        txtName.setForeground(Color.WHITE);
        txtName.setCaretColor(Color.WHITE);
        txtName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtName.setHorizontalAlignment(JTextField.CENTER);
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 55, 68), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        // Auto-select text for easy typing
        txtName.selectAll();
        
        // Save on Enter
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveAndClose();
                }
            }
        });
        mainPanel.add(txtName, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.setBackground(new Color(20, 22, 28));

        JButton btnSave = new JButton("Save Record");
        JButton btnSkip = new JButton("Skip / Guest");

        styleButton(btnSave, new Color(57, 255, 20)); // Neon Green
        styleButton(btnSkip, new Color(110, 115, 125)); // Gray

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAndClose();
            }
        });

        btnSkip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnPanel.add(btnSave);
        btnPanel.add(btnSkip);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void saveAndClose() {
        String input = txtName.getText().trim();
        if (!input.isEmpty()) {
            playerName = input;
        }
        saved = true;
        dispose();
    }

    public String getPlayerName() {
        return playerName;
    }

    private void styleButton(JButton btn, Color accent) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(35, 39, 48));
        btn.setBorder(BorderFactory.createLineBorder(accent, 1));
        btn.setPreferredSize(new Dimension(120, 30));
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(accent);
                btn.setForeground(new Color(15, 17, 22));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(35, 39, 48));
                btn.setForeground(Color.WHITE);
            }
        });
    }
}
