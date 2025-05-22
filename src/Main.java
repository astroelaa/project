import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import database.DatabaseManager;
import ui.AuthView;

public class Main extends Application {
    
    private static final String GAME_TITLE = "Target Hunter";
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize database
        DatabaseManager.getInstance().initDatabase();
        
        // Create the auth view (login/register)
        AuthView authView = new AuthView(primaryStage);
        
        // Set up the primary stage
        Scene scene = new Scene(authView, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        
        primaryStage.setTitle(GAME_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    @Override
    public void stop() {
        // Close database connection when application stops
        DatabaseManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}