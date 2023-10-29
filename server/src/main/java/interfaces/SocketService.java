package interfaces;

import java.util.HashMap;

public interface SocketService 
{
    //account
    public Object addAccount(HashMap<String, Object> argsTable) throws Exception;
    public Object getAccount(HashMap<String, Object> argsTable) throws Exception; 
    public Object deleteAccount(HashMap<String, Object> argsTable) throws Exception;

    //song
    public Object getMostPopularSongs(HashMap<String, Object> argsTable) throws Exception;
    public Object searchSongs(HashMap<String, Object> argsTable) throws Exception;
    public Object getSongByIDs(HashMap<String, Object> argsTable) throws Exception;
    public Object getAlbumsSongs(HashMap<String, Object> argsTable) throws Exception;
    public Object getArtistsSongs(HashMap<String, Object> argsTable) throws Exception;
    public Object getPlaylistsSongs(HashMap<String, Object> argsTable) throws Exception;

    //album
    public Object getRecentPublischedAlbum(HashMap<String, Object> argsTable) throws Exception;
    public Object searchAlbums(HashMap<String, Object> argsTable) throws Exception;
    public Object getAlbumsByIDs(HashMap<String, Object> argsTable) throws Exception; 
    public Object getArtistsAlbums(HashMap<String, Object> argsTable) throws Exception;
    
    //artisti
    public Object searchArtists(HashMap<String, Object> argsTable) throws Exception;
    public Object getArtistsByIDs(HashMap<String, Object> argsTable) throws Exception;

    //playlist
    public Object addPlaylist(HashMap<String, Object> argsTable) throws Exception;
    public Object deletePlaylist(HashMap<String, Object> argsTable) throws Exception;
    public Object removeSongFromPlaylist(HashMap<String, Object> argsTable) throws Exception;
    public Object addSongToPlaylist(HashMap<String, Object> argsTable) throws Exception;
    public Object getAccountsPlaylists(HashMap<String, Object> argsTable) throws Exception;
    public Object renamePlaylist(HashMap<String, Object> argsTable) throws Exception;

    //Comment
    public Object getAccountComments(HashMap<String, Object> argsTable) throws Exception;
    public Object addComment(HashMap<String, Object> argsTable) throws Exception;
    public Object deleteComment(HashMap<String, Object> argsTable) throws Exception;
    
    
}
