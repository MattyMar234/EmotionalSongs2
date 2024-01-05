package enumClasses;

import java.io.File;
import java.io.IOException;

import application.FileManager;
import application.Main;
import application.FileManager.FileType;
import javafx.scene.image.Image;

/**
 * Questa classe rappresenta i tipi di emozioni
 */
public enum EmotionType {
    
    AMAZEMENT(1,    "#800080"), 
    SOLEMNITY(2,    "#228B22"),
    TENDERNESS(3,   "#FFB6C1"),
    NOSTALGIA(4,    "#704214"),
    CALMNESS(5,     "#E6E6FA"),
    POWER(6,        "#FFA500"),
    JOY(7,          "#FFFF00"),
    TENSION(8,      "#58423F"),
    SADNESS(9,      "#4682B4");

    private int index;
    private Image image;
    private String color;

    private EmotionType(int index, String styleColor) 
    {
        //File directory = new File(Main.emotionFolder);
        this.color = styleColor;
        this.index = index;

        FileManager fileManager = FileManager.getInstance();
        try {
            File[] emotionImageFile_list = {
                fileManager.loadFile("Amazement[1].png", FileType.EMOTION),
                fileManager.loadFile("Solemnity[2].png", FileType.EMOTION),
                fileManager.loadFile("Tenderness[3].png", FileType.EMOTION),
                fileManager.loadFile("Nostalgia[4].png", FileType.EMOTION),
                fileManager.loadFile("Calmness[5].png", FileType.EMOTION),
                fileManager.loadFile("Power[6].png", FileType.EMOTION),
                fileManager.loadFile("Joy[7].png", FileType.EMOTION),
                fileManager.loadFile("Tension[8].png", FileType.EMOTION),
                fileManager.loadFile("Sadness[9].png", FileType.EMOTION)
            };

            boolean found = false;

            for (File file : emotionImageFile_list) {
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
        catch (IOException e){
            e.printStackTrace();
        }

        // Verifica se il percorso è una cartella
        // if (!directory.isDirectory()) {
        //     System.out.println("Impossibile reperire le immagini delle emozioni.\nPercorso non valido");
        //     System.exit(0);
        // }

        // Ottieni un array di oggetti File che rappresentano i file nella cartella
        //File[] files = directory.listFiles();

        // Verifica se l'array non è nullo e contiene elementi
        // if (files == null) {
        //     System.out.println("non sono presenti le immagini delle emozioni");
        //     System.exit(0);
        // }
    }

    /**
     * Restituisce il valore del colore in formato HEX
     * @return
     */
    public String getColorHexValue() {
        return color;
    }

    /**
     * Restituisce l'immagine dell'emozione
     * @return
     */
    public Image getEmotionImage() {
        return new Image(this.image.getUrl());
    }

    /**
     * 
     * @param semicolum
     * @return -fx-fill: color
     */
    public String getStyleColor(boolean semicolum) {
        return "-fx-fill: " + color + (semicolum ? ";" : "");
    }

    /**
     * @param semicolum
     * @return -fx-pie-color: color
     */
    public String getPieColor(boolean semicolum) {
        return "-fx-pie-color: " + color + (semicolum ? ";" : "");
    }

    public int getIndex() {
        return index;
    }

    /**
     * Restituisce il nome dell'emozione e non il tipo
     * @return
     */
    public String getName() {
        switch (this) {
            case AMAZEMENT:
                return (Main.applicationLanguage == 0 ? "Stupore" : "Amazement");

            case SOLEMNITY:
                return (Main.applicationLanguage == 0 ? "Solennit\u00E0" : "Solemnity");
                
            case TENDERNESS:
                return (Main.applicationLanguage == 0 ? "Tenerezza" : "Tenderness");
                
            case NOSTALGIA:
                return (Main.applicationLanguage == 0 ? "Nostalgia" : "Nostalgia");
                
            case CALMNESS:
                return (Main.applicationLanguage == 0 ? "Calma" : "Calmness");
                
            case POWER:
                return (Main.applicationLanguage == 0 ? "Forza" : "Power");
                
            case JOY:
                return (Main.applicationLanguage == 0 ? "Gioia" : "Joy");
                
            case TENSION:
                return (Main.applicationLanguage == 0 ? "Tensione" : "Tension");
                
            case SADNESS:
                return (Main.applicationLanguage == 0 ? "Tristezza" : "Sadness");
                
            default:
                return "";
        }
    }
}
