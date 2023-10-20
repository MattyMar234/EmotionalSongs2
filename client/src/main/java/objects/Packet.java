package objects;

import java.io.Serializable;

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

    public String getCommand() {
        return command;
    }
}
