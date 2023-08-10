package objects;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

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

    // Costruttore
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

    public void addImages(ArrayList<MyImage> imgs) {
        for (MyImage myImage : imgs) {
            images.put(myImage.getSize(), myImage);
        }
    }

    

    // Metodi getter e setter per gli attributi della canzone
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getAlbumId() {
        return albumId;
    }

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
