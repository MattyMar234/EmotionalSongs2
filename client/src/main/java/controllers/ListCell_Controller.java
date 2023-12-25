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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import objects.Album;
import objects.Artist;
import objects.Comment;
import objects.Playlist;
import objects.Song;
import objects.MyImage.ImageSize;

public class ListCell_Controller extends ControllerBase implements Initializable, Injectable
{
    private static final SceneManager sceneManager = SceneManager.instance();

    @FXML public Label label1;
    @FXML public Label label2;
    @FXML public Label timeLabel;
    @FXML public Label labelNumber;
    @FXML public Label labelData;

    @FXML public MenuButton actionButton;
    @FXML public AnchorPane anchor;
    @FXML public GridPane grid;

    @FXML public FontIcon exspandButton;
    @FXML public FontIcon spotifyButton;
    @FXML public FontIcon dataIcon;

    @FXML public ImageView image;

    //Utilizzate nell'header
    @FXML public VBox header_container1;
    @FXML public VBox header_container2;
    @FXML public VBox header_container3;
    @FXML public VBox header_container4;
    @FXML public VBox header_container5;
    @FXML public VBox header_container6;


    private FXMLLoader loaderFXML;
    private MainPage_ElementDisplayer_Controller displayer_Controller;
    private ListCell_DisplayMode mode;
    private Object element;
    private long rowNumber = 0;
    private Playlist playlist = null;


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

        if(this.mode == ListCell_DisplayMode.DISPLAY_SONG_and_DELETE) {
            this.playlist = (Playlist) data[4];
        }
        

        if(data.length >= 2) {
            this.element = data[1];
        }

        if(data.length >= 4) {
            this.rowNumber = (long) data[3];
        }
            
        
    
