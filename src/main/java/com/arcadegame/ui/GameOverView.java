package com.arcadegame.ui;

import com.arcadegame.game.GameView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.arcadegame.model.GameSession;
import com.arcadegame.model.Player;
import com.arcadegame.game.DifficultyLevel;

public class GameOverView extends BorderPane {
    
    private final Stage primaryStage;
    private final Player player;
    private final GameSession session;
    
    public GameOverView(Stage primaryStage, Player player, GameSession session) {
        this.primaryStage = primaryStage;
        this.player = player;
        this.session = session;
        
        // Set background
        setStyle("-fx-background-color: linear-gradient(to bottom, #1a2a3a, #0d1b2a);");
        
        // Create header
        Text gameOverText = new Text("Game Over");
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gameOverText.setFill(Color.WHITE);
        
        // Determine message based on completion
        Text resultText;
        if (session.isCompleted()) {
            resultText = new Text("Congratulations! Level Completed");
            resultText.setFill(Color.GREEN);
        } else {
            resultText = new Text("Time's up! Try again");
            resultText.setFill(Color.ORANGE);
        }
        resultText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        VBox headerBox = new VBox(10, gameOverText, resultText);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(30, 0, 20, 0));
        
        // Create stats container
        VBox statsBox = createStatsBox();
        
        // Create buttons
        Button playAgainButton = new Button("Play Again");
        playAgainButton.getStyleClass().add("game-button");
        playAgainButton.setOnAction(e -> playAgain());
        
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.getStyleClass().add("game-button");
        mainMenuButton.setOnAction(e -> returnToMenu());
        
        HBox buttonBox = new HBox(30, playAgainButton, mainMenuButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 30, 0));
        
        // Add all components to the main layout
        VBox mainContainer = new VBox(30, headerBox, statsBox, buttonBox);
        mainContainer.setAlignment(Pos.CENTER);
        
        setCenter(mainContainer);
    }
    
    private VBox createStatsBox() {
        // Create formatted labels
        Label scoreLabel = createStatsLabel("Final Score:", String.valueOf(session.getScore()));
        Label targetsLabel = createStatsLabel("Targets Hit:", 
            session.getTargetsHit() + "/" + session.getTotalTargets());
        Label accuracyLabel = createStatsLabel("Accuracy:", 
            calculateAccuracy(session.getTargetsHit(), session.getTotalTargets()) + "%");
        Label timeLabel = createStatsLabel("Time Played:", 
            formatTime(session.getDuration()));
        Label difficultyLabel = createStatsLabel("Difficulty:", 
            capitalizeFirstLetter(session.getDifficulty()));
        
        // Create stats container
        VBox statsBox = new VBox(15, scoreLabel, targetsLabel, accuracyLabel, timeLabel, difficultyLabel);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(25));
        statsBox.setStyle(
            "-fx-background-color: rgba(30, 45, 65, 0.8);" +
            "-fx-background-radius: 10;"
        );
        statsBox.setMaxWidth(500);
        
        return statsBox;
    }
    
    private Label createStatsLabel(String name, String value) {
        Label label = new Label(name + " " + value);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label.setTextFill(Color.WHITE);
        return label;
    }
    
    private String calculateAccuracy(int targetsHit, int totalTargets) {
        if (totalTargets == 0) return "0";
        double accuracy = (double) targetsHit / totalTargets * 100;
        return String.format("%.1f", accuracy);
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
    
    private void playAgain() {
        // Determine difficulty level
        DifficultyLevel difficulty = null;
        switch (session.getDifficulty()) {
            case "easy":
                difficulty = DifficultyLevel.EASY;
                break;
            case "medium":
                difficulty = DifficultyLevel.MEDIUM;
                break;
            case "hard":
                difficulty = DifficultyLevel.HARD;
                break;
        }
        
        if (difficulty != null) {
            GameView gameView = new GameView(primaryStage, player, difficulty);
            primaryStage.getScene().setRoot(gameView);
        } else {
            returnToMenu();
        }
    }
    
    private void returnToMenu() {
        GameMenuView menuView = new GameMenuView(primaryStage, player);
        primaryStage.getScene().setRoot(menuView);
    }
}