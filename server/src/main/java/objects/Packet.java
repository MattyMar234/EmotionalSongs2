package objects;

import java.io.Serializable;

public class Packet implements Serializable 
{
    private static final long serialVersionUID = 1L;

    public String id;
    public String command;
    public Object[] parameters;

}
