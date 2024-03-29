package objects;

import java.io.Serializable;

/**
 * Questa classe rappresenta un pacchetto contenete tutte le informazioni del servizio da rchiedere al server
 */
public class Packet implements Serializable 
{
    private static final long serialVersionUID = 1L;
    private String id;
    private String command;
    private Object[] parameters;


    public Packet(String id, String command, Object[] parameters) {
        this.id = id;
        this.command = command;
        this.parameters = parameters;
    }

    public String getId() {
        return id;
    } 

    public String getServiceCommand() {
        return command;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
