package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.EmotionalSongs;
import interfaces.ControllerFunctions;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import objects.MyImage;
import objects.Song;

public class MainPage_ElementDisplayer_Controller extends ControllerBase implements Initializable, ControllerFunctions {


    @FXML public Label labelName;
    @FXML public Label labelType;
    @FXML public Label objectsLabel;
    @FXML public ImageView image;

    private Object displayedElement;

    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }



    @Override
    public void injectData(Object... data) {
        this.displayedElement = data[0];

        

        Platform.runLater(() -> {
            if(displayedElement instanceof Song) {
                Song song = (Song) displayedElement;

                labelName.setText(song.getTitle());
                labelType.setText("Song");
                

                String imgURL = song.getImage(MyImage.ImageSize.S300x300).getUrl();
                EmotionalSongs.imageDownloader.addImageToDownload(imgURL, image);
   
            }

        });
        
    }



    @Override
    public void init(Object... data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }
    
}
