package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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

    private ArrayList<String> songID_list = new ArrayList<>();
    private HashMap <String, MyImage> images = new HashMap <String, MyImage>();


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

    public ArrayList<String> getSongsID() {
        return songID_list;
    }

    public MyImage getImage(MyImage.ImageSize size) {
        return images.get(size.getImgSize());
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
