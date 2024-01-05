package server;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Queue;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import Exceptions.InvalidEmailException;
import Exceptions.InvalidPasswordException;
import database.QueriesManager;
import database.PredefinedSQLCode.Colonne;
import enumclass.QueryParameter;
import enumclass.ServerServicesName;
import interfaces.SocketService;
import objects.Account;
import objects.Album;
import objects.Song;
import utility.TimeFormatter;
import utility.WaithingAnimationThread;


/*
 * Questa classe gestisce la comunicazione tra il server e i client 
 */
public class ComunicationManager extends Thread implements SocketService, Serializable
{
	protected ArrayList<ConnectionHandler> clientsThread = new ArrayList<>();
	private HashMap<ServerServicesName, Function<HashMap<String, Object>,Object>> serverFunctions = new HashMap<>();
	private HashMap<ServerServicesName, String[]> functionParametreKeys = new HashMap<>();
	//private HashMap<Function<HashMap<String, Object>,Object>, Method> functionName = new HashMap<>();
	private HashMap<ServerServicesName, String> functionName = new HashMap<>();
	private HashMap<QueryParameter, Colonne> QueryParametre_to_Colonne = new HashMap<>();

	private Terminal terminal;
	private boolean exit = false;
	private int port;

/**
 * Costruisce un nuovo oggetto ComunicationManager con la porta specificata.
 * 
 * @param port la porta su cui il server ascolterà le richieste.
 * @throws RemoteException se si verifica un errore durante la configurazione del server RMI.
 */
	public ComunicationManager(int port) throws RemoteException {
		this.terminal = Terminal.getInstance();
		this.port = port;
		//setDaemon(true);
		setPriority(MAX_PRIORITY);


		//hashMap che associa a ogni servizio la sua funzione
		/////////////////////////////////////////////////////////////
		//Account
		////////////////////////////////////////////////////////////
		serverFunctions.put(ServerServicesName.ADD_ACCOUNT, this::addAccount);
		serverFunctions.put(ServerServicesName.GET_ACCOUNT, this::getAccount);
		serverFunctions.put(ServerServicesName.DELETE_ACCOUNT, this::deleteAccount);

		/////////////////////////////////////////////////////////////
		//SONGs
		////////////////////////////////////////////////////////////
		serverFunctions.put(ServerServicesName.GET_MOST_POPULAR_SONGS, this::getMostPopularSongs);
		serverFunctions.put(ServerServicesName.SEARCH_SONGS, this::searchSongs);
		serverFunctions.put(ServerServicesName.GET_SONG_BY_IDS, this::getSongByIDs);
		serverFunctions.put(ServerServicesName.GET_ALBUM_SONGS, this::getAlbumsSongs);
		
		
		/////////////////////////////////////////////////////////////
		//ALBUMs
		////////////////////////////////////////////////////////////
		serverFunctions.put(ServerServicesName.SEARCH_ALBUMS, this::searchAlbums);
		serverFunctions.put(ServerServicesName.GET_RECENT_PUPLISCED_ALBUMS, this::getRecentPublischedAlbum);
		serverFunctions.put(ServerServicesName.GET_ALBUM_BY_ID, this::getAlbumByID);
		
		
		/////////////////////////////////////////////////////////////
		//ARTISTs
		////////////////////////////////////////////////////////////
		serverFunctions.put(ServerServicesName.GET_ARTIST_SONGS, this::getArtistSongs);		
		serverFunctions.put(ServerServicesName.GET_ARTIST_ALBUMS, this::getArtistAlbums);
		serverFunctions.put(ServerServicesName.SEARCH_ARTISTS, this::searchArtists);
		serverFunctions.put(ServerServicesName.GET_ARTIST_BY_ID, this::getArtistsByIDs);


		


		/////////////////////////////////////////////////////////////
		//PLAYLIST
		////////////////////////////////////////////////////////////
		serverFunctions.put(ServerServicesName.ADD_PLAYLIST, this::addPlaylist);
		serverFunctions.put(ServerServicesName.GET_ACCOUNT_PLAYLIST, this::getAccountsPlaylists);
		serverFunctions.put(ServerServicesName.ADD_SONG_PLAYLIST, this::addSongToPlaylist);
		serverFunctions.put(ServerServicesName.REMOVE_SONG_PLAYLIST, this::removeSongFromPlaylist);
		serverFunctions.put(ServerServicesName.RENAME_PLAYLIST, this::renamePlaylist);
		serverFunctions.put(ServerServicesName.DELETE_PLAYLIST, this::deletePlaylist);
		serverFunctions.put(ServerServicesName.GET_PLAYLIST_SONGS, this::getPlaylistSongs);
		
		/////////////////////////////////////////////////////////////
		//EMOTIONS
		////////////////////////////////////////////////////////////
		serverFunctions.put(ServerServicesName.ADD_EMOTION, this::addEmotion);
		serverFunctions.put(ServerServicesName.REMOVE_EMOTION, this::deleteEmotion);
		serverFunctions.put(ServerServicesName.GET_SONG_EMOTION, this::getSongEmotion);
		serverFunctions.put(ServerServicesName.GET_ACCOUNT_EMOTIONS, this::getAccountEmotion);
		
		
		//============================================================================================================//
		//hashMap che associa a ogni servizio i parametri richiesti
		/////////////////////////////////////////////////////////////
		//Account
		////////////////////////////////////////////////////////////
		functionParametreKeys.put(ServerServicesName.ADD_ACCOUNT, 					new String[] {QueryParameter.NAME.toString(), QueryParameter.USERNAME.toString(), QueryParameter.USER_ID.toString(), QueryParameter.CODICE_FISCALE.toString(), QueryParameter.EMAIL.toString(), QueryParameter.PASSWORD.toString(), QueryParameter.CIVIC_NUMBER.toString(), QueryParameter.VIA_PIAZZA.toString(), QueryParameter.CAP.toString(), QueryParameter.COMMUNE.toString(), QueryParameter.PROVINCE.toString()});  
	    functionParametreKeys.put(ServerServicesName.GET_ACCOUNT, 					new String[]{QueryParameter.EMAIL.toString(), QueryParameter.PASSWORD.toString()}); 
		functionParametreKeys.put(ServerServicesName.DELETE_ACCOUNT, 				new String[]{QueryParameter.ACCOUNT_ID.toString()});
		
		
		/////////////////////////////////////////////////////////////
		//SONGs
		////////////////////////////////////////////////////////////
		functionParametreKeys.put(ServerServicesName.GET_MOST_POPULAR_SONGS, 		new String[]{QueryParameter.LIMIT.toString(), QueryParameter.OFFSET.toString()});
		functionParametreKeys.put(ServerServicesName.SEARCH_SONGS, 					new String[]{QueryParameter.SEARCH_STRING.toString(), QueryParameter.LIMIT.toString(), QueryParameter.OFFSET.toString(), QueryParameter.MODE.toString()}); 
		functionParametreKeys.put(ServerServicesName.GET_SONG_BY_IDS, 				new String[]{QueryParameter.ID.toString()});
		
		
		/////////////////////////////////////////////////////////////
		//ALBUMs
		////////////////////////////////////////////////////////////
		functionParametreKeys.put(ServerServicesName.GET_RECENT_PUPLISCED_ALBUMS, 	new String[]{QueryParameter.LIMIT.toString(), QueryParameter.OFFSET.toString(), QueryParameter.THRESHOLD.toString()}); 
		functionParametreKeys.put(ServerServicesName.SEARCH_ALBUMS, 				new String[]{QueryParameter.SEARCH_STRING.toString(), QueryParameter.LIMIT.toString(), QueryParameter.OFFSET.toString()}); 
		functionParametreKeys.put(ServerServicesName.GET_ALBUM_SONGS, 				new String[]{QueryParameter.ALBUM_ID.toString()}); 
		functionParametreKeys.put(ServerServicesName.GET_ALBUM_BY_ID, 				new String[]{QueryParameter.ID.toString()});
		
		
		/////////////////////////////////////////////////////////////
		//ARTISTs
		////////////////////////////////////////////////////////////
		functionParametreKeys.put(ServerServicesName.GET_ARTIST_BY_ID, 				new String[]{QueryParameter.ARTIST_ID.toString()});
		functionParametreKeys.put(ServerServicesName.GET_ARTIST_ALBUMS, 			new String[]{QueryParameter.ARTIST_ID.toString()});
		functionParametreKeys.put(ServerServicesName.GET_ARTIST_SONGS, 				new String[]{QueryParameter.ARTIST_ID.toString()});
		functionParametreKeys.put(ServerServicesName.SEARCH_ARTISTS, 				new String[]{QueryParameter.SEARCH_STRING.toString(), QueryParameter.LIMIT.toString(), QueryParameter.OFFSET.toString()});



		/////////////////////////////////////////////////////////////
		//PLAYLIST
		////////////////////////////////////////////////////////////
		functionParametreKeys.put(ServerServicesName.ADD_PLAYLIST, 					new String[]{QueryParameter.ACCOUNT_ID.toString(), QueryParameter.PLAYLIST_NAME.toString()});
		functionParametreKeys.put(ServerServicesName.GET_ACCOUNT_PLAYLIST, 			new String[]{QueryParameter.ACCOUNT_ID.toString()});
		functionParametreKeys.put(ServerServicesName.ADD_SONG_PLAYLIST, 			new String[]{QueryParameter.ACCOUNT_ID.toString(), QueryParameter.PLAYLIST_ID.toString(), QueryParameter.SONG_ID.toString()});
		functionParametreKeys.put(ServerServicesName.REMOVE_SONG_PLAYLIST, 			new String[]{QueryParameter.ACCOUNT_ID.toString(), QueryParameter.PLAYLIST_ID.toString(), QueryParameter.SONG_ID.toString()});
		functionParametreKeys.put(ServerServicesName.RENAME_PLAYLIST, 				new String[]{QueryParameter.ACCOUNT_ID.toString(), QueryParameter.PLAYLIST_ID.toString(), QueryParameter.NEW_NAME.toString()});
		functionParametreKeys.put(ServerServicesName.GET_PLAYLIST_SONGS, 			new String[]{QueryParameter.PLAYLIST_ID.toString()});


		/////////////////////////////////////////////////////////////
		//EMOTIONS
		////////////////////////////////////////////////////////////
		functionParametreKeys.put(ServerServicesName.ADD_EMOTION, 					new String[]{QueryParameter.ACCOUNT_ID.toString(), QueryParameter.SONG_ID.toString(), QueryParameter.EMOZIONE.toString(), QueryParameter.COMMENT.toString(), QueryParameter.VAL_EMOZIONE.toString()});
		functionParametreKeys.put(ServerServicesName.REMOVE_EMOTION, 				new String[]{QueryParameter.ID.toString()});
		functionParametreKeys.put(ServerServicesName.GET_COMMENTS_SONG_FOR_ACCOUNT, new String[]{QueryParameter.ACCOUNT_ID.toString(), QueryParameter.SONG_ID.toString()});
		functionParametreKeys.put(ServerServicesName.DELETE_PLAYLIST, 				new String[]{QueryParameter.ACCOUNT_ID.toString(), QueryParameter.PLAYLIST_ID.toString()});
		functionParametreKeys.put(ServerServicesName.GET_SONG_EMOTION, 				new String[]{QueryParameter.SONG_ID.toString()});
		functionParametreKeys.put(ServerServicesName.GET_ACCOUNT_EMOTIONS, 			new String[]{QueryParameter.ACCOUNT_ID.toString()});

		
		
		functionParametreKeys.put(ServerServicesName.GET_COMMENTS_SONG, 			new String[]{QueryParameter.SONG_ID.toString()});
		

		try {
			//hashMap che associa a ogni servizio il nome della sua funzione
			functionName.put(ServerServicesName.ADD_ACCOUNT, "addAccount");
			functionName.put(ServerServicesName.GET_ACCOUNT, "getAccount");
			functionName.put(ServerServicesName.GET_MOST_POPULAR_SONGS, "getMostPopularSongs");
			functionName.put(ServerServicesName.GET_RECENT_PUPLISCED_ALBUMS, "getRecentPublischedAlbum");
			functionName.put(ServerServicesName.SEARCH_SONGS, "searchSongs");
			functionName.put(ServerServicesName.SEARCH_ALBUMS, "searchAlbums");
			functionName.put(ServerServicesName.GET_SONG_BY_IDS, "getSongByIDs");
			functionName.put(ServerServicesName.GET_ALBUM_SONGS, "getAlbumsSongs");
			functionName.put(ServerServicesName.ADD_PLAYLIST, "addPlaylist");
			functionName.put(ServerServicesName.GET_ACCOUNT_PLAYLIST, "getAccountsPlaylistsBy");
			functionName.put(ServerServicesName.ADD_SONG_PLAYLIST, "addSongToPlaylist");
			functionName.put(ServerServicesName.REMOVE_SONG_PLAYLIST, "removeSongFromPlaylist");
			functionName.put(ServerServicesName.RENAME_PLAYLIST, "renamePlaylist");
			functionName.put(ServerServicesName.ADD_EMOTION, "addEmotion");
			functionName.put(ServerServicesName.REMOVE_EMOTION, "deleteComment");
			functionName.put(ServerServicesName.GET_COMMENTS_SONG_FOR_ACCOUNT, "getAccountComments");
			functionName.put(ServerServicesName.GET_COMMENTS_SONG, "getSongComments");
			functionName.put(ServerServicesName.GET_ACCOUNT_EMOTIONS, "getAccountComments");
			functionName.put(ServerServicesName.GET_SONG_EMOTION, "getSongEmotion");
			functionName.put(ServerServicesName.DELETE_PLAYLIST, "deletePlaylist");
			functionName.put(ServerServicesName.DELETE_ACCOUNT, "deleteAccount");
			functionName.put(ServerServicesName.GET_ARTIST_SONGS, "getArtistSongs");
			functionName.put(ServerServicesName.GET_PLAYLIST_SONGS, "getPlaylistsSongs");
			functionName.put(ServerServicesName.GET_ALBUM_BY_ID, "getAlbumsByIDs");
			functionName.put(ServerServicesName.GET_ARTIST_ALBUMS, "getArtistAlbums");
			functionName.put(ServerServicesName.SEARCH_ARTISTS, "searchArtist");
			functionName.put(ServerServicesName.GET_ARTIST_BY_ID, "getArtistByID");
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		QueryParametre_to_Colonne.put(QueryParameter.ACCOUNT_ID, Colonne.ACCOUNT_ID_REF);
		QueryParametre_to_Colonne.put(QueryParameter.SONG_ID, Colonne.SONG_ID_REF);
		QueryParametre_to_Colonne.put(QueryParameter.EMOZIONE, Colonne.TYPE);
		QueryParametre_to_Colonne.put(QueryParameter.COMMENT, Colonne.COMMENTO);
		QueryParametre_to_Colonne.put(QueryParameter.VAL_EMOZIONE, Colonne.VALUE);

	}



/**
 * Converte un HashMap di parametri di query in un HashMap di colonne.
 *
 * @param argsTable   Il HashMap che contiene i parametri di query e i loro valori.
 * @param addIDColumn Un flag che indica se aggiungere la colonna ID.
 * @return Un HashMap che contiene colonne e i loro valori corrispondenti.
 */
	private HashMap<Colonne, Object> convertFromQueryParametre2Colonne(final HashMap<String, Object> argsTable, boolean addID_colum)
	{
		HashMap<Colonne, Object> ColonneValore = new HashMap<Colonne, Object>();

		for(String key : argsTable.keySet()) {
			//System.out.println(key);
			ColonneValore.put(QueryParametre_to_Colonne.get(QueryParameter.valueOf(key)), argsTable.get(key));
		}
			

		if(addID_colum)
			ColonneValore.put(Colonne.ID, QueriesManager.generate_ID_from_Time());

		return ColonneValore;
	}



/**
 * Esegue la funzione associata al servizio del server con i parametri forniti.
 *
 * @param name      Il nome del servizio del server da eseguire.
 * @param params    I parametri necessari per l'esecuzione del servizio.
 * @param clientIP  L'indirizzo IP del client che richiede il servizio.
 * @return L'output risultante dall'esecuzione del servizio o un'istanza di Exception in caso di errore.
 */
	public Object executeServerServiceFunction(final ServerServicesName name, final HashMap<String, Object> params, final String clientIP) 
	{
		Function<HashMap<String, Object>,Object> function = this.serverFunctions.get(name);
		double startTime = System.nanoTime();
		double end = 0;
		
		
		//verifico se tutti i parametri sono corretti
		if(!testParametre(params, functionParametreKeys.get(name))) 
			return new Exception("Missing argument");

		//eseguo le operazioni
		try {
			//printFunctionArgs(function, clientIP);	
			
			Object output =  function.apply(params);

			if(output instanceof SQLException) {
				System.out.println((SQLException) output);
				//terminal.printErrorln(((SQLException) output).toString());
			}
			
			if(output instanceof Exception) {
				throw (Exception) output;
			}
			return output;
		}
		catch (Exception e) {
			printError(e);
            e.printStackTrace();
			return e; 
        }
		finally {
			end = System.nanoTime();
			printFunctionExecutionTime(functionName.get(name), clientIP,  end - startTime);
		}
	}



/**
 * Ottiene l'indirizzo IP della macchina.
 *
 * @return L'indirizzo IP della macchina.
 */
	public static String getMachineIP()
	{
		String IP = "";
		try(final DatagramSocket socket = new DatagramSocket()){
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			IP = socket.getLocalAddress().getHostAddress();
		}
		catch(Exception e) {

		}
		return IP;
	}



/**
 * Avvia il server e gestisce la connessione con i client.
 */
	public void run() 
	{
		ServerSocket server = null;
		String IP = getMachineIP();


		


		terminal.printInfoln("Start comunication inizilization");
		terminal.startWaithing(Terminal.MessageType.INFO + " Starting server...");
		try {Thread.sleep(ThreadLocalRandom.current().nextInt(400, 1000));} catch (Exception e) {}

		try {
			if(IP == "")
				IP = getPrivateIPv4();
			//IP = InetAddress.getLocalHost().getHostAddress();
		} 
		catch (Exception e) {
			terminal.printErrorln(e.toString());
			e.printStackTrace();
			return;
		}

		
		terminal.printInfoln("Start SOCKET configuration:");
		terminal.printInfoln("ServerSocket creation on port " + port);
		
		try {Thread.sleep(ThreadLocalRandom.current().nextInt(400, 1000));} catch (Exception e) {}
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(500);

			//IP = server.getInetAddress()
		} 
		catch (Exception e) {
			terminal.printErrorln(e.toString());
			e.printStackTrace();
			terminal.stopWaithing();
			return;
		}

		terminal.stopWaithing();
		terminal.printSeparator();
		terminal.printSuccesln(Terminal.Color.GREEN_BOLD_BRIGHT + "Server initialization complete" + Terminal.Color.RESET);
		terminal.printSeparator();
		terminal.printInfoln("Server listening on "+ Terminal.Color.MAGENTA + IP + " : " + port + Terminal.Color.RESET);
		terminal.printInfoln("press ENTER to stop the server");
		terminal.setAddTime(true);
		terminal.startWaithing(Terminal.MessageType.INFO + " Server Running", WaithingAnimationThread.Animation.DOTS);
		terminal.printLine();

			
		while (!exit) 
		{
			try {
				//ho impostato un timeout di 500ms
				Socket clientSocket = server.accept();
				if(clientSocket == null) 
					continue;

				terminal.printInfoln("Connection established with: " + Terminal.Color.MAGENTA + clientSocket.getInetAddress().getHostAddress() + Terminal.Color.RESET);
				
				ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket, this);
				connectionHandler.start();
				clientsThread.add(connectionHandler);
			
			} 
			catch (java.io.InterruptedIOException e) {
				continue;
			}
			catch (IOException e) {
				terminal.printErrorln(e.toString());
				e.printStackTrace();
			}

		}	
		
