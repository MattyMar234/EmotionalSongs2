package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

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

    public Artist(HashMap<Colonne, Object> table) {
        this.id = (String) table.get(Colonne.ID);
        this.name = (String) table.get(Colonne.NAME);
        this.spotifyURL = (String) table.get(Colonne.URL);
        this.followers = (long) table.get(Colonne.FOLLOWERS);
        this.popularity = (int) table.get(Colonne.POPULARITY);
    }



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
            //System.out.println("Added image: " + myImage.getSize());
        }
    }

}
