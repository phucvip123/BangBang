package com.shot_tank.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.shot_tank.models.Player;
import com.shot_tank.services.Service;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RoomController implements Initializable {

    @FXML
    private Label txtRoomName;
    @FXML
    private Label txtDescription;
    @FXML
    private HBox listPlayers;
    @FXML
    private VBox rootVBox;

    @FXML
    private void onReady() {
        Service.gI().sendReady(!Player.myChar().isReady);

    }

    @FXML
    private void leaveRoom() {
        Service.gI().sendLeaveRoom();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtDescription.setText("Số người trong phòng: " + Player.myChar().room.description);
        boolean isOwner = Player.myChar().id.equals(Player.myChar().room.idOwner);
        for (int i = 0; i < Player.myChar().room.players.size(); i++) {
            VBox playerBox = new VBox(5);
            playerBox.setAlignment(Pos.CENTER);
            playerBox.setPadding(new Insets(10));
            playerBox.setPrefSize(120, 80); // ô vuông nhỏ gọn

            Player p = Player.myChar().room.players.get(i);

            Label nameLabel = new Label(p.name);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            Label statusLabel = new Label(p.isReady ? "Sẵn sàng" : "Chưa sẵn sàng");
            statusLabel.setStyle(p.isReady
                    ? "-fx-text-fill: green; -fx-font-size: 12px;"
                    : "-fx-text-fill: red; -fx-font-size: 12px;");

            playerBox.setStyle(
                    "-fx-background-color: #ecf0f1;" +
                            "-fx-border-color: #3498db;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;");
            if (p.id.equals(Player.myChar().room.idOwner)) {
                Label owner = new Label("Chủ phòng");
                owner.setStyle("-fx-font-weight: bold; -fx-text-fill: #2980b9;\r\n");
                playerBox.getChildren().addAll(owner);
            }

            playerBox.getChildren().addAll(nameLabel);

            if (p.id.equals(Player.myChar().id)) {
                Button btnReady = new Button(p.isReady ? "Huỷ sẵn sàng" : "Sẵn sàng");

                btnReady.setStyle(p.isReady
                        ? "-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;"
                        : "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");

                btnReady.setOnAction(e -> {
                    onReady();
                });
                playerBox.getChildren().addAll(btnReady);
            }else{
                playerBox.getChildren().addAll(statusLabel);
            }
            listPlayers.getChildren().add(playerBox);
        }

        txtRoomName.setText("Phòng: " + Player.myChar().room.name);

        if (isOwner) {
            Button btnStartGame = new Button("Bắt đầu");
            btnStartGame.setStyle(
                    "-fx-background-color: linear-gradient(to right, #27ae60, #2ecc71);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 8 20 8 20;" +
                            "-fx-background-radius: 20;" +
                            "-fx-cursor: hand;");

            btnStartGame.setOnMouseEntered(e -> btnStartGame.setStyle(
                    "-fx-background-color: linear-gradient(to right, #2ecc71, #27ae60);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 8 20 8 20;" +
                            "-fx-background-radius: 20;" +
                            "-fx-cursor: hand;"));

            btnStartGame.setOnMouseExited(e -> btnStartGame.setStyle(
                    "-fx-background-color: linear-gradient(to right, #27ae60, #2ecc71);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 8 20 8 20;" +
                            "-fx-background-radius: 20;" +
                            "-fx-cursor: hand;"));

            rootVBox.getChildren().add(btnStartGame);

        }
    }
}
