package database;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import database.PredefinedSQLCode.Colonne;
import database.PredefinedSQLCode.Tabelle;
import objects.Account;
import objects.Album;
import objects.MyImage;
import objects.Residenze;
import objects.Song;
import server.Terminal;

public class QueriesManager {

    public static String generate_ID_from_Time() {
        return Long.toHexString(new Date().getTime()).toUpperCase();
	}

    /**
     * Mi restituisce un hashMap contenete tutte le infromazioni per costruire un oggeto.
     * Nota: devono essere presenti tutti i campi della tabella.
     * Nota: la query deve essere stata fatta solo su una tabella.
     * @param resultSet
     * @param tabella
     * @return
     * @throws SQLException
     */
    private static HashMap<Colonne, Object> getHashMap_for_ClassConstructor(ResultSet resultSet, Tabelle tabella) throws SQLException {
        Colonne[] coll = PredefinedSQLCode.tablesAttributes.get(tabella);
        HashMap<Colonne, Object> table = new HashMap<>();
        

        for (Colonne colonna : coll) {
            table.put(colonna, resultSet.getObject(colonna.getName()));
        }
        return table;
    }

    private static HashMap<Tabelle,HashMap<Colonne, Object>> getHashMaps_for_ClassConstructor(ResultSet resultSet, Tabelle... tabelle) throws SQLException 
    {
        HashMap<Tabelle,HashMap<Colonne, Object>> constructorsMap = new HashMap<>();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        
        int offset = 1;

        for (Tabelle tabella : tabelle) {
            Colonne[] coll = PredefinedSQLCode.tablesAttributes.get(tabella);
            HashMap<Colonne, Object> table = new HashMap<>();
            
            //fra tutte le colonne che compongono il mio oggetto
            for(int i = 0; i < coll.length;i++) 
            {
                //guardo che nome ha la colonna i-esima
                String currentColumName = rsmd.getColumnLabel(offset).toLowerCase();
                
                //cerco se quella colonna mi serve
                for (Colonne colonna : coll) {
                    if(colonna.getName().toLowerCase().equals(currentColumName)) {
                        //System.out.println(tabella + ": " + colonna + " -> " + resultSet.getObject(offset));
                        table.put(colonna, resultSet.getObject(offset++));
                        break;
                    }
                }
            }
            constructorsMap.put(tabella, table);
        }
        return constructorsMap;
    }


    
    

    public static Account getAccountByEmail(String Email) throws SQLException {
        DatabaseManager database = DatabaseManager.getInstance();
        ResultSet resultSet = database.submitQuery(QueryBuilder.getAccountByEmail_query(Email));

        System.out.println(resultSet.getFetchSize());
    
        if (resultSet.next()) { 

            HashMap<Tabelle,HashMap<Colonne, Object>> data = getHashMaps_for_ClassConstructor(resultSet, Tabelle.ACCOUNT, Tabelle.RESIDENZA);
            
            Residenze residenze = new Residenze(data.get(Tabelle.RESIDENZA));
            Account account = new Account(data.get(Tabelle.ACCOUNT), residenze);
            
            return account;    
        } 
        else {
            Terminal.getInstance().printQueryln("nessun elemento trovato");
            return null;
        }
    }

    public static Account getAccountByNickname(String nickname) throws SQLException {
        DatabaseManager database = DatabaseManager.getInstance();
        ResultSet resultSet = database.submitQuery(QueryBuilder.getAccountByNickname_query(nickname));
        
        if (resultSet.next()) { 
            HashMap<Tabelle,HashMap<Colonne, Object>> data = getHashMaps_for_ClassConstructor(resultSet, Tabelle.ACCOUNT, Tabelle.RESIDENZA);
            Residenze residenze = new Residenze(data.get(Tabelle.RESIDENZA));
            Account account = new Account(data.get(Tabelle.ACCOUNT), residenze);
            
            return account;    
        } 
        else {
            return null;
        }
    }

    public static ArrayList<MyImage> getAlbumImages_by_ID(String ID) throws SQLException {

        ArrayList<MyImage> result = new ArrayList<MyImage>();
        DatabaseManager database = DatabaseManager.getInstance();

        ResultSet resultSet = database.submitQuery(QueryBuilder.getAlbumImages_by_ID(ID));

        while (resultSet.next()) { 
            result.add(new MyImage(getHashMap_for_ClassConstructor(resultSet, Tabelle.ALBUM_IMAGES)));
        }

        return result; 
    }


