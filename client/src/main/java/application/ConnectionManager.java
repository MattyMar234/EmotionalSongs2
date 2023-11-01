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
import enumClasses.ServerServicesName;
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
import objects.Playlist;
import utility.UtilityOS;

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
import java.rmi.RemoteException;
import java.security.InvalidParameterException;


public class ConnectionManager implements ServerServices{

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
			super("PacketListener");
			start();
		}

		@Override
		public void run() {
			while(true) {try {waitForPacket(); getPackets();} catch (Exception e) {e.printStackTrace();}}
		}
	}


	private synchronized void waitForPacket() 
	{
		//se non ho nulla da attendere vado in wait
		while(resultToWait == 0) {
			try {wait();} catch (InterruptedException e) {}
		}
	}

	private void getPackets() throws ClassNotFoundException, IOException 
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
		} 
		catch (Exception e) {
			synchronized(this) {
				disconnect();
				notifyAll();
			}
		}
		
	}

    private ConnectionManager() {

		hostAddress = defaultHostAddress;
		hostPort = defaultHostPort;
		packetLintener = new PacketLintener_thread();

		if(testCustomConnection(hostAddress, hostPort)) {
            connect();
        }

		new Thread(() -> {
			Thread.currentThread().setName("PING sender");
			//Thread.currentThread().setDaemon(true);

			while(true) {
				try {Thread.sleep(1000);	} catch (InterruptedException e) {System.out.println(e);}
				if(isConnected()) {
					if(!testServerConnection()) {
						disconnect();
						SceneManager.getInstance().fireEvent(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, new ConnectionEvent(ConnectionEvent.DISCONNECTED));
					}
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

			} catch (Exception e) {
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

		if(UtilityOS.isUnix() || UtilityOS.isMac()) {
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
		try {
			if(isConnected() && clientSocket != null) {
				return testConnection();
			} 
			else {
				return false;
			}
		} 
		catch (Exception e) {
			System.out.println(e);
			return false;
			//e.printStackTrace();
		}
	}

	/**
	 * Sets new connection data to find EmotionalSong server
	 * @param host the new server's host
	 * @param port the new server's port
	 */
	public boolean setConnectionData(String host, int port) {

		host = host.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "");

		if(UtilityOS.isUnix() || UtilityOS.isMac()) {
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
			
		try	{
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


	public boolean isConnected() {
		return connected;
	}


	/**
	 * Disconnects the client from Watchneighbours server
	 */
	public synchronized void disconnect() 
	{

		//timeline.stop();
		connected = false;
		if(isConnected()) {
			try {
				this.CloseComunication();
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
		
		resultToWait = 0;
		requestResult.clear();
		clientSocket = null;
		notifyAll();
		
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
	/**
	 * Questa funzione invia i dati al server e aspetta che il server risponda.
	 * @param task L'operazione che deve essere eseguita dal server.
	 * @return	Il risultato dell'operazione.
	 * @throws IOException Se si riscontrano problemi con la comunicazione con il server o con si è connessi.
	 * @throws InvalidParameterException Se il task contiene dei parametri non corretti.
	 */
	private Object makeRequest(Packet task) throws IOException, InvalidParameterException 
	{
		final String myId = task.getId();
		double start = System.nanoTime();

		if(this.clientSocket == null)
			throw new IOException("Non si è connessi con il server");

		//invio i dati e sveglio il thread che gestisce la ricezzione dei risultati
		try {
			synchronized(this) {
				outputStream.writeObject(task);
				outputStream.flush();
				resultToWait++;
				notifyAll();

				while(clientSocket != null && !requestResult.containsKey(myId)) {
					try {wait();} catch (InterruptedException e) {}
				}
			}
		}
		catch (Exception e) {
			System.out.println(e);
			throw e;
		}
		
		if(this.clientSocket == null)
			throw new IOException("Comunicazione con il server persa");
		
		
		Object result = requestResult.get(myId);
		requestResult.remove(myId);

		//se ho mandato dei parametri non validi
		if(result instanceof InvalidParameterException)
			throw (InvalidParameterException) result;

		System.out.println("result: " + Thread.currentThread().getName());
		double end = System.nanoTime();
		System.out.println(task.getCommand() + " time: " + (end - start)/1000000000 + "s" );
		return result;
	}

	
	public boolean testConnection() {
		try {
			makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.PING.name(), null));
			return true;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}


	public void CloseComunication() throws Exception {
		makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.DISCONNECT.name(), null));
	}



	public Account addAccount(String name, String username, String userID, String codiceFiscale, String Email, String password, String civicNumber, String viaPiazza, String cap, String commune, String province) throws InvalidUserNameException, InvalidEmailException
	{
		try {
			Object[] params = new Object[]{
				"name", name,"username", username,"userID", userID,	"codiceFiscale", codiceFiscale,	"Email", Email,	"password", password,
				"civicNumber", civicNumber,"viaPiazza", viaPiazza,"cap", cap,"commune", commune,"province", province
			};

			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.ADD_ACCOUNT.name(), params));
			return (Account) result;
		} 
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
		return null;
	}


	public Account getAccount(String Email, String password) throws InvalidPasswordException, InvalidUserNameException, InvalidEmailException {
		Object[] params = new Object[]{"Email", Email,"password", password};

		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_ACCOUNT.name(), params));
			return (Account) result;
		} 
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
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


	@SuppressWarnings("unchecked")
	public ArrayList<Album> getRecentPublischedAlbum(long limit, long offset, int threshold) throws Exception {
		ArrayList<Album> data = null;
		Object[] params = new Object[]{"limit", limit,"offset", offset, "threshold", threshold};
	
		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_RECENT_PUPLISCED_ALBUMS.name(), params));
			data = (ArrayList<Album>) result;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}


	@SuppressWarnings("unchecked")
	public ArrayList<Song> searchSongs(String searchString, long limit, long offset) throws Exception 
	{
		Object[] params = new Object[]{"searchString", searchString, "limit", limit, "offset", offset};
		ArrayList<Song> data = null;
		
		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.SEARCH_SONGS.name(), params));
			data = (ArrayList<Song>) result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	

	@SuppressWarnings("unchecked")
	public ArrayList<Album> searchAlbums(String searchString, long limit, long offset) throws Exception {
		Object[] params = new Object[]{"searchString", searchString, "limit", limit, "offset", offset};
		ArrayList<Album> data = null;
		
		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.SEARCH_SONGS.name(), params));
			data = (ArrayList<Album>) result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}


	public ArrayList<Song> getSongByIDs(String[] IDs) throws Exception 
	{
		Object[] params = new Object[]{"IDs", IDs};
		ArrayList<Song> data = null;
		
		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_SONG_BY_IDS.name(), params));
			data = (ArrayList<Song>) result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	@Override
    public ArrayList<Song> getAlbumSongs(String AlbumID) throws Exception 
	{
		System.out.println("AlbumID:" + AlbumID);
		Object[] params = new Object[]{"albumID", AlbumID};
		ArrayList<Song> data = null;
		
		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_ALBUM_SONGS.name(), params));
			
			if(result instanceof Exception)
				throw (Exception) result;
			
			data = (ArrayList<Song>) result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	

	@Override
	public boolean addPlaylist(String playlistName, String userID, Object playlistImage) {
		System.out.println("addPlaylist: playlistName = " + playlistName + " userID = " + userID);

		Object[] params = new Object[]{"accountID", userID, "playlistName", playlistName};

		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.ADD_PLAYLIST.name(), params));
			
			if(result instanceof Exception)
				throw (Exception) result;
			
			if(result instanceof Boolean) {
				return (Boolean) result;
			}
			
			throw new RuntimeException("Invalid result");
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean removePlaylist(String userID, String playlistID){
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'removePlaylist'");
	}

	@Override
	public boolean addSongToPlaylist(String userID, String playlistID, String songID) throws Exception {
		System.out.println("addSongToPlaylist: userID = " + userID + " playlistID = " + playlistID + " songID = " + songID);
		
		Object[] params = new Object[]{"accountID", userID, "playlistID", playlistID, "songID", songID};
		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.ADD_SONG_PLAYLIST.name(), params));
			
			if(result instanceof Exception)
				throw (Exception) result;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean removeSongFromPlaylist(String userID, String playlistID, String songID) throws Exception {
		System.out.println("removeSongFromPlaylist: userID = " + userID + " playlistID = " + playlistID + " songID = " + songID);

		Object[] params = new Object[]{"accountID", userID, "playlistID", playlistID, "songID", songID};
		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.REMOVE_SONG_PLAYLIST.name(), params));
			
			if(result instanceof Exception)
				throw (Exception) result;
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	

	@Override
	public ArrayList<Playlist> getAccountPlaylists(String userID) throws Exception {
		System.out.println("getAccountPlaylists: userID = " + userID);

		Object[] params = new Object[]{"accountID", userID};
		Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_ACCOUNT_PLAYLIST.name(), params));
		
		if(result instanceof Exception)
			throw (Exception) result;
		
		return (ArrayList<Playlist>)result;
	}

	@Override
	public Object getPlaylistSongs(String userID, String playlistID) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getPlaylistSongs'");
	}

	@Override
	public boolean renamePlaylist(String userID, String playlistID, String newName) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'renamePlaylist'");
	}

}


