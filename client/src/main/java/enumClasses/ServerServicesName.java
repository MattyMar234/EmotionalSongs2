package enumClasses;

/**
 * Questa classe enum rappresenta il servizio richiesto al server
 */
public enum ServerServicesName 
{
    //account
    ADD_ACCOUNT,
    GET_ACCOUNT,
    DELETE_ACCOUNT,
    
    //research
    SEARCH_SONGS,
    SEARCH_ALBUMS,
    SEARCH_ARTISTS,

    //utility
    GET_MOST_POPULAR_SONGS,
    GET_RECENT_PUPLISCED_ALBUMS,
    DISCONNECT,
    PING,
    
    //song
    GET_SONG_BY_IDS,
    GET_ARTIST_SONGS,
    
    //album
    GET_ALBUM_SONGS,
    GET_ALBUM_BY_ID,
    GET_ARTIST_ALBUMS,

    //artist
    GET_ARTIST_BY_ID,


    //playlist
    ADD_PLAYLIST,
    DELETE_PLAYLIST,
    GET_ACCOUNT_PLAYLIST,
    ADD_SONG_PLAYLIST,
    GET_PLAYLIST_SONGS,
    REMOVE_SONG_PLAYLIST, 
    RENAME_PLAYLIST,

    //emotion
    ADD_EMOTION,
    REMOVE_EMOTION,
    GET_SONG_EMOTION,
    GET_COMMENTS_SONG_FOR_ACCOUNT,
    GET_COMMENTS_SONG,
    GET_ACCOUNT_EMOTIONS;
}