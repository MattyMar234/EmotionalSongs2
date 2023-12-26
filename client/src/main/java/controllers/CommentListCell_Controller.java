package controllers;

import java.awt.Paint;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;

import application.Main;
import application.SceneManager.ApplicationWinodws;
import enumClasses.EmotionType;
import interfaces.Injectable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import objects.Comment;
import objects.Emotion;
import objects.Song;

public class CommentListCell_Controller extends ControllerBase implements Initializable, Injectable 
{

    @FXML public AnchorPane anchor;
    @FXML public Circle circle;
    @FXML public ImageView emojiIcon;
    @FXML public Label labelUserName;
    @FXML public Label labelValue;
    @FXML public Text textContainer;
    @FXML public Label labelEmotionType;
    @FXML public FontIcon commentUserIcon;
    @FXML public Button deleteButton;

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
        labelEmotionType.setText(emotionType.getName());

        String style = commentUserIcon.getStyle();
        commentUserIcon.setStyle(style + "-fx-fill: " + emotionType.getColorHexValue() +";");

        if(Main.account == null || !emotion.getID_Account().equals(Main.account.getNickname())) {
           HBox hb = (HBox) deleteButton.getParent();
           hb.getChildren().remove(deleteButton);
        }

        if(emotion.getComment().length() == 0) {
            AnchorPane n = (AnchorPane)textContainer.getParent();
            n.getChildren().remove(textContainer);
        }

    }

    @Override
    public void init(Object... data) {
        
    }


    @FXML
    public void removeComment(ActionEvent event) {
        try {
            connectionManager.removeEmotion(emotion.getID());
            sceneManager.refreshScene(ApplicationWinodws.EMOTIONALSONGS_WINDOW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
