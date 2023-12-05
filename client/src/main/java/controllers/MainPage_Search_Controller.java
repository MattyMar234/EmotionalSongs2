package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;

import application.Main;
import application.SceneManager;
import application.SceneManager.FXML_elements;
import enumClasses.ListCell_DisplayMode;
import interfaces.Injectable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import objects.Emotion;
import objects.Song;

public class MainPage_Search_Controller extends ControllerBase implements Initializable, Injectable
{
    private static final int MAX_ELEMENT_FOR_PAGE = 50;
    private enum FilterType {ALL, ARTIST, ALBUM, SONG};

    private FilterType filterType = FilterType.SONG;
    private MainPage_SideBar_Controller toolsController;
    private TextField searchBox = null;
    private String key = "";

    private long totalResult = 0;
    private long currentPage = 0;
    private long availablePage = 0;


    @FXML public Button allButtonFilter;
    @FXML public Button artistButtonFilter;
    @FXML public Button albumButtonFilter;
    @FXML public Button songButtonFilter;
    
    @FXML public FontIcon backPage_Button;
    @FXML public FontIcon nectPage_Button;

    @FXML public Label pageIndex;
    @FXML public Label resultLabel;
    
    @FXML public VBox elementContainer;





    

    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        
    }
    
    @Override
    public void injectData(Object... data) {
        if(data.length < 2) {
            throw new IllegalArgumentException("Wrong number of arguments for class \"MainPage_Search_Controller\"");
        }

        if(data[0] instanceof MainPage_SideBar_Controller == false) {
            throw new IllegalArgumentException("Wrong type of arguments. \"data[0]\" must be of type \"MainPage_Search_Controller\"");
        }

        toolsController = (MainPage_SideBar_Controller)data[0];
        searchBox = toolsController.searchField;
        key = (String) data[1];

        if(data.length >= 3) {
            currentPage = (long) data[2];
        }
        else {
            currentPage = 0;
        }

        if(data.length >= 4) {
            filterType = (FilterType) data[3];
        }
        else {
            filterType = FilterType.SONG;
        }

        
        
        pageIndex.setText((0) + (Main.applicationLanguage == 0 ? " di " : " of ") + (0));
        makeResearch(currentPage, true);
    }

    private void makeResearch(long index, boolean caricaPrercedente_e_successivo) 
    {
        try {
            Object[] result = new Object[2];

            switch (filterType) {
                
                case ALL -> {
                    result = connectionManager.searchSongs(key, MAX_ELEMENT_FOR_PAGE, index*MAX_ELEMENT_FOR_PAGE);
                }
                case ARTIST -> {
                    result = connectionManager.searchSongs(key, MAX_ELEMENT_FOR_PAGE, index*MAX_ELEMENT_FOR_PAGE);
                }
                case ALBUM -> {
                    result = connectionManager.searchSongs(key, MAX_ELEMENT_FOR_PAGE, index*MAX_ELEMENT_FOR_PAGE);
                }
                case SONG -> {
                    result = connectionManager.searchSongs(key, MAX_ELEMENT_FOR_PAGE, index*MAX_ELEMENT_FOR_PAGE);
                }
            }

            if(caricaPrercedente_e_successivo == false) {
                return;
            }
           

            final Object list =  result[1];
            totalResult = (long) result[0];
            availablePage = totalResult / MAX_ELEMENT_FOR_PAGE;

            if(totalResult % MAX_ELEMENT_FOR_PAGE != 0) {
                availablePage++;
            }

            //eseguo la query per il precedente
            new Thread(() -> {
                if(currentPage > 0) {
                    makeResearch(currentPage - 1, false);
                }
            }).start();

            //eseguo la query per il successivo
            new Thread(() -> {
                if(currentPage < availablePage) {
                    makeResearch(currentPage + 1, false);
                }
            }).start();


            if(totalResult == 0) {
                pageIndex.setText((Main.applicationLanguage == 0 ? "nessun risultato" : "no result"));
                return;
            }

            pageIndex.setText((index+1) + (Main.applicationLanguage == 0 ? " di " : " of ") + (availablePage));
            resultLabel.setText((Main.applicationLanguage == 0 ? "Risultati trovat: " : "Results found: ") + totalResult);
            
            elementContainer.getChildren().clear();
            
            for (Object object : (ArrayList<Object>)list) 
            {
                new Thread(() -> {
                    Platform.runLater(() -> {
                        ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(FXML_elements.LIST_ELEMENT, elementContainer);
                        listCell.injectData(ListCell_DisplayMode.DISPLAY_SONG, object, true);
                    });
                }).start();
            }

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(Object... data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @FXML
    public void back_page(ActionEvent event) {
        if(currentPage > 0) {
            currentPage--;
            makeResearch(currentPage, true);
        }
    }

    @FXML
    public void next_page(ActionEvent event) {
        if(currentPage < availablePage) {
            currentPage++;
            makeResearch(currentPage, true);
        }
    }


    @FXML
    public void allButton_click(ActionEvent event) {
        currentPage = 0;
        filterType = FilterType.ALL;
    }

    @FXML
    public void artistButton_click(ActionEvent event) {
        currentPage = 0;
        filterType = FilterType.ARTIST;
    }

    @FXML
    public void playlistButton_click(ActionEvent event) {
        currentPage = 0;
        filterType = FilterType.ALBUM;
    }

    @FXML
    public void songButton_click(ActionEvent event) {
        currentPage = 0;
        filterType = FilterType.SONG;
    }
}
