package com.arcadegame.ui;

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

import com.arcadegame.model.Player;
import com.arcadegame.game.DifficultyLevel;
import com.arcadegame.game.GameView;

public class GameMenuView extends BorderPane {
    
    private final Stage primaryStage;
    private final Player player;
    
    public GameMenuView(Stage primaryStage, Player player) {
        this.primaryStage = primaryStage;
        this.player = player;
        
        // Set background
        setStyle("-fx-background-color: linear-gradient(to bottom, #1a2a3a, #0d1b2a);");
        
        // Create welcome header
        Text welcomeText = new Text("Welcome, " + player.getUsername() + "!");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeText.setFill(Color.WHITE);
        
        Text subTitle = new Text("Choose your difficulty level to start the game");
        subTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subTitle.setFill(Color.LIGHTGRAY);
        
        VBox headerBox = new VBox(10, welcomeText, subTitle);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(30, 0, 20, 0));
        
        // Create difficulty buttons
        Button easyButton = createDifficultyButton("Easy", "#4CAF50", DifficultyLevel.EASY);
        Button mediumButton = createDifficultyButton("Medium", "#FFC107", DifficultyLevel.MEDIUM);
        Button hardButton = createDifficultyButton("Hard", "#F44336", DifficultyLevel.HARD);
        
        // Add descriptions
        VBox easyBox = createDifficultyBox(easyButton, 
            "• Larger targets\n• Slower movement\n• 30 seconds\n• 20 targets");
            
        VBox mediumBox = createDifficultyBox(mediumButton, 
            "• Medium targets\n• Moderate speed\n• 45 seconds\n• 30 targets");
            
        VBox hardBox = createDifficultyBox(hardButton, 
            "• Small targets\n• Fast movement\n• 60 seconds\n• 40 targets");
        
        // Create difficulty selection container
        HBox difficultyContainer = new HBox(30, easyBox, mediumBox, hardBox);
        difficultyContainer.setAlignment(Pos.CENTER);
        difficultyContainer.setPadding(new Insets(20));
        
        // Profile and highscores buttons
        Button profileButton = new Button("My Profile");
        profileButton.getStyleClass().add("menu-button");
        profileButton.setOnAction(e -> showProfile());
        
        Button leaderboardButton = new Button("Leaderboard");
        leaderboardButton.getStyleClass().add("menu-button");
        leaderboardButton.setOnAction(e -> showLeaderboard());
        
        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("menu-button");
        logoutButton.setOnAction(e -> logout());
        
        HBox menuButtonsContainer = new HBox(20, profileButton, leaderboardButton, logoutButton);
        menuButtonsContainer.setAlignment(Pos.CENTER);
        menuButtonsContainer.setPadding(new Insets(20, 0, 30, 0));
        
        // Add all components to the main layout
        VBox mainContainer = new VBox(30, headerBox, difficultyContainer, menuButtonsContainer);
        mainContainer.setAlignment(Pos.CENTER);
        
        setCenter(mainContainer);
    }
    
    private Button createDifficultyButton(String text, String color, DifficultyLevel difficulty) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 15 30;" +
            "-fx-background-radius: 5;"
        );
        button.setPrefWidth(200);
        button.setOnAction(e -> startGame(difficulty));
        
        // Add hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: derive(" + color + ", 20%);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 15 30;" +
                "-fx-background-radius: 5;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 15 30;" +
                "-fx-background-radius: 5;"
            )
        );
        
        return button;
    }
    
    private VBox createDifficultyBox(Button button, String description) {
        Label descLabel = new Label(description);
        descLabel.setTextFill(Color.LIGHTGRAY);
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        
        VBox box = new VBox(15, button, descLabel);
        box.setAlignment(Pos.CENTER);
        box.setStyle(
            "-fx-background-color: rgba(30, 45, 65, 0.5);" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 20;"
        );
        box.setPrefWidth(250);
        box.setPrefHeight(250);
        
        return box;
    }
    
    private void startGame(DifficultyLevel difficulty) {
        GameView gameView = new GameView(primaryStage, player, difficulty);
        primaryStage.getScene().setRoot(gameView);
    }
    
    private void showProfile() {
        ProfileView profileView = new ProfileView(primaryStage, player);
        primaryStage.getScene().setRoot(profileView);
    }
    
    private void showLeaderboard() {
        LeaderboardView leaderboardView = new LeaderboardView(primaryStage, player);
        primaryStage.getScene().setRoot(leaderboardView);
    }
    
    private void logout() {
        AuthView authView = new AuthView(primaryStage);
        primaryStage.getScene().setRoot(authView);
    }
}