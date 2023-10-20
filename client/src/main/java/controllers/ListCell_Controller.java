package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.SceneManager;
import application.SceneManager.SceneElements;
import interfaces.ControllerFunctions;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import objects.Album;
import objects.Comment;
import objects.Playlist;
import objects.Song;

public class ListCell_Controller implements Initializable, ControllerFunctions
{
    private static final SceneManager sceneManager = SceneManager.getInstance();

    @FXML public Label label1;
    @FXML public Label label2;
    @FXML public Label timeLabel;
    @FXML public MenuButton playlistMenuBtn;
    @FXML public AnchorPane anchor;


    private FXMLLoader loaderFXML;
    private MainPage_ElementDisplayer_Controller displayer_Controller;


    public ListCell_Controller(Object...args) {
        this.displayer_Controller = (MainPage_ElementDisplayer_Controller)args[0];
    }

    public ListCell_Controller() {
        
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }


    @Override
    public void injectData(Object... data) 
    {
        Object element = data[0];

        if(element instanceof Song) {
            try {
                Song song = (Song)element;


                    //set fxml tags
                label1.setText(String.valueOf("??"));
                label2.setText(song.getTitle());
                timeLabel.setText(convertTime(song.getDurationMs()));

                //setText(null);
                //setGraphic(anchor);
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(element instanceof Comment) {
            
        } 
        else if(element instanceof Playlist) {
            label1.setText(String.valueOf("??"));
            label2.setText("platlist title");
        }
    }

    public static String convertTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
        return formattedTime;
    }


    @Override
    public void init(Object... data) {
       
    }

    /*
    @Override
    protected void updateItem(Object element, boolean empty) 
    {
        super.updateItem(element, empty);

        if(empty || element == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loaderFXML == null) {

                if(element instanceof Song) {
                    try {
                        Song song = (Song)element;

                        loaderFXML = sceneManager.getSceneLoader(SceneElements.SONG_LIST_VIEW);
                        loaderFXML.setController(this);
                        loaderFXML.load();

                         //set fxml tags
                        label1.setText(String.valueOf("??"));
                        label2.setText(song.getTitle());

                        setText(null);
                        setGraphic(anchor);
                    } 
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(element instanceof Comment) {
                    
                } 
                

                
                

            }

           
        }

    }*/

    @FXML
    public void showPlaylist(MouseEvent event) {
        playlistMenuBtn.show(); // Show menu when mouse enters
    }

    @FXML
    public void hidePlaylist(MouseEvent event) {
        playlistMenuBtn.hide(); // Hide menu when mouse exits the button's area
    }


    


}
