package objects;

import java.io.Serializable;

public class MyImage implements Serializable 
{
    public enum ImageSize {
        S300x300("300x300"),
        S640x640("640x640"),
        S64x64("64x64");

        private String imgSize;

        private ImageSize(String size) {
            this.imgSize = size;
        }

        public String getImgSize() {
            return imgSize;
        }

    }
    
    
    
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
