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

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import application.SceneManager.ApplicationScene;
import applicationEvents.ConnectionEvent;
import utility.ImageDownloader;
import utility.UtilityOS;


public class Main
{
    //================================[Variabili di classe]================================//
    public static final boolean jarExecution = isJarFIle();
    public static final String ApplicationDirectory = initPath();

    // public static final String FXML_folder_path = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application"); //main\\resources\\pages-fxml
    // public static final String CSS_file_folder = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\css"); //main\\resources\\pages-fxml
    // public static final String LocationsPath = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application\\data\\comuni.json");
    // public static final String ImageFolder = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application\\image");
    // public static final String flagsFolder = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application\\image\\flags");
    // public static final String emotionFolder = UtilityOS.formatPath(ApplicationDirectory + "\\src\\main\\resources\\application\\image\\emotion_icon");
    
    

    public static final ImageDownloader imageDownloader = new ImageDownloader();
    private static Main instance = null;
    
    
    //================================[Variabili]================================//
    public static int applicationLanguage = 0;
    public static double PING_TIME_us = 0;
    public static Account account;
    
    private ConnectionManager connectionManager;


    private static boolean isJarFIle() {
        try {
            File f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String path = f.getAbsolutePath();

            if(path.endsWith(".jar")) {
                System.out.println("Jar file detected");
                return true;
            }
            return false;
        } 
        catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(0);
            return false;
        }
    }

    private static String initPath() {
        try {
            File f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String path = f.getAbsolutePath();

            if(path.endsWith(".jar")) {
                return "";
            }
            
            return UtilityOS.formatPath(System.getProperty("user.dir"));
        
        } 
        catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(0);
            return "";
        }
    }



    public static void main(String[] args) 
    {
        SceneManager.instance().startWindow(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, args);
        //ManagementFactory.getPlatformMXBean(PlatformLoggingMXBean.class).setLoggerLevel("javafx.css", "OFF");

        //String sha256hex = DigestUtils.sha256Hex("1234");
        //System.out.println(sha256hex);
        //String DeSha256hex = DigestUtils.256("2c1f6848fd51c5ff3a7d3c275bde7fc33c8e143138aa87e712f4a0470559ce40");
    }

    /*
     * Stage stage = sceneManager.getWindowStage(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW);
        stage.addEventFilter(ConnectionEvent.DISCONNECTED, this::handleConnectionLostEvent);
     */
    

    

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