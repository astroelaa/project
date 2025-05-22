package com.arcadegame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

import com.arcadegame.database.DatabaseManager;
import com.arcadegame.model.GameSession;
import com.arcadegame.model.Player;

public class ProfileView extends BorderPane {
    
    private final Stage primaryStage;
    private final Player player;
    
    public ProfileView(Stage primaryStage, Player player) {
        this.primaryStage = primaryStage;
        this.player = player;
        
        // Set background
        setStyle("-fx-background-color: linear-gradient(to bottom, #1a2a3a, #0d1b2a);");
        
        // Create header
        Text profileText = new Text("Player Profile");
        profileText.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        profileText.setFill(Color.WHITE);
        
        Text usernameText = new Text(player.getUsername());
        usernameText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        usernameText.setFill(Color.LIGHTBLUE);
        
        Text memberSinceText = new Text("Member since: " + player.getCreatedAt().split(" ")[0]);
        memberSinceText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        memberSinceText.setFill(Color.LIGHTGRAY);
        
        VBox headerBox = new VBox(10, profileText, usernameText, memberSinceText);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(30, 0, 20, 0));
        
        // Create stats and game history
        VBox statsBox = createStatsBox();
        VBox historyBox = createHistoryBox();
        
        // Back button
        Button backButton = new Button("Back to Menu");
        backButton.getStyleClass().add("menu-button");
        backButton.setOnAction(e -> returnToMenu());
        
