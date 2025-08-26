package com.short_tank.models;

import java.util.UUID;

public class Bullet {
    public String id;
    public double x, y,angle;
    double dx, dy;
    public double speed = 8;

    public Bullet(double startX, double startY, double angle) {
        id = UUID.randomUUID().toString();
        this.angle = angle;
        this.x = startX;
        this.y = startY;
        this.dx = Math.cos(angle) * speed;
        this.dy = Math.sin(angle) * speed;
    }

    boolean update(Player enemy) {
        x += dx;
        y += dy;
        if(enemy != null && enemy.location != null) {
            double distX = enemy.location.x - x;
            double distY = enemy.location.y - y;
            double distance = Math.sqrt(distX * distX + distY * distY);
            if (distance < enemy.size / 2) {
                enemy.injured();
                return true;
            }
        }
        return isOutOfBounds(500, 500);
    }

    boolean isOutOfBounds(int w, int h) {
        return (x < 0 || x > w || y < 0 || y > h);
    }
}
