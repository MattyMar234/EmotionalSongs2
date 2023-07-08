package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientServices extends Remote {

    public void testConnection() throws RemoteException;
    
}
