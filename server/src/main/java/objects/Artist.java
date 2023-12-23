package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

public class Artist implements Serializable 
{
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String spotifyURL;
    private long followers;
    private int popularity;

    private HashMap <String, MyImage> images = new HashMap <String, MyImage>();

    public Artist(HashMap<Colonne, Object> table) {
        this.id = (String) table.get(Colonne.ID);
        this.name = (String) table.get(Colonne.NAME);
        this.spotifyURL = (String) table.get(Colonne.URL);
        this.followers = (long) table.get(Colonne.FOLLOWERS);
        this.popularity = (int) table.get(Colonne.POPULARITY);
    }


    public String getID() {
        return id;
    }

    public void addImages(ArrayList<MyImage> imgs) {
        for (MyImage myImage : imgs) {
            images.put(myImage.getSize(), myImage);
            System.out.println("Added image: " + myImage.getSize());
        }
    }

}
