package objects;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

/**
 * La classe `Song` rappresenta una canzone con le relative informazioni.
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

    
    public Song(String id, String title, String spotifyUrl, long durationMs, int popularity, String albumId) {
        this.id = id;
        this.title = title;
        this.spotifyUrl = spotifyUrl;
        this.durationMs = durationMs;
        this.popularity = popularity;
        this.albumId = albumId;
    }

    public Song(HashMap<Colonne, Object> table) {
        this.id = (String) table.get(Colonne.ID);
        this.title = (String) table.get(Colonne.TITLE);
        this.spotifyUrl = (String)table.get(Colonne.URL);
        this.durationMs = (long)table.get(Colonne.DURATION);
        this.popularity = (int)table.get(Colonne.POPULARITY);
        this.albumId = (String)table.get(Colonne.ALBUM_ID_REF);   
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
 * Restituisce l'ID dell'oggetto.
 *
 * Questo metodo restituisce l'ID dell'oggetto, che è stato precedentemente impostato.
 *
 * @return L'ID dell'oggetto.
 */
    public String getId() {
        return id;
    }



/**
 * Imposta l'ID dell'oggetto.
 *
 * Questo metodo imposta l'ID dell'oggetto utilizzando la stringa fornita.
 *
 * @param id La nuova stringa da assegnare all'ID dell'oggetto.
 */
    public void setId(String id) {
        this.id = id;
    }



/**
 * Restituisce il titolo dell'oggetto.
 *
 * Questo metodo restituisce il titolo dell'oggetto, che è stato precedentemente impostato.
 *
 * @return Il titolo dell'oggetto.
 */
    public String getTitle() {
        return title;
    }



/**
 * Imposta il titolo dell'oggetto.
 *
 * Questo metodo imposta il titolo dell'oggetto utilizzando la stringa fornita.
 *
 * @param title Il nuovo titolo da assegnare all'oggetto.
 */
    public void setTitle(String title) {
        this.title = title;
    }



/**
 * Restituisce l'URL di Spotify associato all'oggetto.
 *
 * Questo metodo restituisce l'URL di Spotify associato all'oggetto, che è stato precedentemente impostato.
 *
 * @return L'URL di Spotify associato all'oggetto.
 */
    public String getSpotifyUrl() {
        return spotifyUrl;
    }



/**
 * Imposta l'URL di Spotify associato all'oggetto.
 *
 * Questo metodo imposta l'URL di Spotify associato all'oggetto utilizzando la stringa fornita.
 *
 * @param spotifyUrl Il nuovo URL di Spotify da assegnare all'oggetto.
 */
    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }



/**
 * Restituisce la durata in millisecondi dell'oggetto.
 *
 * Questo metodo restituisce la durata in millisecondi dell'oggetto, che è stata precedentemente impostata.
 *
 * @return La durata in millisecondi dell'oggetto.
 */
    public long getDurationMs() {
        return durationMs;
    }



/**
 * Imposta la durata in millisecondi dell'oggetto.
 *
 * Questo metodo imposta la durata in millisecondi dell'oggetto utilizzando il valore fornito.
 *
 * @param durationMs La nuova durata in millisecondi da assegnare all'oggetto.
 */
    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }



/**
 * Restituisce la popolarità dell'oggetto.
 *
 * Questo metodo restituisce la popolarità dell'oggetto, che è stata precedentemente impostata.
 *
 * @return La popolarità dell'oggetto.
 */
    public int getPopularity() {
        return popularity;
    }



/**
 * Imposta la popolarità dell'oggetto.
 *
 * Questo metodo imposta la popolarità dell'oggetto utilizzando il valore fornito.
 *
 * @param popularity La nuova popolarità da assegnare all'oggetto.
 */
    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }



/**
 * Restituisce l'ID dell'album associato all'oggetto.
 *
 * Questo metodo restituisce l'ID dell'album associato all'oggetto, che è stato precedentemente impostato.
 *
 * @return L'ID dell'album associato all'oggetto.
 */
    public String getAlbumId() {
        return albumId;
    }



/**
 * Imposta l'ID dell'album associato all'oggetto.
 *
 * Questo metodo imposta l'ID dell'album associato all'oggetto utilizzando la stringa fornita.
 *
 * @param albumId Il nuovo ID dell'album da assegnare all'oggetto.
 */
    public void setAlbumId(String albumId) {
        this.albumId = albumId;
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
