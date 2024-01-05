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
public class QueryBuilder 
{
    private static Terminal terminal = Terminal.getInstance();
    private static boolean queryDebug = false;



/**
 * Imposta la modalità di debug per le query dinamiche.
 *
 * @param mode true per attivare la modalità di debug, false per disattivarla.
 */
    public static void setQueryDebug(boolean mode) {
        QueryBuilder.queryDebug = mode;
        Terminal.getInstance().printInfoln(mode ? "DynamicQueryDebug: true" : "DynamicQueryDebug: false");
    }



/**
 * Stampa una query sulla console in modalità di debug asincrona.
 *
 * @param sb StringBuilder contenente la query da stampare.
 */
    private static void printQuery(final StringBuilder sb) {
        
        if(!QueryBuilder.queryDebug)
            return;
        
        new Thread(() -> {
            terminal.printQueryln(sb.toString());
        }).start();
    }



/**
 * Stampa una query sulla console in modalità di debug asincrona.
 *
 * @param s String contenente la query da stampare.
 */
    private static void printQuery(final String s) {
        
        if(!QueryBuilder.queryDebug)
            return;
        
        new Thread(() -> {
            terminal.printQueryln(s);
        }).start();
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


/**
 * Questa funzione genera la query per inserire un elemento in una tabella
 * @param tableName Nome della tabella
 * @return La stringa che rappresenta la query
 */
    public static String insert_query_creator(final Tabelle tableName, HashMap<Colonne, Object> informazioni)
    {
        Colonne[] colonne = PredefinedSQLCode.tablesAttributes.get(tableName);
        Object[] dati = new Object[informazioni.size()];
        int i = 0;

        //devo disporre gli elementi in ordine
        for (Colonne coll : colonne) {
            dati[i++] = informazioni.get(coll);
        }

        String s = insert_query_creator(tableName, dati);
        printQuery(s);
        return s;
    }



/**
 * Converte e aggiunge un valore al tipo specificato in un oggetto StringBuilder.
 *
 * Questo metodo gestisce la conversione di un valore di un determinato tipo di colonna SQL
 * e lo aggiunge a uno StringBuilder nel formato corretto per la costruzione di una query SQL.
 *
 * @param sb    StringBuilder a cui aggiungere il valore convertito.
 * @param type  Tipo di colonna SQL a cui il valore deve essere convertito.
 * @param obj   Oggetto contenente il valore da convertire e aggiungere.
 *
 * @throws IllegalArgumentException Se il tipo di colonna SQL non è supportato.
 */
    private static void convert_and_addType(StringBuilder sb, Colonne type, Object obj)
    {
        switch (type.getType()) 
        {
            case "INTEGER":          
            case "INT":              
            case "SMALLINT":         
            case "TINYINT":          
                sb.append((int) obj);
                break;

            case "BIGINT":    
                sb.append((long) obj);
                break;   

            case "DECIMAL":          
            case "NUMERIC":          
                sb.append((BigDecimal) obj);
                break;

            case "FLOAT":   
            case "DOUBLE PRECISION":
                sb.append((float) obj);
                break;      

            case "CHAR":              
                sb.append("'" + (char) obj + "'");
                break;

            case "VARCHAR":  
            case "TEXT":             
                sb.append("'" + ((String) obj).replace('\'', ' ') + "'");    
                break;

            case "BOOLEAN":          
                sb.append((Boolean) obj);    
                break;

            case "TIME":             
            case "TIMESTAMP":
            case "DATE":             

                String date = ((String) obj);

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
                                                        
            default:
                throw new IllegalArgumentException("Unsupported SQL type: " + type.getType());
        }
    }



/**
 * Crea e restituisce una stringa di query di inserimento SQL per una tabella specifica
 * con i dati forniti.
 *
 * Questo metodo genera dinamicamente una query di inserimento SQL in base ai dati forniti
 * e restituisce la stringa risultante.
 *
 * @param tableName Nome della tabella SQL in cui eseguire l'inserimento.
 * @param dati      Dati da inserire nella tabella, nell'ordine delle colonne.
 * @return          Stringa della query di inserimento SQL generata.
 *
 * @throws IllegalArgumentException Se il numero di parametri è diverso dal numero di colonne nella tabella.
 */
    protected static String insert_query_creator(final Tabelle tableName, final Object... dati) 
    {
        Colonne[] colonne = PredefinedSQLCode.tablesAttributes.get(tableName);
        StringBuilder sb = new StringBuilder();
        
        //verifico se ho il numero corretto di parametri
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

        for(int i = 0; i < dati.length; i++) {
            try {
                convert_and_addType(sb, colonne[i], dati[i]);
            } 
            catch (Exception e) {
                e.printStackTrace();

                if(dati[i] != null)
                    System.out.println(e + "class found:" + dati[i].getClass().getName());

                System.out.println("Colonna: " + colonne[i] + " => value: " + dati[i]);
                System.exit(0);
            } 
            sb.append(", ");
        }

        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(");");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query di esportazione SQL per una tabella specifica
 * in formato CSV con intestazioni, utilizzando il comando COPY.
 *
 * Questo metodo genera dinamicamente una query di esportazione SQL in formato CSV
 * per la tabella specificata e restituisce la stringa risultante.
 *
 * @param table Nome della tabella SQL da esportare.
 * @param path  Percorso del file CSV in cui esportare i dati.
 * @return      Stringa della query di esportazione SQL generata.
 */
    public static String exportQuery(String table, String path) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("COPY ");
        sb.append(table);
        sb.append(" TO ");
        sb.append("\'" + path + "\'");
        sb.append(" DELIMITER ',' CSV HEADER;");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query di importazione SQL per una tabella specifica
 * da un file CSV con intestazioni, utilizzando il comando COPY.
 *
 * Questo metodo genera dinamicamente una query di importazione SQL da un file CSV
 * per la tabella specificata e restituisce la stringa risultante.
 *
 * @param table          Nome della tabella SQL in cui importare i dati.
 * @param path           Percorso del file CSV da cui importare i dati.
 * @param headerFileCSV  Intestazioni del file CSV da corrispondere con le colonne della tabella.
 * @return               Stringa della query di importazione SQL generata.
 */
    public static String importQuery(String table, String path, String headerFileCSV) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("COPY ");
        sb.append(table);
        sb.append(" (");
        sb.append(headerFileCSV);
        sb.append(") FROM ");
        sb.append("\'" + path + "\'");
        sb.append(" DELIMITER ',' CSV HEADER;");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query di eliminazione SQL per una tabella specifica
 * utilizzando le chiavi primarie.
 *
 * Questo metodo genera dinamicamente una query di eliminazione SQL per la tabella specificata
 * utilizzando le chiavi primarie fornite come argomenti e restituisce la stringa risultante.
 *
 * @param tabella Nome della tabella SQL da cui eliminare i dati.
 * @param keys    Valori delle chiavi primarie per identificare il record da eliminare.
 * @return        Stringa della query di eliminazione SQL generata.
 */
    public static String deleteQueryCreator_by_primaryKey(Tabelle tabella, Object...keys)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + tabella + " WHERE ");

        Colonne[] pkeyColl = PredefinedSQLCode.tablesPrimaryKey.get(tabella);

        for(int i = 0; i < pkeyColl.length; i++) {
            sb.append(pkeyColl[i].getName() + " = " );
            convert_and_addType(sb, pkeyColl[i], keys[i]);

            if(i == pkeyColl.length - 1)
                break;

            sb.append(" AND ");
        }
        sb.append(";");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query di eliminazione SQL per una tabella specifica
 * utilizzando colonne personalizzate come chiavi di confronto.
 *
 * Questo metodo genera dinamicamente una query di eliminazione SQL per la tabella specificata
 * utilizzando le colonne e i valori forniti come argomenti per identificare il record da eliminare
 * e restituisce la stringa risultante.
 *
 * @param tabella Nome della tabella SQL da cui eliminare i dati.
 * @param coll    Colonne da utilizzare come chiavi di confronto per l'eliminazione.
 * @param key     Valori corrispondenti alle colonne per identificare il record da eliminare.
 * @return        Stringa della query di eliminazione SQL generata.
 * @throws RuntimeException Se una o più colonne specificate non sono presenti nella tabella.
 */
    public static String deleteQueryCreator_custom_key(Tabelle tabella, Colonne[] coll, Object[] key)
    {
        boolean allFound = true;
        Colonne[] tableColums = PredefinedSQLCode.tablesAttributes.get(tabella);
        
        for(int i = 0; i < coll.length; i++) {
            boolean found = false;
            for(Colonne c : tableColums) {
                if(c.equals(coll[i])) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                allFound = false;
                break;
            }
        }

        if(!allFound) {
            throw new RuntimeException("La tabella" + tabella + " non contiene le colonne richieste");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + tabella + " WHERE ");

        for(int i = 0; i < coll.length; i++) 
        {
            sb.append(coll[i].getName() + " = " );
            convert_and_addType(sb, coll[i], key[i]);

            if(i == coll.length - 1)
                break;

            sb.append(" AND ");
        }
        sb.append(";");
        
        printQuery(sb);
        return sb.toString();
    }

    

/**
 * Crea e restituisce una stringa di query SQL per aggiungere una colonna a una tabella specifica.
 *
 * Questo metodo genera dinamicamente una query SQL per aggiungere una nuova colonna
 * alla tabella specificata e restituisce la stringa risultante.
 *
 * @param tabella Nome della tabella SQL a cui aggiungere la colonna.
 * @param colonna Colonna da aggiungere alla tabella.
 * @return        Stringa della query SQL generata per aggiungere la colonna.
 */
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



/**
 * Crea e restituisce una stringa di query SQL per rimuovere una colonna da una tabella specifica.
 *
 * Questo metodo genera dinamicamente una query SQL per rimuovere una colonna
 * dalla tabella specificata e restituisce la stringa risultante.
 *
 * @param tabella Nome della tabella SQL da cui rimuovere la colonna.
 * @param colonna Colonna da rimuovere dalla tabella.
 * @return        Stringa della query SQL generata per rimuovere la colonna.
 */
    public static String dropColumn(Tabelle tabella, Colonne colonna) {
        
        //alter table account DROP COLUMN element
        return "";
    }

    

    


    //================================================ SINGOLI ELEMENTI =================================================//



    //================================================ LISTE DI ELEMENTI =================================================//

/**
 * Crea e restituisce una stringa di query SQL per ottenere l'ID di una residenza in base agli attributi specificati.
 *
 * Questo metodo genera dinamicamente una query SQL per selezionare l'ID di una residenza
 * in base alla via, al numero civico, al comune e alla provincia specificati.
 *
 * @param via       Nome della via o piazza della residenza.
 * @param numero    Numero civico della residenza.
 * @param comune    Nome del comune della residenza.
 * @param provincia Nome della provincia della residenza.
 * @return          Stringa della query SQL generata per ottenere l'ID della residenza.
 */
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

    

/**
 * Crea e restituisce una stringa di query SQL per ottenere un account in base all'indirizzo email specificato.
 *
 * Questo metodo genera dinamicamente una query SQL per selezionare tutti i campi da
 * una tabella di account e la relativa residenza in base all'indirizzo email fornito.
 *
 * @param Email Indirizzo email dell'account da cercare.
 * @return      Stringa della query SQL generata per ottenere l'account in base all'indirizzo email.
 */
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



/**
 * Crea e restituisce una stringa di query SQL per ottenere un account in base al nickname specificato.
 *
 * Questo metodo genera dinamicamente una query SQL per selezionare tutti i campi da
 * una tabella di account e la relativa residenza in base al nickname fornito.
 *
 * @param nickname Nickname dell'account da cercare.
 * @return         Stringa della query SQL generata per ottenere l'account in base al nickname.
 */
    public static String getAccountByNickname_query(String nickname) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(Tabelle.ACCOUNT);
        sb.append(" a JOIN ");
        sb.append(Tabelle.RESIDENZA);
        sb.append(" r ON a." + Colonne.RESIDENCE_ID_REF.getName() + " = r." + Colonne.ID.getName());
        sb.append(" WHERE ");
        sb.append(" a." + Colonne.NICKNAME.getName() + " = '" + nickname + "';");

        printQuery(sb);
        return sb.toString();
    }

   

/**
 * Crea e restituisce una stringa di query SQL per ottenere i generi musicali di un artista.
 *
 * Questo metodo genera dinamicamente una query SQL per selezionare i generi musicali
 * di un artista dalla tabella dei generi dell'artista in base all'ID specificato.
 *
 * @param id                  ID dell'artista per il quale si desiderano ottenere i generi.
 * @param artistInformation  Se true, seleziona tutti i campi dell'artista, altrimenti seleziona solo il genere musicale.
 * @return                    Stringa della query SQL generata per ottenere i generi musicali di un artista.
 */
    public static String getArtistGeners_query(final String id, final boolean artistInformation) {
        return "SELECT "+ (artistInformation ? '*' : PredefinedSQLCode.Colonne.GENERE_MUSICALE) + " FROM " + PredefinedSQLCode.Tabelle.GENERI_ARTISTA + " NATURAL JOIN "
        + "WHERE " + PredefinedSQLCode.Tabelle.GENERI_ARTISTA + "."+PredefinedSQLCode.Colonne.ID + " = " + id;
    //select * from generiartista NATURAL JOIN artista WHERE generiartista.id = '66CXWjxzNUsdJxJ2JdwvnR';
    }
    
    

/**
 * Crea e restituisce una stringa di query SQL per ottenere le canzoni di un determinato album.
 *
 * Questo metodo genera dinamicamente una query SQL per selezionare tutte le colonne della tabella delle canzoni
 * in base all'ID dell'album specificato.
 *
 * @param albumID ID dell'album per il quale si desiderano ottenere le canzoni.
 * @return        Stringa della query SQL generata per ottenere le canzoni di un determinato album.
 */
    public static String getSongs_by_AlbumID_query(String albumID) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c.* FROM  " + Tabelle.SONG + " c JOIN " + Tabelle.ALBUM + " a ON c.ID_Album = a.ID WHERE a.ID = \'" + albumID + "\'");
        
        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere le immagini di un album dato il suo ID.
 *
 * Questo metodo genera dinamicamente una query SQL per selezionare tutte le colonne della tabella delle immagini dell'album
 * in base all'ID specificato.
 *
 * @param ID ID dell'album per il quale si desiderano ottenere le immagini.
 * @return   Stringa della query SQL generata per ottenere le immagini di un album.
 */
    public static String getAlbumImages_by_ID(String ID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + PredefinedSQLCode.Tabelle.ALBUM_IMAGES.toString());
        sb.append(" WHERE " + PredefinedSQLCode.Colonne.ID.getName() + " = '" + ID +"';");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere le immagini di un artista dato il suo ID.
 *
 * Questo metodo genera dinamicamente una query SQL per selezionare tutte le colonne della tabella delle immagini dell'artista
 * in base all'ID specificato.
 *
 * @param ID ID dell'artista per il quale si desiderano ottenere le immagini.
 * @return   Stringa della query SQL generata per ottenere le immagini di un artista.
 */
    public static String getArtistImages_by_ID(String ID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + PredefinedSQLCode.Tabelle.ARTIST_IMAGES.toString());
        sb.append(" WHERE " + PredefinedSQLCode.Colonne.ID.getName() + " = '" + ID +"';");