		terminal.printInfoln("Closing Server...");

		//faccio una copia per evitare errori
		ArrayList<ConnectionHandler> temp = (ArrayList<ConnectionHandler>) clientsThread.clone();
		for(ConnectionHandler client : clientsThread)
			client.terminate();

		if(temp.size() > 0)
			terminal.printInfoln("waith for clients thread...");

		for(ConnectionHandler client : temp)
			while(client.isAlive());
		
			
		try {
			while(!server.isClosed()) {
				server.close();
			}	
		} 
		catch (IOException e) {
			terminal.printErrorln(e.toString());
			e.printStackTrace();
			return;
		}
		
		terminal.printInfoln("server is close: " + server.isClosed());
		terminal.stopWaithing();
		terminal.setAddTime(false);	
	}

	public void terminate() {
		this.exit = true;
		Thread.currentThread().interrupt();
	}

	protected void removeClientSocket(ConnectionHandler connectionHandler) {
		this.clientsThread.remove(connectionHandler);
		new Thread(() -> {
			terminal.printInfoln("host disconected: " + Terminal.Color.MAGENTA + connectionHandler.getSocket().getInetAddress().getHostAddress() + Terminal.Color.RESET);
		}).start();
	}
// ==================================== UTILITY ====================================//
/**
 * Restituisce l'indirizzo IPv4 privato della macchina.
 *
 * @return L'indirizzo IPv4 privato della macchina.
 * @throws UnknownHostException Se l'host è sconosciuto.
 * @throws SocketException      Se si verifica un errore di socket.
 */
	@SuppressWarnings("unused")
	public static String getPrivateIPv4() throws UnknownHostException, SocketException 
	{
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		String ipToReturn = null;
		while(e.hasMoreElements())
		{
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration<InetAddress> ee = n.getInetAddresses();
			while (ee.hasMoreElements())
			{
				InetAddress i = (InetAddress) ee.nextElement();
				String currentAddress = i.getHostAddress();
				Terminal.getInstance().printInfoln("IP address "+currentAddress+ " found");

				//i.isSiteLocalAddress()&&!i.isLoopbackAddress() && validate(currentAddress)
				if(validate(currentAddress)){
					ipToReturn = currentAddress;    
				}else{
					//System.out.println("Address not validated as public IPv4");
				}

			}
		}

		return ipToReturn;
	}


