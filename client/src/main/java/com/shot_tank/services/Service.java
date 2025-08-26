package com.shot_tank.services;

import com.shot_tank.message.Message;
import com.shot_tank.network.Session;

public class Service {

    private static Service instance;

    public static Service gI() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    public void sendCreatePlayer(String name) {
        try {
            Message msg = new Message((byte) 0);
            msg.writeUTF(name);
            Session.mySession.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCreateRoom(String roomName, int maxMembers) {
        try {
            Message msg = new Message((byte) 1);
            msg.writeUTF(roomName);
            msg.writeInt(maxMembers);
            Session.mySession.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendJoinRoom(String roomId){
        try{
            Message msg = new Message((byte)2);
            msg.writeUTF(roomId);
            Session.mySession.sendMessage(msg);
            msg.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void sendLeaveRoom(){
        try {
            Message msg = new Message((byte)3);
            Session.mySession.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
        }
    }
    public void sendReady(boolean isReady){
        try {
            Message msg = new Message((byte)4);
            msg.writeBoolean(isReady);
            Session.mySession.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
        }
    }
    public void sendMove(int x,int y){
        try {
            Message msg = new Message((byte)5);
            msg.writeInt(x);
            msg.writeInt(y);
            Session.mySession.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendRequestListRoom(){
        try {
            Message msg = new Message((byte)5);
            Session.mySession.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
        }
    }
    public void sendStartGame() {
        try {
            Message msg = new Message((byte)6);
            Session.mySession.sendMessage(msg);
            msg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
