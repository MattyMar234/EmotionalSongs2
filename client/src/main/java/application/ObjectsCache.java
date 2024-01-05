package application;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import enumClasses.ServerServicesName;
import javafx.scene.image.Image;
import objects.*;

/**
 * Questa classe rappresenta una specie di contenitore di Cache software
 */
public class ObjectsCache 
{
    private static final boolean DEBUG = false;
    
    /**
     * Classe enum che serve per indicare le varie tipologie di cache
     */
    public enum CacheObjectType 
    {
        //cache per le immagini scaricate
        IMAGE(200, javafx.scene.image.Image.class), 
        
        //cache per gli oggetti "Song" creati
        SONG(1000, Song.class), 
        
        //cache per gli oggetti "playlist" creati
        PLAYLIST(32, Playlist.class),

        //cache per gli oggetti "album" creati               
        ALBUM(250, Album.class),
        
        //cache per gli oggetti "artist" creati
        ARTIST(250, Artist.class),

        //cache per tenere traccia di eventuali risultati
        QUERY(40, ServerServicesName.class);

        //grandezza della cache
        private int cacheSize;  
        
        //tipo di dato ammesso
        private Class<?> classType;

        private CacheObjectType(int size, Class<?> classtype) {
            this.cacheSize = size;
            this.classType = classtype;
        }

        /**
         * Restituisce la grandezza della cache.
         * @return
         */
        protected int getSize() {
            return this.cacheSize;
        }

        /**
         * Restituisce il tipo di dato ammesso.
         * @return
         */
        protected Class<?> getClassType() {
            return this.classType;
        }
    }
    
    /**
     * Classe che rappresenta una cache software.
     */
    private class Cache 
    {
        private int maxElement;
        private HashMap<String, Object> table = new HashMap<>();    //tabella dove salvo i dati
        private Queue<String> removableKey_queue = new LinkedList<>();           //per tenere traccia dell'odine di inseriemneto e degli elementi che posso eliminare
        

        public Cache(int maxElement) {
            this.maxElement = maxElement;
        }

        /**
         * Funzione per cancellare la cache.
         * @return
         */
        public boolean clearCache() {
            table.clear();
            removableKey_queue.clear();
            return true;
        }

        /**
         * Funzione per aggiungere un elemento nella cache
         * @param key
         * @param value
         * @return
         */
        public synchronized boolean addItem(String key, Object value, boolean keepInMemory) 
        {
            //se l'elemento deve essere "statico", non lo salvo nella coda
            if(!keepInMemory)
                //aggiungo la chieve nella coda
                removableKey_queue.add(key);             
            
            //salvo i dati nella tabella
            table.put(key, value);      
            
            //Se ho raggiunto il numero massimo di elementi che posso avere,
            //rimuovo l'ultimo elemento che ho inserito
            if(removableKey_queue.size() > this.maxElement) {
                table.remove(removableKey_queue.peek());
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
     * Funzione per eliminare i dati di tutte le cache
     * @return
     */
    public boolean clearAllCache() {
        for (CacheObjectType cahceType : CacheObjectType.values()) {
            boolean result = this.Cache_HashMap.get(cahceType).clearCache();

            if(!result) {
                return false;
            }
        }
        return true;
    }

    /*public boolean setKeepInMemory(CacheObjectType cacheType, String key, Object object, boolean keepInMemory) {

    }*/


    /**
     * Funzione per aggiungere un oggetto nella cache con selezione automatica
     * @param key La chiave di quell'oggetto
     * @param object L'oggetto da inserire
     * @return viene restituito "True" se l'operazione va a buon fine
     */
    public boolean addItem(String key, Object object, boolean keepInMemory) {
        for (CacheObjectType cahceType : CacheObjectType.values()) {
            if(object.getClass() == cahceType.getClassType()) 
            {
                if(DEBUG)
                    System.out.println("Object added on type:" + cahceType);
                return this.Cache_HashMap.get(cahceType).addItem(key, object, keepInMemory);
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
    public boolean addItem(CacheObjectType cacheType, String key, Object object, boolean keepInMemory) 
    {
        //ignoro il tipo di dato se devo salvare il risultato di una query
        if(cacheType == CacheObjectType.QUERY) {
            if(DEBUG)
                System.out.println("Object added on type:" + cacheType);
            return this.Cache_HashMap.get(cacheType).addItem(key, object, keepInMemory);
        }

        //se il tipo di dato passa non combacia con il tipo i dato che richiede la cache
        if(cacheType.getClassType() != object.getClass()) {
            throw new RuntimeException("Tipo di dato non ammesso pr le cache.\nTipo di dato: " + object.getClass());
        }
        
        if(DEBUG)
            System.out.println("Object added on type:" + cacheType);
        return this.Cache_HashMap.get(cacheType).addItem(key, object, keepInMemory);
    
    }
}

    

