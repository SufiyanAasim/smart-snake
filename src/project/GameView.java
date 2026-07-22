/**
 * ==============================================================================
 * Project: Smart Snake
 * Module: GameView (MVC Graphical View Component)
 * Authors:
 *   - Mohammad Sufiyan Aasim (sufiyanaasim@outlook.com / GitHub: SufiyanAasim)
 *   - Fahad Bin Nasir (fahadabbasi17025@gmail.com / GitHub: FahadBinNasir)
 * ==============================================================================
 */
package project;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.font.TextLayout;
import java.awt.Point;
import java.awt.Font;

public class GameView extends JPanel {
    private final GameModel model;
    private java.awt.Image logoImage;

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(model.getWidth(), model.getHeight()));
        setBackground(new Color(10, 12, 16)); // Sleek, modern dark background

        // Load visual logo
        try {
            java.io.File logoFile = new java.io.File("assets/logo.png");
            if (logoFile.exists()) {
                logoImage = javax.imageio.ImageIO.read(logoFile);
            }
        } catch (java.io.IOException e) {
            System.err.println("Could not load logo image: " + e.getMessage());
        }

        // Map creator wall painter mouse hooks
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (model.isMapEditing()) {
                    toggleObstacleAtMouse(e.getPoint(), true);
                }
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (model.isMapEditing()) {
                    toggleObstacleAtMouse(e.getPoint(), false);
                }
            }
        });
    }

    private java.awt.image.BufferedImage bufferImage;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (bufferImage == null) {
            bufferImage = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D gBuffer = bufferImage.createGraphics();
        gBuffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine theme colors dynamically based on model theme selection
        Color bgColor, gridColor, boundaryColor, snakeColor, foodColor;
        String activeTheme = model.getTheme();
        if (activeTheme.equals("Vaporwave")) {
            bgColor = new Color(34, 15, 48); // dark purple
            gridColor = new Color(48, 20, 68);
            boundaryColor = new Color(180, 0, 255); // glowing violet
            snakeColor = new Color(0, 229, 255); // neon cyan
            foodColor = new Color(255, 0, 127); // hot pink
        } else if (activeTheme.equals("Matrix")) {
            bgColor = new Color(0, 0, 0); // pitch black
            gridColor = new Color(0, 30, 0);
            boundaryColor = new Color(0, 180, 0); // matrix forest green
            snakeColor = new Color(0, 255, 0); // matrix neon green
            foodColor = new Color(120, 255, 0); // amber green
        } else {
            // Default Cyberpunk
            bgColor = new Color(10, 12, 16);
            gridColor = new Color(24, 28, 38);
            boundaryColor = new Color(0, 229, 255); // glowing cyan
            snakeColor = new Color(52, 199, 89); // neon green
            foodColor = new Color(255, 59, 48); // neon red
        }

        // Fill background of buffer
        gBuffer.setColor(bgColor);
        gBuffer.fillRect(0, 0, 800, 600);

        int cellSize = model.getCellSize();

        // 1. Draw Grid Lines (Subtle lines for depth)
        gBuffer.setColor(gridColor);
        for (int x = 0; x < model.getWidth(); x += cellSize) {
            gBuffer.drawLine(x, 0, x, model.getHeight());
        }
        for (int y = 0; y < model.getHeight(); y += cellSize) {
            gBuffer.drawLine(0, y, model.getWidth(), y);
        }

        if (!model.isGameStarted()) {
            drawOverlayMessage(gBuffer, "Kill Enemy \nAvoid hitting the walls & obstacles \nSelect a mode in the sidebar \nPress SPACE to Start Game");
        } else {
            // 2. Draw A* Path Overlay
            if (model.isPathVisualized() && model.getCurrentPath() != null && !model.getCurrentPath().isEmpty()) {
                gBuffer.setColor(new Color(76, 141, 255, 60)); // Neon blue translucent overlay
                for (GamePoint p : model.getCurrentPath()) {
                    gBuffer.fillRect(p.x() + 2, p.y() + 2, cellSize - 4, cellSize - 4);
                }
            }

            // 3. Draw Dynamic Obstacles (Gray bricks with solid borders)
            gBuffer.setColor(new Color(70, 75, 85));
            for (GamePoint p : model.getObstacles()) {
                gBuffer.fillRoundRect(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2, 4, 4);
                gBuffer.setColor(new Color(110, 115, 125));
                gBuffer.drawRoundRect(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2, 4, 4);
                gBuffer.setColor(new Color(70, 75, 85));
            }

            // 4. Draw Food (Varied by type)
            if (model.getFoodType().equals("Golden")) {
                gBuffer.setColor(new Color(255, 215, 0)); // Gold
                gBuffer.fillRoundRect(model.getFood().x() + 2, model.getFood().y() + 2, cellSize - 4, cellSize - 4, 4, 4);
                gBuffer.setColor(Color.WHITE);
                gBuffer.drawRoundRect(model.getFood().x() + 2, model.getFood().y() + 2, cellSize - 4, cellSize - 4, 4, 4);
            } else if (model.getFoodType().equals("Shield")) {
                gBuffer.setColor(new Color(0, 229, 255)); // Neon Cyan/Blue Orb
                gBuffer.fillOval(model.getFood().x() + 2, model.getFood().y() + 2, cellSize - 4, cellSize - 4);
                gBuffer.setColor(Color.WHITE);
                gBuffer.drawOval(model.getFood().x() + 2, model.getFood().y() + 2, cellSize - 4, cellSize - 4);
            } else {
                // Normal food
                gBuffer.setColor(foodColor);
                gBuffer.fillRect(model.getFood().x() + 3, model.getFood().y() + 3, cellSize - 6, cellSize - 6);
                gBuffer.setColor(new Color(Math.min(255, foodColor.getRed() + 60), Math.min(255, foodColor.getGreen() + 60), Math.min(255, foodColor.getBlue() + 60)));
                gBuffer.drawRect(model.getFood().x() + 3, model.getFood().y() + 3, cellSize - 6, cellSize - 6);
            }

            // 5a. Draw Player Snake with fading theme gradient
            Color currentSnakeColor = snakeColor;
            int size = model.getSnake().size();
            for (int i = 0; i < size; i++) {
                GamePoint p = model.getSnake().get(i);
                gBuffer.setColor(currentSnakeColor);
                
                if (i == 0) {
                    gBuffer.fillRoundRect(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2, 8, 8);
                    
                    // Draw a glowing shield outline bubble around head if hasShield is true
                    if (model.hasShield()) {
                        gBuffer.setColor(new Color(0, 229, 255, 180));
                        gBuffer.setStroke(new BasicStroke(2.5f));
                        gBuffer.drawOval(p.x() - 4, p.y() - 4, cellSize + 8, cellSize + 8);
                        gBuffer.setStroke(new BasicStroke(1f));
                    }
                } else {
                    gBuffer.fillOval(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2);
                }

                // Apply tail color fading multiplier
                int newGreen = (int) Math.round(currentSnakeColor.getGreen() * 0.85);
                int newRed = (int) Math.round(currentSnakeColor.getRed() * 0.85);
                int newBlue = (int) Math.round(currentSnakeColor.getBlue() * 0.85);
                currentSnakeColor = new Color(newRed, newGreen, newBlue);
            }

            // 5b. Draw Rival AI Snake if active
            if (model.isRivalActive() && !model.getEnemySnake().isEmpty()) {
                Color rivalColor = new Color(255, 110, 0); // Orange/Red Neon
                int enemySize = model.getEnemySnake().size();
                for (int i = 0; i < enemySize; i++) {
                    GamePoint p = model.getEnemySnake().get(i);
                    gBuffer.setColor(rivalColor);
                    if (i == 0) {
                        gBuffer.fillRoundRect(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2, 8, 8);
                    } else {
                        gBuffer.fillOval(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2);
                    }
                    int newGreen = (int) Math.round(rivalColor.getGreen() * 0.85);
                    int newRed = (int) Math.round(rivalColor.getRed() * 0.85);
                    rivalColor = new Color(newRed, newGreen, 0);
                }
            }

            // 5c. Draw Map Editor active text overlay
            if (model.isMapEditing()) {
                gBuffer.setColor(new Color(255, 170, 0, 40));
                gBuffer.fillRect(0, 0, 800, 600);
                gBuffer.setColor(new Color(255, 170, 0));
                gBuffer.setFont(gBuffer.getFont().deriveFont(Font.BOLD, 18f));
                String editorMsg = "MAP EDITOR ACTIVE - Click/Drag to Paint Walls";
                int textX = (800 - gBuffer.getFontMetrics().stringWidth(editorMsg)) / 2;
                gBuffer.drawString(editorMsg, textX, 40);
            }

            // 6. Draw Game Over or Pause Overlays
            if (model.isGameOver()) {
                drawOverlayMessage(gBuffer, "Your Score: " + model.getScore()
                        + "\nHigh Score: " + model.getHighScore()
                        + "\nPress SPACE to Restart");
            } else if (model.isPaused()) {
                drawOverlayMessage(gBuffer, "GAME PAUSED\n\nPress PAUSE to Resume");
            }
        }

        // Draw Glowing Laser Border around the grid play area
        gBuffer.setColor(boundaryColor);
        gBuffer.setStroke(new BasicStroke(4f));
        gBuffer.drawRect(2, 2, 800 - 4, 600 - 4);

        gBuffer.dispose();

        // Draw scaled buffer onto actual panel graphics with aspect ratio preservation
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Fill letterbox border background
        g2d.setColor(new Color(15, 17, 22));
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        double targetAspect = 800.0 / 600.0;
        int drawWidth = panelWidth;
        int drawHeight = (int) (panelWidth / targetAspect);

        if (drawHeight > panelHeight) {
            drawHeight = panelHeight;
            drawWidth = (int) (panelHeight * targetAspect);
        }

        int xOffset = (panelWidth - drawWidth) / 2;
        int yOffset = (panelHeight - drawHeight) / 2;

        g2d.drawImage(bufferImage, xOffset, yOffset, drawWidth, drawHeight, null);
    }

    private void drawOverlayMessage(Graphics2D g2d, String message) {
        // Translucent background card for readability
        g2d.setColor(new Color(15, 20, 30, 220));
        g2d.fillRect(0, 0, model.getWidth(), model.getHeight());

        // Draw Centered Logo
        if (logoImage != null) {
            int logoWidth = 140;
            int logoHeight = 140;
            int logoX = (model.getWidth() - logoWidth) / 2;
            int logoY = model.getHeight() / 6;
            g2d.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, null);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(g2d.getFont().deriveFont(22F));
        
        int currentHeight = (logoImage != null) ? (model.getHeight() / 6 + 160) : (model.getHeight() / 3);
        var frc = g2d.getFontRenderContext();
        for (String line : message.split("\n")) {
            var layout = new TextLayout(line, g2d.getFont(), frc);
            var bounds = layout.getBounds();
            float targetWidth = (float) (model.getWidth() - bounds.getWidth()) / 2;
            
            // Draw text shadow
            g2d.setColor(new Color(0, 0, 0, 180));
            layout.draw(g2d, targetWidth + 1, currentHeight + 1);

            // Draw text front
            g2d.setColor(new Color(230, 235, 245));
            layout.draw(g2d, targetWidth, currentHeight);
            
            currentHeight += g2d.getFontMetrics().getHeight();
        }
    }

    private void toggleObstacleAtMouse(Point p, boolean toggle) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        if (panelWidth <= 0 || panelHeight <= 0) return;
        
        // Account for aspect ratio letterboxing offset
        double targetAspect = 800.0 / 600.0;
        int drawWidth = panelWidth;
        int drawHeight = (int) (panelWidth / targetAspect);

        if (drawHeight > panelHeight) {
            drawHeight = panelHeight;
            drawWidth = (int) (panelHeight * targetAspect);
        }

        int xOffset = (panelWidth - drawWidth) / 2;
        int yOffset = (panelHeight - drawHeight) / 2;

        // Map mouse coordinates to 800x600 buffer coordinates
        int rx = p.x - xOffset;
        int ry = p.y - yOffset;

        if (rx < 0 || rx >= drawWidth || ry < 0 || ry >= drawHeight) return;

        int bx = (int) (rx * 800.0 / drawWidth);
        int by = (int) (ry * 600.0 / drawHeight);

        int cellSize = model.getCellSize();
        int gx = bx / cellSize * cellSize;
        int gy = by / cellSize * cellSize;

        // Bounds check
        if (gx < 0 || gx >= model.getWidth() || gy < 0 || gy >= model.getHeight()) return;

        GamePoint gridPt = new GamePoint(gx, gy);
        // Do not block player snake, food, or enemy snake
        if (model.getSnake().contains(gridPt) || gridPt.equals(model.getFood())) return;
        if (model.isRivalActive() && model.getEnemySnake().contains(gridPt)) return;

        if (toggle) {
            if (model.getObstacles().contains(gridPt)) {
                model.getObstacles().remove(gridPt);
            } else {
                model.getObstacles().add(gridPt);
            }
        } else {
            // Dragging: just add wall obstacles
            if (!model.getObstacles().contains(gridPt)) {
                model.getObstacles().add(gridPt);
            }
        }
        repaint();
    }
}
