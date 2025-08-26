package com.shot_tank.controller;

import java.util.ArrayList;
import java.util.List;

import com.shot_tank.MainApp;
import com.shot_tank.models.Room;
import com.shot_tank.services.Service;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class ListRoomController {

    public static List<Room> rooms = new ArrayList<>();
    @FXML
    private TilePane roomGrid;

    @FXML
    private void onMenuClicked() {
        try {
            MainApp.setRoot("Menu.fxml", 400, 300);
        } catch (Exception e) {
        }
    }

    @FXML
    public void initialize() {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            VBox card = new VBox(5);
            card.setStyle("-fx-border-color:black; -fx-padding:10;");
            Label roomName = new Label("Room: " + room.name);
            Label ownerName = new Label("Owner: " + room.ownerName);
            Label description = new Label(room.description);
            Button joinBtn = new Button("Join");
            joinBtn.setOnAction(e -> {
                try {
                    Service.gI().sendJoinRoom(room.id);
                    MainApp.setRoot("Room.fxml", 500, 400);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            card.getChildren().addAll(roomName, ownerName, description, joinBtn);
            roomGrid.getChildren().add(card);
        }
    }
}
