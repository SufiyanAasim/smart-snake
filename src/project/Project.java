/**
 * ==============================================================================
 * Project: Smart Snake
 * Module: Project (Main Window & Sidebar HUD Dashboard)
 * Authors:
 *   - Mohammad Sufiyan Aasim (sufiyanaasim@outlook.com / GitHub: SufiyanAasim)
 *   - Fahad Bin Nasir (fahadabbasi17025@gmail.com / GitHub: FahadBinNasir)
 * ==============================================================================
 */
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
    private JButton btnPause;

    public Project() {
        super("Smart Snake");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });
        setResizable(true); // Enable window resizing

        // Load window icon
        try {
            java.io.File iconFile = new java.io.File("assets/logo.png");
            if (iconFile.exists()) {
                setIconImage(new ImageIcon(iconFile.getAbsolutePath()).getImage());
            }
        } catch (Exception e) {
            System.err.println("Could not set window icon: " + e.getMessage());
        }

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

        // 3. Setup Keyboard Controls via Key Bindings (Global capture inside focused window)
        setupKeyBindings();
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
                
                if (btnPause != null) {
                    btnPause.setText(model.isPaused() ? "Resume" : "Pause");
                }
            }

            @Override
            public void onGameOver() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        NameInputDialog nameDialog = new NameInputDialog(Project.this, model.getScore());
                        nameDialog.setVisible(true);
                        String name = nameDialog.getPlayerName();
                        
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
        titlePanel.setPreferredSize(new Dimension(300, 50));
        titlePanel.setMinimumSize(new Dimension(300, 50));
        titlePanel.setMaximumSize(new Dimension(300, 50));
        titlePanel.setBorder(new EmptyBorder(12, 10, 12, 10));
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("SMART SNAKE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(0, 229, 255)); // Neon blue cyan
        title.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(title, BorderLayout.CENTER);
        panel.add(titlePanel);

        // Add padding spacing
        panel.add(Box.createVerticalStrut(10));

        // --- SECTION 1: MODE CONTROLS ---
        JPanel sectionModes = createSectionContainer("CONTROLLER MODE", 120);
        
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
        rbManual.setForeground(new Color(57, 255, 20)); // Neon green active selection

        ActionListener modeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbManual.isSelected()) {
                    model.setAIMode("Manual");
                    model.getCurrentPath().clear();
                } else if (rbAStar.isSelected()) {
                    model.setAIMode("A*");
                } else if (rbQLearn.isSelected()) {
                    model.setAIMode("Q-Learning");
                    model.getCurrentPath().clear();
                }
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
        JPanel sectionOptions = createSectionContainer("SPEED & SETTINGS", 385);
        
        // Speed slider
        JLabel lblSpeed = createConfigLabel("Simulation Speed: 2x");
        JSlider sliderSpeed = new JSlider(1, 5, 2); // default 2
        sliderSpeed.setBackground(new Color(25, 27, 34));
        sliderSpeed.setPaintTicks(true);
        sliderSpeed.setMajorTickSpacing(1);
        sliderSpeed.setSnapToTicks(true);
        sliderSpeed.setFocusable(false);
        sliderSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lblSpeed.setText("Simulation Speed: " + sliderSpeed.getValue() + "x");
                controller.setSpeed(sliderSpeed.getValue());
                requestFocusInWindow();
            }
        });
        
        // Epsilon slider (QLearning exploration parameter)
        JLabel lblEpsilon = createConfigLabel("Q-Agent Epsilon (Exploration): 0.10");
        JSlider sliderEpsilon = new JSlider(0, 100, 10); // maps 0.0 to 1.0 (default 0.1)
        sliderEpsilon.setBackground(new Color(25, 27, 34));
        sliderEpsilon.setFocusable(false);
        sliderEpsilon.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lblEpsilon.setText("Q-Agent Epsilon (Exploration): " + String.format("%.2f", sliderEpsilon.getValue() / 100f));
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

        // Border physics selection
        JLabel lblBorderMode = createConfigLabel("Border Physics Mode:");
        String[] borderModes = { "Solid Borders (Death)", "Wrap Borders (Portal)" };
        JComboBox<String> comboBorder = new JComboBox<>(borderModes);
        comboBorder.setBackground(new Color(35, 39, 48));
        comboBorder.setForeground(Color.WHITE);
        comboBorder.setFont(new Font("Segoe UI", Font.BOLD, 11));
        comboBorder.setFocusable(false);
        comboBorder.setMaximumSize(new Dimension(280, 25));
        comboBorder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) comboBorder.getSelectedItem();
                if (selected != null) {
                    if (selected.contains("Wrap")) {
                        model.setBorderMode("Wrap");
                    } else {
                        model.setBorderMode("Solid");
                    }
                }
                requestFocusInWindow();
            }
        });

        // Visual Theme selector
        JLabel lblTheme = createConfigLabel("Visual Theme Stylesheet:");
        String[] themes = { "Cyberpunk Neon", "Vaporwave Pink", "Matrix Green" };
        JComboBox<String> comboTheme = new JComboBox<>(themes);
        comboTheme.setBackground(new Color(35, 39, 48));
        comboTheme.setForeground(Color.WHITE);
        comboTheme.setFont(new Font("Segoe UI", Font.BOLD, 11));
        comboTheme.setFocusable(false);
        comboTheme.setMaximumSize(new Dimension(280, 25));
        comboTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) comboTheme.getSelectedItem();
                if (selected != null) {
                    if (selected.contains("Vaporwave")) {
                        model.setTheme("Vaporwave");
                    } else if (selected.contains("Matrix")) {
                        model.setTheme("Matrix");
                    } else {
                        model.setTheme("Cyberpunk");
                    }
                }
                view.repaint();
                requestFocusInWindow();
            }
        });

        // Spawn Rival AI checkbox
        JCheckBox cbRival = new JCheckBox("Spawn Rival AI Competitor", false);
        cbRival.setBackground(new Color(25, 27, 34));
        cbRival.setForeground(new Color(170, 180, 195));
        cbRival.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbRival.setFocusable(false);
        cbRival.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setRivalActive(cbRival.isSelected());
                controller.resetGame();
                requestFocusInWindow();
            }
        });

        // Map Obstacle Editor checkbox
        JCheckBox cbMapEdit = new JCheckBox("Map Obstacle Editor Mode", false);
        cbMapEdit.setBackground(new Color(25, 27, 34));
        cbMapEdit.setForeground(new Color(170, 180, 195));
        cbMapEdit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbMapEdit.setFocusable(false);
        cbMapEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setMapEditing(cbMapEdit.isSelected());
                if (cbMapEdit.isSelected()) {
                    model.setPaused(true);
                    model.getCurrentPath().clear();
                }
                view.repaint();
                requestFocusInWindow();
            }
        });

        // Sound options checkbox
        JCheckBox cbSound = new JCheckBox("Chiptune Synth Audio Effects", true);
        cbSound.setBackground(new Color(25, 27, 34));
        cbSound.setForeground(new Color(170, 180, 195));
        cbSound.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbSound.setFocusable(false);
        cbSound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getSoundManager().setSoundEnabled(cbSound.isSelected());
                requestFocusInWindow();
            }
        });

        sectionOptions.add(lblSpeed);
        sectionOptions.add(sliderSpeed);
        sectionOptions.add(Box.createVerticalStrut(14));
        sectionOptions.add(lblEpsilon);
        sectionOptions.add(sliderEpsilon);
        sectionOptions.add(Box.createVerticalStrut(14));
        sectionOptions.add(cbShowPath);
        sectionOptions.add(Box.createVerticalStrut(12));
        sectionOptions.add(cbRival);
        sectionOptions.add(Box.createVerticalStrut(12));
        sectionOptions.add(cbMapEdit);
        sectionOptions.add(Box.createVerticalStrut(12));
        sectionOptions.add(cbSound);
        sectionOptions.add(Box.createVerticalStrut(15));
        sectionOptions.add(lblBorderMode);
        sectionOptions.add(Box.createVerticalStrut(4));
        sectionOptions.add(comboBorder);
        sectionOptions.add(Box.createVerticalStrut(15));
        sectionOptions.add(lblTheme);
        sectionOptions.add(Box.createVerticalStrut(4));
        sectionOptions.add(comboTheme);

        panel.add(sectionOptions);
        panel.add(Box.createVerticalStrut(12));

        // --- SECTION 3: STATISTICS HUD ---
        JPanel sectionHUD = createSectionContainer("LIVE METRICS", 160);
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
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        btnPanel.setBackground(new Color(25, 27, 34));
        btnPanel.setPreferredSize(new Dimension(280, 110));
        btnPanel.setMinimumSize(new Dimension(280, 110));
        btnPanel.setMaximumSize(new Dimension(280, 110));
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPause = new JButton("Pause");
        JButton btnReset = new JButton("Reset");
        JButton btnBoard = new JButton("Scores");
        JButton btnHelp = new JButton("Help");
        JButton btnFull = new JButton("Fullscreen");
        JButton btnCredits = new JButton("Credits");
        
        styleButton(btnPause, new Color(76, 141, 255)); // cyan blue
        styleButton(btnReset, new Color(255, 59, 48)); // red
        styleButton(btnBoard, new Color(0, 229, 255)); // neon blue
        styleButton(btnHelp, new Color(255, 170, 0)); // orange help
        styleButton(btnFull, new Color(0, 229, 255)); // neon cyan
        styleButton(btnCredits, new Color(57, 255, 20)); // neon green

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
                if (model.isGameStarted() && !model.isGameOver() && !model.isPaused()) {
                    controller.pauseGame();
                }
                LeaderboardDialog dialog = new LeaderboardDialog(Project.this, new DatabaseManager());
                dialog.setVisible(true);
                requestFocusInWindow();
            }
        });

        btnHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.isGameStarted() && !model.isGameOver() && !model.isPaused()) {
                    controller.pauseGame();
                }
                HelpDialog dialog = new HelpDialog(Project.this);
                dialog.setVisible(true);
                requestFocusInWindow();
            }
        });

        btnFull.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullscreen();
            }
        });

        btnCredits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.isGameStarted() && !model.isGameOver() && !model.isPaused()) {
                    controller.pauseGame();
                }
                CreditsDialog dialog = new CreditsDialog(Project.this);
                dialog.setVisible(true);
                requestFocusInWindow();
            }
        });

        btnPanel.add(btnPause);
        btnPanel.add(btnReset);
        btnPanel.add(btnBoard);
        btnPanel.add(btnHelp);
        btnPanel.add(btnFull);
        btnPanel.add(btnCredits);
        panel.add(btnPanel);

        panel.add(Box.createVerticalStrut(5));

        JButton btnExit = new JButton("Exit");
        styleButton(btnExit, new Color(255, 59, 48)); // red accent
        btnExit.setPreferredSize(new Dimension(280, 33));
        btnExit.setMinimumSize(new Dimension(280, 33));
        btnExit.setMaximumSize(new Dimension(280, 33));
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        });
        panel.add(btnExit);
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void exitApplication() {
        if (controller != null && controller.getGameTimer() != null) {
            controller.getGameTimer().stop();
        }
        dispose();
        System.exit(0);
    }

    // Styling Helpers
    private JPanel createSectionContainer(String title, int height) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(25, 27, 34));
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 55, 68), 1),
                title,
                0,
                2,
                new Font("Segoe UI", Font.BOLD, 11),
                new Color(110, 115, 125)
            ),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        section.setPreferredSize(new Dimension(280, height));
        section.setMinimumSize(new Dimension(280, height));
        section.setMaximumSize(new Dimension(280, height));
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        return section;
    }

    private void styleRadioButton(JRadioButton rb) {
        rb.setBackground(new Color(25, 27, 34));
        rb.setForeground(new Color(200, 205, 215));
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rb.setFocusable(false);
        rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        rb.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rb.setForeground(new Color(0, 229, 255)); // Glow cyan on hover
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (rb.isSelected()) {
                    rb.setForeground(new Color(57, 255, 20)); // Keep green if selected
                } else {
                    rb.setForeground(new Color(200, 205, 215)); // Restore default
                }
            }
        });

        rb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parent = rb.getParent();
                if (parent != null) {
                    for (Component c : parent.getComponents()) {
                        if (c instanceof JRadioButton r) {
                            if (r.isSelected()) {
                                r.setForeground(new Color(57, 255, 20)); // Green for selected
                            } else {
                                r.setForeground(new Color(200, 205, 215)); // Gray for non-selected
                            }
                        }
                    }
                }
            }
        });
    }

    private void styleButton(JButton btn, Color accent) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(35, 39, 48));
        btn.setBorder(BorderFactory.createLineBorder(accent, 1));
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(accent);
                btn.setForeground(new Color(15, 17, 22)); // High-contrast text
                if (controller != null && controller.getSoundManager() != null) {
                    controller.getSoundManager().playHoverSound();
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(35, 39, 48));
                btn.setForeground(Color.WHITE);
            }
        });
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

    private void setupKeyBindings() {
        JComponent content = (JComponent) getContentPane();
        int[] keys = {
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
            KeyEvent.VK_SPACE, KeyEvent.VK_F11, KeyEvent.VK_P
        };

        for (int k : keys) {
            String keyName = "KEY_" + k;
            content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(k, 0), keyName);
            content.getActionMap().put(keyName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (k == KeyEvent.VK_F11) {
                        toggleFullscreen();
                    } else {
                        controller.handleKeyPress(k);
                    }
                }
            });
        }
    }

    // Fullscreen fields & methods
    private boolean isFullscreen = false;
    private Dimension savedSize;
    private Point savedLocation;

    public void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        dispose(); // Dispose frame to change decoration settings
        if (isFullscreen) {
            savedSize = getSize();
            savedLocation = getLocation();
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setUndecorated(false);
            setExtendedState(JFrame.NORMAL);
            setSize(savedSize != null ? savedSize : new Dimension(1100, 600));
            if (savedLocation != null) setLocation(savedLocation);
            else setLocationRelativeTo(null);
        }
        setVisible(true);
        requestFocusInWindow();
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
