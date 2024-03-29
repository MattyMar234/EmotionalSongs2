package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

import Exceptions.InvalidEmailException;
import Exceptions.InvalidPasswordException;
import Exceptions.InvalidUserNameException;
import applicationEvents.ConnectionEvent;
import enumClasses.QueryParameter;
import enumClasses.ServerServicesName;
import interfaces.ServerServices;
import javafx.application.Platform;
import javafx.scene.control.Label;
import objects.Account;
import objects.Album;
import objects.Artist;
import objects.Emotion;
import objects.Packet;
import objects.Playlist;
import objects.Song;
import utility.TimeFormatter;
import utility.UtilityOS;


/**
 * Questa classe gestisce la comunicazione con il server
 */
public class ConnectionManager implements ServerServices
{

    //Singleton pattern
    private static ConnectionManager manager;
	private ObjectsCache cache = ObjectsCache.getInstance();

	private String defaultHostAddress = "127.0.0.1";
	private int defaultHostPort = 8090; 
    private String hostAddress;
    private int hostPort;
	private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
	private Socket clientSocket;
	private boolean connected = false;
	private Label pingLable;

	private HashMap<String, Object> requestResult = new HashMap<>();
	private PacketLintener_thread packetLintener;

	

    private ConnectionManager() {

		hostAddress = defaultHostAddress;
		hostPort = defaultHostPort;
		packetLintener = new PacketLintener_thread();

		if(testCustomConnection(hostAddress, hostPort)) {
            connect();
        }

		new Thread(() -> {
			Thread.currentThread().setName("Connettion Tester");
			//Thread.currentThread().setDaemon(true);

			while(true) 
			{
				//verifico se non sono connesso
				if(!isConnected()) {
					//aspetto finechè non mi connetto con il server
					// synchronized(this) {
					// 	while(!isConnected()) {
					// 		try {
								
					// 			wait();
					// 		} 
					// 		catch (InterruptedException e) {
					// 		}
					// 	}
					// }
					synchronized(this) {
						Platform.runLater(() -> {
							if(pingLable != null)
								this.pingLable.setText("");
						});
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					double start = System.nanoTime();

					//faccio un ping
					if(!testServerConnection()) {
						continue;
					}
					
					//cronometro quanto ci ho messo
					double dt = 1000 - (System.nanoTime() - start)/1000000;
					Main.PING_TIME_us = (System.nanoTime() - start);
					if(dt < 0) dt = 0;

					synchronized(this) {
						final double finalDT = dt;
						Platform.runLater(() -> {
							if(pingLable != null)
								this.pingLable.setText("Ping: " + TimeFormatter.formatTime(Main.PING_TIME_us));
						});
					}
					//System.out.println((long)dt);

					try {
						Thread.sleep((long)dt);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
    }

	/**
	 * Per impostare il riferimento della label che viene usata per mostrare il PING
	 * @param l label da impostare
	 */
	public synchronized void setPinLabel(Label l) {
		this.pingLable = l;
	}
	
	/**
	 *  Funzione per rimuovere il riferimento della lebel che mostra il ping
	 */
	public synchronized void removePinLabel() {
		this.pingLable = null;
	}


	
    /**
     * Class implemented with a Singleton pattern, this method is necessary to get
     * the ConnectionManager instance
     * @return instance of ConnectionManager
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
			if(isConnected() && clientSocket != null)
				return testConnection();
			else 
				return false;
		} 
		catch (Exception e) {
			System.out.println(e);
			disconnect();
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
	 */
	public synchronized boolean connect() 
	{
		//verifico che non sono già collegato
		if(isConnected()) {
			notifyAll();
			return true;
		}
			
			
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
			requestResult.clear();
			notifyAll();
			SceneManager.instance().fireEvent(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, new ConnectionEvent(ConnectionEvent.CONNECTED));
			return true;

		} catch (Exception e) {
			connected = false;
			System.out.println(e);
		}
		notifyAll();
		return false;
	}

	/**
	 * Funzione che ritorna lo stato della connesione
	 * @return
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Funzione che viene eseguta quando perdo la connesione con il server e viene utilizzata per resettare i parametri.
	 */
	private synchronized void connetionLost()
	{
		if(!isConnected())
			return;
			
		if(clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
			}
		}

		SceneManager.instance().fireEvent(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, new ConnectionEvent(ConnectionEvent.DISCONNECTED));
		requestResult.clear();
		connected = false;
		clientSocket = null;
		notifyAll();
	}


	/**
	* funzione per avvisare il server che ci vogliamo disconnettere
	 */
	public synchronized void disconnect() 
	{
		if(clientSocket != null) {
			try {
				this.CloseComunication();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					clientSocket.close();
				} catch (IOException e) {
					
				}
				requestResult.clear();
				connected = false;
				clientSocket = null;
				notifyAll();
			}
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
	private Object makeRequest(Packet task) throws Exception, InvalidParameterException 
	{
		final String myId = task.getId();
		Object result = null;

		//invio i dati e sveglio il thread che gestisce la ricezzione dei risultati
		try {

			if(this.clientSocket == null)
				throw new RuntimeException("Non si è connessi con il server");


			double start = System.nanoTime();
			//System.out.println(Thread.currentThread().getName() + " new Packet: " + task.getCommand());
			
			synchronized(this) {
				try {
					outputStream.writeObject(task);
					outputStream.flush();
					requestResult.put(myId, result);
					notifyAll();
				} catch (java.net.SocketException e) {
					connetionLost();
					return null;
				}
				

				while(this.clientSocket != null && requestResult.get(myId) == null) 
				{
					try {wait(1000);} catch (InterruptedException e) {}
					
					//se sono passati 10s da quando ho inviato i dati
					if((System.nanoTime() - start)/1000000000 >= 10.0) {
						requestResult.remove(myId);
						notifyAll();
						throw new RuntimeException("time out");
					}
				}

				if(this.clientSocket == null || !connected) {
					System.out.println("");
					throw new RuntimeException("Comunicazione con il server persa");
				}

				result = requestResult.get(myId);
				requestResult.remove(myId);
			}

			double end = System.nanoTime();
			System.out.println(myId + "-> Packet recived: " + task.getServiceCommand() + " time: " + TimeFormatter.formatTime(end - start));
		
			//se ho mandato dei parametri non validi
			if(result instanceof InvalidParameterException)
				throw (InvalidParameterException) result;
		
		}
		catch (RuntimeException e) {
			System.out.println(myId + "-> Packet lost");
			throw e;
		}
		catch (Exception e) {
			System.out.println(myId + "-> Packet lost");
			System.out.println(e);
			throw e;
		}
		return result;
	}

	/**
	 * Questa classe gestiscele le risposte del server.
	 */
	private class PacketLintener_thread extends Thread 
	{
		public PacketLintener_thread() {
			super("PacketListener");
			start();
		}

		@Override
		public void run() {
			while(true) {
				try {
					waitForPacket(); 
					getPackets();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Funzione utilizzata da thread "PacketLintener_thread" per aspetta la disponibilità di riposte dal server
	 */
	private synchronized void waitForPacket() 
	{
		//se non ho nulla da attendere vado in wait
		while(requestResult.size() == 0) {
			try {wait();} catch (InterruptedException e) {}
		}
	}

	/*
	 * Funzione utilizzata da thread "PacketLintener_thread" per leggere le risposte del server
	 */
	private void getPackets() throws ClassNotFoundException 
	{
		try {
			String id = (String) inputStream.readObject();
			Object result = inputStream.readObject();

			//salvo il risultato e avviso
			synchronized(this) {

				if(requestResult.containsKey(id) && requestResult.get(id) == null) {
					requestResult.put(id, result);
				}
				notifyAll();
			}
			System.out.print("");
		} 
		catch (IOException e) {
			connetionLost();
		}
		catch (Exception e) {
			synchronized(this) {
				notifyAll();
			}
		}
	}

	/**
	 * Fuznione per verificare lo stato della connessione
	 */
	public boolean testConnection() throws Exception {
		if(makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.PING.name(), null)) == null){
			return false;
		}
		return true;
	}

	/**
	 * Funzione per avvisare il server che si vuole chidere la connesione
	 */
	public void CloseComunication() throws Exception {
		makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.DISCONNECT.name(), null));
	}

	/**
	 * Funzione per generare la chiave da utilizzare con la cache
	 * @param p
	 * @return
	 */
	private String generateKey(Packet p) 
	{
		String service = p.getServiceCommand();
		Object[] parametre = p.getParameters();
		String key = service;

		for (int i = 0; i < parametre.length; i++) {
			if(parametre[i].getClass() == Long.class) {
				key += Long.toString((long)parametre[i]);
			}
			else if(parametre[i].getClass() == String.class) {
				key += (String)parametre[i];
			}
			else if(parametre[i].getClass() == Integer.class) {
				key += Integer.toString((int)parametre[i]);
			}
			else if(parametre[i].getClass() == Boolean.class) {
				key += Boolean.toString((boolean)parametre[i]);
			}
			else if(parametre[i].getClass() == Double.class) {
				key += Double.toString((double)parametre[i]);
			}
			else if(parametre[i].getClass() == Float.class) {
				key += Float.toString((float)parametre[i]);
			}
		}
		return key;
	}


	/**
	 * Funzione per aggiungere un account nel database
	 */
	public Account addAccount(String name, String username, String userID, String codiceFiscale, String Email, String password, String civicNumber, String viaPiazza, String cap, String commune, String province) throws InvalidUserNameException, InvalidEmailException, InvalidPasswordException
	{
		try {
			Object[] params = new Object[]{
				QueryParameter.NAME.toString(), name, 
				QueryParameter.USERNAME.toString(), username, 
				QueryParameter.USER_ID.toString(), userID, 
				QueryParameter.CODICE_FISCALE.toString(), codiceFiscale,	
				QueryParameter.EMAIL.toString(), Email,	
				QueryParameter.PASSWORD.toString(), password,
				QueryParameter.CIVIC_NUMBER.toString(), civicNumber, 
				QueryParameter.VIA_PIAZZA.toString(), viaPiazza, 
				QueryParameter.CAP.toString(), cap, 
				QueryParameter.COMMUNE.toString(), commune, 
				QueryParameter.PROVINCE.toString(), province
			}; 

			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.ADD_ACCOUNT.name(), params));
			
			if(result instanceof String) {
				switch (enumClasses.ErrorString.valueOf((String)result)) {
					case INVALID_PASSWORD -> throw new InvalidPasswordException();
					case INVALID_EMAIL -> throw new InvalidEmailException();
					case INVALID_NICKNAME -> throw new InvalidUserNameException("");
				}
			}

			if(result instanceof Exception)
				throw (Exception) result;

			return (Account) result;
		} 
		catch(InvalidPasswordException e) {
			throw e;
		}
		catch(InvalidUserNameException e) {
			throw e;
		}
		catch(InvalidEmailException e) {
			throw e;
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Funzione per rimuovere un account dal database
	 */
	public boolean deleteAccount(String accountID) {
		Object[] params = new Object[]{QueryParameter.ACCOUNT_ID.toString(), accountID};

		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.DELETE_ACCOUNT.name(), params));
			
			if(result instanceof Exception) {
				throw (Exception) result;
			}
			
			return (boolean) result;
		} 
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Funzione per ottenere un account
	 */
	public Account getAccount(String Email, String password) throws InvalidPasswordException, InvalidUserNameException, InvalidEmailException {
		Object[] params = new Object[]{QueryParameter.EMAIL.toString(), Email,QueryParameter.PASSWORD.toString(), password};

		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_ACCOUNT.name(), params));
			
			if(result instanceof String) {
				switch (enumClasses.ErrorString.valueOf((String)result)) {
					case INVALID_PASSWORD -> throw new InvalidPasswordException();
					case INVALID_EMAIL -> throw new InvalidEmailException();
				}
			}
			
			return (Account) result;
		} 
		catch(InvalidPasswordException e) {
			throw e;
		}
		catch(InvalidUserNameException e) {
			throw e;
		}
		catch(InvalidEmailException e) {
			throw e;
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Funzione per ottenere gli album più popolari
	 * @param limit numero massimo di risultati
	 * @param offset numero di risultati da saltare
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Song> getMostPopularSongs(long limit, long offset) throws Exception 
	{
		try {
			Object[] params = new Object[]{QueryParameter.LIMIT.toString(), limit,QueryParameter.OFFSET.toString(), offset};
			Packet p = new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_MOST_POPULAR_SONGS.name(), params);
			
			//verifico se il dato è già presente in cache
			String key = generateKey(p);
			Object cacheResult = cache.getItem(ObjectsCache.CacheObjectType.QUERY, key);

			if(cacheResult != null) {
				return (ArrayList<Song>) cacheResult;
			}
			
			Object result = makeRequest(p);
			cache.addItem(ObjectsCache.CacheObjectType.QUERY, key, result, true);
			
			return (ArrayList<Song>) result;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * Funzione per ottenere gli album più recenti
	 * @param limit numero massimo di risultati
	 * @param offset numero di risultati da saltare
	 * @param threshold soglia minima da considerare
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Album> getRecentPublischedAlbum(long limit, long offset, int threshold) throws Exception {
		
		try {
			//verifico se il dato è già presente in cache
			Object[] params = new Object[]{QueryParameter.LIMIT.toString(), limit, QueryParameter.OFFSET.toString(), offset, QueryParameter.THRESHOLD.toString(), threshold};
			Packet p = new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_RECENT_PUPLISCED_ALBUMS.name(), params);
			String key = generateKey(p);
			Object cacheResult = cache.getItem(ObjectsCache.CacheObjectType.QUERY, key);

			if(cacheResult != null) {
				return (ArrayList<Album>) cacheResult;
			}
		
			Object result = makeRequest(p);
			
			cache.addItem(ObjectsCache.CacheObjectType.QUERY, key, result, true);
			return (ArrayList<Album>) result;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Per ricercare una canzone
	 * @param searchString prefisso con cui deve incominciare la parola
	 * @param limit numero massimo di risultati
	 * @param offset numero di risultati da saltare
	 * @param mode 0: ricerca per nome, 1: ricerca per anno, 2: ricerca per numero emozioni
	 */
	@SuppressWarnings("unchecked")
	public Object[] searchSongs(String searchString, long limit, long offset, int mode) throws Exception 
	{
		try {
			Object[] params = new Object[]{
				QueryParameter.SEARCH_STRING.toString(), searchString, 
				QueryParameter.LIMIT.toString(), limit, 
				QueryParameter.OFFSET.toString(), offset,
				QueryParameter.MODE.toString(), mode
			}; 

			Packet p = new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.SEARCH_SONGS.name(), params);
			String key = generateKey(p);
			
			if(mode != 2) {
				Object cacheResult = cache.getItem(ObjectsCache.CacheObjectType.QUERY, key);

				if(cacheResult != null) {
					return (Object[]) cacheResult;
					
				}
			}

			Object[] result = (Object[])makeRequest(p);
			
			if(mode == 2) {
				cache.addItem(ObjectsCache.CacheObjectType.QUERY, key, result, false);
			}
			
			
			ArrayList<Song> output = (ArrayList<Song>) result[1];

			//aggiungo tutte le canzoni nella cache
			for (Song song : output) {
				if(cache.getItem(ObjectsCache.CacheObjectType.SONG, song.getId()) == null) {
					cache.addItem(ObjectsCache.CacheObjectType.SONG, song.getId(), song, false);
				}
			}

			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Per ricercare una canzone
	 * @param searchString prefisso con cui deve incominciare la parola
	 * @param limit numero massimo di risultati
	 * @param offset numero di risultati da saltare
	 */
	@SuppressWarnings("unchecked")
	public Object[] searchAlbums(String searchString, long limit, long offset) throws Exception {
		try {
			Object[] params = new Object[]{QueryParameter.SEARCH_STRING.toString(), searchString, QueryParameter.LIMIT.toString(), limit, QueryParameter.OFFSET.toString(), offset};
			Packet p = new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.SEARCH_ALBUMS.name(), params);
			
			String key = generateKey(p);
			Object cacheResult = cache.getItem(ObjectsCache.CacheObjectType.QUERY, key);

			if(cacheResult != null) {
				return (Object[]) cacheResult;
			}
		
			Object[] result = (Object[])makeRequest(p);
			cache.addItem(ObjectsCache.CacheObjectType.QUERY, key, result, false);

			ArrayList<Album> output = (ArrayList<Album>) result[1];

			//aggiungo tutte le canzoni nella cache
			for (Album album : output) {
				if(cache.getItem(ObjectsCache.CacheObjectType.ALBUM, album.getID()) == null) {
					cache.addItem(ObjectsCache.CacheObjectType.ALBUM, album.getID(), album, false);
				}
			}

			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * Per ricercare una canzone
	 * @param searchString prefisso con cui deve incominciare la parola
	 * @param limit numero massimo di risultati
	 * @param offset numero di risultati da saltare
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object[] searchArtists(String searchString, long limit, long offset) {

		try {
			Object[] params = new Object[]{QueryParameter.SEARCH_STRING.toString(), searchString, QueryParameter.LIMIT.toString(), limit, QueryParameter.OFFSET.toString(), offset};
			Packet p = new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.SEARCH_ARTISTS.name(), params);
			
			String key = generateKey(p);
			Object cacheResult = cache.getItem(ObjectsCache.CacheObjectType.QUERY, key);

			if(cacheResult != null) {
				return (Object[]) cacheResult;
			}
		
			Object[] result = (Object[]) makeRequest(p);
			cache.addItem(ObjectsCache.CacheObjectType.QUERY, key, result, false);

			ArrayList<Artist> output = (ArrayList<Artist>) result[1];

			//aggiungo tutte le canzoni nella cache
			for (Artist artist : output) {
				if(cache.getItem(ObjectsCache.CacheObjectType.ARTIST, artist.getID()) == null) {
					cache.addItem(ObjectsCache.CacheObjectType.ARTIST, artist.getID(), artist, true);
				}
			}
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Funzione per ottenere degli oggetti "Song" specificando i loro ID
	 */
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

	/**
	 * Funzione per ottenere un'oggetto artista specificando il suo ID
	 * @param ID l'ID dell'artista
	 * @return
	 */
	public Artist getArtistByID(String ID) 
	{
		try {
			Object cacheResult = cache.getItem(ObjectsCache.CacheObjectType.ARTIST, ID);

			if(cacheResult != null) {
				return (Artist) cacheResult;
			}

			Object[] params = new Object[]{QueryParameter.ARTIST_ID.toString(), ID};
			Packet p = new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_ARTIST_BY_ID.name(), params);
			Object result = makeRequest(p);

			if(result instanceof Exception)
				throw (Exception) result;

			if(result != null)
				cache.addItem(ObjectsCache.CacheObjectType.ARTIST, ID, (Artist)result, false);
			return (Artist) result;
		} 
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * Funzione che resituisce tutte le canzoni di un artista, specificando il suo ID
	 * @param ArtistID L'id dell'artista
	 * @return
	 */
	public ArrayList<Song> getArtistSong(String ArtistID) {
		System.out.println("getArtistSong: ArtistID = " + ArtistID);

		Object[] params = new Object[]{QueryParameter.ARTIST_ID.toString(), ArtistID};
		Packet p = new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_ARTIST_SONGS.name(), params);
		String key = generateKey(p);
		
		Object cacheResult = cache.getItem(ObjectsCache.CacheObjectType.QUERY, key);
		
		if(cacheResult != null) {
			return (ArrayList<Song>) cacheResult;
		}

		try {
			Object result = makeRequest(p);

			if(result instanceof Exception)
				throw (Exception) result;

			cache.addItem(ObjectsCache.CacheObjectType.QUERY, key, result, false);
			return (ArrayList<Song>) result;
		} 
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * Fuznione per ottenere un oggetto "album" specificando il suo ID
	 * @param AlbumID
	 * @return
	 */
	public Album getAlbum_by_ID(String AlbumID) {
		System.out.println("AlbumID:" + AlbumID);

		Object cacheResult = cache.getItem(ObjectsCache.CacheObjectType.ALBUM, AlbumID);

		if(cacheResult != null) {
			return (Album) cacheResult;
		}

		Object[] params = new Object[]{QueryParameter.ID.toString(), AlbumID};
		Album data = null;
		
		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_ALBUM_BY_ID.name(), params));
			
			if(result instanceof Exception)
				throw (Exception) result;
			
			data = (Album) result;
			cache.addItem(ObjectsCache.CacheObjectType.ALBUM, AlbumID, data, false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * Funzione per ottenere tutte le canzoni di un album
	 * @param AlbumID L'id dell'album
	 */
	@Override
    public ArrayList<Song> getAlbumSongs(String AlbumID) throws Exception 
	{
		//System.out.println("AlbumID:" + AlbumID);
		Object[] params = new Object[]{QueryParameter.ALBUM_ID.toString(), AlbumID};
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

	
	/**
	 * Funzione per creare una playlist
	 * @param playlistName Il nome della playlist
	 * @param userID L'id dell'utente
	 * @param playlistImage L'immagine della playlist
	 */
	@Override
	public boolean addPlaylist(String playlistName, String userID, Object playlistImage) {
		System.out.println("addPlaylist: playlistName = " + playlistName + " userID = " + userID);

		Object[] params = new Object[]{QueryParameter.ACCOUNT_ID.toString(), userID, QueryParameter.PLAYLIST_NAME.toString(), playlistName};
		
		 
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

	/**
	 * Funzione per rimuovere una playlist
	 * @param playlistID L'id della playlist
	 * @param userID L'id dell'utente
	 */
	@Override
	public boolean deletePlaylist(String userID, String playlistID){
		System.out.println("removePlaylist: userID = " + userID + " playlistID = " + playlistID);

		Object[] params = new Object[]{QueryParameter.ACCOUNT_ID.toString(), userID, QueryParameter.PLAYLIST_ID.toString(), playlistID}; 
		try {
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.DELETE_PLAYLIST.name(), params));
			
			if(result instanceof Exception)
				throw (Exception) result;
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Funzione per aggiungere una canzone in una playlist
	 * @param userID L'id dell'utente
	 * @param playlistID L'id della playlist
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean addSongToPlaylist(String userID, String playlistID, String songID) throws Exception {
		System.out.println("addSongToPlaylist: userID = " + userID + " playlistID = " + playlistID + " songID = " + songID);
		
		Object[] params = new Object[]{QueryParameter.ACCOUNT_ID.toString(), userID, QueryParameter.PLAYLIST_ID.toString(), playlistID, QueryParameter.SONG_ID.toString(), songID}; 
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

	/**
	 * Funzione per rimuovere una canzone dalla playlist
	 * @param userID L'id dell'utente
	 * @param playlistID L'id della playlist
	 * @param songID L'id della canzone
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean removeSongFromPlaylist(String userID, String playlistID, String songID) throws Exception {
		System.out.println("removeSongFromPlaylist: userID = " + userID + " playlistID = " + playlistID + " songID = " + songID);

		Object[] params = new Object[]{QueryParameter.ACCOUNT_ID.toString(), userID, QueryParameter.PLAYLIST_ID.toString(), playlistID, QueryParameter.SONG_ID.toString(), songID};
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
	
	/**
	 * Fuzione per ottenere tutte le playlist di un utente
	 * @param userID L'id dell'utente
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<Playlist> getAccountPlaylists(String userID) throws Exception {
		System.out.println("getAccountPlaylists: userID = " + userID);

		Object[] params = new Object[]{QueryParameter.ACCOUNT_ID.toString(), userID};
		Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_ACCOUNT_PLAYLIST.name(), params));
		
		if(result instanceof Exception)
			throw (Exception) result;
		
		return (ArrayList<Playlist>)result;
	}

	/**
	 * Funzione per ottenere tutte le playlist di un utente
	 * @param playlistID l'id della playlist
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<Song> getPlaylistSongs(String playlistID) throws Exception {
		System.out.println("getPlaylistSongs: playlistID = " + playlistID);

		Object[] params = new Object[]{QueryParameter.PLAYLIST_ID.toString(), playlistID};
		Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_PLAYLIST_SONGS.name(), params));
		
		if(result instanceof Exception)
			throw (Exception) result;
		
		return (ArrayList<Song>)result;
	}

	/**
	 * Funzione per rinominare una playlist
	 * @param userID L'id dell'utente
	 * @param playlistID L'id della playlist
	 * @param newName Il nuovo nome della playlist
	 * @return true se la playlist è stata rinominata, false altrimenti
	*/
	@Override
	public boolean renamePlaylist(String userID, String playlistID, String newName) throws Exception {
		
		try {

			Object[] params = new Object[]{QueryParameter.ACCOUNT_ID.toString(), userID, QueryParameter.PLAYLIST_ID.toString(), playlistID, QueryParameter.NEW_NAME.toString(), newName};
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.RENAME_PLAYLIST.name(), params));
			
			if(result instanceof Exception)
				throw (Exception) result;
			
			
			return (Boolean) result;
		} 
		catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	/**
	 * Funzione per ottenere tutte le emozioni fatte da un utente
	 * @param userID L'id dell'utente
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Emotion> getAccountEmotions(String userID) throws Exception {

		try {
			Object[] params = new Object[]{QueryParameter.ACCOUNT_ID.toString(), userID};
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_ACCOUNT_EMOTIONS.name(), params));
			
			if(result instanceof Exception)
				throw (Exception) result;
			
			return (ArrayList<Emotion>)result;
		} 
		catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}


	/**
	 * Funzione per creare una nuova emozione
	 * @param userID L'id dell'utente
	 * @param songID L'id della canzone
	 * @param emotionType Il tipo di emozione
	 * @param value Il valore dell'emozione
	 * @param comment Il commento dell'emozione
	 */
	@Override
	public boolean addEmotion(String userID, String songID, String emotionType, int value, String comment) throws Exception {
		System.out.println("addEmotion: userID=" + userID + " songID=" + songID + " emotionType=" + emotionType + " value="+ value + " comment=" + comment);

		Object[] params = new Object[]{
			QueryParameter.ACCOUNT_ID.toString(), userID,
			QueryParameter.SONG_ID.toString(), songID,
			QueryParameter.EMOZIONE.toString(), emotionType,
			QueryParameter.VAL_EMOZIONE.toString(), value,
			QueryParameter.COMMENT.toString(), comment
		};

		Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.ADD_EMOTION.name(), params));
		
		if(result instanceof Exception)
			throw (Exception) result;
		
		return (boolean)result;
	}


	/**
	 * Funzione per remuovere un'emozione
	 * @param id l'id dell'emozione
	 */
	@Override
	public boolean removeEmotion(String id) throws Exception {
		
		try {
			Object[] params = new Object[]{QueryParameter.ID.toString(), id};
			Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.REMOVE_EMOTION.name(), params));
			
			if(result instanceof Exception)
				throw (Exception) result;
			
			return (boolean)result;
		} 
		catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}


	/**
	 * Funzione per ottenere tutte le emozioni di una canzone
	 * @param songID L'id della canzone
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<Emotion> getEmotions(String songID) throws Exception {
		System.out.println("getEmotions: songID=" + songID);

		Object[] params = new Object[]{
			QueryParameter.SONG_ID.toString(), songID,
		};

		Object result = makeRequest(new Packet(Long.toString(Thread.currentThread().getId()), ServerServicesName.GET_SONG_EMOTION.name(), params));
		
		if(result instanceof Exception)
			throw (Exception) result;
		
		return (ArrayList<Emotion>)result;
	}
}


