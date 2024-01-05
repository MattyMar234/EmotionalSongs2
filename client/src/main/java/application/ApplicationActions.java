package application;

import java.util.Stack;
import objects.SceneAction;

/**
 * Questa classe gestisce le azioni (cambi di scene) che possono essere eseguite dall'applicazione.
 * Quinid permette di andare aventi e indietro tra le scene
 */
public class ApplicationActions 
{
    private static final int MAX_SIZE = 32;
    private static final boolean LOG = false;

    private SceneManager sceneManager;
    private Stack<SceneAction> backward_queue = new Stack<SceneAction>();
    private Stack<SceneAction> forward_queue = new Stack<SceneAction>();
    private SceneAction actualAction = null;

 
    /**
     * Costrutore
     */
    public ApplicationActions() {
        forward_queue.clear();
        backward_queue.clear();

        if(LOG)dump();  
        //sceneManager = SceneManager.instance();
    }

    /**
     * Funzione utilizzata a fini di debug, per vedere lo stato delle code
     */
    private void dump(){
        System.out.println("forwad size: " + forward_queue.size());
        System.out.println("backwad size: " + backward_queue.size());
    }

    /**
     * Restituisce la scene precedentemente
     * @return la scene precedente, null se non c'è nessuna precedente
     */
    public SceneAction previeusScene() {
        if(backward_queue.isEmpty())
            return null;
        return backward_queue.peek();
    }

    /**
     * Restituisce la scene successiva
     * @return la scene successiva, null se non c'è nessuna successiva
     */
    public SceneAction nextScene() {
        if(forward_queue.isEmpty())
            return null;
        return forward_queue.peek();
    }


    /**
     * Aggiunge una alla coda
     * @param action
     */
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

    /**
     * Fa l'aggiornamento della scena
     */
    public void refresh()
    {
        if(actualAction == null)
            return;
        
        SceneManager.instance().showScene(actualAction);
    }

    /**
     * Esegue l'operazione di "torna indietro"
     */
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

    /**
     * Esegue l'operazione di "torna avanti"
     */
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

    /**
     * @return true se è possibile fare l'operazione di "torna indietro"
    */
    public boolean undoAvailable() { 
        return !backward_queue.isEmpty();
    }

    /**
     * @return true se è possibile fare l'operazione di "torna avanti"
    */
    public boolean redoAvailable() {
        return !forward_queue.isEmpty();
    }   
}
