package server;

import java.io.*;
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
	private HashMap<ServerServicesName, Function<Object,Object>> serverFunctions = new HashMap<>();

	private Terminal terminal;
	private boolean exit = false;
	private int port;


	public ComunicationManager(int port) throws RemoteException {
		this.terminal = Terminal.getInstance();
		this.port = port;
		setDaemon(true);
		setPriority(MAX_PRIORITY);


		serverFunctions.put(ServerServicesName.ADD_ACCOUNT, this::addAccount);
		serverFunctions.put(ServerServicesName.GET_ACCOUNT, this::getAccount);
		serverFunctions.put(ServerServicesName.GET_MOST_POPULAR_SONGS, this::getMostPopularSongs);
		serverFunctions.put(ServerServicesName.GET_RECENT_PUPLISCED_ALBUMS, this::getRecentPublischedAlbum);
		serverFunctions.put(ServerServicesName.GET_MOST_POPULAR_SONGS, this::getMostPopularSongs);
		serverFunctions.put(ServerServicesName.SEARCH_SONGS, this::searchSongs);
		serverFunctions.put(ServerServicesName.SEARCH_ALBUMS, this::searchAlbums);
	}
	
	public Function<Object,Object> getServerServiceFunction(ServerServicesName name) {
		return this.serverFunctions.get(name);
	}


	
	public void run() 
	{
		ServerSocket server = null;
		String IP = "";

		/*try (ServerSocket tempSocket = new ServerSocket(port)) {
			Socket s1 = new Socket("localhost", port);
			Socket s2 = tempSocket.accept();
			IP =  (((InetSocketAddress) s2.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
			s2.close();
		} 
		catch (IOException e1) {
		}*/

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

		//faccio una copia per evitare errori
		ArrayList<ConnectionHandler> temp = (ArrayList<ConnectionHandler>) clientsThread.clone();
		for(ConnectionHandler client : clientsThread)
			client.terminate();

		for(ConnectionHandler client : temp)
			while(client.isAlive())
		
			
		terminal.printInfoln("Closing Server...");
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

	private void printFunctionExecutionTime(String functionName, String clientHost,double startTime) {
		new Thread(() ->{
				double estimatedTime = System.nanoTime() - startTime;
				double seconds = (double)estimatedTime / 1000000000.0;
				terminal.printInfoln(formatFunctionRequestTime(clientHost, functionName, seconds));
			}).start();
	}

	private void printFunctionArgs(String functionName, String clientHost) {

		new Thread(() ->{
			try {
				//terminal.printRequestln(formatFunctionRequest(clientHost, "MostPopularSongs("+limit+ ", " + offset +")"));
				terminal.printRequestln(formatFunctionRequest(clientHost, functionName));
				for (Parameter parameter : this.getClass().getMethod(functionName).getParameters()) 
				{
					String value = (String) this.getClass().getDeclaredField(parameter.getName()).get(this);
					System.out.println("Valore parametro "+ parameter.getName()+": " + value);
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}	
		}).start();	
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


// ==================================== COMUNICTION ====================================//


//====================================== SOCKET SERVICES ======================================//

	@Override
	public Object addAccount(Object args) 
	{
		final double startTime = System.nanoTime();
		final String[] keys = {"name", "username", "userID", "codiceFiscale", "email", "password", "civicNumber", "viaPiazza", "cap", "commune", "province"};
		final HashMap<String, Object> argsTable = (HashMap<String, Object>) ((Object[])args)[0];
		final String clientHost = (String)((Object[])args)[1];

		HashMap<Colonne, Object> colonne_account   = new HashMap<Colonne, Object>();
		HashMap<Colonne, Object> colonne_residenza = new HashMap<Colonne, Object>();

		//verifico che ci siano tutti i parametri necessari
		for (String str : keys) {
			if (!argsTable.containsKey(str)) {
				return new Exception("Missing argument: " + str);
			}
		}

		Object account = null;

		try {
			
			printFunctionArgs("addAccount", clientHost);
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
			account = getAccount(new Object[] {argsTable.get("email"), argsTable.get("password")});

			printFunctionExecutionTime("addAccount", clientHost, startTime);
		} 
		catch (Exception e) {
			Terminal.getInstance().printErrorln("Error function MostPopularSongs(): " + e);
			return new Exception(e);
		}
		
		return account;
	}


	@Override
	@SuppressWarnings("unchecked")
	public Object getAccount(Object args) 
	{
		final double startTime = System.nanoTime();
		final String[] keys = {"email", "password"};
		final HashMap<String, Object> argsTable = (HashMap<String, Object>) ((Object[])args)[0];
		final String clientHost = (String)((Object[])args)[1];

		Object result = null;

		//verifico che ci siano tutti i parametri necessari
		for (String str : keys) {
			if (!argsTable.containsKey(str)) {
				return new Exception("Missing argument: " + str);
			}
		}
		
		try { 
			printFunctionArgs("getAccount", clientHost);

			//cerco se esiste un account con quell'email
			Account account = QueriesManager.getAccountByEmail((String) argsTable.get("email"));

			//verifico se l'ho trovato
			if(account == null)
				return new InvalidEmailException();

			//verifico se le password combaciano
			if(!account.getPassword().equals(DigestUtils.sha256Hex((String) argsTable.get("password"))))
				return new InvalidPasswordException();

			result = account;

			printFunctionExecutionTime("getAccount", clientHost, startTime);
		} 
		catch (Exception /*| InvalidEmailException*/ e) {
			e.printStackTrace();
			return null;
		}

		return result;	
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getMostPopularSongs(Object args) 
	{
		final double startTime = System.nanoTime();
		final String[] keys = {"limit", "offset"};
		final HashMap<String, Object> argsTable = (HashMap<String, Object>) ((Object[])args)[0];
		final String clientHost = (String)((Object[])args)[1];

		//verifico che ci siano tutti i parametri necessari
		for (String str : keys) {
			if (!argsTable.containsKey(str)) {
				return new Exception("Missing argument: " + str);
			}
		}

		ArrayList<Song> result = null;

		try {
			
			printFunctionArgs("getMostPopularSongs", clientHost);
			result = QueriesManager.getTopPopularSongs((long)argsTable.get("limit"), (long)argsTable.get("offset"));
			printFunctionExecutionTime("getMostPopularSongs", clientHost, startTime);
		} 
		catch (Exception e) {
			Terminal.getInstance().printErrorln("Error function MostPopularSongs(): " + e);
			return new Exception(e);
		}
		
		return result;
	}


	@Override
	@SuppressWarnings("unchecked")
	public Object getRecentPublischedAlbum(Object args) 
	{
		final double startTime = System.nanoTime();
		final String[] keys = {"limit", "offset", "threshold"};
		final HashMap<String, Object> argsTable = (HashMap<String, Object>) ((Object[])args)[0];
		final String clientHost = (String)((Object[])args)[1];

		ArrayList<Album> result = new ArrayList<>();

		//verifico che ci siano tutti i parametri necessari
		for (String str : keys) {
			if (!argsTable.containsKey(str)) {
				return new Exception("Missing argument: " + str);
			}
		}
		
		try {

			printFunctionArgs("getRecentPublischedAlbum", clientHost);
			result = QueriesManager.getRecentPublischedAlbum((long)argsTable.get("limit"), (long)argsTable.get("offset"), (int)argsTable.get("threshold"));
			printFunctionExecutionTime("getRecentPublischedAlbum", clientHost, startTime);
		}
		catch (SQLException e) {
			printError(e);
			e.printStackTrace();	
		}
		catch (Exception e) {
			printError(e);
			e.printStackTrace();
		} 
		return result;
	}


	@Override
	@SuppressWarnings("unchecked")
	public Object searchSongs(Object args) 
	{
		final double startTime = System.nanoTime();
		final String[] keys = {"searchString", "limit", "offset"};
		final HashMap<String, Object> argsTable = (HashMap<String, Object>) ((Object[])args)[0];
		final String clientHost = (String)((Object[])args)[1];
		ArrayList<Song> result = new ArrayList<>();



		//verifico che ci siano tutti i parametri necessari
		for (String str : keys) {
			if (!argsTable.containsKey(str)) {
				return new Exception("Missing argument: " + str);
			}
		}

		
        try {

			printFunctionArgs("searchSongs", clientHost);
            result = QueriesManager.searchSong((String)argsTable.get("searchString"), (long)argsTable.get("limit"), (long)argsTable.get("offset"));
			printFunctionExecutionTime("searchSongs", clientHost, startTime);
		} 
		catch (Exception e) {
			printError(e);
            e.printStackTrace();
        }
		return result;
	}


	@Override
	@SuppressWarnings("unchecked")
	public Object searchAlbums(Object args) 
	{
		final double startTime = System.nanoTime();
		final String[] keys = {"searchString", "limit", "offset"};
		final HashMap<String, Object> argsTable = (HashMap<String, Object>) ((Object[])args)[0];
		final String clientHost = (String)((Object[])args)[1];
		ArrayList<Album> result = new ArrayList<>();



		//verifico che ci siano tutti i parametri necessari
		for (String str : keys) {
			if (!argsTable.containsKey(str)) {
				return new Exception("Missing argument: " + str);
			}
		}

        try {

			printFunctionArgs("searchAlbums", clientHost);
            result = QueriesManager.searchAlbum((String)argsTable.get("searchString"), (long)argsTable.get("limit"), (long)argsTable.get("offset"));
			printFunctionExecutionTime("searchAlbums", clientHost, startTime);
		} 
		catch (Exception e) {
			printError(e);
            e.printStackTrace();
        }
		return result;
	}
}