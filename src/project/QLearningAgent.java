package project;

import java.io.*;
import java.util.*;

public class QLearningAgent {
    private final float[][] qTable;
    private final Random random = new Random();

    // Learning parameters
    private float alpha = 0.1f; // Learning rate
    private float gamma = 0.9f; // Discount factor
    private float epsilon = 0.1f; // Exploration rate (default for gameplay)

    public QLearningAgent() {
        this.qTable = new float[128][3]; // 128 states, 3 actions
    }

    public void setEpsilon(float epsilon) {
        this.epsilon = epsilon;
    }

    public float getEpsilon() {
        return epsilon;
    }

    // Epsilon-greedy action selection
    public int getAction(int state, boolean training) {
        if (training && random.nextFloat() < epsilon) {
            return random.nextInt(3); // Explore
        }
        
        // Exploit: find max Q action
        float maxQ = -Float.MAX_VALUE;
        int bestAction = 0;
        for (int i = 0; i < 3; i++) {
            if (qTable[state][i] > maxQ) {
                maxQ = qTable[state][i];
                bestAction = i;
            }
        }
        return bestAction;
    }

    // Q-value update
    public void update(int state, int action, int nextState, float reward) {
        float maxNextQ = -Float.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            if (qTable[nextState][i] > maxNextQ) {
                maxNextQ = qTable[nextState][i];
            }
        }
        qTable[state][action] = qTable[state][action] + alpha * (reward + gamma * maxNextQ - qTable[state][action]);
    }

    // Translate game grid parameters into a state index [0 - 127]
    public int getState(GamePoint head, Direction currentDir, GamePoint food, Set<GamePoint> blocked, int width, int height, int cellSize) {
        // 1. Danger States relative to current heading
        boolean dangerStraight = isColliding(getNeighborInDir(head, currentDir, cellSize), blocked, width, height, cellSize);
        boolean dangerLeft = isColliding(getNeighborInDir(head, getTurnDirection(currentDir, 1), cellSize), blocked, width, height, cellSize);
        boolean dangerRight = isColliding(getNeighborInDir(head, getTurnDirection(currentDir, 2), cellSize), blocked, width, height, cellSize);

        int dangerBits = (dangerStraight ? 4 : 0) | (dangerLeft ? 2 : 0) | (dangerRight ? 1 : 0);

        // 2. Relative Food Position
        boolean foodUp = food.y() < head.y();
        boolean foodDown = food.y() > head.y();
        boolean foodLeft = food.x() < head.x();
        boolean foodRight = food.x() > head.x();

        int foodBits = (foodUp ? 8 : 0) | (foodDown ? 4 : 0) | (foodLeft ? 2 : 0) | (foodRight ? 1 : 0);

        // Combine: 3 bits of danger (0-7) left-shifted by 4, plus 4 bits of food (0-15)
        return (dangerBits << 4) | foodBits;
    }

    private GamePoint getNeighborInDir(GamePoint head, Direction dir, int cellSize) {
        return switch (dir) {
            case UP -> new GamePoint(head.x(), head.y() - cellSize);
            case DOWN -> new GamePoint(head.x(), head.y() + cellSize);
            case LEFT -> new GamePoint(head.x() - cellSize, head.y());
            case RIGHT -> new GamePoint(head.x() + cellSize, head.y());
        };
    }

    // Relative turns: 1 = Left turn, 2 = Right turn
    public Direction getTurnDirection(Direction current, int relativeAction) {
        if (relativeAction == 0) return current; // Go straight
        
        return switch (current) {
            case UP -> (relativeAction == 1) ? Direction.LEFT : Direction.RIGHT;
            case DOWN -> (relativeAction == 1) ? Direction.RIGHT : Direction.LEFT;
            case LEFT -> (relativeAction == 1) ? Direction.DOWN : Direction.UP;
            case RIGHT -> (relativeAction == 1) ? Direction.UP : Direction.DOWN;
        };
    }

    private boolean isColliding(GamePoint p, Set<GamePoint> blocked, int width, int height, int cellSize) {
        if (p.x() < 0 || p.x() >= width || p.y() < 0 || p.y() >= height) {
            return true;
        }
        return blocked.contains(p);
    }

    // Save/Load persistence
    public void saveQTable(String filepath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            for (int s = 0; s < 128; s++) {
                writer.printf("%d:%.4f,%.4f,%.4f%n", s, qTable[s][0], qTable[s][1], qTable[s][2]);
            }
        } catch (IOException e) {
            System.err.println("Could not save Q-table: " + e.getMessage());
        }
    }

    public void loadQTable(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    int state = Integer.parseInt(parts[0]);
                    String[] vals = parts[1].split(",");
                    if (vals.length == 3 && state >= 0 && state < 128) {
                        qTable[state][0] = Float.parseFloat(vals[0]);
                        qTable[state][1] = Float.parseFloat(vals[1]);
                        qTable[state][2] = Float.parseFloat(vals[2]);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Could not load Q-table: " + e.getMessage());
        }
    }
}
