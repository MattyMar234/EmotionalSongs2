package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import enumClasses.EmotionType;
import interfaces.Injectable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import objects.Comment;
import objects.Emotion;
import objects.Song;

public class CommentListCell_Controller implements Initializable, Injectable 
{

    @FXML public AnchorPane anchor;
    @FXML public Circle circle;
    @FXML public ImageView emojiIcon;
    @FXML public Label labelUserName;
    @FXML public Label labelValue;
    @FXML public Text textContainer;


    public Emotion emotion;



    public CommentListCell_Controller() {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    
    @Override
    public void injectData(Object... data) {
       this.emotion = (Emotion) data[0];
       EmotionType emotionType = emotion.getEmotionType();

        circle.setStyle(emotionType.getStyleColor(true));
        emojiIcon.setImage(emotionType.getEmotionImage());
        labelUserName.setText(emotion.getID_Account());
        labelValue.setText(emotion.getEmotionValue() + "/5");
        textContainer.setText(emotion.getComment());

    }

    @Override
    public void init(Object... data) {
       
    }

}
