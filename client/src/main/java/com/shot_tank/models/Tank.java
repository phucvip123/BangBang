package com.shot_tank.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.shot_tank.services.Service;

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

    private Rectangle body;
    private Rectangle barrel;
    private Rectangle healthBarBg;
    private Rectangle healthBar;
    private Label nameLabel;
    private double x, y;
    private double size;
    private int hp, hpMax;
    private List<Circle> bullets = new ArrayList<>();
    private Pane parent;

    public Tank(Pane parent, double x, double y, double size, String name, int hp, int hpMax) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.hp = hp;
        this.hpMax = hpMax;

        body = new Rectangle(size, size);
        body.setFill(new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#43cea2")),
            new Stop(1, Color.web("#185a9d"))
        ));
        body.setArcWidth(size * 0.4);
        body.setArcHeight(size * 0.4);
        body.setStroke(Color.WHITE);
        body.setStrokeWidth(3);
        body.setTranslateX(x);
        body.setTranslateY(y);
        body.setEffect(new DropShadow(10, Color.BLACK));

        barrel = new Rectangle(size * 0.25, size * 0.75);
        barrel.setFill(new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#f7971e")),
            new Stop(1, Color.web("#ffd200"))
        ));
        barrel.setArcWidth(size * 0.15);
        barrel.setArcHeight(size * 0.15);
        barrel.setStroke(Color.DARKGRAY);
        barrel.setStrokeWidth(2);
        barrel.setTranslateX(x + size / 2 - barrel.getWidth() / 2);
        barrel.setTranslateY(y - barrel.getHeight() / 2);
        barrel.setEffect(new DropShadow(5, Color.GRAY));

        nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 2, 0, 0, 1);");
        nameLabel.setTranslateX(x + size / 2 - 20);
        nameLabel.setTranslateY(y - 30);

        healthBarBg = new Rectangle(size, 6, Color.RED);
        healthBarBg.setArcWidth(6);
        healthBarBg.setArcHeight(6);
        healthBarBg.setTranslateX(x);
        healthBarBg.setTranslateY(y - 15);

        healthBar = new Rectangle(size * (hp / (double) hpMax), 6, Color.LIME);
        healthBar.setArcWidth(6);
        healthBar.setArcHeight(6);
        healthBar.setTranslateX(x);
        healthBar.setTranslateY(y - 15);

        this.parent = parent;
        parent.getChildren().addAll(body, barrel, healthBarBg, healthBar, nameLabel);
    }

    public void addBullet(int x, int y) {
        Platform.runLater(() -> {
            Circle bullet = new Circle(5, Color.CYAN);
            double centerX = x;
            double centerY = y;
            bullet.setTranslateX(centerX);
            bullet.setTranslateY(centerY);

            double dx = x - centerX;
            double dy = y - centerY;
            double length = Math.sqrt(dx * dx + dy * dy);
            bullet.setUserData(new double[] { dx / length * 5, dy / length * 5 });

            bullets.add(bullet);
            parent.getChildren().add(bullet);
        });

    }

    public void updateBullets() {
        Iterator<Circle> iter = bullets.iterator();
        while (iter.hasNext()) {
            Circle bullet = iter.next();
            double[] velocity = (double[]) bullet.getUserData();
            bullet.setTranslateX(bullet.getTranslateX() + velocity[0]);
            bullet.setTranslateY(bullet.getTranslateY() + velocity[1]);

            if (bullet.getTranslateX() < 0 || bullet.getTranslateX() > parent.getWidth()
                    || bullet.getTranslateY() < 0 || bullet.getTranslateY() > parent.getHeight()) {
                parent.getChildren().remove(bullet);
                iter.remove();
            }
        }
    }

    public void updateHealth(int newHp) {
        this.hp = newHp;
        double healthRatio = Math.max(0, Math.min(1.0, hp / (double) hpMax));
        healthBar.setWidth(size * healthRatio);
    }

    public Rectangle getBody() {
        return body;
    }

    public Rectangle getBarrel() {
        return barrel;
    }

    public void setPosition(double x, double y) {
        if (x == this.x && y == this.y) {
            return;
        }
        double dx = x - this.x;
        double dy = y - this.y;

        double angle = Math.toDegrees(Math.atan2(dy, dx));
        body.setRotate(angle);

        this.x = x;
        this.y = y;

        body.setTranslateX(x);
        body.setTranslateY(y);

        barrel.setTranslateX(x + size / 2 - barrel.getWidth() / 2);
        barrel.setTranslateY(y);

        healthBar.setTranslateX(x);
        healthBar.setTranslateY(y - 15);
        healthBarBg.setTranslateX(x);
        healthBarBg.setTranslateY(y - 15);

        nameLabel.setTranslateX(x + size / 2 - 20);
        nameLabel.setTranslateY(y - 30);
    }

    public void rotateBarrel(double mouseX, double mouseY) {
        double centerX = x + size / 2;
        double centerY = y + size / 2;
        double angle = Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX)) - 90;
        barrel.setRotate(angle);

    }

    public void rotateBarrel(double angle) {
        barrel.setRotate(angle);
    }

    public double getCenterX() {
        return x + size / 2;
    }

    public double getCenterY() {
        return y + size / 2;
    }

    public double getAngle() {
        return barrel.getRotate();
    }
}
