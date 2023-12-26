package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import application.ConnectionManager;
import application.Main;
import application.SceneManager;
import interfaces.Injectable;
import interfaces.ServerServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import objects.Song;
import java.util.ArrayList;
import java.util.HashMap;

public class MainPage_Home_Controller extends ControllerBase implements Initializable, Injectable
{
    @FXML public Button buttonBackward;
    @FXML public Button buttonForward;
    @FXML public TextField searchField;

    @FXML public ScrollPane scrollPane;
    @FXML public VBox scrollPaneVBox;

    private SceneManager sceneManager;
    private ArrayList<RowContainerController> rowControllers = new ArrayList<RowContainerController>();
    private HashMap<String, RowContainerController> ControllersFunction = new HashMap<>();

    public MainPage_Home_Controller() {
        super();
        sceneManager = SceneManager.instance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        setBackgroundLinearColor(ControllerBase.backgroundImageIndex);
        
        ConnectionManager services = ConnectionManager.getConnectionManager();

        try {
            
            RowContainerController controller1 = (RowContainerController) sceneManager.injectScene("RowContainer.fxml", scrollPaneVBox, new RowContainerController());
            RowContainerController controller2 = (RowContainerController) sceneManager.injectScene("RowContainer.fxml", scrollPaneVBox, new RowContainerController());
            RowContainerController controller3 = (RowContainerController) sceneManager.injectScene("RowContainer.fxml", scrollPaneVBox, new RowContainerController());
            RowContainerController controller4 = (RowContainerController) sceneManager.injectScene("RowContainer.fxml", scrollPaneVBox, new RowContainerController());
            RowContainerController controller5 = (RowContainerController) sceneManager.injectScene("RowContainer.fxml", scrollPaneVBox, new RowContainerController());
            
            String str1 = "Top 10 " + (Main.applicationLanguage == 0 ? "Canzoni" : "Song");
            String str2 = (Main.applicationLanguage == 0 ? "Canzoni popolari" : "Most popular songs");
            String str3 = (Main.applicationLanguage == 0 ? "Canzoni consigliati per te" : "Songs recommended for you");
            String str4 = "Top 10 Album";
            String str5 = (Main.applicationLanguage == 0 ? "Album recenti" : "Recent Albums");
        

            controller1.init(str1, 10, scrollPane.getPrefWidth());
            controller2.init(str2, 10, scrollPane.getPrefWidth());
            controller3.init(str3, 10, scrollPane.getPrefWidth());
            controller4.init(str4, 10, scrollPane.getPrefWidth());
            controller5.init(str5, 10, scrollPane.getPrefWidth());
            
            new Thread(() -> {try{controller1.InjectData(services.getMostPopularSongs(10, 0), str1);}catch (Exception e) {}}).start();
            new Thread(() -> {try{controller2.InjectData(services.getMostPopularSongs(10, 10), str2);}catch (Exception e) {}}).start();
            new Thread(() -> {try{controller3.InjectData(services.getMostPopularSongs(10, 30), str3);}catch (Exception e) {}}).start();
            new Thread(() -> {try{controller4.InjectData(services.getRecentPublischedAlbum(10,0,30), str4);}catch (Exception e) {}}).start();
            new Thread(() -> {try{controller5.InjectData(services.getRecentPublischedAlbum(10,10,30), str5);}catch (Exception e) {}}).start();
            
            //new Thread(() -> {try{controller4.InjectData(services.searchSongs("Master of Puppets",10,0), "test ricerca canzoni");}catch (Exception e) {}}).start();
            //new Thread(() -> {try{controller5.InjectData(services.searchAlbums("Metallica", 10, 0), "test ricerca album");}catch (Exception e) {}}).start();
        
            
        } 
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    

        

        

        
        
        

        
        
        

        //???
        scrollPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double ds = newValue.doubleValue() - oldValue.doubleValue();
            
            System.out.println(ds);
            
            for (RowContainerController controller : rowControllers) {
                controller.Hbox.prefWidthProperty().bind(scrollPane.prefWidthProperty());
            }
        });

        double currentVal = scrollPane.widthProperty().get();

        for (RowContainerController controller : rowControllers) {
            double ds = currentVal - controller.Hbox.widthProperty().get();
            controller.Hbox.widthProperty().add(ds);
        }
    }

    @Override
    public void injectData(Object... data) {
        
    }

    @Override
    public void init(Object... data) {
        
    }
    


    



   
    
}

/*
 * searchSong.setText(MainPageController_reposity.matrice[0][EmotionalSongs.language]);
        sortItems.setText(MainPageController_reposity.matrice[1][EmotionalSongs.language]);


        for(Song song : application.songManager.getList()) {
            list.add(new Container(song, this));
        }


        

        filter.getItems().addAll(FilterState.values());
        filter.setValue(filter.getItems().get(0));

        //ScrollBar verticalBar = (ScrollBar) SongsTable.lookup(".scroll-bar:vertical");
        //verticalBar.setValue(0.49);

        //disabilito lo scroll con la rotella
        

        
    

     


        Callback<TableColumn<Container, Container>, TableCell<Container, Container>> cellFoctory = (TableColumn<Container, Container> param) -> {
            final TableCell<Container, Container> cell = new TableCell<Container, Container>() {
                
                @Override
                public void updateItem(Container item, boolean empty) {
                    super.updateItem(item, empty);
                    //that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } 
                    else 
                    {
                        try 
                        {
                            //carico la pagina
                            FXMLLoader XMLloader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/test.fxml"));
                            
                            
                            
                            AnchorPane view = XMLloader.load();

                            //carico i parametri
                            RepositorySongElementController controller = XMLloader.getController();
                            controller.injectData(item.mainController, item.song);
                            //System.out.println(item);

                            setGraphic(view);
                        
                        } catch (IOException e) {
                            System.out.println(e);
                        }  
                        setText(null);
                    }
                }
            };
            return cell;
        };

        
        
        
        
        SortedList<Container> sortedData = new SortedList<Container>(filteredData);
        sortedData.comparatorProperty().bind(SongsTable.comparatorProperty());
        SongsTable.setItems(sortedData);
 */

 /*XMLloader.setControllerFactory(c -> {    
                                return new RepositorySongElementController(item); // <-- parametri costruttore classe
                            });*/
