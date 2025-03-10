package project;

import java.util.*;

public class GameModel {
    private final int width;
    private final int height;
    private final int cellSize;

    private final List<GamePoint> snake = new ArrayList<>();
    private final List<GamePoint> obstacles = new ArrayList<>();
    private GamePoint food;
    
    private Direction direction = Direction.RIGHT;
    private Direction newDirection = Direction.RIGHT;
    
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private int score = 0;
    private int highScore = 0;
    private int movesCount = 0;

    // AI configurations
    private String aiMode = "Manual"; // "Manual", "A*", "Q-Learning"
    private List<GamePoint> currentPath = new ArrayList<>();
    private boolean pathVisualized = true;

    public GameModel(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        reset();
    }

    public void reset() {
        snake.clear();
        snake.add(new GamePoint(width / 2, height / 2));
        obstacles.clear();
        currentPath.clear();
        score = 0;
        movesCount = 0;
        direction = Direction.RIGHT;
        newDirection = Direction.RIGHT;
        gameOver = false;
        // Keep gameStarted state so player doesn't have to re-press Space if not needed,
        // but reset if it was game over.
    }

    // Getters and Setters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCellSize() { return cellSize; }

    public List<GamePoint> getSnake() { return snake; }
    public List<GamePoint> getObstacles() { return obstacles; }
    
    public GamePoint getFood() { return food; }
    public void setFood(GamePoint food) { this.food = food; }

    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public Direction getNewDirection() { return newDirection; }
    public void setNewDirection(Direction newDirection) { this.newDirection = newDirection; }

    public boolean isGameStarted() { return gameStarted; }
    public void setGameStarted(boolean started) { this.gameStarted = started; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean over) { this.gameOver = over; }

    public int getScore() { return snake.size(); }
    
    public int getHighScore() { return highScore; }
    public void updateHighScore() {
        int current = getScore();
        if (current > highScore) {
            highScore = current;
        }
    }

    public int getMovesCount() { return movesCount; }
    public void incrementMoves() { movesCount++; }

    public String getAIMode() { return aiMode; }
    public void setAIMode(String mode) { this.aiMode = mode; }

    public List<GamePoint> getCurrentPath() { return currentPath; }
    public void setCurrentPath(List<GamePoint> path) { this.currentPath = path; }

    public boolean isPathVisualized() { return pathVisualized; }
    public void setPathVisualized(boolean vis) { this.pathVisualized = vis; }
}
