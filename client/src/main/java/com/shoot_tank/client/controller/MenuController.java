package com.shoot_tank.client.controller;


import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.shoot_tank.client.models.Player;
import com.shoot_tank.client.services.Service;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class MenuController implements Initializable {

    @FXML
    Label txtWelcome;

    @FXML
    private void onCreateRoom() throws Exception {
        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Tạo Phòng");
        dialog.setHeaderText("Nhập tên phòng và số lượng tối đa người chơi:");

        ButtonType createButtonType = new ButtonType("Tạo", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField roomName = new TextField();
        roomName.setPromptText("Tên phòng");
        TextField maxMembers = new TextField();
        maxMembers.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        }));
        maxMembers.setPromptText("Số lượng tối đa");

        grid.add(new Label("Tên phòng:"), 0, 0);
        grid.add(roomName, 1, 0);
        grid.add(new Label("Số lượng thành viên:"), 0, 1);
        grid.add(maxMembers, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> roomName.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Pair<>(roomName.getText(), Integer.parseInt(maxMembers.getText()));
            }
            return null;
        });

        Optional<Pair<String, Integer>> result = dialog.showAndWait();
        result.ifPresent(data -> {
            try {
                String name = data.getKey();
                int maxMembersz = data.getValue();

                if (maxMembersz <= 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText(null);
                    alert.setContentText("Số lượng thành viên phải lớn hơn 0!");
                    alert.showAndWait();
                    return;
                }

                Service.gI().sendCreateRoom(name, maxMembersz);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Số lượng thành viên không được để trống!");
                alert.showAndWait();
            }

        });
    }

    @FXML
    private void onJoinRoom() throws Exception {
        Service.gI().sendRequestListRoom();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtWelcome = new Label("Xin chào: " + Player.myChar().name);
    }
}
