package com.short_tank.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.short_tank.network.Session;
import com.short_tank.server.GameServer;
import com.short_tank.services.Service;

public class Player {
    public Player(Session session) {
        this.session = session;
        this.id = UUID.randomUUID().toString();
        session.setPlayer(this);
        session.start();
        this.location = new Location(0, 0, 0);
    }

    public Session session;
    public String id;
    public String name;
    public int hp = 100, hpMax = 100;
    public int dmg = 5;
    public Location location;
    public int size = 50;
    public Room room;
    public Boolean isReady = false;
    public int maxBullet = 5;
    public int speed = 12;
    public List<Bullet> bullets = new ArrayList<>();
    public long lastShootTime = 0;
    public long shootCooldown = 500;

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShootTime > shootCooldown) {
        }
    }

    public void injured() {
        this.hp -= 10;
        if (this.hp < 0)
            this.hp = 0;
    }

    public Player() {
        id = UUID.randomUUID().toString();
        this.location = new Location(0, 0, 0);
    }

    public void leaveRoom() {
        if (room != null) {
            room.players.remove(this);
            if (room.players.isEmpty()) {
                GameServer.rooms.remove(room);
            } else {
                room.owner = room.players.get(0);
            }
            this.room = null;
        }
        Service.gI().sendRoom(this);
    }

}
