package application;
import java.lang.reflect.ParameterizedType;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Super classe utilizzata per rappresentare le funzioni di base che deve possedere ogni finestra
 */
public abstract class WindowBase<Type> {
    
    public static final SceneManager sceneManager = SceneManager.instance();
    public static final ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
    public Stage root;
    
    

    public abstract void closeWindow();

}
