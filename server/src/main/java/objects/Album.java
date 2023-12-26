package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import database.PredefinedSQLCode.Colonne;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private String name;
    private String releaseDate;
    private String spotifyURL;
    private String type;
    private int element;
    private String artistID;

    private List<String> songID_list = new ArrayList<>();
    private HashMap <String, MyImage> images = new HashMap <String, MyImage>();

    public Album(String ID, String name, String releaseDate, String spotifyURL, String type, int element, String artistID) {
        this.ID = ID;
        this.name = name;
        this.releaseDate = releaseDate;
        this.spotifyURL = spotifyURL;
        this.type = type;
        this.element = element;
        this.artistID = artistID;
    }

    public Album(HashMap<Colonne, Object> table) {
        this.ID = (String) table.get(Colonne.ID);
        this.name = (String) table.get(Colonne.NAME);
        this.releaseDate = (String) table.get(Colonne.RELEASE_DATE).toString();
        this.spotifyURL = (String) table.get(Colonne.URL);
        this.type = (String) table.get(Colonne.TYPE);
        this.element = (int) table.get(Colonne.ELEMENT);
        this.artistID = (String) table.get(Colonne.ARTIST_ID_REF);  
    }

    @Override
    public String toString() {
        return "Album{" +
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", spotifyURL='" + spotifyURL + '\'' +
                ", type='" + type + '\'' +
                ", element=" + element +
                ", artistID='" + artistID + '\'' +
                ", songID_list=" + songID_listToString() +
                '}';
    }

    private String songID_listToString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (String songID : songID_list) {
            stringBuilder.append(songID).append(", ");
        }
        if (!songID_list.isEmpty()) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public void addImages(ArrayList<MyImage> imgs) {
        for (MyImage myImage : imgs) {
            images.put(myImage.getSize(), myImage);
        }
    }

    public void addSongID(String id) {
        songID_list.add(id);
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getSpotifyURL() {
        return spotifyURL;
    }

    public String getType() {
        return type;
    }

    public int getElement() {
        return element;
    }

    public String getArtistID() {
        return artistID;
    }

    
    
}