/**
 * La classe IPv4Validator fornisce metodi di utilità per la validazione degli indirizzi IPv4.
 *
 * Gli indirizzi IPv4 sono rappresentati da una sequenza di quattro numeri interi separati da punti,
 * ognuno dei quali può variare da 0 a 255. Ad esempio, "192.168.0.1".
 *
 * Questa classe offre un metodo che utilizza espressioni regolari per verificare se una stringa
 * rappresenta un indirizzo IPv4 valido.
 */
	private static final Pattern IPv4RegexPattern = Pattern.compile(
			"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	public static boolean validate(final String ip) {
		return IPv4RegexPattern.matcher(ip).matches();
	}



/**
 * Stampa il tempo di esecuzione di una funzione in un thread separato.
 *
 * Questo metodo accetta il nome della funzione, l'host del client e il tempo di esecuzione,
 * quindi crea un nuovo thread per eseguire la stampa delle informazioni sul tempo di esecuzione
 * della funzione. Il risultato viene formattato utilizzando il metodo formatFunctionRequestTime
 * e stampato sulla console tramite l'oggetto terminal.
 *
 * @param function Il nome della funzione il cui tempo di esecuzione deve essere stampato.
 * @param clientHost L'host del client associato all'esecuzione della funzione.
 * @param dt Il tempo di esecuzione della funzione in secondi.
 * @throws NullPointerException Se il parametro 'function' o 'clientHost' è null.
 */
	private void printFunctionExecutionTime(String function, String clientHost, double dt) {
		new Thread(() ->{
			//Method f = this.functionName.get(function);
			terminal.printInfoln(formatFunctionRequestTime(clientHost, function, dt));
		}).start();
	}



