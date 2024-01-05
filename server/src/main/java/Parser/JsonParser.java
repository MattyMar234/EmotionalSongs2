package Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import database.PredefinedSQLCode;
import utility.OS_utility;



/**
 * Classe per il parsing di file JSON contenenti dati relativi ad artisti, album e tracce musicali.
 */
public class JsonParser 
{
    

/**
 * Legge un file JSON da un percorso specificato e restituisce un oggetto JsonNode.
 *
 * Questo metodo utilizza l'ObjectMapper di Jackson per leggere un file JSON dal percorso specificato
 * e restituisce un oggetto JsonNode che rappresenta la struttura del file JSON.
 *
 * @param path Il percorso del file JSON da leggere.
 * @return Un oggetto JsonNode rappresentante la struttura del file JSON letto.
 * @throws IOException Se si verifica un errore durante la lettura del file o se il file non esiste.
 */
    public static JsonNode readJsonFile(String path) throws IOException {
        
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(path);

        if (!file.exists()) {
            throw new IOException("File not found");
        }

        JsonNode jsonNode = objectMapper.readTree(file);
        return jsonNode;
    }

    

/**
 * Scrive un oggetto JsonNode in un file JSON nel percorso specificato.
 *
 * Questo metodo utilizza l'ObjectMapper di Jackson per scrivere un oggetto JsonNode in un file JSON
 * nel percorso specificato. L'output verrà formattato con l'indentazione abilitata.
 *
 * @param path Il percorso del file JSON in cui scrivere i dati.
 * @param jsonNode L'oggetto JsonNode da scrivere nel file JSON.
 */
    public static void writeJsonFile(String path, JsonNode jsonNode) 
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(path);

        try {
            objectMapper.writeValue(file, jsonNode);
        } catch (IOException e) {
            System.out.println("error while saving settings");
            e.printStackTrace();
        }
    }