    public static void addAccount_and_addResidence(HashMap<Colonne, Object> colonne_account, HashMap<Colonne, Object> colonne_residenza) throws SQLException {

        DatabaseManager database = DatabaseManager.getInstance();
        String query = QueryBuilder.getResidenceId_Query((String)colonne_residenza.get(Colonne.VIA_PIAZZA), (int)colonne_residenza.get(Colonne.CIVIC_NUMER), (String)colonne_residenza.get(Colonne.COUNCIL_NAME), (String)colonne_residenza.get(Colonne.PROVINCE_NAME));
        String residence_id = "";

        ResultSet resultSet = database.submitQuery(query);

        if(resultSet.next() == true) {
            residence_id = resultSet.getString(Colonne.ID.getName());
            resultSet.close();
        }
        else  {
           resultSet.close(); 
           residence_id = generate_ID_from_Time();
           colonne_residenza.put(Colonne.ID, residence_id);
           database.submitInsertQuery(QueryBuilder.insert_query_creator(Tabelle.RESIDENZA, colonne_residenza));
        }

        colonne_account.put(Colonne.RESIDENCE_ID_REF, residence_id);
        database.submitInsertQuery(QueryBuilder.insert_query_creator(Tabelle.ACCOUNT, colonne_account));
    }



    public static synchronized ArrayList<Song> getTopPopularSongs(long limit, long offset) throws SQLException {

        /*
        * SELECT * FROM Canzone c JOIN immaginialbums on immaginialbums.id = c.id_album  
            WHERE immaginialbums.image_size = '300x300' ORDER BY c.popularity DESC limit 10;
        * 
        */
        
        ArrayList<Song> result = new ArrayList<Song>();
        DatabaseManager database = DatabaseManager.getInstance();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Canzone c ORDER BY c.popularity DESC ");
        sb.append("LIMIT " + limit + " OFFSET " + offset + ";");


        ResultSet resultSet = database.submitQuery(sb.toString());

        while (resultSet.next()) { 
            Song song = new Song(getHashMap_for_ClassConstructor(resultSet, Tabelle.SONG));
            result.add(song);    
        }

        resultSet.close();

        for (Song song : result) {
            song.addImages(getAlbumImages_by_ID(song.getAlbumId()));
        }


        return result; 
    }



    public static synchronized ArrayList<Album> getRecentPublischedAlbum(long limit, long offset, int threshold) throws SQLException 
    {
        DatabaseManager database = DatabaseManager.getInstance();
        ArrayList<Album> result = new ArrayList<Album>();
        Semaphore MUTEX = new Semaphore(0);
        ArrayList<Thread> threads = new ArrayList<Thread>();

        //per ogni album ottenuto, creo un thread e lo eseguo
        /*while (resultSet.next()) { 
            Thread t = new Thread(() -> {
                try {
                    //creo un album
                    Album album = new Album(getHashMap_for_ClassConstructor(resultSet, Tabelle.ALBUM));
                    
                    System.out.println(album);

                    
                    album.addImages(getAlbumImages_by_ID(album.getID()));

                    //creo ed eseguo la query per ottenere tutte le canzoni dell'album
                    String albums_song_query = QueryBuilder.getSongs_by_AlbumID_query(resultSet.getString(Colonne.ID.getName()));
                    ResultSet songResultSet = database.submitQuery2(albums_song_query);

                    //per tutte le canzoni che ho ottenuto, predo l'ID e aggiungo alla lista di canzoni dell'album
                    while(songResultSet.next()) {
                        String song_id = songResultSet.getString(Colonne.ID.getName());
                        album.addSongID(song_id);
                    }

                    //aggiungo il dato alla raccolta
                    MUTEX.tryAcquire();
                    result.add(album);
                    MUTEX.release();
                } 
                catch (SQLException e) {
                    Terminal.getInstance().printQuery_ln("error: " + Terminal.Color.RED_BOLD_BRIGHT + e.getMessage() + Terminal.Color.RESET);
                }

            });
            threads.add(t);
            t.start();    
        }

        //aspetto che tutti i thread siano terminati
        for (Thread t : threads) {
            try {t.join();} catch (InterruptedException e) {}
        }
        */

        String query = QueryBuilder.getRecentPublischedAlbum_query(limit, offset, threshold);
        
        
            ResultSet resultSet = database.submitQuery(query);
            while (resultSet.next()) { 
                
                //creo un album
                Album album = new Album(getHashMap_for_ClassConstructor(resultSet, Tabelle.ALBUM));
                result.add(album);
                //System.out.println(album);
            } 
            resultSet.close();
        
            
        for (Album album : result) {
            album.addImages(getAlbumImages_by_ID(album.getID()));

            //creo ed eseguo la query per ottenere tutte le canzoni dell'album
            /*
            String albums_song_query = QueryBuilder.getSongs_by_AlbumID_query(album.getID());
            ResultSet songResultSet = database.submitQuery(albums_song_query);
            
            while(songResultSet.next()) {
                String song_id = songResultSet.getString(Colonne.ID.getName());
                album.addSongID(song_id);
            }
            songResultSet.close();
            */
            
        }      


        return result;
    }
}
