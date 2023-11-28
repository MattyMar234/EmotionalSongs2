package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import interfaces.Injectable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class EmotionsChart extends ControllerBase implements Initializable, Injectable 
{
    @FXML public PieChart chart;
    @FXML public Label labelTotUsers;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InitializePieChart();
    }

    @Override
    public void injectData(Object... data) {
        
    }

    @Override
    public void init(Object... data) {
       
    }


    private void InitializePieChart() {
        
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

    
}
