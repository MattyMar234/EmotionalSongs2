package application;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
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
import enumClasses.ElementDisplayerMode;
import interfaces.Injectable;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private static final String PlaylistCreation_path = "PlaylistCreationPage.fxml";

    private int theme = 0;
    private ArrayList<ControllerBase> loadedControllers = new ArrayList<>();
    private ArrayList<SceneElemets> loadedSceneElemets = new ArrayList<>();
    private ApplicationState applicationState = null;

    private HashMap<ApplicationWinodws, ApplicationScene> windows_currentScene = new HashMap<>();
    private HashMap<ApplicationWinodws, Object[]> windows_currentArgs = new HashMap<>();
    private HashMap<ApplicationWinodws, Stack<ControllerBase>> windows_currentSceneControllers = new HashMap<>();
    private HashMap <ApplicationWinodws, Stage> windowsStage = new HashMap<>();
    private HashMap <ApplicationWinodws, Object> activeWindow = new HashMap<>();
    private HashMap <ApplicationWinodws, ApplicationActions> windowUserActions = new HashMap<>();

    private static SceneManager classReference = null;

    /**
     * Classe enum per tener traccia delle finestre presenti nell'applicazione
     */
    public enum ApplicationWinodws {
        EMOTIONALSONGS_WINDOW(EmotionalSongsWindow.class),
        PLAYLIST_CREATION_WINDOW(PlaylistCreationWindow.class);
        
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
        MAIN_DISPLAYER(ElementDisplay_path),
        PLAYLIST_CREATOR(PlaylistCreation_path);

        

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
        MAIN_PAGE_HOME,
        MAIN_PAGE_EXPLORE,
        MAIN_PAGE_PLAYLIST,
        MAIN_PAGE_SHOW_SONG,
        MAIN_PAGE_SHOW_ALBUM,
        DISPLAY_ELEMENT_PAGE,
        MAIN_PAGE_SHOW_PLAYLIST,

        CREATE_PLAYLIST;
    }

    public static SceneManager instance() {
        if (SceneManager.classReference == null) {
            SceneManager.classReference = new SceneManager();
        }
        return SceneManager.classReference;
    }

    private SceneManager() {

        for (ApplicationWinodws availableWIndow : ApplicationWinodws.values()) {
            windows_currentScene.put(availableWIndow, null);
            windows_currentSceneControllers.put(availableWIndow, new Stack<>());
            windowUserActions.put(availableWIndow, new ApplicationActions());
        }
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }


    public void setStage(ApplicationWinodws window, Stage primaryStage) {
        this.windowsStage.put(window, primaryStage);
    }

    public void removeStage(ApplicationWinodws window, Class<?> windowManagerClass) {
        
        //if(windowManagerClass != window.getWindowManagerClass())
        //    throw new RuntimeException("invalid class");
        Stage stage =  this.windowsStage.get(window);
        stage.close();

        this.windowsStage.remove(window);
        this.activeWindow.remove(window);
        
        windows_currentScene.put(window, null);
        windows_currentSceneControllers.get(window).clear();    
        windows_currentSceneControllers.put(window, new Stack<>());
        windowUserActions.put(window, new ApplicationActions());
    }

    public void fireEvent(ApplicationWinodws window, Event event) {
        this.windowsStage.get(window).fireEvent(event);
    }

    public void addEventFilter(ApplicationWinodws window, EventType<Event> eventType, EventHandler<? super Event> eventFilter) {
        this.windowsStage.get(window).addEventFilter(eventType, eventFilter);
    }

    public Stage getWindowStage(ApplicationWinodws window) {
        return this.windowsStage.get(window);
    }

    /**
     * Questa funzione viene utilizzata avviare una particolarte finestra del programma.
     * @param name
     * @param args
     */
    public void startWindow(ApplicationWinodws wName, String[] args) 
    {
        try {
            if(wName == null)
                return;

            if(activeWindow.containsKey(wName)) {
                //throw new RuntimeException("Window already started");
                removeStage(wName,null);
            }

            Class<?> windowManager = wName.getWindowManagerClass();

            if(windowManager == null)
                throw new RuntimeException("Window not available");
        
                Method method = windowManager.getDeclaredMethod("startWindow", Object.class);
                method.setAccessible(true);
                Object result = method.invoke(Object.class, (Object)args);
                activeWindow.put(wName, result);
        } 
        catch (InvocationTargetException e) {
        
            // Answer:
            e.getCause().printStackTrace();
        }
        catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(0);
        }
    }


    public void closeWindow(ApplicationWinodws wName) {

        if(wName == null)
            return;

        if(activeWindow.containsKey(wName)) {
            //throw new RuntimeException("Window already started");
            removeStage(wName,null);
        }
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
        String path = UtilityOS.formatPath(Main.FXML_folder_path + "\\" + ((!name.endsWith(".fxml")) ? name + ".fxml" : name));
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
    private Pair<Scene,FXMLLoader> setStageScene(Stage stage, String name)
    {
        Pair<Scene,FXMLLoader> output = null;
        System.out.println("FXML file requested: " + name);

        try {
            Scene scene = null; 
            FXMLLoader loader = get_FXML_File_Loader(name);
           
            scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show(); 

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



    public void undo(ApplicationWinodws window) {
        windowUserActions.get(window).undo();
    }

    public void redo(ApplicationWinodws window) {
        windowUserActions.get(window).redo();
    }

 

    /**
    * 
    * @param sceneName Identificativo della scena
    * @param args Parametri per i controllers
    */
    public ControllerBase setScene(ApplicationWinodws window, ApplicationScene sceneName, Object... args) 
    {
        ApplicationScene currentScene = windows_currentScene.get(window);
        if((currentScene != sceneName) || differentParametre(window, args)) {
            windowUserActions.get(window).addAction(new SceneAction(sceneName, args));
        }
       
        return executeShowScene(window,sceneName, args);
    }
    
    /**
     * Verifico se i parametri sono differenti
     * @param window
     * @param args
     * @return
     */
    private boolean differentParametre(ApplicationWinodws window, Object[] args) {
        Object[] currentArgs = windows_currentArgs.get(window);
        boolean different = true;
        
        if(currentArgs == null && args == null)
           return false;
        
        if((currentArgs == null && args != null) || (currentArgs != null && args == null))
           return true;

        if(currentArgs.length != args.length)
            return true;
        

        for(int i = 0; i < currentArgs.length; i++) {
            //verifico se hanno lo stesso contenuto
            if(currentArgs[i].equals(args[i])) {
                different = false;
                break;
            }
        }

        return different;
    }


    public ControllerBase showScene(SceneAction sceneAction) {
        return executeShowScene(ApplicationWinodws.EMOTIONALSONGS_WINDOW,sceneAction.scena_name, sceneAction.args);
    }

    
    private ControllerBase executeShowScene(ApplicationWinodws window, ApplicationScene sceneName, Object[] args) 
    {
        if(!this.windowsStage.containsKey(window))
            throw new RuntimeException("windowManager");

        //verifico che il nome sia valido
        if(sceneName == null)
            throw new RuntimeException("sceneName can't be \"NULL\"");
        

        ApplicationScene currentScene = windows_currentScene.get(window);
        Stack<ControllerBase> loadedController = windows_currentSceneControllers.get(window);         
        Stage stage = this.windowsStage.get(window);

        //verifico se devo caricare una scena diversa da quella attuale
        //if(currentScene == sceneName)
        //    return null;

        if(loadedController.size() == 0) {
            Pair<Scene,FXMLLoader> result = setStageScene(stage, SceneElemets.BASE_CONTAINER.file);
            Scene scene = result.getValue0();
            FXMLLoader loader = result.getValue1();
            loadedController.push((ControllerBase)loader.getController());
        }
        
        switch(window)
        {
            case EMOTIONALSONGS_WINDOW -> {
                switch(sceneName) 
                {
                    case ACCESS_PAGE:
                        while(loadedController.size() > 1) loadedController.pop();
                        loadedController.push((ControllerBase)injectScene(SceneElemets.ACCESS.file, loadedController.peek().anchor_for_injectScene));
                        stage.setMinWidth(800);
                        stage.setMinHeight(800);
                        break;

                    case REGISTRATION_PAGE:
                        while(loadedController.size() > 1) loadedController.pop();
                        loadedController.push((ControllerBase)injectScene(SceneElemets.REGISTRATION.file, loadedController.peek().anchor_for_injectScene));
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
                switch(sceneName) 
                {
                    case CREATE_PLAYLIST:
                        stage.setMinWidth(512);
                        stage.setMinHeight(350);

                        while(loadedController.size() > 1) loadedController.pop();
                        loadedController.push((ControllerBase)injectScene(SceneElemets.PLAYLIST_CREATOR.file, loadedController.peek().anchor_for_injectScene));                     
                        break;
                    
                    default:
                        throw new RuntimeException("Invalid window scene");   
                }
            }

            default -> {
                throw new RuntimeException("Invalid window");
            }
        }

        //==================================== verfica passaggio args... ====================================//
        //verifico se ho dei parametri da passare al controller
        if(args.length >= 0 ) {
            //verifico se implementa l'interfaccia
            // Inheritance testing:
            Class<?> interfaceType = Injectable.class;
            //Class<?> classType = SomeClass.class;

            if (interfaceType.isAssignableFrom(windows_currentSceneControllers.get(window).peek().getClass())) {
                Injectable controller = (Injectable)windows_currentSceneControllers.get(window).peek();
                
                switch (sceneName) {
                    case MAIN_PAGE_SHOW_PLAYLIST:   controller.injectData(ElementDisplayerMode.SHOW_PLAYLIST,args); break;
                    case MAIN_PAGE_SHOW_ALBUM:      controller.injectData(ElementDisplayerMode.SHOW_ALBUM,args); break;
                    case MAIN_PAGE_SHOW_SONG:       controller.injectData(ElementDisplayerMode.SHOW_SONG,args); break;
                    case MAIN_PAGE_PLAYLIST:        System.out.println("hereeee"); controller.injectData(ElementDisplayerMode.SHOW_USER_PLAYLISTS,args); break;
                
                    default:
                        controller.injectData(args);
                        break;
                }
            }
            else {
                //se ho dei parametri che non posso passare
                throw new UnsupportedOperationException("this controller not implements: " + Injectable.class.getName());
            }
        }

        windows_currentScene.put(window, sceneName);
        windows_currentArgs.put(window, args);

        return windows_currentSceneControllers.get(window).peek();
    }
}