        printQuery(sb);
        return sb.toString();
    }


   
/**
 * Crea e restituisce una stringa di query SQL per ottenere le informazioni delle canzoni dato un array di ID.
 *
 * Questo metodo genera dinamicamente una query SQL per selezionare tutte le colonne della tabella delle canzoni
 * in base agli ID specificati nell'array.
 *
 * @param IDs Array di ID delle canzoni per le quali si desiderano ottenere le informazioni.
 * @return    Stringa della query SQL generata per ottenere le informazioni delle canzoni dato un array di ID.
 */
    public static String getSongByID_query(String[] IDs) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + PredefinedSQLCode.Tabelle.SONG.toString());
        sb.append(" WHERE " );

        for (int i = 0; i < IDs.length; i++) {
            sb.append(PredefinedSQLCode.Colonne.ID.getName() + " = '" + IDs[i] + (i < IDs.length - 1 ? "' OR " : "';"));
        } 

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere le informazioni delle canzoni di un determinato album.
 *
 * Questo metodo genera dinamicamente una query SQL per selezionare tutte le colonne della tabella delle canzoni
 * in base all'ID dell'album specificato.
 *
 * @param ID ID dell'album per il quale si desiderano ottenere le informazioni delle canzoni.
 * @return   Stringa della query SQL generata per ottenere le informazioni delle canzoni di un determinato album.
 */
    public static String getAlbumSongs_query(String ID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + PredefinedSQLCode.Tabelle.SONG.toString());
        sb.append(" WHERE " + PredefinedSQLCode.Colonne.ALBUM_ID_REF.getName() + " = '" + ID +"';");

        printQuery(sb);
        return sb.toString();
    }





    //================================================ OPERAZIONI PARTICOLARI =================================================//
