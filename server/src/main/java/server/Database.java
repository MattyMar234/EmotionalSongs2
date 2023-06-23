package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private final String PROTOCOL = "jdbc:postgresql://";
    private final String DB_NAME = "EmotionalSongs";
    private final String HOST = "localhost";
    private final String PORT = "5432";

    private final String URL = PROTOCOL + HOST +":"+ PORT +"/"+ DB_NAME;

    private final String user = "postgres";
    private final String password = "admin";

    /*Variabili connessione DB  */
    private static Database database;
    private static Connection connection;
    private static Statement statement;

    
    private Database() throws SQLException {
        connection = DriverManager.getConnection(URL, user, password);
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    /*Metodo statico per Pattern Singleton */
    public static Database getInstance() throws SQLException 
    {
        if (database == null)
            database = new Database();

        return database;
    }

    public boolean testconnection() throws SQLException {
        return Database.connection.isValid(2); //timeout
    }


    public static Statement getStatement() {
        return statement;
    }

    public static Connection getConnection(){
        return connection;
    }

    public ResultSet submitQuery(String sql) throws SQLException {
        if(statement.execute(sql)){
            return statement.getResultSet();
        }
        return null;
    }
}
