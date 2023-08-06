package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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

    private Stage stage;


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
        this.stage = stage;
        /*System.out.println(EmotionalSongs.class.getResource("hello-view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(EmotionalSongs.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);*/

        stage.setOnCloseRequest(event -> {
            event.consume();
            logout();
        });

        this.stage.setTitle("EmotionalSongs");
        WindowContainerController temp = (WindowContainerController) setStageScene("ApplicationBase");
        temp.setAccessPage();

        ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
        connectionManager.testCustomConnection("192.168.1.128",8090);
    
    }

    public void logout() {
        stage.close();
    }

    public Object SetScene(String sceneName, Object anchor) throws IOException {

        FXMLLoader loader = getSceneLoader(sceneName);
        Object view = loader.load();

        if(anchor instanceof BorderPane) {
            BorderPane temp = (BorderPane)anchor;

            if(view instanceof AnchorPane) {
            temp.getChildren().removeAll();
            temp.setCenter((AnchorPane)view);
            }
            else if(view instanceof BorderPane) {
                temp.getChildren().removeAll();
                temp.setCenter((BorderPane)view);
            }
        }
        else if(anchor instanceof AnchorPane) {
            AnchorPane temp = (AnchorPane)anchor;  
        }
        return loader.getController();
    }

    public Object SetSceneOnAnchor(String sceneName, BorderPane anchor, Callback<Class<?>, Object> controllerFactory) throws IOException {

        FXMLLoader loader = getSceneLoader(sceneName);
        AnchorPane view = loader.load();

        loader.setControllerFactory(controllerFactory);
        anchor.getChildren().removeAll();
        anchor.setCenter(view);

        return loader.getController();
    }

    private FXMLLoader getSceneLoader(String name) throws IOException  {
        FXMLLoader loader = new FXMLLoader();
        System.out.println(EmotionalSongs.class.getResource(name + ((!name.endsWith(".fxml")) ? ".fxml" : "")));
        loader.setLocation(EmotionalSongs.class.getResource(name + ((!name.endsWith(".fxml")) ? ".fxml" : "")));
        return loader;
    }


    public Object setStageScene(String name)
    {
        //String path = PathFormatter.formatPath(EmotionalSongs.FXML_folder_path + "\\" + name + ((!name.endsWith(".fxml")) ? ".fxml" : ""));
        Object output = null;

        try {
            FXMLLoader loader = getSceneLoader(name);
            System.out.println(loader);
            //AnchorPane view = loader.load();
            Scene scene = new Scene(loader.load());
            this.stage.setScene(scene);
            this.stage.show();

            output = loader.getController();
        }
        catch (IOException e) {
            System.out.println("File loading error");
            System.out.println(e);
            e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return output;
    }
}