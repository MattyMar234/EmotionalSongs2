package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private final String PROTOCOL = "jdbc:postgresql://";
    private String DB_NAME  = null;
    private String HOST     = null;
    private String PORT     = null;
    private String user     = null;
    private String password = null;
    
    /*Variabili connessione DB  */
    private static Database database;
    private static Connection connection = null;
    private static Statement statement   = null;
    
    private String URL;
    
    private Database(){

    }

    public void setConnection(String db_name, String host, int port, String user, String password) throws SQLException {
        this.DB_NAME = db_name;
        this.HOST = host;
        this.PORT = Integer.toString(port);
        this.user = user;
        this.password = password;
        this.URL = PROTOCOL + HOST +":"+ PORT +"/"+ DB_NAME;
        
        connection = DriverManager.getConnection(URL, user, password);
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    /*Metodo statico per Pattern Singleton */
    public static Database getInstance() 
    {
        if (database == null)
            database = new Database();

        return database;
    }

    public boolean testconnection() throws SQLException {
        if(connection == null) 
            return false;

        return Database.connection.isValid(2); //timeout
    }


    public static Statement getStatement() {
        return statement;
    }

    public static Connection getConnection(){
        return connection;
    }

    public synchronized ResultSet submitQuery(String sql) throws SQLException {
        if(statement.execute(sql)){
            return statement.getResultSet();
        }
        return null;
    }

    public void submitInsertQuery(String sql) throws SQLException {
        statement.execute(sql);
    }
}
