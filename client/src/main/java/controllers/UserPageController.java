package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.Main;
import application.SceneManager;
import application.SceneManager.SceneElemets;
import enumClasses.ListCell_DisplayMode;
import interfaces.Injectable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import objects.Playlist;

public class UserPageController extends ControllerBase implements Initializable, Injectable{

    @FXML public Label accountIDLabel;
    @FXML public Label capLabel;
    @FXML public Label codeLabel;
    @FXML public Label commentLabel;
    @FXML public Label communeLabel;
    @FXML public Label contValoreLabel;
    @FXML public Label contibuzioneLabel;
    @FXML public Button deleteButton;
    @FXML public Label emailLabel;
    @FXML public Label infoUserLabel;
    @FXML public Label nameLabel;
    @FXML public Label playlistCountLabel;
    @FXML public Label playlistLabel;
    @FXML public Label surnameLabel;
    @FXML public Label viaLabel;
    @FXML public Label provinceLabel;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        nameLabel.setText(Main.account.getName());
        surnameLabel.setText(Main.account.getSurname());
        accountIDLabel.setText(Main.account.getNickname());
        emailLabel.setText(Main.account.getEmail());
        codeLabel.setText(Main.account.getFiscalCode());
        viaLabel.setText(Main.account.getResidenza().getViaPiazza());
        communeLabel.setText(Main.account.getResidenza().getCouncilName());
        provinceLabel.setText(Main.account.getResidenza().getProvinceName());

        contibuzioneLabel.setText(Main.applicationLanguage == 0 ? "Attività utente" : "User Activity");
        playlistLabel.setText(Main.applicationLanguage == 0 ? "Playlist create" : "Playlist created");

        new Thread(() -> {
            try {
                ArrayList<Playlist> playlist_list = connectionManager.getAccountPlaylists(Main.account.getNickname());
                Platform.runLater(() -> {
                    playlistCountLabel.setText(Integer.toString(playlist_list.size()));
                });  
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
    }
    
    @Override
    public void injectData(Object... data) {
       
    }

    @Override
    public void init(Object... data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }


    @FXML
    public void deleteAccount(ActionEvent event) {

    }

    
}
