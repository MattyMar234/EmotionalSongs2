package database;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import org.javatuples.Triplet;

import database.PredefinedSQLCode.Colonne;
import database.PredefinedSQLCode.Operazioni_SQL;
import database.PredefinedSQLCode.Tabelle;
import objects.Playlist;
import server.App;
import server.Terminal;

//import javax.imageio.spi.RegisterableService;

/**
 * La classe PredefinedSQLCode definisce costanti e metodi correlati al codice SQL predefinito per le operazioni sul database.
 * Include informazioni sui nomi delle tabelle, colonne, query e chiavi esterne.
 * Questa classe fornisce anche metodi per generare ed eseguire query SQL per l'inserimento di dati nel database.
 */

public class PredefinedSQLCode 
{
    public static final ArrayList<Hashtable<Tabelle, String>> elenco_QuerySQL; 
    public static final Hashtable<Tabelle, Colonne[]> tablesAttributes;
    public static final Hashtable<Tabelle, Colonne[]> tablesPrimaryKey;
    public static final Hashtable<Tabelle, Object[]> tablesForeignKey;
    public static final Hashtable<Tabelle, String> createTable_Queries;
    public static final Hashtable<Tabelle, String> deleteTable_Queries;

    private static final String ID_SIZE = "(64)";
    private static final String ACCOUNT_ID_SIZE = "(120)";
    
   
    public static enum Colonne
    {
        ID("ID",                      "VARCHAR",  ID_SIZE,         "NOT NULL"),
        SONG_ID_REF("ID_Song",        "VARCHAR",  ID_SIZE,         "NOT NULL"),
        EMAIL("email",                "VARCHAR",  "(120)",    "NOT NULL UNIQUE"),
        PASSWORD("password",          "VARCHAR",  "(120)",    "NOT NULL"),
        ARTIST_ID_REF("ID_Artist",    "VARCHAR",  ID_SIZE,         "NOT NULL"),
        PLAYLIST_ID_REF("playli_ref", "VARCHAR",  ID_SIZE,         "NOT NULL"),
        IMAGE_ID_REF("ID_Image",      "VARCHAR",  ID_SIZE,         "NOT NULL"),
        ALBUM_ID_REF("ID_Album",      "VARCHAR",  ID_SIZE,         "NOT NULL"),
        ACCOUNT_ID_REF("Account_id",  "VARCHAR",  ACCOUNT_ID_SIZE, "NOT NULL"),
        RESIDENCE_ID_REF("Residen_id","VARCHAR",  ID_SIZE,         "NOT NULL"),
        EMOZIONE_ID_REF("Emotion_id", "VARCHAR",  ID_SIZE,         "NOT NULL"),
        URL("Spotify_URL",            "VARCHAR",  "(120)",    "NOT NULL"),
        IMAGE_SIZE("Image_size",      "VARCHAR",  "(12)",     "NOT NULL"),
        NAME("name",                  "VARCHAR",  "(320)",    "NOT NULL"),
        SURNAME("surname",            "VARCHAR",  "(120)",    "NOT NULL"),
        FISCAL_CODE("FiscalCode",     "VARCHAR",  "(16)",     "NOT NULL"),
        TITLE("title",                "VARCHAR",  "(340)",    "NOT NULL"),
        POPULARITY("popularity",      "SMALLINT", "",         "NOT NULL"),
        YEAR("Year",                  "INTEGER",  "",         "NOT NULL"),
        VALUE("Value",                "INTEGER",  "",         "NOT NULL"),
        FOLLOWERS("followers",        "BIGINT",   "",         "NOT NULL"),
        DURATION("Duration_ms",       "BIGINT",   "",         "NOT NULL"),
        RELEASE_DATE("Release_date",  "VARCHAR",  "(32)",         "NOT NULL"),
        CREATION_DATE("Creation_date","VARCHAR",  "(32)",         "NOT NULL"),
        TYPE("Type",                  "VARCHAR",  "(32)",     "NOT NULL"),
        ELEMENT("Element",            "INTEGER",  "",         "NOT NULL"),
        GENERE_MUSICALE("genre",      "VARCHAR",  "(64)",     "NOT NULL"),
        COMMENTO("commento",          "VARCHAR",  "(256)",    ""),
        NICKNAME("nickname",          "VARCHAR",  "(120)",    "NOT NULL"),
        VIA_PIAZZA("Via_Piazza",      "VARCHAR",  "(120)",    "NOT NULL"),
        CIVIC_NUMER("civicNumber",    "INTEGER",  "",         "NOT NULL"),
        COUNCIL_NAME("council_name",  "VARCHAR",  "(80)",     "NOT NULL"),
        PROVINCE_NAME("province_name","VARCHAR",  "(80)",     "NOT NULL"),
        CAP("cap",                    "VARCHAR",  "(10)",     "");
        
        
        private String name;
        private String type;
        private String args;
        private String size;
        
