package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import objects.Emotion;
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
        if(Main.applicationLanguage == 0) {
            nameLabel.setText("Nome: " + Main.account.getName());
            surnameLabel.setText("Cognome: " + Main.account.getSurname());
            accountIDLabel.setText("ID utente: " + Main.account.getNickname());
            emailLabel.setText("Email: " + Main.account.getEmail());
            codeLabel.setText("Codice Fiscale: " + Main.account.getFiscalCode());
            viaLabel.setText("Via: " + Main.account.getResidenza().getViaPiazza());
            communeLabel.setText("Comune: " + Main.account.getResidenza().getCouncilName());
            provinceLabel.setText("Provinica: " + Main.account.getResidenza().getProvinceName());
            infoUserLabel.setText("Informazioni Utente");
        }
        else {
            nameLabel.setText("Name:" + Main.account.getName());
            surnameLabel.setText("Surname:" + Main.account.getSurname());
            accountIDLabel.setText("User ID:" + Main.account.getNickname());
            emailLabel.setText("Email: " + Main.account.getEmail());
            codeLabel.setText("Fiscal code: " + Main.account.getFiscalCode());
            viaLabel.setText("Via: " + Main.account.getResidenza().getViaPiazza());
            communeLabel.setText("commune: " + Main.account.getResidenza().getCouncilName());
            provinceLabel.setText("Province: " + Main.account.getResidenza().getProvinceName());
            infoUserLabel.setText("User Information");
        }

        capLabel.setText("CAP: " + Main.account.getResidenza().getCAP());
        

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

        new Thread(() -> {
            try {
                ArrayList<Emotion> list = connectionManager.getAccountEmotions(Main.account.getNickname());
                
                //System.out.println("list size: " + list.size());
                Platform.runLater(() -> {
                    contValoreLabel.setText(Integer.toString(list.size()));
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
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("");
        alert.setHeaderText(Main.applicationLanguage == 0 ? "Eliminazione Account" : "Delete Account");
    
        if(Main.applicationLanguage == 0) {
            alert.setContentText("Sei sicuro di volor eliminare il tuo Account ?");
        }
        else if(Main.applicationLanguage == 1) {
            alert.setContentText("are you sure you want to delete your account ?");
        }

        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) 
        {
            if(!connectionManager.deleteAccount(Main.account.getNickname())) {
                Alert alert2 = new Alert(AlertType.ERROR);
                alert2.setHeaderText(Main.applicationLanguage == 0 ? "Operazione fallita" : "Operation failed");
                alert2.setTitle("");
                alert2.setContentText("");
                alert.getButtonTypes().setAll(ButtonType.OK);
                alert.showAndWait();
                return;
            }
            SceneManager.instance().setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.ACCESS_PAGE);
            Main.account = null;
        }
    }

    
}