/**
 * Crea e restituisce una stringa di query SQL per ottenere gli album recentemente pubblicati.
 *
 * Questo metodo genera dinamicamente una query SQL per selezionare tutte le colonne della tabella degli album
 * in base a un criterio di pubblicazione recente, con un limite e un offset specificati.
 *
 * @param limit     Limite di risultati da restituire.
 * @param offset    Offset per la query per paginazione.
 * @param threshold Soglia minima per la pubblicazione dell'album.
 * @return          Stringa della query SQL generata per ottenere gli album recentemente pubblicati.
 */
    public static String getRecentPublischedAlbum_query(long limit, long offset, int threshold) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(Tabelle.ALBUM + " a WHERE ");
        sb.append(" a." + Colonne.ID.getName() + " IN (SELECT "+ Colonne.ID.getName());
        sb.append(" FROM ");  
        sb.append(Tabelle.ALBUM); 
        sb.append(" WHERE " + Colonne.ELEMENT.getName() + " >= " + threshold + " ORDER BY " + Colonne.RELEASE_DATE.getName() + " DESC) ");      
        sb.append(" ORDER BY a." + Colonne.RELEASE_DATE.getName() + " DESC LIMIT " + limit + " OFFSET " + offset + ";");
        
        printQuery(sb);
        return sb.toString();

    }


    //================================================ OPERAZIONI DI RICERCA =================================================//
