package utility;

import interfaces.GenericThreadInterface;

public class GenericThread extends Thread {

    private GenericThreadInterface function;
    private Object[] args;

    public GenericThread(GenericThreadInterface function, Object... args) {
        super();
        this.function = function;
        this.args = args;
        start();
    }


    public void run() {
        function.execute(args);
    }
    
}
