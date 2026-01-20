package project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Project extends JFrame {
    private static final int GRID_WIDTH = 800;
    private static final int GRID_HEIGHT = 600;
    private static final int CELL_SIZE = 20;

    private final GameModel model;
    private final GameView view;
    private final GameController controller;

    // HUD Sidebar UI Labels
    private JLabel lblScoreVal;
    private JLabel lblHighScoreVal;
    private JLabel lblStepsVal;
    private JLabel lblPathLenVal;
    private JLabel lblEfficiencyVal;
    private JLabel lblStateVal;

    public Project() {
        super("Smart Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 1. Initialize MVC
        model = new GameModel(GRID_WIDTH, GRID_HEIGHT, CELL_SIZE);
        view = new GameView(model);
        controller = new GameController(model, view);

        // 2. Main Double-Pane Layout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(15, 17, 22));

        // Add Game Play View to Center
        mainContainer.add(view, BorderLayout.CENTER);

        // Add Dashboard Sidebar to East
        JPanel sidebar = createSidebar();
        mainContainer.add(sidebar, BorderLayout.EAST);

        setContentPane(mainContainer);
        pack();
        setLocationRelativeTo(null);

        // 3. Setup Keyboard Controls
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                controller.handleKeyPress(e.getKeyCode());
            }
        });
        setFocusable(true);
        requestFocusInWindow();

        // 4. Hook HUD updates
        controller.setHUDCallback(new GameController.HUDUpdateCallback() {
            private final DatabaseManager dbManager = new DatabaseManager();

            @Override
            public void updateHUD(int score, int highScore, int steps, int pathLength, String efficiency, String stateVector) {
                lblScoreVal.setText(String.valueOf(score));
                lblHighScoreVal.setText(String.valueOf(highScore));
                lblStepsVal.setText(String.valueOf(steps));
                lblPathLenVal.setText(pathLength > 0 ? String.valueOf(pathLength) : "N/A");
                lblEfficiencyVal.setText(efficiency);
                lblStateVal.setText(stateVector);
            }

            @Override
            public void onGameOver() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String name = JOptionPane.showInputDialog(Project.this, 
                            "Game Over! Enter your name for the Scoreboard:", 
                            "Score Record", 
                            JOptionPane.PLAIN_MESSAGE);
                        if (name == null) name = "Guest";
                        if (name.trim().isEmpty()) name = "Guest";
                        
                        dbManager.recordScore(name, model.getScore(), model.getHighScore(), model.getAIMode(), model.getMovesCount());
                        
                        LeaderboardDialog dialog = new LeaderboardDialog(Project.this, dbManager);
                        dialog.setVisible(true);
                    }
                });
            }
        });
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, GRID_HEIGHT));
        panel.setBackground(new Color(25, 27, 34)); // Dark premium gray
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new MatteBorder(0, 2, 0, 0, new Color(40, 44, 55))); // Left border separator

        // Title Header
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(20, 22, 28));
        titlePanel.setMaximumSize(new Dimension(300, 70));
        titlePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("SYNTH SERPENT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(0, 229, 255)); // Neon blue cyan
        title.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(title, BorderLayout.CENTER);
        panel.add(titlePanel);

        // Add padding spacing
        panel.add(Box.createVerticalStrut(10));

        // --- SECTION 1: MODE CONTROLS ---
        JPanel sectionModes = createSectionContainer("CONTROLLER MODE");
        
        JRadioButton rbManual = new JRadioButton("Manual (Keyboard)", true);
        JRadioButton rbAStar = new JRadioButton("A* Pathfinder Autoplay");
        JRadioButton rbQLearn = new JRadioButton("Q-Learning Autoplay");

        ButtonGroup group = new ButtonGroup();
        group.add(rbManual);
        group.add(rbAStar);
        group.add(rbQLearn);

        styleRadioButton(rbManual);
        styleRadioButton(rbAStar);
        styleRadioButton(rbQLearn);

        ActionListener modeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbManual.isSelected()) model.setAIMode("Manual");
                else if (rbAStar.isSelected()) model.setAIMode("A*");
                else if (rbQLearn.isSelected()) model.setAIMode("Q-Learning");
                controller.updateHUD();
                view.repaint();
                requestFocusInWindow();
            }
        };

        rbManual.addActionListener(modeListener);
        rbAStar.addActionListener(modeListener);
        rbQLearn.addActionListener(modeListener);

        sectionModes.add(rbManual);
        sectionModes.add(Box.createVerticalStrut(5));
        sectionModes.add(rbAStar);
        sectionModes.add(Box.createVerticalStrut(5));
        sectionModes.add(rbQLearn);
        
        panel.add(sectionModes);
        panel.add(Box.createVerticalStrut(10));

        // --- SECTION 2: SPEED & OPTIONS ---
        JPanel sectionOptions = createSectionContainer("SPEED & SETTINGS");
        
        // Speed slider
        JLabel lblSpeed = createConfigLabel("Simulation Speed:");
        JSlider sliderSpeed = new JSlider(1, 5, 2); // default 2
        sliderSpeed.setBackground(new Color(25, 27, 34));
        sliderSpeed.setPaintTicks(true);
        sliderSpeed.setMajorTickSpacing(1);
        sliderSpeed.setSnapToTicks(true);
        sliderSpeed.setFocusable(false);
        sliderSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                controller.setSpeed(sliderSpeed.getValue());
                requestFocusInWindow();
            }
        });
        
        // Epsilon slider (QLearning exploration parameter)
        JLabel lblEpsilon = createConfigLabel("Q-Agent Epsilon (Exploration):");
        JSlider sliderEpsilon = new JSlider(0, 100, 10); // maps 0.0 to 1.0 (default 0.1)
        sliderEpsilon.setBackground(new Color(25, 27, 34));
        sliderEpsilon.setFocusable(false);
        sliderEpsilon.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                controller.getQLearningAgent().setEpsilon(sliderEpsilon.getValue() / 100f);
                requestFocusInWindow();
            }
        });

        // Path visualized checkbox
        JCheckBox cbShowPath = new JCheckBox("Visualize A* Trajectory Path", true);
        cbShowPath.setBackground(new Color(25, 27, 34));
        cbShowPath.setForeground(new Color(170, 180, 195));
        cbShowPath.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbShowPath.setFocusable(false);
        cbShowPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setPathVisualized(cbShowPath.isSelected());
                view.repaint();
                requestFocusInWindow();
            }
        });

        sectionOptions.add(lblSpeed);
        sectionOptions.add(sliderSpeed);
        sectionOptions.add(Box.createVerticalStrut(8));
        sectionOptions.add(lblEpsilon);
        sectionOptions.add(sliderEpsilon);
        sectionOptions.add(Box.createVerticalStrut(10));
        sectionOptions.add(cbShowPath);

        panel.add(sectionOptions);
        panel.add(Box.createVerticalStrut(10));

        // --- SECTION 3: STATISTICS HUD ---
        JPanel sectionHUD = createSectionContainer("LIVE METRICS");
        sectionHUD.setLayout(new GridLayout(6, 2, 5, 5));

        lblScoreVal = createHUDValueLabel("0");
        lblHighScoreVal = createHUDValueLabel("0");
        lblStepsVal = createHUDValueLabel("0");
        lblPathLenVal = createHUDValueLabel("N/A");
        lblEfficiencyVal = createHUDValueLabel("N/A");
        lblStateVal = createHUDValueLabel("N/A");

        sectionHUD.add(createHUDNameLabel("Score:"));
        sectionHUD.add(lblScoreVal);
        sectionHUD.add(createHUDNameLabel("High Score:"));
        sectionHUD.add(lblHighScoreVal);
        sectionHUD.add(createHUDNameLabel("Total Steps:"));
        sectionHUD.add(lblStepsVal);
        sectionHUD.add(createHUDNameLabel("A* Path Length:"));
        sectionHUD.add(lblPathLenVal);
        sectionHUD.add(createHUDNameLabel("Path Efficiency:"));
        sectionHUD.add(lblEfficiencyVal);
        sectionHUD.add(createHUDNameLabel("Agent State:"));
        sectionHUD.add(lblStateVal);

        panel.add(sectionHUD);
        panel.add(Box.createVerticalStrut(15));

        // --- SECTION 4: ACTIONS BUTTONS ---
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        btnPanel.setBackground(new Color(25, 27, 34));
        btnPanel.setMaximumSize(new Dimension(290, 40));

        JButton btnPause = new JButton("Pause");
        JButton btnReset = new JButton("Reset");
        JButton btnBoard = new JButton("Scores");
        
        styleButton(btnPause, new Color(76, 141, 255)); // cyan blue
        styleButton(btnReset, new Color(255, 59, 48)); // red
        styleButton(btnBoard, new Color(0, 229, 255)); // neon blue

        btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.pauseGame();
                requestFocusInWindow();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.resetGame();
                requestFocusInWindow();
            }
        });

        btnBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LeaderboardDialog dialog = new LeaderboardDialog(Project.this, new DatabaseManager());
                dialog.setVisible(true);
                requestFocusInWindow();
            }
        });

        btnPanel.add(btnPause);
        btnPanel.add(btnReset);
        btnPanel.add(btnBoard);
        panel.add(btnPanel);
        
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // Styling Helpers
    private JPanel createSectionContainer(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(25, 27, 34));
        section.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(50, 55, 68), 1),
            title,
            0,
            2,
            new Font("Segoe UI", Font.BOLD, 11),
            new Color(110, 115, 125)
        ));
        section.setMaximumSize(new Dimension(280, 190));
        return section;
    }

    private void styleRadioButton(JRadioButton rb) {
        rb.setBackground(new Color(25, 27, 34));
        rb.setForeground(new Color(200, 205, 215));
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rb.setFocusable(false);
    }

    private void styleButton(JButton btn, Color accent) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(35, 39, 48));
        btn.setBorder(BorderFactory.createLineBorder(accent, 1));
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JLabel createConfigLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(150, 160, 175));
        return label;
    }

    private JLabel createHUDNameLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(130, 135, 150));
        return label;
    }

    private JLabel createHUDValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(57, 255, 20)); // Neon green values
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Project p = new Project();
                p.setVisible(true);
            }
        });
    }
}
