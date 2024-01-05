package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * La classe `Artist` rappresenta un artista musicale con le relative informazioni.
 */
public class Artist implements Serializable 
{
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String spotifyURL;
    private long followers;
    private int popularity;

    private HashMap <String, MyImage> images = new HashMap <String, MyImage>();

/**
 * Restituisce l'ID dell'oggetto.
 *
 * Questo metodo restituisce l'ID dell'oggetto, che è stato precedentemente impostato.
 *
 * @return L'ID dell'oggetto.
 */
    public String getID() {
        return id;
    }

    /**
     * Questa funzione restituisce l'immagine dell'artista.
     * @param size il formato dell'immagine
     * @return
     */
    public MyImage getImage(MyImage.ImageSize size) {
        return images.get(size.getImgSize());
    }

    /**
     * Restituisce il nome dell'artista.
     * @return
     */
    public String getName() {
        return name;
    }

    
    /**
     * Restituisce l'URL di Spotify dell'artista.
     * @return
     */
    public String getSpotifyURL() {
        return spotifyURL;
    }

    /**
     * Restituisce il numero di seguitore dell'artista.
     * @return
     */
    public long getFollowers() {
        return followers;
    }

        /**
         * Restituisce la popularità dell'artista.
         * @return
         */
    public int getPopularity() {
        return popularity;
    }
}
