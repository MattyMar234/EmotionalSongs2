package application.client;


import java.util.ArrayList;

public class ConnectionManager {

    //Singleton pattern
    private static ConnectionManager manager;

    private String hostAddress;
    private int hostPort;
    //private Registry registry;

    private ConnectionManager() {

    }

    /**
     * Class implemented with a Singleton pattern, this method is necessary to get
     * the ConnectionManager instance
     * @return instance of ConnectionManager
     */
    public static ConnectionManager getConnectionManager() {
        if(manager == null)
            manager = new ConnectionManager();

        return manager;
    }

}
