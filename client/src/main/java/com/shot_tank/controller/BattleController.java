package com.shot_tank.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import com.shot_tank.models.Player;
import com.shot_tank.models.Tank;
import com.shot_tank.services.Service;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BattleController implements Initializable {
    public static List<Player> players = new ArrayList<>();
    @FXML
    private Pane gamePane;

    private Tank playerTank;

    private final Set<KeyCode> keysPressed = new HashSet<>();
    private final List<Circle> bullets = new ArrayList<>();
    private double mouseX, mouseY;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerTank = new Tank(gamePane, Player.myChar().location.x, Player.myChar().location.x, Player.myChar().size,
                Player.myChar().name, Player.myChar().hp, Player.myChar().hpMax);
        Player.myChar().tank = playerTank;
        for (int i = 0; i < players.size(); i++) {
            Tank enemyTank = new Tank(gamePane, players.get(i).location.x, players.get(i).location.y, players.get(i).size,
                    players.get(i).name, players.get(i).hp, players.get(i).hpMax);
            players.get(i).tank = enemyTank;
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
                moveTank();
                playerTank.rotateBarrel(mouseX, mouseY);
                double angle = playerTank.getAngle();
                if(Math.abs(angle - Player.myChar().location.angle) >= 5){
                    Player.myChar().location.angle = angle;
                    // Service.gI().sendAngle(angle);
                }
                playerTank.updateBullets();
                for(int i=0;i<players.size();i++){
                    players.get(i).tank.updateBullets();
                }
            }
        };
        timer.start();
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

        double paneWidth = gamePane.getWidth();
        double paneHeight = gamePane.getHeight();
        double size = playerTank.getBody().getWidth();

        newX = Math.max(0, Math.min(newX, paneWidth - size));
        newY = Math.max(0, Math.min(newY, paneHeight - size));

        playerTank.setPosition(newX, newY);
        if (Player.myChar().location.x != (int)newX || Player.myChar().location.y != (int)newY) {
            Player.myChar().location.x = (int)newX;
            Player.myChar().location.y = (int)newY;
            com.shot_tank.services.Service.gI().sendMove((int)newX, (int)newY);
        }
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

            if (bullet.getTranslateX() < 0 || bullet.getTranslateX() > gamePane.getWidth()
                    || bullet.getTranslateY() < 0 || bullet.getTranslateY() > gamePane.getHeight()) {
                gamePane.getChildren().remove(bullet);
                iter.remove();
            }
        }
    }
}
