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
import enumClasses.ElementDisplayerMode;
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

    @FXML public AnchorPane linearColorAnchorPane;
    @FXML public AnchorPane blackColorAnchorPane;

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
    }



    @Override
    public void injectData(Object... data) 
    {
        if(!(data[0] instanceof ElementDisplayerMode))
            throw new RuntimeException("invalid configuration");
        
        this.mode = (ElementDisplayerMode)data[0];

        if(data.length >= 2)
            this.displayedElement = data[1];

        Platform.runLater(() -> {
            try {
                switch (mode) {
                    case SHOW_ALBUM:
                        if(displayedElement instanceof Album) {
                            setupAsAlbum(data);
                            setImage_and_backgroundColor();    
                            setImageLink();
                        }
                        break;
                    case SHOW_ASRTIST:
                        if(displayedElement instanceof Artist) {
                            setupAsArtist(data);  
                            setImage_and_backgroundColor();  
                            setImageLink();
                        }
                        break;
                    case SHOW_PLAYLIST:
                        if(displayedElement instanceof Playlist) {
                            //setupAsArtist(data);   
                            setImage_and_backgroundColor(); 
                            setImageLink();
                        }
                        break;
                    case SHOW_SONG:
                        if(displayedElement instanceof Song) {
                            setupAsSong(data);  
                            setImage_and_backgroundColor();
                            setImageLink();
                        }
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
        img = ObjectsCache.getImage(imgURL);

        if(img == null) {
            Main.imageDownloader.addImageToDownload(imgURL, image);  
        }
        
        
        Color everegedColor = getAverageColor(img);

        everegedColor = brightenColor(everegedColor, 0.1);

        String color = ColorToHex(everegedColor);
        linearColorAnchorPane.setStyle("-fx-background-color: linear-gradient(to top, #030300, "+ color +");");
        image.setImage(img);
        
    }



    @Override
    public void init(Object... data) {
       
    }



    private void setupAsSong(Object... data) 
    { 
        ((SongDetails_controller)SceneManager.instance().injectScene("SongDetails.fxml", elementContainer)).init();

        final Song song = (Song) displayedElement;
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
                
                System.out.println("element: " + songs.size());
                for (Song song : songs) {

                    System.out.println("loading element: " + song.getTitle());
                    
                    Platform.runLater(() -> {
                        ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(FXML_elements.LIST_ELEMENT, elementContainer);
                        listCell.injectData(song);
                    });
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

    private void setupAsPlaylistShower(Object... data) 
    {
        labelName.setText("Le tue playlist");
        labelType.setText("");
        image.setImage(new Image(UtilityOS.formatPath(Main.ImageFolder + "\\icon\\playlistIcon.png")));
        linearColorAnchorPane.setStyle("-fx-background-color: linear-gradient(to top, #030300, "+ "#050500" +");");

        actionButton.setDisable(false);
        actionButton.setVisible(true);
        actionButton.setText("Crea una nuova playlist");

        actionButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sceneManager.startWindow(SceneManager.ApplicationWinodws.PLAYLIST_CREATION_WINDOW, null);
            }
        });

        new Thread(() -> {
            try {
                ArrayList<Playlist> playlist_list = connectionManager.getAccountPlaylists(Main.account.getNickname());

                //carico le playlist
                try {
                    for (Playlist p : playlist_list){

                        Platform.runLater(() -> {
                            ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(FXML_elements.LIST_ELEMENT, elementContainer);
                            listCell.injectData(p);
                        });
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


    public static Color getAverageColor(Image image) {
        // Create a PixelReader to read pixel data
        PixelReader pixelReader = image.getPixelReader();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Initialize variables for calculating average color components
        double totalRed = 0;
        double totalGreen = 0;
        double totalBlue = 0;
        //double pixelCount = 0;
        // Iterate through all pixels and accumulate color components
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                /*if(color.getRed() > 32 && color.getGreen() > 32 && color.getBlue() > 32 ) {
                    totalRed += color.getRed();
                    totalGreen += color.getGreen();
                    totalBlue += color.getBlue();
                    pixelCount++;
                }*/

                totalRed += color.getRed();
                totalGreen += color.getGreen();
                totalBlue += color.getBlue();
                    
            }
        }

        // Calculate average color components
        double pixelCount = width * height;
        double averageRed = totalRed / pixelCount;
        double averageGreen = totalGreen / pixelCount;
        double averageBlue = totalBlue / pixelCount;

        // Create a color from the average components
        return new Color(averageRed, averageGreen, averageBlue, 1);
    }


    public static Color brightenColor(Color originalColor, double factor) {
        double r = Math.min(originalColor.getRed() + factor, 1.0);
        double g = Math.min(originalColor.getGreen() + factor, 1.0);
        double b = Math.min(originalColor.getBlue() + factor, 1.0);

        return new Color(r, g, b, originalColor.getOpacity());
    }
    

    private String ColorToHex(Color color) {
        return String.format("#%02X%02X%02X", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255));
    }

    
    
}
