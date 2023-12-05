package application;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import application.SceneManager.ApplicationScene;
import applicationEvents.ConnectionEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EmotionalSongsWindow extends Application
{
    private static EmotionalSongsWindow classReference = null;
    private final SceneManager sceneManager = SceneManager.instance();
    private final ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
    private Stage root;


    @SuppressWarnings("unchecked")
    public static void startWindow(Object args_) 
    {
        String[] args = (String[]) args_;
        //Class<Type> clazz = (Class<Type>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        System.out.print("EmotionalSongsWindow args:");
        
        if(args_ != null && args.length > 0) {
            System.out.println();
            for (String string : args) 
                System.out.println("-" + string);
        }  
        else
            System.out.println("null");
        
        launch(args);
    }

    
    @Override
    public void start(Stage root) throws Exception 
    {
        this.root = root;

        root.setTitle("EmotionalSongs 2.0");
        root.setResizable(true);

        root.setOnCloseRequest(new EventHandler<WindowEvent>() {
            
            @Override
			public void handle(WindowEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Conferma");
				alert.setHeaderText("");
				alert.setContentText("Sei sicuro di voler uscire dal programma?");
				Optional<ButtonType> result = alert.showAndWait();
				
				if(result.get() == ButtonType.OK) {
					System.out.println("Closing application..");
                    event.consume();
					closeWindow();
				}
				else
					event.consume();
            } 
        });

        root.addEventFilter(ConnectionEvent.DISCONNECTED, this::handleConnectionLostEvent);
        root.addEventFilter(ConnectionEvent.SERVER_NOT_FOUND, this::handleInvalidConnectionEvent);
        root.addEventFilter(ConnectionEvent.CONNECTED, this::handleConnectedEvent);

        sceneManager.setStage(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, root);
        sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, ApplicationScene.ACCESS_PAGE);
    }


    public void closeWindow() {
        sceneManager.removeStage(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, getClass());
        root.close();
        System.exit(0);
    }


    public void handleConnectionLostEvent(ConnectionEvent event) {
        System.out.println("Connection lost");
        ObjectsCache.getInstance().clearAllCache();
        
        this.showConnectionAlert();
        Platform.runLater(() -> {
            SceneManager.instance().setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, ApplicationScene.ACCESS_PAGE);
        });
    }

    public void handleConnectedEvent(ConnectionEvent event) {
        System.out.println("Host connected");

        new Thread(() -> {
            try {ConnectionManager.getConnectionManager().getMostPopularSongs(10, 0);} 
            catch (Exception e) {e.printStackTrace();}
        }).start();

        new Thread(() -> {
            try {ConnectionManager.getConnectionManager().getRecentPublischedAlbum(10,0,30);} 
            catch (Exception e) {e.printStackTrace();}
        }).start();
       

        
            
        
    }

    public void handleInvalidConnectionEvent(ConnectionEvent event) {
        System.out.println("invalid parameter");
        this.showInvalidConnectionAlert();
    }


    public void showConnectionAlert() 
    {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(Main.applicationLanguage == 0 ? "Errore di connessione" : "Connection Error");

            if(Main.applicationLanguage == 0) {
                alert.setHeaderText("Verifica la tua connessione a internet,\n o prova a modificare le impostazioni di\nconnessione");
                alert.setContentText("Impossibile connettersi al server");
            }
            else if(Main.applicationLanguage == 1) {
                alert.setHeaderText("Check your Internet connection,\nor try changing your connection settings");
                alert.setContentText("Unable to connect to the server");
            }
            
            // Aggiungi un pulsante "OK" per chiudere l'alert
            alert.getButtonTypes().setAll(ButtonType.OK);

            // Mostra l'alert e attendi la chiusura prima di procedere con la Timeline
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Puoi gestire l'azione quando l'utente preme "OK" qui, se necessario
            }
            //SceneManager.getInstance().showScene(SceneName.ACCESS_PAGE);
        });
	}

    public void showInvalidConnectionAlert() 
    {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(Main.applicationLanguage == 0 ? "Errore di connessione" : "Connection Error");

            if(Main.applicationLanguage == 0) {
                alert.setHeaderText("parametri di connessione non corretti");
                alert.setContentText("Impossibile connettersi al server");
            }
            else if(Main.applicationLanguage == 1) {
                alert.setHeaderText("incorrect connection parameters");
                alert.setContentText("Unable to connect to the server");
            }
            
            // Aggiungi un pulsante "OK" per chiudere l'alert
            alert.getButtonTypes().setAll(ButtonType.OK);

            // Mostra l'alert e attendi la chiusura prima di procedere con la Timeline
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Puoi gestire l'azione quando l'utente preme "OK" qui, se necessario
            }
        });
	}
}
