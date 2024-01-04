package objects;

import java.io.Serializable;

/**
 * La classe `Packet` rappresenta un pacchetto di dati utilizzato per comunicare comandi e parametri tra le componenti del sistema.
 */

public class Packet implements Serializable 
{
    private static final long serialVersionUID = 1L;

    public String id;
    public String command;
    public Object[] parameters;

}
