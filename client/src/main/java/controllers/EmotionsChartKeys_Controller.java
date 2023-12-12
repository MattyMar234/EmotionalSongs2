package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import enumClasses.EmotionType;
import interfaces.Injectable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import objects.Emotion;

public class EmotionsChartKeys_Controller extends ControllerBase implements Initializable, Injectable{

    @FXML public Circle colorCircle;
    @FXML public Label emotionTypeLabel;
    @FXML public Label everageLabel;
    @FXML public Label numerLabel;

    private EmotionType emotionType;
    private long numer;
    private double everage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       
    }

    @Override
    public void injectData(Object... data) {
        this.emotionType = (EmotionType) data[0];
        this.numer = (long) data[1];

        if(numer == 0) {
            everageLabel.setText("?");
        }
        else {
            this.everage = (double)(((long) data[2])/this.numer);
            everageLabel.setText(Double.toString(everage));
        }

        emotionTypeLabel.setText(emotionType.getName());
        numerLabel.setText(Long.toString(numer));
        colorCircle.setFill(Paint.valueOf(emotionType.getColorHexValue()));
        
    }

    @Override
    public void init(Object... data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    
}
