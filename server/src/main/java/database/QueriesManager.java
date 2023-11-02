package database;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.apache.commons.codec.digest.DigestUtils;

import database.PredefinedSQLCode.Colonne;
import database.PredefinedSQLCode.Tabelle;
import objects.Account;
import objects.Album;
import objects.MyImage;
import objects.Playlist;
import objects.Residenze;
import objects.Song;
import server.Terminal;

public class QueriesManager 
{
    private static DatabaseManager database = DatabaseManager.getInstance();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");



    private static String generate_ID_from_Time() {
        return DigestUtils.sha256Hex(Long.toHexString(new Date().getTime()).toUpperCase());
	}

    private static String getCurrentDate() {
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
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


    private static ArrayList<Song> buildSongObjects_From_resultSet(ResultSet resultSet, boolean autoClose) throws SQLException 
    {
        ArrayList<Song> result = new ArrayList<Song>();

        while (resultSet.next()) { 
            Song song = new Song(getHashMap_for_ClassConstructor(resultSet, Tabelle.SONG));
            song.addImages(getAlbumImages_by_ID(song.getAlbumId()));
            result.add(song);    
        }

        if(autoClose) {
            resultSet.close();
        }

        return result;
    }
    

    public static Account getAccountByEmail(String Email) throws SQLException {
        ResultSet resultSet = database.submitQuery(QueryBuilder.getAccountByEmail_query(Email));

        System.out.println(resultSet.getFetchSize());
    
        if (resultSet.next()) { 

            HashMap<Tabelle,HashMap<Colonne, Object>> data = getHashMaps_for_ClassConstructor(resultSet, Tabelle.ACCOUNT, Tabelle.RESIDENZA);
            
            Residenze residenze = new Residenze(data.get(Tabelle.RESIDENZA));
            Account account = new Account(data.get(Tabelle.ACCOUNT), residenze);
            
            return account;    
        } 
        else {
            //Terminal.getInstance().printQueryln("nessun elemento trovato");
            return null;
        }
    }

    public static Account getAccountByNickname(String nickname) throws SQLException {
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

        ResultSet resultSet = database.submitQuery(QueryBuilder.getAlbumImages_by_ID(ID));

        while (resultSet.next()) { 
            result.add(new MyImage(getHashMap_for_ClassConstructor(resultSet, Tabelle.ALBUM_IMAGES)));
        }

        return result; 
    }


    public static void addAccount_and_addResidence(HashMap<Colonne, Object> colonne_account, HashMap<Colonne, Object> colonne_residenza) throws SQLException {

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



    public static ArrayList<Song> getTopPopularSongs(long limit, long offset) throws SQLException {

        /*
        * SELECT * FROM Canzone c JOIN immaginialbums on immaginialbums.id = c.id_album  
            WHERE immaginialbums.image_size = '300x300' ORDER BY c.popularity DESC limit 10;
        * 
        */
        
        ArrayList<Song> result = new ArrayList<Song>();

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



    public static ArrayList<Album> getRecentPublischedAlbum(long limit, long offset, int threshold) throws SQLException 
    {
        ArrayList<Album> result = new ArrayList<Album>();
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

    /**
     * Cerca tutte le canzoni che contengono nel titolo la parola passata come parametro
     * @param search la parola da cercare nel titolo delle canzoni
     * @param limit numero di record massimi che si vuole avere come risultato
     * @param offset numero di record da saltare
     * @return una lista di Song che contengono nel titolo la parola passata come parametro
     * @throws SQLException
     */
    public static ArrayList<Song> searchSong(String search, long limit, long offset) throws SQLException {
        ArrayList<Song> result = new ArrayList<Song>();

        String query = QueryBuilder.getSongSearch_query(search, limit, offset);
        ResultSet resultSet = database.submitQuery(query);

        while (resultSet.next()) { 
            Song song = new Song(getHashMap_for_ClassConstructor(resultSet, Tabelle.SONG));
            song.addImages(getAlbumImages_by_ID(song.getAlbumId()));
            result.add(song);    
        }

        resultSet.close();

        /*for (Song song : result) {
            song.addImages(getAlbumImages_by_ID(song.getAlbumId()));
        }*/

        return result; 
    }

    public static ArrayList<Song> searchSongByIDs(String[] IDs) throws SQLException {
        String query = QueryBuilder.getSongByID_query(IDs);
        return buildSongObjects_From_resultSet(database.submitQuery(query), true);
    }

    public static ArrayList<Song> getAlbumSongs(String albumID) throws SQLException {
        String query = QueryBuilder.getAlbumSongs_query(albumID);
        return buildSongObjects_From_resultSet(database.submitQuery(query), true);
    }

    public static ArrayList<Album> searchAlbum(String search, long limit, long offset) throws SQLException {
        ArrayList<Album> result = new ArrayList<Album>();

        String query = QueryBuilder.getAlbumSearch_query(search, limit, offset);
        ResultSet resultSet = database.submitQuery(query);

        while (resultSet.next()) { 
            Album album = new Album(getHashMap_for_ClassConstructor(resultSet, Tabelle.ALBUM));
            album.addImages(getAlbumImages_by_ID(album.getID()));
            result.add(album);    
        }

        resultSet.close();

        /*for (Album album : result) {
            album.addImages(getAlbumImages_by_ID(album.getID()));
        }*/


        return result; 
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OPERAZIONI SULLE PLAYLIST
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Operazione per creare una muova playlist
     * @param accountID
     * @param playlistName
     * @throws SQLException
     */
    public static void addPlaylist(String accountID, String playlistName) throws SQLException  {
        String query = QueryBuilder.addPlaylist_query(accountID, playlistName, getCurrentDate(), generate_ID_from_Time());
        database.submitInsertQuery(query);
    }

    /**
     * Operazione per ottenere tutte le canzoni di una playlist
     * @param playlistID
     * @return array di stringhe contenenti gli ID delle canzoni
     * @throws SQLException
     */
    public static String[] getPlaylistSongsID(String playlistID) throws SQLException {
        String query = QueryBuilder.getPlaylistSongsID_query(playlistID);
        ResultSet resultSet = database.submitQuery(query);
        String[] output = new String[resultSet.getFetchSize()];
        int offset = 0;

        while (resultSet.next()) { 
            output[offset++] = resultSet.getString(Colonne.ID.getName());  
        }

        return output;
    }

    
    /**
     * Operazione per ottenere tutte le playlist di un account
     * @param accountID
     * @return
     * @throws SQLException
     */
    public static Object getAccountsPlaylists(String accountID) throws SQLException {
        String query = QueryBuilder.getAccountsPlaylists_query(accountID);
        ResultSet resultSet = database.submitQuery(query);
        ArrayList<Playlist> list = new ArrayList<Playlist>();

        while (resultSet.next()) { 
            Playlist playlist = new Playlist(getHashMap_for_ClassConstructor(resultSet, Tabelle.PLAYLIST));
            playlist.setSongsID(getPlaylistSongsID(playlist.getId()));
            list.add(playlist); 
        }

        return list;
    }

    public static void addSongToPlaylist(String accountID, String playlistID, String songID) throws SQLException {
        String query = QueryBuilder.addSongToPlaylist_query(playlistID, songID);
        database.submitInsertQuery(query);
    }

    public static void removeSongFromPlaylist (String accountID, String playlistID, String songID) throws SQLException {
        String query = QueryBuilder.removeSongFromPlaylist_query(playlistID, songID);
        database.submitQuery(query);
    }

    public static void renamePlaylist(String accountID, String playlistID, String newName) throws SQLException {
        String query = QueryBuilder.renamePlaylist_query(playlistID, newName);
        database.submitQuery(query);
    }

    public static void addComment(String accountID, String songID, String comment) throws SQLException {
        String query = QueryBuilder.addComment_query(accountID, songID, comment);
        database.submitInsertQuery(query);
    }

    public static void deleteComment(String accountID, String commentID) throws SQLException {
        String query = QueryBuilder.deleteComment_query(commentID);
        database.submitQuery(query);
    }

    public static void getAccountSongComment(String accountID, String songID) throws SQLException{
        String query = QueryBuilder.getAccountSongComment_query(accountID, songID);
        database.submitQuery(query);
    }

    public static void getSongComment(String songID) throws SQLException {
        String query = QueryBuilder.getSongComment_query(songID);
        database.submitQuery(query);
    }
    
    public static void getAccountComment(String accountID) throws SQLException {
        String query = QueryBuilder.getAccountComment_query(accountID);
        database.submitQuery(query);
    }

    public static void getSongEmotion(String songID) throws SQLException {
        String query = QueryBuilder.getSongEmotion_query(songID);
        database.submitQuery(query);
    }


}

