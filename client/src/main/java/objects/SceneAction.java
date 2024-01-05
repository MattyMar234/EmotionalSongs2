package objects;

import application.SceneManager.ApplicationScene;

/**
 * Questa classe contiene tutte le informazioni sulla scene appena caricata
 */
public class SceneAction 
{
    public ApplicationScene scena_name;
    public Object[] args;

    public SceneAction(ApplicationScene scenaName, Object[] args) {
        this.scena_name = scenaName;
        this.args = args;
    }
}