/**
 * Stampa gli argomenti di una funzione in un thread separato.
 *
 * Questo metodo accetta un oggetto di tipo Function rappresentante una funzione e l'host del client,
 * quindi crea un nuovo thread per eseguire la stampa degli argomenti della funzione. 
 * Per effettuare la stampa, è possibile utilizzare il metodo formatFunctionRequest dalla classe terminal.
 * 
 * Nota: Attualmente, il corpo del metodo è commentato e non esegue alcuna azione. 
 * Rimuovi i commenti e implementa la logica desiderata per la stampa degli argomenti.
 *
 * @param functionName L'oggetto di tipo Function rappresentante la funzione di cui stampare gli argomenti.
 * @param clientHost L'host del client associato alla richiesta della funzione.
 * @throws NullPointerException Se il parametro 'functionName' o 'clientHost' è null.
 */
	private void printFunctionArgs(Function functionName, String clientHost) {

		/*new Thread(() ->{
			try {
				//terminal.printRequestln(formatFunctionRequest(clientHost, "MostPopularSongs("+limit+ ", " + offset +")"));
				//terminal.printRequestln(formatFunctionRequest(clientHost, functionName));
				
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}).start();*/
	}



/**
 * Formatta una richiesta di funzione per la stampa.
 *
 * Questo metodo accetta l'host del client e il nome della funzione e restituisce una stringa formattata
 * rappresentante la richiesta di funzione. Il formato include il colore magenta per l'host del client,
 * il colore ciano brillante per il nome della funzione e il reset del colore per garantire la coerenza nella stampa.
 *
 * @param clientHost L'host del client associato alla richiesta della funzione.
 * @param function Il nome della funzione richiesta.
 * @return Una stringa formattata rappresentante la richiesta di funzione.
 * @throws NullPointerException Se il parametro 'clientHost' o 'function' è null.
 */
	private String formatFunctionRequest(String clientHost, String function) {
		return "Host " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET + " requested function:" + Terminal.Color.CYAN_BOLD_BRIGHT + "\"" + function + "\"" + Terminal.Color.RESET;
	}



/**
 * Formatta una richiesta di funzione includendo il tempo di esecuzione colorato.
 *
 * Questo metodo accetta l'host del client, il nome della funzione e il tempo di esecuzione,
 * quindi restituisce una stringa formattata che rappresenta la richiesta di funzione.
 * Il tempo di esecuzione è colorato in base a intervalli predefiniti di durata.
 *
 * @param clientHost L'host del client associato alla richiesta della funzione.
 * @param function Il nome della funzione richiesta.
 * @param dt Il tempo di esecuzione della funzione in nanosecondi.
 * @return Una stringa formattata rappresentante la richiesta di funzione con il tempo di esecuzione colorato.
 * @throws NullPointerException Se il parametro 'clientHost' o 'function' è null.
 */
	private String formatFunctionRequestTime(String clientHost, String function, double dt) {

		String timeStr = "";
		Terminal.Color color = null;
		double seconds = (double) (dt / 1000000000.0);

		if(seconds <= 0.400) 
			color = Terminal.Color.GREEN_BOLD_BRIGHT;
		else if(seconds <= 0.800) 
			color = Terminal.Color.YELLOW_BOLD_BRIGHT;
		else if(seconds >= 0.800) 
			color = Terminal.Color.RED_BOLD_BRIGHT;

		timeStr = "  executed in " + color + TimeFormatter.formatTime(dt) + Terminal.Color.RESET;
		return "Host: " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET + ", function " + Terminal.Color.CYAN_BOLD_BRIGHT + "\"" + function + "\"" + Terminal.Color.RESET + timeStr;
	}



/**
 * Stampa un'eccezione in un thread separato con informazioni sull'host e l'errore.
 *
 * Questo metodo accetta un'eccezione come parametro, quindi crea un nuovo thread per eseguire
 * la stampa dell'errore sulla console tramite l'oggetto Terminal. L'output include l'host del client
 * ottenuto dal server e il dettaglio dell'eccezione.
 *
 * @param e L'eccezione da stampare.
 * @throws NullPointerException Se l'eccezione passata è null.
 */
	private void printError(Exception e) {
		new Thread(() -> {
			try {
				Terminal.getInstance().printErrorln("Host " + Terminal.Color.MAGENTA + RemoteServer.getClientHost() + Terminal.Color.RESET + " error: " + e);
			} catch (ServerNotActiveException e1) {
				e1.printStackTrace();
			}
		}).start();
	}




	//====================================== SOCKET SERVICES ======================================//
