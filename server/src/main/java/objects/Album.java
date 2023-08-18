package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private String name;
    private String releaseDate;
    private String spotifyURL;
    private String type;
    private int element;
    private String artistID;
    private List<Song> canzoni;

    public Album(String ID, String name, String releaseDate, String spotifyURL, String type, int element, String artistID) {
        this.ID = ID;
        this.name = name;
        this.releaseDate = releaseDate;
        this.spotifyURL = spotifyURL;
        this.type = type;
        this.element = element;
        this.artistID = artistID;
        this.canzoni = new ArrayList<>();
    }
    
}