/**
 * Crea e restituisce una stringa di query SQL per cercare canzoni in base a un criterio specifico.
 *
 * Questo metodo genera dinamicamente una query SQL per cercare canzoni in base al tipo di ricerca specificato
 * (0 per il titolo, 1 per la data di rilascio) con un limite e un offset specificati.
 *
 * @param search La stringa da cercare nella ricerca delle canzoni.
 * @param limit  Limite di risultati da restituire.
 * @param offset Offset per la query per paginazione.
 * @param mode   Modalità di ricerca (0 per il titolo, 1 per la data di rilascio).
 * @return       Stringa della query SQL generata per cercare le canzoni.
 */
    public static String getSongSearch_query(String search, long limit, long offset, int mode) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c.* ");
        
        if(mode == 2) {

            sb.append("FROM " + Tabelle.SONG + " c LEFT JOIN " + Tabelle.EMOZIONE + " e ON c."+ Colonne.ID.getName() + " = e."+ Colonne.SONG_ID_REF.getName());
            sb.append(" WHERE e." + Colonne.ID.getName() + " IS NOT NULL");
            sb.append(" GROUP BY c." + Colonne.ID.getName() + ", c." + Colonne.TITLE.getName());
            sb.append(" ORDER BY COUNT(e."+ Colonne.ID.getName() + ") DESC");
        }
        else 
        {
            sb.append("FROM " + Tabelle.SONG + " c JOIN " + Tabelle.ALBUM + " a ON c."+ Colonne.ALBUM_ID_REF.getName() + " = a."+ Colonne.ID.getName());
        
            switch (mode) 
            {
                //NAME
                case 0:
                    sb.append(" WHERE c." + Colonne.TITLE.getName() + " LIKE '" + search + "%'");
                    break;

                //DATE
                case 1:
                    //sb.append(" WHERE CAST(a." + Colonne.RELEASE_DATE.getName() + " AS VARCHAR) LIKE '" + search + "%'");
                    sb.append(" WHERE a." + Colonne.RELEASE_DATE.getName() + " LIKE '" + search + "%'");
                    //sb.append(" ORDER BY a." + Colonne.RELEASE_DATE.getName() + " DESC");
                    break;
            
                //NAME
                default:
                    sb.append(" WHERE c." + Colonne.TITLE.getName() + " LIKE '" + search + "%'");
                    break;
            }
        }
        
        //sb.append(" ORDER BY c." + Colonne.TITLE.getName() + ", a." + Colonne.RELEASE_DATE.getName());   
        sb.append(" LIMIT " + limit + " OFFSET " + offset + ";");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere il conteggio delle canzoni in base a un criterio specifico.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere il conteggio delle canzoni in base al tipo di ricerca specificato
 * (0 per il titolo, 1 per la data di rilascio).
 *
 * @param search La stringa da cercare nella ricerca delle canzoni.
 * @param mode   Modalità di ricerca (0 per il titolo, 1 per la data di rilascio).
 * @return       Stringa della query SQL generata per ottenere il conteggio delle canzoni.
 */
    public static String getSongSearch_Count_query(String search, int mode) {
        StringBuilder sb = new StringBuilder();
        
        if(mode == 2) {

            sb.append("SELECT COUNT(*)");
            sb.append("FROM (");
            sb.append("    SELECT ");
            sb.append("        " + Tabelle.SONG + "." + Colonne.ID.getName() + ", ");
            sb.append("        " + Tabelle.SONG + "." + Colonne.TITLE.getName() + ", ");
            sb.append("        COUNT(" + Tabelle.EMOZIONE + "." + Colonne.ID.getName() + ") AS num_comments ");
            sb.append("    FROM ");
            sb.append("        " + Tabelle.SONG + " ");
            sb.append("    LEFT JOIN ");
            sb.append("        " + Tabelle.EMOZIONE + " ON " + Tabelle.SONG + "." + Colonne.ID.getName() + " = " + Tabelle.EMOZIONE + "." + Colonne.SONG_ID_REF.getName() + " ");
            sb.append("    WHERE ");
            sb.append("        " + Tabelle.EMOZIONE + "." + Colonne.ID.getName() + " IS NOT NULL ");
            sb.append("    GROUP BY ");
            sb.append("        " + Tabelle.SONG + "." + Colonne.ID.getName() + ", " + Tabelle.SONG + "." + Colonne.TITLE.getName() + " ");
            sb.append(") AS grouped_data;");

            printQuery(sb);
            return sb.toString();
        }
        
        
        sb.append("SELECT count(c.*) ");
        sb.append("FROM " + Tabelle.SONG + " c JOIN " + Tabelle.ALBUM + " a ON c."+ Colonne.ALBUM_ID_REF.getName() + " = a."+ Colonne.ID.getName());
        
        switch (mode) 
        {
            //NAME
            case 0:
                sb.append(" WHERE c." + Colonne.TITLE.getName() + " LIKE '" + search + "%'");
                break;

            //DATE
            case 1:
                sb.append(" WHERE CAST(a." + Colonne.RELEASE_DATE.getName() + " AS VARCHAR) LIKE '" + search + "%'");
                //sb.append(" ORDER BY a." + Colonne.RELEASE_DATE.getName() + " DESC");
                break;
        
            //NAME
            default:
                sb.append(" WHERE c." + Colonne.TITLE.getName() + " LIKE '" + search + "%'");
                break;
        }
        sb.append(";");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per cercare album in base a un criterio di ricerca.
 *
 * Questo metodo genera dinamicamente una query SQL per cercare album in base al nome dell'album con un limite e un offset specificati.
 *
 * @param search La stringa da cercare nella ricerca degli album.
 * @param limit  Limite di risultati da restituire.
 * @param offset Offset per la query per paginazione.
 * @return       Stringa della query SQL generata per cercare gli album.
 */
    public static String getAlbumSearch_query(String search, long limit, long offset) 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * ");
        sb.append("FROM " + Tabelle.ALBUM + " ");
        sb.append("WHERE " + Colonne.NAME.getName() + " LIKE '" + search + "%'");
        sb.append(" ORDER BY " + Colonne.RELEASE_DATE.getName() + " DESC");
        sb.append(" LIMIT " + limit + " OFFSET " + offset + ";");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere il conteggio degli album in base a un criterio di ricerca.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere il conteggio degli album in base al nome dell'album.
 *
 * @param search La stringa da cercare nella ricerca degli album.
 * @return       Stringa della query SQL generata per ottenere il conteggio degli album.
 */
    public static String getAlbumSearch_Count_query(String search) 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(*) ");
        sb.append("FROM " + Tabelle.ALBUM + " ");
        sb.append("WHERE " + Colonne.NAME.getName() + " LIKE '" + search + "%';");
        //sb.append(" ORDER BY " + Colonne.RELEASE_DATE.getName() + " DESC;");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere un artista in base all'ID specificato.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere un artista in base all'ID dell'artista.
 *
 * @param id L'ID dell'artista da cercare.
 * @return   Stringa della query SQL generata per ottenere un artista.
 */
    public static String getArtistByID_query(String id) {
       StringBuilder sb = new StringBuilder();
        sb.append("SELECT * ");
        sb.append("FROM " + Tabelle.ARTIST + " ");
        sb.append("WHERE " + Colonne.ID.getName() + " = '" + id + "';");
        
        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per cercare artisti in base a un criterio di ricerca.
 *
 * Questo metodo genera dinamicamente una query SQL per cercare artisti in base al nome dell'artista con un limite e un offset specificati.
 *
 * @param search La stringa da cercare nella ricerca degli artisti.
 * @param limit  Limite di risultati da restituire.
 * @param offset Offset per la query per paginazione.
 * @return       Stringa della query SQL generata per cercare gli artisti.
 */
    public static String searchArtist_query(String search, long limit, long offset) {
       StringBuilder sb = new StringBuilder();
        sb.append("SELECT * ");
        sb.append(" FROM " + Tabelle.ARTIST);
        sb.append(" WHERE " + Colonne.NAME.getName() + " LIKE '" + search + "%'");
        sb.append(" ORDER BY " + Colonne.FOLLOWERS.getName() + " DESC");
        sb.append(" LIMIT " + limit + " OFFSET " + offset + ";");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere il conteggio degli artisti in base a un criterio di ricerca.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere il conteggio degli artisti in base al nome dell'artista.
 *
 * @param search La stringa da cercare nella ricerca degli artisti.
 * @return       Stringa della query SQL generata per ottenere il conteggio degli artisti.
 */
    public static String searchArtist_Count_query(String search) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(*)");
        sb.append(" FROM " + Tabelle.ARTIST);
        sb.append(" WHERE " + Colonne.NAME.getName() + " LIKE '" + search + "%';");
        //sb.append(" ORDER BY " + Colonne.FOLLOWERS.getName() + " DESC;");

        printQuery(sb);
        return sb.toString();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OPERAZIONI SULLE PLAYLIST
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * Crea e restituisce una stringa di query SQL per aggiungere una playlist nel database.
 *
 * Questo metodo genera dinamicamente una query SQL per inserire una nuova playlist nel database.
 *
 * @param accountID    L'ID dell'account associato alla playlist.
 * @param playlistName Il nome della playlist da aggiungere.
 * @param date         La data di creazione della playlist.
 * @param ID           L'ID univoco della playlist.
 * @return             Stringa della query SQL generata per aggiungere una playlist.
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
        
        printQuery(sb);
        return sb.toString();
    }

    

/**
 * Crea e restituisce una stringa di query SQL per ottenere gli ID delle canzoni in una determinata playlist.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere gli ID delle canzoni presenti in una specifica playlist.
 *
 * @param playlistID L'ID della playlist per cui ottenere gli ID delle canzoni.
 * @return           Stringa della query SQL generata per ottenere gli ID delle canzoni di una playlist.
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
 * Crea e restituisce una stringa di query SQL per ottenere le playlist di un account specifico.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere tutte le playlist associate a un account.
 *
 * @param accountID L'ID dell'account per cui ottenere le playlist.
 * @return           Stringa della query SQL generata per ottenere le playlist di un account.
 */
    public static String getAccountsPlaylists_query(String accountID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.PLAYLIST + " WHERE " + Colonne.ACCOUNT_ID_REF.getName() + " = '" + accountID + "';");
        
        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per aggiungere una canzone a una playlist.
 *
 * Questo metodo genera dinamicamente una query SQL per inserire una relazione tra una playlist e una canzone nel database.
 *
 * @param playlistID L'ID della playlist a cui aggiungere la canzone.
 * @param songID     L'ID della canzone da aggiungere alla playlist.
 * @return           Stringa della query SQL generata per aggiungere una canzone a una playlist.
 */
    public static String addSongToPlaylist_query(String playlistID, String songID) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO " + Tabelle.PLAYLIST_SONGS + " (");
        sb.append(Colonne.PLAYLIST_ID_REF.getName() + ", ");
        sb.append(Colonne.SONG_ID_REF.getName() + ") VALUES (");
        sb.append("'" + playlistID + "', ");
        sb.append("'" + songID + "');");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per rimuovere una canzone da una playlist.
 *
 * Questo metodo genera dinamicamente una query SQL per rimuovere una relazione tra una playlist e una canzone nel database.
 *
 * @param playlistID L'ID della playlist da cui rimuovere la canzone.
 * @param songID     L'ID della canzone da rimuovere dalla playlist.
 * @return           Stringa della query SQL generata per rimuovere una canzone da una playlist.
 */
    public static String removeSongFromPlaylist_query(String playlistID, String songID) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + Tabelle.PLAYLIST_SONGS + " WHERE ");
        sb.append(Colonne.PLAYLIST_ID_REF.getName() + " = '" + playlistID + "' AND ");
        sb.append(Colonne.SONG_ID_REF.getName() + " = '" + songID + "';");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per rinominare una playlist.
 *
 * Questo metodo genera dinamicamente una query SQL per aggiornare il nome di una playlist nel database.
 *
 * @param playlistID L'ID della playlist da rinominare.
 * @param newName    Il nuovo nome da assegnare alla playlist.
 * @return           Stringa della query SQL generata per rinominare una playlist.
 */
    public static String renamePlaylist_query(String playlistID, String newName) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE " + Tabelle.PLAYLIST + " SET ");
        sb.append(Colonne.NAME.getName() + " = '" + newName + "' WHERE ");
        sb.append(Colonne.ID.getName() + " = '" + playlistID + "';");

        printQuery(sb);
        return sb.toString();
    }


   
/**
 * Crea e restituisce una stringa di query SQL per ottenere le emozioni associate a una canzone.
 *
 * Questo metodo genera dinamicamente una query SQL per recuperare le emozioni associate a una specifica canzone.
 *
 * @param songID L'ID della canzone per cui ottenere le emozioni.
 * @return        Stringa della query SQL generata per ottenere le emozioni di una canzone.
 */
    public static String getSongEmotion_query(String songID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.EMOZIONE + " WHERE ");
        sb.append(Colonne.SONG_ID_REF.getName() + " = '" + songID + "';");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per eliminare una playlist dal database.
 *
 * Questo metodo genera dinamicamente una query SQL per eliminare una playlist dal database.
 *
 * @param playlistID L'ID della playlist da eliminare.
 * @return           Stringa della query SQL generata per eliminare una playlist.
 */
    public static String deletePlaylist_query(String playlistID) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + Tabelle.PLAYLIST + " WHERE ");
        sb.append(Colonne.ID.getName() + " = '" + playlistID + "';");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per eliminare un account dal database.
 *
 * Questo metodo genera dinamicamente una query SQL per eliminare un account dal database.
 *
 * @param accountID L'ID dell'account da eliminare.
 * @return           Stringa della query SQL generata per eliminare un account.
 */
    public static String deleteAccount_query(String accountID) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + Tabelle.ACCOUNT + " WHERE ");
        sb.append(Colonne.NICKNAME.getName() + " = '" + accountID + "';");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere le canzoni di un determinato artista.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere tutte le canzoni di un artista specifico.
 *
 * @param artistID L'ID dell'artista per cui ottenere le canzoni.
 * @return         Stringa della query SQL generata per ottenere le canzoni di un artista.
 */
    public static String getArtistSong_query(String artistID) {
        StringBuilder sb = new StringBuilder();
        //sb.append("SELECT * FROM " + Tabelle.SONG + " WHERE ");
        //sb.append(Colonne.ARTIST_ID_REF.getName() + " = '" + artistID + "';");

        //select c.* from canzone c join album a on c.id_album = a.id where a.id_artist = '3TqjDuqT3xvg7YlNh4JWp5'
        sb.append("SELECT c.* FROM " + Tabelle.SONG + " c JOIN " + Tabelle.ALBUM + " a ");
        sb.append("ON c." + Colonne.ALBUM_ID_REF.getName() + " = a." + Colonne.ID.getName());
        sb.append(" WHERE a." + Colonne.ARTIST_ID_REF.getName() + " = '" + artistID + "';");


        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere le canzoni di una playlist.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere tutte le canzoni associate a una specifica playlist.
 *
 * @param playlistID L'ID della playlist per cui ottenere le canzoni.
 * @return           Stringa della query SQL generata per ottenere le canzoni di una playlist.
 */
    public static String getPlaylistSong_query(String playlistID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.SONG + " WHERE ");
        sb.append(Colonne.ID.getName() + " IN (SELECT " + Colonne.SONG_ID_REF.getName() + " FROM " + Tabelle.PLAYLIST_SONGS + " WHERE ");
        sb.append(Colonne.PLAYLIST_ID_REF.getName() + " = '" + playlistID + "');");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere le informazioni di un album tramite l'ID.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere le informazioni di un album specifico usando l'ID.
 *
 * @param ID L'ID dell'album per cui ottenere le informazioni.
 * @return   Stringa della query SQL generata per ottenere le informazioni di un album.
 */
    public static String getAlbumByID_query(String ID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.ALBUM + " WHERE ");
        sb.append(Colonne.ID.getName() + " = '" + ID + "';");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere gli album di un determinato artista.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere tutti gli album di un artista specifico.
 *
 * @param artistID L'ID dell'artista per cui ottenere gli album.
 * @return         Stringa della query SQL generata per ottenere gli album di un artista.
 */
    public static String getArtistAlbum_query(String artistID) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.ALBUM + " WHERE ");
        sb.append(Colonne.ARTIST_ID_REF.getName() + " = '" + artistID + "';");

        printQuery(sb);
        return sb.toString();
    }



