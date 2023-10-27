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
import utility.UtilityOS;

import org.javatuples.*;

import applicationEvents.ConnectionEvent;
import controllers.ApplicationAccessController;
import controllers.ControllerBase;
import controllers.MainPage_SideBar_Controller;
import controllers.WindowContainerController;
import interfaces.Injectable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


public class SceneManager {

    private static final String AccessPage_path = "AccessPage.fxml";
    private static final String MainPage_SideBar_path = "MainPage_SideBar.fxml";
    private static final String MainPage_home_path = "MainPage_Home.fxml";
    private static final String RegistrationPage_path = "UserRegistration.fxml";
    private static final String ElementDisplay_path = "MainPage_ElementDisplayer.fxml";
    private static final String BaseContainer_path = "ApplicationBase.fxml";
    private static final String Comment_path = "Comment.fxml";
    private static final String SongListView_path = "SongListCell.fxml";

    private int theme = 0;
    private ArrayList<ControllerBase> loadedControllers = new ArrayList<>();
    private ArrayList<SceneElemets> loadedSceneElemets = new ArrayList<>();
    private ApplicationState applicationState = null;

    private HashMap<ApplicationWinodws, ApplicationScene> windows_currentScene = new HashMap<>();
    private HashMap<ApplicationWinodws, Stack<ControllerBase>> windows_currentSceneControllers = new HashMap<>();
    
    private static SceneManager instance;
    private Stage stage;


    /**
     * Classe enum per tener traccia delle finestre presenti nell'applicazione
     */
    public enum ApplicationWinodws {
        EMOTIONL_SONGS_WINDOW(null),
        PLAYLIST_CREATION_WINDOW(null);
        
        Class<?> windowManagerClass;

        private ApplicationWinodws(Class<?> windowManagerClass) {
            this.windowManagerClass = windowManagerClass;
        }

        private Class<?> getWindowManagerClass() {
            return this.windowManagerClass;
        }
    }

    /**
     * Classe enum per tener traccia degli static in cui si può trovare una finestra
     */
    public enum ApplicationState {
        ACCESS_PAGE,
        REGISTRATION_PAGE,
        MAIN_PAGE
    }

    public enum FXML_elements {

        LIST_ELEMENT(SongListView_path);

        private String file;
    
        private FXML_elements(String file) {
            this.file = file;
        }

        public String getPath() {
            return file;
        }
    }

    private enum SceneElemets
    {
        BASE_CONTAINER(BaseContainer_path),
        REGISTRATION(RegistrationPage_path),
        ACCESS(AccessPage_path),
        MAIN_SIDEBAR(MainPage_SideBar_path),
        MAIN_HOME(MainPage_home_path),
        MAIN_EXPLORE(null),
        MAIN_DISPLAYER(ElementDisplay_path);
        

        private Object[] parametre;
        private String file;

        private SceneElemets(String file, Object...parametre) {
            this.parametre = parametre;
            this.file = file;
        }

        public String getElemetFilePath() {
            return file;
        }

        public Object[] getParameters() {
            return parametre;
        }
    }
    


    public enum ApplicationScene 
    {
        ACCESS_PAGE,
        REGISTRATION_PAGE,
        CREATE_PLAYLIST,
        MAIN_PAGE_HOME,
        MAIN_PAGE_EXPLORE,
        MAIN_PAGE_PLAYLIST,
        MAIN_PAGE_SHOW_SONG,
        MAIN_PAGE_SHOW_ALBUM,
        DISPLAY_ELEMENT_PAGE,
        MAIN_PAGE_SHOW_PLAYLIST;
    }

    public static SceneManager getInstance() {
        if (instance == null)
            instance = new SceneManager();
        return instance;
    }

    private SceneManager() {
        windows_currentScene.put(ApplicationWinodws.EMOTIONL_SONGS_WINDOW, null);
        windows_currentScene.put(ApplicationWinodws.PLAYLIST_CREATION_WINDOW, null);
        
        windows_currentSceneControllers.put(ApplicationWinodws.EMOTIONL_SONGS_WINDOW, new Stack<>());
        windows_currentSceneControllers.put(ApplicationWinodws.PLAYLIST_CREATION_WINDOW, new Stack<>());
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }


    public void setStage(Stage primaryStage) {

        stage = primaryStage;
        stage.setTitle("Emotional Songs 2.0");
        stage.setResizable(true);
    }

    public void setResizable(boolean resizable) {
        stage.setResizable(resizable);
    }


    //============================================= METHODS =============================================//
    /**
    * Resistuisce il loader del file FXML specificato
    * @param name Il nome del file FXML
    * @return Il loader del file FXML
    * @throws IOException Eccezione generata nel caso il file FXML non sia trovato
    */
    private FXMLLoader get_FXML_File_Loader(String name) throws IOException  
    {
        FXMLLoader loader = new FXMLLoader();
        String path = UtilityOS.formatPath(EmotionalSongs.FXML_folder_path + "\\" + ((!name.endsWith(".fxml")) ? name + ".fxml" : name));
        File file = new File(path);
        URL file_URL = file.toURI().toURL();
        loader.setLocation(file_URL);
       
        return loader;
    }

    /**
     * In base al tipo di nodo, eseguo l'operazione per inserire il nuovo codice
     * @param view
     * @param anchor
     */
    private void inject_FXML_code(Node view, Object anchor) 
    {
        if(anchor instanceof BorderPane) {
            BorderPane temp = (BorderPane)anchor;
            temp.getChildren().removeAll();
            temp.setCenter(view);   
        }
        else if(anchor instanceof AnchorPane) {
            AnchorPane temp = (AnchorPane)anchor; 
            temp.getChildren().removeAll(); 
            temp.getChildren().add(view);
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
     * Imposta il contenute dello stage
     */
    private Pair<Scene,FXMLLoader> setStageScene(String name)
    {
        Pair<Scene,FXMLLoader> output = null;
        System.out.println("FXML file requested: " + name);

        try {
            Scene scene = null; 
            FXMLLoader loader = get_FXML_File_Loader(name);
           
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
        catch (IllegalStateException e) {
            if(e.toString().toLowerCase().contains("Location is not set")) {
                System.out.println("file " + name + "not found");
            }
        }  
        catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }  
        return output;    
    }


    /**
    * Carico il contenuto di un file fxml all'interno di un anchor generico
    * @param sceneName Il nome del file fxml
    * @param anchor Il riferimento dell'anchor (anchorPane o BorderPane)
    * @return riferimento della classe controller del file fxml caricato.
    */ 
    public Object injectElement(FXML_elements element, Object anchor) {

        FXMLLoader loader = null;
        try {
            loader = get_FXML_File_Loader(element.getPath());
            Node view = loader.load();
            inject_FXML_code(view, anchor);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return loader.getController();
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
            loader = get_FXML_File_Loader(sceneName);
            Node view = loader.load();
            inject_FXML_code(view, anchor);
        } 
        catch (IOException e) {
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
    public Object injectScene(String sceneName, Object anchor, final Object controller) throws IOException
    //public Object injectScene(String sceneName, Object anchor, Callback<Class<?>, Object> controllerFactory) throws IOException 
    {
        FXMLLoader loader = null;


        try {
            loader = get_FXML_File_Loader(sceneName);
            Node view = loader.load();
            loader.setControllerFactory(controllerClass -> {return controller;});//controllerClass -> {return controller;}
            //loader.setController(controller);
            inject_FXML_code(view, anchor);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return loader.getController();
    }

 

    /**
    * 
    * @param sceneName Identificativo della scena
    * @param args Parametri per i controllers
    */
    public ControllerBase showScene(ApplicationScene sceneName, Object... args) 
    {
        SceneAction action = new SceneAction(sceneName, args);
        EmotionalSongs.getInstance().userActions.addAction(action);
        return executeShowScene(ApplicationWinodws.EMOTIONL_SONGS_WINDOW,sceneName, args);
    } 


    public ControllerBase showScene(SceneAction sceneAction) {
        return executeShowScene(ApplicationWinodws.EMOTIONL_SONGS_WINDOW,sceneAction.scena_name, sceneAction.args);
    }

    
    private ControllerBase executeShowScene(ApplicationWinodws window, ApplicationScene sceneName, Object[] args) 
    {
        ApplicationScene currentScene = windows_currentScene.get(window);
        Stack<ControllerBase> loadedController = windows_currentSceneControllers.get(window);         

        //verifico che il nome sia valido
        if(sceneName == null)
            throw new RuntimeException("sceneName can't be \"NULL\"");
        
        //verifico se devo caricare una scena diversa da quella attuale
        if(currentScene == sceneName)
            return null;

        
        switch(window)
        {
            case EMOTIONL_SONGS_WINDOW -> {

                if(loadedController.size() == 0) {
                    Pair<Scene,FXMLLoader> result = setStageScene(SceneElemets.BASE_CONTAINER.file);
                    Scene scene = result.getValue0();
                    FXMLLoader loader = result.getValue1();
                    loadedController.push((ControllerBase)loader.getController());
                }

                switch(sceneName) 
                {
                    case ACCESS_PAGE:
                        while(loadedController.size() > 1) loadedController.pop();
                        loadedController.push((ControllerBase)injectScene(SceneElemets.ACCESS.file, loadedController.peek().anchor_for_injectScene));
                        EmotionalSongs.getInstance().stage.setMinWidth(800);
                        break;

                    case REGISTRATION_PAGE:
                        while(loadedController.size() > 1) loadedController.pop();
                        loadedController.push((ControllerBase)injectScene(SceneElemets.REGISTRATION.file, loadedController.peek().anchor_for_injectScene));
                        break;

                    case CREATE_PLAYLIST:
                        break;
                    case MAIN_PAGE_EXPLORE:
                        break;
                    case MAIN_PAGE_HOME:

                        while(loadedController.size() > 2) loadedController.pop();

                        if(loadedController.size() == 1) {
                           loadedController.push((ControllerBase)injectScene(SceneElemets.MAIN_SIDEBAR.file, loadedController.peek().anchor_for_injectScene)); 
                        }
                        else if(!(loadedController.peek() instanceof MainPage_SideBar_Controller)) {
                            loadedController.pop();
                            loadedController.push((ControllerBase)injectScene(SceneElemets.MAIN_SIDEBAR.file, loadedController.peek().anchor_for_injectScene));
                        }
                        loadedController.push((ControllerBase)injectScene(SceneElemets.MAIN_HOME.file, loadedController.peek().anchor_for_injectScene));
                        break;

                    case MAIN_PAGE_SHOW_PLAYLIST:
                    case MAIN_PAGE_SHOW_ALBUM:
                    case MAIN_PAGE_SHOW_SONG:
                    case MAIN_PAGE_PLAYLIST:
                    case DISPLAY_ELEMENT_PAGE:
                        while(loadedController.size() > 2) loadedController.pop();

                        if(loadedController.size() == 1) {
                           loadedController.push((ControllerBase)injectScene(SceneElemets.MAIN_SIDEBAR.file, loadedController.peek().anchor_for_injectScene)); 
                        }
                        else if(!(loadedController.peek() instanceof MainPage_SideBar_Controller)) {
                            loadedController.pop();
                            loadedController.push((ControllerBase)injectScene(SceneElemets.MAIN_SIDEBAR.file, loadedController.peek().anchor_for_injectScene));
                        }
                        loadedController.push((ControllerBase)injectScene(SceneElemets.MAIN_DISPLAYER.file, loadedController.peek().anchor_for_injectScene));
                        break;
                    
                    
                    
                    default:
                        throw new RuntimeException("Invalid window scene");   
                }
            }

            case PLAYLIST_CREATION_WINDOW -> {

            }

            default -> {
                throw new RuntimeException("Invalid window");
            }
        }



        //==================================== verfica passaggio args... ====================================//
        //verifico se ho dei parametri da passare al controller
        if(args.length > 0 ) {
            //verifico se implementa l'interfaccia
            // Inheritance testing:
            Class<?> interfaceType = Injectable.class;
            //Class<?> classType = SomeClass.class;

            if (interfaceType.isAssignableFrom(windows_currentSceneControllers.get(window).peek().getClass())) {
                Injectable controller = (Injectable)windows_currentSceneControllers.get(window).peek();
                controller.injectData(args);
            }
            else {
                //se ho dei parametri che non posso passare
                throw new UnsupportedOperationException("this controller not implements: " + Injectable.class.getName());
            }
        }

        windows_currentScene.put(window, sceneName);
        return windows_currentSceneControllers.get(window).peek();
    }
}
