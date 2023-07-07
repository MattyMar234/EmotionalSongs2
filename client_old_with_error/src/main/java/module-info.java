module application.client {
   
    requires javafx.fxml;
    requires transitive javafx.controls;
    requires transitive javafx.base;
    requires transitive javafx.graphics;



    opens application.client to javafx.fxml;
    
    exports application.client;
    exports application.controllers;
}