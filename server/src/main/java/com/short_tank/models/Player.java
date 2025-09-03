package com.short_tank.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.short_tank.ConfigLoader;
import com.short_tank.network.Session;
import com.short_tank.server.GameServer;
import com.short_tank.services.Service;
import com.short_tank.services.Util;

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
    public int speed = 200;
    public List<Bullet> bullets = new ArrayList<>();
    public long lastShootTime = 0;
    public long shootCooldown = 500;
    public long lastTimeUpdate = 0;
    public boolean isBattle = false;

    public void battle() {
        isBattle = true;
        Thread battleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isBattle) {
                    try {
                        update();
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });
        battleThread.start();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastTimeUpdate) / 1000.0;
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update(deltaTime);
            boolean colision = false;
            if (room != null) {
                for (int i = 0; i < room.players.size(); i++) {
                    Player p = room.players.get(i);
                    if (p != this && Util.checkPlayerCollision(p, b.x, b.y)) {
                        colision = true;
                        int damage = p.injured(10);
                        Service.gI().sendPlayerInjured(p, damage);
                        break;
                    }
                }
            }
            if (colision || b.x < 0 || b.x > GameServer.WIDTH || b.y < 0 || b.y > GameServer.HEIGHT) {
                it.remove();
            }
        }
        lastTimeUpdate = currentTime;
    }

    public boolean isDie() {
        return hp <= 0;
    }

    public int injured(int damage) {
        this.hp -= damage;
        if (this.hp < 0){
            this.hp = 0;
            isBattle = false;
            Service.gI().sendPlayerDie(this);
        }
        return damage;
    }

    public Player() {
        id = UUID.randomUUID().toString();
        this.location = new Location(0, 0, 0);
    }

    public void leaveRoom() {
        if (room != null) {
            Room r = this.room;
            r.players.remove(this);
            if (r.players.isEmpty()) {
                GameServer.rooms.remove(r);
            } else if (r.owner == this) {
                r.owner = r.players.get(0);
            }
            Service.gI().sendRoom(this);
            this.room = null;
        }
    }

}
