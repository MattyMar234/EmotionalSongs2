package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import application.EmotionalSongs;
import application.ObjectsCache;
import application.SceneManager;
import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import objects.MyImage;
import objects.Song;
import utility.PathFormatter;

public class ElementContainer extends ControllerBase implements Initializable{


    @FXML public ImageView image;
    @FXML public Label title;


    private SceneManager sceneManager;
    private Object displayedElement;
    
    public ElementContainer(Object displayedElement) {
        super();
        sceneManager = SceneManager.getInstance();
        this.displayedElement = displayedElement;
    }

    public ElementContainer() {
        super();
        sceneManager = SceneManager.getInstance();
    }

    public void InjectData(Object displayedElement) {
        this.displayedElement = displayedElement;


        Platform.runLater(() -> { // Lambda Expression
            if(displayedElement instanceof Song) 
            {
                Song song = (Song) displayedElement;
                title.setText(song.getTitle());

                Image img = ObjectsCache.getImage(song.getImage(MyImage.ImageSize.S300x300).getUrl());

                if(img == null) {
                    String imgURL = song.getImage(MyImage.ImageSize.S300x300).getUrl();
                    EmotionalSongs.imageDownloader.addImageToDownload(imgURL, image);
                    //EmotionalSongs.imageDownloader.addImageToDownload(song.getImage(MyImage.ImageSize.S300x300).getUrl(), image);
                    
                }
                else {
                   
                    image.setImage(img);
                }

                
                
                /*new Thread(() -> { // Lambda Expression
                    System.out.println(s.getImage("300x300").getUrl());
                    
                    try {
                        image.setImage(download_Image_From_Internet(s.getImage("300x300").getUrl()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();*/
        } 
        /*else if(displayedElement instanceof Artist) {

        }
        else if(displayedElement instanceof Album) {

        }*/
            
        });

        
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // genera numero casuale tra 0 e 3
        Random random = new Random();
        int number = random.nextInt(22) + 1;

        image.setImage(new Image(PathFormatter.formatPath(EmotionalSongs.ImageFolder + "\\colored_icon\\" + number + ".png")));
        
    }

    @FXML
    public void hovered(MouseEvent event) {
        
    }

    @FXML
    public void openLink(MouseEvent event) {
        if(displayedElement instanceof Song) {
            
            
            /*Song s = (Song) displayedElement;
            
            try {
                openLink(s.getSpotifyUrl());
            } 
            catch (IOException e) {
                e.printStackTrace();
            } 
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }*/ 

            ArrayList<ControllerBase> loadedControllers = SceneManager.getInstance().showScene(SceneManager.SceneName.DISPLAY_ELEMENT_PAGE, displayedElement);
            //MainPage_ElementDisplayer_Controller Displayer_Controller = (MainPage_ElementDisplayer_Controller) loadedControllers.get(1);

        /*else if(displayedElement instanceof Artist) {

        }
        else if(displayedElement instanceof Album) {

        }*/
        }
    }
    
}
