package enumClasses;

import java.io.File;

import application.Main;
import javafx.scene.image.Image;

public enum EmotionData {
    
    Amazement(1),
    Solemnity(2),
    Tenderness(3),
    Nostalgia(4),
    Calmness(5),
    Power(6),
    joy(7),
    Tension(8),
    Sadness(9);

    private int index;
    private Image image;

    private EmotionData(int index) 
    {
        File directory = new File(Main.emotionFolder);

        // Verifica se il percorso è una cartella
        if (!directory.isDirectory()) {
            System.out.println("Impossibile reperire le immagini delle emozioni.\nPercorso non valido");
            System.exit(0);
        }

        // Ottieni un array di oggetti File che rappresentano i file nella cartella
        File[] files = directory.listFiles();

        // Verifica se l'array non è nullo e contiene elementi
        if (files == null) {
            System.out.println("non sono presenti le immagini delle emozioni");
            System.exit(0);
        }

        boolean found = false;

        for (File file : files) {
            if(file.getName().contains("[" + Integer.toString(index) +"]")) {
                image = new Image(new File(file.getAbsolutePath()).toURI().toString());
                found = true;
                break;
            }
        }

        if(!found) {
            System.out.println("L'immagne dell'emozione con l'Id" + index + " non è stata trovata");
            System.exit(0);
        }
    }

    public Image getEmotionImage() {
        return new Image(this.image.getUrl());
    }

}
