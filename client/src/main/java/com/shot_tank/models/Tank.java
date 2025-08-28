package com.shot_tank.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.shot_tank.controller.BattleController;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Tank {
    // Image barrelImage = new
    // Image(getClass().getResource("/images/barrel.png").toExternalForm());
    // Image bodyImage = new
    // Image(getClass().getResource("/images/body.png").toExternalForm());
    // Image bulletImage = new
    // Image(getClass().getResource("/images/bullet.png").toExternalForm());

    private final Rectangle body;
    private final Rectangle barrel;
    private final Rectangle healthBarBg;
    private final Rectangle healthBar;
    private final Label nameLabel;
    private List<Circle> bullets = new ArrayList<>();
    private final Pane parent;
    public Player player;
    public Tank(Pane parent, Player player) {
        this.player = player;
        player.tank = this;
        body = new Rectangle(player.size, player.size);
        body.setFill(new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#43cea2")),
            new Stop(1, Color.web("#185a9d"))
        ));
        body.setArcWidth(player.size * 0.4);
        body.setArcHeight(player.size * 0.4);
        body.setStroke(Color.WHITE);
        body.setStrokeWidth(3);
        body.setTranslateX(player.location.x);
        body.setTranslateY(player.location.y);
        body.setEffect(new DropShadow(10, Color.BLACK));

        barrel = new Rectangle(player.size * 0.25, player.size * 0.75);
        barrel.setFill(new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#f7971e")),
            new Stop(1, Color.web("#ffd200"))
        ));
        barrel.setArcWidth(player.size * 0.15);
        barrel.setArcHeight(player.size * 0.15);
        barrel.setStroke(Color.DARKGRAY);
        barrel.setStrokeWidth(2);
        barrel.setTranslateX(player.location.x + player.size / 2 - barrel.getWidth() / 2);
        barrel.setTranslateY(player.location.y - barrel.getHeight() / 2);
        barrel.setEffect(new DropShadow(5, Color.GRAY));

        nameLabel = new Label(player.name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 2, 0, 0, 1);");
        nameLabel.setTranslateX(player.location.x + player.size / 2 - 20);
        nameLabel.setTranslateY(player.location.y - 30);

        healthBarBg = new Rectangle(player.size, 6, Color.RED);
        healthBarBg.setArcWidth(6);
        healthBarBg.setArcHeight(6);
        healthBarBg.setTranslateX(player.location.x);
        healthBarBg.setTranslateY(player.location.y - 15);

        healthBar = new Rectangle(player.size * (player.hp / (double) player.hpMax), 6, Color.LIME);
        healthBar.setArcWidth(6);
        healthBar.setArcHeight(6);
        healthBar.setTranslateX(player.location.x);
        healthBar.setTranslateY(player.location.y - 15);

        this.parent = parent;
        parent.getChildren().addAll(body, barrel, healthBarBg, healthBar, nameLabel);
    }

    public void addBullets(double startX, double startY, double dx, double dy) {
        Platform.runLater(() -> {
            Circle bullet = new Circle(5, Color.CYAN);

            bullet.setTranslateX(startX);
            bullet.setTranslateY(startY);

            bullet.setUserData(new double[] { dx , dy });
            bullets.add(bullet);

            parent.getChildren().add(bullet);
        });
    }
    private void updateBullets() {
        Iterator<Circle> iter = bullets.iterator();
        while (iter.hasNext()) {
            Circle bullet = iter.next();
            double[] velocity = (double[]) bullet.getUserData();
            bullet.setTranslateX(bullet.getTranslateX() + velocity[0]);
            bullet.setTranslateY(bullet.getTranslateY() + velocity[1]);

            if (bullet.getTranslateX() < 0 || bullet.getTranslateX() > BattleController.MAP_WIDTH
                    || bullet.getTranslateY() < 0 || bullet.getTranslateY() > BattleController.MAP_HEIGHT) {
                parent.getChildren().remove(bullet);
                iter.remove();
            }
        }
    }
    public void updateHealth(int newHp) {
        this.player.hp = newHp;
        double healthRatio = Math.max(0, Math.min(1.0, player.hp / (double) player.hpMax));
        healthBar.setWidth(player.size * healthRatio);
    }

    public Rectangle getBody() {
        return body;
    }

    public Rectangle getBarrel() {
        return barrel;
    }

    public void setPosition(double x, double y) {
        double dx = x - this.player.location.x;
        double dy = y - this.player.location.y;

        double angle = Math.toDegrees(Math.atan2(dy, dx));
        body.setRotate(angle);

        this.player.location.x = x;
        this.player.location.y = y;

        body.setTranslateX(x);
        body.setTranslateY(y);

        barrel.setTranslateX(x + player.size / 2 - barrel.getWidth() / 2);
        barrel.setTranslateY(y);

        healthBar.setTranslateX(x);
        healthBar.setTranslateY(y - 15);
        healthBarBg.setTranslateX(x);
        healthBarBg.setTranslateY(y - 15);

        nameLabel.setTranslateX(x + player.size / 2 - 20);
        nameLabel.setTranslateY(y - 30);
    }

    public double rotateBarrel(double mouseX, double mouseY) {
        double centerX = player.location.x + player.size / 2;
        double centerY = player.location.y + player.size / 2;
        double angle = Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX)) - 90;
        barrel.setRotate(angle);
        return angle;
    }

    public void rotateBarrel(double angle) {
        barrel.setRotate(angle);
    }

    public double getCenterX() {
        return player.location.x + player.size / 2;
    }

    public double getCenterY() {
        return player.location.y + player.size / 2;
    }

    public double getAngle() {
        return barrel.getRotate();
    }

    public void paint(){
        setPosition(player.location.x, player.location.y);
        rotateBarrel(player.location.angle);
        updateBullets();
    }
}
