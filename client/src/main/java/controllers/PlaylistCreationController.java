package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import application.SceneManager;
import interfaces.Injectable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import objects.Playlist;

public class PlaylistCreationController extends ControllerBase implements Initializable, Injectable 
{
    @FXML public Label labellTitle;
    @FXML public Label ErrorLabel;
    @FXML public ImageView playlistImage;
    @FXML public TextField playtlistName;
    @FXML public Button saveButton;

    private boolean editPlaylist = false;
    private String fieldDefaultStyle;
    private Playlist playlist = null;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       labellTitle.setText((Main.applicationLanguage == 0) ? "Creazione Playlist" : "Playlist Creation");
        fieldDefaultStyle = playtlistName.getStyle();
        ErrorLabel.setVisible(false);
    
    }

    @Override
    public void injectData(Object... data) {
        if(data.length == 1) {
            // String playlistName = (String) data[0];
            // Image img = (Image) data[1];
            playlist = (Playlist) data[0];

            labellTitle.setText((Main.applicationLanguage == 0) ? "Modifica Playlist" : "Playlist editor");
            playtlistName.setText(playlist.getName());
            //playlistImage.setImage(img);

            editPlaylist = true;
        }
    }

    @Override
    public void init(Object... data) {
        
    }

    @FXML
    public void saveData(ActionEvent event) throws Exception {

        String text = playtlistName.getText();

        if(text == null || text.length() <= 0) {
            ErrorLabel.setVisible(true);
            ErrorLabel.setText((Main.applicationLanguage == 0) ? "Inserire un nome per la playlist" : "Insert a playlist name");
            playtlistName.setStyle(fieldDefaultStyle + "-fx-border-width: 1.5px; -fx-border-radius: 6px; -fx-border-color: #F14934;");  
            return;
        }

        ErrorLabel.setVisible(false);
        playtlistName.setStyle(fieldDefaultStyle);
        boolean result = false;

        if(editPlaylist) {
            result = connectionManager.renamePlaylist(Main.account.getNickname(), playlist.getId(), text);
        }
        else {
            result = connectionManager.addPlaylist(text, Main.account.getNickname(), null); 
        }

        if(result) {
            sceneManager.closeWindow(SceneManager.ApplicationWinodws.PLAYLIST_CREATION_WINDOW);
            sceneManager.refreshScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW);
            
            //sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.MAIN_PAGE_PLAYLIST);
            return;
        }

        ErrorLabel.setVisible(true);
        ErrorLabel.setText((Main.applicationLanguage == 0) ? "Qualcosa è andato male" : "Something went wrong");
        playtlistName.setStyle(fieldDefaultStyle + "-fx-border-width: 1.5px; -fx-border-radius: 6px; -fx-border-color: #F14934;"); 
    } 
}
