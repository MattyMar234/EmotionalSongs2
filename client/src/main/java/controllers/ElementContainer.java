package controllers;


import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import application.EmotionalSongs;
import application.ObjectsCache;
import application.SceneManager;
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


        if(displayedElement instanceof Song) 
        {
            Song song = (Song) displayedElement;
            Platform.runLater(() -> {title.setText(song.getTitle());});


            Image img = ObjectsCache.getImage(song.getImage(MyImage.ImageSize.S300x300).getUrl());

            if(img == null) {
                String imgURL = song.getImage(MyImage.ImageSize.S300x300).getUrl();
                EmotionalSongs.imageDownloader.addImageToDownload(imgURL, image);
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
            System.out.println(album);
            Platform.runLater(() -> {title.setText(album.getName());});

            Image img = ObjectsCache.getImage(album.getImage(MyImage.ImageSize.S300x300).getUrl());

            if(img == null) {
                String imgURL = album.getImage(MyImage.ImageSize.S300x300).getUrl();
                EmotionalSongs.imageDownloader.addImageToDownload(imgURL, image);
                //EmotionalSongs.imageDownloader.addImageToDownload(song.getImage(MyImage.ImageSize.S300x300).getUrl(), image);
            }
            else {
                Platform.runLater(() -> {image.setImage(img);});
                
            }


        }
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
        
        SceneManager.getInstance().showScene(SceneManager.SceneName.DISPLAY_ELEMENT_PAGE, displayedElement);
        if(displayedElement instanceof Song) { 
            /*Song s = (Song) displayedElement;openLink(s.getSpotifyUrl());*/
            SceneManager.getInstance().showScene(SceneManager.SceneName.DISPLAY_ELEMENT_PAGE, displayedElement);
            //MainPage_ElementDisplayer_Controller Displayer_Controller = (MainPage_ElementDisplayer_Controller) loadedControllers.get(1);

        /*else if(displayedElement instanceof Artist) {

        }
        else if(displayedElement instanceof Album) {

        }*/
        }
    }
    
}
