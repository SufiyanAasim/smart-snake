package project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseManager {
    private static final String DEFAULT_DB_PATH = "data/scores.db";
    private final String dbUrl;

    public DatabaseManager() {
        String dbPath = DEFAULT_DB_PATH;
        
        // Attempt to load from .env config if available
        File envFile = new File(".env");
        if (envFile.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("DB_PATH=")) {
                        dbPath = line.substring(8).trim();
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading .env file: " + e.getMessage());
            }
        }

        // Ensure directories exist
        File dbFile = new File(dbPath);
        File parentDir = dbFile.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        this.dbUrl = "jdbc:sqlite:" + dbPath;
        initializeSchema();
    }

    private Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(dbUrl);
    }

    private void initializeSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS scores (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "player_name TEXT NOT NULL," +
                     "score INTEGER NOT NULL," +
                     "high_score INTEGER NOT NULL," +
                     "controller_mode TEXT NOT NULL," +
                     "moves_count INTEGER NOT NULL," +
                     "date_played TEXT NOT NULL" +
                     ");";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Schema initialization failed: " + e.getMessage());
        }
    }

    public void recordScore(String playerName, int score, int highScore, String mode, int moves) {
        String sql = "INSERT INTO scores(player_name, score, high_score, controller_mode, moves_count, date_played) VALUES(?,?,?,?,?,?)";
        
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerName.trim().isEmpty() ? "Guest" : playerName);
            pstmt.setInt(2, score);
            pstmt.setInt(3, highScore);
            pstmt.setString(4, mode);
            pstmt.setInt(5, moves);
            pstmt.setString(6, dateStr);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error recording score: " + e.getMessage());
        }
    }

    public List<ScoreRecord> fetchRecentScores(String filterName) {
        List<ScoreRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM scores ORDER BY id DESC";
        
        if (filterName != null && !filterName.trim().isEmpty()) {
            sql = "SELECT * FROM scores WHERE player_name LIKE ? ORDER BY id DESC";
        }

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (filterName != null && !filterName.trim().isEmpty()) {
                pstmt.setString(1, "%" + filterName.trim() + "%");
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ScoreRecord(
                    rs.getInt("id"),
                    rs.getString("player_name"),
                    rs.getInt("score"),
                    rs.getInt("high_score"),
                    rs.getString("controller_mode"),
                    rs.getInt("moves_count"),
                    rs.getString("date_played")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching scores: " + e.getMessage());
        }
        return list;
    }

    public void deleteScore(int id) {
        String sql = "DELETE FROM scores WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting score ID " + id + ": " + e.getMessage());
        }
    }

    public void clearAllScores() {
        String sql = "DELETE FROM scores";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error clearing scores: " + e.getMessage());
        }
    }

    public boolean exportToCSV(String filepath) {
        List<ScoreRecord> records = fetchRecentScores(null);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println("ID,Date,PlayerName,Score,HighScore,Mode,Steps");
            for (ScoreRecord r : records) {
                writer.printf("%d,%s,%s,%d,%d,%s,%d%n",
                    r.id(), r.datePlayed(), r.playerName(), r.score(), r.highScore(), r.mode(), r.movesCount()
                );
            }
            return true;
        } catch (IOException e) {
            System.err.println("Export to CSV failed: " + e.getMessage());
            return false;
        }
    }

    public record ScoreRecord(int id, String playerName, int score, int highScore, String mode, int movesCount, String datePlayed) {
    }
}
