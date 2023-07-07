package controllers;

import java.io.IOException;

import application.EmotionalSongs;
import javafx.scene.layout.BorderPane;

public abstract class ControllerBase {

    private EmotionalSongs MainClassReference;

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

}
