package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends Thread implements ServerServices
{
	private int port;

	public Server(int port) throws RemoteException {
		this.port = port;
	}

	
	public void run() 
	{
		Terminal terminal = Terminal.getInstance();
		terminal.printInfo_ln("Server Starting...");
		
		try {
			/*Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind("EmotionalSongs_services", this);*/
			
			UnicastRemoteObject.exportObject(this, port);
		} 
		catch (RemoteException e) {
			terminal.printError_ln("Error creating registry");
			e.printStackTrace();
			return;
		}

		
		try {
			while (true) {
				Thread.sleep(Long.MAX_VALUE);
			}
		} 
		catch (InterruptedException e) {
			try {
				UnicastRemoteObject.unexportObject(this, true);
			} catch (NoSuchObjectException e1) {
				e1.printStackTrace();
			}
		}
		
		terminal.printInfo_ln("Closing Server...");
	}
}