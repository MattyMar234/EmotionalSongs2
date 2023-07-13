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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;

import interfaces.ClientServices;
import interfaces.ServerServices;
import utility.WaithingAnimationThread;

public class Server extends Thread implements ServerServices
{
	private ArrayList<ClientServices> clients = new ArrayList<ClientServices>();
	private Hashtable<ClientServices,String> IPs = new Hashtable<ClientServices,String>();
	private int port;
	private boolean exit = false;

	private static final String SERVICE_NAME = "EmotionalSongs_services";

	public Server(int port) throws RemoteException {
		this.port = port;
	}

	
	public void run() 
	{
		Terminal terminal = Terminal.getInstance();

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
			registry.rebind(Server.SERVICE_NAME, services);
			
			try {Thread.sleep(ThreadLocalRandom.current().nextInt(400, 1000));} catch (Exception e) {}
		

			//InetAddress localhost = InetAddress.getLocalHost();
            //String ipAddress = localhost.getHostAddress();

			//Socket socket = new Socket();
			//socket.connect(new InetSocketAddress("google.com", 80));
			//String IP = socket.getLocalAddress().getHostAddress();
			String IP = "";

			Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while( networkInterfaceEnumeration.hasMoreElements()){
                for ( InterfaceAddress interfaceAddress : networkInterfaceEnumeration.nextElement().getInterfaceAddresses())
                    if ( interfaceAddress.getAddress().isSiteLocalAddress())
                        IP = interfaceAddress.getAddress().getHostAddress();
			}

			terminal.stopWaithing();
			terminal.printSeparator();
			terminal.printSucces_ln(Color.GREEN_BOLD_BRIGHT + "Server initialization complete" + Color.RESET);
			terminal.printSeparator();
			terminal.printInfo_ln("Server listening on "+ Color.MAGENTA + IP + ":" + port + Color.RESET);
			terminal.printInfo_ln("press ENTER to end the communication");

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
						terminal.printError_ln("Connection lost with: " + Color.MAGENTA + IPs.get(clientServices) + Color.RESET);
						IPs.remove(clientServices);
						clients.remove(clientServices);

					}
				}

				try {sleep((int)1000/clients.size());} catch (Exception e) {}
				
			}
			
			UnicastRemoteObject.unexportObject(this, true);
			registry.unbind(Server.SERVICE_NAME);
			terminal.stopWaithing();
			terminal.printInfo_ln("Closing Server...");
    
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
			//String clientHost = RemoteServer.getClientHost();

			String clientHost = "";

			Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while( networkInterfaceEnumeration.hasMoreElements()){
                for ( InterfaceAddress interfaceAddress : networkInterfaceEnumeration.nextElement().getInterfaceAddresses())
                    if ( interfaceAddress.getAddress().isSiteLocalAddress())
                        clientHost = interfaceAddress.getAddress().getHostAddress();
			}

			Terminal.getInstance().printInfo_ln("Host connected: " + Color.MAGENTA + clientHost + Color.RESET);
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
			Terminal.getInstance().printInfo_ln("Host disconnected : " + Color.MAGENTA + clientHost + Color.RESET);
			IPs.remove(client);
		} 
		catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
	}


	@Override
	public Object getAccount(String Email, String Password) throws RemoteException {
		try {
			String clientHost = RemoteServer.getClientHost();
			Terminal.getInstance().printInfo_ln("Host " + Color.MAGENTA + clientHost + Color.RESET + " requested account");
		} catch (Exception e) {

		}
		return null;
	}
}