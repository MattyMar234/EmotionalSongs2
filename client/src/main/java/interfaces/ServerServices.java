package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerServices extends Remote {
    
    public void addClient(ClientServices client) throws RemoteException;
    public void disconnect(ClientServices client) throws RemoteException;

    public Object getAccount(String Email, String Password) throws RemoteException;

}
