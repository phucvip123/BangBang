package com.shoot_tank.client.controller;


import com.shoot_tank.client.network.Session;
import com.shoot_tank.client.services.Service;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CharacterController {

    @FXML
    private TextField txtName;
    @FXML
    private Button okBtn;

    @FXML
    private void initialize() {
        okBtn.setDefaultButton(true);
    }

    @FXML
    private void onOkClick() {
        String name = txtName.getText();
        if (name.isEmpty())
            return;

        try {
            Session.mySession = new Session();
            Session.mySession.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Service.gI().sendCreatePlayer(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
