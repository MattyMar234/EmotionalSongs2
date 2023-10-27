package objects;

import application.SceneManager.ApplicationScene;

public class SceneAction 
{
    public ApplicationScene scena_name;
    public Object[] args;

    public SceneAction(ApplicationScene scenaName, Object[] args) {
        this.scena_name = scenaName;
        this.args = args;
    }
}
