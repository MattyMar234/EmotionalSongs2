package objects;

import application.SceneManager.SceneName;

public class SceneAction 
{
    public SceneName scena_name;
    public Object[] args;

    public SceneAction(SceneName scenaName, Object[] args) {
        this.scena_name = scenaName;
        this.args = args;
    }
}
