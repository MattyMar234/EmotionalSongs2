import java.util.Hashtable;

import javax.imageio.spi.RegisterableService;

/**
 * The class <code>PredefinedSQLCode</code> contains the static predefined SQL query.
 * @version 1.0
 */
public class PredefinedSQLCode {
    // insert_table_query - Insert
    // select_table_query - Select
    // update_table_query - Update
    // delete_table_query - Delete
    // create view - to create view

    protected enum NomiTabelle {
        SONG("Canzone"), ARTIST("Artista"), GENERE_MUSICALE("GenereMusicale"), ALBUM("Album"), COMMENTO("Commento"), EMOZIONE("Emozione"), PLAYLIST("Playlist"), ACCOUNT("Account"), PROVINCIA("Provincia"), COMUNE("Comune"), LUOGO_RESIDENZA("LuogoResidenza"), IMAGES("Immagine"), GEN_ARTIST("GenereArtista"), SONG_ATIST("CanzoneArtista"), SONG_PLAYLIST("PlaylistCanzone");

        private String name;
       
        private NomiTabelle(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return this.name;
        }

    }

    protected static final Hashtable<NomiTabelle, String> create_table_hashtable_query = new Hashtable<NomiTabelle, String>()
        
    {
        {
        create_table_hashtable_query.put(
            NomiTabelle.GENERE_MUSICALE,
            "INSERT INTO" + NomiTabelle.GENERE_MUSICALE.toString() + " ("
                    + "Genere_musicale, "
                    + "Id, "
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.ALBUM,
            "INSERT INTO" + NomiTabelle.ALBUM.toString() + " ("
                    + "ID, "
                    + "Nome, "
                    + "Release_date, "
                    + "Spotify_URL, "
                    + "Type, "
                    + "Element, "
                    + "Id_Image" 
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.SONG,
            "INSERT INTO" + NomiTabelle.SONG.toString() + " ("
                    + "ID, "
                    + "Title, "
                    + "Spotify_URL, "
                    + "Duration_ms, "
                    + "Popularity, "
                    + "Year, "
                    + "Album, "    
                    + "Id_Image"               
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.ARTIST,
            "INSERT INTO" + NomiTabelle.ARTIST.toString() + " ("
                    + "ID, "
                    + "Nome, "
                    + "Spotify_URL, "
                    + "Followers, "
                    + "Popularity, "
                    + "Id_Image"         
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.COMMENTO,
            "INSERT INTO" + NomiTabelle.COMMENTO.toString() + " ("
                    + "ID_Commento, "
                    + "Commento, "
                    + "ID, "
                    + "Nickname, "                  
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.EMOZIONE,
            "INSERT INTO" + NomiTabelle.EMOZIONE.toString() + " ("
                    + "ID_Emozione, "
                    + "Tipo, "
                    + "Valore, "
                    + "ID, "
                    + "Nickname, "                  
            );
        }
        
        {
        create_table_hashtable_query.put(
            NomiTabelle.PLAYLIST,
            "INSERT INTO" + NomiTabelle.PLAYLIST.toString() + " ("
                    + "ID_Playlist, "
                    + "Nome, "
                    + "Data_Creazione, "
                    + "Nickname, "                
            );
        }
    
        {
        create_table_hashtable_query.put(
            NomiTabelle.ACCOUNT,
            "INSERT INTO" + NomiTabelle.ACCOUNT.toString() + " ("
                    + "Nickname, "
                    + "Nome, "
                    + "Cognome, "
                    + "Codice_Fiscale, "
                    + "ID_Residenza, "                  
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.PROVINCIA,
            "INSERT INTO" + NomiTabelle.PROVINCIA.toString() + " ("
                    + "Nome_Provincia, "                 
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.COMUNE,
            "INSERT INTO" + NomiTabelle.COMUNE.toString() + " ("
                    + "Nome_Comune, "    
                    + "Cap, "             
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.LUOGO_RESIDENZA,
            "INSERT INTO" + NomiTabelle.LUOGO_RESIDENZA.toString() + " ("
                    + "ID_Residenza, "    
                    + "Via_Piazza, "  
                    + "NumeroCivico, "
                    + "NomeComune, "
                    + "NomeProvincia, "           
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.IMAGES,
            "INSERT INTO" + NomiTabelle.IMAGES.toString() + " ("
                    + "tipe, "    
                    + "size, "  
                    + "link, "
                    + "id, "         
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.GEN_ARTIST,
            "INSERT INTO" + NomiTabelle.GEN_ARTIST.toString() + " ("
                    + "ID_Artista, "    
                    + "Id_genere, "             
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.SONG_ATIST,
            "INSERT INTO" + NomiTabelle.SONG_ARTIST.toString() + " ("
                    + "ID_Artista, "    
                    + "Id_Song, "             
            );
        }

        {
        create_table_hashtable_query.put(
            NomiTabelle.SONG_PLAYLIST,
            "INSERT INTO" + NomiTabelle.SONG_PLAYLIST.toString() + " ("
                    + "ID_Playlist, "    
                    + "Id_Song, "             
            );
        }

    };
        
    /**
     * @param city the city on which you want to create a view
     * @return the string of the SQL query
     */
    protected static String create_view_expression(String city) {
        return create_view_query[0] + city + "_users" + create_view_query[1] + "'" + city + "'";
    }

    /**
     * @param city the city for which you want to know the number of users
     * @return the string of the SQL query
     */
    protected static String create_num_users_query(String city) {
        return "SELECT suburb, count(*) FROM " + city + "_users GROUP BY suburb";
    }
}
