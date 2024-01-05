package objects;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Questa classe modella una playlist
 */
public class Playlist implements Serializable
{    
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String data;
    private String userID;
    private String[] songsID;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Playlist)
            return this.id.equals(((Playlist)obj).getId());
        return false;
    }
    
    /**
     * Questa funzione restituisce l'id della playlist
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Questa funzione restituisce il nome della playlist.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Questa funzione restituisce la data di creazione della playlist.
     * @return
     */
    public String getData() {
        return data;
    }

    /**
     * Questa funzione restituisce l'ID dell'utente che crea la playlist.
     * @return
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Questa funzione restituisce l'ID delle canzoni presenti nella playlist.
     * @return
     */
    public String[] getSongsID() {
        return songsID;
    }
}
