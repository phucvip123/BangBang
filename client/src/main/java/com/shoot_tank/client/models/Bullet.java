package com.shoot_tank.client.models;

public class Bullet {
    public String id;
    public double x, y, angle,speed;
    public Bullet(String id, double x, double y,double angle, double speed){
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
    }
}
