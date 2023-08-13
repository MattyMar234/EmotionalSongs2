package objects;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import application.SceneManager;
import application.SceneManager.SceneName;

public class UserActions {

    private SceneManager sceneManager = SceneManager.getInstance();
    private Stack<SceneAction> backward_queue = new Stack<SceneAction>();
    private Stack<SceneAction> forward_queue = new Stack<SceneAction>();


    public UserActions() {
        forward_queue.clear();
        backward_queue.clear();
        System.out.println("forwad size: " + forward_queue.size());
        System.out.println("backwad size: " + backward_queue.size());
    }

    public void addAction(SceneAction action) {

        //aggiungo un'operazione che posso fare all'indietro
        backward_queue.push(action);

        //rimuovo le operazione che posso fare in avanti
        forward_queue.clear();

        System.out.println("forwad size: " + forward_queue.size());
        System.out.println("backwad size: " + backward_queue.size());
    }

    public void undo() {
        if (!backward_queue.isEmpty()) {
            SceneAction action = backward_queue.pop();
            forward_queue.push(action);
            sceneManager.showScene(action);

            System.out.println("forwad size: " + forward_queue.size());
            System.out.println("backwad size: " + backward_queue.size());
        }
    }

    public void redo() {
        if (!forward_queue.isEmpty()) {
            SceneAction action = forward_queue.pop();
            backward_queue.push(action);
            sceneManager.showScene(action);

            System.out.println("forwad size: " + forward_queue.size());
            System.out.println("backwad size: " + backward_queue.size());
        }
    }

    public boolean undoAvailable() { 
        return !backward_queue.isEmpty();
    }

    public boolean redoAvailable() {
        return !forward_queue.isEmpty();
    }   
}
