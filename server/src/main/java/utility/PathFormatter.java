package utility;

import java.util.Locale;


public final class PathFormatter 
{
    private static String OS = System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT);
    private static String pathSeparator = System.getProperty("path.separator");

    public static String formatPath(String path) 
    {
        //System.out.println(OS);
        
        if (OS.contains("win")) {
            return path.replace("/", "\\");
        }
        else if (OS.contains("nux")) {
            return path.replace("\\", "/");
        }
        else if (OS.contains("mac")) {
            return path.replace("\\", "/");
        }
        else {
            return path;
        }
    } 

    public static String getPathSeparator() {
        if (OS.contains("win")) {
            return "\\";
        }
        else if (OS.contains("nux")) {
            return "/";
        }
        else if (OS.contains("mac")) {
            return "/";
        }
        else {
            return null;
        }
    }
}
