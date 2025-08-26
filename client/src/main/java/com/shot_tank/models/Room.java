package com.shot_tank.models;

import java.util.ArrayList;
import java.util.List;

public class Room {
    public String id,name, ownerName, description;
    public List<Player> players = new ArrayList<>();
    public String idOwner;
    public Room(String id,String name){
        this.id = id;
        this.name =name;
    }
    public void addPlayers(Player pl){
        this.players.add(pl);
    }
}
