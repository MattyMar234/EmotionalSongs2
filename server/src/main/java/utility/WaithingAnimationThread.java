package utility;

public class WaithingAnimationThread extends Thread
{
    public enum Animation {
        SPIN(new String[]{"\b|", "\b/", "\b-", "\b\\"}, 150),
        DOTS(new String[]{".", "..", "...", ""}, 700);

        private final String[] frames;
        private int delay;

        private Animation(String[] frames, int delay) {
            this.frames = frames;
            this.delay = delay;
        }
    }
    
    private Animation animation;
    private int step = 0;
    private boolean pause = false;
    private String clearingString1 = "";
    private String clearingString2 = "";
    private String text = "";
    private boolean end = false;
    private boolean inPause = false;
    private Thread me;

    public WaithingAnimationThread(String text) {
        //this.text = (text.endsWith("...") ? text : text + "...");
        this.text = text;
        animation = Animation.SPIN;    
    }

    public WaithingAnimationThread(String text, Animation animation) {
        //this.text = (text.endsWith("...") ? text : text + "...");
        this.text = text;
        this.animation = animation;    
    }

    public WaithingAnimationThread(String text, Animation animation, String operation) {
        //this.text = (text.endsWith("...") ? text : text + "...");
        this.text = text;
        this.animation = animation;    
    }
    
    public void run() 
    {
        me = Thread.currentThread();

        for(int k = 0; k < 2; k++ ) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < (int) text.length() + animation.frames.length; i++)
                sb.append((k==1) ? " " : "\b"); 

            if(k == 0)
                clearingString1 = sb.toString();
            else
                clearingString2 = sb.toString();
        } 

        while(true) {
            try {Thread.sleep(animation.delay);}catch (InterruptedException e) {}

            if(end) {
                clear();
                return;
            }
            print();
        }
    }
    public synchronized void print() {

        if(pause && Thread.currentThread() == me) {
            while(pause) {
                clear();
                this.inPause = true;
                try {wait();} catch (InterruptedException e) {}
                this.inPause = false;
            }
        } else {

            clear();
            System.out.print(text + animation.frames[(step = (++step % animation.frames.length))]);
            System.out.flush();
        }
    }

    public synchronized boolean isInPause() {
        //System.out.println(this.inPause);
        return this.inPause;
    }

    public synchronized void clear() {
        /*for(int k = 0; k < 3; k++ ) {
            for (int i = 0; i < (int) text.length() + animation.frames.length; i++)
                System.out.print((k==1) ? " " : "\b"); 
        }*/
        System.out.print(clearingString1); //\b
        System.out.print(clearingString2); //space
        System.out.print(clearingString1); //\b
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
    

