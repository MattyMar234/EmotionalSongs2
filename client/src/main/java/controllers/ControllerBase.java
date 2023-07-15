package controllers;

import java.io.IOException;

import application.EmotionalSongs;
import javafx.scene.layout.BorderPane;
import java.util.HashMap;

public abstract class ControllerBase {

    private EmotionalSongs MainClassReference;
    private HashMap<Object, String[]> windowObjectsTexts = new HashMap<Object, String[]>();
    private HashMap<Object, String[]> ObjectsErrorVisualization = new HashMap<Object, String[]>();




    public ControllerBase() {
        this.MainClassReference = EmotionalSongs.getInstance();
    }

    public void setApplicationPage(String sceneName, BorderPane anchor) {
        try {
            MainClassReference.SetScene(sceneName, anchor);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    protected void addObjectText_Translations(Object object, String[] texts) {
        windowObjectsTexts.put(object, texts);
    }

    protected void setTextsLanguage() {

        for (Object object : windowObjectsTexts.keySet()) {

            if(object instanceof javafx.scene.control.Label)
                ((javafx.scene.control.Label) object).setText(windowObjectsTexts.get(object)[EmotionalSongs.applicationLanguage]);
            
            else if(object instanceof javafx.scene.control.Button)
                ((javafx.scene.control.Button) object).setText(windowObjectsTexts.get(object)[EmotionalSongs.applicationLanguage]);   
        }
    }


    protected void clearErrors() {
        for (Object object : ObjectsErrorVisualization.keySet()) {
            if(object instanceof javafx.scene.control.Label)
                ((javafx.scene.control.Label) object).setVisible(false);
        }
        
    } 

    
}


