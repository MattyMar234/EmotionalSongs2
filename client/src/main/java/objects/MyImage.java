package objects;

import java.io.Serializable;

/**
 * Questa classe viene utilizzata per rappresentare le immagini con i relativi dati
 */
public class MyImage implements Serializable 
{
    /**
     * Questa classe viene utilizzata per rappresentare i formati dell'immagine
     */
    public enum ImageSize {
        S64x64("64x64"),
        S160x160("160x160"),
        S300x300("300x300"),
        S320x320("300x300"),
        S640x640("640x640");

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


    /**
     * Restituisce la dimensione dell'immagine
     * @return
     */
    public String getSize() {
        return size;
    }

    /**
     * Restituisce l'id dell'immagine
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Restituisce l'url dell'immagine
     * @return
     */
    public String getUrl() {
        return url;
    }

    

    
    
}
