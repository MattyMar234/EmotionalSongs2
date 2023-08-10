package objects;

import java.io.Serializable;

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
