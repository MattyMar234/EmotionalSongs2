package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import Exceptions.InvalidEmailException;
import Exceptions.InvalidPasswordException;
import Exceptions.InvalidUserNameException;
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
import objects.Account;
import objects.Album;
import objects.Song;
import objects.Packet;
import utility.PathFormatter;

import java.beans.EventHandler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ConnectionManager {

    //Singleton pattern
    private static ConnectionManager manager;

	private String defaultHostAddress = "127.0.0.1";
	private int defaultHostPort = 8090; 

    private String hostAddress;
    private int hostPort;
	private boolean connected = false;

	private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
	private Socket clientSocket;
	private HashMap<String, Object> requestResult = new HashMap<>();
	private int resultToWait = 0;
	private PacketLintener_thread packetLintener;


	

	private class PacketLintener_thread extends Thread 
	{
		public PacketLintener_thread() {
			start();
		}

		@Override
		public void run() {
			while(true) {try {waitForPacket(); getPacket();} catch (Exception e) {e.printStackTrace();}}
		}
	}


	private synchronized void waitForPacket() 
	{
		//se non ho nulla da attendere vado in wait
		while(resultToWait == 0) {
			try {wait();} catch (InterruptedException e) {}
		}
	}

	private void getPacket() throws ClassNotFoundException, IOException 
	{
		try {
			String id = (String) inputStream.readObject();
			Object result = inputStream.readObject();

			//salvo il risultato e avviso
			synchronized(this) {
				requestResult.put(id, result);
				resultToWait--;
				notifyAll();
			}
		} catch (Exception e) {
			synchronized(this) {
				disconnect();
				notifyAll();
			}
			EmotionalSongs.getInstance().stage.fireEvent(new ConnectionEvent(ConnectionEvent.DISCONNECTED));
			
		}
		
	}

    private ConnectionManager() throws RemoteException {

		hostAddress = defaultHostAddress;
		hostPort = defaultHostPort;
		packetLintener = new PacketLintener_thread();

		if(testCustomConnection(hostAddress, hostPort)) {
            connect();
        }

		new Thread(() -> {
			try {Thread.sleep(1000);	} catch (InterruptedException e) {}
			
			while(true) {
				if(isConnected()) {
					testServerConnection();
				}
			}
		}).start();

		/*Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
		
	}));*/
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
	public boolean testCustomConnection(String host, int port) 
	{
		host.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "");

		if(PathFormatter.isUnix() || PathFormatter.isMac()) {
			if(host.split("\\.").length != 4 ) {
				return false;
			}
		}
		else {
			if(host.split("\\.").length != 4 ) {
				return false;
			}
		}
		
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
	public synchronized boolean testServerConnection() {
		boolean result = false;

		try {
			if(isConnected() && clientSocket != null) {
				result = testConnection();
			} 
		} catch (Exception e) {
			result = false;
			//e.printStackTrace();
			System.out.println(e);
		}
		return result;
	}

	/**
	 * Sets new connection data to find EmotionalSong server
	 * @param host the new server's host
	 * @param port the new server's port
	 */
	public boolean setConnectionData(String host, int port) {

		host = host.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "");

		if(PathFormatter.isUnix() || PathFormatter.isMac()) {
			if(host.split("\\.").length != 4 ) {
				return false;
			}
		}
		else {
			if(host.split("\\.").length != 4 ) {
				return false;
			}
		}

		hostAddress = host;
		hostPort = port;
		return true;
	}

	/**
	 * Connects the client to server's instance of ConnectionBalancer
	 * @return success of the operation
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public synchronized boolean connect() 
	{
		//verifico che non sono già collegato
		if(isConnected())
			return true;
			
		try 
		{
			//provo a creare un socket e i relativi streams.
			//se va a buon fine, vuoldire che mi sono collegato al server
			Socket clientSocket  = new Socket(hostAddress, hostPort);
			this.clientSocket = clientSocket;

			this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			this.inputStream = new ObjectInputStream(clientSocket.getInputStream());

			
			System.out.println("ConnectionManager - connected to EmotionalSongs_services");
			//timeline.setCycleCount(Timeline.INDEFINITE); // Imposta il conteggio ciclico infinito
			//timeline.play();
			connected = true;
			notifyAll();
			return true;

		} catch (Exception e) {
			connected = false;
			System.out.println(e);
			//e.printStackTrace();
			return false;
		}
	}


	public synchronized boolean isConnected() {
		return connected;
	}


	/**
	 * Disconnects the client from Watchneighbours server
	 */
	public synchronized void disconnect() 
	{

		//timeline.stop();
		connected = false;
		try {
			this.CloseComunication();
		} catch (Exception e) {
			System.out.println(e);
			//e.printStackTrace();
		}
		
		resultToWait = 0;
		requestResult.clear();
		clientSocket = null;
		
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
	public String getAddress() {
		return hostAddress;
	}
	
	/**
	 * Gets actual host port
	 * @return port actual host port
	 */
	public int getPort() {
		return hostPort;
	}

	/* ========================== Service methods ==========================  */

	private Object makeRequest(Packet task) throws IOException 
	{
		final String myId = task.getId();

		if(this.clientSocket == null )
			return null;

		//invio i dati
		synchronized(this) {
			outputStream.writeObject(task);
			outputStream.flush();
			resultToWait++;
			notifyAll();
		}
		
		
		//aspetto che il mio risultato si disponibile
		synchronized(this) {
			while(clientSocket != null && !requestResult.containsKey(myId)) {
				try {wait();} catch (InterruptedException e) {}
			}
		}

		if(this.clientSocket == null ) {
			throw new IOException();
		}
		
		Object result = requestResult.get(myId);
		requestResult.remove(myId);
		return result;
	}

	
	public boolean testConnection() 
	{
		//System.out.println("sending: " + ServerServicesName.PING.name());
		try {
			makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.PING.name(), null));
			return true;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}



	public void addClient(ClientServices client) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'addClient'");
	}


	
	public void CloseComunication() throws Exception {
		makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.DISCONNECT.name(), null));
	}



	public synchronized Account addAccount(String name, String username, String userID, String codiceFiscale, String Email, String password, String civicNumber, String viaPiazza, String cap, String commune, String province) throws RemoteException, InvalidUserNameException, InvalidEmailException
	{
		try {
			for (Parameter parameter : this.getClass().getMethod("addAccount").getParameters()) {
				String value = (String) getClass().getDeclaredField(parameter.getName()).get(this);
				System.out.println("Valore parametro "+ parameter.getName()+": " + value);
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}


	public Account getAccount(String Email, String Password) throws RemoteException, InvalidPasswordException, InvalidUserNameException, InvalidEmailException{
		return null;
	}


	public ArrayList<Song> getMostPopularSongs(long limit, long offset) throws Exception 
	{
		ArrayList<Song> data = null;
		Object[] params = new Object[]{"limit", limit,"offset", offset};
	
		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_MOST_POPULAR_SONGS.name(), params));
			data = (ArrayList<Song>) result;
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}


	
	public ArrayList<Album> getRecentPublischedAlbum(long limit, long offset, int threshold) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getRecentPublischedAlbum'");
	}



	public ArrayList<Song> searchSongs(String searchString, long limit, long offset) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'searchSongs'");
	}


	public ArrayList<Album> searchAlbums(String searchString, long limit, long offset) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'searchAlbums'");
	}

}


