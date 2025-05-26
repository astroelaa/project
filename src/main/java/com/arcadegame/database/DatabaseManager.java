package com.arcadegame.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.arcadegame.model.Player;
import com.arcadegame.model.GameSession;

public class DatabaseManager {
    
    private static final String DB_URL = "jdbc:sqlite:arcadegame.db";
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        // Private constructor for singleton pattern
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public void initDatabase() {
        try {
            // Establish connection
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection established.");
            
            // Create tables if they don't exist
            createTables();
            
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();
        
        // Create Players table
        statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        
        // Create Game Sessions table
        statement.execute("CREATE TABLE IF NOT EXISTS game_sessions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_id INTEGER NOT NULL," +
                "difficulty TEXT NOT NULL," +
                "score INTEGER NOT NULL," +
                "duration INTEGER NOT NULL," +
                "targets_hit INTEGER NOT NULL," +
                "total_targets INTEGER NOT NULL," +
                "completed BOOLEAN NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (player_id) REFERENCES players(id))");
                
        statement.close();
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    // Player-related methods
    
    public Player registerPlayer(String username, String password) throws SQLException {
        String sql = "INSERT INTO players (username, password) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // In a real app, this would be hashed
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new Player(id, username, password, new Timestamp(System.currentTimeMillis()).toString());
            }
        }
        
        return null;
    }
    
    public Player getPlayerByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM players WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1,
                    String.valueOf(username));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Player(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("created_at")
                );
            }
        }
        
        return null;
    }
    
    public List<Player> getTopPlayers(int limit) throws SQLException {
        String sql = "SELECT p.*, MAX(gs.score) as high_score " +
                    "FROM players p " +
                    "JOIN game_sessions gs ON p.id = gs.player_id " +
                    "GROUP BY p.id " +
                    "ORDER BY high_score DESC " +
                    "LIMIT ?";
        
        List<Player> topPlayers = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Player player = new Player(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("created_at")
                );
                topPlayers.add(player);
            }
        }
        
        return topPlayers;
    }
    
    // Game session methods
    
    public GameSession saveGameSession(GameSession session) throws SQLException {
        String sql = "INSERT INTO game_sessions (player_id, difficulty, score, duration, " +
                    "targets_hit, total_targets, completed) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, session.getPlayerId());
            pstmt.setString(2, session.getDifficulty());
            pstmt.setInt(3, session.getScore());
            pstmt.setInt(4, session.getDuration());
            pstmt.setInt(5, session.getTargetsHit());
            pstmt.setInt(6, session.getTotalTargets());
            pstmt.setBoolean(7, session.isCompleted());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                session.setId(rs.getInt(1));
                return session;
            }
        }
        
        return null;
    }
    
    public List<GameSession> getPlayerSessions(int playerId) throws SQLException {
        String sql = "SELECT * FROM game_sessions WHERE player_id = ? ORDER BY created_at DESC";
        
        List<GameSession> sessions = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                GameSession session = new GameSession(
                    rs.getInt("id"),
                    rs.getInt("player_id"),
                    rs.getString("difficulty"),
                    rs.getInt("score"),
                    rs.getInt("duration"),
                    rs.getInt("targets_hit"),
                    rs.getInt("total_targets"),
                    rs.getBoolean("completed"),
                    rs.getString("created_at")
                );
                sessions.add(session);
            }
        }
        
        return sessions;
    }
    public Player getPlayerById(int id) throws SQLException {
        String sql = "SELECT * FROM players WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Player(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("created_at")
                );
            }
        }

        return null;
    }

    public List<GameSession> getHighScores(String difficulty, int limit) throws SQLException {
        String sql = "SELECT * FROM game_sessions WHERE difficulty = ? AND completed = 1 " +
                    "ORDER BY score DESC LIMIT ?";
        
        List<GameSession> highScores = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, difficulty);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                GameSession session = new GameSession(
                    rs.getInt("id"),
                    rs.getInt("player_id"),
                    rs.getString("difficulty"),
                    rs.getInt("score"),
                    rs.getInt("duration"),
                    rs.getInt("targets_hit"),
                    rs.getInt("total_targets"),
                    rs.getBoolean("completed"),
                    rs.getString("created_at")
                );
                highScores.add(session);
            }
        }
        
        return highScores;
    }
}