        private Colonne(String name, String type,String size, String args){
            this.name = name;
            this.type = type;
            this.args = args;
            this.size = size;
        }
        


    /**
     * Restituisce il nome dell'oggetto.
     *
     * Questo metodo restituisce il nome dell'oggetto, che è stato precedentemente impostato.
     *
     * @return Il nome dell'oggetto.
     */
        public String getName(){
            return this.name;
        }



    /**
     * Restituisce il tipo dell'oggetto.
     *
     * Questo metodo restituisce il tipo dell'oggetto, che è stato precedentemente impostato.
     *
     * @return Il tipo dell'oggetto.
     */
        public String getType(){
            return this.type;
        }
    


    /**
     * Restituisce una stringa che rappresenta il tipo e la dimensione dell'oggetto.
     *
     * Questo metodo restituisce una stringa che rappresenta sia il tipo che la dimensione dell'oggetto,
     * concatenandoli insieme.
     *
     * @return Una stringa che rappresenta il tipo e la dimensione dell'oggetto.
     */
        public String getType_and_Size(){
            return this.type + this.size;
        }

        @Override
        public String toString() {
            return this.name + " " + this.type + this.size + " " + this.args;
        }
    }

    public static enum Tabelle 
    {
        //!! l'ordine dipende dal "DROP TABLE" e dal "CREATE TABLE"
        ARTIST          ("Artista"), 
        GENERI_MUSICALI ("GeneriMusicali"), 
        GENERI_ARTISTA  ("GeneriArtista"), 
        ALBUM           ("Album"), 
        ALBUM_IMAGES    ("ImmaginiAlbums"),
        ARTIST_IMAGES   ("ImmaginiArtisti"), 
        SONG            ("Canzone"), 
        SONG_AUTORS     ("AutoriCanzone"), 
        RESIDENZA       ("Residenza"), 
        ACCOUNT         ("Account"), 
        //COMMENTO        ("Commento"), 
        EMOZIONE        ("Emozione"), 
        PLAYLIST        ("Playlist"), 
        //PROVINCIA       ("Provincia"), 
        //COMUNE          ("Comune"), 
        PLAYLIST_SONGS   ("canzoni_playlist");
        
        
        private String name;
       
        private Tabelle(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }



/**
 * Enumerazione che rappresenta diverse operazioni SQL.
 *
 * Questa enumerazione fornisce costanti per diverse operazioni SQL come CREATE, DELETE, INSERT, CLEAR_DB e TABLE_KEY.
 * Ogni costante ha un valore di stringa associato che rappresenta l'operazione corrispondente.
 */
    public static enum Operazioni_SQL
    {
        CREATE("CREATE TABLE IF NOT EXISTS"),
        DELETE("DROP TABLE IF EXISTS"),
        INSERT("INSERT INTO"),
        CLEAR_DB("SELECT table_name FROM information_schema.tables WHERE table_schema = 'nome_database';"),
        TABLE_KEY("PRIMARY KEY");
        private String name;
       
