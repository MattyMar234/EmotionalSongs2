package enumclass;

/**
 * Enumerazione che rappresenta i parametri di query utilizzati nel sistema.
 * Ogni valore enum contiene una stringa associata utilizzata come parametro di query.
 */
public enum QueryParameter 
{    
    NAME("Nome"),
    USERNAME("Cognome"),
    USER_ID("UserId"),
    CODICE_FISCALE("Codice_fiscale"),
    EMAIL("Email"),
    PASSWORD("Password"),
    CIVIC_NUMBER("CivicNumber"),
    VIA_PIAZZA("Piazza"),
    CAP("CAP"),
    COMMUNE("Comune"),
    PROVINCE("Provincia"),
    LIMIT("Limit"),
    OFFSET("Offset"),
    THRESHOLD("Threshold"),
    SEARCH_STRING("SearchString"),
    ID("IDs"),
    ALBUM_ID("AlbumID"),
    ACCOUNT_ID("AccountID"),
    PLAYLIST_NAME("PlaylistName"),
    PLAYLIST_ID("PlaylistID"),
    SONG_ID("SongID"),
    NEW_NAME("NewName"),
    COMMENT("Commento"),
    COMMENT_ID("CommentID"),
    ARTIST_ID("ID_Artista"),
    EMOZIONE("Emozione"),
    VAL_EMOZIONE("Val_Emozione"),
    MODE("Mode");


    private String text;

    QueryParameter(String str) {
        this.text = str;
    }

    
    /*@Override
    public String toString() {
        return text;
    }*/
}
