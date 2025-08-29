package com.short_tank.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.short_tank.ConfigLoader;
import com.short_tank.models.Player;
import com.short_tank.models.Room;
import com.short_tank.network.Session;


public class GameServer extends Thread {
    public static final int WIDTH = 1600;
    public static final int HEIGHT = 1600;
    public static List<Room> rooms = new ArrayList<>();
    public static List<Player> players = new ArrayList<>();
    private final String host;
    private final int port;
    private final int maxRoom;
    public GameServer() {
        this.host = ConfigLoader.gI().getHost();
        this.port = ConfigLoader.gI().getPort();
        this.maxRoom = ConfigLoader.gI().getMaxRoom();
        rooms = new ArrayList<>(maxRoom);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Game Server running on " + host + ":" + port);
            System.out.println("Max Room: " + maxRoom);

            while (true) {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                System.out.println("New player connected: " + socket);
                Session session = new Session(socket);
                Player player = new Player(session);
                players.add(player);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}