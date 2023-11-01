package objects;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

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
        this.data = (String) dateFormat.format((java.sql.Date)table.get(Colonne.CREATION_DATE));
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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public String getUserID() {
        return userID;
    }

    public String[] getSongsID() {
        return songsID;
    }

    public void setSongsID(String[] songsID) {
        this.songsID = songsID;
    }

    
}
