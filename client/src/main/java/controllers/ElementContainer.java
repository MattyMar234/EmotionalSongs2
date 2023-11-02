package controllers;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import application.Main;
import application.ObjectsCache;
import application.SceneManager;
import interfaces.Injectable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import objects.Album;
import objects.Artist;
import objects.MyImage;
import objects.Song;
import utility.UtilityOS;

public class ElementContainer extends ControllerBase implements Initializable, Injectable 
{
    @FXML public ImageView image;
    @FXML public Label title;
    
    private Object displayedElement;
    
    
    public ElementContainer() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        Random random = new Random();
        int number = random.nextInt(22) + 1;

        this.title.setText("?");
        image.setImage(UtilityOS.getImage(Main.ImageFolder + "\\colored_icon\\" + number + ".png"));    
    }

    @Override
    public void init(Object... data) {
        
    }

    
    @Override
    public void injectData(Object...data) {
        this.displayedElement = data[0];

        if(displayedElement instanceof Song) 
        {
            Song song = (Song) displayedElement;
            Platform.runLater(() -> {title.setText(song.getTitle());});

            Image img = ObjectsCache.getImage(song.getImage(MyImage.ImageSize.S300x300).getUrl());

            if(img == null) {
                String imgURL = song.getImage(MyImage.ImageSize.S300x300).getUrl();
                Main.imageDownloader.addImageToDownload(imgURL, image);
                //EmotionalSongs.imageDownloader.addImageToDownload(song.getImage(MyImage.ImageSize.S300x300).getUrl(), image);
            }
            else {
                Platform.runLater(() -> {image.setImage(img);});
                
            }
    
        } 
        else if(displayedElement instanceof Artist) {

        }
        else if(displayedElement instanceof Album) 
        {
            Album album = (Album) displayedElement;
            //System.out.println(album);
            Platform.runLater(() -> {title.setText(album.getName());});

            Image img = ObjectsCache.getImage(album.getImage(MyImage.ImageSize.S300x300).getUrl());

            if(img == null) {
                String imgURL = album.getImage(MyImage.ImageSize.S300x300).getUrl();
                Main.imageDownloader.addImageToDownload(imgURL, image);
                //EmotionalSongs.imageDownloader.addImageToDownload(song.getImage(MyImage.ImageSize.S300x300).getUrl(), image);
            }
            else {
                Platform.runLater(() -> {image.setImage(img);});
                
            }
        }
    }
    
    

    @FXML
    public void hovered(MouseEvent event) {
        
    }

    @FXML
    public void openLink(MouseEvent event) 
    {
        if(displayedElement instanceof Song) { 
            sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, displayedElement);
        }
        else if(displayedElement instanceof Artist) {
        }
        else if(displayedElement instanceof Album) {
            sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, displayedElement);
        }
    }
}
