package server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.codec.digest.DigestUtils;

import Exceptions.InvalidEmailException;
import Exceptions.InvalidPasswordException;
import Exceptions.InvalidUserNameException;
import database.DatabaseManager;
import database.QueryBuilder;
import database.QueriesManager;
import database.PredefinedSQLCode.Colonne;
import database.PredefinedSQLCode.Tabelle;
import interfaces.ClientServices;
import interfaces.ServerServices;
import objects.Account;
import objects.Album;
import objects.Song;
import utility.WaithingAnimationThread;

public class ComunicationManager extends Thread implements ServerServices
{
	private ArrayList<ClientServices> clients = new ArrayList<ClientServices>();
	private Hashtable<ClientServices,String> IPs = new Hashtable<ClientServices,String>();
	
	private Terminal terminal;
	private boolean exit = false;
	private int port;

	private static final String SERVICE_NAME = "EmotionalSongs_services";

	public ComunicationManager(int port) throws RemoteException {
		this.port = port;
	}

	
	public void run() 
	{
		this.terminal = Terminal.getInstance();
		terminal.printInfoln("Start comunication inizilization");
		terminal.startWaithing(Terminal.MessageType.INFO + " Starting server...");
		
		
		try {
			try {Thread.sleep(ThreadLocalRandom.current().nextInt(400, 1000));} catch (Exception e) {}

			terminal.printInfoln("Creating registry on port " + port);
			Registry registry = null;
			
			try {
				registry = LocateRegistry.createRegistry(port);
			} catch (Exception e) {
				registry = LocateRegistry.getRegistry(port);
			}

			
			


			try {Thread.sleep(ThreadLocalRandom.current().nextInt(400, 1000));} catch (Exception e) {}
			

			terminal.printInfoln("Exporting objects");
			ServerServices services = (ServerServices) UnicastRemoteObject.exportObject(this, 0);
			//registry.rebind(ComunicationManager.SERVICE_NAME, services);
			
			try {Thread.sleep(ThreadLocalRandom.current().nextInt(400, 1000));} catch (Exception e) {}
		

			//InetAddress localhost = InetAddress.getLocalHost();
            //String ipAddress = localhost.getHostAddress();

			//Socket socket = new Socket();
			//socket.connect(new InetSocketAddress("google.com", 80));
			//String IP = socket.getLocalAddress().getHostAddress();
			String IP = "";

			//stampa L'IP del server
			Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while( networkInterfaceEnumeration.hasMoreElements()){
                for ( InterfaceAddress interfaceAddress : networkInterfaceEnumeration.nextElement().getInterfaceAddresses())
                    if ( interfaceAddress.getAddress().isSiteLocalAddress())
                        IP = interfaceAddress.getAddress().getHostAddress();
			}

			terminal.stopWaithing();
			terminal.printSeparator();
			terminal.printSuccesln(Terminal.Color.GREEN_BOLD_BRIGHT + "Server initialization complete" + Terminal.Color.RESET);
			terminal.printSeparator();
			terminal.printInfoln("Server listening on "+ Terminal.Color.MAGENTA + IP + ":" + port + Terminal.Color.RESET);
			terminal.printInfoln("press ENTER to end the communication");

			terminal.setAddTime(true);
			terminal.startWaithing(Terminal.MessageType.INFO + " Server Running", WaithingAnimationThread.Animation.DOTS);

			registry.rebind(ComunicationManager.SERVICE_NAME, services);

			int index = 0;
			while (!exit) 
			{
				if(clients.size() > 0) {
					index = (++index) % clients.size();
					ClientServices clientServices = clients.get(index);
					
					try {
						clientServices.testConnection();
					}
					catch (Exception e) {
						terminal.printErrorln("Connection lost with: " + Terminal.Color.MAGENTA + IPs.get(clientServices) + Terminal.Color.RESET);
						IPs.remove(clientServices);
						clients.remove(clientServices);

					}
				}

				try {sleep((int)1000/(clients.size() == 0 ? 1 : clients.size()));} catch (Exception e) {}
			}
			
			UnicastRemoteObject.unexportObject(this, true);
			registry.unbind(ComunicationManager.SERVICE_NAME);
			terminal.stopWaithing();
			terminal.printInfoln("Closing Server...");
			terminal.setAddTime(false);
    
		} 
		catch (RemoteException e) {
			terminal.printErrorln("Server fallied");
			e.printStackTrace();
			return;
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}	

		
	}

	public void terminate() {
		this.exit = true;
	}

	


