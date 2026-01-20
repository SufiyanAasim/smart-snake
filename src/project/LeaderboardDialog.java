package project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class LeaderboardDialog extends JDialog {
    private final DatabaseManager dbManager;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField txtSearch;

    public LeaderboardDialog(JFrame parent, DatabaseManager dbManager) {
        super(parent, "Recent Game History & Leaderboard", true);
        this.dbManager = dbManager;

        setSize(780, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(new Color(20, 22, 28));

        // Root container panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(20, 22, 28));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header Panel (Title + Search Field)
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(20, 22, 28));

        JLabel title = new JLabel("Recent Game History & Leaderboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(0, 229, 255)); // Neon blue cyan
        headerPanel.add(title, BorderLayout.NORTH);

        // Divider
        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(750, 2));
        divider.setBackground(new Color(76, 141, 255));
        headerPanel.add(divider, BorderLayout.CENTER);

        // Search container
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchContainer.setBackground(new Color(20, 22, 28));
        
        JLabel lblSearch = new JLabel("Filter by Player Name: ");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSearch.setForeground(new Color(170, 180, 195));
        
        txtSearch = new JTextField(20);
        txtSearch.setBackground(new Color(30, 32, 40));
        txtSearch.setForeground(Color.WHITE);
        txtSearch.setCaretColor(Color.WHITE);
        txtSearch.setBorder(BorderFactory.createLineBorder(new Color(50, 55, 68), 1));
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refreshTableData();
            }
        });

        searchContainer.add(lblSearch);
        searchContainer.add(txtSearch);
        headerPanel.add(searchContainer, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table Panel (Center)
        String[] columns = {"ID", "Date Played", "Player Name", "Score", "High Score", "Game Mode", "Steps"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setBackground(new Color(25, 27, 34));
        table.setForeground(new Color(210, 215, 225));
        table.setGridColor(new Color(40, 44, 55));
        table.setRowHeight(25);
        table.setSelectionBackground(new Color(76, 141, 255, 100));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setBackground(new Color(35, 39, 48));
        table.getTableHeader().setForeground(new Color(170, 180, 195));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(new Color(20, 22, 28));
        scrollPane.getViewport().setBackground(new Color(20, 22, 28));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 55, 68), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Action Panel (Bottom Buttons)
        JPanel actionPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        actionPanel.setBackground(new Color(20, 22, 28));
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnDelete = new JButton("Delete Selected");
        JButton btnClear = new JButton("Clear All Scores");
        JButton btnExport = new JButton("Export CSV");
        JButton btnClose = new JButton("Close");

        styleButton(btnDelete, new Color(255, 59, 48));
        styleButton(btnClear, new Color(255, 59, 48));
        styleButton(btnExport, new Color(0, 229, 255));
        styleButton(btnClose, new Color(110, 115, 125));

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(LeaderboardDialog.this, "Please select a score record to delete.", "Select Record", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(LeaderboardDialog.this, "Delete this score record?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) tableModel.getValueAt(row, 0);
                    dbManager.deleteScore(id);
                    refreshTableData();
                }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(LeaderboardDialog.this, "Clear all scores history?", "Wipe Scoreboard", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    dbManager.clearAllScores();
                    refreshTableData();
                }
            }
        });

        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String outPath = "scores_history.csv";
                if (dbManager.exportToCSV(outPath)) {
                    JOptionPane.showMessageDialog(LeaderboardDialog.this, "Exported successfully to " + outPath, "Export CSV", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(LeaderboardDialog.this, "Failed to export CSV.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        actionPanel.add(btnDelete);
        actionPanel.add(btnClear);
        actionPanel.add(btnExport);
        actionPanel.add(btnClose);

        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel);
        refreshTableData();
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        String searchVal = txtSearch.getText();
        List<DatabaseManager.ScoreRecord> list = dbManager.fetchRecentScores(searchVal);
        for (DatabaseManager.ScoreRecord r : list) {
            tableModel.addRow(new Object[]{
                r.id(),
                r.datePlayed(),
                r.playerName(),
                r.score(),
                r.highScore(),
                r.mode(),
                r.movesCount()
            });
        }
    }

    private void styleButton(JButton btn, Color border) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(25, 27, 34));
        btn.setBorder(BorderFactory.createLineBorder(border, 1));
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
