package project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ==============================================================================
 * Project: Smart Snake Game
 * Module: ConfirmDialog (Custom dark-themed neon confirm popup)
 * Authors:
 *   - Mohammad Sufiyan Aasim (sufiyanaasim@outlook.com / GitHub: SufiyanAasim)
 *   - Fahad Bin Nasir (fahadabbasi17025@gmail.com / GitHub: FahadBinNasir)
 * ==============================================================================
 */
public class ConfirmDialog extends JDialog {
    private boolean confirmed = false;

    public ConfirmDialog(Window parent, String titleText, String questionText, Color accentColor, boolean showCancel) {
        super(parent, titleText, ModalityType.APPLICATION_MODAL);
        setSize(360, 160);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true); // Custom undecorated layout

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(20, 22, 28));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2), // Custom outline color
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblQuestion = new JLabel("<html><div style='text-align: center; color: #E6EBF5; font-family: Segoe UI; font-size: 13px;'>" + questionText + "</div></html>");
        lblQuestion.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblQuestion, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.setBackground(new Color(20, 22, 28));

        JButton btnYes = new JButton(showCancel ? "Yes" : "OK");
        styleButton(btnYes, accentColor);
        btnYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = true;
                dispose();
            }
        });
        btnPanel.add(btnYes);

        if (showCancel) {
            JButton btnNo = new JButton("No");
            styleButton(btnNo, new Color(110, 115, 125));
            btnNo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    confirmed = false;
                    dispose();
                }
            });
            btnPanel.add(btnNo);
        }

        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void styleButton(JButton btn, Color accent) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(35, 39, 48));
        btn.setBorder(BorderFactory.createLineBorder(accent, 1));
        btn.setPreferredSize(new Dimension(80, 28));
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
