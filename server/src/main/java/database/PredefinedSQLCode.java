package database;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Hashtable;
import org.javatuples.Triplet;
import javax.imageio.spi.RegisterableService;


/**
 * The class <code>PredefinedSQLCode</code> contains the static predefined SQL query.
 * @version 1.0
 */
public class PredefinedSQLCode 
{
    public static final ArrayList<Hashtable<Tabelle, String>> elenco_QuerySQL; 
    public static final Hashtable<Tabelle, Colonne[]> tablesAttributes;
    public static final Hashtable<Tabelle, Colonne[]> tablesPrimaryKey;
    public static final Hashtable<Tabelle, Object[]> tablesForeignKey;
    public static final Hashtable<Tabelle, String> createTable_Queries;
    public static final Hashtable<Tabelle, String> deleteTable_Queries;

    private static final String ID_SIZE = "(22)";
    private static final String ACCOUNT_ID_SIZE = "(120)";
    

    
    
    public static enum Colonne
    {
        ID("ID",                      "VARCHAR",  ID_SIZE,         "NOT NULL"),
        SONG_ID_REF("ID_Song",        "VARCHAR",  ID_SIZE,         "NOT NULL"),
        PLAYLIST_ID_REF("playli_ref", "VARCHAR",  "(260)",    "NOT NULL"),
        IMAGE_ID_REF("ID_Image",      "VARCHAR",  ID_SIZE,         "NOT NULL"),
        ALBUM_ID_REF("ID_Album",      "VARCHAR",  ID_SIZE,         "NOT NULL"),
        ACCOUNT_ID_REF("Account_id",  "VARCHAR",  ACCOUNT_ID_SIZE, "NOT NULL"),
        RESIDENCE_ID_REF("Residen_id","VARCHAR",  ID_SIZE,         "NOT NULL"),
        URL("Spotify_URL",            "VARCHAR",  "(60)",     "NOT NULL"),
        IMAGE_SIZE("Image_size",      "VARCHAR",  "(12)",     "NOT NULL"),
        NAME("name",                  "VARCHAR",  "(260)",    "NOT NULL"),
        SURNAME("name",               "VARCHAR",  "(120)",    "NOT NULL"),
        FISCAL_CODE("name",           "VARCHAR",  "(16)",     "NOT NULL"),
        TITLE("title",                "VARCHAR",  "(200)",    "NOT NULL"),
        POPULARITY("popularity",      "SMALLINT", "",         "NOT NULL"),
        YEAR("Year",                  "INTEGER",  "",         "NOT NULL"),
        VALUE("Year",                 "INTEGER",  "",         "NOT NULL"),
        FOLLOWERS("followers",        "BIGINT",   "",         "NOT NULL"),
        DURATION("Duration_ms",       "BIGINT",   "",         "NOT NULL"),
        RELEASE_DATE("Release_date",  "DATE",     "",         "NOT NULL"),
        CREATION_DATE("Creation_date","DATE",     "",         "NOT NULL"),
        TYPE("Type",                  "VARCHAR",  "(32)",     "NOT NULL"),
        ELEMENT("Element",            "INTEGER",  "",         "NOT NULL"),
        GENERE_MUSICALE("genre",      "VARCHAR",  "(32)",     "NOT NULL"),
        COMMENTO("genre",             "VARCHAR",  "(256)",    ""),
        NICKNAME("nickname",          "VARCHAR",  "(120)",    "NOT NULL"),
        VIA_PIAZZA("Via_Piazza",      "VARCHAR",  "(120)",    "NOT NULL"),
        CIVIC_NUMER("civicNumber",    "INTEGER",  "",         "NOT NULL"),
        COUNCIL_NAME("council_name",  "VARCHAR",  "(80)",    "NOT NULL"),
        PROVINCE_NAME("province_name","VARCHAR",  "(80)",    "NOT NULL"),
        CAP("cap",                    "VARCHAR",  "(88)",     "NOT NULL");
        
        
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
        
        public String getName(){
            return this.name;
        }
        public String getType(){
            return this.type;
        }

