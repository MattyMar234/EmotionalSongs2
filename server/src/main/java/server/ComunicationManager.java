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
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import Exceptions.InvalidEmailException;
import Exceptions.InvalidPasswordException;
import database.QueriesManager;
import database.PredefinedSQLCode.Colonne;
import interfaces.SocketService;
import objects.Account;
import objects.Album;
import objects.Song;
import utility.WaithingAnimationThread;

public class ComunicationManager extends Thread implements SocketService, Serializable
{
	protected ArrayList<ConnectionHandler> clientsThread = new ArrayList<>();
	private HashMap<ServerServicesName, Function<HashMap<String, Object>,Object>> serverFunctions = new HashMap<>();
	private HashMap<ServerServicesName, String[]> functionParametreKeys = new HashMap<>();
	//private HashMap<Function<HashMap<String, Object>,Object>, Method> functionName = new HashMap<>();
	private HashMap<ServerServicesName, String> functionName = new HashMap<>();

	private Terminal terminal;
	private boolean exit = false;
	private int port;

	/**
	 * Costruttore della classe
	 * @param port
	 * @throws RemoteException
	 */
	public ComunicationManager(int port) throws RemoteException {
		this.terminal = Terminal.getInstance();
		this.port = port;
		setDaemon(true);
		setPriority(MAX_PRIORITY);

		//hashMap che associa a ogni servizio la sua funzione
		serverFunctions.put(ServerServicesName.ADD_ACCOUNT, this::addAccount);
		serverFunctions.put(ServerServicesName.GET_ACCOUNT, this::getAccount);
		serverFunctions.put(ServerServicesName.GET_RECENT_PUPLISCED_ALBUMS, this::getRecentPublischedAlbum);
		serverFunctions.put(ServerServicesName.GET_MOST_POPULAR_SONGS, this::getMostPopularSongs);
		serverFunctions.put(ServerServicesName.SEARCH_SONGS, this::searchSongs);
		serverFunctions.put(ServerServicesName.SEARCH_ALBUMS, this::searchAlbums);
		serverFunctions.put(ServerServicesName.GET_SONG_BY_IDS, this::getSongByIDs);
		serverFunctions.put(ServerServicesName.GET_ALBUM_SONGS, this::getAlbumsSongs);

		//hashMap che associa a ogni servizio i parametri richiesti
		functionParametreKeys.put(ServerServicesName.ADD_ACCOUNT, new String[] {"name", "username", "userID", "codiceFiscale", "email", "password", "civicNumber", "viaPiazza", "cap", "commune", "province"});
		functionParametreKeys.put(ServerServicesName.GET_ACCOUNT, new String[]{"email", "password"});
		functionParametreKeys.put(ServerServicesName.GET_MOST_POPULAR_SONGS, new String[]{"limit", "offset"});
		functionParametreKeys.put(ServerServicesName.GET_RECENT_PUPLISCED_ALBUMS, new String[]{"limit", "offset", "threshold"});
		functionParametreKeys.put(ServerServicesName.SEARCH_SONGS, new String[]{"searchString", "limit", "offset"});
		functionParametreKeys.put(ServerServicesName.SEARCH_ALBUMS, new String[]{"searchString", "limit", "offset"});
		functionParametreKeys.put(ServerServicesName.GET_SONG_BY_IDS, new String[]{"IDs"});
		functionParametreKeys.put(ServerServicesName.GET_ALBUM_SONGS, new String[]{"albumID"});

		try {
			//hashMap che associa a ogni servizio il nome della sua funzione
			functionName.put(ServerServicesName.ADD_ACCOUNT, "addAccount");
			functionName.put(ServerServicesName.GET_ACCOUNT, "getAccount");
			functionName.put(ServerServicesName.GET_MOST_POPULAR_SONGS, "getRecentPublischedAlbum");
			functionName.put(ServerServicesName.GET_RECENT_PUPLISCED_ALBUMS, "getMostPopularSongs");
			functionName.put(ServerServicesName.SEARCH_SONGS, "searchSongs");
			functionName.put(ServerServicesName.SEARCH_ALBUMS, "searchAlbums");
			functionName.put(ServerServicesName.GET_SONG_BY_IDS, "getSongByIDs");
			functionName.put(ServerServicesName.GET_ALBUM_SONGS, "getAlbumsSongs");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Funzione che serve per eseguire i servizi del server
	 * @param name Nome del servizio
	 * @param params Parametri del servizio ( HashMap<String, Object> )
	 * @param clientIP L'IP dell'host
	 * @return
	 */
	public Object executeServerServiceFunction(final ServerServicesName name, final HashMap<String, Object> params, final String clientIP) 
	{
		//inizializzo il timer e ottengo il riferimento della funzione da richiamare
		final double startTime = System.nanoTime();
		Function<HashMap<String, Object>,Object> function = this.serverFunctions.get(name);
		
		//verifico se tutti i parametri sono corretti
		if(!testParametre(params, functionParametreKeys.get(name))) 
			return new Exception("Missing argument");

		//eseguo le operazioni
		try {
			//printFunctionArgs(function, clientIP);	
			Object output =  function.apply(params);

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
			printFunctionExecutionTime(functionName.get(name), clientIP, startTime);
		}
	}


	
	public void run() 
	{
		ServerSocket server = null;
		String IP = "";

		try(final DatagramSocket socket = new DatagramSocket()){
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			IP = socket.getLocalAddress().getHostAddress();
		}
		catch(Exception e) {

		}


		terminal.printInfoln("Start comunication inizilization");
		terminal.startWaithing(Terminal.MessageType.INFO + " Starting server...");
		try {Thread.sleep(ThreadLocalRandom.current().nextInt(400, 1000));} catch (Exception e) {}

		try {
			if(IP == "")
				IP = getPublicIPv4();
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
		
		terminal.printInfo("server is close: " + server.isClosed());
		
		terminal.stopWaithing();
		terminal.setAddTime(false);	
	}

	public void terminate() {
		this.exit = true;
		Thread.currentThread().interrupt();
	}

	protected void removeClientSocket(ConnectionHandler connectionHandler) {
		this.clientsThread.remove(connectionHandler);
		terminal.printInfoln("host disconected: " + Terminal.Color.MAGENTA + connectionHandler.getSocket().getInetAddress().getHostAddress() + Terminal.Color.RESET);
	}
// ==================================== UTILITY ====================================//

	@SuppressWarnings("unused")
	private String getPublicIPv4() throws UnknownHostException, SocketException 
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

	private static final Pattern IPv4RegexPattern = Pattern.compile(
			"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	public static boolean validate(final String ip) {
		return IPv4RegexPattern.matcher(ip).matches();
	}

	private void printFunctionExecutionTime(String function, String clientHost,double startTime) {
		new Thread(() ->{
			//Method f = this.functionName.get(function);
			

			double estimatedTime = System.nanoTime() - startTime;
			double seconds = (double)estimatedTime / 1000000000.0;
			terminal.printInfoln(formatFunctionRequestTime(clientHost, function, seconds));
		}).start();
	}

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


	private String formatFunctionRequest(String clientHost, String function) {
		return "Host " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET + " requested function:" + Terminal.Color.CYAN_BOLD_BRIGHT + "\"" + function + "\"" + Terminal.Color.RESET;
	}

	private String formatFunctionRequestTime(String clientHost, String function, double time) {

		String timeStr = "";

		if(time <= 0.400) timeStr = " executed in " + Terminal.Color.GREEN_BOLD_BRIGHT + time + Terminal.Color.RESET + " seconds";
		else if(time <= 0.800) timeStr = " executed in " + Terminal.Color.YELLOW_BOLD_BRIGHT + time + Terminal.Color.RESET + " seconds";
		else if(time >= 0.800) timeStr = " executed in " + Terminal.Color.RED_BOLD_BRIGHT + time + Terminal.Color.RESET + " seconds";

		return "Host " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET + " function " + Terminal.Color.CYAN_BOLD_BRIGHT + "\"" + function + "\"" + Terminal.Color.RESET + timeStr;
	}


	private void printError(Exception e) {
		try {
			Terminal.getInstance().printErrorln("Host " + Terminal.Color.MAGENTA + RemoteServer.getClientHost() + Terminal.Color.RESET + " error: " + e);
		} catch (ServerNotActiveException e1) {
			e1.printStackTrace();
		}
	}




	//====================================== SOCKET SERVICES ======================================//
	
	private boolean testParametre(HashMap<String, Object> argsTable, String[] keys) {
		//verifico che ci siano tutti i parametri necessari
		for (String str : keys) {
			if (!argsTable.containsKey(str)) {
				terminal.printErrorln("Missing argument: " + str);
				return false;
			}
		}
		return true;
	}
	
	
	//---------------------------- operazioni con Account ---------------------------- //
	@Override
	public Object addAccount(final HashMap<String, Object> argsTable) 
	{
		HashMap<Colonne, Object> colonne_account   = new HashMap<Colonne, Object>();
		HashMap<Colonne, Object> colonne_residenza = new HashMap<Colonne, Object>();

		try {
			colonne_account.put(Colonne.NAME, argsTable.get("name"));
			colonne_account.put(Colonne.SURNAME, argsTable.get("username"));
			colonne_account.put(Colonne.NICKNAME, argsTable.get("userID"));
			colonne_account.put(Colonne.FISCAL_CODE, argsTable.get("codiceFiscale"));
			colonne_account.put(Colonne.EMAIL, argsTable.get("email"));
			colonne_account.put(Colonne.PASSWORD, DigestUtils.sha256Hex((String) argsTable.get("password")));
			//colonne_account.put(Colonne.RESIDENCE_ID_REF, resd_ID);
			
			//colonne_residenza.put(Colonne.ID, resd_ID);
			colonne_residenza.put(Colonne.VIA_PIAZZA, argsTable.get("viaPiazza"));
			colonne_residenza.put(Colonne.CIVIC_NUMER, Integer.parseInt((String)argsTable.get("civicNumber")));
			colonne_residenza.put(Colonne.PROVINCE_NAME, argsTable.get("province"));
			colonne_residenza.put(Colonne.COUNCIL_NAME, argsTable.get("commune"));
			colonne_residenza.put(Colonne.CAP, Integer.parseInt((String)argsTable.get("cap")));

			QueriesManager.addAccount_and_addResidence(colonne_account, colonne_residenza);

			HashMap<String, Object> temp = new HashMap<String, Object>();
			temp.put("email", argsTable.get("email"));
			temp.put("password", argsTable.get("password"));

			return getAccount(temp);
		} 
		catch (Exception e) {
			return e;
        }
	}


	@Override
	@SuppressWarnings("unchecked")
	public Object getAccount(final HashMap<String, Object> argsTable) 
	{
		try { 
			//cerco se esiste un account con quell'email
			Account account = QueriesManager.getAccountByEmail((String) argsTable.get("email"));

			//verifico se l'ho trovato
			if(account == null)
				return new InvalidEmailException();

			//verifico se le password combaciano
			if(!account.getPassword().equals(DigestUtils.sha256Hex((String) argsTable.get("password"))))
				return new InvalidPasswordException();

			return account;
		} 
		catch (Exception e) {
			return e;
        }	
	}

	//---------------------------- operazioni con SONG ---------------------------- //

	@Override
	@SuppressWarnings("unchecked")
	public Object getMostPopularSongs(final HashMap<String, Object> argsTable) 
	{
		try {
			return QueriesManager.getTopPopularSongs((long)argsTable.get("limit"), (long)argsTable.get("offset"));
		} 
		catch (Exception e) {
			return e;
        }
	}

	

	@Override
	@SuppressWarnings("unchecked")
	public Object searchSongs(final HashMap<String, Object> argsTable) 
	{
        try {
            ArrayList<Song> result = QueriesManager.searchSong((String)argsTable.get("searchString"), (long)argsTable.get("limit"), (long)argsTable.get("offset"));
			return result;
		} 
		catch (Exception e) {
			return e;
        }
	}


	@Override
	@SuppressWarnings("unchecked")
	public Object getRecentPublischedAlbum(final HashMap<String, Object> argsTable) 
	{
		try {
			ArrayList<Album> result = QueriesManager.getRecentPublischedAlbum((long)argsTable.get("limit"), (long)argsTable.get("offset"), (int)argsTable.get("threshold"));
			return result;
		}
		catch (Exception e) {
			return e;
        }
	}



	@Override
	@SuppressWarnings("unchecked")
	public Object searchAlbums(final HashMap<String, Object> argsTable) 
	{
        try {
            ArrayList<Album> result = QueriesManager.searchAlbum((String)argsTable.get("searchString"), (long)argsTable.get("limit"), (long)argsTable.get("offset"));
			return result;
		} 
		catch (Exception e) {
			return e;
        }
	}

	@Override
	public Object deleteAccount(final HashMap<String, Object> argsTable) {
		throw new UnsupportedOperationException("Unimplemented method 'deleteAccount'");
	}

	@Override
	public Object getSongByIDs(final HashMap<String, Object> argsTable) {
		try {
            return QueriesManager.searchSongByIDs((String[])argsTable.get("IDs"));
		} 
		catch (Exception e) {
			return e;
        }
	}

	@Override
	public Object getAlbumsSongs(final HashMap<String, Object> argsTable) 
	{
        try {
            ArrayList<Song> result = QueriesManager.getAlbumSongs((String)argsTable.get("albumID"));
			terminal.printInfoln("element: " + result.size());
			return result;
		} 
		catch (Exception e) {
			return e;
        }
	}

	@Override
	public Object getArtistsSongs(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getArtistsSongs'");
	}

	@Override
	public Object getPlaylistsSongs(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getPlaylistsSongs'");
	}

	@Override
	public Object getAlbumsByIDs(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAlbumsByIDs'");
	}

	@Override
	public Object getArtistsAlbums(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getArtistsAlbums'");
	}

	@Override
	public Object searchArtists(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'searchArtists'");
	}

	@Override
	public Object getArtistsByIDs(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getArtistsByIDs'");
	}

	@Override
	public Object addPlaylist(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'addPlaylist'");
	}

	@Override
	public Object deletePlaylist(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'deletePlaylist'");
	}

	@Override
	public Object removeSongFromPlaylist(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'removeSongFromPlaylist'");
	}

	@Override
	public Object addSongToPlaylist(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'addSongToPlaylist'");
	}

	@Override
	public Object getAccountsPlaylistsBy(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAccountsPlaylistsBy'");
	}

	@Override
	public Object getAccountComments(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAccountComments'");
	}

	@Override
	public Object addComment(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'addComment'");
	}

	@Override
	public Object deleteComment(final HashMap<String, Object> argsTable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'deleteComment'");
	}
}