package controllers;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import objects.Song;

public class RowContainerController extends ControllerBase implements Initializable{

    @FXML public HBox Hbox;
    @FXML public Label title;
    @FXML public AnchorPane anchor;

    private ArrayList<ElementContainer> displayElementContainerList = new ArrayList<ElementContainer>();
    private SceneManager sceneManager;
    private Object list;
    private String elementsTitle;

    //controllers.RowContainerController<

    public RowContainerController(Object list) {
        super();
        this.list = list;
        sceneManager = SceneManager.getInstance();
    }

    public RowContainerController() {
        super();
        sceneManager = SceneManager.getInstance();
    }


    public void init(String str, int elementNumber, double size) {

        anchor.setPrefWidth(size);
        anchor.setMinWidth(size);
        
        for(int i = 0; i < elementNumber; i++) {
           try {
                ElementContainer controller = (ElementContainer) sceneManager.injectScene("ElementContainer.fxml", Hbox, new ElementContainer());
                displayElementContainerList.add(controller);
            } 
            catch (RemoteException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }    
        }
        
    }

    @SuppressWarnings("unchecked")
    public void InjectData(Object list, String str) 
    {
        ArrayList<Object> castedList = (ArrayList<Object>) list;
        this.elementsTitle = str;
        this.list = list;

        int i = 0;
        Platform.runLater(() -> {title.setText(this.elementsTitle);});

        for(final Object o : castedList) {
            displayElementContainerList.get(i++).InjectData(o);
        }  
    }

    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        
    }

    public void setList(Object list) {
        this.list = list;
    }

    public Object getList() {
        return list;
    }

    
    
}
