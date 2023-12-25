package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import Exceptions.InvalidEmailException;
import Exceptions.InvalidPasswordException;
import Exceptions.InvalidUserNameException;
import objects.Account;
import objects.Album;
import objects.Artist;
import objects.Song;

public interface ServerServices {
    
    //public void addClient(ClientServices client) throws Exception;
    public void CloseComunication() throws Exception;
    public boolean testConnection() throws Exception;
    
    //account
    public Account addAccount(String name, String username, String userID, String codiceFiscale, String Email, String password, String civicNumber, String viaPiazza, String cap, String commune, String province) throws InvalidUserNameException, InvalidEmailException, InvalidPasswordException;
    public Account getAccount(String Email, String Password) throws RemoteException, InvalidPasswordException, InvalidUserNameException, InvalidEmailException; 
    
    //raccolte
    public ArrayList<Song>  getMostPopularSongs(long limit, long offset) throws Exception;
    public ArrayList<Album> getRecentPublischedAlbum(long limit, long offset, int threshold) throws Exception;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ricerca
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Object[] searchSongs(String searchString, long limit, long offset, int mode) throws Exception;
    public Object[] searchAlbums(String searchString, long limit, long offset) throws Exception;
    public Object[] searchArtists(String searchString, long limit, long offset) throws RemoteException;

    //canzoni
    public ArrayList<Song> getSongByIDs(String[] IDs) throws Exception;
    public ArrayList<Song> getAlbumSongs(String AlbumID) throws Exception;
    
    //playlist
    public boolean addPlaylist(String playlistName, String userID, Object image);
    public boolean deletePlaylist(String userID, String playlistID);
    public boolean addSongToPlaylist(String userID, String playlistID, String songID) throws Exception;
    public boolean removeSongFromPlaylist(String userID, String playlistID, String songID) throws Exception;
    public Object getAccountPlaylists(String userID) throws Exception;
    public ArrayList<Song> getPlaylistSongs(String playlistID) throws Exception;
    public boolean renamePlaylist(String userID, String playlistID, String newName) throws Exception;

    //Emotion
    public boolean addEmotion(String userID, String songID, String emotionType, int value, String comment) throws Exception;
    public boolean removeEmotion(String id) throws Exception;
    public Object getEmotions(String songID) throws Exception;
}

