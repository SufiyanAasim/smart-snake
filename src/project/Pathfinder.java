package project;

import java.util.*;

public class Pathfinder {
    private final int width;
    private final int height;
    private final int cellSize;

    public Pathfinder(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
    }

    private static class Node implements Comparable<Node> {
        GamePoint point;
        int g; // cost from start
        int h; // heuristic (Manhattan distance)
        int f; // g + h
        Node parent;

        Node(GamePoint point, int g, int h, Node parent) {
            this.point = point;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
    }

    // A* implementation
    public List<GamePoint> findPath(GamePoint start, GamePoint target, List<GamePoint> snake, List<GamePoint> obstacles) {
        if (start.equals(target)) {
            return new ArrayList<>();
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<GamePoint> closedSet = new HashSet<>();
        
        Set<GamePoint> blocked = new HashSet<>(snake);
        if (obstacles != null) {
            blocked.addAll(obstacles);
        }
        
        // Remove the tail from blocked since it will move on the next step (if snake is not growing)
        if (!snake.isEmpty()) {
            blocked.remove(snake.getLast());
        }

        Node startNode = new Node(start, 0, getManhattanDistance(start, target), null);
        openSet.add(startNode);

        Map<GamePoint, Integer> gScores = new HashMap<>();
        gScores.put(start, 0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.point.equals(target)) {
                return reconstructPath(current);
            }

            closedSet.add(current.point);

            for (GamePoint neighborPoint : getNeighbors(current.point, blocked)) {
                if (closedSet.contains(neighborPoint)) {
                    continue;
                }

                int tentativeG = current.g + cellSize;
                int currentG = gScores.getOrDefault(neighborPoint, Integer.MAX_VALUE);

                if (tentativeG < currentG) {
                    gScores.put(neighborPoint, tentativeG);
                    Node neighborNode = new Node(neighborPoint, tentativeG, getManhattanDistance(neighborPoint, target), current);
                    openSet.removeIf(node -> node.point.equals(neighborPoint));
                    openSet.add(neighborNode);
                }
            }
        }

        return null; // No path found
    }

    // BFS Fallback implementation (finds path to tail or any open cell maximizing free space)
    public List<GamePoint> findSafetyPath(GamePoint start, List<GamePoint> snake, List<GamePoint> obstacles) {
        Queue<Node> queue = new LinkedList<>();
        Set<GamePoint> visited = new HashSet<>();
        
        Set<GamePoint> blocked = new HashSet<>(snake);
        if (obstacles != null) {
            blocked.addAll(obstacles);
        }

        GamePoint tail = snake.isEmpty() ? start : snake.getLast();
        blocked.remove(tail); // Tail is our target safety point

        queue.add(new Node(start, 0, 0, null));
        visited.add(start);

        List<GamePoint> longestPath = new ArrayList<>();
        Node bestNode = null;
        int maxEmptyCells = -1;

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // If we found a path to the tail, return it immediately as it guarantees a loop
            if (current.point.equals(tail)) {
                return reconstructPath(current);
            }

            List<GamePoint> neighbors = getNeighbors(current.point, blocked);
            
            // Track the node that has the most space available around it
            if (neighbors.size() > maxEmptyCells) {
                maxEmptyCells = neighbors.size();
                bestNode = current;
            }

            for (GamePoint neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(new Node(neighbor, current.g + 1, 0, current));
                }
            }
        }

        // If we couldn't reach the tail, steer towards the node that has the most open neighbors
        if (bestNode != null) {
            return reconstructPath(bestNode);
        }

        return null; // Completely trapped
    }

    private List<GamePoint> reconstructPath(Node node) {
        List<GamePoint> path = new ArrayList<>();
        Node current = node;
        while (current.parent != null) {
            path.add(0, current.point);
            current = current.parent;
        }
        return path;
    }

    private List<GamePoint> getNeighbors(GamePoint point, Set<GamePoint> blocked) {
        List<GamePoint> neighbors = new ArrayList<>();
        int[] dx = {0, 0, -cellSize, cellSize};
        int[] dy = {-cellSize, cellSize, 0, 0};

        for (int i = 0; i < 4; i++) {
            int nx = point.x() + dx[i];
            int ny = point.y() + dy[i];
            GamePoint neighbor = new GamePoint(nx, ny);

            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                if (!blocked.contains(neighbor)) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    private int getManhattanDistance(GamePoint a, GamePoint b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
