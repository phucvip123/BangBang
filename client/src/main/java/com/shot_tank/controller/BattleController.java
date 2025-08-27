package com.shot_tank.controller;

import java.net.URL;
import java.util.*;
import com.shot_tank.models.Player;
import com.shot_tank.models.Tank;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class BattleController implements Initializable {
    public static final double MAP_WIDTH = 1600;
    public static final double MAP_HEIGHT = 1600;

    @FXML
    private Pane gamePane;

    private Tank playerTank;
    private Rectangle mapBorder;
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private final List<Circle> bullets = new ArrayList<>();
    private double mouseX, mouseY;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Rectangle mapBackground = new Rectangle(0, 0, MAP_WIDTH, MAP_HEIGHT);
        mapBackground.setFill(new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#2c3e50")),
            new Stop(1, Color.web("#27ae60"))
        ));
        gamePane.getChildren().add(mapBackground);

        mapBorder = new Rectangle(0, 0, MAP_WIDTH, MAP_HEIGHT);
        mapBorder.setFill(Color.TRANSPARENT);
        mapBorder.setStroke(Color.WHITE);
        mapBorder.setStrokeWidth(6);
        mapBorder.setArcWidth(30);
        mapBorder.setArcHeight(30);
        gamePane.getChildren().add(mapBorder);

        playerTank = new Tank(gamePane, Player.myChar().location.x, Player.myChar().location.x, Player.myChar().size,
                Player.myChar().name, Player.myChar().hp, Player.myChar().hpMax);
        Player.myChar().tank = playerTank;

        gamePane.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        gamePane.setOnKeyPressed(e -> keysPressed.add(e.getCode()));
        gamePane.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));
        gamePane.setFocusTraversable(true);

        gamePane.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                shoot();
            }
        });

        Platform.runLater(() -> gamePane.requestFocus());

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveTank();
                playerTank.rotateBarrel(mouseX, mouseY);
                updateBullets();
                updateCamera();
            }
        };
        timer.start();
    }

    private void updateCamera() {
        double centerX = playerTank.getCenterX();
        double centerY = playerTank.getCenterY();

        double paneWidth = gamePane.getWidth();
        double paneHeight = gamePane.getHeight();

        double offsetX = paneWidth / 2 - centerX;
        double offsetY = paneHeight / 2 - centerY;

        offsetX = Math.min(0, Math.max(offsetX, paneWidth - MAP_WIDTH));
        offsetY = Math.min(0, Math.max(offsetY, paneHeight - MAP_HEIGHT));

        gamePane.setTranslateX(offsetX);
        gamePane.setTranslateY(offsetY);
    }

    private void moveTank() {
        double dx = 0, dy = 0;

        if (keysPressed.contains(KeyCode.W)) {
            dy -= 3;
        }
        if (keysPressed.contains(KeyCode.S)) {
            dy += 3;
        }
        if (keysPressed.contains(KeyCode.A)) {
            dx -= 3;
        }
        if (keysPressed.contains(KeyCode.D)) {
            dx += 3;
        }

        double newX = playerTank.getBody().getTranslateX() + dx;
        double newY = playerTank.getBody().getTranslateY() + dy;

        double size = playerTank.getBody().getWidth();

        newX = Math.max(0, Math.min(newX, MAP_WIDTH - size));
        newY = Math.max(0, Math.min(newY, MAP_HEIGHT - size));

        playerTank.setPosition(newX, newY);
    }

    private void shoot() {
        Circle bullet = new Circle(5, Color.CYAN);
        double centerX = playerTank.getCenterX();
        double centerY = playerTank.getCenterY();
        bullet.setTranslateX(centerX);
        bullet.setTranslateY(centerY);

        double dx = mouseX - centerX;
        double dy = mouseY - centerY;

        double length = Math.sqrt(dx * dx + dy * dy);
        bullet.setUserData(new double[] { dx / length * 5, dy / length * 5 });

        bullets.add(bullet);
        gamePane.getChildren().add(bullet);
    }

    private void updateBullets() {
        Iterator<Circle> iter = bullets.iterator();
        while (iter.hasNext()) {
            Circle bullet = iter.next();
            double[] velocity = (double[]) bullet.getUserData();
            bullet.setTranslateX(bullet.getTranslateX() + velocity[0]);
            bullet.setTranslateY(bullet.getTranslateY() + velocity[1]);

            if (bullet.getTranslateX() < 0 || bullet.getTranslateX() > MAP_WIDTH
                    || bullet.getTranslateY() < 0 || bullet.getTranslateY() > MAP_HEIGHT) {
                gamePane.getChildren().remove(bullet);
                iter.remove();
            }
        }
    }
}