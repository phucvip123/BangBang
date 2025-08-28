package com.short_tank.server;

import com.short_tank.message.Message;
import com.short_tank.models.Player;
import com.short_tank.services.HandleMessage;
import com.short_tank.services.Service;

public class Controller {

    private static Controller instance;

    public static Controller gI() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void handleMessage(Player player, Message msg) {
        try {
            byte type = msg.getType();
            if(type != 7 && type !=8) {
                System.out.println("Receive MSG: " + type);
            }
            switch (type) {
                case 0:
                    String name = msg.readUTF();
                    HandleMessage.gI().setPlayerName(player, name);
                    break;
                case 1:
                    String roomName = msg.readUTF();
                    int maxMembers = msg.readInt();
                    HandleMessage.gI().createRoom(player, roomName, maxMembers);
                    break;
                case 2:
                    String roomId = msg.readUTF();
                    HandleMessage.gI().joinRoom(player, roomId);
                    break;
                case 3:
                    HandleMessage.gI().leaveRoom(player);
                    break;
                case 4:
                    boolean isReady = msg.readBoolean();
                    HandleMessage.gI().playerReady(player, isReady);
                    break;
                case 5:
                    Service.gI().sendListRoom(player);
                    break;
                case 6:
                    HandleMessage.gI().startGame(player);
                    break;
                case 7:
                    double x = msg.readDouble();
                    double y = msg.readDouble();
                    HandleMessage.gI().playerMove(player, x, y);
                    break;
                case 8:
                    double angle = msg.readDouble();
                    HandleMessage.gI().playerAngle(player, angle);
                    break;
                case 9:
                    double[] direction = new double[2];
                    direction[0] = msg.readDouble();
                    direction[1] = msg.readDouble();
                    HandleMessage.gI().playerShoot(player, direction[0], direction[1]);
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
