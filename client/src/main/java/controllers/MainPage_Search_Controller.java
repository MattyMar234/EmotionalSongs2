package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;

import application.Main;
import application.SceneManager;
import application.SceneManager.FXML_elements;
import application.SceneManager.SceneElemets;
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
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import objects.Emotion;
import objects.Song;
import utility.UtilityOS;

public class MainPage_Search_Controller extends ControllerBase implements Initializable, Injectable
{
    private static final int MAX_ELEMENT_FOR_PAGE = 50;
    private enum FilterType {SONG_NAME, SONG_DATE, ALBUM_NAME, ARTIST_NAME};

    private static FilterType filterType = FilterType.SONG_NAME;
    private MainPage_SideBar_Controller toolsController;
    private TextField searchBox = null;
    private String key = "";

    private long totalResult = 0;
    private long currentPage = 0;
    private long availablePage = 0;


    @FXML public Button songNameButtonFilter;
    @FXML public Button songDateButtonFilter;
    @FXML public Button albumNameButtonFilter;
    @FXML public Button artistNameButtonFilter;
    
    @FXML public FontIcon backPage_Button;
    @FXML public FontIcon nectPage_Button;

    @FXML public Label pageIndex;
    @FXML public Label resultLabel;
    @FXML public Label resultLabel1;
    
    @FXML public VBox elementContainer;

    


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setBackgroundLinearColor(ControllerBase.backgroundImageIndex);

        resultLabel1.setText((Main.applicationLanguage == 0 ? "Filtra per: " : "Filter by: "));

        
        
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

        // if(data.length >= 4) {
        //     filterType = (FilterType) data[3];
        // }
        // else {
        //     filterType = FilterType.SONG_NAME;
        // }

        
        pageIndex.setText((0) + (Main.applicationLanguage == 0 ? " di " : " of ") + (0));
        songNameButtonFilter.setText((Main.applicationLanguage == 0 ? "Titolo Canzone" : "Song Title"));
        songDateButtonFilter.setText((Main.applicationLanguage == 0 ? "Data Canzone" : "Song Date"));
        albumNameButtonFilter.setText((Main.applicationLanguage == 0 ? "Titolo Album" : "Album Title"));
        artistNameButtonFilter.setText((Main.applicationLanguage == 0 ? "Artista" : "Artist"));

        changeButtonColor();
        makeResearch(currentPage, true);

