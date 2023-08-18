package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.plaf.nimbus.State;

public class DatabaseManager {

    private final String PROTOCOL = "jdbc:postgresql://";
    private String DB_NAME  = null;
    private String HOST     = null;
    private String PORT     = null;
    private String user     = null;
    private String password = null;
    
    /*Variabili connessione DB  */
    private static DatabaseManager database;
    private static Connection connection = null;
    private static Statement statement   = null;
    
    private String URL;
    
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
    }

    public boolean connect() throws SQLException {
        connection = DriverManager.getConnection(URL, user, password);
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return true;
    }

    public void close() throws SQLException {
        if(connection == null) 
            return;

        connection.close();
    }


    public boolean testConnection() throws SQLException {
        if(connection == null) 
            return false;
        return connection.isValid(2); //timeout
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

    public Statement getStatement() {
        return statement;
    }

    public Connection getConnection(){
        return connection;
    }

    public synchronized ResultSet submitQuery(String sql) throws SQLException {
        if(statement.execute(sql)){
            return statement.getResultSet();
        }
        return null;
    }

    public ResultSet submitQuery2(String sql) throws SQLException 
    {
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
       
        if(statement.execute(sql)){
            ResultSet result = statement.getResultSet();
            statement.close();
            return result;
        }
        return null;
    }

    public void submitInsertQuery(String sql) throws SQLException {
        statement.execute(sql);
    }
}
