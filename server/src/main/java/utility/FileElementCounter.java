package utility;

import java.io.File;
import java.util.Queue;

/**
 * La classe FileElementCounter estende la classe Thread e implementa la logica per contare
 * il numero di elementi nei file con estensione ".json" all'interno di una cartella specificata.
 */
public class FileElementCounter extends Thread {

    private static long sharedCounter = 0;
    private File folder;
    private FileCounterInterface function;
    private Queue<File> queue;



/**
 * Costruisce un oggetto FileElementCounter per contare gli elementi all'interno di una cartella specificata.
 *
 * @param folder   La cartella di cui contare gli elementi.
 * @param queue    La coda in cui inserire i file trovati durante il conteggio.
 * @param function L'interfaccia FileCounterInterface per eseguire operazioni specifiche durante il conteggio.
 */
    public FileElementCounter(File folder, Queue<File> queue, FileCounterInterface function) {
        this.folder = folder;
        this.function = function;
        this.queue = queue;
        start();
    }



/**
 * Esegue il conteggio degli elementi all'interno della cartella specificata.
 * Per ogni file JSON trovato nella cartella, utilizza l'interfaccia FileCounterInterface
 * per ottenere il conteggio degli elementi nel file e lo aggiunge alla coda specificata.
 * 
 * Il conteggio degli elementi è basato sull'implementazione fornita dall'oggetto FileCounterInterface.
 */
    public void run() {
        
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) { 
               add(function.getFileElementsCount(file.getAbsolutePath()), file, queue);
               //System.out.println(file.getName() + " " + sharedCounter);
            }
        }
    }

/**
 * Reimposta il contatore condiviso a zero.
 */
    public static synchronized void resetCounter() {
        sharedCounter = 0;
    }

/**
 * Restituisce il valore corrente del contatore condiviso.
 *
 * @return Il valore corrente del contatore condiviso.
 */
    public static synchronized long getCounterValue() {
        return sharedCounter;
    }


/**
 * Aggiunge il valore specificato al contatore condiviso e aggiunge il file e il suo conteggio
 * alla coda specificata.
 *
 * @param n     Il valore da aggiungere al contatore condiviso.
 * @param f     Il file associato al conteggio.
 * @param queue La coda in cui aggiungere il file.
 */
    private static synchronized void add(long n, File f, Queue<File> queue) {
        sharedCounter += n;
        queue.add(f);
    }
    
}
