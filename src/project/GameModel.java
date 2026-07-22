/**
 * ==============================================================================
 * Project: Smart Snake
 * Module: GameModel (MVC Game State Data Manager)
 * Authors:
 *   - Mohammad Sufiyan Aasim (sufiyanaasim@outlook.com / GitHub: SufiyanAasim)
 *   - Fahad Bin Nasir (fahadabbasi17025@gmail.com / GitHub: FahadBinNasir)
 * ==============================================================================
 */
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
    private boolean paused = false;
    private int score = 0;
    private int highScore = 0;
    private int movesCount = 0;
    private String borderMode = "Solid"; // "Solid" or "Wrap"

    // v7.0.0 features variables
    private String theme = "Cyberpunk"; // "Cyberpunk", "Vaporwave", "Matrix"
    private String foodType = "Normal"; // "Normal", "Golden", "Shield"
    private boolean hasShield = false;
    private boolean mapEditing = false;
    private boolean rivalActive = false;
    private final List<GamePoint> enemySnake = new ArrayList<>();
    private Direction enemyDirection = Direction.LEFT;

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
        paused = false;
        hasShield = false;
        foodType = "Normal";
        
        enemySnake.clear();
        if (rivalActive) {
            int startX = (width / cellSize - 4) * cellSize;
            int startY = (height / cellSize - 4) * cellSize;
            enemySnake.add(new GamePoint(startX, startY));
            enemySnake.add(new GamePoint(startX + cellSize, startY));
            enemySnake.add(new GamePoint(startX + cellSize * 2, startY));
            enemyDirection = Direction.LEFT;
        }
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

    public int getScore() { return Math.max(0, snake.size() - 1); }
    
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

    public String getBorderMode() { return borderMode; }
    public void setBorderMode(String mode) { this.borderMode = mode; }

    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    public boolean hasShield() { return hasShield; }
    public void setHasShield(boolean shield) { this.hasShield = shield; }

    public boolean isMapEditing() { return mapEditing; }
    public void setMapEditing(boolean editing) { this.mapEditing = editing; }

    public boolean isRivalActive() { return rivalActive; }
    public void setRivalActive(boolean active) { this.rivalActive = active; }

    public List<GamePoint> getEnemySnake() { return enemySnake; }

    public Direction getEnemyDirection() { return enemyDirection; }
    public void setEnemyDirection(Direction direction) { this.enemyDirection = direction; }
}
