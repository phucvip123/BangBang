package com.short_tank.message;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageSender extends Thread {
    private final DataOutputStream out;
    private final BlockingQueue<Message> sendQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    public MessageSender(Socket socket) throws IOException {
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void send(Message msg) {
        sendQueue.add(msg);
    }

    @Override
    public void run() {
        try {
            while (running) {
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
            e.printStackTrace();
        }
    }

    public void stopSender() {
        running = false;
        this.interrupt();
    }
}

