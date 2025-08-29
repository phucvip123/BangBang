package com.shoot_tank.client.network;


import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.shoot_tank.client.ConfigLoader;
import com.shoot_tank.client.message.Message;

import java.io.*;

public class Session extends Thread {
    public static Session mySession;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private Boolean running = true;
    private final Thread senderThread;
    private final BlockingQueue<Message> sendQueue = new LinkedBlockingQueue<>();
    public Boolean connected = false;

    public Session() throws IOException {

        this.socket = new Socket(ConfigLoader.getInstance().getHost(), ConfigLoader.getInstance().getPort());
        socket.setTcpNoDelay(true);
        connected = true;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        senderThread = new Thread(() -> {
            try {
                while (running && ConfigLoader.getInstance().isRunninng) {
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

    @Override
    public void run() {
        try {
            while (running) {
                byte type = in.readByte();
                int length = in.readInt();
                byte[] data = new byte[length];
                in.readFully(data);

                Message msg = new Message(type, data);

                HandleMessage.gI().onMessage(msg);

                msg.close();
            }
        } catch (IOException e) {
            
        } finally {
            stopHandler();
        }
    }
    public void shutdown() {
    if (socket != null && !socket.isClosed()) {
        try { socket.close(); } catch (Exception e) { }
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
            socket.close();
        } catch (IOException ignored) {
        }
        senderThread.interrupt();
    }

}