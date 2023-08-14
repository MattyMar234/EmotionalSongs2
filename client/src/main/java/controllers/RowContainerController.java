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
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import objects.Song;

public class RowContainerController extends ControllerBase implements Initializable{

    @FXML public HBox Hbox;
    @FXML public Label title;

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

    @SuppressWarnings("unchecked")
    public void InjectData(Object list, String str) 
    {
        System.out.println("heree");
        ArrayList<Object> castedList = (ArrayList<Object>) list;
        this.elementsTitle = str;
        this.list = list;

        title.setText(this.elementsTitle);

        System.out.println(castedList.size());
        for(final Object o : castedList)
        {
            
            try {
                System.out.println("heree");
                ElementContainer controller = (ElementContainer) sceneManager.injectScene("ElementContainer.fxml", Hbox, new ElementContainer(o));
                controller.InjectData(o);
            } 
            catch (RemoteException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
            
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
