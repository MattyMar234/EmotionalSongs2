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

            if(displayedElement instanceof Song) {
                final Song song = (Song) displayedElement;

                labelName.setText(song.getTitle());
                labelType.setText("Song");

                Image img = ObjectsCache.getImage(song.getImage(MyImage.ImageSize.S300x300).getUrl());
                
                if(img == null) {
                    String imgURL = song.getImage(MyImage.ImageSize.S300x300).getUrl();
                    EmotionalSongs.imageDownloader.addImageToDownload(imgURL, image);
                    
                }
                else {
                    String color = ColorToHex(getAverageColor(img));
                    linearColorAnchorPane.setStyle("-fx-background-color: linear-gradient(to top, #030300, "+ color +");");
                    image.setImage(img);
                }

                image.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        try {
                            openLink(song.getSpotifyUrl());
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

        });
        
    }



    @Override
    public void init(Object... data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
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

        // Iterate through all pixels and accumulate color components
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
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
