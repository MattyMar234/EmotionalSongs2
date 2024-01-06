package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import application.Main;
import application.SceneManager;
import enumClasses.EmotionType;
import interfaces.Injectable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import objects.Emotion;

public class EmotionsChart extends ControllerBase implements Initializable, Injectable 
{
    @FXML public PieChart chart;
    @FXML public Label labelTotUsers;
    @FXML public VBox Vbox_keys;

    @FXML public HBox chartContainer;
    @FXML public VBox labelContainer;

    @FXML public Label labelDataNotAvailable;
    @FXML public Label commentiLabel;
    @FXML public Label emozioneLabel;
    @FXML public Label imgLabel;
    @FXML public Label mediaLabel;

    private ArrayList<Emotion> listaEmozini;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commentiLabel.setText(Main.applicationLanguage == 0 ? "Commenti" : "Comments");
        emozioneLabel.setText(Main.applicationLanguage == 0 ? "Emozione" : "Emotion");
        imgLabel.setText(Main.applicationLanguage == 0 ? "Immagine" : "Image");
        mediaLabel.setText(Main.applicationLanguage == 0 ? "Media" : "Media");
    }

    @Override
    public void injectData(Object... data) {
        
        listaEmozini = (ArrayList<Emotion>) data[0];

        if(listaEmozini.size() == 0) {
            StackPane sp = (StackPane)chartContainer.getParent();
            sp.getChildren().remove(chartContainer);
            
            labelDataNotAvailable.setText(Main.applicationLanguage == 0 ? "Nessun dato disponibile\nper la visualizzazione" : "No data available for visualization");
        }
        else {
            StackPane sp = (StackPane)chartContainer.getParent();
            sp.getChildren().remove(labelContainer);

            labelTotUsers.setText(Main.applicationLanguage == 0 ? "Commenti Totali: " + listaEmozini.size() : "Total Comments: " + listaEmozini.size());
            InitializePieChart();
        }

        
    }

    @Override
    public void init(Object... data) {
       
    }

    private class Node {
        public long count = 0;
        public long summ = 0;
    }


    private void InitializePieChart() {
        
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
     

        HashMap<EmotionType, Node> conteggioEmozioni = new HashMap<>();
        final long total_emotion = listaEmozini.size();

        //inizilizzo tutto 0
        for (EmotionType e : enumClasses.EmotionType.values()) {
            conteggioEmozioni.put(e, new Node());
        }

        //conto i valori
        for (Emotion e : listaEmozini) {
            Node n = conteggioEmozioni.get(e.getEmotionType());
            n.count++;
            n.summ += e.getEmotionValue();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(chart.getStyle() + "\n");
        int index = 0;

        pieData.clear();
        
        for (EmotionType e : enumClasses.EmotionType.values()) 
        {
            if(conteggioEmozioni.get(e).count != 0) {
                PieChart.Data p = new PieChart.Data(e.getName(), conteggioEmozioni.get(e).count);
                pieData.add(p); 
            }
                       
            EmotionsChartKeys_Controller controller = (EmotionsChartKeys_Controller) sceneManager.injectScene(SceneManager.SceneElemets.CHART_KEYS.getElemetFilePath(), Vbox_keys);
            controller.injectData(e, conteggioEmozioni.get(e).count, conteggioEmozioni.get(e).summ);
        }
   

        chart.setData(pieData);

        chart.getData().forEach(data -> {
            String percentage  = data.getName() + ": " + String.format("%.2f%%", data.getPieValue() / total_emotion*100);
            Tooltip tooltip = new Tooltip(percentage);
            Tooltip.install(data.getNode(), tooltip);
        });

    }

    
}
