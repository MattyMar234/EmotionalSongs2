package utility;

import java.util.Properties;

public final class UtilityOS
{
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String fileSeparator = System.getProperty("file.separator");

    public static void printSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ":" + v));
    }

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

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    public static String getSeparator() {
        return fileSeparator;
    }
}
