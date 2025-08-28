package com.shot_tank.network;

import com.shot_tank.MainApp;
import com.shot_tank.controller.BattleController;
import com.shot_tank.controller.ListRoomController;
import com.shot_tank.message.Message;
import com.shot_tank.models.Player;
import com.shot_tank.models.Room;

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
            System.out.println("Receive MSG: " + type);
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
                            MainApp.setRoot("Menu.fxml", 400, 300);
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
                                MainApp.setRoot("Menu.fxml", 400, 300);
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
                        Platform.runLater(() -> {
                            try {
                                MainApp.setRoot("Room.fxml", 650, 250);
                            } catch (Exception e) {
                            }
                        });
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
                            MainApp.setRoot("ListRoom.fxml", 500, 400);
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
                        BattleController.playerMap.put(id, new Player(id, name, hp, hpMax, dmg, size, speed, x, y));
                    }

                    Platform.runLater(() -> {
                        try {
                            MainApp.setRoot("Battle.fxml", 800, 600);
                        } catch (Exception e) {
                        }
                    });
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
