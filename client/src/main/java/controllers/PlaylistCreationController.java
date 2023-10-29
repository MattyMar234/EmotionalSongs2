package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import interfaces.Injectable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlaylistCreationController extends ControllerBase implements Initializable, Injectable 
{
    @FXML public Label labellTitle;
    @FXML public ImageView playlistImage;
    @FXML public TextField playtlistName;
    @FXML public Button saveButton;

    private boolean editPlaylist = false;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       labellTitle.setText((Main.applicationLanguage == 0) ? "Creazione Playlist" : "Playlist Creation");
    }

    @Override
    public void injectData(Object... data) {
        if(data.length == 2) {
            String playlistName = (String) data[0];
            Image img = (Image) data[1];

            labellTitle.setText((Main.applicationLanguage == 0) ? "Modifica Playlist" : "Playlist editor");
            playtlistName.setText(playlistName);
            playlistImage.setImage(img);

            editPlaylist = true;
        }
    }

    @Override
    public void init(Object... data) {
        
    }

    @FXML
    public void saveData(ActionEvent event) {

    }

    
}
