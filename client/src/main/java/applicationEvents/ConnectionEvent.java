package applicationEvents;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Questa classe rappresenta tutti gli eventi che possono essere generati dalla connessione al server.
 */
public class ConnectionEvent extends Event {
    private static final long serialVersionUID = 1L;

    public static final EventType<ConnectionEvent> CONNECTED = new EventType<ConnectionEvent>(Event.ANY, "CONNECTED");
    public static final EventType<ConnectionEvent> DISCONNECTED = new EventType<ConnectionEvent>(Event.ANY, "DISCONNECTED");
    public static final EventType<ConnectionEvent> SERVER_NOT_FOUND = new EventType<ConnectionEvent>(Event.ANY, "SERVER_NOT_FOUND");


    public ConnectionEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

   
    
}
