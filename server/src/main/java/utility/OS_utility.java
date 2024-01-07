package utility;

import java.util.Locale;
import java.util.Properties;

/**
 * Classe di utilità per operazioni legate al sistema operativo.
 */
public final class OS_utility 
{
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String fileSeparator = System.getProperty("file.separator");

    /**
     * Stampa tutte le proprietà di sistema.
     */
    public static void printSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    /**
     * Formatta il percorso del file in base al sistema operativo corrente.
     *
     * @param path Il percorso del file da formattare.
     * @return Il percorso del file formattato.
     */
    public static String formatPath(String path)
    {
        if (isWindows()) {
            return path.replace("/", fileSeparator);
        }
        else if (isUnix() || isMac()) {
            return path.replace("\\", fileSeparator);
        }
        else {
            System.out.println("Your OS is not support!!");
            return path;
        }
    }

    /**
     * Verifica se il sistema operativo è Windows.
     *
     * @return True se il sistema operativo è Windows, false altrimenti.
     */
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    /**
     * Verifica se il sistema operativo è macOS.
     *
     * @return True se il sistema operativo è macOS, false altrimenti.
     */
    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    /**
     * Verifica se il sistema operativo è Unix-like.
     *
     * @return True se il sistema operativo è Unix-like, false altrimenti.
     */
    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    /**
     * Restituisce il separatore di file del sistema operativo corrente.
     *
     * @return Il separatore di file del sistema operativo.
     */
    public static String getSeparator() {
        return fileSeparator;
    }
}
