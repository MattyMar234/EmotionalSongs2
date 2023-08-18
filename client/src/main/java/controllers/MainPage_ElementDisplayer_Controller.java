package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import application.EmotionalSongs;
import application.ObjectsCache;
import application.SceneManager;
import interfaces.ControllerFunctions;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import objects.Album;
import objects.MyImage;
import objects.Song;


public class MainPage_ElementDisplayer_Controller extends ControllerBase implements Initializable, ControllerFunctions {


    @FXML public Label labelName;
    @FXML public Label labelType;
    @FXML public Label objectsLabel;
    @FXML public ImageView image;

    @FXML public VBox elementContainer;

    @FXML public AnchorPane linearColorAnchorPane;
    @FXML public AnchorPane blackColorAnchorPane;

    private Object displayedElement;

    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for(int i = 0; i < 10; i++)
            SceneManager.getInstance().injectScene("Comment.fxml", elementContainer);
        
    }



    @Override
    public void injectData(Object... data) {
        this.displayedElement = data[0];

        

        Platform.runLater(() -> {

            Image img;
            String imgURL = "";
            String spotifyUrl = "";

            if(displayedElement instanceof Song) {
                final Song song = (Song) displayedElement;
                labelName.setText(song.getTitle());
                labelType.setText("Song");

                imgURL = song.getImage(MyImage.ImageSize.S300x300).getUrl();
                spotifyUrl = song.getSpotifyUrl();
            }
                
            else if(displayedElement instanceof Album) {
                final Album album = (Album) displayedElement;

                labelName.setText(album.getName());
                labelType.setText("Album");

                imgURL = ((Album) displayedElement).getImage(MyImage.ImageSize.S300x300).getUrl();
                album.getSpotifyURL();
            }
        

            img = ObjectsCache.getImage(imgURL);

                
            if(img == null) {
                EmotionalSongs.imageDownloader.addImageToDownload(imgURL, image);  
            }
            else {
                String color = ColorToHex(getAverageColor(img));
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
    

    private String ColorToHex(Color color) {
        return String.format("#%02X%02X%02X", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255));
    }

    
    
}
