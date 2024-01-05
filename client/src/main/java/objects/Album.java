package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * La classe `Album` rappresenta un album musicale con le relative informazioni.
 */
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

    /**
 * Converte la lista degli ID delle canzoni in una stringa formattata.
 *
 * Questo metodo converte la lista degli ID delle canzoni in una stringa formattata
 * del tipo "[ID1, ID2, ...]".
 *
 * @return Una stringa formattata contenente gli ID delle canzoni.
 */
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

    /**
 * Restituisce l'ID dell'oggetto.
 *
 * @return L'ID dell'oggetto.
 */
    public String getID() {
        return ID;
    }

    /**
 * Restituisce il nome dell'oggetto.
 *
 * @return Il nome dell'oggetto.
 */
    public String getName() {
        return name;
    }

    /**
 * Restituisce la data di rilascio dell'oggetto.
 *
 * @return La data di rilascio dell'oggetto.
 */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Restituisce l'URL di Spotify associato all'oggetto.
     *
     * @return L'URL di Spotify associato all'oggetto.
     */
    public String getSpotifyURL() {
        return spotifyURL;
    }

    /**
 * Restituisce il tipo dell'oggetto.
 *
 * @return Il tipo dell'oggetto.
 */
    public String getType() {
        return type;
    }

    /**
     * Restituisce l'elemento associato all'oggetto.
     *
     * @return L'elemento associato all'oggetto.
     */
    public int getElement() {
        return element;
    }

    /**
     * Restituisce l'ID dell'artista associato all'oggetto.
     *
     * @return L'ID dell'artista associato all'oggetto.
     */
    public String getArtistID() {
        return artistID;
    }

    
    
}
