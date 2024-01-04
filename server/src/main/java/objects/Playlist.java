package objects;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

/**
 * La classe `Playlist` rappresenta una playlist di brani musicali associata a un utente.
 */

public class Playlist implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String data;
    private String userID;
    private String[] songsID;
    
    public Playlist(HashMap<Colonne, Object> table) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        this.id = (String) table.get(Colonne.ID);
        this.name = (String) table.get(Colonne.NAME);
        this.data = (String) table.get(Colonne.CREATION_DATE);
        this.userID = (String) table.get(Colonne.ACCOUNT_ID_REF);  
    }

    @Override
    public String toString() {
        return "Playlist{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", data='" + data + '\'' +
        ", userID='" + userID + '\'' +
        ", songsID=" + songsID +
        '}';
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
 * Restituisce il nome dell'oggetto.
 *
 * Questo metodo restituisce il nome dell'oggetto, che è stato precedentemente impostato.
 *
 * @return Il nome dell'oggetto.
 */
    public String getName() {
        return name;
    }



/**
 * Restituisce i dati associati all'oggetto.
 *
 * Questo metodo restituisce i dati associati all'oggetto, che sono stati precedentemente impostati.
 *
 * @return I dati associati all'oggetto.
 */
    public String getData() {
        return data;
    }



/**
 * Restituisce l'ID dell'utente associato all'oggetto.
 *
 * Questo metodo restituisce l'ID dell'utente associato all'oggetto, che è stato precedentemente impostato.
 *
 * @return L'ID dell'utente associato all'oggetto.
 */
    public String getUserID() {
        return userID;
    }



/**
 * Restituisce un array contenente gli ID delle canzoni associate all'oggetto.
 *
 * Questo metodo restituisce un array contenente gli ID delle canzoni associate all'oggetto,
 * che sono stati precedentemente impostati.
 *
 * @return Un array di String contenente gli ID delle canzoni associate all'oggetto.
 */
    public String[] getSongsID() {
        return songsID;
    }



/**
 * Imposta gli ID delle canzoni associate all'oggetto.
 *
 * Questo metodo imposta gli ID delle canzoni associate all'oggetto utilizzando l'array fornito.
 *
 * @param songsID Un array di String contenente gli ID delle canzoni da associare all'oggetto.
 */
    public void setSongsID(String[] songsID) {
        this.songsID = songsID;
    }

    
}
