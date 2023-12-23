package application;

import java.util.Optional;

import application.SceneManager.ApplicationScene;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PlaylistCreationWindow extends Application
{
    private final SceneManager sceneManager = SceneManager.instance();
    private final ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
    private Stage root;
    private static Object[] args;


    @SuppressWarnings("unchecked")
    public static void startWindow(Object args_) 
    {
        Object[] args = (Object[])args_;
        PlaylistCreationWindow.args = (Object[])args_;
        //Class<Type> clazz = (Class<Type>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        System.out.print("PlaylistCreationWindow args:");
        
        if(args_ != null && args.length > 0) {
            System.out.println();
            for (Object string : args) 
                System.out.println("-" + string);
        }  
        else {
            System.out.println("null");
        }
        
        try {
            new PlaylistCreationWindow().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
            
    }
    
    @Override
    public void start(Stage root) throws Exception {
        this.root = root;

        root.setTitle("Playlist creator");
        root.setResizable(false);


        root.setOnCloseRequest(new EventHandler<WindowEvent>() {
            
            @Override
			public void handle(WindowEvent event) {
				closeWindow();
            } 
        });

        sceneManager.setStage(SceneManager.ApplicationWinodws.PLAYLIST_CREATION_WINDOW, root);
        sceneManager.setScene(SceneManager.ApplicationWinodws.PLAYLIST_CREATION_WINDOW, ApplicationScene.CREATE_PLAYLIST, PlaylistCreationWindow.args);
    }

    public void closeWindow() {
        sceneManager.removeStage(SceneManager.ApplicationWinodws.PLAYLIST_CREATION_WINDOW, getClass());
        root.close();
    }
    
}
