package com.shoot_tank.client.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import com.shoot_tank.client.App;
import com.shoot_tank.client.models.Player;
import com.shoot_tank.client.models.Tank;
import com.shoot_tank.client.services.Service;
import com.shoot_tank.client.services.Util;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BattleController implements Initializable {
    public static Map<String, Player> playerMap = new HashMap<>();
    public static final double MAP_WIDTH = 1600;
    public static final double MAP_HEIGHT = 1600;

    @FXML
    private Pane gamePane;

    private Tank playerTank;
    private Rectangle mapBorder;
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private final List<Circle> bullets = new ArrayList<>();
    private double mouseX, mouseY;
    private long lastUpdateTime = System.currentTimeMillis();
    // đo fps
    private long lastTime = 0;
    private int frames = 0;
    private double fps = 0;
    private boolean isEnd = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Player.myChar().isBattle = true;
        Rectangle mapBackground = new Rectangle(0, 0, MAP_WIDTH, MAP_HEIGHT);
        mapBackground.setFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2c3e50")),
                new Stop(1, Color.web("#27ae60"))));
        gamePane.getChildren().add(mapBackground);
        int cellSize = 80;
        Color lineColor = Color.rgb(255, 255, 255, 0.12);
        for (int x = 0; x <= MAP_WIDTH; x += cellSize) {
            javafx.scene.shape.Line line = new javafx.scene.shape.Line(x, 0, x, MAP_HEIGHT);
            line.setStroke(lineColor);
            line.setStrokeWidth(1.5);
            gamePane.getChildren().add(line);
        }
        for (int y = 0; y <= MAP_HEIGHT; y += cellSize) {
            javafx.scene.shape.Line line = new javafx.scene.shape.Line(0, y, MAP_WIDTH, y);
            line.setStroke(lineColor);
            line.setStrokeWidth(1.5);
            gamePane.getChildren().add(line);
        }
        mapBorder = new Rectangle(0, 0, MAP_WIDTH, MAP_HEIGHT);
        mapBorder.setFill(Color.TRANSPARENT);
        mapBorder.setStroke(Color.WHITE);
        mapBorder.setStrokeWidth(6);
        mapBorder.setArcWidth(30);
        mapBorder.setArcHeight(30);
        gamePane.getChildren().add(mapBorder);

        playerTank = new Tank(gamePane, Player.myChar());

        for (Map.Entry<String, Player> en : playerMap.entrySet()) {
            Player val = en.getValue();
            val.tank = new Tank(gamePane, val);
        }

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
                if (isEnd)
                    return;
                isEnd = checkWin();
                // do fps
                if (lastTime > 0) {
                    frames++;
                    if (now - lastTime >= 1_000_000_000) {
                        fps = frames;
                        frames = 0;
                        lastTime = now;
                        // System.out.println("FPS: " + fps);
                    }
                } else {
                    lastTime = now;
                }
                // ============================
                double deltaTime = (now - lastUpdateTime) / 1e9;
                lastUpdateTime = now;

                moveTank(deltaTime);
                playerTank.updateHP();
                double angle = playerTank.rotateBarrel(mouseX, mouseY);
                if (Math.abs(angle - playerTank.player.location.angle) > 1) {
                    Service.gI().sendAngle(angle);
                    playerTank.player.location.angle = angle;
                }
                for (java.util.Map.Entry<String, Player> entry : playerMap.entrySet()) {
                    Player player = entry.getValue();
                    player.tank.paint(deltaTime);
                }
                updateBullets(deltaTime);
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

    private void moveTank(double deltaTime) {
        double dx = 0, dy = 0;
        double speed = Player.myChar().speed * deltaTime;
        if (keysPressed.contains(KeyCode.W)) {
            dy -= speed;
        }
        if (keysPressed.contains(KeyCode.S)) {
            dy += speed;
        }
        if (keysPressed.contains(KeyCode.A)) {
            dx -= speed;
        }
        if (keysPressed.contains(KeyCode.D)) {
            dx += speed;
        }

        double newX = playerTank.getBody().getTranslateX() + dx;
        double newY = playerTank.getBody().getTranslateY() + dy;

        double size = playerTank.getBody().getWidth();

        newX = Math.max(0, Math.min(newX, MAP_WIDTH - size));
        newY = Math.max(0, Math.min(newY, MAP_HEIGHT - size));
        if (newX != playerTank.player.location.x || newY != playerTank.player.location.y) {
            Service.gI().sendMove(newX, newY);
        }
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
        double bulletSpeed = 250;
        Service.gI().sendShoot(dx, dy);
        double length = Math.sqrt(dx * dx + dy * dy);
        bullet.setUserData(new double[] { dx * bulletSpeed / length, dy * bulletSpeed / length });

        bullets.add(bullet);
        gamePane.getChildren().add(bullet);
    }

    private void updateBullets(double deltaTime) {
        Iterator<Circle> iter = bullets.iterator();
        while (iter.hasNext()) {
            Circle bullet = iter.next();
            double[] velocity = (double[]) bullet.getUserData();
            bullet.setTranslateX(bullet.getTranslateX() + velocity[0] * deltaTime);
            bullet.setTranslateY(bullet.getTranslateY() + velocity[1] * deltaTime);
            boolean colision = false;
            for (Entry<String, Player> entry : BattleController.playerMap.entrySet()) {
                Player p = entry.getValue();
                if (Util.checkCircleRectangleCollision(bullet, p.tank.getBody())) {
                    colision = true;
                    break;
                }
            }
            if (colision || bullet.getTranslateX() < 0 || bullet.getTranslateX() > MAP_WIDTH
                    || bullet.getTranslateY() < 0 || bullet.getTranslateY() > MAP_HEIGHT) {
                gamePane.getChildren().remove(bullet);
                iter.remove();
            }
        }
    }

    public boolean checkWin() {
        if (Player.myChar().hp <= 0) {
            Player.myChar().tank = null;
            Player.myChar().isReady = false;
            Service.gI().sendReady(false);
            Platform.runLater(() -> {
                bullets.clear();
                showEndEffect("Bạn đã thua!\nBạn đã bị hạ gục!");
            });
            return true;
        } else if (playerMap.isEmpty()) {
            Player.myChar().tank = null;
            Player.myChar().isReady = false;
            Service.gI().sendReady(false);
            bullets.clear();
            Platform.runLater(() -> {
                showEndEffect("Chiến thắng!\nBạn đã thắng!");
            });
            return true;
        }
        return false;
    }

    private void showEndEffect(String message) {
        gamePane.setTranslateX(0);
        gamePane.setTranslateY(0);
        StackPane overlay = new StackPane();
        overlay.setPrefSize(gamePane.getWidth(), gamePane.getHeight());
        overlay.setStyle("-fx-background-color: rgba(44, 62, 80, 0.85);");

        Label label = new Label(message);
        label.setStyle("-fx-font-size: 36px; -fx-text-fill: #fff; -fx-font-weight: bold;");

        Button btn = new Button("Về phòng");
        btn.setStyle(
                "-fx-font-size: 20px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 30 10 30; -fx-background-radius: 20;");
        btn.setOnAction(e -> {
            try {
                Player.myChar().isBattle = false;
                App.setRoot("Room.fxml", 650, 250);
            } catch (Exception ex) {
            }
        });

        VBox vbox = new VBox(30, label, btn);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        overlay.getChildren().add(vbox);

        gamePane.getChildren().add(overlay);

        FadeTransition ft = new FadeTransition(Duration.seconds(1.2), overlay);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(Duration.seconds(0.8), label);
        st.setFromX(0.7);
        st.setFromY(0.7);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.play();

        javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.7);
        btn.setEffect(glow);
    }
}