        @Override
        public String toString() {
            return this.name + " " + this.type + this.size + " " + this.args;
        }
    }

    public static enum Tabelle 
    {
        //l'ordine dipende dal "DROP TABLE"
        GENERI_ARTISTA  ("GeneriArtista"), 
        GENERI_MUSICALI ("GeneriMusicali"), 
        SONG            ("Canzone"), 
        ARTIST          ("Artista"), 
        ALBUM           ("Album"), 
        COMMENTO        ("Commento"), 
        EMOZIONE        ("Emozione"), 
        PLAYLIST        ("Playlist"), 
        ACCOUNT         ("Account"), 
        //PROVINCIA       ("Provincia"), 
        //COMUNE          ("Comune"), 
        RESIDENZA       ("Residenza"), 
        IMAGES          ("Immagine"), 
        SONG_ARTIST     ("CanzoneArtista"), 
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


        //Lista delle colonne
        tablesAttributes.put(Tabelle.ARTIST,            new Colonne[] {Colonne.ID, Colonne.NAME, Colonne.URL, Colonne.FOLLOWERS, Colonne.POPULARITY, Colonne.IMAGE_ID_REF});
        tablesAttributes.put(Tabelle.SONG,              new Colonne[] {Colonne.ID,Colonne.TITLE,Colonne.URL, Colonne.DURATION, Colonne.POPULARITY, Colonne.YEAR, Colonne.ALBUM_ID_REF, Colonne.IMAGE_ID_REF});
        tablesAttributes.put(Tabelle.GENERI_MUSICALI,   new Colonne[] {Colonne.GENERE_MUSICALE});
        tablesAttributes.put(Tabelle.GENERI_ARTISTA,    new Colonne[] {Colonne.GENERE_MUSICALE, Colonne.ID});
        tablesAttributes.put(Tabelle.ALBUM,             new Colonne[] {Colonne.ID, Colonne.NAME, Colonne.RELEASE_DATE, Colonne.URL, Colonne.TYPE, Colonne.ELEMENT, Colonne.IMAGE_ID_REF});
        tablesAttributes.put(Tabelle.COMMENTO,          new Colonne[] {Colonne.ID, Colonne.COMMENTO, Colonne.ACCOUNT_ID_REF});
        tablesAttributes.put(Tabelle.EMOZIONE,          new Colonne[] {Colonne.ID, Colonne.TYPE, Colonne.VALUE, Colonne.SONG_ID_REF});
        tablesAttributes.put(Tabelle.PLAYLIST,          new Colonne[] {Colonne.ID, Colonne.NAME, Colonne.CREATION_DATE, Colonne.ACCOUNT_ID_REF});
        tablesAttributes.put(Tabelle.ACCOUNT,           new Colonne[] {Colonne.NAME, Colonne.NICKNAME, Colonne.SURNAME, Colonne.FISCAL_CODE, Colonne.RESIDENCE_ID_REF});
        //tablesAttributes.put(Tabelle.COMUNE,            new Colonne[] {Colonne.NAME, Colonne.CAP});
        //tablesAttributes.put(Tabelle.PROVINCIA,         new Colonne[] {Colonne.NAME});
        tablesAttributes.put(Tabelle.RESIDENZA,         new Colonne[] {Colonne.ID, Colonne.VIA_PIAZZA, Colonne.CIVIC_NUMER, Colonne.PROVINCE_NAME, Colonne.COUNCIL_NAME});
        tablesAttributes.put(Tabelle.IMAGES,            new Colonne[] {Colonne.ID, Colonne.URL, Colonne.TYPE, Colonne.IMAGE_SIZE});
        tablesAttributes.put(Tabelle.PLAYLIST_SONGS,    new Colonne[] {Colonne.PLAYLIST_ID_REF, Colonne.SONG_ID_REF});

        
        
