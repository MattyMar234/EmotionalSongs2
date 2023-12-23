package objects;

import java.io.Serializable;
import java.util.HashMap;


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
    

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public String getUserID() {
        return userID;
    }

    public String[] getSongsID() {
        return songsID;
    }
}
