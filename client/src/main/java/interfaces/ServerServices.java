package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Exceptions.InvalidPasswordException;
import Exceptions.InvalidUserNameException;

public interface ServerServices extends Remote {
    
    public void addClient(ClientServices client) throws RemoteException;
    public void disconnect(ClientServices client) throws RemoteException;

    public Object getAccount(String Email, String Password) throws RemoteException, InvalidPasswordException, InvalidUserNameException;

}