        private Operazioni_SQL(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    


    
    static {
        
        //inizilizzazione HashTables
        createTable_Queries = new Hashtable<Tabelle, String>();
        deleteTable_Queries = new Hashtable<Tabelle, String>();
        
        tablesAttributes = new Hashtable<Tabelle, Colonne []>();
        tablesPrimaryKey = new Hashtable<Tabelle, Colonne []>();
        tablesForeignKey = new Hashtable<Tabelle, Object  []>(); 


        //======================= [Lista delle colonne] =======================//
        tablesAttributes.put(Tabelle.ARTIST,            new Colonne[] {Colonne.ID, Colonne.NAME, Colonne.URL, Colonne.FOLLOWERS, Colonne.POPULARITY/*, Colonne.IMAGE_ID_REF*/});
        tablesAttributes.put(Tabelle.SONG,              new Colonne[] {Colonne.ID,Colonne.TITLE,Colonne.URL, Colonne.DURATION, Colonne.POPULARITY, Colonne.ALBUM_ID_REF});
        tablesAttributes.put(Tabelle.GENERI_MUSICALI,   new Colonne[] {Colonne.GENERE_MUSICALE});
        tablesAttributes.put(Tabelle.GENERI_ARTISTA,    new Colonne[] {Colonne.GENERE_MUSICALE, Colonne.ID});
        tablesAttributes.put(Tabelle.ALBUM,             new Colonne[] {Colonne.ID, Colonne.NAME, Colonne.RELEASE_DATE, Colonne.URL, Colonne.TYPE, Colonne.ELEMENT, Colonne.ARTIST_ID_REF});
        //tablesAttributes.put(Tabelle.COMMENTO,          new Colonne[] {Colonne.ID, Colonne.COMMENTO, Colonne.ACCOUNT_ID_REF, Colonne.EMOZIONE_ID_REF});
        tablesAttributes.put(Tabelle.EMOZIONE,          new Colonne[] {Colonne.ID, Colonne.TYPE, Colonne.VALUE, Colonne.COMMENTO, Colonne.ACCOUNT_ID_REF, Colonne.SONG_ID_REF});
        tablesAttributes.put(Tabelle.PLAYLIST,          new Colonne[] {Colonne.ID, Colonne.NAME, Colonne.CREATION_DATE, Colonne.ACCOUNT_ID_REF});
        tablesAttributes.put(Tabelle.ACCOUNT,           new Colonne[] {Colonne.NAME, Colonne.NICKNAME, Colonne.SURNAME, Colonne.FISCAL_CODE, Colonne.EMAIL, Colonne.PASSWORD,Colonne.RESIDENCE_ID_REF});
        //tablesAttributes.put(Tabelle.COMUNE,            new Colonne[] {Colonne.NAME, Colonne.CAP});
        //tablesAttributes.put(Tabelle.PROVINCIA,         new Colonne[] {Colonne.NAME});
        tablesAttributes.put(Tabelle.RESIDENZA,         new Colonne[] {Colonne.ID, Colonne.VIA_PIAZZA, Colonne.CIVIC_NUMER, Colonne.PROVINCE_NAME, Colonne.COUNCIL_NAME, Colonne.CAP});
        tablesAttributes.put(Tabelle.ALBUM_IMAGES,      new Colonne[] {Colonne.ID, Colonne.URL, Colonne.IMAGE_SIZE});
        tablesAttributes.put(Tabelle.ARTIST_IMAGES,     new Colonne[] {Colonne.ID, Colonne.URL, Colonne.IMAGE_SIZE});
        tablesAttributes.put(Tabelle.PLAYLIST_SONGS,    new Colonne[] {Colonne.PLAYLIST_ID_REF, Colonne.SONG_ID_REF});
        tablesAttributes.put(Tabelle.SONG_AUTORS,       new Colonne[] {Colonne.ARTIST_ID_REF, Colonne.SONG_ID_REF});

        

        //======================= [Lista delle chiavi primarie] =======================//
        tablesPrimaryKey.put(Tabelle.ARTIST,            new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.SONG,              new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.GENERI_MUSICALI,   new Colonne[] {Colonne.GENERE_MUSICALE});
        tablesPrimaryKey.put(Tabelle.GENERI_ARTISTA,    new Colonne[] {Colonne.GENERE_MUSICALE, Colonne.ID});
        tablesPrimaryKey.put(Tabelle.ALBUM,             new Colonne[] {Colonne.ID});
        //tablesPrimaryKey.put(Tabelle.COMMENTO,          new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.EMOZIONE,          new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.PLAYLIST,          new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.ACCOUNT,           new Colonne[] {Colonne.NICKNAME}); //, Colonne.EMAIL
        //tablesPrimaryKey.put(Tabelle.COMUNE,            new Colonne[] {Colonne.NAME});
        //tablesPrimaryKey.put(Tabelle.PROVINCIA,         new Colonne[] {Colonne.NAME});
        tablesPrimaryKey.put(Tabelle.RESIDENZA,         new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.ALBUM_IMAGES,      new Colonne[] {Colonne.ID, Colonne.IMAGE_SIZE});
        tablesPrimaryKey.put(Tabelle.ARTIST_IMAGES,     new Colonne[] {Colonne.ID, Colonne.IMAGE_SIZE});
        tablesPrimaryKey.put(Tabelle.PLAYLIST_SONGS,    new Colonne[] {Colonne.PLAYLIST_ID_REF, Colonne.SONG_ID_REF});
        tablesPrimaryKey.put(Tabelle.SONG_AUTORS,       new Colonne[] {Colonne.ARTIST_ID_REF, Colonne.SONG_ID_REF});


        //======================= [Lista delle chiavi esterne] =======================//
        tablesForeignKey.put(Tabelle.GENERI_ARTISTA, new Object[] {
            new Triplet<Colonne, Tabelle, Colonne> (Colonne.ID, Tabelle.ARTIST, Colonne.ID), 
            new Triplet<Colonne, Tabelle, Colonne> (Colonne.GENERE_MUSICALE, Tabelle.GENERI_MUSICALI, Colonne.GENERE_MUSICALE)
        });

        tablesForeignKey.put(Tabelle.PLAYLIST_SONGS, new Object[] {
            new Triplet<Colonne, Tabelle, Colonne> (Colonne.PLAYLIST_ID_REF, Tabelle.PLAYLIST, Colonne.ID), 
            new Triplet<Colonne, Tabelle, Colonne> (Colonne.SONG_ID_REF, Tabelle.SONG, Colonne.ID)
        });

        tablesForeignKey.put(Tabelle.SONG_AUTORS, new Object[] {
            new Triplet<Colonne, Tabelle, Colonne> (Colonne.ARTIST_ID_REF, Tabelle.ARTIST, Colonne.ID), 
            new Triplet<Colonne, Tabelle, Colonne> (Colonne.SONG_ID_REF, Tabelle.SONG, Colonne.ID)
        });

        /*tablesForeignKey.put(Tabelle.COMMENTO,      new Object[] { 
            new Triplet<Colonne, Tabelle, Colonne> (Colonne.ACCOUNT_ID_REF, Tabelle.ACCOUNT, Colonne.NICKNAME),
            new Triplet<Colonne, Tabelle, Colonne> (Colonne.EMOZIONE_ID_REF, Tabelle.EMOZIONE, Colonne.ID)
        });*/
        
        tablesForeignKey.put(Tabelle.EMOZIONE,      new Object[] { 
            new Triplet<Colonne, Tabelle, Colonne> (Colonne.SONG_ID_REF, Tabelle.SONG, Colonne.ID),
            new Triplet<Colonne, Tabelle, Colonne> (Colonne.ACCOUNT_ID_REF, Tabelle.ACCOUNT, Colonne.NICKNAME)
        });
        
        tablesForeignKey.put(Tabelle.ALBUM_IMAGES,  new Object[] { new Triplet<Colonne, Tabelle, Colonne> (Colonne.ID, Tabelle.ALBUM, Colonne.ID)});
        tablesForeignKey.put(Tabelle.ARTIST_IMAGES, new Object[] { new Triplet<Colonne, Tabelle, Colonne> (Colonne.ID, Tabelle.ARTIST, Colonne.ID)});
        tablesForeignKey.put(Tabelle.SONG,          new Object[] { new Triplet<Colonne, Tabelle, Colonne> (Colonne.ALBUM_ID_REF, Tabelle.ALBUM, Colonne.ID)});
        tablesForeignKey.put(Tabelle.PLAYLIST,      new Object[] { new Triplet<Colonne, Tabelle, Colonne> (Colonne.ACCOUNT_ID_REF, Tabelle.ACCOUNT, Colonne.NICKNAME)});
        tablesForeignKey.put(Tabelle.ACCOUNT,       new Object[] { new Triplet<Colonne, Tabelle, Colonne> (Colonne.RESIDENCE_ID_REF, Tabelle.RESIDENZA, Colonne.ID)});
        tablesForeignKey.put(Tabelle.ALBUM,         new Object[] { new Triplet<Colonne, Tabelle, Colonne> (Colonne.ARTIST_ID_REF, Tabelle.ARTIST, Colonne.ID)});
        


        //Triplet<NomiColonne, NomiTabelle,NomiColonne> s = new Triplet<NomiColonne, NomiTabelle,NomiColonne>

        for(Tabelle nomeTabella: Tabelle.values()) {
            deleteTable_Queries.put(nomeTabella,Operazioni_SQL.DELETE.toString() + " " + nomeTabella + " CASCADE;");
            createTable_Queries.put(nomeTabella, QueryBuilder.createTable_query_creator(nomeTabella));
        }
        
        
        //elenco delle hashTable
        elenco_QuerySQL = new ArrayList<>();
        elenco_QuerySQL.add(createTable_Queries);
        elenco_QuerySQL.add(deleteTable_Queries);
    }



/**
 * Disponi gli elementi dell'array in base ai valori della mappa e alla tabella specificata.
 *
 * Questo metodo prende un array di oggetti, una mappa di valori e una tabella specificata.
 * Organizza gli elementi dell'array in base ai valori della mappa corrispondenti alle colonne della tabella.
 *
 */
    public static Object[] disponiElementiColonne(Object[] array, HashMap<String, Object> valori, Tabelle t) {
        
        Object[] output = new Object[array.length];
        
        for (int i = 0; i < array.length; i++) {    
            String nomeCol_i = PredefinedSQLCode.tablesAttributes.get(t)[i].getName();
            output[i] = valori.get(nomeCol_i);

            if(output[i] == null) {
                throw new NoSuchFieldError("Table: " + t.name + ", colum " + PredefinedSQLCode.tablesAttributes.get(t)[i].name() + " not found.");
            }
        }
        return output;
    }



/**
 * Crea e esegue una query di inserimento nella tabella specificata utilizzando i dati forniti.
 *
 * Questo metodo prende una mappa di dati, una tabella specificata, un oggetto App principale e un parametro opzionale abilitato.
 * Utilizza i dati forniti per generare una query di inserimento e la esegue sulla base di dati associata all'oggetto principale App.
 */
    public static void crea_INSER_query_ed_esegui(HashMap<String, Object> data, Tabelle t, App main, boolean... enable) 
    {
        String query = "";
        try 
        {
            //preparo gli elementi per generare la query
            Object[] array = new Object[data.keySet().size()];                              //array contenenti i valori delle colonne del record         
            Object[] element = PredefinedSQLCode.disponiElementiColonne(array, data, t);    //riordino gli attributi
            

            query = QueryBuilder.insert_query_creator(t, element);
            //main.database.submitQuery(query);
            main.database.submitInsertQuery(query);
        } 
        catch (SQLException e) 
        {
            if(enable.length > 0 && enable[0] == true) {
                System.out.println(e);
                return;
            }
               
            if(e.getMessage().toLowerCase().contains("duplicate") || e.getMessage().toLowerCase().contains("duplicato")) {
                //System.out.println("hereee");
            }
            else if( e.getMessage().toLowerCase().contains(" vincolo di chiave esterna")) {

            }
            else {
                //e.printStackTrace();
                System.out.println("error: " + Terminal.Color.RED_BOLD_BRIGHT + e.getMessage() + Terminal.Color.RESET);
                System.out.println("Query: " + Terminal.Color.CYAN_BOLD_BRIGHT + query + Terminal.Color.RESET);
                //System.exit(0);
            } 
        }
    }
}
