package com.short_tank.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.short_tank.ConfigLoader;
import com.short_tank.message.Message;
import com.short_tank.models.Player;
import com.short_tank.server.Controller;
import com.short_tank.server.GameServer;
import com.short_tank.services.Service;

public class Session extends Thread {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private final Thread senderThread;
    private final BlockingQueue<Message> sendQueue = new LinkedBlockingQueue<>();

    private Player player;

    private volatile boolean running = true;

    public Session(Socket socket) throws IOException {
        this.socket = socket;

        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        senderThread = new Thread(() -> {
            try {
                while (running && ConfigLoader.gI().isRunninng) {
                    Message msg = sendQueue.take();
                    byte[] data = msg.getData();

                    out.writeByte(msg.getType());
                    out.writeInt(data.length);
                    out.write(data);
                    out.flush();

                    msg.close();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                if (running)
                    e.printStackTrace();
            }
        });
        senderThread.start();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            while (running) {
                byte type = in.readByte();
                int length = in.readInt();
                byte[] data = new byte[length];
                in.readFully(data);

                Message msg = new Message(type, data);

                Controller.gI().handleMessage(player, msg);

                msg.close();
            }
        } catch (IOException e) {
            System.out.println("Player disconnected: " + socket.getRemoteSocketAddress());
        } finally {
            stopHandler();
        }
    }

    public void sendMessage(Message msg) {
        try {
            sendQueue.put(msg);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stopHandler() {
        running = false;
        try {
            Service.gI().sendPlayerDie(this.player);
            socket.close();
        } catch (IOException ignored) {
            System.out.println("Client disconnected!");
        }finally {
            if(player.room != null){
                player.leaveRoom();
            }
            GameServer.players.remove(player);
            player = null;
        }
        senderThread.interrupt();
    }
}