/**
 * Restituisce il numero di elementi presenti nel file JSON nel percorso specificato.
 *
 * @param path Il percorso del file JSON da leggere.
 * @return Il numero di elementi nel file JSON, o -1 se si verifica un errore durante la lettura.
 */
    public static int getFile_element_count(String path) {

        try {
            JsonNode rootNode = readJsonFile(path);
            return rootNode.size();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }



/**
 * Analizza un file JSON contenente dati sugli artisti e restituisce un array di HashMap.
 *
 * @param path Il percorso del file JSON contenente i dati sugli artisti.
 * @return Un array contenente tre HashMap: artistsData, artistsImage e artistsGenres.
 *         Ogni HashMap contiene i dati relativi agli artisti, alle immagini degli artisti e ai generi degli artisti, rispettivamente.
 *         Restituisce null se si verifica un errore durante l'analisi.
 */
    public static Object[] parseArtists(String path) 
    {
        Object [] data = new Object[3];
        HashMap<String, Object> artistsData = new HashMap<String, Object>();
        HashMap<String, Object> artistsImage = new HashMap<String, Object>();
        HashMap<String, Object> artistsGenres = new HashMap<String, Object>();
        JsonNode rootNode = null;
        
        data[0] = artistsData;
        data[1] = artistsImage;
        data[2] = artistsGenres;


        try {
            rootNode = readJsonFile(path);

            for (JsonNode artistNode : rootNode) 
            {
                HashMap<String, Object> artistInfo = new HashMap<>();
                ArrayList<Object> artistImages = new ArrayList<>();
                List<String> genres = new ArrayList<>();

                JsonNode imagesNode = artistNode.get("images");
                JsonNode genresNode = artistNode.get("genres");

                //Informazioni
                String artistId = artistNode.get("id").asText();
                String spotify_url = artistNode.get("spotify_url").asText();
                String artistName = artistNode.get("name").asText();
                int popularity = artistNode.get("popularity").asInt();
                int followers = artistNode.get("followers").asInt();

                //uso gli stessi nomi delle colonne della tabella
                artistInfo.put(PredefinedSQLCode.Colonne.NAME.getName(), artistName);
                artistInfo.put(PredefinedSQLCode.Colonne.POPULARITY.getName(), popularity);
                artistInfo.put(PredefinedSQLCode.Colonne.FOLLOWERS.getName(), followers);
                artistInfo.put(PredefinedSQLCode.Colonne.URL.getName(), spotify_url);
                artistInfo.put(PredefinedSQLCode.Colonne.ID.getName(), artistId);
                
                artistsData.put(artistId, artistInfo);

                //Immagini
                for (JsonNode imageNode : imagesNode) {
                    HashMap<String, Object> image = new HashMap<String, Object>();

                    String imageUrl = imageNode.get("url").asText();
                    String height = imageNode.get("height").asText();
                    String width = imageNode.get("width").asText();
                  
                    image.put(PredefinedSQLCode.Colonne.ID.getName(), artistId);
                    //image.put(PredefinedSQLCode.Colonne.TYPE.getName(), PredefinedSQLCode.ImageType.ARTIST.toString());
                    image.put(PredefinedSQLCode.Colonne.IMAGE_SIZE.getName(), height + "x" + width);
                    image.put(PredefinedSQLCode.Colonne.URL.getName(), imageUrl);

                    artistImages.add(image);
                }
                artistsImage.put(artistId, artistImages);

                //Generi 
                for (JsonNode genreNode : genresNode) {
                    String genre = genreNode.asText();
                    genres.add(genre);
                }
                artistsGenres.put(artistId, genres);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return data;
    } 



/**
 * Analizza un file JSON contenente dati sugli album e restituisce un array di HashMap.
 *
 * @param path Il percorso del file JSON contenente i dati sugli album.
 * @return Un array contenente tre HashMap: albumsData, albumsImage e albumsArtists.
 *         Ogni HashMap contiene i dati relativi agli album, alle immagini degli album e agli artisti degli album, rispettivamente.
 *         Restituisce null se si verifica un errore durante l'analisi.
 */
    public static Object[] parseAlbums(String path) {
        Object[] data = new Object[3];
        HashMap<String, Object> albumsData = new HashMap<>();
        HashMap<String, Object> albumsImage = new HashMap<>();
        HashMap<String, Object> albumsArtists = new HashMap<>();

        data[0] = albumsData;
        data[1] = albumsImage;
        data[2] = albumsArtists;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(path));

            for (JsonNode albumNode : rootNode) {
                HashMap<String, Object> albumInfo = new HashMap<>();
                ArrayList<Object> albumImages = new ArrayList<>();
                List<String> artists = new ArrayList<>();

                JsonNode imagesNode = albumNode.get("images");
                JsonNode artistsNode = albumNode.get("artists_ID");

                // Informazioni
                String albumId = albumNode.get("id").asText();
                int element = albumNode.get("element").asInt();
                String spotifyUrl = albumNode.get("spotify_url").asText();
                String albumName = albumNode.get("name").asText();
                String releaseDate = albumNode.get("release_date").asText();

                
                String artist_ID = new File(path).getName();

                // Uso gli stessi nomi delle colonne della tabella
                albumInfo.put(PredefinedSQLCode.Colonne.ID.getName(), albumId);
                albumInfo.put(PredefinedSQLCode.Colonne.TYPE.getName(), "album");
                albumInfo.put(PredefinedSQLCode.Colonne.NAME.getName(), albumName);
                albumInfo.put(PredefinedSQLCode.Colonne.RELEASE_DATE.getName(), releaseDate);
                albumInfo.put(PredefinedSQLCode.Colonne.URL.getName(), spotifyUrl);
                albumInfo.put(PredefinedSQLCode.Colonne.ARTIST_ID_REF.getName(), artist_ID.replace(".json", ""));
                albumInfo.put(PredefinedSQLCode.Colonne.ELEMENT.getName(), element);

                albumsData.put(albumId, albumInfo);

                // Immagini
                for (JsonNode imageNode : imagesNode) {
                    HashMap<String, Object> image = new HashMap<>();

                    String imageUrl = imageNode.get("url").asText();
                    String height = imageNode.get("height").asText();
                    String width = imageNode.get("width").asText();

                    image.put(PredefinedSQLCode.Colonne.ID.getName(), albumId);
                    //image.put(PredefinedSQLCode.Colonne.TYPE.getName(), PredefinedSQLCode.ImageType.ALBUM.toString());
                    image.put(PredefinedSQLCode.Colonne.IMAGE_SIZE.getName(), height + "x" + width);
                    image.put(PredefinedSQLCode.Colonne.URL.getName(), imageUrl);

                    albumImages.add(image);
                }
                albumsImage.put(albumId, albumImages);

                // Artisti
                for (JsonNode artistNode : artistsNode) {
                    String artistId = artistNode.asText();
                    artists.add(artistId);
                }
                albumsArtists.put(albumId, artists);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return data;
    }



/**
 * Analizza un file JSON contenente dati sulle tracce e restituisce un array di HashMap.
 *
 * @param path Il percorso del file JSON contenente i dati sulle tracce.
 * @return Un array contenente tre HashMap: tracksData, null e tracksArtists.
 *         Ogni HashMap contiene i dati relativi alle tracce e agli artisti delle tracce, rispettivamente.
 *         Restituisce null se si verifica un errore durante l'analisi.
 */
    public static Object[] parseTracks(String path) {
        Object[] data = new Object[3];
        HashMap<String, Object> tracksData = new HashMap<>();
        HashMap<String, Object> tracksArtists = new HashMap<>();

        data[0] = tracksData;
        data[1] = null;
        data[2] = tracksArtists;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(path));

            for (JsonNode trackNode : rootNode) {
                HashMap<String, Object> trackInfo = new HashMap<>();
                List<String> artists = new ArrayList<>();

                String trackId = trackNode.get("id").asText();
                String albumId = trackNode.get("album_ID").asText();
                int durationMs = trackNode.get("duration_ms").asInt();
                String spotifyUrl = trackNode.get("spotify_url").asText();
                String trackName = trackNode.get("name").asText();
                int popularity = trackNode.get("popularity").asInt();
                JsonNode artistsNode = trackNode.get("artists_ID");

                trackInfo.put(PredefinedSQLCode.Colonne.TITLE.getName(), trackName);
                trackInfo.put(PredefinedSQLCode.Colonne.DURATION.getName(), durationMs);
                trackInfo.put(PredefinedSQLCode.Colonne.POPULARITY.getName(), popularity);
                trackInfo.put(PredefinedSQLCode.Colonne.URL.getName(), spotifyUrl);
                trackInfo.put(PredefinedSQLCode.Colonne.ID.getName(), trackId);
                trackInfo.put(PredefinedSQLCode.Colonne.ALBUM_ID_REF.getName(), albumId);

                tracksData.put(trackId, trackInfo);

                for (JsonNode artistNode : artistsNode) {
                    String artistId = artistNode.asText();
                    artists.add(artistId);
                }
                tracksArtists.put(trackId, artists);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return data;
    }
}

