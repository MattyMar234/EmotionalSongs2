package objects;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Questa classe viene utilizzata per modella le informazioni di una canzone
 */
public class Song implements Serializable 
{
    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String spotifyUrl;
    private long durationMs;
    private int popularity;
    private String albumId;

    private HashMap <String, MyImage> images = new HashMap <String, MyImage>();
    //private 

    // Costruttore
    public Song(String id, String title, String spotifyUrl, long durationMs, int popularity, String albumId) {
        this.id = id;
        this.title = title;
        this.spotifyUrl = spotifyUrl;
        this.durationMs = durationMs;
        this.popularity = popularity;
        this.albumId = albumId;
    }

    /**
     * Questa funzione resituisce l'immagine della canzone in base alla dimensione richiesta.
     * @param size
     * @return
     */
    public MyImage getImage(MyImage.ImageSize size) {
        return images.get(size.getImgSize());
    }

    /**
     * Restituisce l'id della canzone.
     * @return
     */
    public String getId() {
        return id;
    }

  
    /**
     * Restituisce il titolo della canzone.
     * @return
     */
    public String getTitle() {
        return title;
    }

  
    /**
     * Restituisce l'URL di Spotify della canzone.
     * @return
     */
    public String getSpotifyUrl() {
        return spotifyUrl;
    }

   
    /**
     * Restituisce la durata della canzone in millisecondi.
     * @return
     */
    public long getDurationMs() {
        return durationMs;
    }

  
    /**
     * Restituisce la popolarità della canzone.
     * @return
     */
    public int getPopularity() {
        return popularity;
    }

   
    /**
     * Restituisce l'ID dell'album della canzone.
     * @return
     */
    public String getAlbumId() {
        return albumId;
    }

   

    // Override del metodo toString per visualizzare le informazioni della canzone
    @Override
    public String toString() {
        return "ID: " + id +
               ", Title: " + title +
               ", Spotify URL: " + spotifyUrl +
               ", Duration (ms): " + durationMs +
               ", Popularity: " + popularity +
               ", Album ID: " + albumId;
    }
}
