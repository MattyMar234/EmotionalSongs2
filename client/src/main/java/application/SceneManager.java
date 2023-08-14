package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import objects.SceneAction;
import objects.UserActions;

import org.javatuples.*;

import applicationEvents.ConnectionEvent;
import controllers.ControllerBase;
import controllers.MainPage_SideBar_Controller;
import controllers.WindowContainerController;

import java.io.IOException;
import java.util.ArrayList;

public class SceneManager {

    private static final String AccessPage_path = "AccessPage.fxml";
    private static final String MainPage_SideBar_path = "MainPage_SideBar.fxml";
    private static final String MainPage_home_path = "MainPage_Home.fxml";
    private static final String RegistrationPage_path = "UserRegistration.fxml";

    private int theme = 0;
    private ArrayList<ControllerBase> loadedControllers = new ArrayList<>();
    private ApplicationState applicationState = null;
    
    private static SceneManager instance;
    private Stage stage;
    


    public enum SceneName {

        ACCESS_PAGE(ApplicationState.ACCESS_PAGE,AccessPage_path),
        REGISTRATION_PAGE(ApplicationState.REGISTRATION_PAGE ,RegistrationPage_path),
        HOME_PAGE(ApplicationState.MAIN_PAGE, MainPage_SideBar_path, MainPage_home_path),
        PLAYLISTS_PAGE(ApplicationState.MAIN_PAGE,""),
        ACCOUNT_PAGE(ApplicationState.MAIN_PAGE,""),
        COMMENT_PAGE(ApplicationState.MAIN_PAGE,"");


        private String[] file_array;
        private ApplicationState state;

    
        private SceneName(ApplicationState state, String... file) {
            this.file_array = file;
            this.state = state;
        }

        public String[] getFilePath() {
            return file_array;
        }

        public ApplicationState getCorrespondentState() {
            return state;
        }
    }

    private enum ApplicationState {
        ACCESS_PAGE,
        REGISTRATION_PAGE,
        MAIN_PAGE
    }

    

    


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

    /**
    * Resistuisce il loader del file FXML specificato
    * @param name Il nome del file FXML
    * @return Il loader del file FXML
    * @throws IOException Eccezione generata nel caso il file FXML non sia trovato
    */
    public FXMLLoader getSceneLoader(String name) throws IOException  {
        FXMLLoader loader = new FXMLLoader();
        System.out.println(EmotionalSongs.class.getResource(name + ((!name.endsWith(".fxml")) ? ".fxml" : "")));
        loader.setLocation(EmotionalSongs.class.getResource(name + ((!name.endsWith(".fxml")) ? ".fxml" : "")));
        //print hereee
        System.out.println();

        return loader;
    }


    private void inject_FXML_code(Node view, Object anchor) 
    {
        if(anchor instanceof BorderPane) {
            BorderPane temp = (BorderPane)anchor;
            temp.getChildren().removeAll();
            temp.setCenter((AnchorPane)view);   
        }
        else if(anchor instanceof AnchorPane) {
            AnchorPane temp = (AnchorPane)anchor; 
            temp.getChildren().removeAll(); 
            temp.getChildren().add(temp);
        }
        else if(anchor instanceof VBox) {
            VBox temp = (VBox)anchor;  
            temp.getChildren().add(view);
        }
        else if(anchor instanceof HBox) {
            HBox temp = (HBox)anchor;  
            temp.getChildren().add(view);
        }
    }


