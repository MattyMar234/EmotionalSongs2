package utility;

import java.io.File;
import java.util.Properties;

import application.Main;
import javafx.scene.image.Image;

/**
 * Questa funzione di utilità fornisce informazioni sul sistema operativo e permette formattare i percorsi.
 */
public final class UtilityOS
{
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String fileSeparator = System.getProperty("file.separator");

    public static void printSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    /**
     * Questa fuzione permette di formattare i percorsi in base al sistema operativo.
     * @param path
     * @return
     */
    public static String formatPath(String path)
    {
        if (isWindows()) {
            return path.replace("/", fileSeparator);
        }
        else if (isUnix() || isMac()) {
            // if(!isUnix())
            //     return path.replace("\\", fileSeparator);

            path = path.replace("\\", fileSeparator);

            // if(path.startsWith("/home/")) {
            //     String[] paths = path.split("/");

            //     for(int i = 2; i < paths.length; i++) {
            //         path += "/" + paths[i];
            //     }
            // }
            return path;
        }
        else {
            System.out.println("Your OS is not support!!");
            return path;
        }
    }

    /**
     * Questa funzione permette di verificare se il sistema operativo è Windows.
     * @return
     */
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    /**
     * Questa funzione permette di verificare se il sistema operativo è Mac.
     * @return
     */
    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    /**
     * Questa funzione permette di verificare se il sistema operativo è Unix.
     * @return
     */
    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    /**
     * Questa funzione permette di ottenere il separatore di file.
     * @return
     */
    public static String getSeparator() {
        return fileSeparator;
    }

    /**
     * Questa funzione permette di ottenere l'immagine in base al percorso.
     * @param path
     * @return
     */
    public static Image getImage(String path) 
    {
        if(UtilityOS.isUnix() || UtilityOS.isMac())
            return new Image(new File(UtilityOS.formatPath(path)).toURI().toString());
        else
            return new Image(UtilityOS.formatPath(path));
    }
}

