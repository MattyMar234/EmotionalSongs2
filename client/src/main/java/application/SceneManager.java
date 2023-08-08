package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.javatuples.*;

import controllers.MainPageController;
import controllers.WindowContainerController;

import java.io.IOException;

public class SceneManager {

    private static final String ACCESSPAGE = "/fxml/access-page.fxml";
    private Stage stage;
    private static SceneManager instance;
    private WindowContainerController sceneBase;

    


    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }


    public void setStage(Stage primaryStage) {

        stage = primaryStage;
        stage.setTitle("Emotional Songs 2.0");
        stage.setResizable(true);
    }

    public void setResizable(boolean resizable) {
        stage.setResizable(resizable);
    }


    private FXMLLoader getSceneLoader(String name) throws IOException  {
        FXMLLoader loader = new FXMLLoader();
        System.out.println(EmotionalSongs.class.getResource(name + ((!name.endsWith(".fxml")) ? ".fxml" : "")));
        loader.setLocation(EmotionalSongs.class.getResource(name + ((!name.endsWith(".fxml")) ? ".fxml" : "")));
        return loader;
    }


    private void addFileCode(Object view, Object anchor) 
    {
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
            temp.getChildren().add(temp);
        }
    }

    //operazione classica. Mi ritorna il riferimento della classe controller.
    public Object SetScene(String sceneName, Object anchor) {

        FXMLLoader loader = null;
        try {
            loader = getSceneLoader(sceneName);
            Object view = loader.load();
            addFileCode(view, anchor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        return loader.getController();
    }

    //posso impostare anche il costruttore
    public Object SetSceneOnAnchor(String sceneName, BorderPane anchor, Callback<Class<?>, Object> controllerFactory) throws IOException {

        FXMLLoader loader = getSceneLoader(sceneName);
        Object view = loader.load();
        loader.setControllerFactory(controllerFactory);
        addFileCode(view, anchor);

        return loader.getController();
    }

    

    /*
     * Imposta il contenute dello stage
     */
    public Pair<Scene,FXMLLoader> setStageScene(String name)
    {
        Pair<Scene,FXMLLoader> output = null;

        //String path = PathFormatter.formatPath(EmotionalSongs.FXML_folder_path + "\\" + name + ((!name.endsWith(".fxml")) ? ".fxml" : ""));
        System.out.println("file requested: " + name);
        System.out.println();

        try {
            Scene scene = null;
            FXMLLoader loader = getSceneLoader(name);
           
            scene = new Scene(loader.load());
            this.stage.setScene(scene);
            this.stage.show();

            output = new Pair<Scene,FXMLLoader>(scene, loader);
        }
        catch (IOException e) {
            System.out.println("File loading error" + e);
            e.printStackTrace();
            
        }
        catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }  
        return output;    
    }

    private void showSceneBase() {
        Pair<Scene,FXMLLoader> result = setStageScene("ApplicationBase.fxml");
        FXMLLoader loader = result.getValue1();

        this.sceneBase = loader.getController();
    }


    //set application
    public void showAccess() 
    {
        Pair<Scene,FXMLLoader> result = setStageScene("AccessPage.fxml");
        //SetScene("MainPage_Home.fxml", this.sceneBase.anchor);

        


        //scene.setFill(Color.TRANSPARENT);
        //scene.getStylesheets().add(SceneManager.class.getResource("/styles/dark-theme.css").toExternalForm());
    }


    //show access-page.fxml
    public void showHomePage() {
        
        Pair<Scene,FXMLLoader> result = setStageScene("homepage.fxml");
        Scene scene = result.getValue0();
        FXMLLoader loader = result.getValue1();

        MainPageController mainPageController = loader.getController();

        SetScene("MainPage_Home.fxml", mainPageController.anchor);
        

        //scene.getStylesheets().add(SceneManager.class.getResource("/styles/dark-theme.css").toExternalForm());

        
    }

    public void showRegistrationPage (){
        
        Pair<Scene,FXMLLoader> result = setStageScene("UserRegistration.fxml");
        Scene scene = result.getValue0();

        //scene.getStylesheets().add(SceneManager.class.getResource("/styles/dark-theme.css").toExternalForm());

    }


    /*private static Parent loadFXML(String page){
        Parent root= null;
        try {
            root = FXMLLoader.load(SceneManager.class.getResource(page));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  root;
    }*/
}
