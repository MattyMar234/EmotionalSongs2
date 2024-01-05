package applicationEvents;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Questa classe rappresenta un evento di cambio scena.
 */
public class SceneChangeEvent extends Event {
    private static final long serialVersionUID = 1L;

    public static final EventType<ConnectionEvent> SCENE_CHANGED = new EventType<ConnectionEvent>(Event.ANY, "CONNECTED");

    public SceneChangeEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
    
}
