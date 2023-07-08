module application.client {
    requires javafx.fxml;
    requires transitive javafx.controls;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires java.rmi;
    

    opens application to javafx.fxml;

    exports application;
    exports controllers;
    exports utility;
    exports interfaces;
}