/**
 * Verifica la presenza e la corrispondenza dei parametri in una tabella di argomenti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) e un array di chiavi (keys).
 * Verifica se la dimensione della tabella è uguale a quella dell'array di chiavi.
 * Successivamente, verifica che la tabella contenga tutte le chiavi necessarie.
 * In caso di mancata corrispondenza, stampa un messaggio di errore sulla console tramite l'oggetto Terminal
 * e restituisce false; altrimenti, restituisce true.
 *
 * @param argsTable La tabella di argomenti da verificare.
 * @param keys Un array di chiavi rappresentanti i parametri richiesti.
 * @return true se la tabella contiene tutte le chiavi necessarie, false altrimenti.
 * @throws NullPointerException Se uno dei parametri 'argsTable' o 'keys' è null.
 */
	private boolean testParametre(HashMap<String, Object> argsTable, String[] keys) {
		
		if(argsTable.size() != keys.length)
			return false;
		
		//verifico che ci siano tutti i parametri necessari
		for (String str : keys) {
			if (!argsTable.containsKey(str)) {
				new Thread(() -> {
					terminal.printErrorln("Missing argument: " + str);
				}).start();
				return false;
			}
		}
		return true;
	}
	
	
	//---------------------------- operazioni con Account ---------------------------- //
/**
 * Aggiunge un nuovo account al sistema utilizzando i parametri forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente le informazioni necessarie
 * per creare un nuovo account. Effettua alcune verifiche preliminari per garantire che l'indirizzo email
 * e il nickname siano unici nel sistema. Se l'indirizzo email o il nickname è già associato a un account,
 * restituisce un messaggio di errore corrispondente.
 * Altrimenti, crea un nuovo account e una nuova residenza associata a esso, quindi restituisce l'account appena creato.
 *
 * @param argsTable La tabella di argomenti contenente le informazioni necessarie per creare l'account.
 * @return L'account appena creato se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' è null.
 */
	@Override
	public Object addAccount(final HashMap<String, Object> argsTable) 
	{
		try {
			HashMap<Colonne, Object> colonne_account   = new HashMap<Colonne, Object>();
			HashMap<Colonne, Object> colonne_residenza = new HashMap<Colonne, Object>();
			Account account = null;

			account = QueriesManager.getAccountByEmail((String) argsTable.get(QueryParameter.EMAIL.toString()));
			
			if(account != null) {
				return enumclass.ErrorString.INVALID_EMAIL.name();
			}

			account = QueriesManager.getAccountByNickname((String) argsTable.get(QueryParameter.USER_ID.toString()));

			if(account != null) {
				return enumclass.ErrorString.INVALID_NICKNAME.name();
			}

		
			colonne_account.put(Colonne.NAME, argsTable.get(QueryParameter.NAME.toString()));
			colonne_account.put(Colonne.SURNAME, argsTable.get(QueryParameter.USERNAME.toString()));
			colonne_account.put(Colonne.NICKNAME, argsTable.get(QueryParameter.USER_ID.toString()));
			colonne_account.put(Colonne.FISCAL_CODE, argsTable.get(QueryParameter.CODICE_FISCALE.toString()));
			colonne_account.put(Colonne.EMAIL, argsTable.get(QueryParameter.EMAIL.toString()));
			colonne_account.put(Colonne.PASSWORD, DigestUtils.sha256Hex((String) argsTable.get(QueryParameter.PASSWORD.toString())));
			//colonne_account.put(Colonne.RESIDENCE_ID_REF, resd_ID);
			
			//colonne_residenza.put(Colonne.ID, resd_ID);
			colonne_residenza.put(Colonne.VIA_PIAZZA, argsTable.get(QueryParameter.VIA_PIAZZA.toString()));
			colonne_residenza.put(Colonne.CIVIC_NUMER, Integer.parseInt((String)argsTable.get(QueryParameter.CIVIC_NUMBER.toString())));
			colonne_residenza.put(Colonne.PROVINCE_NAME, argsTable.get(QueryParameter.PROVINCE.toString()));
			colonne_residenza.put(Colonne.COUNCIL_NAME, argsTable.get(QueryParameter.COMMUNE.toString()));
			colonne_residenza.put(Colonne.CAP, (String)argsTable.get(QueryParameter.CAP.toString()));

			QueriesManager.addAccount_and_addResidence(colonne_account, colonne_residenza);

			HashMap<String, Object> temp = new HashMap<String, Object>();
			temp.put(QueryParameter.EMAIL.toString(), argsTable.get(QueryParameter.EMAIL.toString()));
			temp.put(QueryParameter.PASSWORD.toString(), argsTable.get(QueryParameter.PASSWORD.toString()));

			return getAccount(temp);
		} 
		catch (Exception e) {
			return e;
        }
	}

	

/**
 * Recupera un account dal sistema utilizzando l'indirizzo email e la password forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'indirizzo email e la password
 * necessari per recuperare un account. Cerca un account nel sistema con l'indirizzo email fornito
 * e verifica se la password corrisponde a quella associata all'account.
 * Se l'indirizzo email non è valido, restituisce un messaggio di errore corrispondente.
 * Se la password non corrisponde, restituisce un messaggio di errore corrispondente.
 * Altrimenti, restituisce l'account associato all'indirizzo email fornito.
 *
 * @param argsTable La tabella di argomenti contenente l'indirizzo email e la password necessari per il recupero dell'account.
 * @return L'account recuperato se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' è null.
 */
	@Override
	@SuppressWarnings("unchecked")
	public Object getAccount(final HashMap<String, Object> argsTable) 
	{
		try { 
			//cerco se esiste un account con quell'email
			Account account = QueriesManager.getAccountByEmail((String) argsTable.get(QueryParameter.EMAIL.toString()));

			//verifico se l'ho trovato
			if(account == null)
				return enumclass.ErrorString.INVALID_EMAIL.name();

			//verifico se le password combaciano
			if(!account.getPassword().equals(DigestUtils.sha256Hex((String) argsTable.get(QueryParameter.PASSWORD.toString()))))
				return enumclass.ErrorString.INVALID_PASSWORD.name();

			return account;
		} 
		catch (Exception e) {
			return e;
        }	
	}

	//---------------------------- operazioni con SONG ---------------------------- //
/**
 * Recupera le canzoni più popolari dal sistema utilizzando i parametri forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente i parametri LIMIT e OFFSET necessari
 * per recuperare le canzoni più popolari. Utilizza questi parametri per eseguire una query al sistema
 * e ottenere le canzoni più popolari in base ai criteri specificati.
 * Restituisce una lista di canzoni se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente i parametri LIMIT e OFFSET per il recupero delle canzoni più popolari.
 * @return Una lista di canzoni più popolari se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' è null.
 */
	@Override
	@SuppressWarnings("unchecked")
	public Object getMostPopularSongs(final HashMap<String, Object> argsTable) 
	{
		try {
			return QueriesManager.getTopPopularSongs((long)argsTable.get(QueryParameter.LIMIT.toString()), (long)argsTable.get(QueryParameter.OFFSET.toString()));
		} 
		catch (Exception e) {
			return e;
        }
	}



