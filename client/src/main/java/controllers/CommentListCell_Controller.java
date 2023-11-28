package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import interfaces.Injectable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import objects.Comment;
import objects.Song;

public class CommentListCell_Controller extends ListCell<Song> implements Initializable, Injectable {

    @FXML public Label label1;
    @FXML public Label label2;
    @FXML public MenuButton playlistMenuBtn;


    private Comment comment;



    public CommentListCell_Controller() {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    
    @Override
    public void injectData(Object... data) {
       this.comment = (Comment) data[0];
    }

    @Override
    public void init(Object... data) {
       
    }


    @Override
    protected void updateItem(Song song, boolean empty) {
        super.updateItem(song, empty);

        if(empty || song == null) {

            setText(null);
            setGraphic(null);

        } else {
            /*if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("ListCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            //set fxml tags
            label1.setText(String.valueOf(student.getStudentId()));
            label2.setText(student.getName());

            setText(null);
            setGraphic(anchor);
        */
        }

    }

    
}
