package database;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

import org.javatuples.Triplet;

import database.PredefinedSQLCode.Colonne;
import database.PredefinedSQLCode.Tabelle;
import database.PredefinedSQLCode.Operazioni_SQL;

/**
 * Questa classe offre dei metodi per creare delle query SQL
 */
public class QueryBuilder {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
    
}
