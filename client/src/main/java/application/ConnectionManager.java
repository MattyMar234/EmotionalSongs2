package application;

import java.util.ArrayList;

import interfaces.ClientServices;
import interfaces.ServerServices;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ConnectionManager extends UnicastRemoteObject implements ClientServices {

    //Singleton pattern
    private static ConnectionManager manager;

    private String hostAddress;
    private int hostPort;
    private Registry registry;

    private ConnectionManager() throws RemoteException {
		super();
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
	 * Tests a connection to the actual server
	 * @return success of the connection
	 */
	public boolean validConnection() 
	{
		boolean success = true;
		
		try {
			Registry registry = LocateRegistry.getRegistry(hostAddress, hostPort);
			ServerServices test = (ServerServices) registry.lookup("EmotionalSongs_services");
			test.addClient(this);
			success = (test != null);
		} catch (RemoteException | NotBoundException e) {
			success = false;
		} catch (ClassCastException e) {
			success = false;
			e.printStackTrace();
		}
		
		return true;
	}

    /**
	 * Tests a custom connection to a Watchneighbours server
	 * @param host server's address
	 * @param port server's port
	 * @return success of the connection
	 */
	public boolean testCustomConnection(String host, int port) {
		/*String backupHost = hostAddress;
		int backupPort = hostPort;
		
		//setConnectionData(host, port);
		boolean success = validConnection();

		//setConnectionData(backupHost, backupPort);
		
		return success;*/

		try {
			Registry registry = LocateRegistry.getRegistry(host, port);
			ServerServices test = (ServerServices) registry.lookup("EmotionalSongs_services");
			test.addClient(this);
			
		} catch (RemoteException | NotBoundException e) {
		
		} catch (ClassCastException e) {
		
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public void testConnection() throws RemoteException {
		System.out.println("server request");
	}

}


