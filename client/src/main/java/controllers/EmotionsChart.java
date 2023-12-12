package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

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
import javafx.scene.layout.VBox;
import objects.Emotion;

public class EmotionsChart extends ControllerBase implements Initializable, Injectable 
{
    @FXML public PieChart chart;
    @FXML public Label labelTotUsers;
    @FXML public VBox Vbox_keys;

    private ArrayList<Emotion> listaEmozini;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
    }

    @Override
    public void injectData(Object... data) {
        
        listaEmozini = (ArrayList<Emotion>) data[0];
        InitializePieChart();
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
            pieData.add(new PieChart.Data(e.getName(), conteggioEmozioni.get(e).count));            

            EmotionsChartKeys_Controller controller = (EmotionsChartKeys_Controller) sceneManager.injectScene(SceneManager.SceneElemets.CHART_KEYS.getElemetFilePath(), Vbox_keys);
            controller.injectData(e, conteggioEmozioni.get(e).count, conteggioEmozioni.get(e).summ);
            
            // System.out.println("index: " + index + " => " + e.getName() + " e " + e.getPieColor(true));
            // sb.append(".default-color" + (index++) + ".chart-pie{\n");
            // sb.append("\t" + e.getPieColor(true) + "\n");
            // sb.append("}\n");
        }
        
        //System.out.println("style: " + sb.toString());
        //chart.setStyle(sb.toString());
        chart.setData(pieData);

        chart.getData().forEach(data -> {
            String percentage  = String.format("%.2f%%", data.getPieValue() / total_emotion*100);
            Tooltip tooltip = new Tooltip(percentage);
            Tooltip.install(data.getNode(), tooltip);
        });

    }

    
}
