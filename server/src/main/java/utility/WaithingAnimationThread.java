package utility;

public class WaithingAnimationThread extends Thread
{
    public enum Animation {
        SPIN(new String[]{"\b|", "\b/", "\b-", "\b\\"}, 150),
        DOTS(new String[]{".", "..", "...", ""}, 800);

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
    private String clearingString = "";
    private String text = "";
    private boolean end = false;
    private boolean inPause = false;

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
    
    public void run() 
    {
        while(true) {
            try {Thread.sleep(animation.delay);}catch (InterruptedException e) {}

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
            System.out.print(text + animation.frames[(step = (++step % animation.frames.length))]);
            System.out.flush();
        }
    }

    public synchronized boolean isInPause() {
        //System.out.println(this.inPause);
        return this.inPause;
    }

    public synchronized void clear() {
        for(int k = 0; k < 3; k++ ) {
            for (int i = 0; i < (int) text.length() + animation.frames.length; i++)
                System.out.print((k==1) ? " " : "\b"); 
        } 
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
    

