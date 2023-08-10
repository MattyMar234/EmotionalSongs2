package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import application.ConnectionManager;
import application.SceneManager;
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

public class MainPage_Home_Controller extends ControllerBase implements Initializable
{
    @FXML public Button buttonBackward;
    @FXML public Button buttonForward;
    @FXML public TextField searchField;

    @FXML public ScrollPane scrollPane;
    @FXML public VBox scrollPaneVBox;

    private SceneManager sceneManager;
    private ArrayList<RowContainerController> rowControllers = new ArrayList<RowContainerController>();

    public MainPage_Home_Controller() {
        super();
        sceneManager = SceneManager.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        
        try {
            for(int i = 0; i < 7; i++) {
                ArrayList<Song> songs = ConnectionManager.getConnectionManager().getService().getMostPopularSongs(10, 10*i);
                RowContainerController controller = (RowContainerController) sceneManager.injectScene("RowContainer.fxml", scrollPaneVBox, new RowContainerController()/*new RowContainerController(songs)*/);
                controller.InjectData(songs, "Top 10 song");

                rowControllers.add(controller);
            }
            
        
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
    


    @FXML
    public void BackwardAction(MouseEvent event) {

    }

    @FXML
    public void ForwardAction(MouseEvent event) {

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
