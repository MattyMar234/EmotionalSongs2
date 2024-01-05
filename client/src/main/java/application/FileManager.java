package application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import utility.UtilityOS;

/**
 * Questa classe gestisce tutti i file necessari al funzionamento dell'applicazione.
 */
public class FileManager 
{
    // public static final String FXML_folder_path = UtilityOS.formatPath(Main.ApplicationDirectory + (Main.jarExecution ? "\\application" : "\\src\\main\\resources\\application")); //main\\resources\\pages-fxml
    // public static final String CSS_file_folder = UtilityOS.formatPath(Main.ApplicationDirectory + (Main.jarExecution ? "\\css" : "\\src\\main\\resources\\application")); //main\\resources\\pages-fxml
    // public static final String LocationsPath = UtilityOS.formatPath(Main.ApplicationDirectory + (Main.jarExecution ? "\\data\\comuni.json" : "\\src\\main\\resources\\application\\data\\comuni.json"));
    // public static final String ImageFolder = UtilityOS.formatPath(Main.ApplicationDirectory + (Main.jarExecution ? "\\image": "\\src\\main\\resources\\application\\image"));
    // public static final String flagsFolder = UtilityOS.formatPath(Main.ApplicationDirectory + (Main.jarExecution ? "\\image\\flags": "\\src\\main\\resources\\application\\image\\flags"));
    // public static final String emotionFolder = UtilityOS.formatPath(Main.ApplicationDirectory + (Main.jarExecution ? "\\image\\emotion_icon": "\\src\\main\\resources\\application\\image\\emotion_icon"));
    private static final String jarDataFolderName = "ApplicationData";
    private static final String JarDataFolder = UtilityOS.formatPath(Main.jarExecution ? System.getProperty("user.dir") + "\\" + jarDataFolderName + "\\" : "");

    public static final String FXML_folder_path = UtilityOS.formatPath((Main.jarExecution ? System.getProperty("user.dir") + "\\" + jarDataFolderName + "\\" : "")); //main\\resources\\pages-fxml
    public static final String CSS_file_folder = UtilityOS.formatPath((Main.jarExecution ? System.getProperty("user.dir") + "\\" + jarDataFolderName + "\\" : "") +"css"); //main\\resources\\pages-fxml
    public static final String DataFolderPath = UtilityOS.formatPath((Main.jarExecution ? System.getProperty("user.dir") + "\\" + jarDataFolderName + "\\" : "") +"data");
    public static final String IconFolder = UtilityOS.formatPath((Main.jarExecution ? System.getProperty("user.dir") + "\\" + jarDataFolderName + "\\" : "") +"image\\icon");
    public static final String FlagsFolder = UtilityOS.formatPath((Main.jarExecution ? System.getProperty("user.dir") + "\\" + jarDataFolderName + "\\" : "") +"image\\flags");
    public static final String EmotionFolder = UtilityOS.formatPath((Main.jarExecution ? System.getProperty("user.dir") + "\\" + jarDataFolderName + "\\" : "") +"image\\emotion_icon");
    public static final String coloredIconFolder = UtilityOS.formatPath((Main.jarExecution ? System.getProperty("user.dir") + "\\" + jarDataFolderName + "\\" : "") +"image\\colored_icon");

    private static final boolean DEBUG = true;

    private static final String[] folders = {
        FXML_folder_path,
        CSS_file_folder,
        DataFolderPath,
        IconFolder,
        FlagsFolder,
        EmotionFolder,
        coloredIconFolder
    };
    
    private static FileManager instance = null;

    /**
     * Questa classe enum rappresenta le varie categorie di file che possiede l'appicazione
     */
    public enum FileType {
        ICON,
        FLAG,
        EMOTION,
        FXML,
        COLORED_ICON,
        JSON,
        CSS;
    }

    /**
     * Costruttore della classe
     */
    private FileManager() {

        if(Main.jarExecution)
        {
            File jarDAtaFOlder = new File(UtilityOS.formatPath(JarDataFolder));
            if(!jarDAtaFOlder.exists()) {
                System.out.println("Missing folder " +  jarDAtaFOlder.getAbsolutePath());
                System.exit(0);
            }  
            
            for (String string : folders) {
                File folder = new File(UtilityOS.formatPath(string));
                if(!folder.exists()) {
                    System.out.println("Missing folder " +  folder.getAbsolutePath());
                    System.exit(0);
                }     
            }
        }
    }

