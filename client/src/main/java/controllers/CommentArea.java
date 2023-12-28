package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;

import application.Main;
import application.SceneManager;
import enumClasses.ElementDisplayerMode;
import interfaces.Injectable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import objects.Emotion;
import objects.Song;

public class CommentArea extends ControllerBase implements Initializable, Injectable 
{
    private static final int COMMENT_LENGHT = 256;

    @FXML public AnchorPane anchor;
    @FXML public ComboBox<String> emotionCombox;
    @FXML public Slider slider;
    @FXML public TextArea textArea;
    @FXML public Button sendButton;
    @FXML public Label labelCommenti;
    @FXML public Label charsCounterLabel;
    @FXML public Label labelDistribuzione;

    

    private Song song;
    private MainPage_ElementDisplayer_Controller controller;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (enumClasses.EmotionType emotion : enumClasses.EmotionType.values()) {
            emotionCombox.getItems().add(emotion.getName());
        }

       
       emotionCombox.getSelectionModel().select(enumClasses.EmotionType.AMAZEMENT.getName());
       sendButton.setText(Main.applicationLanguage == 0 ? "Conferma" : "Confirm");

        if(labelDistribuzione != null)
            labelDistribuzione.setText(Main.applicationLanguage == 0 ? "Distribuzione Commenti" : "Comment Distribution");
       
       slider.setValue(0);

        if(Main.account == null) {
            sendButton.setDisable(true);
            textArea.setDisable(true);
            // textArea.cente
            textArea.setText(Main.applicationLanguage == 0 ? "Per poter commentare devi effettuare il login" : "To comment you must login");
        }

        labelCommenti.setText(Main.applicationLanguage == 0 ? "Commenti" : "Comments");

        new Thread(() -> {
            try {
                ArrayList<Emotion> list = connectionManager.getEmotions(song.getId());
            
                if(list.size() == 0) {
                     Platform.runLater(() -> {
                        labelCommenti.setText(Main.applicationLanguage == 0 ? "Nessun Commento" : "No Comments");
                    });
                }
            } 
            catch (Exception e) {
                
               
            }
        });


        textArea.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= getCharsCounter(change.getControlNewText().length()) ? change : null
        ));

        charsCounterLabel.setText("0/" + COMMENT_LENGHT);
    }

    private int getCharsCounter(int lenght) {

        charsCounterLabel.setText(Math.min(lenght, 256) + "/" + COMMENT_LENGHT);

        if(Math.min(lenght, 256) == 256) {
            charsCounterLabel.setTextFill(Color.RED);
        }
        else {
            charsCounterLabel.setTextFill(Color.WHITE);
        }

        return COMMENT_LENGHT;
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
    public void createEmotion(ActionEvent event) {
        if(Main.account == null) {
            System.out.println("L'Account è null");
            return;
        }

        try {

            String name = emotionCombox.getValue();

            for (enumClasses.EmotionType emotionType : enumClasses.EmotionType.values()) {
                if(emotionType.getName().equals(name)) 
                {
                    int value = (int) slider.getValue();
                    String comment = textArea.getText();

                    if(comment.length() > COMMENT_LENGHT) {
                        comment = comment.substring(0, COMMENT_LENGHT);
                    }
                    
                    if(connectionManager.addEmotion(Main.account.getNickname(), song.getId(), emotionType.toString(), value, comment)) {
                        //controller.refreshData();
                        sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, ElementDisplayerMode.SHOW_SONG, this.song);
                        
                    }
                    break;
                } 
            }  
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        textArea.clear();
    }

    
}
