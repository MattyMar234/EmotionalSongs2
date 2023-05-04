package server;

import java.sql.SQLException;
import java.text.ParseException;
import server.Terminal;


public class App 
{
    public static void main( String[] args ) throws InterruptedException
    {
        new App();
    }

    public App () throws InterruptedException {

        Terminal terminal = new Terminal(this);
        terminal.start();
        terminal.printLine();

        Database db = null;
        

        try {
            //Class.forName("org.postgresql.Driver");
            db = Database.getInstance();

        } catch (SQLException e) {
            terminal.printError(e.toString());

        } catch (Exception e) {
            terminal.printError(e.toString());
        }


        while(terminal.isAlive()) {
            Thread.sleep(1000);
            terminal.printInfo("...");
        }
    }
    
}
