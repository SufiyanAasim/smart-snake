/**
 * ==============================================================================
 * Project: Smart Snake Game
 * Module: Snakegame (Legacy Container Engine & Autoplay hooks)
 * Authors:
 *   - Mohammad Sufiyan Aasim (sufiyanaasim@outlook.com / GitHub: SufiyanAasim)
 *   - Fahad Bin Nasir (fahadabbasi17025@gmail.com / GitHub: FahadBinNasir)
 * ==============================================================================
 */
package project;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Snakegame extends JPanel implements ActionListener {
    private final int width;
    private final int height;
    private final int cellSize;
    private final Random random = new Random();
    private static final int FRAME_RATE = 20;
    
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private int highScore;

    private GamePoint food;
    private Direction direction = Direction.RIGHT;
    private Direction newDirection = Direction.RIGHT;
    private final List<GamePoint> snake = new ArrayList<>();

    // Version 2.0.0 additions
    private final Pathfinder pathfinder;
    private final QLearningAgent qAgent;
    private String aiMode = "Manual"; // "Manual", "A*", "Q-Learning"
    private final List<GamePoint> obstacles = new ArrayList<>();
    private List<GamePoint> currentPath = new ArrayList<>();
    private int movesCount = 0;
    private final Timer gameTimer;
    private static final String Q_TABLE_FILE = "q_table.txt";

    public Snakegame(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.cellSize = width / (FRAME_RATE * 2);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);

        // Initialize AI components
        this.pathfinder = new Pathfinder(width, height, cellSize);
        this.qAgent = new QLearningAgent();
        this.qAgent.loadQTable(Q_TABLE_FILE);

        // Pre-train Q-learning if table does not exist
        java.io.File file = new java.io.File(Q_TABLE_FILE);
        if (!file.exists()) {
            trainQLearning(5000);
        }

        // Initialize timer
        this.gameTimer = new Timer(1000 / FRAME_RATE, this);
    }

    public void setAIMode(String mode) {
        this.aiMode = mode;
    }

    public String getAIMode() {
        return aiMode;
    }

    public void setPathVisualized(boolean show) {
        // Handled in drawing
    }

    public int getMovesCount() {
        return movesCount;
    }

    public List<GamePoint> getCurrentPath() {
        return currentPath;
    }

    public QLearningAgent getQLearningAgent() {
        return qAgent;
    }

    public void startGame() {
        resetGameData();
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        
        // Remove old listeners to prevent duplication
        for (var listener : getKeyListeners()) {
            removeKeyListener(listener);
        }

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                handleKeyEvent(e.getKeyCode());
            }
        });
        
        gameTimer.start();
    }

    private void handleKeyEvent(final int keyCode) {
        if (!gameStarted) {
            if (keyCode == KeyEvent.VK_SPACE) {
                gameStarted = true;
            }
        } else if (!gameOver) {
            // Manual controls
            if (aiMode.equals("Manual")) {
                switch (keyCode) {
                    case KeyEvent.VK_UP -> {
                        if (direction != Direction.DOWN) newDirection = Direction.UP;
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (direction != Direction.UP) newDirection = Direction.DOWN;
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (direction != Direction.LEFT) newDirection = Direction.RIGHT;
                    }
                    case KeyEvent.VK_LEFT -> {
                        if (direction != Direction.RIGHT) newDirection = Direction.LEFT;
                    }
                }
            }
            
            // Mode toggle options via numeric keys for retro mode
            if (keyCode == KeyEvent.VK_1) {
                aiMode = "Manual";
            } else if (keyCode == KeyEvent.VK_2) {
                aiMode = "A*";
            } else if (keyCode == KeyEvent.VK_3) {
                aiMode = "Q-Learning";
            }
        } else if (keyCode == KeyEvent.VK_SPACE) {
            gameStarted = false;
            gameOver = false;
            resetGameData();
        }
    }

    private void resetGameData() {
        snake.clear();
        snake.add(new GamePoint(width / 2, height / 2));
        obstacles.clear();
        currentPath.clear();
        movesCount = 0;
        direction = Direction.RIGHT;
        newDirection = Direction.RIGHT;
        gameTimer.setDelay(1000 / FRAME_RATE); // Reset speed
        generateFood();
    }

    private void generateFood() {
        Set<GamePoint> blocked = new HashSet<>(snake);
        blocked.addAll(obstacles);
        
        do {
            food = new GamePoint(
                random.nextInt(width / cellSize) * cellSize,
                random.nextInt(height / cellSize) * cellSize
            );
        } while (blocked.contains(food));
    }

    private void spawnObstacles() {
        // Spawn obstacles based on score milestones
        int score = snake.size();
        if (score > 0 && score % 4 == 0) {
            Set<GamePoint> blocked = new HashSet<>(snake);
            blocked.add(food);
            blocked.addAll(obstacles);

            GamePoint obs;
            int attempts = 0;
            do {
                obs = new GamePoint(
                    random.nextInt(width / cellSize) * cellSize,
                    random.nextInt(height / cellSize) * cellSize
                );
                attempts++;
            } while (blocked.contains(obs) && attempts < 100);

            if (attempts < 100) {
                obstacles.add(obs);
            }
        }
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);

        if (!gameStarted) {
            printMessage(graphics, "Kill Enemy \nAvoid hitting the walls & obstacles \n1: Manual, 2: A*, 3: Q-Learning \nPress Space Bar to Begin Game");
        } else {
            // Draw A* Path Overlay if path exists
            if (aiMode.equals("A*") && currentPath != null && !currentPath.isEmpty()) {
                graphics.setColor(new Color(255, 255, 255, 80)); // Translucent white
                for (GamePoint p : currentPath) {
                    graphics.fillRect(p.x() + 2, p.y() + 2, cellSize - 4, cellSize - 4);
                }
            }

            // Draw Obstacles (Gray blocks)
            graphics.setColor(Color.GRAY);
            for (GamePoint p : obstacles) {
                graphics.fillRect(p.x() + 1, p.y() + 1, cellSize - 2, cellSize - 2);
            }

            // Draw Food
            graphics.setColor(Color.RED);
            graphics.fillRect(food.x(), food.y(), (int) (cellSize - (cellSize * 0.3)), cellSize);

            // Draw Snake
            Color snakeColor = Color.GREEN;
            for (final var point : snake) {
                graphics.setColor(snakeColor);
                graphics.fillOval(point.x(), point.y(), cellSize, cellSize);
                final int newGreen = (int) Math.round(snakeColor.getGreen() * (0.85));
                snakeColor = new Color(0, newGreen, 0);
            }

            // Overlay Mode Header
            graphics.setColor(Color.WHITE);
            graphics.setFont(graphics.getFont().deriveFont(16F));
            graphics.drawString("Mode: " + aiMode + " | Score: " + snake.size() + " | Steps: " + movesCount, 15, 25);

            if (gameOver) {
                final int currentScore = snake.size();
                if (currentScore > highScore) {
                    highScore = currentScore;
                }
                printMessage(graphics, "Your Score: " + currentScore
                        + "\nHigh Score: " + highScore
                        + "\nPress Space Bar to Reset");
            }
        }
    }

    private void printMessage(final Graphics graphics, final String message) {
        graphics.setColor(Color.WHITE);
        graphics.setFont(graphics.getFont().deriveFont(24F));
        int currentHeight = height / 3;
        final var graphics2D = (Graphics2D) graphics;
        final var frc = graphics2D.getFontRenderContext();
        for (final var line : message.split("\n")) {
            final var layout = new TextLayout(line, graphics.getFont(), frc);
            final var bounds = layout.getBounds();
            final var targetWidth = (float) (width - bounds.getWidth()) / 2;
            layout.draw(graphics2D, targetWidth, currentHeight);
            currentHeight += graphics.getFontMetrics().getHeight();
        }
    }

    private void move() {
        GamePoint head = snake.getFirst();

        // AI calculations
        if (aiMode.equals("A*")) {
            List<GamePoint> path = pathfinder.findPath(head, food, snake, obstacles);
            if (path != null && !path.isEmpty()) {
                currentPath = path;
                GamePoint nextStep = path.get(0);
                direction = getDirToNode(head, nextStep);
            } else {
                // BFS Fallback search to tail/safety
                List<GamePoint> safetyPath = pathfinder.findSafetyPath(head, snake, obstacles);
                if (safetyPath != null && !safetyPath.isEmpty()) {
                    currentPath = safetyPath;
                    GamePoint nextStep = safetyPath.get(0);
                    direction = getDirToNode(head, nextStep);
                } else {
                    currentPath.clear();
                }
            }
            newDirection = direction;
        } else if (aiMode.equals("Q-Learning")) {
            Set<GamePoint> blocked = new HashSet<>(snake);
            blocked.addAll(obstacles);
            int state = qAgent.getState(head, direction, food, blocked, width, height, cellSize);
            int action = qAgent.getAction(state, false);
            newDirection = qAgent.getTurnDirection(direction, action);
            direction = newDirection;
        } else {
            direction = newDirection;
        }

        final GamePoint newHead = switch (direction) {
            case UP -> new GamePoint(head.x(), head.y() - cellSize);
            case DOWN -> new GamePoint(head.x(), head.y() + cellSize);
            case LEFT -> new GamePoint(head.x() - cellSize, head.y());
            case RIGHT -> new GamePoint(head.x() + cellSize, head.y());
        };
        
        snake.addFirst(newHead);
        movesCount++;

        if (newHead.equals(food)) {
            generateFood();
            spawnObstacles();
            // Dynamic difficulty: increase speed as score grows
            int newSpeed = Math.max(30, 1000 / (FRAME_RATE + (snake.size() / 2)));
            gameTimer.setDelay(newSpeed);
        } else if (isCollision()) {
            gameOver = true;
            snake.removeFirst();
        } else {
            snake.removeLast();
        }
    }

    private Direction getDirToNode(GamePoint from, GamePoint to) {
        if (to.y() < from.y()) return Direction.UP;
        if (to.y() > from.y()) return Direction.DOWN;
        if (to.x() < from.x()) return Direction.LEFT;
        return Direction.RIGHT;
    }

    private boolean isCollision() {
        final GamePoint head = snake.getFirst();
        
        // Wall Collision
        if (head.x() < 0 || head.x() >= width || head.y() < 0 || head.y() >= height) {
            return true;
        }

        // Obstacle Collision
        if (obstacles.contains(head)) {
            return true;
        }

        // Self Collision
        return snake.size() != new HashSet<>(snake).size();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (gameStarted && !gameOver) {
            move();
        }
        repaint();
    }

    // Headless Q-learning training method
    public void trainQLearning(int episodes) {
        System.out.println("Starting headless Q-learning training for " + episodes + " episodes...");
        qAgent.setEpsilon(1.0f); // start high exploration
        float epsilonDecay = 0.999f;
        float minEpsilon = 0.01f;

        for (int ep = 0; ep < episodes; ep++) {
            List<GamePoint> tSnake = new ArrayList<>();
            tSnake.add(new GamePoint(width / 2, height / 2));
            GamePoint tFood = new GamePoint(random.nextInt(width / cellSize) * cellSize, random.nextInt(height / cellSize) * cellSize);
            Direction tDir = Direction.RIGHT;
            boolean tOver = false;
            int steps = 0;

            while (!tOver && steps < 500) {
                Set<GamePoint> blocked = new HashSet<>(tSnake);
                int state = qAgent.getState(tSnake.get(0), tDir, tFood, blocked, width, height, cellSize);
                int action = qAgent.getAction(state, true);
                
                Direction nextDir = qAgent.getTurnDirection(tDir, action);
                GamePoint curHead = tSnake.get(0);
                GamePoint nextHead = switch (nextDir) {
                    case UP -> new GamePoint(curHead.x(), curHead.y() - cellSize);
                    case DOWN -> new GamePoint(curHead.x(), curHead.y() + cellSize);
                    case LEFT -> new GamePoint(curHead.x() - cellSize, curHead.y());
                    case RIGHT -> new GamePoint(curHead.x() + cellSize, curHead.y());
                };

                float reward = -0.1f;
                boolean ate = nextHead.equals(tFood);
                boolean col = nextHead.x() < 0 || nextHead.x() >= width || nextHead.y() < 0 || nextHead.y() >= height || tSnake.contains(nextHead);

                if (col) {
                    reward = -100f;
                    tOver = true;
                } else if (ate) {
                    reward = 10f;
                    tSnake.add(0, nextHead);
                    tFood = new GamePoint(random.nextInt(width / cellSize) * cellSize, random.nextInt(height / cellSize) * cellSize);
                } else {
                    int prevDist = Math.abs(curHead.x() - tFood.x()) + Math.abs(curHead.y() - tFood.y());
                    int nextDist = Math.abs(nextHead.x() - tFood.x()) + Math.abs(nextHead.y() - tFood.y());
                    if (nextDist < prevDist) {
                        reward = 1f;
                    } else {
                        reward = -1.5f;
                    }
                    tSnake.add(0, nextHead);
                    tSnake.remove(tSnake.size() - 1);
                }

                int nextState = qAgent.getState(tSnake.get(0), nextDir, tFood, new HashSet<>(tSnake), width, height, cellSize);
                qAgent.update(state, action, nextState, reward);
                
                tDir = nextDir;
                steps++;
            }
            qAgent.setEpsilon(Math.max(minEpsilon, qAgent.getEpsilon() * epsilonDecay));
        }
        qAgent.saveQTable(Q_TABLE_FILE);
        System.out.println("Headless training completed and saved to " + Q_TABLE_FILE);
    }
}
