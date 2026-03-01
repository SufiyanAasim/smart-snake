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

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(model.getWidth(), model.getHeight()));
        setBackground(new Color(10, 12, 16)); // Sleek, modern dark background
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cellSize = model.getCellSize();

        // 1. Draw Grid Lines (Subtle dark lines for depth)
        g2d.setColor(new Color(24, 28, 38));
        for (int x = 0; x < model.getWidth(); x += cellSize) {
            g2d.drawLine(x, 0, x, model.getHeight());
        }
        for (int y = 0; y < model.getHeight(); y += cellSize) {
            g2d.drawLine(0, y, model.getWidth(), y);
        }

        if (!model.isGameStarted()) {
            drawOverlayMessage(g2d, "Kill Enemy \nAvoid hitting the walls & obstacles \nSelect a mode in the sidebar \nPress SPACE to Start Game");
        } else {
            // 2. Draw A* Path Overlay
            if (model.isPathVisualized() && model.getCurrentPath() != null && !model.getCurrentPath().isEmpty()) {
                g2d.setColor(new Color(76, 141, 255, 60)); // Neon blue translucent overlay
                for (GamePoint p : model.getCurrentPath()) {
                    g2d.fillRect(p.x() + 2, p.y() + 2, cellSize - 4, cellSize - 4);
                }
            }

            // 3. Draw Dynamic Obstacles (Gray bricks with solid borders)
            g2d.setColor(new Color(70, 75, 85));
            for (GamePoint p : model.getObstacles()) {
                g2d.fillRoundRect(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2, 4, 4);
                g2d.setColor(new Color(110, 115, 125));
                g2d.drawRoundRect(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2, 4, 4);
                g2d.setColor(new Color(70, 75, 85));
            }

            // 4. Draw Food (Neon Red rectangular target)
            g2d.setColor(new Color(255, 59, 48)); // Neon Red
            g2d.fillRect(model.getFood().x() + 3, model.getFood().y() + 3, cellSize - 6, cellSize - 6);
            // Glowing border
            g2d.setColor(new Color(255, 120, 110));
            g2d.drawRect(model.getFood().x() + 3, model.getFood().y() + 3, cellSize - 6, cellSize - 6);

            // 5. Draw Snake with a beautiful fading green gradient
            Color snakeColor = new Color(52, 199, 89); // Neon Green
            int size = model.getSnake().size();
            for (int i = 0; i < size; i++) {
                GamePoint p = model.getSnake().get(i);
                g2d.setColor(snakeColor);
                
                // Draw head slightly larger or as a round block
                if (i == 0) {
                    g2d.fillRoundRect(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2, 8, 8);
                } else {
                    g2d.fillOval(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2);
                }

                // Apply tail color multiplier
                int newGreen = (int) Math.round(snakeColor.getGreen() * 0.85);
                int newRed = (int) Math.round(snakeColor.getRed() * 0.85);
                snakeColor = new Color(newRed, newGreen, 0);
            }

            // 6. Draw Game Over Overlay Screen
            if (model.isGameOver()) {
                drawOverlayMessage(g2d, "Your Score: " + model.getScore()
                        + "\nHigh Score: " + model.getHighScore()
                        + "\nPress SPACE to Restart");
            }
        }
    }

    private void drawOverlayMessage(Graphics2D g2d, String message) {
        // Translucent background card for readability
        g2d.setColor(new Color(15, 20, 30, 220));
        g2d.fillRect(0, 0, model.getWidth(), model.getHeight());

        g2d.setColor(Color.WHITE);
        g2d.setFont(g2d.getFont().deriveFont(22F));
        
        int currentHeight = model.getHeight() / 3;
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
