package application;

import java.util.Stack;
import objects.SceneAction;

public class ApplicationActions {

    private SceneManager sceneManager = SceneManager.getInstance();
    private Stack<SceneAction> backward_queue = new Stack<SceneAction>();
    private Stack<SceneAction> forward_queue = new Stack<SceneAction>();
    private SceneAction actualAction = null;

    private static boolean log = false;



    public ApplicationActions() {
        forward_queue.clear();
        backward_queue.clear();

        if(log)dump();  
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
            
            actualAction = action;

            if(log) {
                System.out.println("actual action: " + actualAction.scena_name.name());
            }

            //rimuovo le operazione che posso fare in avanti
            forward_queue.clear();

            if(log)dump();
        }
        
    }

    public void undo() 
    {
        if(actualAction == null || backward_queue.isEmpty())
            return;

        //salvo l'azione
        forward_queue.push(actualAction);

        //reperisco l'azione precedente
        actualAction = backward_queue.pop();
        sceneManager.showScene(actualAction);

        if(log)dump();
    }

    public void redo() {

        if(actualAction == null || forward_queue.isEmpty())
            return;

        //salvo l'azione
        backward_queue.push(actualAction);

        //reperisco l'azione precedente
        actualAction = forward_queue.pop();
        sceneManager.showScene(actualAction);
       
        if(log)dump();
        
    }

    public boolean undoAvailable() { 
        return !backward_queue.isEmpty();
    }

    public boolean redoAvailable() {
        return !forward_queue.isEmpty();
    }   
}
