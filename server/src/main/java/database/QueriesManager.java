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
import objects.Artist;
import objects.Emotion;
import objects.MyImage;
import objects.Playlist;
import objects.Residenze;
import objects.Song;
import server.Terminal;



/**
 * Questa classe gestisce le query al database e fornisce metodi
 * per recuperare e manipolare dati come oggetti Java.
 */
public class QueriesManager 
{
    private static DatabaseManager database = DatabaseManager.getInstance();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static double generate_ID_from_Time_last_call = System.currentTimeMillis();



/**
 * Genera un ID univoco basato sul timestamp corrente utilizzando l'algoritmo SHA-256.
 *
 * Questo metodo genera un ID univoco utilizzando il timestamp corrente, garantendo che ogni chiamata successiva
 * avrà un timestamp diverso, evitando collisioni. L'ID è ottenuto applicando l'algoritmo di hashing SHA-256
 * alla rappresentazione esadecimale del timestamp corrente convertito in stringa.
 *
 * @return Un ID univoco generato basato sul timestamp corrente.
 */
    public synchronized static String generate_ID_from_Time() {
        while(System.currentTimeMillis() - QueriesManager.generate_ID_from_Time_last_call < 1000);
        generate_ID_from_Time_last_call = System.currentTimeMillis();

        return DigestUtils.sha256Hex(Long.toHexString(new Date().getTime()).toUpperCase());
	}



/**
 * Restituisce la data corrente nel formato specificato.
 *
 * Questo metodo ottiene la data corrente utilizzando l'istanza di Calendar e la converte in una stringa
 * utilizzando l'oggetto SimpleDateFormat con il formato predefinito.
 *
 * @return Una stringa che rappresenta la data corrente nel formato specificato.
 */
    private static String getCurrentDate() {
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }



/**
 * Crea una HashMap di colonne e valori da un ResultSet per il costruttore di una classe.
 *
 * Questo metodo prende un ResultSet e una tabella specificata, e restituisce una HashMap che associa
 * le colonne della tabella ai loro valori corrispondenti nel ResultSet. La HashMap viene utilizzata
 * per il costruttore di una classe rappresentante la tabella.
 *
 * @param resultSet Il ResultSet contenente i dati della query.
 * @param tabella   La tabella specificata per la quale ottenere la HashMap.
 * @return Una HashMap che associa le colonne della tabella ai loro valori corrispondenti nel ResultSet.
 * @throws SQLException Se si verifica un errore durante l'accesso al ResultSet.
 */
    private static HashMap<Colonne, Object> getHashMap_for_ClassConstructor(ResultSet resultSet, Tabelle tabella) throws SQLException {
        Colonne[] coll = PredefinedSQLCode.tablesAttributes.get(tabella);
        HashMap<Colonne, Object> table = new HashMap<>();
        

        for (Colonne colonna : coll) {
            table.put(colonna, resultSet.getObject(colonna.getName()));
        }
        return table;
    }



/**
 * Crea una mappa di costruttori per le classi associate alle tabelle specificate da un ResultSet.
 *
 * Questo metodo prende un ResultSet e un elenco di tabelle specificate, e restituisce una HashMap che associa
 * ogni tabella ai suoi dati corrispondenti nel ResultSet. La struttura della HashMap esterna utilizza le tabelle come chiavi,
 * e ognuna di queste ha una HashMap interna che associa le colonne della tabella ai loro valori corrispondenti nel ResultSet.
 * Questa HashMap viene utilizzata per costruire oggetti delle classi associate alle tabelle.
 *
 * @param resultSet Il ResultSet contenente i dati della query.
 * @param tabelle   Un elenco di tabelle specificate per le quali ottenere le HashMap.
 * @return Una HashMap che associa le tabelle ai loro dati corrispondenti nel ResultSet.
 * @throws SQLException Se si verifica un errore durante l'accesso al ResultSet.
 */
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



/**
 * Costruisce oggetti Song da un ResultSet e restituisce una lista di essi.
 *
 * Questo metodo prende un ResultSet contenente dati relativi alle canzoni, costruisce oggetti Song
 * utilizzando la HashMap associata alla tabella SONG e restituisce una lista di questi oggetti.
 * Opzionalmente, chiude il ResultSet automaticamente se autoClose è impostato su true.
 *
 * @param resultSet Il ResultSet contenente i dati delle canzoni.
 * @param autoClose Indica se chiudere automaticamente il ResultSet dopo la costruzione degli oggetti Song.
 * @return Una lista di oggetti Song costruiti dal ResultSet.
 * @throws SQLException Se si verifica un errore durante l'accesso al ResultSet o la costruzione degli oggetti Song.
 */
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
    


/**
 * Ottiene un Account associato a un'email specifica dalla base di dati.
 *
 * Questo metodo prende un'email come parametro, esegue una query per ottenere un ResultSet contenente
 * i dati dell'Account e della Residenza associati a quell'email, quindi costruisce e restituisce un oggetto Account.
 *
 * @param Email L'email associata all'Account da recuperare.
 * @return Un oggetto Account associato all'email specificata, o null se nessun Account è trovato.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione dell'oggetto Account.
 */
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



/**
 * Ottiene un Account associato a un nickname specifico dalla base di dati.
 *
 * Questo metodo prende un nickname come parametro, esegue una query per ottenere un ResultSet contenente
 * i dati dell'Account e della Residenza associati a quel nickname, quindi costruisce e restituisce un oggetto Account.
 *
 * @param nickname Il nickname associato all'Account da recuperare.
 * @return Un oggetto Account associato al nickname specificato, o null se nessun Account è trovato.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione dell'oggetto Account.
 */
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



/**
 * Ottiene le immagini di un album associato a un determinato ID dalla base di dati.
 *
 * Questo metodo prende un ID di album come parametro, esegue una query per ottenere un ResultSet contenente
 * i dati delle immagini dell'album associato a quell'ID, quindi costruisce e restituisce una lista di oggetti MyImage.
 *
 * @param ID L'ID dell'album per il quale ottenere le immagini.
 * @return Una lista di oggetti MyImage associate all'album specificato.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti MyImage.
 */
    public static ArrayList<MyImage> getAlbumImages_by_ID(String ID) throws SQLException {

        ArrayList<MyImage> result = new ArrayList<MyImage>();
        ResultSet resultSet = database.submitQuery(QueryBuilder.getAlbumImages_by_ID(ID));

        while (resultSet.next()) { 
            result.add(new MyImage(getHashMap_for_ClassConstructor(resultSet, Tabelle.ALBUM_IMAGES)));
        }

        return result; 
    }



/**
 * Ottiene le immagini di un artista associato a un determinato ID dalla base di dati.
 *
 * Questo metodo prende un ID di artista come parametro, esegue una query per ottenere un ResultSet contenente
 * i dati delle immagini dell'artista associato a quell'ID, quindi costruisce e restituisce una lista di oggetti MyImage.
 *
 * @param ID L'ID dell'artista per il quale ottenere le immagini.
 * @return Una lista di oggetti MyImage associate all'artista specificato.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti MyImage.
 */
    public static ArrayList<MyImage> getArtistImages_by_ID(String ID) throws SQLException {

        ArrayList<MyImage> result = new ArrayList<MyImage>();
        ResultSet resultSet = database.submitQuery(QueryBuilder.getArtistImages_by_ID(ID));

        while (resultSet.next()) { 
            result.add(new MyImage(getHashMap_for_ClassConstructor(resultSet, Tabelle.ALBUM_IMAGES)));
        }

        return result; 
    }



/**
 * Aggiunge un nuovo Account e una nuova Residenza alla base di dati.
 *
 * Questo metodo prende due HashMap, una contenente i dati dell'Account e l'altra contenente i dati della Residenza,
 * esegue una query per ottenere l'ID della Residenza corrispondente, e se la Residenza non esiste, la aggiunge.
 * Successivamente, aggiunge un nuovo Account utilizzando l'ID della Residenza ottenuto.
 *
 * @param colonne_account   Una HashMap contenente i dati dell'Account da aggiungere.
 * @param colonne_residenza Una HashMap contenente i dati della Residenza associata all'Account da aggiungere.
 * @throws SQLException Se si verifica un errore durante l'esecuzione delle query o l'inserimento dei dati.
 */
    public static void addAccount_and_addResidence(HashMap<Colonne, Object> colonne_account, HashMap<Colonne, Object> colonne_residenza) throws SQLException {

        String query = QueryBuilder.getResidenceId_Query((String)colonne_residenza.get(Colonne.VIA_PIAZZA), (int)colonne_residenza.get(Colonne.CIVIC_NUMER), (String)colonne_residenza.get(Colonne.COUNCIL_NAME), (String)colonne_residenza.get(Colonne.PROVINCE_NAME));
        String residence_id = "";

        ResultSet resultSet = database.submitQuery(query);

        //aggiungo la resistenza se non esiste
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



/**
 * Ottiene un elenco di canzoni popolari in base al limite e all'offset specificati.
 *
 * Questo metodo esegue una query per ottenere un elenco di canzoni ordinate per popolarità in modo discendente.
 * Limita il risultato in base al numero specificato e utilizza l'offset per saltare un numero specificato di risultati.
 * Aggiunge le immagini degli album associate a ciascuna canzone.
 *
 * @param limit  Il numero massimo di canzoni da restituire.
 * @param offset Il numero di canzoni da saltare prima di iniziare a restituire risultati.
 * @return Una lista di oggetti Song rappresentanti le canzoni popolari.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Song.
 */
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



/**
 * Ottiene un elenco di album pubblicati recentemente in base al limite, all'offset e alla soglia specificati.
 *
 * Questo metodo esegue una query per ottenere un elenco di album ordinati per data di pubblicazione in modo discendente.
 * Limita il risultato in base al numero specificato e utilizza l'offset per saltare un numero specificato di risultati.
 * Aggiunge le immagini associate a ciascun album e recupera le canzoni associate a ciascun album.
 *
 * @param limit     Il numero massimo di album da restituire.
 * @param offset    Il numero di album da saltare prima di iniziare a restituire risultati.
 * @param threshold La soglia di pubblicazione degli album.
 * @return Una lista di oggetti Album rappresentanti gli album pubblicati recentemente.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Album.
 */
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
 * Cerca tutte le canzoni che contengono nel titolo la parola passata come parametro e restituisce anche il numero di elementi
 * @param search_key la parola da cercare nel titolo delle canzoni
 * @param limit numero di record massimi che si vuole avere come risultato
 * @param offset numero di record da saltare
 * @return una lista di Song che contengono nel titolo la parola passata come parametro
 * @throws SQLException
 */
    public static Object[] searchSong_and_countElement(String search_key, long limit, long offset, int mode) throws SQLException 
    {
        
        ArrayList<Song> pageElement = new ArrayList<Song>();
        long total_element = 0;

        String query = QueryBuilder.getSongSearch_query(search_key, limit, offset, mode);
        ResultSet resultSet1 = database.submitQuery(query);

        while (resultSet1.next()) { 
            Song song = new Song(getHashMap_for_ClassConstructor(resultSet1, Tabelle.SONG));
            song.addImages(getAlbumImages_by_ID(song.getAlbumId()));
            pageElement.add(song);    
        }

        resultSet1.close();
      
        String query_result = QueryBuilder.getSongSearch_Count_query(search_key, mode);
        ResultSet resultSet2 = database.submitQuery(query_result);

        resultSet2.next();
        total_element = (long) resultSet2.getObject("count");
        resultSet2.close();
    
        return new Object[] {total_element, pageElement}; 
    }



/**
 * Ricerca le canzoni corrispondenti agli ID specificati.
 *
 * @param IDs Un array di ID delle canzoni da cercare.
 * @return Una lista di oggetti Song corrispondenti agli ID specificati.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Song.
 */
    public static ArrayList<Song> searchSongByIDs(String[] IDs) throws SQLException {
        String query = QueryBuilder.getSongByID_query(IDs);
        return buildSongObjects_From_resultSet(database.submitQuery(query), true);
    }



/**
 * Ottiene un elenco di canzoni associate a un album specificato.
 *
 * @param albumID L'ID dell'album da cui ottenere le canzoni.
 * @return Una lista di oggetti Song associate all'album specificato.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Song.
 */
    public static ArrayList<Song> getAlbumSongs(String albumID) throws SQLException {
        String query = QueryBuilder.getAlbumSongs_query(albumID);
        return buildSongObjects_From_resultSet(database.submitQuery(query), true);
    }



/**
 * Esegue una ricerca degli album in base ai criteri specificati.
 *
 * @param search La stringa di ricerca per gli album.
 * @param limit  Il numero massimo di album da restituire.
 * @param offset Il numero di album da saltare prima di iniziare a restituire risultati.
 * @return Un array contenente il numero totale di elementi trovati e una lista di oggetti Album corrispondenti ai criteri specificati.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Album.
 */
    public static Object[] searchAlbum(String search, long limit, long offset) throws SQLException {
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

        String query_result = QueryBuilder.getAlbumSearch_Count_query(search);
        ResultSet resultSet2 = database.submitQuery(query_result);

        resultSet2.next();
        long total_element = (long) resultSet2.getObject("count");
        resultSet2.close();
    
        return new Object[] {total_element, result}; 
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OPERAZIONI SULLE PLAYLIST
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



/**
 * Aggiunge una nuova playlist per un account specificato.
 *
 * @param accountID    L'ID dell'account a cui aggiungere la playlist.
 * @param playlistName Il nome della nuova playlist.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
 */
    public static void addPlaylist(String accountID, String playlistName) throws SQLException  {
        String query = QueryBuilder.addPlaylist_query(accountID, playlistName, getCurrentDate(), generate_ID_from_Time());
        database.submitInsertQuery(query);
    }



/**
 * Ottiene gli ID delle canzoni associate a una playlist specificata.
 *
 * @param playlistID L'ID della playlist da cui ottenere gli ID delle canzoni.
 * @return Un array di stringhe contenente gli ID delle canzoni associate alla playlist.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
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
 * Ottiene tutte le playlist associate a un account specificato.
 *
 * @param accountID L'ID dell'account da cui ottenere le playlist.
 * @return Un'istanza di ArrayList contenente oggetti Playlist associati all'account specificato.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Playlist.
 */
    public static Object getAccountsPlaylists(String accountID) throws SQLException {
        String query = QueryBuilder.getAccountsPlaylists_query(accountID);
        ResultSet resultSet = database.submitQuery(query);
        ArrayList<Playlist> list = new ArrayList<Playlist>();

        while (resultSet.next()) { 
            Playlist playlist = new Playlist(getHashMap_for_ClassConstructor(resultSet, Tabelle.PLAYLIST));
            //playlist.setSongsID(getPlaylistSongsID(playlist.getId()));
            list.add(playlist); 
        }

        return list;
    }



/**
 * Aggiunge una canzone a una playlist specificata.
 *
 * @param accountID  L'ID dell'account associato alla playlist.
 * @param playlistID L'ID della playlist a cui aggiungere la canzone.
 * @param songID     L'ID della canzone da aggiungere alla playlist.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
 */
    public static void addSongToPlaylist(String accountID, String playlistID, String songID) throws SQLException {
        String query = QueryBuilder.addSongToPlaylist_query(playlistID, songID);
        database.submitInsertQuery(query);
    }



/**
 * Rimuove una canzone da una playlist specificata.
 *
 * @param accountID  L'ID dell'account associato alla playlist.
 * @param playlistID L'ID della playlist da cui rimuovere la canzone.
 * @param songID     L'ID della canzone da rimuovere dalla playlist.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
 */
    public static void removeSongFromPlaylist (String accountID, String playlistID, String songID) throws SQLException {
        String query = QueryBuilder.removeSongFromPlaylist_query(playlistID, songID);
        database.submitQuery(query);
    }



/**
 * Rinomina una playlist specificata.
 *
 * @param accountID  L'ID dell'account associato alla playlist.
 * @param playlistID L'ID della playlist da rinominare.
 * @param newName    Il nuovo nome per la playlist.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
 */
    public static void renamePlaylist(String accountID, String playlistID, String newName) throws SQLException {
        String query = QueryBuilder.renamePlaylist_query(playlistID, newName);
        database.submitQuery(query);
    }

/**
 * Funnzione per crecre un nuovo commento
 * @throws SQLException
 */
    public static void addEmotion(HashMap<Colonne, Object> ColonneValore) throws SQLException {
        String query = QueryBuilder.insert_query_creator(PredefinedSQLCode.Tabelle.EMOZIONE, ColonneValore);
        database.submitQuery(query);
    }
        
/**
 * Eliminare un commento
 * @throws SQLException
 */
    public static void deleteEmotion(String emotionID) throws SQLException{
        
        String query = QueryBuilder.deleteQueryCreator_by_primaryKey(Tabelle.EMOZIONE, emotionID);
        database.submitQuery(query);
    }

    
   
/**
 * Ottiene le emozioni associate a una canzone specificata.
 *
 * @param songID L'ID della canzone da cui ottenere le emozioni.
 * @return Un'istanza di ArrayList contenente oggetti Emotion associate alla canzone specificata.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Emotion.
 */
    public static Object getSongEmotion(String songID) throws SQLException 
    {
        String query = QueryBuilder.getSongEmotion_query(songID);
        ResultSet resultSet = database.submitQuery(query);
        ArrayList<Emotion> list = new ArrayList<Emotion>();

        while (resultSet.next()) { 
            Emotion playlist = new Emotion(getHashMap_for_ClassConstructor(resultSet, Tabelle.EMOZIONE));
            list.add(playlist); 
        }
        return list;
    }



/**
 * Ottiene le emozioni associate a un account specificato.
 *
 * @param accountID L'ID dell'account da cui ottenere le emozioni.
 * @return Un'istanza di ArrayList contenente oggetti Emotion associate all'account specificato.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Emotion.
 */
    public static Object getAccountEmotions(String accountID) throws SQLException 
    {
        String query = QueryBuilder.getAccountEmotions(accountID);
        ResultSet resultSet = database.submitQuery(query);
        ArrayList<Emotion> list = new ArrayList<Emotion>();

        while (resultSet.next()) { 
            Emotion playlist = new Emotion(getHashMap_for_ClassConstructor(resultSet, Tabelle.EMOZIONE));
            list.add(playlist); 
        }
        return list;
    }



/**
 * Elimina una playlist specificata associata a un account.
 *
 * @param accountID  L'ID dell'account a cui è associata la playlist da eliminare.
 * @param playlistID L'ID della playlist da eliminare.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
 */
    public static void deletePlaylist(String accountID, String playlistID) throws SQLException {
        String query = QueryBuilder.deletePlaylist_query(playlistID);
        database.submitQuery(query);
    }



/**
 * Elimina un account specificato.
 *
 * @param accountID L'ID dell'account da eliminare.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
 */
    public static void deleteAccount(String accountID) throws SQLException {
        String query = QueryBuilder.deleteAccount_query(accountID);
        database.submitQuery(query);
    }



/**
 * Ottiene tutte le canzoni associate a un artista specificato.
 *
 * @param artistID L'ID dell'artista da cui ottenere le canzoni.
 * @return Un'istanza di ArrayList contenente oggetti Song associate all'artista specificato.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Song.
 */
    public static ArrayList<Song> getArtistSong(String artistID) throws SQLException {
        String query = QueryBuilder.getArtistSong_query(artistID);
        ArrayList<Song> result = new ArrayList<Song>();

        ResultSet resultSet = database.submitQuery(query);
        while (resultSet.next()) { 
            Song song = new Song(getHashMap_for_ClassConstructor(resultSet, Tabelle.SONG));
            //album.addImages(getAlbumImages_by_ID(album.getID()));
            
            song.addImages(getAlbumImages_by_ID(song.getAlbumId()));
            result.add(song);    
        }

        return result;
    }



/**
 * Ottiene un oggetto artista con l'ID specificato.
 *
 * @param ID L'ID dell'artista da ottenere.
 * @return Un'istanza di Artist o null se l'artista non è trovato.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione dell'oggetto Artist.
 */
    public static Artist getArtistByID(String ID) throws SQLException {

        String query = QueryBuilder.getArtistByID_query(ID);
        ResultSet resultSet = database.submitQuery(query);

        if(!resultSet.next())
            return null;

        Artist artist = new Artist(getHashMap_for_ClassConstructor(resultSet, Tabelle.ARTIST));
        artist.addImages(getArtistImages_by_ID(artist.getID()));

        return artist;
    
    }



/**
 * Cerca gli artisti che corrispondono alla chiave specificata.
 *
 * @param key   La chiave di ricerca per gli artisti.
 * @param limit Il limite di risultati restituiti.
 * @param offset L'offset per la paginazione dei risultati.
 * @return Un array contenente il totale degli elementi e un'istanza di ArrayList con oggetti Artist che corrispondono alla ricerca.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Artist.
 */
    public static Object[] searchArtists(String key, long limit, long offset) throws SQLException {
        String query = QueryBuilder.searchArtist_query(key, limit, offset);
        ArrayList<Artist> result = new ArrayList<Artist>();

        ResultSet resultSet = database.submitQuery(query);
        while (resultSet.next()) { 
            Artist artist = new Artist(getHashMap_for_ClassConstructor(resultSet, Tabelle.ARTIST));
            //album.addImages(getAlbumImages_by_ID(album.getID()));
            
            artist.addImages(getArtistImages_by_ID(artist.getID()));
            
            result.add(artist);    
        }

        String query_result = QueryBuilder.searchArtist_Count_query(key);
        ResultSet resultSet2 = database.submitQuery(query_result);

        resultSet2.next();
        long total_element = (long) resultSet2.getObject("count");
        resultSet2.close();

        resultSet.close();
        return new Object[] {total_element, result};
    }



/**
 * Ottiene le canzoni associate a una playlist specificata.
 *
 * @param playlistID L'ID della playlist da cui ottenere le canzoni.
 * @return Un'istanza di ArrayList contenente oggetti Song associate alla playlist specificata.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione degli oggetti Song.
 */
    public static ArrayList<Song> getPlaylistSong(String playlistID) throws SQLException {
        String query = QueryBuilder.getPlaylistSong_query(playlistID);
        return buildSongObjects_From_resultSet(database.submitQuery(query), true);
    }



/**
 * Ottiene un oggetto Album con l'ID specificato.
 *
 * @param ID L'ID dell'album da ottenere.
 * @return Un'istanza di Album o null se l'album non è trovato.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query o la costruzione dell'oggetto Album.
 */
    public static Album getAlbumByID(String ID) throws SQLException {
        String query = QueryBuilder.getAlbumByID_query(ID);
        ResultSet resultSet = database.submitQuery(query);
        

        if(!resultSet.next())
            return null;
        
        Album album = new Album(getHashMap_for_ClassConstructor(resultSet, Tabelle.ALBUM));
        album.addImages(getAlbumImages_by_ID(album.getID()));
        resultSet.close();

        return album;
    }

    

    

}

