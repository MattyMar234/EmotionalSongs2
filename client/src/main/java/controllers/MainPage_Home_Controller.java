package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class MainPage_Home_Controller extends ControllerBase implements Initializable{


    public MainPage_Home_Controller() {
        super();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
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
