package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.EmotionalSongs;
import application.ObjectsCache;
import application.SceneManager;
import application.SceneManager.SceneElements;
import interfaces.ControllerFunctions;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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


public class MainPage_ElementDisplayer_Controller extends ControllerBase implements Initializable, ControllerFunctions {


    @FXML public Label labelName;
    @FXML public Label labelType;
    @FXML public Label objectsLabel;
    @FXML public ImageView image;

    @FXML public VBox elementContainer;

    @FXML public AnchorPane linearColorAnchorPane;
    @FXML public AnchorPane blackColorAnchorPane;

    @FXML public ListView<Object> listView;
    private Object displayedElement;
    private ObservableList<Object> ObservableList = FXCollections.observableArrayList();


    private Image img;
    private String imgURL = "";
    private String spotifyUrl = "";

    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        
        
    }



    @Override
    public void injectData(Object... data) {
        this.displayedElement = data[0];

        Platform.runLater(() -> {
            try {
                if(displayedElement instanceof Song) {
                    setupAsSong(data);  
                }
                else if(displayedElement instanceof Album) {
                    setupAsAlbum(data);    
                }
                else if(displayedElement instanceof Artist) {
                    setupAsArtist(data);    
                }
                else if(displayedElement instanceof Playlist) {
                    //setupAsArtist(data);    
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        

            img = ObjectsCache.getImage(imgURL);

                
            if(img == null) {
                EmotionalSongs.imageDownloader.addImageToDownload(imgURL, image);  
            }
            else {
                Color everegedColor = getAverageColor(img);

                everegedColor = brightenColor(everegedColor, 0.1);

                String color = ColorToHex(everegedColor);
                linearColorAnchorPane.setStyle("-fx-background-color: linear-gradient(to top, #030300, "+ color +");");
                image.setImage(img);
            }

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

        });
        
    }



    @Override
    public void init(Object... data) {
       
    }



    private void setupAsSong(Object... data) 
    { 
        for(int i = 0; i < 1; i++) {
            ((SongDetails_controller)SceneManager.getInstance().injectScene("SongDetails.fxml", elementContainer)).init();
        }
            

        final Song song = (Song) displayedElement;
        labelName.setText(song.getTitle());
        labelType.setText("Song");

        imgURL = song.getImage(MyImage.ImageSize.S300x300).getUrl();
        spotifyUrl = song.getSpotifyUrl();

        listView.setVisible(false);
    }   



    private void setupAsAlbum(Object... data) throws Exception 
    {
        final Album album = (Album) displayedElement;

        
        labelName.setText(album.getName());
        labelType.setText("Album");

        imgURL = ((Album) displayedElement).getImage(MyImage.ImageSize.S300x300).getUrl();
        album.getSpotifyURL();

        //connectionManager.getSongByIDs(album.getSongsID().toArray());
        
        

        new Thread(() -> {
            try {
                //ObservableList.addAll(songs);
                ArrayList<Song> songs = connectionManager.getAlbumSongs(album.getID());
                
                System.out.println("element: " + songs.size());
                for (Song song : songs) {

                    System.out.println("loading element: " + song.getTitle());
                    
                    Platform.runLater(() -> {
                        ListCell_Controller listCell = (ListCell_Controller) SceneManager.getInstance().injectElement(SceneElements.SONG_LIST_VIEW, elementContainer);
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