	@Override
	public synchronized void addClient(ClientServices client) throws RemoteException 
	{
		clients.add(client);
		
		try {
			String clientHost = RemoteServer.getClientHost();

			/*String clientHost = "";

			Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while( networkInterfaceEnumeration.hasMoreElements()){
                for ( InterfaceAddress interfaceAddress : networkInterfaceEnumeration.nextElement().getInterfaceAddresses())
                    if ( interfaceAddress.getAddress().isSiteLocalAddress())
                        clientHost = interfaceAddress.getAddress().getHostAddress();
			}*/

			Terminal.getInstance().printInfoln("Host connected: " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET);
			IPs.put(client, clientHost);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void disconnect(ClientServices client) throws RemoteException 
	{
		clients.remove(client);
	
		try {
			String clientHost = RemoteServer.getClientHost();
			Terminal.getInstance().printInfoln("Host disconnected : " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET);
			IPs.remove(client);
		} 
		catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
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


	@Override
	public ArrayList<Song> getMostPopularSongs(long limit, long offset) throws RemoteException 
	{
		final Terminal terminal = Terminal.getInstance();
		final long startTime = System.nanoTime();

		ArrayList<Song> result = null;

		try {
			final String clientHost = RemoteServer.getClientHost();
			
			new Thread(() ->{
				terminal.printRequestln(formatFunctionRequest(clientHost, "MostPopularSongs("+limit+ ", " + offset +")"));
			}).start();

			result = QueriesManager.getTopPopularSongs(limit, offset);


			new Thread(() ->{
				double estimatedTime = System.nanoTime() - startTime;
				double seconds = (double)estimatedTime / 1000000000.0;
				terminal.printInfoln(formatFunctionRequestTime(clientHost, "MostPopularSongs("+limit+ ", " + offset +")", seconds));
			}).start();
			
			
		} 
		catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			Terminal.getInstance().printErrorln("Error function MostPopularSongs(): " + e);
		}
		
		return result;
		
	}


	@Override
	public Account getAccount(String Email, String password) throws RemoteException, InvalidPasswordException, InvalidUserNameException 
	{
		Account account = null;
		final Terminal terminal = Terminal.getInstance();
		final long startTime = System.nanoTime();
		
		try {
			final String clientHost = RemoteServer.getClientHost();
			
			new Thread(() ->{
				terminal.printRequestln(formatFunctionRequest(clientHost, "getAccount(email:"+Email+")"));
			}).start();


			account = QueriesManager.getAccountByEmail(Email);


			if(account == null)
				throw new InvalidEmailException();

			if(!account.getPassword().equals(DigestUtils.sha256Hex(password)))
				throw new InvalidPasswordException();

			
			new Thread(() ->{
				double estimatedTime = System.nanoTime() - startTime;
				double seconds = (double)estimatedTime / 1000000000.0;
				terminal.printInfoln(formatFunctionRequestTime(clientHost, "getAccount(email:"+Email+")", seconds));
			}).start();
			


		} 
		catch (ServerNotActiveException e) {
			Terminal.getInstance().printErrorln("Error function MostPopularSongs(): " + e);
		}
		catch (Exception e) {
			Terminal.getInstance().printErrorln("Error function MostPopularSongs(): " + e);
		}

		return account;
	}



	@Override
	public Account addAccount(String name, String username, String userID, String codiceFiscale, String Email,
			String password, String civicNumber, String viaPiazza, String cap, String commune, String province)
			throws RemoteException, InvalidUserNameException, InvalidEmailException 
	{
		HashMap<Colonne, Object> colonne_account   = new HashMap<Colonne, Object>();
		HashMap<Colonne, Object> colonne_residenza = new HashMap<Colonne, Object>();
		
		final Terminal terminal = Terminal.getInstance();
		final long startTime = System.nanoTime();
		Account account = null;
		

		try {
			final String clientHost = RemoteServer.getClientHost();
			
			new Thread(() ->{
				terminal.printRequestln(formatFunctionRequest(clientHost, "addAccount()"));
			}).start();

			colonne_account.put(Colonne.NAME, name);
			colonne_account.put(Colonne.SURNAME, username);
			colonne_account.put(Colonne.NICKNAME, userID);
			colonne_account.put(Colonne.FISCAL_CODE, codiceFiscale);
			colonne_account.put(Colonne.EMAIL, Email);
			colonne_account.put(Colonne.PASSWORD, DigestUtils.sha256Hex(password));
			//colonne_account.put(Colonne.RESIDENCE_ID_REF, resd_ID);
			
			//colonne_residenza.put(Colonne.ID, resd_ID);
			colonne_residenza.put(Colonne.VIA_PIAZZA, viaPiazza);
			colonne_residenza.put(Colonne.CIVIC_NUMER, Integer.parseInt(civicNumber));
			colonne_residenza.put(Colonne.PROVINCE_NAME, province);
			colonne_residenza.put(Colonne.COUNCIL_NAME, commune);


			QueriesManager.addAccount_and_addResidence(colonne_account, colonne_residenza);
			account = getAccount(Email, password);	

			new Thread(() ->{
				double estimatedTime = System.nanoTime() - startTime;
				double seconds = (double)estimatedTime / 1000000000.0;
				terminal.printInfoln(formatFunctionRequestTime(clientHost, "addAccount()", seconds));
			}).start();

		} 
		catch (ServerNotActiveException e) {
			Terminal.getInstance().printErrorln("Error function MostPopularSongs(): " + e);
		}
		catch (Exception e) {
			Terminal.getInstance().printErrorln("Error function MostPopularSongs(): " + e);
		}

		return account;
	}


	@Override
	public ArrayList<Album> getRecentPublischedAlbum(long limit, long offset, int threshold) throws RemoteException {

		Account account = null;
		final Terminal terminal = Terminal.getInstance();
		final long startTime = System.nanoTime(); 
		ArrayList<Album> result = new ArrayList<>();

		
		try {
			final String clientHost = RemoteServer.getClientHost();
			new Thread(() ->{
				terminal.printRequestln(formatFunctionRequest(clientHost, "getRecentPublischedAlbum(limit: " + limit + ", offset: " + offset + ", threshold: " + threshold + ")"));
			});
			
			result = QueriesManager.getRecentPublischedAlbum(limit, offset, threshold);
			
			new Thread(() ->{
				double estimatedTime = System.nanoTime() - startTime;
				double seconds = (double)estimatedTime / 1000000000.0;
				terminal.printInfoln(formatFunctionRequestTime(clientHost, "getRecentPublischedAlbum(limit: " + limit + ", offset: " + offset + ", threshold: " + threshold + ")", seconds));
			}).start();
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
}