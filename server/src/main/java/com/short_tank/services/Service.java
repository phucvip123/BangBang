package com.short_tank.services;

import java.util.List;
import java.util.stream.Collectors;

import com.short_tank.message.Message;
import com.short_tank.models.Player;
import com.short_tank.models.Room;
import com.short_tank.server.GameServer;

public class Service {

    private static Service instance;

    public static Service gI() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    public void sendNotificationis(Player player, String title, String message) {
        try {
            Message msg = new Message((byte) -1);
            msg.writeUTF(title);
            msg.writeUTF(message);
            player.session.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
        }
    }

    public void sendCreatePlayer(Player player) {
        try {
            Message msg = new Message((byte) 0);
            msg.writeUTF(player.id);
            msg.writeUTF(player.name);
            player.session.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRoom(Player player) {
        try {
            Message msg = new Message((byte) 1);
            if (player.room == null) {
                msg.writeBoolean(false);
            } else {
                msg.writeBoolean(true);
                msg.writeUTF(player.room.id);
                msg.writeUTF(player.room.name);
                msg.writeUTF(player.room.players.size() + "/" + player.room.maxMembers);
                msg.writeUTF(player.room.owner.id);
                msg.writeInt(player.room.players.size());
                for (int i = 0; i < player.room.players.size(); i++) {
                    Player p = player.room.players.get(i);
                    msg.writeUTF(p.id);
                    msg.writeUTF(p.name);
                    msg.writeBoolean(p.isReady);
                }
            }
            player.session.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendReady(Player player) {
        try {
            Message msg = new Message((byte) 4);
            msg.writeBoolean(player.isReady);
            player.session.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendListRoom(Player pl) {
        try {
            Message msg = new Message((byte) 5);
            List<Room> onePlayerRooms = GameServer.rooms.stream()
                    .filter(room -> room.players.size() < room.maxMembers)
                    .collect(Collectors.toList());
            int countRoom = onePlayerRooms.size();
            msg.writeInt(countRoom);
            for (int i = 0; i < countRoom; i++) {
                Room r = onePlayerRooms.get(i);
                msg.writeUTF(r.id);
                msg.writeUTF(r.name);
                msg.writeUTF(r.owner != null ? r.owner.name : "No name");
                msg.writeUTF(onePlayerRooms.size() + "/" + r.maxMembers);

            }
            pl.session.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
        }
    }

    public void sendStartGame(Player player) {
        try {
            Room room = player.room;
            if (room != null) {

            }
            Message msg = new Message((byte) 6);
            int countPlayerInRooms = room.players.size();
            msg.writeInt(countPlayerInRooms);
            for (int i = 0; i < countPlayerInRooms; i++) {
                Player p = room.players.get(i);
                msg.writeUTF(p.id);
                msg.writeUTF(p.name);
                msg.writeInt(p.hp);
                msg.writeInt(p.hpMax);
                msg.writeInt(p.dmg);
                msg.writeInt(p.speed);
                msg.writeInt(p.size);
                msg.writeDouble(p.location.x);
                msg.writeDouble(p.location.y);
            }
            for (int i = 0; i < countPlayerInRooms; i++) {
                Player p = room.players.get(i);
                p.session.sendMessage(msg);
            }
            msg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLocationMeInMap(Player pl) {
        try {
            Room room = pl.room;
            if (room != null) {
                Message msg = new Message((byte) 7);
                msg.writeUTF(pl.id);
                msg.writeDouble(pl.location.x);
                msg.writeDouble(pl.location.y);
                for (int i = 0; i < room.players.size(); i++) {
                    Player p = room.players.get(i);
                    if (p.id.equals(pl.id))
                        continue;
                    p.session.sendMessage(msg);
                }
                msg.close();
            }
        } catch (Exception e) {
        }

    }

    public void sendAngleMeInMap(Player pl) {
        try {
            Room room = pl.room;
            if (room != null) {
                Message msg = new Message((byte) 8);
                msg.writeUTF(pl.id);
                msg.writeDouble(pl.location.angle);
                for (int i = 0; i < room.players.size(); i++) {
                    Player p = room.players.get(i);
                    if (p.id.equals(pl.id))
                        continue;
                    p.session.sendMessage(msg);
                }
                msg.close();
            }
        } catch (Exception e) {
        }
    }

    public void sendPlayerShoot(Player pl, double[] direction) {
        try {
            Room room = pl.room;
            if (room != null) {
                Message msg = new Message((byte) 9);
                msg.writeUTF(pl.id);
                msg.writeDouble(pl.location.x + pl.size / 2);
                msg.writeDouble(pl.location.y + pl.size / 2);
                msg.writeDouble(direction[0]);
                msg.writeDouble(direction[1]);
                for (int i = 0; i < room.players.size(); i++) {
                    Player p = room.players.get(i);
                    if (p.id.equals(pl.id))
                        continue;
                    p.session.sendMessage(msg);
                }
                msg.close();
            }
        } catch (Exception e) {
        }
    }
    public void sendPlayerInjured(Player player, int damage) {
        try {
            Message msg = new Message((byte) 10);
            msg.writeUTF(player.id);
            msg.writeInt(damage);
            msg.writeInt(player.hp);
            for (Player p : player.room.players) {
                p.session.sendMessage(msg);
            }
            msg.close();
        } catch (Exception e) {
        }
    }
    public void sendPlayerDie(Player player) {
        try {
            Message msg = new Message((byte) 11);
            msg.writeUTF(player.id);
            for (Player p : player.room.players) {
                if(p.id.equals(player.id)) continue;
                p.session.sendMessage(msg);
            }
            msg.close();
        } catch (Exception e) {
        }
    }
}
