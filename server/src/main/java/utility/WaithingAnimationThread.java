package utility;

public class WaithingAnimationThread extends Thread
{
    private String text;

    public WaithingAnimationThread(String text) {
        this.text = text;
    }
    
    public void run() {
        final String str = text + "...|";
        final String[] animation = {"\b|", "\b/", "\b/", "\b-", "\b\\", "\b\\"};
        
        System.out.print(str);
        int step = 0;

        while(true) {
            try {Thread.sleep(95);}catch (InterruptedException e) {for (int i = 0; i < (int)str.length()*1.6; i++)System.out.print("\b");System.out.flush();return;}
            System.out.flush();
            System.out.print(animation[(step = (++step % 6))]);
        }
    }
}
    

