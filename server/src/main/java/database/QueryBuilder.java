package database;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.javatuples.Triplet;

import database.PredefinedSQLCode.Colonne;
import database.PredefinedSQLCode.Tabelle;
import objects.Album;
import server.Terminal;
import database.PredefinedSQLCode.Operazioni_SQL;

/**
 * Questa classe offre dei metodi per creare delle query SQL
 */
public class QueryBuilder {

    
    private static Terminal terminal = Terminal.getInstance();


    private static void printQuery(final StringBuilder sb) {
        new Thread(() -> {
            terminal.printQueryln(sb.toString());
        });
    }

    /**
     * Questa funzione restituisce una stringa che rappresenta la query SQL per la realizzazione della tabella specificata.
     * @param tableName nome della tabella da creare
     * @return stringa che rappresenta la query SQL
     */
    protected static String createTable_query_creator(Tabelle tableName) 
    {
        StringBuilder sb = new StringBuilder();
        Colonne[] colonne = PredefinedSQLCode.tablesAttributes.get(tableName);
        Colonne[] primaryKey = PredefinedSQLCode.tablesPrimaryKey.get(tableName);
        Object[] foreignKey = PredefinedSQLCode.tablesForeignKey.get(tableName);

        final int char_per_elemnt = 14;
        
        
        sb.append(Operazioni_SQL.CREATE.toString() + " ");
        sb.append(tableName.toString());
        sb.append("\n(");
        

        //creazione colonne
        if (colonne != null) {
            for(Colonne colonna: colonne) {
                sb.append("\n\t");
                String colElements[] = colonna.toString().split(" ");

                for(int j = 0; j < colElements.length; j++) {
                    String str = colElements[j];
                    sb.append(str);

                    if(j < 2 )
                        for(int i=str.length(); i<char_per_elemnt; i++)
                            sb.append(" ");
                    else {
                         sb.append(" ");
                    }
                }
                sb.deleteCharAt(sb.length()-1);
                sb.append(", ");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length()-1);
        }
        
        //creazione chiavi primarie
        if(primaryKey != null) {
            sb.append(",\n\n\tPRIMARY KEY (");
            for(Colonne colonna: primaryKey) {
                sb.append(colonna.getName());
                sb.append(", ");
            } 
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length()-1);
            sb.append(")");
        }

