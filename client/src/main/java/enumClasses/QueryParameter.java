package enumClasses;


public enum QueryParameter {
    NAME("nome"),
    USERNAME("cognome"),
    USER_ID("user_id"),
    CODICE_FISCALE("codice_fiscale"),
    EMAIL("email"),
    PASSWORD("password"),
    CIVIC_NUMBER("civic_number"),
    VIA_PIAZZA("piazza"),
    CAP("cap"),
    COMMUNE("comune"),
    PROVINCE("provincia"),
    LIMIT("limit"),
    OFFSET("offset"),
    THRESHOLD("threshold"),
    SEARCH_STRING("search_string"),
    IDS("ids"),
    ALBUM_ID("album_id"),
    ACCOUNT_ID("account_id"),
    PLAYLIST_NAME("playlist_name"),
    PLAYLIST_ID("playlist_id"),
    SONG_ID("song_id"),
    NEW_NAME("nuovo_name"),
    COMMENT("commento"),
    COMMENT_ID("id_commento");



    private String text;

    QueryParameter(String...str) {
        this.text = str[0];
    }

    
    @Override
    public String toString() {
        return text;
    }
}
