package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class DatabaseManager {

    private final String PROTOCOL = "jdbc:postgresql://";
    private String DB_NAME  = null;
    private String HOST     = null;
    private String PORT     = null;
    private String user     = null;
    private String password = null;
    private String URL;
    private Properties jdbcOptions;
    
    /*Variabili connessione DB  */
    private static DatabaseManager database;
    private static Connection connection = null;
    //private static Statement statement   = null;

    
    private DatabaseManager(){
        
    }
    
    /*Metodo statico per Pattern Singleton*/

/**
 * Restituisce un'istanza singleton della classe DatabaseManager.
 *
 * Questo metodo assicura che esista una sola istanza della classe DatabaseManager
 * durante l'esecuzione dell'applicazione. Se l'istanza non esiste, viene creata
 * e restituita; in caso contrario, viene restituita l'istanza esistente.
 *
 * @return Un'istanza singleton della classe DatabaseManager.
 */
    public static DatabaseManager getInstance() 
    {
        if (database == null)
            database = new DatabaseManager();

        return database;
    }



/**
 * Imposta i parametri di connessione per il DatabaseManager.
 *
 * Questo metodo consente di impostare i parametri necessari per la connessione al database,
 * come il nome del database, l'host, la porta, l'utente e la password. Inoltre, calcola e imposta
 * l'URL di connessione utilizzando i parametri forniti. Vengono anche configurate le opzioni JDBC
 * tramite un oggetto Properties.
 *
 * @param db_name Nome del database.
 * @param host Host del database.
 * @param port Porta del database.
 * @param user Nome utente per la connessione al database.
 * @param password Password per la connessione al database.
 */
    public void setConnectionParametre(String db_name, String host, int port, String user, String password) {
        this.DB_NAME = db_name;
        this.HOST = host;
        this.PORT = Integer.toString(port);
        this.user = user;
        this.password = password;
        this.URL = PROTOCOL + HOST +":"+ PORT +"/"+ DB_NAME;  

        jdbcOptions = new Properties();
        jdbcOptions.put("user", user);
        jdbcOptions.put("password", this.password);
        /*jdbcOptions.put("serverTimezone", "UTC");
        jdbcOptions.put("useSSL", "false");
        jdbcOptions.put("useUnicode", "true");
        jdbcOptions.put("characterEncoding", "UTF-8");
        jdbcOptions.put("autoReconnect", "true");
        jdbcOptions.put("failOverReadOnly", "false");
        jdbcOptions.put("maxReconnects", "10");
        jdbcOptions.put("minReconnectInterval", "1");
        jdbcOptions.put("maxIdleTime", "1");
        jdbcOptions.put("statementCacheSize", "0");
        jdbcOptions.put("socketTimeout", "0");
        jdbcOptions.put("connectTimeout", "0");
        jdbcOptions.put("keepAlive", "false");
        jdbcOptions.put("tcpKeepAlive", "false");
        jdbcOptions.put("applicationName", "EmotionalSongs");
        jdbcOptions.put("allowPublicKeyRetrieval", "true");*/
        jdbcOptions.put("MultipleActiveResultSets", "true");
    }


    
/**
 * Stabilisce una connessione al database utilizzando i parametri precedentemente configurati.
 *
 * Questo metodo tenta di stabilire una connessione al database utilizzando l'URL e le opzioni JDBC
 * configurate. Se la connessione riesce con successo, restituisce true; altrimenti, solleva un'eccezione
 * di tipo SQLException.
 *
 * @return True se la connessione al database ha avuto successo, altrimenti solleva un'eccezione SQLException.
 * @throws SQLException Eccezione sollevata in caso di errore durante la connessione al database.
 */
    public boolean connect() throws SQLException {
        //connection = DriverManager.getConnection(URL, user, password);
        connection = DriverManager.getConnection(URL, jdbcOptions);
        //statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return true;
    }



/**
 * Chiude la connessione al database, se è attualmente aperta.
 *
 * Questo metodo chiude la connessione al database se è attualmente aperta. Verifica
 * se la connessione è diversa da null prima di tentare di chiuderla. Dopo la chiusura,
 * imposta il riferimento alla connessione a null.
 *
 * @throws SQLException Eccezione sollevata in caso di errore durante la chiusura della connessione.
 */
    public void closeConnection() throws SQLException {
        if(connection == null) 
            return;

        connection.close();
        connection = null;
    }



/**
 * Verifica la validità della connessione al database.
 *
 * Questo metodo verifica se la connessione al database è attualmente aperta e se è valida.
 * Utilizza il metodo `isValid` della connessione con un timeout specificato. Restituisce
 * true se la connessione è valida, altrimenti restituisce false. Se la connessione è null,
 * viene restituito false.
 *
 * @return True se la connessione è valida, altrimenti restituisce false.
 * @throws SQLException Eccezione sollevata in caso di errore durante il test di validità della connessione.
 */
    public boolean testConnection() throws SQLException {
        if(connection == null) 
            return false;
        return connection.isValid(2); //timeout
    }



/**
 * Crea e restituisce un oggetto Statement associato alla connessione corrente.
 *
 * Questo metodo crea un nuovo oggetto Statement associato alla connessione corrente.
 * L'oggetto Statement è configurato per restituire risultati di tipo scroll insensitive e
 * concorrenza di sola lettura. Restituisce l'oggetto Statement appena creato.
 *
 * @return Un oggetto Statement associato alla connessione corrente.
 * @throws SQLException Eccezione sollevata in caso di errore durante la creazione dell'oggetto Statement.
 */
    private Statement createStatement() throws SQLException {
        return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }



/**
 * Esegue una query SQL e restituisce il risultato come un oggetto ResultSet.
 *
 * Questo metodo crea un nuovo oggetto Statement utilizzando il metodo privato `createStatement`,
 * esegue la query SQL fornita e restituisce il risultato sotto forma di oggetto ResultSet.
 * Se la query restituisce un risultato, viene restituito l'oggetto ResultSet corrispondente;
 * altrimenti, restituisce null. L'oggetto Statement viene chiuso automaticamente dopo l'esecuzione della query.
 *
 * @param sql La query SQL da eseguire.
 * @return Un oggetto ResultSet contenente il risultato della query, o null se la query non restituisce risultati.
 * @throws SQLException Eccezione sollevata in caso di errori durante l'esecuzione della query.
 */
    public ResultSet submitQuery(String sql) throws SQLException 
    {
        Statement statement = createStatement();
        if(statement.execute(sql)){
            return statement.getResultSet();
        }
        return null;
    }



/**
 * Esegue una query SQL e restituisce il risultato come un oggetto ResultSet.
 *
 * Questo metodo crea un nuovo oggetto Statement utilizzando il metodo privato `createStatement`,
 * esegue la query SQL fornita e restituisce il risultato sotto forma di oggetto ResultSet.
 * Dopo l'ottenimento del risultato, chiude immediatamente l'oggetto Statement per rilasciare le risorse.
 * Se la query restituisce un risultato, viene restituito l'oggetto ResultSet corrispondente;
 * altrimenti, restituisce null.
 *
 * @param sql La query SQL da eseguire.
 * @return Un oggetto ResultSet contenente il risultato della query, o null se la query non restituisce risultati.
 * @throws SQLException Eccezione sollevata in caso di errori durante l'esecuzione della query o la chiusura dell'oggetto Statement.
 */
    public ResultSet submitQuery2(String sql) throws SQLException 
    {
        Statement statement = createStatement();
       
        if(statement.execute(sql)){
            ResultSet result = statement.getResultSet();
            statement.close();
            return result;
        }
        return null;
    }



/**
 * Esegue una query SQL di tipo INSERT, UPDATE o DELETE.
 *
 * Questo metodo crea un nuovo oggetto Statement utilizzando il metodo privato `createStatement`,
 * esegue la query SQL fornita, che deve essere di tipo INSERT, UPDATE o DELETE, e non restituisce
 * alcun risultato. L'oggetto Statement viene chiuso automaticamente dopo l'esecuzione della query.
 *
 * @param sql La query SQL di tipo INSERT, UPDATE o DELETE da eseguire.
 * @throws SQLException Eccezione sollevata in caso di errori durante l'esecuzione della query.
 */
    public void submitInsertQuery(String sql) throws SQLException {
        
        Statement statement = createStatement();
        statement.execute(sql);
    }



/**
 * Restituisce l'URL di connessione al database.
 *
 * Questo metodo restituisce l'URL di connessione al database, che è stato precedentemente
 * configurato utilizzando i parametri di connessione come il nome del database, l'host, la porta, ecc.
 *
 * @return L'URL di connessione al database.
 */
    public String getURL() {
        return URL;
    }



/**
 * Restituisce il nome del database.
 *
 * Questo metodo restituisce il nome del database, che è stato precedentemente configurato
 * utilizzando i parametri di connessione come parte della configurazione della connessione al database.
 *
 * @return Il nome del database.
 */
    public String getDB_NAME() {
        return DB_NAME;
    }



/**
 * Restituisce l'host del database.
 *
 * Questo metodo restituisce l'host del database, che è stato precedentemente configurato
 * utilizzando i parametri di connessione come parte della configurazione della connessione al database.
 *
 * @return L'host del database.
 */
    public String getHOST() {
        return HOST;
    }



/**
 * Restituisce la porta del database.
 *
 * Questo metodo restituisce la porta del database, che è stata precedentemente configurata
 * utilizzando i parametri di connessione come parte della configurazione della connessione al database.
 *
 * @return La porta del database.
 */
    public String getPORT() {
        return PORT;
    }



/**
 * Restituisce il nome utente per la connessione al database.
 *
 * Questo metodo restituisce il nome utente utilizzato per la connessione al database,
 * che è stato precedentemente configurato come parte della configurazione della connessione al database.
 *
 * @return Il nome utente per la connessione al database.
 */
    public String getUser() {
        return user;
    }

    

/**
 * Restituisce l'oggetto Connection attualmente utilizzato.
 *
 * Questo metodo restituisce l'oggetto Connection attualmente in uso per la connessione al database.
 *
 * @return L'oggetto Connection attualmente utilizzato, o null se la connessione non è attiva.
 */
    public Connection getConnection(){
        return connection;
    }



/**
 * Verifica se la connessione al database è attualmente attiva.
 *
 * Questo metodo restituisce true se la connessione al database è attiva (non è null),
 * altrimenti restituisce false.
 *
 * @return True se la connessione al database è attiva, altrimenti false.
 */
    public boolean isConnected() {
        return connection != null;
    }

}
