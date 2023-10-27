package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import interfaces.Injectable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import objects.Emotion;

public class SongDetails_controller extends ControllerBase implements Initializable, Injectable
{

    @FXML public PieChart chart;
    @FXML public TextArea textArea;
    @FXML public Button sendButton;
    @FXML public ComboBox<String> emotionComboBox;

    

    @Override
    public void injectData(Object... data) {
    }

    @Override
    public void init(Object... data) {
        try {
            InitializePieChart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 256) {
                textArea.setText(oldValue); // Revert to the old value
            }
        });

        for(Emotion.EmotionType e : Emotion.EmotionType.values()) {
            emotionComboBox.getItems().add(e.getName());
        }

        //emotionComboBox.setValue(emotionComboBox.getItems().get(0));
    }


    private void InitializePieChart() throws IOException {
        HashMap<String, Integer> hashMapEmozioni = new HashMap<String, Integer>();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        pieData.add(new PieChart.Data("test1", 100));
        pieData.add(new PieChart.Data("test2", 600));
        pieData.add(new PieChart.Data("test3",200));

        chart.setData(pieData);
        final int  finalTotVoti = 900;

        chart.getData().forEach(data -> {
            String percentage  = String.format("%.2f%%",data.getPieValue()/ finalTotVoti*100);
            Tooltip tooltip= new Tooltip(percentage);
            Tooltip.install(data.getNode(), tooltip);
        });
    }


    private boolean regexCommento(final String input){

        final Pattern pattern = Pattern.compile(".{0,256}");
        final Matcher matcher = pattern.matcher(input);
        
        /*if(!matcher.matches()){
            AlertManager.showErrorAlert("ERROR! Emotional Songs - Commento", "Sono ammessi massimo 256 caratteri");
        }*/
        return matcher.matches();
    }



    @FXML
    public void keyTyped(KeyEvent event) {

        String text = textArea.getText();
        System.out.println(text.length());


        /*if(!regexCommento(text)) {
            System.out.println("heree");
            textArea.setText(text.substring(0, 256));
        }*/

        if(text.length() > 256) {
            //System.out.println("heree");
            //textArea.setText(text.substring(0, 257));
        }
    }

    @FXML
    public void sendComment(MouseEvent event) {

    }
    
}
