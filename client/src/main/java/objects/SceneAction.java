package objects;

import application.SceneManager.SceneName;

public class SceneAction 
{
    public SceneName scenaName;
    public Object[] args;

    public SceneAction(SceneName scenaName, Object[] args) {
        this.scenaName = scenaName;
        this.args = args;
    }
}
