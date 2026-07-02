/**
 * ==============================================================================
 * Project: Smart Snake Game
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

        // Fill background of buffer
        gBuffer.setColor(new Color(10, 12, 16));
        gBuffer.fillRect(0, 0, 800, 600);

        int cellSize = model.getCellSize();

        // 1. Draw Grid Lines (Subtle dark lines for depth)
        gBuffer.setColor(new Color(24, 28, 38));
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

            // 4. Draw Food (Neon Red rectangular target)
            gBuffer.setColor(new Color(255, 59, 48)); // Neon Red
            gBuffer.fillRect(model.getFood().x() + 3, model.getFood().y() + 3, cellSize - 6, cellSize - 6);
            // Glowing border
            gBuffer.setColor(new Color(255, 120, 110));
            gBuffer.drawRect(model.getFood().x() + 3, model.getFood().y() + 3, cellSize - 6, cellSize - 6);

            // 5. Draw Snake with a beautiful fading green gradient
            Color snakeColor = new Color(52, 199, 89); // Neon Green
            int size = model.getSnake().size();
            for (int i = 0; i < size; i++) {
                GamePoint p = model.getSnake().get(i);
                gBuffer.setColor(snakeColor);
                
                // Draw head slightly larger or as a round block
                if (i == 0) {
                    gBuffer.fillRoundRect(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2, 8, 8);
                } else {
                    gBuffer.fillOval(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2);
                }

                // Apply tail color multiplier
                int newGreen = (int) Math.round(snakeColor.getGreen() * 0.85);
                int newRed = (int) Math.round(snakeColor.getRed() * 0.85);
                snakeColor = new Color(newRed, newGreen, 0);
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
        gBuffer.setColor(new Color(0, 229, 255));
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
}