        switch (mode) {
            case DISPLAY_SONG ->                 setupAsSong();
            case DISPLAY_SONG_and_DELETE ->      setupAsSong();
            case DISPLAY_COMMENT ->              setupAsComment();
            case DISPLAY_PLAYLIST ->             setupAsPlaylist();
            case DISPLAY_ARTIST ->               setupAsArtist();
            case DISPLAY_ALBUM ->                setupAsAlbum();
            case SONG_HEADER ->                  setupAsSongHeader();
            case ALBUM_HEADER ->                 setupAsAlbumHeader();
            case ARTIST_HEADER ->                setupAsArtistHeader();
            case COMMENT_HEADER -> throw new UnsupportedOperationException("Unimplemented case: " + mode);
            case PLAYLIST_HEADER ->   setupAsPlaylistHeader();
            default -> throw new IllegalArgumentException("Unexpected value: " + mode);
        }
    }


    private void setupAsPlaylistHeader()
    {
        Label numerLabel = new Label("#");
        Label imageLable = new Label(Main.applicationLanguage == 0 ? "Immagine" : "Image");
        Label titleLable = new Label(Main.applicationLanguage == 0 ? "Titolo" : "Title");
        Label artistLable = new Label(Main.applicationLanguage == 0 ? "" : "");
        //FontIcon clockImage = new FontIcon("mdi2c-clock-outline");
        Label timeLabel = new Label(Main.applicationLanguage == 0 ? "Canzoni" : "Songs");
        Label azioniLable = new Label(Main.applicationLanguage == 0 ? "Azioni" : "Actions");

        dataIcon.setVisible(false);
        
        numerLabel.getStyleClass().add("Label-Style1");
        //imageLable.getStyleClass().add("Label-Style1");
        titleLable.getStyleClass().add("Label-Style1");
        artistLable.getStyleClass().add("Label-Style1");
        azioniLable.getStyleClass().add("Label-Style1");
        timeLabel.getStyleClass().add("Label-Style1");
        //clockImage.getStyleClass().add("generic-fontIcon-style");
        
        header_container1.getChildren().add(numerLabel);
        //header_container2.getChildren().add(imageLable);
        header_container3.getChildren().add(titleLable);
        header_container4.getChildren().add(artistLable);
        header_container5.getChildren().add(timeLabel);
        header_container6.getChildren().add(azioniLable);
    }


    private void setupAsSongHeader()
    {
        Label numerLabel = new Label("#");
        Label imageLable = new Label(Main.applicationLanguage == 0 ? "Immagine" : "Image");
        Label titleLable = new Label(Main.applicationLanguage == 0 ? "Titolo" : "Title");
        Label artistLable = new Label(Main.applicationLanguage == 0 ? "Artista" : "Artist");
        FontIcon clockImage = new FontIcon("mdi2c-clock-outline");
        Label azioniLable = new Label(Main.applicationLanguage == 0 ? "Azioni" : "Actions");
        
        numerLabel.getStyleClass().add("Label-Style1");
        imageLable.getStyleClass().add("Label-Style1");
        titleLable.getStyleClass().add("Label-Style1");
        artistLable.getStyleClass().add("Label-Style1");
        azioniLable.getStyleClass().add("Label-Style1");
        clockImage.getStyleClass().add("generic-fontIcon-style");
        
        header_container1.getChildren().add(numerLabel);
        header_container2.getChildren().add(imageLable);
        header_container3.getChildren().add(titleLable);
        header_container4.getChildren().add(artistLable);
        header_container5.getChildren().add(clockImage);
        header_container6.getChildren().add(azioniLable);

    }


    private void setupAsSong() {
        final Song song = (Song)element;

          
        label1.setText(song.getTitle());
        timeLabel.setText(convertTime(song.getDurationMs()));
        labelNumber.setText(String.valueOf(rowNumber));
        //grid.getRowConstraints().remove(1);
        new Thread(() -> {
            Album album = connectionManager.getAlbum_by_ID(song.getAlbumId());

            if(album != null) 
            {
                Platform.runLater(() -> {
                    labelData.setText(album.getReleaseDate());
                });

                Artist artist = connectionManager.getArtistByID(album.getArtistID());

                if(artist != null) {
                    Platform.runLater(() -> {
                        label2.setText(artist.getName());
                    });
                }
                else {
                    Platform.runLater(() -> {
                        label2.setText("");
                    });
                }
            }
            else {
                Platform.runLater(() -> {
                    labelData.setText("");
                    label2.setText("");
                });
            }
        }).start();

        new Thread(() -> {
            
            try {
                Image img = super.download_Image_From_Internet(song.getImage(ImageSize.S64x64).getUrl(), true);
                Platform.runLater(() -> {
                    image.setImage(img);
                });
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }).start();


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
            new Thread(() -> {
                
                Menu pMenu = new Menu(Main.applicationLanguage == 0 ? "Aggiungi ad una playlist" : "Add to a playlist");
                actionButton.getItems().add(pMenu);

                try {
                    ArrayList<Playlist> playlist_list = connectionManager.getAccountPlaylists(Main.account.getNickname());
                    
                    Platform.runLater(() -> {
                        for (Playlist playlist : playlist_list) 
                        {
                            if(this.playlist != null && playlist.equals(this.playlist)) {
                                continue;
                            }
                            
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

                    if(this.mode == ListCell_DisplayMode.DISPLAY_SONG_and_DELETE) {
                        Menu deleteMenu = new Menu(Main.applicationLanguage == 0 ? "Rimuovi dalla playlist" : "Remove from playlist");
                        actionButton.getItems().add(deleteMenu);
                        
                        deleteMenu.setOnAction(event -> {
                            try {
                                connectionManager.removeSongFromPlaylist(Main.account.getNickname(), this.playlist.getId(), song.getId());
                                sceneManager.refreshScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW);
                            } 
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } 
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }).start();
        }
        else {
            actionButton.setDisable(true);
        }      
    }

    /**
     * Funzione per configurare l'oggetto per visulizzare le informazioni di una playlist 
     */
    private void setupAsPlaylist() {
        Playlist playlist = (Playlist)element;
        label1.setText(playlist.getName());
        //label2.setText(playlist.getId());

        ArrayList<Song> result;
        try {
            result = connectionManager.getPlaylistSongs(playlist.getId());
            timeLabel.setText(Integer.toString(result.size()));
        } 
        catch (Exception e) {
            timeLabel.setText("0");
            e.printStackTrace();
        }

    
        MenuItem deleteItem = new MenuItem(Main.applicationLanguage == 0 ? "Elimina Playlist" : "Delete Playlist");
        MenuItem renameItem = new MenuItem(Main.applicationLanguage == 0 ? "Rinomina Playlist" : "Rename Playlist");

        HBox hb = (HBox) actionButton.getParent();
        hb.getChildren().clear();
        hb.getChildren().add(actionButton);

        actionButton.getItems().addAll(renameItem, deleteItem);

        //exspandButton.setVisible(false);
        //spotifyButton.setVisible(false);

        labelNumber.setText(String.valueOf(rowNumber));

        //funzione 
        renameItem.setOnAction(event -> {
            sceneManager.startWindow(SceneManager.ApplicationWinodws.PLAYLIST_CREATION_WINDOW, playlist);
        });

        //cancello la playlist
        deleteItem.setOnAction(event -> {
           if((boolean) connectionManager.deletePlaylist(Main.account.getNickname(), playlist.getId())) {
               sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.MAIN_PAGE_PLAYLIST);
           }
        });

        grid.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent e){
                sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, ElementDisplayerMode.SHOW_PLAYLIST, playlist);
            }
        });

    }


    private void setupAsArtistHeader() {
        Label numerLabel = new Label("#");
        Label imageLable = new Label(Main.applicationLanguage == 0 ? "Immagine" : "Image");
        Label titleLable = new Label(Main.applicationLanguage == 0 ? "Nome Artista" : "Artist name");
        Label artistLable = new Label(Main.applicationLanguage == 0 ? "Followers" : "Followers");
        //FontIcon clockImage = new FontIcon("mdi2c-clock-outline");
        Label timeLabel = new Label(Main.applicationLanguage == 0 ? "" : "");
        Label azioniLable = new Label(Main.applicationLanguage == 0 ? "Azioni" : "Actions");

        dataIcon.setVisible(false);
        
        
        numerLabel.getStyleClass().add("Label-Style1");
        //imageLable.getStyleClass().add("Label-Style1");
        titleLable.getStyleClass().add("Label-Style1");
        artistLable.getStyleClass().add("Label-Style1");
        azioniLable.getStyleClass().add("Label-Style1");
        timeLabel.getStyleClass().add("Label-Style1");
        //clockImage.getStyleClass().add("generic-fontIcon-style");
        
        header_container1.getChildren().add(numerLabel);
        //header_container2.getChildren().add(imageLable);
        header_container3.getChildren().add(titleLable);
        header_container4.getChildren().add(artistLable);
        header_container5.getChildren().add(timeLabel);
        header_container6.getChildren().add(azioniLable);
    }

    private void setupAsArtist() 
    {
        final Artist artist = (Artist)element;
        label1.setText(artist.getName());
        
        String.valueOf(rowNumber);
        String str = Long.toString(artist.getFollowers());
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        
        int counter = 0;
        for(int i = str.length() - 1; i >= 0; i--) {
            if(counter++ % 3 == 0 && counter != 0)
                sb.append(".");
            sb.append(str.charAt(i));
        }

        String s = sb.toString();

        for(int i = 0; i < s.length(); i++) {
            if(s.length() - 1 == i && s.charAt(s.length() - 1 - i) == '.')
                continue;

            sb2.append(s.charAt(s.length() - 1 - i));
        }
    

        
        
        label2.setText(sb2.toString());
        labelNumber.setText(String.valueOf(rowNumber));
        labelData.setText("");
        timeLabel.setText("");

        HBox hb = (HBox) actionButton.getParent();
        hb.getChildren().clear();
        hb.getChildren().add(spotifyButton);
        hb.getChildren().add(exspandButton);  

        new Thread(() -> {
            
            String link = null;

            try {link = artist.getImage(ImageSize.S64x64).getUrl();} catch (Exception e) {}
            try {link = artist.getImage(ImageSize.S160x160).getUrl();} catch (Exception e) {}
            try {link = artist.getImage(ImageSize.S300x300).getUrl();} catch (Exception e) {}
            try {link = artist.getImage(ImageSize.S320x320).getUrl();} catch (Exception e) {}
            try {link = artist.getImage(ImageSize.S640x640).getUrl();} catch (Exception e) {}

            try {
                Image img = super.download_Image_From_Internet(link, true);
                Platform.runLater(() -> {
                    image.setImage(img);
                });
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }).start();

        spotifyButton.setOnMouseClicked(event -> 
        {
            try {
                super.openLink(artist.getSpotifyURL());
            } 
            catch (IOException e) {
                e.printStackTrace();
            } 
            catch (URISyntaxException e) {
                System.out.println(e);
                System.out.println("Invalid url " + artist.getSpotifyURL());
                e.printStackTrace();
            }
        });

        exspandButton.setOnMouseClicked(event -> {
            sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, ElementDisplayerMode.SHOW_ARTIST_SONGS, artist);
        });

    }


    private void setupAsAlbumHeader() {
        Label numerLabel = new Label("#");
        Label imageLable = new Label(Main.applicationLanguage == 0 ? "Immagine" : "Image");
        Label titleLable = new Label(Main.applicationLanguage == 0 ? "Titolo" : "Title");
        Label artistLable = new Label(Main.applicationLanguage == 0 ? "Artista" : "Artist");
        //FontIcon clockImage = new FontIcon("mdi2c-clock-outline");
        Label timeLabel = new Label(Main.applicationLanguage == 0 ? "" : "");
        Label azioniLable = new Label(Main.applicationLanguage == 0 ? "Azioni" : "Actions");

        //dataIcon.setVisible(false);
        
        numerLabel.getStyleClass().add("Label-Style1");
        //imageLable.getStyleClass().add("Label-Style1");
        titleLable.getStyleClass().add("Label-Style1");
        artistLable.getStyleClass().add("Label-Style1");
        azioniLable.getStyleClass().add("Label-Style1");
        timeLabel.getStyleClass().add("Label-Style1");
        //clockImage.getStyleClass().add("generic-fontIcon-style");
        
        header_container1.getChildren().add(numerLabel);
        //header_container2.getChildren().add(imageLable);
        header_container3.getChildren().add(titleLable);
        header_container4.getChildren().add(artistLable);
        header_container5.getChildren().add(timeLabel);
        header_container6.getChildren().add(azioniLable);
    }

    private void setupAsAlbum() {

        final Album album = (Album)element;

        label1.setText(album.getName());
        labelNumber.setText(String.valueOf(rowNumber));
        labelData.setText(album.getReleaseDate());
        timeLabel.setText("");

        HBox hb = (HBox) actionButton.getParent();
        hb.getChildren().clear();
        hb.getChildren().add(spotifyButton);
        hb.getChildren().add(exspandButton);

        Artist artist = connectionManager.getArtistByID(album.getArtistID());

        if(artist != null) {
            Platform.runLater(() -> {
                label2.setText(artist.getName());
            });
        }
        else {
            Platform.runLater(() -> {
                label2.setText("");
            });
        }

        new Thread(() -> {
            
            try {
                Image img = super.download_Image_From_Internet(album.getImage(ImageSize.S64x64).getUrl(), true);
                Platform.runLater(() -> {
                    image.setImage(img);
                });
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }).start();


        spotifyButton.setOnMouseClicked(event -> 
        {
            try {
                super.openLink(album.getSpotifyURL());
            } 
            catch (IOException e) {
                e.printStackTrace();
            } 
            catch (URISyntaxException e) {
                System.out.println(e);
                System.out.println("Invalid url " + album.getSpotifyURL());
                e.printStackTrace();
            }
        });

        exspandButton.setOnMouseClicked(event -> {
            sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, ElementDisplayerMode.SHOW_ALBUM, album);
        });

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
