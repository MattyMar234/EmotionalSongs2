package objects;

import java.io.Serializable;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

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


    public String getSize() {
        return size;
    }


    public String getId() {
        return id;
    }


    public String getUrl() {
        return url;
    }

    

    
    
}
