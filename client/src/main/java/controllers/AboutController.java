package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import interfaces.Injectable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class AboutController extends ControllerBase implements Initializable, Injectable{

    @FXML public Label label1;
    
    @Override
    public void injectData(Object... data) {
 
    }

    @Override
    public void init(Object... data) {
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label1.setText(Main.applicationLanguage == 0 ? "Questo software è stato sviluppato da:" : "This software was developed by:");
    }
    
}
