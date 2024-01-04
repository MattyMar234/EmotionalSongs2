package objects;

import java.io.Serializable;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

/**
 * La classe `Emotion` rappresenta un'emozione associata a una canzone e un account utente.
 */

public class Emotion implements Serializable 
{
    private static final long serialVersionUID = 1L;
    
    private String ID;
    private String emotionType;
    private int emotionValue;
    private String emotionDate;
    private String comment;

    private String ID_Song;
    private String ID_Account;


    public Emotion(HashMap<Colonne, Object> table) {
        
        this.ID = (String) table.get(Colonne.ID);
        this.emotionType = (String) table.get(Colonne.TYPE);
        //this.releaseDate = ((java.sql.Date) table.get(Colonne.RELEASE_DATE)).toString();
        //this.spotifyURL = (String) table.get(Colonne.URL);
        this.emotionValue = (int) table.get(Colonne.VALUE);
        this.comment = (String) table.get(Colonne.COMMENTO);
        
        this.ID_Song = (String) table.get(Colonne.SONG_ID_REF);  
        this.ID_Account = (String) table.get(Colonne.ACCOUNT_ID_REF);  
    }

    

    

}