/**
 * Esegue una ricerca di canzoni nel sistema utilizzando i parametri forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente i parametri SEARCH_STRING, LIMIT, OFFSET e MODE
 * necessari per eseguire una ricerca di canzoni nel sistema. Utilizza questi parametri per eseguire una query
 * e ottenere i risultati della ricerca.
 * Restituisce un array di oggetti contenenti le informazioni sulla ricerca e il conteggio totale degli elementi
 * se l'operazione è riuscita, altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente i parametri SEARCH_STRING, LIMIT, OFFSET e MODE per la ricerca delle canzoni.
 * @return Un array di oggetti contenenti i risultati della ricerca e il conteggio totale degli elementi
 *         se l'operazione è riuscita, altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' è null.
 */
	@Override
	@SuppressWarnings("unchecked")
	public Object searchSongs(final HashMap<String, Object> argsTable) 
	{
        try {
			String key = (String)argsTable.get(QueryParameter.SEARCH_STRING.toString());
			long limit = (long)argsTable.get(QueryParameter.LIMIT.toString());
			long offset = (long)argsTable.get(QueryParameter.OFFSET.toString());
			int mode = (int)argsTable.get(QueryParameter.MODE.toString());

            Object[] result = QueriesManager.searchSong_and_countElement(key, limit, offset, mode);
		
			
			return result;
		} 
		catch (Exception e) {
			return e;
        }
	}

	

/**
 * Recupera gli album più recentemente pubblicati nel sistema utilizzando i parametri forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente i parametri LIMIT, OFFSET e THRESHOLD
 * necessari per ottenere gli album più recentemente pubblicati. Utilizza questi parametri per eseguire una query
 * e restituire gli album che soddisfano i criteri specificati.
 * Restituisce un'ArrayList di oggetti Album se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente i parametri LIMIT, OFFSET e THRESHOLD per il recupero degli album più recentemente pubblicati.
 * @return Un'ArrayList di oggetti Album se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' è null.
 */
	@Override
	@SuppressWarnings("unchecked")
	public Object getRecentPublischedAlbum(final HashMap<String, Object> argsTable) 
	{
		try {
			ArrayList<Album> result = QueriesManager.getRecentPublischedAlbum((long)argsTable.get(QueryParameter.LIMIT.toString()), (long)argsTable.get(QueryParameter.OFFSET.toString()), (int)argsTable.get(QueryParameter.THRESHOLD.toString()));
			return result;
		}
		catch (Exception e) {
			return e;
        }
	}



/**
 * Esegue una ricerca di album nel sistema utilizzando i parametri forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente i parametri SEARCH_STRING, LIMIT e OFFSET
 * necessari per eseguire una ricerca di album nel sistema. Utilizza questi parametri per eseguire una query
 * e ottenere i risultati della ricerca.
 * Restituisce un'ArrayList di oggetti Album se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente i parametri SEARCH_STRING, LIMIT e OFFSET per la ricerca degli album.
 * @return Un'ArrayList di oggetti Album se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' è null.
 */
	@Override
	@SuppressWarnings("unchecked")
	public Object searchAlbums(final HashMap<String, Object> argsTable) 
	{
        try {
            return QueriesManager.searchAlbum((String)argsTable.get(QueryParameter.SEARCH_STRING.toString()), (long)argsTable.get(QueryParameter.LIMIT.toString()), (long)argsTable.get(QueryParameter.OFFSET.toString()));
		} 
		catch (Exception e) {
			return e;
        }
	}

	

/**
 * Elimina un account dal sistema utilizzando l'ID dell'account fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'account da eliminare.
 * Utilizza l'ID per eseguire l'operazione di eliminazione tramite il gestore delle query del sistema.
 * Restituisce true se l'operazione è riuscita, altrimenti restituisce una stringa di errore o un'eccezione
 * che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'account da eliminare.
 * @return true se l'operazione di eliminazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro 'argsTable' o il suo valore associato a 'QueryParameter.ACCOUNT_ID' è null.
 */
	@Override
	public Object deleteAccount(final HashMap<String, Object> argsTable) {
		try {
			QueriesManager.deleteAccount((String)argsTable.get(QueryParameter.ACCOUNT_ID.toString()));
			return true;
		} 
		catch (Exception e) {
			return e;
		}
	}



/**
 * Recupera le canzoni dal sistema utilizzando gli ID forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente un array di ID delle canzoni.
 * Utilizza gli ID per eseguire una query e ottenere le informazioni sulle canzoni corrispondenti.
 * Restituisce un'ArrayList di oggetti Song se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente un array di ID delle canzoni.
 * @return Un'ArrayList di oggetti Song se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro 'argsTable' o il suo valore associato a 'QueryParameter.ID' è null.
 */
	@Override
	public Object getSongByIDs(final HashMap<String, Object> argsTable) {
		try {
            return QueriesManager.searchSongByIDs((String[])argsTable.get(QueryParameter.ID.toString()));
		} 
		catch (Exception e) {
			return e;
        }
	}

	

/**
 * Recupera le canzoni associate a un album nel sistema utilizzando l'ID dell'album fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'album.
 * Utilizza l'ID dell'album per eseguire una query e ottenere le informazioni sulle canzoni associate a quell'album.
 * Restituisce un'ArrayList di oggetti Song se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'album.
 * @return Un'ArrayList di oggetti Song se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro 'argsTable' o il suo valore associato a 'QueryParameter.ALBUM_ID' è null.
 */
	@Override
	public Object getAlbumsSongs(final HashMap<String, Object> argsTable) 
	{
        try {
            ArrayList<Song> result = QueriesManager.getAlbumSongs((String)argsTable.get(QueryParameter.ALBUM_ID.toString()));
			//terminal.printInfoln("element: " + result.size());
			return result;
		} 
		catch (Exception e) {
			return e;
        }
	}



/**
 * Recupera le canzoni associate a un artista nel sistema utilizzando l'ID dell'artista fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'artista.
 * Utilizza l'ID dell'artista per eseguire una query e ottenere le informazioni sulle canzoni associate a quell'artista.
 * Restituisce un'ArrayList di oggetti Song se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'artista.
 * @return Un'ArrayList di oggetti Song se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro 'argsTable' o il suo valore associato a 'QueryParameter.ARTIST_ID' è null.
 */
	@Override
	public Object getArtistSongs(final HashMap<String, Object> argsTable) {
		try {
			ArrayList<Song> result = QueriesManager.getArtistSong((String)argsTable.get(QueryParameter.ARTIST_ID.toString()));
			//terminal.printInfoln("element: " + result.size());
			return result;
		} 
		catch (Exception e) {
			return e;
		}
	}



