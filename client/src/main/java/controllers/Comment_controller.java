package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import interfaces.ControllerFunctions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

public class Comment_controller implements Initializable, ControllerFunctions{

    @FXML
    public ComboBox<String> ComboBox;

    @Override
    public void injectData(Object... data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'injectData'");
    }

    @Override
    public void init(Object... data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ComboBox.setValue("0");
        ObservableList<String> list = FXCollections.observableArrayList("0","1", "2", "3", "4", "5");
        ComboBox.setItems(list);
    }
    
}

