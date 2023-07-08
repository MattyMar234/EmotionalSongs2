package utility;

public class WaithingAnimationThread extends Thread
{
    private final String[] animation = {"\b|", "\b/", "\b/", "\b-", "\b\\", "\b\\"};
    private int step = 0;
    private boolean pause = false;
    private String clearingString = "";
    private String text = "";
    private boolean end = false;
    private boolean inPause = false;

    public WaithingAnimationThread(String text) {
        this.text = (text.endsWith("...") ? text : text + "...");
        
        for (int i = 0; i < (int)text.length() + 1; i++)
            clearingString += "\b";
        for (int i = 0; i < (int)text.length() + 1; i++)
            clearingString += " ";
        for (int i = 0; i < (int)text.length() + 1; i++)
            clearingString += "\b";    
    }
    
    public void run() 
    {
        while(true) {
            try {Thread.sleep(95);}catch (InterruptedException e) {}

            if(end) {
                clear();
                return;
            }
            print();
        }
    }
    private synchronized void print() {

        if(pause) {
            while(pause) {
                clear();
                this.inPause = true;
                try {wait();} catch (InterruptedException e) {}
                this.inPause = false;
            }
        } else {

            clear();
            System.out.print(text + animation[(step = (++step % 6))]);
            System.out.flush();
        }
    }

    public synchronized boolean isInPause() {
        //System.out.println(this.inPause);
        return this.inPause;
    }

    public synchronized void clear() {
        System.out.print(clearingString);
        System.out.flush();
    }

    public synchronized void pause() {
        pause = true;
    }

    public synchronized void restart() {
        pause = false;
        notify();
    } 

    public synchronized void terminate() {
        end = true;
    }
}
    

