/**
 * ==============================================================================
 * Project: Smart Snake Game
 * Module: GameController (MVC Game Update Timer Scheduler)
 * Authors:
 *   - Mohammad Sufiyan Aasim (sufiyanaasim@outlook.com / GitHub: SufiyanAasim)
 *   - Fahad Bin Nasir (fahadabbasi17025@gmail.com / GitHub: FahadBinNasir)
 * ==============================================================================
 */
package project;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameController implements ActionListener {
    private final GameModel model;
    private final GameView view;
    private final Timer gameTimer;
    private final Pathfinder pathfinder;
    private final QLearningAgent qAgent;
    private final Random random = new Random();

    private HUDUpdateCallback hudCallback;
    private static final String Q_TABLE_FILE = "q_table.txt";

    public interface HUDUpdateCallback {
        void updateHUD(int score, int highScore, int steps, int pathLength, String efficiency, String stateVector);
        void onGameOver();
    }

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        this.gameTimer = new Timer(50, this); // Default frame delay
        this.pathfinder = new Pathfinder(model.getWidth(), model.getHeight(), model.getCellSize());
        this.qAgent = new QLearningAgent();
        this.qAgent.loadQTable(Q_TABLE_FILE);

        // Pre-train if missing
        java.io.File file = new java.io.File(Q_TABLE_FILE);
        if (!file.exists()) {
            trainQLearning(5000);
        }
    }

    public void setHUDCallback(HUDUpdateCallback callback) {
        this.hudCallback = callback;
    }

    public QLearningAgent getQLearningAgent() {
        return qAgent;
    }

    public void setSpeed(int speedMultiplier) {
        // speedMultiplier: 1x to 5x. Maps to delay 100ms down to 20ms.
        int delay = 120 - (speedMultiplier * 20);
        gameTimer.setDelay(delay);
    }

    public void startGame() {
        model.reset();
        generateFood();
        model.setGameStarted(true);
        gameTimer.start();
        updateHUD();
    }

    public void pauseGame() {
        if (gameTimer.isRunning()) {
            gameTimer.stop();
        } else {
            gameTimer.start();
        }
    }

    public void resetGame() {
        gameTimer.stop();
        model.reset();
        generateFood();
        model.setGameStarted(false);
        view.repaint();
        updateHUD();
    }

    public void handleKeyPress(int keyCode) {
        if (!model.isGameStarted()) {
            if (keyCode == KeyEvent.VK_SPACE) {
                startGame();
            }
            return;
        }

        if (model.isGameOver()) {
            if (keyCode == KeyEvent.VK_SPACE) {
                startGame();
            }
            return;
        }

        // Manual controls
        if (model.getAIMode().equals("Manual")) {
            switch (keyCode) {
                case KeyEvent.VK_UP -> {
                    if (model.getDirection() != Direction.DOWN) model.setNewDirection(Direction.UP);
                }
                case KeyEvent.VK_DOWN -> {
                    if (model.getDirection() != Direction.UP) model.setNewDirection(Direction.DOWN);
                }
                case KeyEvent.VK_RIGHT -> {
                    if (model.getDirection() != Direction.LEFT) model.setNewDirection(Direction.RIGHT);
                }
                case KeyEvent.VK_LEFT -> {
                    if (model.getDirection() != Direction.RIGHT) model.setNewDirection(Direction.LEFT);
                }
            }
        }
    }

    private void generateFood() {
        Set<GamePoint> blocked = new HashSet<>(model.getSnake());
        blocked.addAll(model.getObstacles());
        int cellSize = model.getCellSize();
        
        GamePoint foodPoint;
        do {
            foodPoint = new GamePoint(
                random.nextInt(model.getWidth() / cellSize) * cellSize,
                random.nextInt(model.getHeight() / cellSize) * cellSize
            );
        } while (blocked.contains(foodPoint));
        
        model.setFood(foodPoint);
    }

    private void spawnObstacle() {
        int score = model.getScore();
        if (score > 0 && score % 4 == 0) {
            Set<GamePoint> blocked = new HashSet<>(model.getSnake());
            blocked.add(model.getFood());
            blocked.addAll(model.getObstacles());

            int cellSize = model.getCellSize();
            GamePoint obs;
            int attempts = 0;
            do {
                obs = new GamePoint(
                    random.nextInt(model.getWidth() / cellSize) * cellSize,
                    random.nextInt(model.getHeight() / cellSize) * cellSize
                );
                attempts++;
            } while (blocked.contains(obs) && attempts < 100);

            if (attempts < 100) {
                model.getObstacles().add(obs);
            }
        }
    }

    private Direction getDirectionToTarget(GamePoint from, GamePoint to) {
        if (to.y() < from.y()) return Direction.UP;
        if (to.y() > from.y()) return Direction.DOWN;
        if (to.x() < from.x()) return Direction.LEFT;
        return Direction.RIGHT;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (model.isGameStarted() && !model.isGameOver()) {
            move();
        }
        view.repaint();
    }

    private void move() {
        GamePoint head = model.getSnake().getFirst();
        int cellSize = model.getCellSize();

        // 1. Evaluate Autoplay Steering
        if (model.getAIMode().equals("A*")) {
            List<GamePoint> path = pathfinder.findPath(head, model.getFood(), model.getSnake(), model.getObstacles());
            if (path != null && !path.isEmpty()) {
                model.setCurrentPath(path);
                model.setDirection(getDirectionToTarget(head, path.get(0)));
            } else {
                // BFS survival routing
                List<GamePoint> safety = pathfinder.findSafetyPath(head, model.getSnake(), model.getObstacles());
                if (safety != null && !safety.isEmpty()) {
                    model.setCurrentPath(safety);
                    model.setDirection(getDirectionToTarget(head, safety.get(0)));
                } else {
                    model.getCurrentPath().clear();
                }
            }
            model.setNewDirection(model.getDirection());
        } else if (model.getAIMode().equals("Q-Learning")) {
            Set<GamePoint> blocked = new HashSet<>(model.getSnake());
            blocked.addAll(model.getObstacles());
            
            int state = qAgent.getState(head, model.getDirection(), model.getFood(), blocked, model.getWidth(), model.getHeight(), cellSize);
            int action = qAgent.getAction(state, false);
            
            model.setNewDirection(qAgent.getTurnDirection(model.getDirection(), action));
            model.setDirection(model.getNewDirection());
            model.getCurrentPath().clear();
        } else {
            model.setDirection(model.getNewDirection());
            model.getCurrentPath().clear();
        }

        // 2. Perform step translation
        GamePoint newHead = switch (model.getDirection()) {
            case UP -> new GamePoint(head.x(), head.y() - cellSize);
            case DOWN -> new GamePoint(head.x(), head.y() + cellSize);
            case LEFT -> new GamePoint(head.x() - cellSize, head.y());
            case RIGHT -> new GamePoint(head.x() + cellSize, head.y());
        };

        model.getSnake().addFirst(newHead);
        model.incrementMoves();

        // 3. Collision Checks
        boolean isWallCollision = newHead.x() < 0 || newHead.x() >= model.getWidth() || newHead.y() < 0 || newHead.y() >= model.getHeight();
        boolean isObstacleCollision = model.getObstacles().contains(newHead);
        boolean isSelfCollision = model.getSnake().size() != new HashSet<>(model.getSnake()).size();

        if (isWallCollision || isObstacleCollision || isSelfCollision) {
            model.setGameOver(true);
            model.getSnake().removeFirst();
            model.updateHighScore();
            gameTimer.stop();
            if (hudCallback != null) {
                hudCallback.onGameOver();
            }
        } else if (newHead.equals(model.getFood())) {
            generateFood();
            spawnObstacle();
        } else {
            model.getSnake().removeLast();
        }

        updateHUD();
    }

    // Helper statistics
    public void updateHUD() {
        if (hudCallback == null) return;

        int score = model.getScore();
        int highScore = model.getHighScore();
        int steps = model.getMovesCount();
        int pathLength = model.getCurrentPath().size();

        // Path Efficiency Index (A* path length / Manhattan distance)
        String efficiency = "N/A";
        if (model.getAIMode().equals("A*") && pathLength > 0) {
            GamePoint head = model.getSnake().getFirst();
            int manhattan = Math.abs(head.x() - model.getFood().x()) + Math.abs(head.y() - model.getFood().y());
            // divide by cellSize to compare segment steps
            int manhattanSteps = manhattan / model.getCellSize();
            if (manhattanSteps > 0) {
                double eff = (double) pathLength / manhattanSteps;
                efficiency = String.format("%.2fx", eff);
            } else {
                efficiency = "1.00x";
            }
        }

        // Active State Vector string for ML monitoring
        String stateVector = "N/A";
        if (model.getAIMode().equals("Q-Learning")) {
            GamePoint head = model.getSnake().getFirst();
            Set<GamePoint> blocked = new HashSet<>(model.getSnake());
            blocked.addAll(model.getObstacles());
            int state = qAgent.getState(head, model.getDirection(), model.getFood(), blocked, model.getWidth(), model.getHeight(), model.getCellSize());
            
            // Format state as danger binary + food relative quadrants
            int dangerBits = (state >> 4) & 7;
            int foodBits = state & 15;
            stateVector = String.format("D:%s | F:%s (%d)", 
                leftPad(Integer.toBinaryString(dangerBits), 3),
                leftPad(Integer.toBinaryString(foodBits), 4),
                state
            );
        }

        hudCallback.updateHUD(score, highScore, steps, pathLength, efficiency, stateVector);
    }

    private String leftPad(String binary, int len) {
        StringBuilder sb = new StringBuilder(binary);
        while (sb.length() < len) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    // Headless Q-learning training method
    public void trainQLearning(int episodes) {
        System.out.println("Starting headless Q-learning training for " + episodes + " episodes...");
        qAgent.setEpsilon(1.0f);
        float epsilonDecay = 0.999f;
        float minEpsilon = 0.01f;
        int cellSize = model.getCellSize();

        for (int ep = 0; ep < episodes; ep++) {
            List<GamePoint> tSnake = new ArrayList<>();
            tSnake.add(new GamePoint(model.getWidth() / 2, model.getHeight() / 2));
            GamePoint tFood = new GamePoint(
                random.nextInt(model.getWidth() / cellSize) * cellSize,
                random.nextInt(model.getHeight() / cellSize) * cellSize
            );
            Direction tDir = Direction.RIGHT;
            boolean tOver = false;
            int steps = 0;

            while (!tOver && steps < 500) {
                Set<GamePoint> blocked = new HashSet<>(tSnake);
                int state = qAgent.getState(tSnake.get(0), tDir, tFood, blocked, model.getWidth(), model.getHeight(), cellSize);
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
                boolean col = nextHead.x() < 0 || nextHead.x() >= model.getWidth() || nextHead.y() < 0 || nextHead.y() >= model.getHeight() || tSnake.contains(nextHead);

                if (col) {
                    reward = -100f;
                    tOver = true;
                } else if (ate) {
                    reward = 10f;
                    tSnake.add(0, nextHead);
                    tFood = new GamePoint(
                        random.nextInt(model.getWidth() / cellSize) * cellSize,
                        random.nextInt(model.getHeight() / cellSize) * cellSize
                    );
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

                int nextState = qAgent.getState(tSnake.get(0), nextDir, tFood, new HashSet<>(tSnake), model.getWidth(), model.getHeight(), cellSize);
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
