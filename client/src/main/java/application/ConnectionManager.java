package application;

import java.util.ArrayList;
import java.util.Optional;

import applicationEvents.ConnectionEvent;
import interfaces.ClientServices;
import interfaces.ServerServices;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

import java.beans.EventHandler;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ConnectionManager extends UnicastRemoteObject implements ClientServices {

    //Singleton pattern
    private static ConnectionManager manager;


	private String defaultHostAddress = "127.0.0.1";
	private int defaultHostPort = 8090; 

    private String hostAddress;
    private int hostPort;
    private Registry registry;
	private ServerServices stub;
	private boolean connected = false;


	Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
		if(!testServerConnection()) {
			EmotionalSongs.getInstance().stage.fireEvent(new ConnectionEvent(ConnectionEvent.DISCONNECTED));
			disconnect();
		}
	}));



    private ConnectionManager() throws RemoteException {
		super();

		hostAddress = defaultHostAddress;
		hostPort = defaultHostPort;

		if(testCustomConnection(hostAddress, hostPort)) {
            connect();
        }
    }

	
    /**
     * Class implemented with a Singleton pattern, this method is necessary to get
     * the ConnectionManager instance
     * @return instance of ConnectionManager
     * @throws RemoteException
     */
    public static ConnectionManager getConnectionManager() {
        if(manager == null)
			try {
				manager = new ConnectionManager();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

        return manager;
    }


    /**
	 * Tests a custom connection to a EmotionalSongs server
	 * @param host server's address
	 * @param port server's port
	 * @return success of the connection
	 */
	public boolean testCustomConnection(String host, int port) {
		String backupHost = hostAddress;
		int backupPort = hostPort;
		
		setConnectionData(host, port);
		boolean success = testServerConnection();
		setConnectionData(backupHost, backupPort);
		
		return success;
	}

	//qundo sono collegato
	/**
	 * Tests a connection to the EmotionalSong server
	 * @return success of the connection
	 */
	private boolean testServerConnection() {
		boolean success = true;
		
		try {
			Registry registry = LocateRegistry.getRegistry(hostAddress, hostPort);
			ServerServices test = (ServerServices) registry.lookup("EmotionalSongs_services");
			success = (test != null);
		} catch (RemoteException | NotBoundException e) {
			success = false;
		} catch (ClassCastException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}

	/**
	 * Sets new connection data to find EmotionalSong server
	 * @param host the new server's host
	 * @param port the new server's port
	 */
	public void setConnectionData(String host, int port) {
		hostAddress = host;
		hostPort = port;
	}

	/**
	 * Connects the client to server's instance of ConnectionBalancer
	 * @return success of the operation
	 */
	public boolean connect() {
		boolean connectionAvailable = testServerConnection();
		
		if(connectionAvailable) {
			try {
				registry = LocateRegistry.getRegistry(hostAddress, hostPort);
				stub = (ServerServices) registry.lookup("EmotionalSongs_services");
				stub.addClient(this);
				connected = true;

			} catch (RemoteException | NotBoundException e) {
				disconnect();
				EmotionalSongs.getInstance().showConnectionAlert();
				return false;
			}
			System.out.println("ConnectionManager - connected to EmotionalSongs_services");
		} 
				
		else {
			EmotionalSongs.getInstance().showConnectionAlert();
		}	

		timeline.setCycleCount(Timeline.INDEFINITE); // Imposta il conteggio ciclico infinito
        timeline.play();
		
	
		return connectionAvailable;
	}



	public ServerServices getService() {
		if(isConnected())
			return stub;
		else
			return null;
	}

	public boolean isConnected() {
		return connected;
	}


	/**
	 * Disconnects the client from Watchneighbours server
	 */
	public void disconnect() {
		timeline.stop();

		if(connected) {
			try {
				stub.disconnect(this);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			connected = false;
		}

		
	}
	
	/**
	 * Gets default host address 
	 * @return address default host address
	 */
	public String getDefaultHost() {
		return defaultHostAddress;
	}
	
	/**
	 * Gets default host port
	 * @return port default host port
	 */
	public int getDefaultPort() {
		return defaultHostPort;
	}
	
	/**
	 * Gets actual host address
	 * @return address actual host address
	 */
	public String getHost() {
		return hostAddress;
	}
	
	/**
	 * Gets actual host port
	 * @return port actual host port
	 */
	public int getPort() {
		return hostPort;
	}

	/* ========================== RMI methods ==========================  */

	@Override
	public void testConnection() throws RemoteException {
		System.out.println("server request");
	}

	@Override
	public void setLastUsedAccount(Object account) throws RemoteException {
		throw new UnsupportedOperationException("Unimplemented method 'setLastUsedAccount'");
	}

}


