package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.simple.ItemList;
import org.kordamp.ikonli.javafx.FontIcon;

import application.Main;
import application.SceneManager;
import application.SceneManager.FXML_elements;
import enumClasses.ElementDisplayerMode;
import enumClasses.ListCell_DisplayMode;
import interfaces.Injectable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import objects.Album;
import objects.Comment;
import objects.Playlist;
import objects.Song;

public class ListCell_Controller extends ControllerBase implements Initializable, Injectable
{
    private static final SceneManager sceneManager = SceneManager.instance();

    @FXML public Label label1;
    @FXML public Label label2;
    @FXML public Label timeLabel;
    @FXML public MenuButton actionButton;
    @FXML public AnchorPane anchor;
    @FXML public GridPane grid;

    @FXML public FontIcon exspandButton;
    @FXML public FontIcon spotifyButton;


    private FXMLLoader loaderFXML;
    private MainPage_ElementDisplayer_Controller displayer_Controller;
    private ListCell_DisplayMode mode;
    private Object element;

    public ListCell_Controller() {
        super();
    }

    public ListCell_Controller(Object...args) {
        this.displayer_Controller = (MainPage_ElementDisplayer_Controller)args[0];
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    //SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene
    
    @Override
    public void injectData(Object... data) 
    {
        if(!(data[0] instanceof ListCell_DisplayMode))
            throw new RuntimeException("invalid configuration for list cell. Arg[0] must be a \"ListCell_DisplayMode\" type");
        
        this.mode = (ListCell_DisplayMode)data[0];
        this.element = data[1];

        switch (mode) {
            case DISPLAY_SONG -> {
                setupAsSong();
            }
            case DISPLAY_COMMENT -> {
                setupAsComment();
            }
            case DISPLAY_PLAYLIST -> {
                setupAsPlaylist();
            }
            case DISPLAY_ALBUM -> {
                setupAsAlbum();
            }
        }
    }

    private void setupAsSong() {
        final Song song = (Song)element;

          
        label1.setText(song.getTitle());
        timeLabel.setText(convertTime(song.getDurationMs()));
        //grid.getRowConstraints().remove(1);


        spotifyButton.setOnMouseClicked(event -> 
        {
            try {
                super.openLink(song.getSpotifyUrl());
            } 
            catch (IOException e) {
                e.printStackTrace();
            } 
            catch (URISyntaxException e) {
                System.out.println(e);
                System.out.println("Invalid url " + song.getSpotifyUrl());
                e.printStackTrace();
            }
        });

        exspandButton.setOnMouseClicked(event -> {
            System.out.println(song);
            sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, ElementDisplayerMode.SHOW_SONG, song);
        });

        if(Main.account != null) {
            Menu pMenu = new Menu(Main.applicationLanguage == 0 ? "Aggiungi ad una playlist" : "Add to a playlist");
            actionButton.getItems().add(pMenu);

            new Thread(() -> {
                try {
                    ArrayList<Playlist> playlist_list = connectionManager.getAccountPlaylists(Main.account.getNickname());
                    
                    Platform.runLater(() -> {
                        for (Playlist playlist : playlist_list) {
                            MenuItem item = new MenuItem(playlist.getName());
                            pMenu.getItems().add(item);
                            
                            item.setOnAction(event -> {
                                try {
                                    connectionManager.addSongToPlaylist(playlist.getUserID(), playlist.getId(), song.getId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });
                } 
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }).start();
        }      
    }

    /**
     * Funzione per configurare l'oggetto per visulizzare le informazioni di una playlist 
     */
    private void setupAsPlaylist() {
        Playlist playlist = (Playlist)element;
        label1.setText(playlist.getName());
        label2.setText(playlist.getId());

        MenuItem deleteItem = new MenuItem(Main.applicationLanguage == 0 ? "Elimina Playlist" : "Delete Playlist");
        MenuItem renameItem = new MenuItem(Main.applicationLanguage == 0 ? "Rinomina Playlist" : "Rename Playlist");
        actionButton.getItems().addAll(renameItem, deleteItem);

        exspandButton.setVisible(false);
        spotifyButton.setVisible(false);

        //funzione 
        renameItem.setOnAction(event -> {
            
        });

        //cancello la playlist
        deleteItem.setOnAction(event -> {
           if((boolean)connectionManager.deletePlaylist(Main.account.getNickname(), playlist.getId()));
            sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.MAIN_PAGE_PLAYLIST);
        });

        grid.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent e){
                sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, ElementDisplayerMode.SHOW_PLAYLIST, playlist);
            }
        });

    }

    private void setupAsAlbum() {


    }

    private void setupAsComment() {

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

    


    


}
