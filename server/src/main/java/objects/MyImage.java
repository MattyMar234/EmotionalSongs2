package objects;

import java.io.Serializable;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

/**
 * La classe `MyImage` rappresenta un'immagine con le relative informazioni.
 */

public class MyImage implements Serializable 
{
    private static final long serialVersionUID = 1L;

    private String size;
    private String id;
    private String url;


    public MyImage(String size, String id, String url) {
        this.size = size;
        this.id = id;
        this.url = url;
    }

    public MyImage(HashMap<Colonne, Object> table) {
        this.size = (String) table.get(Colonne.IMAGE_SIZE);
        this.id = (String) table.get(Colonne.ID);
        this.url = (String)table.get(Colonne.URL);
     
    }



/**
 * Restituisce la dimensione dell'immagine.
 *
 * Questo metodo restituisce la dimensione dell'immagine, che è stata precedentemente impostata.
 *
 * @return La dimensione dell'immagine.
 */
    public String getSize() {
        return size;
    }



/**
 * Restituisce l'ID dell'immagine.
 *
 * Questo metodo restituisce l'ID dell'immagine, che è stato precedentemente impostato.
 *
 * @return L'ID dell'immagine.
 */
    public String getId() {
        return id;
    }



/**
 * Restituisce l'URL dell'immagine.
 *
 * Questo metodo restituisce l'URL dell'immagine, che è stato precedentemente impostato.
 *
 * @return L'URL dell'immagine.
 */
    public String getUrl() {
        return url;
    }

    

    
    
}
