package utility;

import java.io.File;
import java.util.Queue;

public class FileElementCounter extends Thread {

    private static long sharedCounter = 0;
    private File folder;
    private FileCounterInterface function;
    private Queue<File> queue;

    public FileElementCounter(File folder, Queue<File> queue, FileCounterInterface function) {
        this.folder = folder;
        this.function = function;
        this.queue = queue;
        start();
    }

    public void run() {
        
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) { 
               add(function.getFileElementsCount(file.getAbsolutePath()), file, queue);
               //System.out.println(file.getName() + " " + sharedCounter);
            }
        }
    }


    public static synchronized void resetCounter() {
        sharedCounter = 0;
    }

    public static synchronized long getCounterValue() {
        return sharedCounter;
    }

    private static synchronized void add(long n, File f, Queue<File> queue) {
        sharedCounter += n;
        queue.add(f);
    }
    
}
