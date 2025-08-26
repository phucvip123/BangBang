package com.short_tank.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room {
    public String id, name;
    public List<Player> players = new ArrayList<>();
    public Player owner;
    public int maxMembers;

    public Room(String name){
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
    public boolean add(Player player){
        if(players.size() >= 2) return false;
        players.add(player);
        return true;
    }
}