/**
 * Recupera le canzoni associate a una playlist nel sistema utilizzando l'ID della playlist fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID della playlist.
 * Utilizza l'ID della playlist per eseguire una query e ottenere le informazioni sulle canzoni associate a quella playlist.
 * Restituisce un'ArrayList di oggetti Song se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID della playlist.
 * @return Un'ArrayList di oggetti Song se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro 'argsTable' o il suo valore associato a 'QueryParameter.PLAYLIST_ID' è null.
 */
	@Override
	public Object getPlaylistSongs(final HashMap<String, Object> argsTable) {
		try {
			ArrayList<Song> result = QueriesManager.getPlaylistSong((String)argsTable.get(QueryParameter.PLAYLIST_ID.toString()));
			//terminal.printInfoln("element: " + result.size());
			return result;
		} 
		catch (Exception e) {
			return e;
		}
	}



/**
 * Rinomina una playlist nel sistema utilizzando gli ID dell'account, della playlist e il nuovo nome fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente gli ID dell'account, della playlist e il nuovo nome.
 * Utilizza questi ID e il nuovo nome per eseguire l'operazione di rinomina della playlist tramite il gestore delle query del sistema.
 * Restituisce true se l'operazione è riuscita, altrimenti restituisce una stringa di errore o un'eccezione
 * che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente gli ID dell'account, della playlist e il nuovo nome.
 * @return true se l'operazione di rinomina è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' o i loro valori associati a
 *         'QueryParameter.ACCOUNT_ID', 'QueryParameter.PLAYLIST_ID' o 'QueryParameter.NEW_NAME' è null.
 */
	@Override
	public Object renamePlaylist(final HashMap<String, Object> argsTable) {
		try {
			QueriesManager.renamePlaylist((String)argsTable.get(QueryParameter.ACCOUNT_ID.toString()), (String)argsTable.get(QueryParameter.PLAYLIST_ID.toString()), (String)argsTable.get(QueryParameter.NEW_NAME.toString()));
			return true;        
		} 
		catch (Exception e) {
			return e;
		}
	}

	

/**
 * Recupera un album dal sistema utilizzando l'ID fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'album.
 * Utilizza l'ID dell'album per eseguire una query e ottenere le informazioni sull'album corrispondente.
 * Restituisce un oggetto Album se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'album.
 * @return Un oggetto Album se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro 'argsTable' o il suo valore associato a 'QueryParameter.ID' è null.
 */
	@Override
	public Object getAlbumByID(final HashMap<String, Object> argsTable) {
		try {
			//System.out.println(argsTable.get(QueryParameter.ID.toString()).getClass());
            return QueriesManager.getAlbumByID((String)argsTable.get(QueryParameter.ID.toString()));
		} 
		catch (Exception e) {
			return e;
        }
	}

	

/**
 * Recupera gli album associati a un artista nel sistema utilizzando l'ID dell'artista fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'artista.
 * Utilizza l'ID dell'artista per eseguire una query e ottenere le informazioni sugli album associati a quell'artista.
 * Restituisce un'ArrayList di oggetti Album se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'artista.
 * @return Un'ArrayList di oggetti Album se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro 'argsTable' o il suo valore associato a 'QueryParameter.ARTIST_ID' è null.
 */
	@Override
	public Object getArtistAlbums(final HashMap<String, Object> argsTable) {
		try {
			//ArrayList<Album> result = QueriesManager.getArtistAlbums((String)argsTable.get(QueryParameter.ARTIST_ID.toString()));
			//terminal.printInfoln("element: " + result.size());
			return null;
		} 
		catch (Exception e) {
			return e;
		}
	}

/**
 * Esegue una ricerca degli artisti nel sistema utilizzando una stringa chiave di ricerca, un limite e uno spostamento.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente una stringa chiave di ricerca, un limite e uno spostamento.
 * Utilizza questi parametri per eseguire una ricerca degli artisti tramite il gestore delle query del sistema.
 * Restituisce un array di oggetti Artist e un numero totale di elementi se l'operazione è riuscita,
 * altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente la stringa chiave di ricerca, il limite e lo spostamento.
 * @return Un array di oggetti Artist e un numero totale di elementi se l'operazione è riuscita,
 *         altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro 'argsTable' o uno dei suoi valori associati a 'QueryParameter.SEARCH_STRING',
 *         'QueryParameter.LIMIT' o 'QueryParameter.OFFSET' è null.
 */
	@Override
	public Object searchArtists(final HashMap<String, Object> argsTable) {
		try {
			String key = (String)argsTable.get(QueryParameter.SEARCH_STRING.toString());
			long limit = (long)argsTable.get(QueryParameter.LIMIT.toString());
			long offset = (long)argsTable.get(QueryParameter.OFFSET.toString());

            Object[] result = QueriesManager.searchArtists(key, limit, offset);

			return result;
		} 
		catch (Exception e) {
			return e;
		}
	}



/**
 * Recupera gli artisti dal sistema utilizzando gli ID forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente un array di ID degli artisti.
 * Utilizza gli ID per eseguire una query e ottenere le informazioni sugli artisti corrispondenti.
 * Restituisce un oggetto Artist se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente un array di ID degli artisti.
 * @return Un oggetto Artist se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro 'argsTable' o il suo valore associato a 'QueryParameter.ARTIST_ID' è null.
 */
	@Override
	public Object getArtistsByIDs(HashMap<String, Object> argsTable) {
		try {
			
			return QueriesManager.getArtistByID((String)argsTable.get(QueryParameter.ARTIST_ID.toString()));
		} 
		catch (Exception e) {
			return e;
		}
	}



/**
 * Aggiunge una nuova playlist nel sistema utilizzando l'ID dell'account e il nome della playlist forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'account e il nome della playlist.
 * Utilizza questi parametri per eseguire l'operazione di aggiunta della playlist tramite il gestore delle query del sistema.
 * Restituisce true se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'account e il nome della playlist.
 * @return true se l'operazione di aggiunta della playlist è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' o i loro valori associati a
 *         'QueryParameter.ACCOUNT_ID' o 'QueryParameter.PLAYLIST_NAME' è null.
 */
	@Override
	public Object addPlaylist(final HashMap<String, Object> argsTable) {
		try {
			//Aggiungere eventuale immagine
			QueriesManager.addPlaylist((String)argsTable.get(QueryParameter.ACCOUNT_ID.toString()), (String)argsTable.get(QueryParameter.PLAYLIST_NAME.toString()));
			return true;        
		} 
		catch (Exception e) {
			return e;
        }
	}



/**
 * Elimina una playlist dal sistema utilizzando l'ID dell'account e l'ID della playlist forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'account e l'ID della playlist.
 * Utilizza questi parametri per eseguire l'operazione di eliminazione della playlist tramite il gestore delle query del sistema.
 * Restituisce true se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'account e l'ID della playlist.
 * @return true se l'operazione di eliminazione della playlist è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' o i loro valori associati a
 *         'QueryParameter.ACCOUNT_ID' o 'QueryParameter.PLAYLIST_ID' è null.
 */
	@Override
	public Object deletePlaylist(final HashMap<String, Object> argsTable) {
		try {
			QueriesManager.deletePlaylist((String)argsTable.get(QueryParameter.ACCOUNT_ID.toString()), (String)argsTable.get(QueryParameter.PLAYLIST_ID.toString()));
			return true;
		} 
		catch (Exception e) {
			return e;     
		} 
	}

	

