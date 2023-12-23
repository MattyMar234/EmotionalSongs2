package application;

import java.util.Stack;
import objects.SceneAction;

public class ApplicationActions 
{
    private static final int MAX_SIZE = 32;
    private static final boolean LOG = false;

    private SceneManager sceneManager;
    private Stack<SceneAction> backward_queue = new Stack<SceneAction>();
    private Stack<SceneAction> forward_queue = new Stack<SceneAction>();
    private SceneAction actualAction = null;

 
    public ApplicationActions() {
        forward_queue.clear();
        backward_queue.clear();

        if(LOG)dump();  
        //sceneManager = SceneManager.instance();
    }

    
    private void dump(){
        System.out.println("forwad size: " + forward_queue.size());
        System.out.println("backwad size: " + backward_queue.size());
    }



    public void addAction(SceneAction action) 
    {
        if(actualAction == null) {
            actualAction = action;
        }
        else {
            //aggiungo un'operazione che posso fare all'indietro
            backward_queue.push(actualAction);

            //se la coda è troppo grande, rimuovo l'elemento in fondo alla stack 
            if(backward_queue.size() > MAX_SIZE) {
                backward_queue.remove(0);
            }
            
            actualAction = action;

            if(LOG) {
                System.out.println("actual action: " + actualAction.scena_name.name());
            }

            //rimuovo le operazione che posso fare in avanti
            forward_queue.clear();

            if(LOG)dump();
        }
        
    }

    public void refresh()
    {
        if(actualAction == null)
            return;
        
        SceneManager.instance().showScene(actualAction);
    }

    public void undo() 
    {
        if(actualAction == null || backward_queue.isEmpty())
            return;

        //salvo l'azione
        forward_queue.push(actualAction);

        //reperisco l'azione precedente
        actualAction = backward_queue.pop();
        SceneManager.instance().showScene(actualAction);

        if(LOG)dump();
    }

    public void redo() {

        if(actualAction == null || forward_queue.isEmpty())
            return;

        //salvo l'azione
        backward_queue.push(actualAction);

        //reperisco l'azione precedente
        actualAction = forward_queue.pop();
        SceneManager.instance().showScene(actualAction);
       
        if(LOG)dump();
        
    }

    public boolean undoAvailable() { 
        return !backward_queue.isEmpty();
    }

    public boolean redoAvailable() {
        return !forward_queue.isEmpty();
    }   
}
