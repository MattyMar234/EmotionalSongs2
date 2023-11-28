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
    private static final boolean DEBUG = true;
    
    public enum CacheObjectType 
    {
        //cache per le immagini scaricate
        IMAGE(64, javafx.scene.image.Image.class), 
        
        //cache per gli oggetti "Song" creati
        SONG(120, Song.class), 
        
        //cache per gli oggetti "playlist" creati
        PLAYLIST(32, Playlist.class),

        //cache per gli oggetti "album" creati               
        ALBUM(64, Album.class),
        
        //cache per gli oggetti "artist" creati
        ARTIST(32, Artist.class),

        //cache per tenere traccia di eventuali risultati
        QUERY(32, ServerServicesName.class);

        //grandezza della cache
        private int cacheSize;  
        
        //tipo di dato ammesso
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
        private HashMap<String, Object> table = new HashMap<>(); 

        //per tenere traccia dell'odine di inseriemneto 
        private Queue<String> queue = new LinkedList<>();

        public Cache(int maxElement) {
            this.maxElement = maxElement;
        }

        /**
         * Funzione per aggiungere un elemento nella cache
         * @param key
         * @param value
         * @return
         */
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

        /**
         * Funzione per cercare un elemento nella cache
         * @param key
         * @return viene restituito l'oggetto se è presente, altreimenti "null".
         */
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
     * Funzione per aggiungere un oggetto nella cache con selezione automatica
     * @param key La chiave di quell'oggetto
     * @param object L'oggetto da inserire
     * @return viene restituito "True" se l'operazione va a buon fine
     */
    public boolean addItem(String key, Object object) {
        for (CacheObjectType cahceType : CacheObjectType.values()) {
            if(object.getClass() == cahceType.getClassType()) 
            {
                if(DEBUG)
                    System.out.println("Object added on type:" + cahceType);
                return this.Cache_HashMap.get(cahceType).addItem(key, object);
            }
        }
        throw new RuntimeException("Tipo di dato non ammesso per le cache.\nTipo di dato: " + object.getClass());
    }

    /**
     * Funzione per aggiungere un oggetto in una cache specifica
     * @param key La chiave di quell'oggetto
     * @param object L'oggetto da inserire
     * @return viene restituito "True" se l'operazione va a buon fine
     */
    public boolean addItem(CacheObjectType cacheType, String key, Object object) 
    {
        //ignoro il tipo di dato se devo salvare il risultato di una query
        if(cacheType == CacheObjectType.QUERY) {
            if(DEBUG)
                System.out.println("Object added on type:" + cacheType);
            return this.Cache_HashMap.get(cacheType).addItem(key, object);
        }

        //se il tipo di dato passa non combacia con il tipo i dato che richiede la cache
        if(cacheType.getClassType() != object.getClass()) {
            throw new RuntimeException("Tipo di dato non ammesso pr le cache.\nTipo di dato: " + object.getClass());
        }
        
        if(DEBUG)
            System.out.println("Object added on type:" + cacheType);
        return this.Cache_HashMap.get(cacheType).addItem(key, object);
    
    }
}

    

