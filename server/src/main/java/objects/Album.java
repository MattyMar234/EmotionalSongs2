package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import database.PredefinedSQLCode.Colonne;

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



/**
 * Aggiunge una lista di immagini all'oggetto.
 *
 * Questo metodo aggiunge una lista di immagini all'oggetto, utilizzando la dimensione
 * dell'immagine come chiave nell'HashMap delle immagini.
 *
 * @param imgs La lista di immagini da aggiungere.
 */
    public void addImages(ArrayList<MyImage> imgs) {
        for (MyImage myImage : imgs) {
            images.put(myImage.getSize(), myImage);
        }
    }



/**
 * Aggiunge un ID di canzone alla lista degli ID delle canzoni.
 *
 * @param id L'ID della canzone da aggiungere.
 */
    public void addSongID(String id) {
        songID_list.add(id);
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