/**
 * Rimuove una canzone da una playlist nel sistema utilizzando l'ID dell'account, l'ID della playlist e l'ID della canzone forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'account, l'ID della playlist e l'ID della canzone.
 * Utilizza questi parametri per eseguire l'operazione di rimozione della canzone dalla playlist tramite il gestore delle query del sistema.
 * Restituisce true se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'account, l'ID della playlist e l'ID della canzone.
 * @return true se l'operazione di rimozione della canzone dalla playlist è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' o i loro valori associati a
 *         'QueryParameter.ACCOUNT_ID', 'QueryParameter.PLAYLIST_ID' o 'QueryParameter.SONG_ID' è null.
 */
	@Override
	public Object removeSongFromPlaylist(final HashMap<String, Object> argsTable) {
		try {
			QueriesManager.removeSongFromPlaylist((String)argsTable.get(QueryParameter.ACCOUNT_ID.toString()), (String)argsTable.get(QueryParameter.PLAYLIST_ID.toString()), (String)argsTable.get(QueryParameter.SONG_ID.toString()));
			return true;        
		} 
		catch (Exception e) {
			return e;
		}
	}

	

/**
 * Aggiunge una canzone a una playlist nel sistema utilizzando l'ID dell'account, l'ID della playlist e l'ID della canzone forniti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'account, l'ID della playlist e l'ID della canzone.
 * Utilizza questi parametri per eseguire l'operazione di aggiunta della canzone alla playlist tramite il gestore delle query del sistema.
 * Restituisce true se l'operazione è riuscita, altrimenti restituisce una stringa di errore
 * o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'account, l'ID della playlist e l'ID della canzone.
 * @return true se l'operazione di aggiunta della canzone alla playlist è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' o i loro valori associati a
 *         'QueryParameter.ACCOUNT_ID', 'QueryParameter.PLAYLIST_ID' o 'QueryParameter.SONG_ID' è null.
 */
	@Override
	public Object addSongToPlaylist(final HashMap<String, Object> argsTable) {
		try {
			QueriesManager.addSongToPlaylist((String)argsTable.get(QueryParameter.ACCOUNT_ID.toString()), (String)argsTable.get(QueryParameter.PLAYLIST_ID.toString()), (String)argsTable.get(QueryParameter.SONG_ID.toString()));
			return true;        
		} 
		catch (Exception e) {
			return e;
		}
	}

	

/**
 * Recupera le playlist associate a un account nel sistema utilizzando l'ID dell'account fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'account.
 * Utilizza questo parametro per eseguire l'operazione di recupero delle playlist associate all'account
 * tramite il gestore delle query del sistema.
 * Restituisce un oggetto che rappresenta le playlist associate all'account se l'operazione è riuscita,
 * altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'account.
 * @return Un oggetto che rappresenta le playlist associate all'account se l'operazione è riuscita,
 *         altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro nella tabella 'argsTable' o il suo valore associato a 'QueryParameter.ACCOUNT_ID' è null.
 */
	@Override
	public Object getAccountsPlaylists(final HashMap<String, Object> argsTable) {
		try {
			return QueriesManager.getAccountsPlaylists((String)argsTable.get(QueryParameter.ACCOUNT_ID.toString()));
		} 
		catch (Exception e) {
			return e;
		}
	}


	/////////////////////////////////////////////////////////////////////////////////
	//EMOTION
	/////////////////////////////////////////////////////////////////////////////////

/**
 * Aggiunge un'emozione al sistema utilizzando i parametri forniti nella tabella degli argomenti.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente i parametri necessari per l'aggiunta dell'emozione.
 * Utilizza questi parametri per eseguire l'operazione di aggiunta dell'emozione tramite il gestore delle query del sistema.
 * Restituisce true se l'operazione è riuscita, altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente i parametri necessari per l'aggiunta dell'emozione.
 * @return true se l'operazione di aggiunta dell'emozione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se uno dei parametri nella tabella 'argsTable' o i loro valori associati ai parametri necessari sono nulli.
 */
	@Override
	public Object addEmotion(final HashMap<String, Object> argsTable) {
		try {
			QueriesManager.addEmotion(convertFromQueryParametre2Colonne(argsTable, true));
		} 
		catch (Exception e) {
			return e;
		}
		return true;
	}

	

/**
 * Recupera le emozioni associate a una canzone nel sistema utilizzando l'ID della canzone fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID della canzone.
 * Utilizza questo parametro per eseguire l'operazione di recupero delle emozioni associate alla canzone
 * tramite il gestore delle query del sistema.
 * Restituisce un oggetto che rappresenta le emozioni associate alla canzone se l'operazione è riuscita,
 * altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID della canzone.
 * @return Un oggetto che rappresenta le emozioni associate alla canzone se l'operazione è riuscita,
 *         altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro nella tabella 'argsTable' o il suo valore associato a 'QueryParameter.SONG_ID' è null.
 */
	@Override
	public Object getSongEmotion(final HashMap<String, Object> argsTable) {
		try {
			return QueriesManager.getSongEmotion((String)argsTable.get(QueryParameter.SONG_ID.toString()));
		} 
		catch (Exception e) {
			return e;     
		} 
	}

	

/**
 * Elimina un'emozione dal sistema utilizzando l'ID dell'emozione fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'emozione.
 * Utilizza questo parametro per eseguire l'operazione di eliminazione dell'emozione tramite il gestore delle query del sistema.
 * Restituisce true se l'operazione è riuscita, altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'emozione.
 * @return true se l'operazione di eliminazione dell'emozione è riuscita, altrimenti restituisce una stringa di errore
 *         o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro nella tabella 'argsTable' o il suo valore associato a 'QueryParameter.ID' è null.
 */
	@Override
	public Object deleteEmotion(final HashMap<String, Object> argsTable) {
		try {
			
			QueriesManager.deleteEmotion((String)argsTable.get(QueryParameter.ID.toString()));
			return true;
		} 
		catch (Exception e) {
			return e;     
		}

	}

	

/**
 * Recupera le emozioni associate a un account nel sistema utilizzando l'ID dell'account fornito.
 *
 * Questo metodo accetta una tabella di argomenti (HashMap) contenente l'ID dell'account.
 * Utilizza questo parametro per eseguire l'operazione di recupero delle emozioni associate all'account
 * tramite il gestore delle query del sistema.
 * Restituisce un oggetto che rappresenta le emozioni associate all'account se l'operazione è riuscita,
 * altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 *
 * @param argsTable La tabella di argomenti contenente l'ID dell'account.
 * @return Un oggetto che rappresenta le emozioni associate all'account se l'operazione è riuscita,
 *         altrimenti restituisce una stringa di errore o un'eccezione che è stata catturata durante l'esecuzione.
 * @throws NullPointerException Se il parametro nella tabella 'argsTable' o il suo valore associato a 'QueryParameter.ACCOUNT_ID' è null.
 */
	@Override
	public Object getAccountEmotion(HashMap<String, Object> argsTable) {
		try {
			return QueriesManager.getAccountEmotions((String)argsTable.get(QueryParameter.ACCOUNT_ID.toString()));
		} 
		catch (Exception e) {
			return e;     
		}
	}

	

	

	
	
	

	

	
	
	
}