    /**
    * Carico il contenuto di un file fxml all'interno di un anchor generico
    * @param sceneName Il nome del file fxml
    * @param anchor Il riferimento dell'anchor (anchorPane o BorderPane)
    * @return riferimento della classe controller del file fxml caricato.
    */ 
    public Object injectScene(String sceneName, Object anchor) {

        FXMLLoader loader = null;
        try {
            loader = getSceneLoader(sceneName);
            Node view = loader.load();
            inject_FXML_code(view, anchor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        return loader.getController();
    }

    /**
    * Carico il contenuto di un file fxml all'interno di un anchor generico, con la possibilià di specificare il costruttore del controller.
    * @param sceneName Il nome del file fxml
    * @param anchor Il riferimento dell'anchor (anchorPane o BorderPane)
    * @param controllerFactory il costruttore della classe controller del file fxml
    * @return riferimento della classe controller del file fxml caricato.
    */ 
    public Object injectScene(String sceneName, Object anchor, Object controller) throws IOException
    //public Object injectScene(String sceneName, Object anchor, Callback<Class<?>, Object> controllerFactory) throws IOException 
    {
        FXMLLoader loader = null;

        try {
            loader = getSceneLoader(sceneName);
            Node view = loader.load();
            loader.setControllerFactory(controllerClass -> {return controller;});//controllerClass -> {return controller;}
            inject_FXML_code(view, anchor);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return loader.getController();
    }

    

    /*
     * Imposta il contenute dello stage
     */
    public Pair<Scene,FXMLLoader> setStageScene(String name)
    {
        Pair<Scene,FXMLLoader> output = null;

        //String path = PathFormatter.formatPath(EmotionalSongs.FXML_folder_path + "\\" + name + ((!name.endsWith(".fxml")) ? ".fxml" : ""));
        //System.out.println("file requested: " + name);
    

        try {
            
            Scene scene = null;
            FXMLLoader loader = getSceneLoader(name);
           
            scene = new Scene(loader.load());
            this.stage.setScene(scene);
            this.stage.show();

            output = new Pair<Scene,FXMLLoader>(scene, loader);
        }
        catch (IOException e) {
            System.out.println("File loading error: " + e);

            if(e.toString().contains("javafx.fxml.LoadException:")) {
                System.out.println("Error in Controller class");
            }

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

        //this.sceneBase = loader.getController();
    }

    /**
    * 
    * @param sceneName Identificativo della scena
    * @param args Parametri per i controllers
    */
    public void showScene(SceneName sceneName, Object... args) 
    {
        SceneAction action = new SceneAction(sceneName, args);
        EmotionalSongs.getInstance().userActions.addAction(action);
        executeShowScene(sceneName, args);
    } 


    public void showScene(SceneAction sceneAction) {
        executeShowScene(sceneAction.scenaName, sceneAction.args);
    }


    private void executeShowScene(SceneName sceneName, Object[] args) {
        //offset del file
        int fileOffset = 0;
        
        
        //se non ho ancora uno stato oppure devo caricare uno stato differente
        if(applicationState == null || applicationState != sceneName.getCorrespondentState()) {
            applicationState = sceneName.getCorrespondentState(); //ottengo lo stato a cui appartiene la scena
            loadedControllers.clear();
        }
        //se devo modificare la scena di uno stesso stato
        else if(applicationState == ApplicationState.MAIN_PAGE && applicationState == sceneName.getCorrespondentState()) {
            applicationState = sceneName.getCorrespondentState();
            ControllerBase c = (ControllerBase)loadedControllers.get(0);
            loadedControllers.clear();
            loadedControllers.add(c);
            fileOffset = 1;
        }
        

        for (int i = fileOffset; i < sceneName.getFilePath().length; i++) {
            String file = sceneName.getFilePath()[i];

            //se sono la scena di base
            if(loadedControllers.size() == 0) 
            {
                Pair<Scene,FXMLLoader> result = setStageScene(file);
                Scene scene = result.getValue0();
                FXMLLoader loader = result.getValue1();

                //se voglio un tema da applicare all'applicazione
                /*if(theme == 0) {
                    scene.getStylesheets().add(SceneManager.class.getResource("/styles/dark-theme.css").toExternalForm());
                }
                else if (theme == 1) {
                    scene.getStylesheets().add(SceneManager.class.getResource("/styles/dark-theme.css").toExternalForm());
                }*/

                loadedControllers.add(loader.getController());
            }
            //se sono una scena da inserire in un altra scena
            else {
                //accedo all'anchor dell'untimo controller della lista controller e inserisco il codice della nuova scena
                loadedControllers.add((ControllerBase)injectScene(file, loadedControllers.get(loadedControllers.size() - 1).anchor_for_injectScene));
            }
        }
        
        //se voglio fare delle operazioni aggiuntive per ogni scena
        //per accedere a un specifico controller uso: "loadedControllers.get(index)"
        switch (sceneName) 
        {
            case ACCESS_PAGE -> {

            }
            case REGISTRATION_PAGE -> {

            }
            case HOME_PAGE -> {
                
            }
            default -> {
                throw new IllegalArgumentException("Unexpected value: " + sceneName);
            }
        }
    }
}