        HBox buttonBox = new HBox(backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 30, 0));
        
        // Add all components to the main layout
        VBox mainContainer = new VBox(30, headerBox, statsBox, historyBox, buttonBox);
        mainContainer.setAlignment(Pos.CENTER);
        
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        setCenter(scrollPane);
    }
    
    private VBox createStatsBox() {
        Text statsTitle = new Text("Player Statistics");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        statsTitle.setFill(Color.WHITE);
        
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(10);
        statsGrid.setPadding(new Insets(20));
        
        try {
            List<GameSession> playerSessions = DatabaseManager.getInstance().getPlayerSessions(player.getId());
            
            // Calculate statistics
            int totalGames = playerSessions.size();
            int totalScore = 0;
            int totalTargetsHit = 0;
            int totalTargets = 0;
            int easyGames = 0;
            int mediumGames = 0;
            int hardGames = 0;
            int gamesCompleted = 0;
            
            for (GameSession session : playerSessions) {
                totalScore += session.getScore();
                totalTargetsHit += session.getTargetsHit();
                totalTargets += session.getTotalTargets();
                
                if (session.isCompleted()) {
                    gamesCompleted++;
                }
                
                switch (session.getDifficulty()) {
                    case "easy":
                        easyGames++;
                        break;
                    case "medium":
                        mediumGames++;
                        break;
                    case "hard":
                        hardGames++;
                        break;
                }
            }
            
            // Add statistics to grid
            statsGrid.add(createStatLabel("Total Games:"), 0, 0);
            statsGrid.add(createStatValue(String.valueOf(totalGames)), 1, 0);
            
            statsGrid.add(createStatLabel("Games Completed:"), 0, 1);
            statsGrid.add(createStatValue(gamesCompleted + " (" + calculatePercentage(gamesCompleted, totalGames) + "%)"), 1, 1);
            
            statsGrid.add(createStatLabel("Total Score:"), 0, 2);
            statsGrid.add(createStatValue(String.valueOf(totalScore)), 1, 2);
            
            statsGrid.add(createStatLabel("Accuracy:"), 0, 3);
            statsGrid.add(createStatValue(calculatePercentage(totalTargetsHit, totalTargets) + "%"), 1, 3);
            
            statsGrid.add(createStatLabel("Easy Games:"), 2, 0);
            statsGrid.add(createStatValue(String.valueOf(easyGames)), 3, 0);
            
            statsGrid.add(createStatLabel("Medium Games:"), 2, 1);
            statsGrid.add(createStatValue(String.valueOf(mediumGames)), 3, 1);
            
            statsGrid.add(createStatLabel("Hard Games:"), 2, 2);
            statsGrid.add(createStatValue(String.valueOf(hardGames)), 3, 2);
            
        } catch (SQLException e) {
            System.err.println("Error loading player statistics: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message
            Label errorLabel = new Label("Could not load statistics. Database error.");
            errorLabel.setTextFill(Color.RED);
            statsGrid.add(errorLabel, 0, 0, 2, 1);
        }
        
        VBox statsBox = new VBox(15, statsTitle, statsGrid);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(25));
        statsBox.setStyle(
            "-fx-background-color: rgba(30, 45, 65, 0.8);" +
            "-fx-background-radius: 10;"
        );
        statsBox.setMaxWidth(700);
        
        return statsBox;
    }
    
    private VBox createHistoryBox() {
        Text historyTitle = new Text("Recent Games");
        historyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        historyTitle.setFill(Color.WHITE);
        
        VBox historyList = new VBox(10);
        
        try {
            List<GameSession> playerSessions = DatabaseManager.getInstance().getPlayerSessions(player.getId());
            
            if (playerSessions.isEmpty()) {
                Label noGamesLabel = new Label("No game history yet. Start playing to see your results here!");
                noGamesLabel.setTextFill(Color.LIGHTGRAY);
                historyList.getChildren().add(noGamesLabel);
            } else {
                // Create headers
                HBox headerRow = new HBox(20);
                headerRow.getChildren().addAll(
                    createHistoryHeaderLabel("Date"),
                    createHistoryHeaderLabel("Difficulty"),
                    createHistoryHeaderLabel("Score"),
                    createHistoryHeaderLabel("Targets"),
                    createHistoryHeaderLabel("Time"),
                    createHistoryHeaderLabel("Status")
                );
                historyList.getChildren().add(headerRow);
                
                // Add game sessions (latest first, limited to 10)
                int count = 0;
                for (GameSession session : playerSessions) {
                    if (count++ >= 10) break;
                    
                    HBox sessionRow = new HBox(20);
                    
                    // Format date
                    String date = session.getCreatedAt().split(" ")[0];
                    
                    // Determine status
                    String status = session.isCompleted() ? "Completed" : "Time's up";
                    Color statusColor = session.isCompleted() ? Color.GREEN : Color.ORANGE;
                    
                    sessionRow.getChildren().addAll(
                        createHistoryValueLabel(date),
                        createHistoryValueLabel(capitalizeFirstLetter(session.getDifficulty())),
                        createHistoryValueLabel(String.valueOf(session.getScore())),
                        createHistoryValueLabel(session.getTargetsHit() + "/" + session.getTotalTargets()),
                        createHistoryValueLabel(formatTime(session.getDuration())),
                        createHistoryValueLabel(status, statusColor)
                    );
                    
                    historyList.getChildren().add(sessionRow);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading game history: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message
            Label errorLabel = new Label("Could not load game history. Database error.");
            errorLabel.setTextFill(Color.RED);
            historyList.getChildren().add(errorLabel);
        }
        
        VBox historyBox = new VBox(15, historyTitle, historyList);
        historyBox.setAlignment(Pos.CENTER);
        historyBox.setPadding(new Insets(25));
        historyBox.setStyle(
            "-fx-background-color: rgba(30, 45, 65, 0.8);" +
            "-fx-background-radius: 10;"
        );
        historyBox.setMaxWidth(700);
        
        return historyBox;
    }
    
    private Label createStatLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.LIGHTGRAY);
        return label;
    }
    
    private Label createStatValue(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.WHITE);
        return label;
    }
    
    private Label createHistoryHeaderLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.LIGHTBLUE);
        label.setPrefWidth(100);
        return label;
    }
    
    private Label createHistoryValueLabel(String text) {
        return createHistoryValueLabel(text, Color.WHITE);
    }
    
    private Label createHistoryValueLabel(String text, Color color) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setTextFill(color);
        label.setPrefWidth(100);
        return label;
    }
    
    private String calculatePercentage(int part, int total) {
        if (total == 0) return "0";
        return String.format("%.1f", (double) part / total * 100);
    }
    
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
    
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    private void returnToMenu() {
        GameMenuView menuView = new GameMenuView(primaryStage, player);
        primaryStage.getScene().setRoot(menuView);
    }
}