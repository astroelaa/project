package com.arcadegame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
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

public class LeaderboardView extends BorderPane {
    
    private final Stage primaryStage;
    private final Player player;
    private final VBox leaderboardList;
    private String currentDifficulty = "easy";
    
    public LeaderboardView(Stage primaryStage, Player player) {
        this.primaryStage = primaryStage;
        this.player = player;
        
        // Set background
        setStyle("-fx-background-color: linear-gradient(to bottom, #1a2a3a, #0d1b2a);");
        
        // Create header
        Text leaderboardText = new Text("Leaderboard");
        leaderboardText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        leaderboardText.setFill(Color.WHITE);
        
        // Difficulty selector
        ComboBox<String> difficultySelector = new ComboBox<>();
        difficultySelector.getItems().addAll("Easy", "Medium", "Hard");
        difficultySelector.setValue("Easy");
        difficultySelector.setOnAction(e -> {
            currentDifficulty = difficultySelector.getValue().toLowerCase();
            updateLeaderboard();
        });
        
        HBox selectorBox = new HBox(15, new Label("Difficulty:"), difficultySelector);
        selectorBox.setAlignment(Pos.CENTER);
        selectorBox.setPadding(new Insets(10));
        
        VBox headerBox = new VBox(10, leaderboardText, selectorBox);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(30, 0, 20, 0));
        
        // Create leaderboard list
        leaderboardList = new VBox(10);
        leaderboardList.setAlignment(Pos.CENTER);
        leaderboardList.setPadding(new Insets(20));
        
        // Initial load of leaderboard
        updateLeaderboard();
        
        // Create leaderboard container
        VBox leaderboardBox = new VBox(15, leaderboardList);
        leaderboardBox.setAlignment(Pos.CENTER);
        leaderboardBox.setPadding(new Insets(25));
        leaderboardBox.setStyle(
            "-fx-background-color: rgba(30, 45, 65, 0.8);" +
            "-fx-background-radius: 10;"
        );
        leaderboardBox.setMaxWidth(700);
        
        // Back button
        Button backButton = new Button("Back to Menu");
        backButton.getStyleClass().add("menu-button");
        backButton.setOnAction(e -> returnToMenu());
        
        HBox buttonBox = new HBox(backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 30, 0));
        
        // Add all components to the main layout
        VBox mainContainer = new VBox(30, headerBox, leaderboardBox, buttonBox);
        mainContainer.setAlignment(Pos.CENTER);
        
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        setCenter(scrollPane);
    }
    
    private void updateLeaderboard() {
        leaderboardList.getChildren().clear();
        
        // Create headers
        HBox headerRow = new HBox(20);
        headerRow.getChildren().addAll(
            createLeaderboardHeaderLabel("Rank"),
            createLeaderboardHeaderLabel("Player"),
            createLeaderboardHeaderLabel("Score"),
            createLeaderboardHeaderLabel("Targets"),
            createLeaderboardHeaderLabel("Time"),
            createLeaderboardHeaderLabel("Date")
        );
        leaderboardList.getChildren().add(headerRow);
        
        try {
            List<GameSession> highScores = DatabaseManager.getInstance().getHighScores(currentDifficulty, 10);
            
            if (highScores.isEmpty()) {
                Label noScoresLabel = new Label("No scores recorded for this difficulty level yet!");
                noScoresLabel.setTextFill(Color.LIGHTGRAY);
                leaderboardList.getChildren().add(noScoresLabel);
            } else {
                // Add top scores
                for (int i = 0; i < highScores.size(); i++) {
                    GameSession session = highScores.get(i);
                    
                    // Get player info
                    Player scorePlayer = DatabaseManager.getInstance().getPlayerByUsername(String.valueOf(session.getPlayerId()));
                    String playerName = scorePlayer != null ? scorePlayer.getUsername() : "Unknown";
                    
                    // Highlight current player
                    boolean isCurrentPlayer = scorePlayer != null && scorePlayer.getId() == player.getId();
                    
                    // Format date
                    String date = session.getCreatedAt().split(" ")[0];
                    
                    HBox scoreRow = new HBox(20);
                    
                    // Add rank with medal for top 3
                    String rankText = String.valueOf(i + 1);
                    if (i == 0) rankText = "🥇 " + rankText;
                    else if (i == 1) rankText = "🥈 " + rankText;
                    else if (i == 2) rankText = "🥉 " + rankText;
                    
                    scoreRow.getChildren().addAll(
                        createLeaderboardValueLabel(rankText),
                        createLeaderboardValueLabel(playerName, isCurrentPlayer ? Color.GOLD : Color.WHITE),
                        createLeaderboardValueLabel(String.valueOf(session.getScore())),
                        createLeaderboardValueLabel(session.getTargetsHit() + "/" + session.getTotalTargets()),
                        createLeaderboardValueLabel(formatTime(session.getDuration())),
                        createLeaderboardValueLabel(date)
                    );
                    
                    // Highlight row for current player
                    if (isCurrentPlayer) {
                        scoreRow.setStyle("-fx-background-color: rgba(255, 215, 0, 0.2); -fx-background-radius: 5;");
                    }
                    
                    leaderboardList.getChildren().add(scoreRow);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading leaderboard: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message
            Label errorLabel = new Label("Could not load leaderboard. Database error.");
            errorLabel.setTextFill(Color.RED);
            leaderboardList.getChildren().add(errorLabel);
        }
    }
    
    private Label createLeaderboardHeaderLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.LIGHTBLUE);
        label.setPrefWidth(100);
        return label;
    }
    
    private Label createLeaderboardValueLabel(String text) {
        return createLeaderboardValueLabel(text, Color.WHITE);
    }
    
    private Label createLeaderboardValueLabel(String text, Color color) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        label.setTextFill(color);
        label.setPrefWidth(100);
        return label;
    }
    
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
    
    private void returnToMenu() {
        GameMenuView menuView = new GameMenuView(primaryStage, player);
        primaryStage.getScene().setRoot(menuView);
    }
}