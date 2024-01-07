package utility;

/**
 * Questa classe rappresenta un thread generico che esegue un'interfaccia di funzione specificata.
 */
public class GenericThread extends Thread {

    private GenericThreadInterface function;
    private Object[] args;

    public GenericThread(GenericThreadInterface function, Object... args) {
        super();
        this.function = function;
        this.args = args;
        start();
    }

    /**
     * Esegue la funzione specificata nell'interfaccia {@code GenericThreadInterface} con gli argomenti forniti.
     */
    public void run() {
        function.execute(args);
    }
    
}
