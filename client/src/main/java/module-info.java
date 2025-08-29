module com.shoot_tank.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    
    opens com.shoot_tank.client to javafx.fxml;
    opens com.shoot_tank.client.controller to javafx.fxml;

    exports com.shoot_tank.client;
    exports com.shoot_tank.client.controller;
    exports com.shoot_tank.client.models;
}