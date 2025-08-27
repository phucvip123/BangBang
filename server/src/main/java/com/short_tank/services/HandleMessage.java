package com.short_tank.services;

import com.short_tank.ConfigLoader;
import com.short_tank.models.Player;
import com.short_tank.models.Room;
import com.short_tank.server.GameServer;

public class HandleMessage {

    private static HandleMessage instance;

    public static HandleMessage gI() {
        if (instance == null) {
            instance = new HandleMessage();
        }
        return instance;
    }

    public void setPlayerName(Player player, String name) {
        player.name = name;
        Service.gI().sendCreatePlayer(player);
    }

    public void createRoom(Player player, String roomName, int maxMembers) {
        if (player.room != null) {
            Service.gI().sendNotificationis(player, "Cảnh báo", "Hãy rời phòng trước khi tạo mới!");
            return;
        }
        if (maxMembers <= 0 || maxMembers > ConfigLoader.gI().getMaxMembersInRoom()) {
            Service.gI().sendNotificationis(player, "Cảnh báo",
                    "Số lượng thành viên tối đa là " + ConfigLoader.gI().getMaxMembersInRoom());
            return;
        }
        Room room = new Room(roomName);
        room.maxMembers = maxMembers;
        player.room = room;
        room.owner = player;
        room.add(player);
        GameServer.rooms.add(room);
        Service.gI().sendRoom(player);
    }

    public void joinRoom(Player player, String roomId) {
        if (player.room != null) {
            return;
        }
        GameServer.rooms.stream()
                .filter(r -> r.id.equals(roomId))
                .findFirst()
                .ifPresent(r -> {
                    if (r.add(player)) {
                        player.room = r;
                    }
                });

        for (Player p : player.room.players) {
            Service.gI().sendRoom(p);
        }
    }

    public void leaveRoom(Player player) {
        Room room = player.room;
        player.leaveRoom();
        Service.gI().sendRoom(player);
        for (int i = 0; i < room.players.size(); i++) {
            Player p = room.players.get(i);
            Service.gI().sendRoom(p);
        }
    }

    public void playerReady(Player player, boolean ready) {
        if (player.room == null) {
            return;
        }
        player.isReady = ready;
        Service.gI().sendReady(player);
        for (Player p : player.room.players) {
            Service.gI().sendRoom(p);
        }

    }

    public void startGame(Player player) {
        if (player.room == null) {
            Service.gI().sendNotificationis(player, "Cảnh báo",
                    "Hãy tham gia phòng trước khi bắt đầu trò chơi!");
            return;
        }
        if (player.room.owner != player) {
            Service.gI().sendNotificationis(player, "Cảnh báo",
                    "Chỉ chủ phòng mới có thể bắt đầu trò chơi!");
            return;
        }
        boolean allReady = player.room.players.stream().allMatch(p -> p.isReady);
        if (!allReady) {
            Service.gI().sendNotificationis(player, "Cảnh báo",
                    "Tất cả thành viên trong phòng phải sẵn sàng!");
            return;
        }
        for (Player p : player.room.players) {
            Service.gI().sendStartGame(p);
        }
    }

    public void playerMove(Player player, int x, int y) {
        if (player.room == null) {
            return;
        }
        player.location.x = x;
        player.location.y = y;
    }

}