        //lista colonne chiave primaria
        tablesPrimaryKey.put(Tabelle.ARTIST,            new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.SONG,              new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.GENERI_MUSICALI,   new Colonne[] {Colonne.GENERE_MUSICALE});
        tablesPrimaryKey.put(Tabelle.GENERI_ARTISTA,    new Colonne[] {Colonne.GENERE_MUSICALE, Colonne.ID});
        tablesPrimaryKey.put(Tabelle.ALBUM,             new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.COMMENTO,          new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.EMOZIONE,          new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.PLAYLIST,          new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.ACCOUNT,           new Colonne[] {Colonne.NICKNAME});
        //tablesPrimaryKey.put(Tabelle.COMUNE,            new Colonne[] {Colonne.NAME});
        //tablesPrimaryKey.put(Tabelle.PROVINCIA,         new Colonne[] {Colonne.NAME});
        tablesPrimaryKey.put(Tabelle.RESIDENZA,         new Colonne[] {Colonne.ID});
        tablesPrimaryKey.put(Tabelle.IMAGES,            new Colonne[] {Colonne.ID, Colonne.TYPE});
        tablesPrimaryKey.put(Tabelle.PLAYLIST_SONGS,    new Colonne[] {Colonne.PLAYLIST_ID_REF, Colonne.SONG_ID_REF});


        //lista colonne chiave esterna
        tablesForeignKey.put(Tabelle.GENERI_ARTISTA,    new Object[] {new Triplet<Colonne, Tabelle, Colonne> (Colonne.ID, Tabelle.ARTIST, Colonne.ID), new Triplet<Colonne, Tabelle, Colonne> (Colonne.GENERE_MUSICALE, Tabelle.GENERI_MUSICALI, Colonne.GENERE_MUSICALE)});
        tablesForeignKey.put(Tabelle.SONG,              new Object[] {new Triplet<Colonne, Tabelle, Colonne> (Colonne.ALBUM_ID_REF, Tabelle.ALBUM, Colonne.ID)});
        tablesForeignKey.put(Tabelle.COMMENTO,          new Object[] {new Triplet<Colonne, Tabelle, Colonne> (Colonne.ACCOUNT_ID_REF, Tabelle.ACCOUNT, Colonne.NICKNAME)});
        tablesForeignKey.put(Tabelle.EMOZIONE,          new Object[] {new Triplet<Colonne, Tabelle, Colonne> (Colonne.SONG_ID_REF, Tabelle.SONG, Colonne.ID)});
        tablesForeignKey.put(Tabelle.PLAYLIST,          new Object[] {new Triplet<Colonne, Tabelle, Colonne> (Colonne.ACCOUNT_ID_REF, Tabelle.ACCOUNT, Colonne.NICKNAME)});
        tablesForeignKey.put(Tabelle.ACCOUNT,           new Object[] {new Triplet<Colonne, Tabelle, Colonne> (Colonne.RESIDENCE_ID_REF, Tabelle.RESIDENZA, Colonne.ID)});
        tablesForeignKey.put(Tabelle.PLAYLIST_SONGS,    new Object[] {new Triplet<Colonne, Tabelle, Colonne> (Colonne.PLAYLIST_ID_REF, Tabelle.PLAYLIST, Colonne.ID), new Triplet<Colonne, Tabelle, Colonne> (Colonne.SONG_ID_REF, Tabelle.SONG, Colonne.ID)});


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

    
    

    /*protected static final Hashtable<NomiTabelle, String> insert_hashtable_query = new Hashtable<NomiTabelle, String>()  
    {
        


        {
        insert_hashtable_query.put(
            NomiTabelle.SONG_PLAYLIST,
            "INSERT INTO" + NomiTabelle.SONG_PLAYLIST.toString() + " ("
                    + "ID_Playlist, "    
                    + "Id_Song, "             
            );
        }

    };*/
        
    /**
     * @param city the city on which you want to create a view
     * @return the string of the SQL query
     */
    /*protected static String create_view_expression(String city) {
        return create_view_query[0] + city + "_users" + create_view_query[1] + "'" + city + "'";
    }*/

    /**
     * @param city the city for which you want to know the number of users
     * @return the string of the SQL query
     */
    protected static String create_num_users_query(String city) {
        return "SELECT suburb, count(*) FROM " + city + "_users GROUP BY suburb";
    }
}
