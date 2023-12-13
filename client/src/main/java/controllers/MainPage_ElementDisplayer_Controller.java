package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.Main;
import application.ObjectsCache;
import application.SceneManager;
import application.SceneManager.FXML_elements;
import application.SceneManager.SceneElemets;
import enumClasses.ElementDisplayerMode;
import enumClasses.ListCell_DisplayMode;
import interfaces.Injectable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import objects.Album;
import objects.Artist;
import objects.Comment;
import objects.Emotion;
import objects.MyImage;
import objects.Playlist;
import objects.Song;
import utility.UtilityOS;


public class MainPage_ElementDisplayer_Controller extends ControllerBase implements Initializable, Injectable {


    @FXML public Label labelName;
    @FXML public Label labelType;
    @FXML public Label objectsLabel;
    @FXML public ImageView image;

    @FXML public VBox elementContainer;
    @FXML public Button actionButton;


    @FXML public ListView<Object> listView;
    private Object displayedElement;
    private ElementDisplayerMode mode;
    private ObservableList<Object> ObservableList = FXCollections.observableArrayList();


    private Image img = null;
    private String imgURL = "";
    private String spotifyUrl = "";
    

    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actionButton.setDisable(true);
        actionButton.setVisible(false);

        linearGradien_background_upper.setStyle("-fx-background-color: linear-gradient(to top, #030300, #060600);");
    }



    @Override
    public void injectData(Object... data) 
    {
        if(!(data[0] instanceof ElementDisplayerMode))
            throw new RuntimeException("invalid configuration");
      
        if(data.length < 2)
            throw new RuntimeException("missing data");
            
        this.mode = (ElementDisplayerMode)data[0];
        this.displayedElement = data[1];

        Platform.runLater(() -> {
            try {
                switch (mode) {
                    case SHOW_ALBUM:
                        if(!(displayedElement instanceof Album)) 
                            throw new RuntimeException("obejct type must be \"Album\" type");
                        setupAsAlbum(data);
                        setImage_and_backgroundColor();    
                        setImageLink();
                        break;
                        
                    case SHOW_ARTIST:
                        if(!(displayedElement instanceof Album)) 
                            throw new RuntimeException("obejct type must be \"Artist\" type");
                        
                        setupAsArtist(data);  
                        setImage_and_backgroundColor();  
                        setImageLink();
                        break;
                    case SHOW_PLAYLIST:
                        if(!(displayedElement instanceof Playlist)) 
                            throw new RuntimeException("obejct type must be \"Playlist\" type");
                        
                        setupAsPlaylist(data);   
                        //setImage_and_backgroundColor(); 
                        //setImageLink();
                        break;
                    case SHOW_SONG:
                        if(!(displayedElement instanceof Song)) 
                            throw new RuntimeException("obejct type must be \"Song\" type");
                        
                        setupAsSong(data);  
                        setImage_and_backgroundColor();
                        setImageLink();
                        
                        break;
                    case SHOW_USER_PLAYLISTS:
                        setupAsPlaylistShower(data);
                        break; 
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    protected void refreshData()
    {

    }

    private void setImageLink() {
        final String url = spotifyUrl;
        image.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    openLink(url);
                } 
                catch (IOException e) {
                    e.printStackTrace();
                } 
                catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setImage_and_backgroundColor() {
        
        if(!imgURL.equals("")) {
            
        }

        img = (Image) ObjectsCache.getInstance().getItem(ObjectsCache.CacheObjectType.IMAGE,imgURL);

        if(img == null) {
            new Thread(() -> {
                try {
                    //image = Main.imageDownloader.addImageToDownload(imgURL);  
                    img = super.download_Image_From_Internet(imgURL, true);
                    Color everegedColor = getAverageColor(img, 0.3f);
                    String color = ColorToHex(everegedColor);
                    linearGradien_background_upper.setStyle("-fx-background-color: linear-gradient(to top, #030300, "+ color +");");
                    
                    Platform.runLater(() -> {
                        image.setImage(img);
                    });
                    
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        else {
            Color everegedColor = getAverageColor(img, 0.3f);
            String color = ColorToHex(everegedColor);
            linearGradien_background_upper.setStyle("-fx-background-color: linear-gradient(to top, #030300, "+ color +");");
            image.setImage(img);
        }   
    }



    @Override
    public void init(Object... data) {
       
    }



    private void setupAsSong(Object... data) 
    { 
        final Song song = (Song) displayedElement;
        
        try {
            ArrayList<Emotion> list = connectionManager.getEmotions(song.getId());

            if(list.size() != 0) {
                EmotionsChart emotionsChart = (EmotionsChart) SceneManager.instance().injectScene(SceneManager.SceneElemets.CHART.getElemetFilePath(), elementContainer);
                emotionsChart.injectData(list);
            }

            CommentArea commentArea = (CommentArea)SceneManager.instance().injectScene(SceneManager.SceneElemets.COMMENT_AREA.getElemetFilePath(), elementContainer);
            commentArea.injectData(song);

            for (Emotion emotion : list) {
                boolean canBeDeleted = emotion.getID_Account().equals(Main.account.getNickname());
                CommentListCell_Controller controller = (CommentListCell_Controller)SceneManager.instance().injectScene(SceneManager.SceneElemets.COMMENT_VIEW.getElemetFilePath(), elementContainer, canBeDeleted);
                controller.injectData(emotion);
            }
        } 
        catch (Exception e) {
           System.out.println(e);
            e.printStackTrace();
        }

        labelName.setText(song.getTitle());
        labelType.setText("Song");

        imgURL = song.getImage(MyImage.ImageSize.S300x300).getUrl();
        spotifyUrl = song.getSpotifyUrl();

        System.out.println(imgURL);

        //listView.setVisible(false);
    }   



    private void setupAsAlbum(Object... data) throws Exception 
    {
        final Album album = (Album) displayedElement;

        labelName.setText(album.getName());
        labelType.setText("Album");

        imgURL = ((Album) displayedElement).getImage(MyImage.ImageSize.S300x300).getUrl();
        album.getSpotifyURL();

        
        new Thread(() -> {
            try {
                //ObservableList.addAll(songs);
                ArrayList<Song> songs = connectionManager.getAlbumSongs(album.getID());
                
            
                Platform.runLater(() -> {
                    ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_HEADER, elementContainer);
                    listCell.injectData(ListCell_DisplayMode.SONG_HEADER);
                });
                long i = 1;
                for (Song song : songs) 
                {
                    final long j = i;
                    System.out.println("loading element: " + song.getTitle());
                
                    Platform.runLater(() -> {
                        ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_ELEMENT, elementContainer);
                        listCell.injectData(ListCell_DisplayMode.DISPLAY_SONG, song, null, j);
                    });
                    i++;
                } 
            } 
            catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }).start();
    }

    private void setupAsArtist(Object... data) 
    {
        

    }

    private void setupAsPlaylist(Object... data)
    {
        setBackgroundLinearColor(ControllerBase.backgroundImageIndex);
        image.setImage(new Image(UtilityOS.formatPath(Main.ImageFolder + "\\icon\\playlistIcon.png")));

        final Playlist playlist = (Playlist) displayedElement;
        labelName.setText((Main.applicationLanguage == 0 ? "La mia playlist " : "My playlist ") + playlist.getName());
        labelType.setText("Playlist");
        objectsLabel.setText(Main.account.getNickname());
        

        new Thread(() -> {
            try {
                ArrayList<Song> song_list = connectionManager.getPlaylistSongs(playlist.getId());

            
                Platform.runLater(() -> {
                    ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_HEADER, elementContainer);
                    listCell.injectData(ListCell_DisplayMode.SONG_HEADER);
                });

                //carico le playlist
                try {
                    long i = 1;
                    for (Song song : song_list){
                        final long j = i;
                        Platform.runLater(() -> {
                            
                            ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_ELEMENT, elementContainer);
                            listCell.injectData(ListCell_DisplayMode.DISPLAY_SONG, song, null, j);
                        });
                        i++;
                    } 
                } 
                catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupAsPlaylistShower(Object... data) 
    {
        setBackgroundLinearColor(ControllerBase.backgroundImageIndex);
        image.setImage(new Image(UtilityOS.formatPath(Main.ImageFolder + "\\icon\\playlistIcon.png")));
        labelName.setText(Main.applicationLanguage == 0 ? "Le tue playlist" : "Your playlist");
        labelType.setText("");
        objectsLabel.setText("");
        
        //linearGradien_background_upper.setStyle("-fx-background-color: linear-gradient(to top, #030300, "+ "#050500" +");");

        actionButton.setDisable(false);
        actionButton.setVisible(true);
        actionButton.setText(Main.applicationLanguage == 0 ? "Crea una nuova playlist" : "Create a new playlist");

        actionButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sceneManager.startWindow(SceneManager.ApplicationWinodws.PLAYLIST_CREATION_WINDOW, null);
            }
        });

        new Thread(() -> {
            try {
                ArrayList<Playlist> playlist_list = connectionManager.getAccountPlaylists(Main.account.getNickname());

            
                Platform.runLater(() -> {
                    ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_HEADER, elementContainer);
                    listCell.injectData(ListCell_DisplayMode.PLAYLIST_HEADER);
                });

                //carico le playlist
                try {
                    long i = 1;
                    for (Playlist p : playlist_list){
                        final long j = i;
                        Platform.runLater(() -> {
                            ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_ELEMENT, elementContainer);
                            listCell.injectData(ListCell_DisplayMode.DISPLAY_PLAYLIST, p, null, j);
                        });
                        i++;
                    } 
                } 
                catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    


    public static Color brightenColor(Color originalColor, double factor) {
        double r = Math.min(originalColor.getRed() + factor, 1.0);
        double g = Math.min(originalColor.getGreen() + factor, 1.0);
        double b = Math.min(originalColor.getBlue() + factor, 1.0);

        return new Color(r, g, b, originalColor.getOpacity());
    }

}
