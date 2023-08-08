package applicationEvents;

import javafx.event.Event;
import javafx.event.EventType;

public class ConnectionEvent extends Event {
    private static final long serialVersionUID = 1L;

    public static final EventType<ConnectionEvent> CONNECTED = new EventType<ConnectionEvent>(Event.ANY, "CONNECTED");
    public static final EventType<ConnectionEvent> DISCONNECTED = new EventType<ConnectionEvent>(Event.ANY, "DISCONNECTED");


    public ConnectionEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

   
    
}