        //creazione chiavi esterne
        if(foreignKey != null && foreignKey.length > 0) {
            sb.append(", ");
            for(Object reference: foreignKey) {
                sb.append("\n\t");
                Triplet<Colonne, Tabelle,Colonne> temp = (Triplet<Colonne, Tabelle,Colonne>) reference;
                sb.append("FOREIGN KEY (" + temp.getValue0().getName() + ") REFERENCES " + temp.getValue1() + "(" + temp.getValue2().getName() + ") ON DELETE CASCADE");
                sb.append(", ");
            } 
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length()-1);
        }


        sb.append("\n);\n");
        return sb.toString();
    }


    public static String insert_query_creator(final Tabelle tableName, HashMap<Colonne, Object> colonne) {

        Colonne[] TableColonne = PredefinedSQLCode.tablesAttributes.get(tableName);
        Object[] data = new Object[colonne.size()];
        int i = 0;

        //devo disporre gli elementi in ordine
        for (Colonne coll : TableColonne) {
            data[i++] = colonne.get(coll);
            //System.out.println("coll: " + coll.getName() + " -> " + colonne.get(coll));
        }

        return insert_query_creator(tableName, data);
    }

    /**
     * genera la query per la creazione e inserimento di un entry nella tabella
     * @param tableName nome della tabella in cui inserire la nuova entry
     * @param dati lista dei valori degli attributi
     */
    public static String insert_query_creator(final Tabelle tableName, final Object... dati) 
    {
        StringBuilder sb = new StringBuilder();
        Colonne[] colonne = PredefinedSQLCode.tablesAttributes.get(tableName);

        if(dati.length > colonne.length)
            throw new IllegalArgumentException("too many parameters for insert query");
        if(dati.length < colonne.length)
            throw new IllegalArgumentException("few parameters for insert query.\nparametre: " + dati.length);


        sb.append(Operazioni_SQL.INSERT.toString() + " ");
        sb.append(tableName.toString());
        
        sb.append("(");
        for(Colonne colonna: colonne) {
            sb.append(colonna.getName());
            sb.append(", ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(") VALUES (");

        for(int i=0; i<dati.length; i++) {
            //System.out.println(colonne[i].name()  + "  " + colonne[i].getType());
            try {
                switch (colonne[i].getType()) {
                    case "INTEGER":          if (dati[i] instanceof Integer)    sb.append((int) dati[i]);                   else throw new ClassCastException(); break;
                    case "INT":              if (dati[i] instanceof Integer)    sb.append((int) dati[i]);                   else throw new ClassCastException(); break;
                    case "SMALLINT":         if (dati[i] instanceof Integer)    sb.append((int) dati[i]);                 else throw new ClassCastException(); break;
                    case "TINYINT":          if (dati[i] instanceof Integer)    sb.append((int) dati[i]);                  else throw new ClassCastException(); break;
                    case "DECIMAL":          if (dati[i] instanceof BigDecimal) sb.append((BigDecimal) dati[i]);            else throw new ClassCastException(); break;
                    case "NUMERIC":          if (dati[i] instanceof BigDecimal) sb.append((BigDecimal) dati[i]);            else throw new ClassCastException(); break;
                    case "FLOAT":            if (dati[i] instanceof Float)      sb.append((float) dati[i]);                 else throw new ClassCastException(); break;
                    case "DOUBLE PRECISION": if (dati[i] instanceof Double)     sb.append((double) dati[i]);                else throw new ClassCastException(); break;
                    case "CHAR":             if (dati[i] instanceof Character)  sb.append("'" + (char) dati[i] + "'");      else throw new ClassCastException(); break;
                    case "VARCHAR":          if (dati[i] instanceof String)     sb.append("'" + ((String) dati[i]).replace('\'', ' ') + "'");    else throw new ClassCastException(); break;
                    case "TEXT":             if (dati[i] instanceof String)     sb.append("'" + ((String) dati[i]).replace('\'', ' ') + "'");    else throw new ClassCastException(); break;
                    case "TIME":             
                    case "TIMESTAMP":
                    case "DATE":             
                        if (dati[i] instanceof String) {
                            String date = ((String) dati[i]);

                            for(int j = 1; j < 3; j++) {
                                if(date.split("-").length == j) {
                                    String val = Integer.toString((int)(Math.random()*11*j + 1*j));
                                    date +=   (val.length() < 2 ? "-0" + val : "-" +val);
                                }
                            }

                            String yearString = date.substring(0, 4);
                            int year = Integer.parseInt(yearString);

                            if (year < 1500) {
                                yearString = "2000";
                            }

                            String modifiedDate = yearString + date.substring(4);
                    
                            sb.append("'" + modifiedDate + "'");  
                            break;    
                        }
                        else {
                            throw new ClassCastException(); 
                        }

                    case "BOOLEAN":          
                        if (dati[i] instanceof Boolean) {
                            sb.append((Boolean) dati[i]);    
                            break;
                        }
                        else 
                            throw new ClassCastException(); 

                    case "BIGINT":           
                        try {
                            if (dati[i] instanceof Long) 
                                sb.append((long) dati[i]);                      
                            else
                                throw new ClassCastException(); break;
                        }
                        catch (java.lang.ClassCastException e) {
                            sb.append((int) dati[i]);
                            break;
                        }                                                
                    default:
                        throw new IllegalArgumentException("Unsupported SQL type: " + colonne[i].getType());
                } 
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e + "class found:" + dati[i].getClass().getName());
                System.exit(0);
            } 
            sb.append(", ");
        }

        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(");");
        return sb.toString();
    }

    


    public static String addColumn(Tabelle tabella, Colonne colonna) 
    {
        StringBuilder sb = new StringBuilder();
        Colonne[] colls = PredefinedSQLCode.tablesPrimaryKey.get(tabella);
        boolean found = false;

        //cerco se è tra le chiavi primarie
        for (Colonne c : colls) {
            if (c.equals(colonna)) {
                found = true;
                break;
            }
        }

        sb.append("ALTER TABLE ");
        sb.append(tabella.toString());
        sb.append(" ADD COLUMN ");
        sb.append(colonna.toString());

        if(found) {


            sb.append(", ADD CONSTRAINT ");
            sb.append(colonna.name());
            sb.append(" UNIQUE (");
            sb.append(colonna.name());
            sb.append(");");

            
        }
        
        return sb.toString();
    }


    public static String dropColumn(Tabelle tabella, Colonne colonna) {
        
        //alter table account DROP COLUMN element
        return "";
    }

    

    


    //================================================ SINGOLI ELEMENTI =================================================//



    //================================================ LISTE DI ELEMENTI =================================================//
    public static String getResidenceId_Query(String via, int numero, String comune, String provincia) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(PredefinedSQLCode.Colonne.ID.getName());
        sb.append(" FROM  ");
        sb.append(PredefinedSQLCode.Tabelle.RESIDENZA);
        sb.append(" WHERE  ");
        sb.append(PredefinedSQLCode.Colonne.VIA_PIAZZA.getName() + " = '" + via + "'");
        sb.append(" AND ");
        sb.append(PredefinedSQLCode.Colonne.CIVIC_NUMER.getName() + " = '" + numero + "'");
        sb.append(" AND ");
        sb.append(PredefinedSQLCode.Colonne.COUNCIL_NAME.getName() + " = '" + comune + "'");
        sb.append(" AND ");
        sb.append(PredefinedSQLCode.Colonne.PROVINCE_NAME.getName() + " = '" + provincia + "'");
    
        printQuery(sb);
        return sb.toString();
    }

    
    
    public static String getAccountByEmail_query(String Email) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(Tabelle.ACCOUNT);
        sb.append(" a JOIN ");
        sb.append(Tabelle.RESIDENZA);
        sb.append(" r ON a." + Colonne.RESIDENCE_ID_REF.getName() + " = r." + Colonne.ID.getName());
        sb.append(" WHERE ");
        sb.append(Colonne.EMAIL.getName() + " = '" + Email + "';");

        printQuery(sb);
        return sb.toString();

    }

    public static String getAccountByNickname_query(String nickname) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(Tabelle.ACCOUNT);
        sb.append(" a JOIN ");
        sb.append(Tabelle.RESIDENZA);
        sb.append(" r ON a." + Colonne.RESIDENCE_ID_REF.getName() + " = r." + Colonne.ID.getName());
        sb.append(" WHERE ");
        sb.append(Colonne.NICKNAME.getName() + " = '" + nickname + "';");

        printQuery(sb);
        return sb.toString();
    }

    /**
     * Genera la query per ricavare i generi musicali dell'artista
     * @param id l'id dell'artista
     * @param artistInformation se True aggiunge ai risultati i datim dell'artista
     * @return
     */
    public static String getArtistGeners_query(final String id, final boolean artistInformation) {
        return "SELECT "+ (artistInformation ? '*' : PredefinedSQLCode.Colonne.GENERE_MUSICALE) + " FROM " + PredefinedSQLCode.Tabelle.GENERI_ARTISTA + " NATURAL JOIN "
        + "WHERE " + PredefinedSQLCode.Tabelle.GENERI_ARTISTA + "."+PredefinedSQLCode.Colonne.ID + " = " + id;
    //select * from generiartista NATURAL JOIN artista WHERE generiartista.id = '66CXWjxzNUsdJxJ2JdwvnR';
    }
    
    

    public static String getSongs_by_AlbumID_query(String albumID) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c.* FROM  " + Tabelle.SONG + " c JOIN " + Tabelle.ALBUM + " a ON c.ID_Album = a.ID WHERE a.ID = \'" + albumID + "\'");
        
        //terminal.printQuery_ln(sb.toString());
        return sb.toString();
    }


    public static String getAlbumImages_by_ID(String ID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + PredefinedSQLCode.Tabelle.ALBUM_IMAGES.toString());
        sb.append(" WHERE " + PredefinedSQLCode.Colonne.ID.getName() + " = '" + ID +"';");

        //terminal.printQuery_ln(sb.toString());
        return sb.toString();
    }

    /**
     * Genera la query che ritorna le canzoni con gli ID specificati
     * @param IDs
     * @return
     */
    public static String getSongByID_query(String[] IDs) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + PredefinedSQLCode.Tabelle.SONG.toString());
        sb.append(" WHERE " );

        for (int i = 0; i < IDs.length; i++) {
            sb.append(PredefinedSQLCode.Colonne.ID.getName() + " = '" + IDs[i] + (i < IDs.length - 1 ? "' OR " : "';"));
        } 

        //terminal.printQuery_ln(sb.toString());
        return sb.toString();
    }

    /**
     * Genera la query per ottenere tutte le canzoni di un album
     * @param ID l'ID dell'album
     * @return
     */
    public static String getAlbumSongs_query(String ID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + PredefinedSQLCode.Tabelle.SONG.toString());
        sb.append(" WHERE " + PredefinedSQLCode.Colonne.ALBUM_ID_REF.getName() + " = '" + ID +"';");

        //printQuery(sb);
        return sb.toString();
    }





    //================================================ OPERAZIONI PARTICOLARI =================================================//
    public static String getRecentPublischedAlbum_query(long limit, long offset, int threshold) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(Tabelle.ALBUM + " a WHERE ");
        sb.append(" a." + Colonne.ID.getName() + " IN (SELECT "+ Colonne.ID.getName());
        sb.append(" FROM ");  
        sb.append(Tabelle.ALBUM); 
        sb.append(" WHERE " + Colonne.ELEMENT.getName() + " >= " + threshold + " ORDER BY " + Colonne.RELEASE_DATE.getName() + " DESC) ");      
        sb.append(" ORDER BY a." + Colonne.RELEASE_DATE.getName() + " DESC LIMIT " + limit + " OFFSET " + offset + ";");
        
        //terminal.printQuery_ln(sb.toString());
        return sb.toString();

    }


    //================================================ OPERAZIONI DI RICERCA =================================================//
    public static String getSongSearch_query(String search, long limit, long offset) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c.* ");
        sb.append("FROM " + Tabelle.SONG + " c JOIN " + Tabelle.ALBUM + " a ON c."+ Colonne.ALBUM_ID_REF.getName() + " = a."+ Colonne.ID.getName());
        sb.append(" WHERE c." + Colonne.TITLE.getName() + " LIKE '" + search + "%'");
        //sb.append(" OR a."+ Colonne.RELEASE_DATE.getName() +" LIKE '" + search + "%'");   
        sb.append(" ORDER BY c." + Colonne.TITLE.getName() + ", a." + Colonne.RELEASE_DATE.getName());   
        sb.append(" LIMIT " + limit + " OFFSET " + offset + ";");

        //printQuery(sb);
        return sb.toString();
    }

    public static String getAlbumSearch_query(String search, long limit, long offset) 
    {
        
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT a.* ");
        sb.append("FROM " + Tabelle.ALBUM + " a ");
        sb.append("WHERE a." + Colonne.NAME.getName() + " LIKE '" + search + "%'");
        //sb.append(" OR a." + Colonne.RELEASE_DATE.getName() + " LIKE '" + search + "%'");
        sb.append(" ORDER BY a." + Colonne.NAME.getName() + ", a." + Colonne.RELEASE_DATE.getName());
        sb.append(" LIMIT " + limit + " OFFSET " + offset + ";");

        //printQuery(sb);
        return sb.toString();

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OPERAZIONI SULLE PLAYLIST
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Query per creare una nuova playlist
     * @param accountID
     * @param playlistName
     * @param date
     * @param ID
     * @return
     */
    public static String addPlaylist_query(String accountID,String playlistName, String date, String ID)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO " + Tabelle.PLAYLIST + " (");
        sb.append(Colonne.ID.getName() + ", ");
        sb.append(Colonne.NAME.getName() + ", ");
        sb.append(Colonne.CREATION_DATE.getName() + ", ");
        sb.append(Colonne.ACCOUNT_ID_REF.getName());
        sb.append(") VALUES (");
        sb.append("'" + ID + "', ");
        sb.append("'" + playlistName + "', ");
        sb.append("'" + date + "', ");
        sb.append("'" + accountID + "');");
        
        //printQuery(sb);
        return sb.toString();
    }

    /**
     * Query per ottenere tutte le canzoni di una playlist
     * @param playlistID
     * @return
     */
    public static String getPlaylistSongsID_query(String playlistID) 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT " + Tabelle.SONG + "." + Colonne.ID.getName());
        sb.append(" FROM " + Tabelle.SONG);
        sb.append(" JOIN " + Tabelle.PLAYLIST_SONGS);
        sb.append(" ON " + Tabelle.SONG + "." + Colonne.ID.getName() + " = " + Tabelle.PLAYLIST_SONGS + "." + Colonne.SONG_ID_REF.getName());
        sb.append(" WHERE " + Tabelle.PLAYLIST_SONGS + "." + Colonne.PLAYLIST_ID_REF.getName() + " = '" + playlistID + "';");
        
        printQuery(sb);
        return sb.toString();
    }

    /**
     * Query per ottenere tutte le playlist di un account
     * @param accountID
     * @return
     */
    public static String getAccountsPlaylists_query(String accountID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.PLAYLIST + " WHERE " + Colonne.ACCOUNT_ID_REF.getName() + " = '" + accountID + "';");
        
        printQuery(sb);
        return sb.toString();
    }

    public static String addSongToPlaylist_query(String playlistID, String songID) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO " + Tabelle.PLAYLIST_SONGS + " (");
        sb.append(Colonne.PLAYLIST_ID_REF.getName() + ", ");
        sb.append(Colonne.SONG_ID_REF.getName() + ") VALUES (");
        sb.append("'" + playlistID + "', ");
        sb.append("'" + songID + "');");

        //printQuery(sb);
        return sb.toString();
    }

    public static String removeSongFromPlaylist_query(String playlistID, String songID) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + Tabelle.PLAYLIST_SONGS + " WHERE ");
        sb.append(Colonne.PLAYLIST_ID_REF.getName() + " = '" + playlistID + "' AND ");
        sb.append(Colonne.SONG_ID_REF.getName() + " = '" + songID + "';");

        //printQuery(sb);
        return sb.toString();
    }

    public static String renamePlaylist_query(String playlistID, String newName) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE " + Tabelle.PLAYLIST + " SET ");
        sb.append(Colonne.NAME.getName() + " = '" + newName + "' WHERE ");
        sb.append(Colonne.ID.getName() + " = '" + playlistID + "';");

        //printQuery(sb);
        return sb.toString();
    }

    public static String addComment_query(String accountID, String songID, String comment) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO " + Tabelle.COMMENTO + " (");
        sb.append(Colonne.ACCOUNT_ID_REF.getName() + ", ");
        sb.append(Colonne.SONG_ID_REF.getName() + ", ");
        sb.append(Colonne.COMMENTO.getName() + ") VALUES (");
        sb.append("'" + accountID + "', ");
        sb.append("'" + songID + "', ");
        sb.append("'" + comment + "');");

        //printQuery(sb);
        return sb.toString();
    }

    public static String deleteComment_query(String commentID) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + Tabelle.COMMENTO + " WHERE ");
        sb.append(Colonne.ID.getName() + " = '" + commentID + "';");

        //printQuery(sb);
        return sb.toString();
    }

    public static String getAccountSongComment_query(String songID, String accountID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.COMMENTO + " WHERE ");
        sb.append(Colonne.SONG_ID_REF.getName() + " = '" + songID + "'AND");
        sb.append(Colonne.ACCOUNT_ID_REF.getName() + " = '" + accountID + "';");


        //printQuery(sb);
        return sb.toString();
    }

    public static String getSongComment_query(String songID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.COMMENTO + " WHERE ");
        sb.append(Colonne.SONG_ID_REF.getName() + " = '" + songID + "';");

        //printQuery(sb);
        return sb.toString();
    }

    public static String getAccountComment_query(String accountID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.COMMENTO + " WHERE ");
        sb.append(Colonne.ACCOUNT_ID_REF.getName() + " = '" + accountID + "';");

        //printQuery(sb);
        return sb.toString();
    }

    public static String getSongEmotion_query(String songID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.EMOZIONE + " WHERE ");
        sb.append(Colonne.SONG_ID_REF.getName() + " = '" + songID + "';");

        //printQuery(sb);
        return sb.toString();
    }

    public static String deletePlaylist_query(String playlistID) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + Tabelle.PLAYLIST + " WHERE ");
        sb.append(Colonne.ID.getName() + " = '" + playlistID + "';");

        //printQuery(sb);
        return sb.toString();
    }

    public static String deleteAccount_query(String accountID) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + Tabelle.ACCOUNT + " WHERE ");
        sb.append(Colonne.ID.getName() + " = '" + accountID + "';");

        //printQuery(sb);
        return sb.toString();
    }

    public static String getArtistSong_query(String artistID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.SONG + " WHERE ");
        sb.append(Colonne.ARTIST_ID_REF.getName() + " = '" + artistID + "';");

        //printQuery(sb);
        return sb.toString();
    }

    public static String getPlaylistSong_query(String playlistID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.SONG + " WHERE ");
        sb.append(Colonne.ID.getName() + " IN (SELECT " + Colonne.SONG_ID_REF.getName() + " FROM " + Tabelle.PLAYLIST_SONGS + " WHERE ");
        sb.append(Colonne.PLAYLIST_ID_REF.getName() + " = '" + playlistID + "');");

        //printQuery(sb);
        return sb.toString();
    }

    public static String getAlbumByID_query(String ID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.ALBUM + " WHERE ");
        sb.append(Colonne.ID.getName() + " = '" + ID + "';");

        //printQuery(sb);
        return sb.toString();
    }

    public static String getArtistAlbum_query(String artistID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.ALBUM + " WHERE ");
        sb.append(Colonne.ARTIST_ID_REF.getName() + " = '" + artistID + "';");

        //printQuery(sb);
        return sb.toString();
    }

   

    

}
