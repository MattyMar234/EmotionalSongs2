package application;

import java.util.HashMap;
import javafx.scene.image.Image;

public class ObjectsCache {

    private static HashMap<String, Image> imageCache = new HashMap<String, Image>();





    public static Image getImage(String url) 
    {
        if (imageCache.containsKey(url)) {
            return imageCache.get(url);
        } else {
            /*Image image = new Image(imageName);
            imageCache.put(imageName, image);
            return image;*/
            return null;
        }
    
    }

    public static void addImage(String url, Image image) {
        imageCache.put(url, image);
    }

}

    