        System.out.println("");
    }

    private void makeResearch(long index, boolean caricaPrercedente_e_successivo) 
    {
        try {
            Object[] result = new Object[2];

            switch (filterType) {
                
                case SONG_NAME -> {
                    result = connectionManager.searchSongs(key, MAX_ELEMENT_FOR_PAGE, index*MAX_ELEMENT_FOR_PAGE, 0);
                }
                case SONG_DATE -> {
                    result = connectionManager.searchSongs(key, MAX_ELEMENT_FOR_PAGE, index*MAX_ELEMENT_FOR_PAGE, 1);
                }
                case ALBUM_NAME -> {
                    result = connectionManager.searchAlbums(key, MAX_ELEMENT_FOR_PAGE, index*MAX_ELEMENT_FOR_PAGE);
                }
                case ARTIST_NAME -> {
                    result = connectionManager.searchArtists(key, MAX_ELEMENT_FOR_PAGE, index*MAX_ELEMENT_FOR_PAGE);
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
            

            switch (MainPage_Search_Controller.filterType) {
                case ALBUM_NAME:
                    Platform.runLater(() -> {
                        ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_HEADER, elementContainer);
                        listCell.injectData(ListCell_DisplayMode.ALBUM_HEADER);
                    });
                    break;
                case ARTIST_NAME:
                    Platform.runLater(() -> {
                        ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_HEADER, elementContainer);
                        listCell.injectData(ListCell_DisplayMode.ARTIST_HEADER);
                    });
                    break;

                case SONG_DATE:
                case SONG_NAME:
                    Platform.runLater(() -> {
                        ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_HEADER, elementContainer);
                        listCell.injectData(ListCell_DisplayMode.SONG_HEADER);
                    });
                    break;
                default:
                    break;
                
            }

            new Thread(() -> {
                int rowIndex = 1;
                for (Object object : (ArrayList<Object>)list) 
                {
                    final int final_rowIndex = rowIndex++;
                    
                    //new Thread(() -> {
                        switch (MainPage_Search_Controller.filterType) {
                            case ALBUM_NAME:
                                Platform.runLater(() -> {
                                    ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_ELEMENT, elementContainer);
                                    listCell.injectData(ListCell_DisplayMode.DISPLAY_ALBUM, object, true, final_rowIndex + currentPage*MAX_ELEMENT_FOR_PAGE);
                                });
                                break;
                            case ARTIST_NAME:
                                Platform.runLater(() -> {
                                    ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_ELEMENT, elementContainer);
                                    listCell.injectData(ListCell_DisplayMode.DISPLAY_ARTIST, object, true, final_rowIndex + currentPage*MAX_ELEMENT_FOR_PAGE);
                                });
                                break;
                            case SONG_DATE:
                            case SONG_NAME:
                                Platform.runLater(() -> {
                                    ListCell_Controller listCell = (ListCell_Controller) SceneManager.instance().injectElement(SceneElemets.EDITABLE_LIST_CELL_ELEMENT, elementContainer);
                                    listCell.injectData(ListCell_DisplayMode.DISPLAY_SONG, object, true, final_rowIndex + currentPage*MAX_ELEMENT_FOR_PAGE);
                                });
                                break;
                            default:
                                break;
                    
                        }
                    //}).start(); 
                }
            }).start();
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
            new Thread(() -> {
                Platform.runLater(() -> {makeResearch(currentPage, true);});}
            ).start();
        }
    }
    
    @FXML
    public void next_page(ActionEvent event) {
        if(currentPage < availablePage - 1) {
            currentPage++;
            new Thread(() -> {
                Platform.runLater(() -> {makeResearch(currentPage, true);});}
            ).start();
        }
    }
    
    @FXML
    public void songNameButton_click(ActionEvent event) {
        currentPage = 0;
        MainPage_Search_Controller.filterType = FilterType.SONG_NAME;
        changeButtonColor();
        new Thread(() -> {
                Platform.runLater(() -> {makeResearch(currentPage, true);});}
            ).start();
    }

    @FXML
    public void songDateButton_click(ActionEvent event) {
        currentPage = 0;
        MainPage_Search_Controller.filterType = FilterType.SONG_DATE;
        String s = searchBox.getText();
        changeButtonColor();

        if(s.startsWith("-")) {
            elementContainer.getChildren().clear();
            pageIndex.setText((Main.applicationLanguage == 0 ? "nessun risultato" : "no result"));
            resultLabel.setText((Main.applicationLanguage == 0 ? "Risultati trovat: " : "Results found: ") + "0");
            return;
        }

        for (int i = 0; i < s.length(); i++) {
            if(!Character.isDigit(s.charAt(i))  && s.charAt(i) != '-') {
                elementContainer.getChildren().clear();
                resultLabel.setText((Main.applicationLanguage == 0 ? "Risultati trovat: " : "Results found: ") + "0");
                pageIndex.setText((Main.applicationLanguage == 0 ? "nessun risultato" : "no result"));
                return;
            }
        }

        new Thread(() -> {makeResearch(currentPage, true);}).start();
    }

    @FXML
    public void albumNameButton_click(ActionEvent event) {
        currentPage = 0;
        MainPage_Search_Controller.filterType = FilterType.ALBUM_NAME;
        changeButtonColor();
        new Thread(() -> {
                Platform.runLater(() -> {makeResearch(currentPage, true);});}
            ).start();
    }

    @FXML
    public void artistNameButton_click(ActionEvent event) {
        currentPage = 0;
        MainPage_Search_Controller.filterType = FilterType.ARTIST_NAME;
        changeButtonColor();
        new Thread(() -> {
                Platform.runLater(() -> {makeResearch(currentPage, true);});}
            ).start();
    }


    private void changeButtonColor() 
    {
        songNameButtonFilter.getStyleClass().clear();
        songDateButtonFilter.getStyleClass().clear();
        albumNameButtonFilter.getStyleClass().clear();
        artistNameButtonFilter.getStyleClass().clear();

        songNameButtonFilter.getStyleClass().add("button");
        songDateButtonFilter.getStyleClass().add("button");
        albumNameButtonFilter.getStyleClass().add("button");
        artistNameButtonFilter.getStyleClass().add("button");

        

        switch (MainPage_Search_Controller.filterType) {
            case ALBUM_NAME:
                songNameButtonFilter.getStyleClass().add("PrimaryButton");
                songDateButtonFilter.getStyleClass().add("PrimaryButton");
                albumNameButtonFilter.getStyleClass().add("PrimaryButtonSelected");
                artistNameButtonFilter.getStyleClass().add("PrimaryButton");
                break;
            case ARTIST_NAME:
                songNameButtonFilter.getStyleClass().add("PrimaryButton");
                songDateButtonFilter.getStyleClass().add("PrimaryButton");
                albumNameButtonFilter.getStyleClass().add("PrimaryButton");
                artistNameButtonFilter.getStyleClass().add("PrimaryButtonSelected");
                break;
            case SONG_DATE:
                songNameButtonFilter.getStyleClass().add("PrimaryButton");
                songDateButtonFilter.getStyleClass().add("PrimaryButtonSelected");
                albumNameButtonFilter.getStyleClass().add("PrimaryButton");
                artistNameButtonFilter.getStyleClass().add("PrimaryButton");
                break;
            case SONG_NAME:
                songNameButtonFilter.getStyleClass().add("PrimaryButtonSelected");
                songDateButtonFilter.getStyleClass().add("PrimaryButton");
                albumNameButtonFilter.getStyleClass().add("PrimaryButton");
                artistNameButtonFilter.getStyleClass().add("PrimaryButton");
                break;
            default:
                break;
        }
    }
}
