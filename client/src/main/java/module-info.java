module application.client {
    requires transitive javafx.fxml;
    requires transitive javafx.controls;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.swing;
  
    
    requires java.rmi;

    //https://stackoverflow.com/questions/61183565/unable-to-import-certain-classes-in-fontawesomefx-after-updating-for-java-9-ja
    requires de.jensd.fx.glyphs.commons;
    requires de.jensd.fx.glyphs.materialdesignicons;
    requires de.jensd.fx.glyphs.fontawesome;

    requires transitive json.simple;
    requires transitive javatuples;
    requires transitive org.kordamp.ikonli.javafx;

    opens application to javafx.fxml;

    exports application;
    exports controllers;
    exports utility;
    exports interfaces;
    exports applicationEvents;
    exports objects;
}