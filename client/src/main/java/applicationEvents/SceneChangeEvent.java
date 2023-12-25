package applicationEvents;

import javafx.event.Event;
import javafx.event.EventType;

public class SceneChangeEvent extends Event {
    private static final long serialVersionUID = 1L;

    public static final EventType<ConnectionEvent> SCENE_CHANGED = new EventType<ConnectionEvent>(Event.ANY, "CONNECTED");

    public SceneChangeEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
    
}
