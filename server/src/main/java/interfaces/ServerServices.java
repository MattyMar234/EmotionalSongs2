package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import Exceptions.InvalidPasswordException;
import Exceptions.InvalidUserNameException;
import objects.Song;

public interface ServerServices extends Remote {
    
    public void addClient(ClientServices client) throws RemoteException;
    public void disconnect(ClientServices client) throws RemoteException;

    public Object getAccount(String Email, String Password) throws RemoteException, InvalidPasswordException, InvalidUserNameException; 
    public ArrayList<Song> getMostPopularSongs(long limit, long offset) throws RemoteException;
}
