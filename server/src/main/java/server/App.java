package server;

import java.sql.SQLException;
import java.text.ParseException;
import server.Terminal;


public class App
{
    Database database = null;
    boolean databaseConnected = false;
    Terminal terminal = null;
    boolean running = false;
    Server server = null;


    public static void main( String[] args ) throws InterruptedException {
        new App(args);
    }

    public App (String[] args) throws InterruptedException {

        //Class.forName("org.postgresql.Driver");
        int attemptCount = 0;
        int attemptMAX = 10;

        this.terminal = new Terminal(this);
        terminal.printInfo_ln("Application Running...");
        terminal.printInfo_ln("Establishing database connection ");

        while ((attemptCount++ < attemptMAX) && !databaseConnected) {
            try {
                database = Database.getInstance();
                databaseConnected = database.testconnection();

            } catch (SQLException e) {
                terminal.printError_ln("connection attempt failed");
                Thread.sleep(1000);
            }
        }

        if(databaseConnected) {
            terminal.printSucces_ln("connection established");
        } else {
            terminal.printError_ln("Database not available");
        }

        
        terminal.printLine();
        terminal.start();

       


        
    }

    public void runServer() {

        if(server != null && !server.isAlive()) {
            this.server = new Server();
            server.start();
        }
    }

    public void StopServer() {
        
        if(server != null && server.isAlive()) {
            this.server = null;
        }
    }
    
}
