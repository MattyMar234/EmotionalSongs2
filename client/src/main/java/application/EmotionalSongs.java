package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import objects.Account;

import java.io.IOException;
import java.lang.System.Logger.Level;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import application.SceneManager.ApplicationScene;
import applicationEvents.ConnectionEvent;
import utility.ImageDownloader;
import utility.UtilityOS;


public class EmotionalSongs extends Application
{
    //================================[Variabili di classe]================================//
    public static final String ApplicationDirectory = System.getProperty("user.dir");
    public static final String FXML_folder_path = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application"); //main\\resources\\pages-fxml
    public static final String CSS_file_folder = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\css"); //main\\resources\\pages-fxml
    public static final String flagsFolder = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application\\image\\flags");
    public static final String LocationsPath = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application\\data\\comuni.json");
    public static final String ImageFolder = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application\\image");
    
    public static final ImageDownloader imageDownloader = new ImageDownloader();
    private static EmotionalSongs instance = null;
    
    
    //================================[Variabili]================================//
    public static int applicationLanguage = 0;
    public ApplicationActions userActions = new ApplicationActions();
    public Stage stage;
    public Account account;
    
    private ConnectionManager connectionManager;


    static {

    }

    public static void main(String[] args) 
    {
        launch(args);
        //ManagementFactory.getPlatformMXBean(PlatformLoggingMXBean.class).setLoggerLevel("javafx.css", "OFF");

        //String sha256hex = DigestUtils.sha256Hex("1234");
        //System.out.println(sha256hex);
        //String DeSha256hex = DigestUtils.256("2c1f6848fd51c5ff3a7d3c275bde7fc33c8e143138aa87e712f4a0470559ce40");
    }

    public static EmotionalSongs getInstance() {
        return instance;
    }


    @Override
    public void start(Stage stage) throws IOException
    {
        EmotionalSongs.instance = this;

        connectionManager = ConnectionManager.getConnectionManager();
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.setStage(stage);
        this.stage = stage;

        //stage.setTitle();
        System.out.println(UtilityOS.formatPath(ImageFolder + "\\generic\\icon.png"));
        //stage.getIcons().add(new Image();
        
        
      


        //stage.initStyle(StageStyle.UTILITY);

        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            
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
					logout();
				}
				else
					event.consume();
            } 
        });

        stage.addEventFilter(ConnectionEvent.DISCONNECTED, this::handleConnectionLostEvent);
        stage.addEventFilter(ConnectionEvent.SERVER_NOT_FOUND, this::handleInvalidConnectionEvent);

    
        sceneManager.showScene(ApplicationScene.ACCESS_PAGE);
        

        /*for (Song s : this.connectionManager.getService().getMostPopularSongs(10,0)) {
           System.out.println(s); 

           try {
                openLink(s.getSpotifyUrl());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
            break;
        }*/


    }

    public void handleConnectionLostEvent(ConnectionEvent event) {
        System.out.println("Connection lost");
        this.showConnectionAlert();
        Platform.runLater(() -> {
            SceneManager.getInstance().showScene(ApplicationScene.ACCESS_PAGE);
        });
    }

    public void handleInvalidConnectionEvent(ConnectionEvent event) {
        System.out.println("invalid parameter");
        this.showInvalidConnectionAlert();
    }

    public void logout() {
        //connectionManager.disconnect();
        stage.close();
        System.exit(0);
    }



    /**
	 * Shows a connection alert dialog
	 */
	public void showConnectionAlert() 
    {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(EmotionalSongs.applicationLanguage == 0 ? "Errore di connessione" : "Connection Error");

            if(EmotionalSongs.applicationLanguage == 0) {
                alert.setHeaderText("Verifica la tua connessione a internet,\n o prova a modificare le impostazioni di\nconnessione");
                alert.setContentText("Impossibile connettersi al server");
            }
            else if(EmotionalSongs.applicationLanguage == 1) {
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
            alert.setTitle(EmotionalSongs.applicationLanguage == 0 ? "Errore di connessione" : "Connection Error");

            if(EmotionalSongs.applicationLanguage == 0) {
                alert.setHeaderText("parametri di connessione non corretti");
                alert.setContentText("Impossibile connettersi al server");
            }
            else if(EmotionalSongs.applicationLanguage == 1) {
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




    

    

    /*private void applyModification(Stage primaryStage, StackPane root) {
        Undecorator undecorator = new Undecorator(primaryStage, root);
        undecorator.getStylesheets().add("/skin/undecorator.css");
        Scene scene = new Scene(undecorator, 300, 250);
        primaryStage.setScene(scene);
        scene.setFill(null);
        Node stageMenu = undecorator.lookup("#StageMenu");
        stageMenu.setVisible(false);
        Node maximize = undecorator.lookup(".decoration-button-maximize");
        maximize.setVisible(false);
        Node manimize = undecorator.lookup(".decoration-button-minimize");
        manimize.setVisible(false);
        Node restore = undecorator.lookup(".decoration-button-fullscreen");
        restore.setVisible(false);

    }*/
}