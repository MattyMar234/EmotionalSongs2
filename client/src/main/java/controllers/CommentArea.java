package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;

import application.Main;
import interfaces.Injectable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import objects.Emotion;
import objects.Song;

public class CommentArea extends ControllerBase implements Initializable, Injectable {

    @FXML public AnchorPane anchor;
    @FXML public ComboBox<?> emotionCombox;
    @FXML public FontIcon sendIcon;
    @FXML public Slider slider;
    @FXML public TextArea textArea;

    private Song song;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       
    }

    /**
     * Inizilizzatore della classe. 
     * data[0]: "song"
     */
    @Override
    public void injectData(Object... data) {
        this.song = (Song) data[0];
    }

    @Override
    public void init(Object... data) {
        
    }

    

    @FXML
    public void createEmotion(MouseEvent event) {
        if(Main.account == null) {
            System.out.println("L'Account è null");
            return;
        }

        try {
            connectionManager.addEmotion(Main.account.getNickname(), song.getId(), Emotion.EmotionType.AMAZEMENT.toString(), 2, "");
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    
}
