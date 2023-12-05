package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;

import application.Main;
import application.SceneManager;
import enumClasses.ElementDisplayerMode;
import interfaces.Injectable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import objects.Emotion;
import objects.Song;

public class CommentArea extends ControllerBase implements Initializable, Injectable 
{
    private static final int COMMENT_LENGHT = 256;

    @FXML public AnchorPane anchor;
    @FXML public ComboBox<enumClasses.EmotionType> emotionCombox;
    @FXML public FontIcon sendIcon;
    @FXML public Slider slider;
    @FXML public TextArea textArea;

    private Song song;
    private MainPage_ElementDisplayer_Controller controller;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       emotionCombox.getItems().addAll(enumClasses.EmotionType.values());
       emotionCombox.getSelectionModel().select(enumClasses.EmotionType.AMAZEMENT);
       slider.setValue(0);
    }

    /**
     * Inizilizzatore della classe. 
     * data[0]: "song"
     */
    @Override
    public void injectData(Object... data) {
        this.song = (Song) data[0];
        //this.controller = (MainPage_ElementDisplayer_Controller) data[1];
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

            enumClasses.EmotionType emotionType = emotionCombox.getValue();
            int value = (int) slider.getValue();
            String comment = textArea.getText();

            if(comment.length() > COMMENT_LENGHT) {
                comment = comment.substring(0, COMMENT_LENGHT);
            }
            
            if(connectionManager.addEmotion(Main.account.getNickname(), song.getId(), emotionType.toString(), value, comment)) {
                //controller.refreshData();
                sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, ElementDisplayerMode.SHOW_SONG, this.song);
                
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    
}
