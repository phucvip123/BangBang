package com.short_tank.models;


public class Bullet {
    public double x,y;
    public double dx,dy;
    public Bullet(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void update(double deltaTime) {
        this.x += dx * deltaTime;
        this.y += dy * deltaTime;
    }
}
