package interfaces;

import java.util.HashMap;

public interface SocketService 
{
    /////////////////////////////////////////////////////////////////////////////////
    //Account
    /////////////////////////////////////////////////////////////////////////////////
    public Object addAccount(HashMap<String, Object> argsTable) throws Exception;
    public Object getAccount(HashMap<String, Object> argsTable) throws Exception; 
    public Object deleteAccount(HashMap<String, Object> argsTable) throws Exception;


    /////////////////////////////////////////////////////////////////////////////////
    //Song
    /////////////////////////////////////////////////////////////////////////////////
    public Object getMostPopularSongs(HashMap<String, Object> argsTable) throws Exception;
    public Object getSongByIDs(HashMap<String, Object> argsTable) throws Exception;
    public Object getAlbumsSongs(HashMap<String, Object> argsTable) throws Exception;
    public Object getArtistSongs(HashMap<String, Object> argsTable) throws Exception;
    public Object getPlaylistSongs(HashMap<String, Object> argsTable) throws Exception;


    /////////////////////////////////////////////////////////////////////////////////
    //Album
    /////////////////////////////////////////////////////////////////////////////////
    public Object getRecentPublischedAlbum(HashMap<String, Object> argsTable) throws Exception;
    public Object getAlbumByID(HashMap<String, Object> argsTable) throws Exception; 
    public Object getArtistAlbums(HashMap<String, Object> argsTable) throws Exception;
    

    /////////////////////////////////////////////////////////////////////////////////
    //Artisti
    /////////////////////////////////////////////////////////////////////////////////
    public Object getArtistsByIDs(HashMap<String, Object> argsTable) throws Exception;
    
    /////////////////////////////////////////////////////////////////////////////////
    //Playlist
    /////////////////////////////////////////////////////////////////////////////////
    public Object addPlaylist(HashMap<String, Object> argsTable) throws Exception;
    public Object deletePlaylist(HashMap<String, Object> argsTable) throws Exception;
    public Object removeSongFromPlaylist(HashMap<String, Object> argsTable) throws Exception;
    public Object addSongToPlaylist(HashMap<String, Object> argsTable) throws Exception;
    public Object getAccountsPlaylists(HashMap<String, Object> argsTable) throws Exception;
    public Object renamePlaylist(HashMap<String, Object> argsTable) throws Exception;
    

    /////////////////////////////////////////////////////////////////////////////////
    //Emozioni
    /////////////////////////////////////////////////////////////////////////////////
    public Object getSongEmotion(HashMap<String, Object> argsTable) throws Exception;
    public Object addEmotion(final HashMap<String, Object> argsTable) throws Exception;
    public Object deleteEmotion(final HashMap<String, Object> argsTable) throws Exception;
    

    /////////////////////////////////////////////////////////////////////////////////
    //Ricerca
    /////////////////////////////////////////////////////////////////////////////////
    public Object searchSongs(HashMap<String, Object> argsTable) throws Exception;
    public Object searchAlbums(HashMap<String, Object> argsTable) throws Exception;
    public Object searchArtists(HashMap<String, Object> argsTable) throws Exception;
    
}
