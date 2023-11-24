package application;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import enumClasses.ServerServicesName;
import javafx.scene.image.Image;
import objects.*;

public class ObjectsCache 
{
    public enum CacheObjectType 
    {
        IMAGE(64, String.class),
        SONG(120, Song.class),
        PLAYLIST(32, Playlist.class),
        ALBUM(64, Album.class),
        ARTIST(32, Artist.class),
        QUERY(32, ServerServicesName.class);

        private int cacheSize;
        private Class<?> classType;

        private CacheObjectType(int size, Class<?> classtype) {
            this.cacheSize = size;
            this.classType = classtype;
        }

        protected int getSize() {
            return this.cacheSize;
        }

        protected Class<?> getClassType() {
            return this.classType;
        }
    }
    
    private class Cache 
    {
        private int maxElement;
        private Queue<String> queue = new LinkedList<>();
        private HashMap<String, Object> table = new HashMap<>();

        public Cache(int maxElement) {
            this.maxElement = maxElement;
        }

        public synchronized boolean addItem(String key, Object value) 
        {
            queue.add(key);             //aggiungo la chieve nella coda
            table.put(key, value);      //salvo i dati nella tabella
            
            //Se ho raggiunto il numero massimo di elementi che posso avere,
            //rimuovo l'ultimo elemento che ho inserito
            if(queue.size() > this.maxElement) {
                table.remove(queue.peek());
            }

            //se tutto va a buon fine
            return true;
        }

        public synchronized Object getItem(String key) {
            return table.get(key);
        }
    }

    private static ObjectsCache classInstanceReff = null;
    private HashMap<CacheObjectType, Cache> Cache_HashMap = new HashMap<>();


    /**
     * Singleton method
     * @return
     */
    public static ObjectsCache getInstance(){
        if(ObjectsCache.classInstanceReff == null) {
            classInstanceReff = new ObjectsCache();
        }
        return ObjectsCache.classInstanceReff;
    }

    /**
     * Construttore della classe
     */
    private ObjectsCache() {
        for (CacheObjectType cahceType : CacheObjectType.values()) {
            this.Cache_HashMap.put(cahceType, new Cache(cahceType.getSize()));
        }
    }

    /**
     * Funzione per cercare se un oggetto è presente nella cache
     * @param itemType il typo di oggetto da cercare
     * @param key la chiave di quell'oggetto da cercare
     * @return viene restituito l'oggetto se è presente, altreimenti "null". 
     */
    public Object getItem(CacheObjectType itemType, String key) {
        return this.Cache_HashMap.get(itemType).getItem(key);
    }


    /**
     * Funzione er aggiungere un oggetto nella cache
     * @param key La chiave di quell'oggetto
     * @param object L'oggetto da inserire
     * @return viene restituito "True" se l'operazione va a buon fine
     */
    public boolean addItem(String key, Object object) {
        for (CacheObjectType cahceType : CacheObjectType.values()) {
            if(object.getClass() == cahceType.getClassType()) {
                System.out.println("Object added on type:" + cahceType);
                return this.Cache_HashMap.get(cahceType).addItem(key, object);
            }
        }
        throw new RuntimeException("Tipo di dato non ammesso pr le cache.\nTipo di dato: " + object.getClass());
    }
}

    

