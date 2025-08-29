package com.shoot_tank.client.models;


import java.util.ArrayList;
import java.util.List;

public class Player {
    private static Player instance;
    public static Player myChar(){
        if(instance == null) instance = new Player();
        return instance;
    }

    public String id;
    public String name;
    public int hp,hpMax;
    public int dmg;
    public int speed;
    public Location location;
    public int size = 50;
    public Room room;
    public boolean isReady;
    public List<Bullet> bullets = new ArrayList<>();
    public Tank tank;
    public int injured = 0;
    public boolean isBattle = false;
    public Player(){
        this.location = new Location(0, 0, 0);
    }
    public Player(String id,String name,int hp,int hpMax, int dmg, int size,int speed, double x, double y) {
        this.id = id;
        this.name = name;
        this.hp = hp;
        this.hpMax = hpMax;
        this.size = size;
        this.dmg = dmg;
        this.speed = speed;
        this.location = new Location(x,y,0);
    }
}
