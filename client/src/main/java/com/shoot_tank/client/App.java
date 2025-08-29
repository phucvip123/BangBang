package com.shoot_tank.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.shoot_tank.client.network.Session;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Character.fxml"));

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("Game Shoot Tank");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        stage.setOnCloseRequest(event -> {
            try {
                if (Session.mySession != null)
                    Session.mySession.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        });
    }

    public static void setRoot(String fxml, int w, int h) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + fxml));
        Scene scene = new Scene(loader.load(), w, h);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }

}