    /**
     * Funzione per il patternSingleton
     * @return
     */
    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    /**
     * Funzione di classe utilizzata per formattare il percorso dei file
     * @param path
     * @return
     */
    private String formatResoursePath(String path) {
        return path.replace("\\", "/");
    }


    
    /**
     * QUesta fuznione permette il caricamnto dei file
     * @param fileName il nome del file da caricare
     * @param type la categoria del file da caricare
     * @return il file caricato altrimenti "null"
     * @throws IOException
     */
    public File loadFile(String fileName, FileType type) throws IOException 
    {
       
        File file = null; 

        try {
            if(Main.jarExecution)
            {
                switch (type) 
                {
                    case CSS:
                        break;

                    case EMOTION:
                        System.out.println("File requested: " + UtilityOS.formatPath(FlagsFolder + "\\" + fileName));
                        file = new File(UtilityOS.formatPath(EmotionFolder + "\\" + fileName));
                        break;

                    case COLORED_ICON:
                        System.out.println("File requested: " + UtilityOS.formatPath(coloredIconFolder + "\\" + fileName));
                        file = new File(UtilityOS.formatPath(coloredIconFolder + "\\" + fileName));
                        break;
                        
                    case FLAG:
                        System.out.println("File requested: " + UtilityOS.formatPath(FlagsFolder + "\\" + fileName));
                        file = new File(UtilityOS.formatPath(FlagsFolder + "\\" + fileName));
                        break;

                    case FXML:
                        if(Main.jarExecution)
                            file = new File(UtilityOS.formatPath(FXML_folder_path + "\\" + fileName));
                        break;
                    
                    case ICON:
                        System.out.println("File requested: " + UtilityOS.formatPath(FlagsFolder + "\\" + fileName));
                        file = new File(UtilityOS.formatPath(IconFolder + "\\" + fileName));
                        break;
                    
                    case JSON:
                        System.out.println("File requested: " + UtilityOS.formatPath(DataFolderPath + "\\" + fileName));
                        file = new File(UtilityOS.formatPath(DataFolderPath + "\\" + fileName));
                        break;
                    
                    default:
                        break;   
                }
            }
            else 
            {
                switch (type) 
                {
                    case CSS:
                        break;

                    case EMOTION:
                        System.out.println("File requested: " + UtilityOS.formatPath(FlagsFolder + "\\" + fileName));
                        file = new File(getClass().getResource(formatResoursePath(EmotionFolder + "\\" + fileName)).toURI());
                        break;

                    case COLORED_ICON:
                        System.out.println("File requested: " + UtilityOS.formatPath(coloredIconFolder + "\\" + fileName));
                        file = new File(getClass().getResource(formatResoursePath(coloredIconFolder + "\\" + fileName)).toURI());
                        break;
                        
                    case FLAG:
                        System.out.println("File requested: " + UtilityOS.formatPath(FlagsFolder + "\\" + fileName));
                        file = new File(getClass().getResource(formatResoursePath(FlagsFolder + "\\" + fileName)).toURI());
                        break;

                    case FXML:
                        file = new File(getClass().getResource(fileName).toURI());
                        break;
                    
                    case ICON:
                        System.out.println("File requested: " + UtilityOS.formatPath(FlagsFolder + "\\" + fileName));
                        file = new File(getClass().getResource(formatResoursePath(IconFolder + "\\" + fileName)).toURI());
                        break;
                    
                    case JSON:
                        System.out.println("File requested: " + UtilityOS.formatPath(DataFolderPath + "\\" + fileName));
                        file = new File(getClass().getResource(formatResoursePath(DataFolderPath + "\\" + fileName)).toURI());
                        break;
                    
                    default:
                        break;   
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        finally {
            if(DEBUG && file != null) {
                System.out.println("FILE PATH: " + file.getAbsolutePath());
            }
        }

        return file;
    }


    

    




}
