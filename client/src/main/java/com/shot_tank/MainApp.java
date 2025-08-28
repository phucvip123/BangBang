package com.shot_tank;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Character.fxml"));

        Scene scene = new Scene(root,800,600);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("Game Shoot Tank");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml, int w, int h) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/" + fxml));
        Scene scene = new Scene(loader.load(), w, h);
        primaryStage.setScene(scene);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
