package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import application.SceneManager;
import javafx.animation.FillTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import objects.Song;

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
            Song s = (Song) displayedElement;
            title.setText(s.getTitle());
            
            new Thread(() -> { // Lambda Expression
                System.out.println(s.getImage("300x300").getUrl());
                
            try {
                image.setImage(download_Image_From_Internet(s.getImage("300x300").getUrl()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            }).start();

            
        } 
        /*else if(displayedElement instanceof Artist) {

        }
        else if(displayedElement instanceof Album) {

        }*/
    }
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    @FXML
    public void hovered(MouseEvent event) {
        
    }

    @FXML
    public void openLink(MouseEvent event) {
        if(displayedElement instanceof Song) {
            Song s = (Song) displayedElement;
            
            try {
                openLink(s.getSpotifyUrl());
            } 
            catch (IOException e) {
                e.printStackTrace();
            } 
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
   
        } 
        /*else if(displayedElement instanceof Artist) {

        }
        else if(displayedElement instanceof Album) {

        }*/
    }
    
}
