package com.shoot_tank.client.message;


import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class MessageReader extends Thread {
    private final DataInputStream in;
    private volatile boolean running = true;

    public interface MessageHandler {
        void onMessage(Message msg);
    }

    private final MessageHandler handler;

    public MessageReader(Socket socket, MessageHandler handler) throws IOException {
        this.in = new DataInputStream(socket.getInputStream());
        this.handler = handler;
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
                handler.onMessage(msg); 
                msg.close();
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    public void stopReader() {
        running = false;
        this.interrupt();
    }
}
