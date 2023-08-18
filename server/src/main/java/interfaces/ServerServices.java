package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import Exceptions.InvalidEmailException;
import Exceptions.InvalidPasswordException;
import Exceptions.InvalidUserNameException;
import objects.Account;
import objects.Album;
import objects.Song;

public interface ServerServices extends Remote {
    
    public void addClient(ClientServices client) throws RemoteException;
    public void disconnect(ClientServices client) throws RemoteException;
    
    
    public Account addAccount(String name, String username, String userID, String codiceFiscale, String Email, String password, String civicNumber, String viaPiazza, String cap, String commune, String province) throws RemoteException, InvalidUserNameException, InvalidEmailException;
    public Account getAccount(String Email, String Password) throws RemoteException, InvalidPasswordException, InvalidUserNameException, InvalidEmailException; 
    
    
    public ArrayList<Song>  getMostPopularSongs(long limit, long offset) throws RemoteException;
    public ArrayList<Album> getRecentPublischedAlbum(long limit, long offset, int threshold) throws RemoteException;
}
