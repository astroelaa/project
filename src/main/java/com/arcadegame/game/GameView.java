package com.arcadegame.game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Random;

import com.arcadegame.database.DatabaseManager;
import com.arcadegame.model.GameSession;
import com.arcadegame.model.Player;
import com.arcadegame.ui.GameMenuView;
import com.arcadegame.ui.GameOverView;

public class GameView extends BorderPane {
    
    private final Stage primaryStage;
    private final Player player;
    private final DifficultyLevel difficulty;
    
    private final Pane gameArea;
    private final Label scoreLabel;
    private final Label timeLabel;
    private final Label targetsLabel;
    
    private int score;
    private int targetsHit;
    private int remainingTime;
    private boolean gameActive;
    
    private final Random random = new Random();
    private final Timeline gameTimer;
    private final Timeline targetSpawner;
    
    public GameView(Stage primaryStage, Player player, DifficultyLevel difficulty) {
        this.primaryStage = primaryStage;
        this.player = player;
        this.difficulty = difficulty;
        this.score = 0;
        this.targetsHit = 0;
        this.remainingTime = difficulty.getGameDuration();
        this.gameActive = true;
        
        // Set background
        setStyle("-fx-background-color: #121212;");
        
        // Create game header
        Label titleLabel = new Label("Target Hunter - " + difficulty.getName().toUpperCase());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        scoreLabel.setTextFill(Color.WHITE);
        
        timeLabel = new Label("Time: " + formatTime(remainingTime));
        timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        timeLabel.setTextFill(Color.WHITE);
        
        targetsLabel = new Label("Targets: 0/" + difficulty.getTotalTargets());
        targetsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        targetsLabel.setTextFill(Color.WHITE);
        
        Button quitButton = new Button("Quit Game");
        quitButton.setOnAction(e -> quitGame());
        
        HBox headerBox = new HBox(30, titleLabel, scoreLabel, timeLabel, targetsLabel, quitButton);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(15));
        headerBox.setStyle("-fx-background-color: #1a2a3a;");
        
        // Create game area
        gameArea = new Pane();
        gameArea.setPrefSize(1024, 668); // Leave space for header
        gameArea.setStyle("-fx-background-color: #121212;");
        
        // Set up layout
        setTop(headerBox);
        setCenter(gameArea);
        
        // Game timer (countdown)
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        gameTimer.setCycleCount(Animation.INDEFINITE);
        gameTimer.play();
        
        // Target spawner
        targetSpawner = new Timeline(
            new KeyFrame(Duration.millis(difficulty.getTargetDuration()), e -> spawnTarget())
        );
        targetSpawner.setCycleCount(Animation.INDEFINITE);
        targetSpawner.play();
        
        // Spawn initial target
        spawnTarget();
    }
    
    private void updateTimer() {
        remainingTime--;
        timeLabel.setText("Time: " + formatTime(remainingTime));
        
        if (remainingTime <= 0 || targetsHit >= difficulty.getTotalTargets()) {
            endGame();
        }
    }
    
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
    
    private void spawnTarget() {
        if (!gameActive) return;
        
        // Remove old targets
        gameArea.getChildren().removeIf(node -> node instanceof Circle);
        
        // Create new target
        int targetSize = difficulty.getTargetSize();
        double maxX = gameArea.getWidth() - targetSize * 2;
        double maxY = gameArea.getHeight() - targetSize * 2;
        
        // Ensure we have valid dimensions
        if (maxX <= 0) maxX = 500;
        if (maxY <= 0) maxY = 500;
        
        double x = targetSize + random.nextDouble() * maxX;
        double y = targetSize + random.nextDouble() * maxY;
        
        Circle target = new Circle(x, y, targetSize);
        target.setFill(Color.RED);
        
        // Add glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.ORANGERED);
        glow.setRadius(targetSize * 0.5);
        target.setEffect(glow);
        
        target.setOnMouseClicked(this::handleTargetClick);
        gameArea.getChildren().add(target);
    }
    
    private void handleTargetClick(MouseEvent event) {
        if (!gameActive) return;
        
        // Calculate score based on difficulty
        int points = 0;
        switch (difficulty) {
            case EASY:
                points = 10;
                break;
            case MEDIUM:
                points = 20;
                break;
            case HARD:
                points = 30;
                break;
        }
        
        score += points;
        targetsHit++;
        
        scoreLabel.setText("Score: " + score);
        targetsLabel.setText("Targets: " + targetsHit + "/" + difficulty.getTotalTargets());
        
        // Create hit effect
        Circle hitEffect = new Circle(
            ((Circle) event.getSource()).getCenterX(),
            ((Circle) event.getSource()).getCenterY(),
            difficulty.getTargetSize() * 1.5
        );
        hitEffect.setFill(Color.YELLOW);
        hitEffect.setOpacity(0.7);
        
        gameArea.getChildren().add(hitEffect);
        
        // Remove hit effect after animation
        Timeline hitAnimation = new Timeline(
            new KeyFrame(Duration.millis(200), e -> gameArea.getChildren().remove(hitEffect))
        );
        hitAnimation.play();
        
        // Remove the target and spawn a new one
        gameArea.getChildren().remove(event.getSource());
        spawnTarget();
        
        // Check if game is complete
        if (targetsHit >= difficulty.getTotalTargets()) {
            endGame();
        }
    }
    
    private void endGame() {
        gameActive = false;
        gameTimer.stop();
        targetSpawner.stop();
        
        // Clear all targets
        for (Node node : gameArea.getChildren()) {
            if (node instanceof Circle) {
                gameArea.getChildren().remove(node);
                break;
            }
        }
        
        // Save game session
        try {
            GameSession session = new GameSession(
                player.getId(),
                difficulty.getName(),
                score,
                difficulty.getGameDuration() - remainingTime,
                targetsHit,
                difficulty.getTotalTargets(),
                targetsHit >= difficulty.getTotalTargets()
            );
            
            DatabaseManager.getInstance().saveGameSession(session);
            
            // Show game over screen
            GameOverView gameOverView = new GameOverView(primaryStage, player, session);
            primaryStage.getScene().setRoot(gameOverView);
            
        } catch (SQLException e) {
            System.err.println("Error saving game session: " + e.getMessage());
            e.printStackTrace();
            
            // Return to menu if error
            quitGame();
        }
    }
    
    private void quitGame() {
        gameActive = false;
        gameTimer.stop();
        targetSpawner.stop();
        
        // Return to menu
        GameMenuView menuView = new GameMenuView(primaryStage, player);
        primaryStage.getScene().setRoot(menuView);
    }
}