package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import objects.Song;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.UnknownHostException;
import java.util.HashMap;
import java.util.Optional;

import applicationEvents.ConnectionEvent;
import controllers.WindowContainerController;
import utility.PathFormatter;


public class EmotionalSongs extends Application
{
    //================================[Variabili di classe]================================//
    public static final String ApplicationDirectory = System.getProperty("user.dir");
    public static final String FXML_folder_path = PathFormatter.formatPath("page-fxml"); //main\\resources\\pages-fxml
    public static final String CSS_file_folder = PathFormatter.formatPath(ApplicationDirectory + "\\src\\main\\css"); //main\\resources\\pages-fxml
    public static final String flagsFolder = PathFormatter.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application\\image\\flags");
    public static final String LocationsPath = PathFormatter.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application\\data\\comuni.json");

    public static int applicationLanguage = 0;
    private static EmotionalSongs instance = null;


    //================================[Variabili]================================//

    private ConnectionManager connectionManager;
    public Stage stage;


    static {

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static EmotionalSongs getInstance() {
        return instance;
    }


    @Override
    public void start(Stage stage) throws IOException
    {
        EmotionalSongs.instance = this;

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.setStage(stage);
        this.stage = stage;

        //stage.initStyle(StageStyle.UTILITY);

        this.connectionManager = ConnectionManager.getConnectionManager();
        
        /*if(connectionManager.testCustomConnection("192.168.1.128",8090)) {
            System.out.println("Server found on 192.168.1.128:8090" );
            connectionManager.setConnectionData("192.168.1.128",8090);
            connectionManager.connect();
        }
        else {
            System.out.println("server not found on 192.168.1.128:8090");
            logout();
        }*/

        

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

        
        
        sceneManager.showAccess();
        //WindowContainerController controller = (WindowContainerController) setStageScene("ApplicationBase");
        //controller.setAccessPage();

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
            alert.setTitle("Errore connessione");
            alert.setHeaderText("Verifica la tua connessione a internet," + "\n" + "o prova a modificare le impostazioni di" + 
                    "\n" + "connessione");
            alert.setContentText("Impossibile connettersi al server");

            // Aggiungi un pulsante "OK" per chiudere l'alert
            alert.getButtonTypes().setAll(ButtonType.OK);

            // Mostra l'alert e attendi la chiusura prima di procedere con la Timeline
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Puoi gestire l'azione quando l'utente preme "OK" qui, se necessario
            }
        });
	}




    

    private static void openLink(String link) throws IOException, URISyntaxException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(link));
        } else {
            // Se il desktop non è supportato o l'azione di apertura del browser non è supportata,
            // puoi gestire l'apertura del link in modo diverso qui (ad esempio, visualizzando il link in un terminale).
            System.out.println("Desktop o l'azione di apertura del browser non sono supportati.");
        }
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