/**
 * Crea e restituisce una stringa di query SQL per ottenere le emozioni associate a un account.
 *
 * Questo metodo genera dinamicamente una query SQL per ottenere le emozioni associate a un account specifico.
 *
 * @param accountID L'ID dell'account per cui ottenere le emozioni.
 * @return          Stringa della query SQL generata per ottenere le emozioni di un account.
 */
    public static String getAccountEmotions(String accountID)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Tabelle.EMOZIONE + " WHERE ");
        sb.append(Colonne.ACCOUNT_ID_REF.getName() + " = '" + accountID + "';");

        printQuery(sb);
        return sb.toString();
    }

    /*=======================================[Utility]=======================================*/
/**
 * Crea e restituisce una stringa di query SQL per modificare la dimensione di una colonna in una tabella.
 *
 * Questo metodo genera dinamicamente una query SQL per modificare la dimensione di una colonna specifica in una tabella specifica.
 *
 * @param table La tabella in cui si desidera modificare la colonna.
 * @param colum La colonna che si desidera modificare.
 * @return      Stringa della query SQL generata per modificare la dimensione di una colonna.
 */
    public static String editColumSize(PredefinedSQLCode.Tabelle table, PredefinedSQLCode.Colonne colum) 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE " + table.toString());
        sb.append(" ALTER COLUMN " + colum.getName());
        sb.append(" TYPE " + colum.getType_and_Size() + ";");

        return sb.toString();
    }
   

    

}
