package com.shoot_tank.client.network;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.shoot_tank.client.App;
import com.shoot_tank.client.controller.BattleController;
import com.shoot_tank.client.controller.ListRoomController;
import com.shoot_tank.client.message.Message;
import com.shoot_tank.client.models.Player;
import com.shoot_tank.client.models.Room;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class HandleMessage {

    private static HandleMessage instance;

    public static HandleMessage gI() {
        if (instance == null) {
            instance = new HandleMessage();
        }
        return instance;
    }

    public void onMessage(Message msg) {
        try {
            byte type = msg.getType();
            if (type != 7 && type != 8) {
                System.out.println("Receive MSG: " + type);
            }
            switch (type) {
                case -1:
                    String title = msg.readUTF();
                    String message = msg.readUTF();

                    Platform.runLater(() -> {
                        try {
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle(title);
                            alert.setHeaderText(null);
                            alert.setContentText(message);
                            alert.showAndWait();
                        } catch (Exception e) {
                        }
                    });

                    break;
                case 0:
                    Player.myChar().id = msg.readUTF();
                    Player.myChar().name = msg.readUTF();
                    Platform.runLater(() -> {
                        try {
                            App.setRoot("Menu.fxml", 400, 300);
                        } catch (Exception e) {
                        }
                    });
                    break;
                case 1:
                    Boolean isRoom = msg.readBoolean();
                    if (!isRoom) {
                        Player.myChar().room = null;
                        Platform.runLater(() -> {
                            try {
                                App.setRoot("Menu.fxml", 400, 300);
                            } catch (Exception e) {
                            }
                        });

                    } else {
                        String roomId = msg.readUTF();
                        String roomName = msg.readUTF();
                        String roomDescription = msg.readUTF();
                        String idOwner = msg.readUTF();
                        Player.myChar().room = new Room(roomId, roomName);
                        Player.myChar().room.description = roomDescription;
                        Player.myChar().room.idOwner = idOwner;
                        Player.myChar().room.addPlayers(Player.myChar());
                        int countPlayersInRoom = msg.readInt();

                        for (int i = 0; i < countPlayersInRoom; i++) {
                            String plId = msg.readUTF();
                            if (plId.equals(Player.myChar().id)) {
                                msg.readUTF();
                                msg.readBoolean();
                                continue;
                            }
                            Player pl = new Player();
                            pl.id = plId;
                            pl.name = msg.readUTF();
                            pl.isReady = msg.readBoolean();
                            Player.myChar().room.addPlayers(pl);
                        }

                        if (Player.myChar().isBattle) {
                            Set<Player> playerSet = new HashSet<>(Player.myChar().room.players);
                            Iterator<Map.Entry<String, Player>> iterator = BattleController.playerMap.entrySet()
                                    .iterator();
                            while (iterator.hasNext()) {
                                Map.Entry<String, Player> entry = iterator.next();
                                if (!playerSet.contains(entry.getValue())) {
                                    iterator.remove();
                                }
                            }
                        } else {
                            Platform.runLater(() -> {
                                try {
                                    if (Player.myChar().isBattle == false)
                                        App.setRoot("Room.fxml", 650, 250);
                                } catch (Exception e) {
                                }
                            });
                        }

                    }
                    break;
                case 4:
                    boolean isReady = msg.readBoolean();
                    Player.myChar().isReady = isReady;
                    break;

                case 5:
                    int countRooms = msg.readInt();
                    ListRoomController.rooms.clear();
                    for (int i = 0; i < countRooms; i++) {
                        String idRoom = msg.readUTF();
                        String name = msg.readUTF();
                        String owner = msg.readUTF();
                        String description = msg.readUTF();
                        Room r = new Room(idRoom, name);
                        r.ownerName = owner;
                        r.description = description;
                        ListRoomController.rooms.add(r);
                    }
                    Platform.runLater(() -> {
                        try {
                            App.setRoot("ListRoom.fxml", 500, 400);
                        } catch (Exception e) {
                        }
                    });
                    break;
                case 6:
                    int countPlayerInRoom = msg.readInt();
                    BattleController.playerMap.clear();
                    for (int i = 0; i < countPlayerInRoom; i++) {
                        String id = msg.readUTF();
                        String name = msg.readUTF();
                        int hp = msg.readInt();
                        int hpMax = msg.readInt();
                        int dmg = msg.readInt();
                        int speed = msg.readInt();
                        int size = msg.readInt();
                        double x = msg.readDouble();
                        double y = msg.readDouble();
                        if (id.equals(Player.myChar().id)) {
                            Player.myChar().hp = hp;
                            Player.myChar().hpMax = hpMax;
                            Player.myChar().dmg = dmg;
                            Player.myChar().size = size;
                            Player.myChar().speed = speed;
                            Player.myChar().location.x = x;
                            Player.myChar().location.y = y;
                        } else {
                            BattleController.playerMap.put(id, new Player(id, name, hp, hpMax, dmg, size, speed, x, y));
                        }
                    }

                    Platform.runLater(() -> {
                        try {
                            App.setRoot("Battle.fxml", 800, 600);
                        } catch (Exception e) {
                        }
                    });
                    break;
                case 7:
                    String id = msg.readUTF();
                    double x = msg.readDouble();
                    double y = msg.readDouble();
                    Player pl = BattleController.playerMap.get(id);
                    if (id.equals(Player.myChar().id)) {
                        Player.myChar().location.x = x;
                        Player.myChar().location.y = y;
                    } else if (pl != null) {
                        pl.location.x = x;
                        pl.location.y = y;
                    }
                    break;
                case 8:
                    id = msg.readUTF();
                    double angle = msg.readDouble();
                    pl = BattleController.playerMap.get(id);
                    if (id.equals(Player.myChar().id)) {
                        Player.myChar().location.angle = angle;
                    } else if (pl != null) {
                        pl.location.angle = angle;
                    }
                    break;
                case 9:
                    id = msg.readUTF();
                    x = msg.readDouble();
                    y = msg.readDouble();
                    double dx = msg.readDouble();
                    double dy = msg.readDouble();
                    pl = BattleController.playerMap.get(id);
                    if (id.equals(Player.myChar().id)) {
                        Player.myChar().tank.addBullets(x, y, dx, dy);
                    } else if (pl != null) {
                        pl.tank.addBullets(x, y, dx, dy);
                    }
                    break;
                case 10:
                    id = msg.readUTF();
                    int injured = msg.readInt();
                    int hp = msg.readInt();
                    pl = BattleController.playerMap.get(id);
                    if (id.equals(Player.myChar().id)) {
                        Player.myChar().injured += injured;
                        Player.myChar().hp = hp;
                    } else if (pl != null) {
                        pl.injured += injured;
                        pl.hp = hp;
                    }
                    break;
                case 11:
                    id = msg.readUTF();
                    pl = BattleController.playerMap.get(id);
                    if (id.equals(Player.myChar().id)) {

                    } else if (pl != null) {
                        BattleController.playerMap.remove(id);
                    }
                    break;
                default:
                    throw new AssertionError();
            }
            msg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
