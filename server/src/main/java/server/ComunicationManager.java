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
		terminal.printInfo_ln("Start comunication inizilization");
		terminal.startWaithing(Terminal.MessageType.INFO + " Starting server...");
		
		
		try {
			try {Thread.sleep(ThreadLocalRandom.current().nextInt(400, 1000));} catch (Exception e) {}

			terminal.printInfo_ln("Creating registry on port " + port);
			Registry registry = null;
			
			try {
				registry = LocateRegistry.createRegistry(port);
			} catch (Exception e) {
				registry = LocateRegistry.getRegistry(port);
			}

			
			


			try {Thread.sleep(ThreadLocalRandom.current().nextInt(400, 1000));} catch (Exception e) {}
			

			terminal.printInfo_ln("Exporting objects");
			ServerServices services = (ServerServices) UnicastRemoteObject.exportObject(this, 0);
			registry.rebind(ComunicationManager.SERVICE_NAME, services);
			
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
			terminal.printSucces_ln(Terminal.Color.GREEN_BOLD_BRIGHT + "Server initialization complete" + Terminal.Color.RESET);
			terminal.printSeparator();
			terminal.printInfo_ln("Server listening on "+ Terminal.Color.MAGENTA + IP + ":" + port + Terminal.Color.RESET);
			terminal.printInfo_ln("press ENTER to end the communication");

			terminal.setAddTime(true);
			terminal.startWaithing(Terminal.MessageType.INFO + " Server Running", WaithingAnimationThread.Animation.DOTS);

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
						terminal.printError_ln("Connection lost with: " + Terminal.Color.MAGENTA + IPs.get(clientServices) + Terminal.Color.RESET);
						IPs.remove(clientServices);
						clients.remove(clientServices);

					}
				}

				try {sleep((int)1000/(clients.size() == 0 ? 1 : clients.size()));} catch (Exception e) {}
			}
			
			UnicastRemoteObject.unexportObject(this, true);
			registry.unbind(ComunicationManager.SERVICE_NAME);
			terminal.stopWaithing();
			terminal.printInfo_ln("Closing Server...");
			terminal.setAddTime(false);
    
		} 
		catch (RemoteException e) {
			terminal.printError_ln("Server fallied");
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

			Terminal.getInstance().printInfo_ln("Host connected: " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET);
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
			Terminal.getInstance().printInfo_ln("Host disconnected : " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET);
			IPs.remove(client);
		} 
		catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
	}


	private String formatFunctionRequest(String function) throws ServerNotActiveException {
		String clientHost = RemoteServer.getClientHost();
		return "Host " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET + " requested function:" + Terminal.Color.CYAN_BOLD_BRIGHT + "\"" + function + "\"" + Terminal.Color.RESET;
	}


	@Override
	public Account getAccount(String Email, String Password) throws RemoteException, InvalidPasswordException, InvalidUserNameException {
		try {
			terminal.printRequest_ln(formatFunctionRequest("getAccount() with email " + Email));
			Account account = QueriesManager.getAccountByEmail(Email);
			System.out.println(account);

			if(account == null)
				throw new InvalidEmailException();

			if(!account.getPassword().equals(Password))
				throw new InvalidPasswordException();
				
			return account;
		}
		 
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		throw new InvalidPasswordException();
		//return null;
	}


	@Override
	public ArrayList<Song> getMostPopularSongs(long limit, long offset) throws RemoteException {
		
		String clientHost = "";
		
		try {
			clientHost = RemoteServer.getClientHost();
			Terminal.getInstance().printInfo_ln("Host " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET + " requested function: MostPopularSongs("+limit+ ", " + offset +")");
			return QueriesManager.getTopPopularSongs(limit, offset);
		} 
		catch (Exception e) {
			Terminal.getInstance().printError_ln("Host " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET + " error: " + e);
			return null;
		}
	}


	@Override
	public Account addAccount(String name, String username, String userID, String codiceFiscale, String Email,
			String password, String civicNumber, String viaPiazza, String cap, String commune, String province)
			throws RemoteException, InvalidUserNameException, InvalidEmailException 
	{
		HashMap<Colonne, Object> colonne_account   = new HashMap<Colonne, Object>();
		HashMap<Colonne, Object> colonne_residenza = new HashMap<Colonne, Object>();
		Account account = null;
		String clientHost = "";
		
		try {
			clientHost = RemoteServer.getClientHost();
			Terminal.getInstance().printInfo_ln(formatFunctionRequest("addAccount()"));


			colonne_account.put(Colonne.NAME, name);
			colonne_account.put(Colonne.SURNAME, username);
			colonne_account.put(Colonne.NICKNAME, userID);
			colonne_account.put(Colonne.FISCAL_CODE, codiceFiscale);
			colonne_account.put(Colonne.EMAIL, Email);
			colonne_account.put(Colonne.PASSWORD, password);
			//colonne_account.put(Colonne.RESIDENCE_ID_REF, resd_ID);
			
			//colonne_residenza.put(Colonne.ID, resd_ID);
			colonne_residenza.put(Colonne.VIA_PIAZZA, viaPiazza);
			colonne_residenza.put(Colonne.CIVIC_NUMER, Integer.parseInt(civicNumber));
			colonne_residenza.put(Colonne.PROVINCE_NAME, province);
			colonne_residenza.put(Colonne.COUNCIL_NAME, commune);




			QueriesManager.addAccount_and_addResidence(colonne_account, colonne_residenza);
			
			return getAccount(Email, password);	
		} 
		catch (SQLException e) {
			Terminal.getInstance().printError_ln("Host " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET + " error: " + e);
			e.printStackTrace();
			
		}
		catch (Exception e) {
			Terminal.getInstance().printError_ln("Host " + Terminal.Color.MAGENTA + clientHost + Terminal.Color.RESET + " error: " + e);
			
		}

		return account;
	}
}