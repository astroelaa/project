package com.arcadegame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.sql.SQLException;

import com.arcadegame.database.DatabaseManager;
import com.arcadegame.model.Player;

public class AuthView extends StackPane {
    
    private final Stage primaryStage;
    private final VBox loginContainer;
    private final VBox registerContainer;
    private final TextField loginUsername;
    private final PasswordField loginPassword;
    private final TextField registerUsername;
    private final PasswordField registerPassword;
    private final PasswordField confirmPassword;
    private final Label errorLabel;
    
    public AuthView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Set background
        setStyle("-fx-background-color: linear-gradient(to bottom, #1a2a3a, #0d1b2a);");
        
        // Create containers
        loginContainer = new VBox(15);
        registerContainer = new VBox(15);
        
        // Error label
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        
        // Set up login form
        loginUsername = new TextField();
        loginUsername.setPromptText("Username");
        loginUsername.getStyleClass().add("auth-input");
        
        loginPassword = new PasswordField();
        loginPassword.setPromptText("Password");
        loginPassword.getStyleClass().add("auth-input");
        
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("auth-button");
        loginButton.setOnAction(e -> handleLogin());
        
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register here");
        registerLink.setTextFill(Color.LIGHTBLUE);
        registerLink.setOnAction(e -> showRegisterForm());
        
        // Set up register form
        registerUsername = new TextField();
        registerUsername.setPromptText("Username");
        registerUsername.getStyleClass().add("auth-input");
        
        registerPassword = new PasswordField();
        registerPassword.setPromptText("Password");
        registerPassword.getStyleClass().add("auth-input");
        
        confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm Password");
        confirmPassword.getStyleClass().add("auth-input");
        
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("auth-button");
        registerButton.setOnAction(e -> handleRegister());
        
        Hyperlink loginLink = new Hyperlink("Already have an account? Login here");
        loginLink.setTextFill(Color.LIGHTBLUE);
        loginLink.setOnAction(e -> showLoginForm());
        
        // Build login container
        Text loginTitle = new Text("Login to Play");
        loginTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        loginTitle.setFill(Color.WHITE);
        
        loginContainer.getChildren().addAll(
            loginTitle,
            new Label("Username:"),
            loginUsername,
            new Label("Password:"),
            loginPassword,
            errorLabel,
            loginButton,
            registerLink
        );
        
        // Build register container
        Text registerTitle = new Text("Create an Account");
        registerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        registerTitle.setFill(Color.WHITE);
        
        registerContainer.getChildren().addAll(
            registerTitle,
            new Label("Username:"),
            registerUsername,
            new Label("Password:"),
            registerPassword,
            new Label("Confirm Password:"),
            confirmPassword,
            errorLabel,
            registerButton,
            loginLink
        );
        
        // Style the containers
        loginContainer.setStyle("-fx-background-color: rgba(30, 45, 65, 0.8); -fx-background-radius: 10;");
        loginContainer.setPadding(new Insets(30));
        loginContainer.setMaxWidth(400);
        loginContainer.setAlignment(Pos.CENTER);
        
        registerContainer.setStyle("-fx-background-color: rgba(30, 45, 65, 0.8); -fx-background-radius: 10;");
        registerContainer.setPadding(new Insets(30));
        registerContainer.setMaxWidth(400);
        registerContainer.setAlignment(Pos.CENTER);
        registerContainer.setVisible(false);
        
        // Add containers to the main pane
        getChildren().addAll(loginContainer, registerContainer);
        setAlignment(Pos.CENTER);
    }
    
    private void showLoginForm() {
        errorLabel.setVisible(false);
        registerContainer.setVisible(false);
        loginContainer.setVisible(true);
    }
    
    private void showRegisterForm() {
        errorLabel.setVisible(false);
        loginContainer.setVisible(false);
        registerContainer.setVisible(true);
    }
    
    private void handleLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();
        
        // Input validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        
        try {
            Player player = DatabaseManager.getInstance().getPlayerByUsername(username);
            
            if (player == null) {
                showError("Player not found");
                return;
            }
            
            if (!player.getPassword().equals(password)) {
                showError("Invalid password");
                return;
            }
            
            // Successful login
            showGameMenu(player);
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }
    
    private void handleRegister() {
        String username = registerUsername.getText().trim();
        String password = registerPassword.getText();
        String confirm = confirmPassword.getText();
        
        // Input validation
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirm)) {
            showError("Passwords do not match");
            return;
        }
        
        if (username.length() < 3) {
            showError("Username must be at least 3 characters");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }
        
        try {
            // Check if username already exists
            Player existingPlayer = DatabaseManager.getInstance().getPlayerByUsername(username);
            if (existingPlayer != null) {
                showError("Username already taken");
                return;
            }
            
            // Register new player
            Player newPlayer = DatabaseManager.getInstance().registerPlayer(username, password);
            
            if (newPlayer != null) {
                showGameMenu(newPlayer);
            } else {
                showError("Registration failed");
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void showGameMenu(Player player) {
        GameMenuView gameMenu = new GameMenuView(primaryStage, player);
        primaryStage.getScene().setRoot(gameMenu);
    }
}