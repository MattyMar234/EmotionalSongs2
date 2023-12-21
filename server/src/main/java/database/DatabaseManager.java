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
    
    /*Metodo statico per Pattern Singleton */
    public static DatabaseManager getInstance() 
    {
        if (database == null)
            database = new DatabaseManager();

        return database;
    }

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

    public boolean connect() throws SQLException {
        //connection = DriverManager.getConnection(URL, user, password);
        connection = DriverManager.getConnection(URL, jdbcOptions);
        //statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return true;
    }

    public void closeConnection() throws SQLException {
        if(connection == null) 
            return;

        connection.close();
        connection = null;
    }


    public boolean testConnection() throws SQLException {
        if(connection == null) 
            return false;
        return connection.isValid(2); //timeout
    }

    private Statement createStatement() throws SQLException {
        return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }



    public ResultSet submitQuery(String sql) throws SQLException 
    {
        Statement statement = createStatement();
        if(statement.execute(sql)){
            return statement.getResultSet();
        }
        return null;
    }

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

    public void submitInsertQuery(String sql) throws SQLException {
        
        Statement statement = createStatement();
        statement.execute(sql);
    }



    public String getURL() {
        return URL;
    }

    public String getDB_NAME() {
        return DB_NAME;
    }

    public String getHOST() {
        return HOST;
    }

    public String getPORT() {
        return PORT;
    }

    public String getUser() {
        return user;
    }

    

    public Connection getConnection(){
        return connection;
    }

    public boolean isConnected() {
        return connection != null;
    }

}
