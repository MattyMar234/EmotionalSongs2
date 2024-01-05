package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JFileChooser;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import Parser.JsonParser;
import database.PredefinedSQLCode;
import database.QueryBuilder;
import database.PredefinedSQLCode.Colonne;
import database.PredefinedSQLCode.Tabelle;
//import me.tongfei.progressbar.ProgressBar;
//import me.tongfei.progressbar.ProgressBarStyle;
import server.Terminal.MessageType;
import server.Terminal;
import utility.FileElementCounter;
import utility.GenericThread;
import utility.OS_utility;
import utility.WaithingAnimationThread;


/** 
 * La classe Loader si occupa di caricare i dati nel database.
 */
public class Loader {

    private static Loader instance;
    private Terminal terminal;
    private App main;
    private FileType filesType = FileType.CSV;
    private int progressBarsLastValue = -1;
    private long lastprogressBarUpdate = 0;
    

/**
 * Enumerazione che rappresenta i tipi di file supportati, come JSON e CSV.
 *
 * Questa enumerazione definisce i possibili tipi di file che possono essere utilizzati nel sistema.
 * Attualmente supporta i tipi di file JSON e CSV.
 */
    private enum FileType {
        JSON, CSV
    }


/**
 * Restituisce l'istanza singola dell'oggetto Loader utilizzando il pattern Singleton.
 *
 * Questo metodo restituisce l'istanza dell'oggetto Loader e, se non è ancora stata creata,
 * ne crea una nuova utilizzando il costruttore privato. Il pattern Singleton garantisce che
 * ci sia una sola istanza dell'oggetto Loader nell'applicazione.
 *
 * @return L'istanza singola dell'oggetto Loader.
 */
    //pattern singleton
    public static Loader getInstance() {
        if (instance == null) {
            instance = new Loader();
        }
        return instance;
    }


/**
 * Costruisce un nuovo oggetto Loader utilizzando l'istanza del terminale e l'istanza principale dell'applicazione.
 *
 * Questo costruttore è privato e viene utilizzato per inizializzare l'oggetto Loader.
 * L'istanza del terminale viene ottenuta utilizzando il metodo `Terminal.getInstance()`,
 * e l'istanza principale dell'applicazione viene ottenuta utilizzando il metodo `App.getInstance()`.
 */
    private Loader() {
        this.terminal = Terminal.getInstance();
        this.main = App.getInstance();
    }



/**
 * Aggiunge una nuova colonna a una tabella specificata nel database.
 *
 * Questo metodo costruisce una query per aggiungere una colonna alla tabella specificata
 * utilizzando l'oggetto QueryBuilder. La query viene quindi eseguita nel database attraverso
 * il metodo `submitQuery` dell'istanza principale dell'applicazione.
 *
 * @param tabella L'enumerazione che rappresenta la tabella a cui aggiungere la colonna.
 * @param colonna L'enumerazione che rappresenta la colonna da aggiungere alla tabella.
 * @return true se l'operazione di aggiunta della colonna ha successo, altrimenti false.
 * @throws SQLException Se si verifica un errore durante l'esecuzione della query nel database.
 */
    public boolean addColum(Tabelle tabella, Colonne colonna) throws SQLException {

        String query = QueryBuilder.addColumn(tabella, colonna);
        System.out.println(query);
        this.main.database.submitQuery(query); 
        return true;
    }



/**
 * Costruisce le tabelle nel database utilizzando le query predefinite.
 *
 * Questo metodo itera attraverso le tabelle definite nell'enumerazione Tabelle e, se l'opzione
 * 'clear' è attiva, elimina prima ogni tabella utilizzando le relative query di eliminazione.
 * Successivamente, crea ciascuna tabella nel database utilizzando le relative query di creazione.
 *
 * @param clear Indica se eliminare le tabelle esistenti prima di crearle nuovamente.
 */
    private void buildTables(boolean clear) {
        try {
            for (Tabelle table : PredefinedSQLCode.Tabelle.values()) 
            {
                if(clear)// || table== Tabelle.SONG)
                    this.main.database.submitQuery(PredefinedSQLCode.deleteTable_Queries.get(table));

                terminal.printInfoln("Creating table: " + table);
                this.main.database.submitQuery(PredefinedSQLCode.createTable_Queries.get(table)); 
            }
        } catch (SQLException e) {
            terminal.printErrorln(e.toString());
            e.printStackTrace();
            System.exit(0);
        }
    }



/**
 * Stampa una barra di avanzamento nel terminale per indicare lo stato di un processo.
 *
 * Questo metodo crea e stampa una barra di avanzamento nel terminale, mostrando visivamente
 * lo stato di avanzamento di un processo. La barra di avanzamento include informazioni come
 * la percentuale completata, il numero di elementi completati rispetto al totale e il tempo
 * stimato rimanente per il completamento del processo.
 *
 * @param index L'indice corrente del processo.
 * @param total Il numero totale di elementi nel processo.
 */
    private void makeProgressBar(long index, long total) 
    {
        final double step = terminal.getTerminalColumns() - 32;
        StringBuilder sb = new StringBuilder();

        long startTime = System.currentTimeMillis();
        double progressPercentage = (double) index / total;
        int progressBars = (int) (progressPercentage * step); // Lunghezza della barra di avanzamento

        if(progressBarsLastValue == progressBars && startTime - lastprogressBarUpdate < 1000)
            return;

        lastprogressBarUpdate = System.currentTimeMillis();

        // Calcola il tempo trascorso
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        // Calcola il tempo stimato rimanente
        long estimatedRemainingTime = (long) ((1.0 - progressPercentage) * elapsedTime / progressPercentage);
        progressBarsLastValue = progressBars;

        // Stampa la barra di avanzamento nel terminale
        sb.append("\rLoading: [" + Terminal.Color.GREEN_BOLD_BRIGHT);
        
        for (int j = 0; j < progressBars; j++)
            sb.append("#");

        sb.append(Terminal.Color.RESET);

        for (int j = progressBars; j < step; j++)
            sb.append(" ");
            
        sb.append("] " + Terminal.Color.YELLOW_BOLD_BRIGHT + (int) (progressPercentage * 100) + "%  " +  Terminal.Color.BLUE_BOLD_BRIGHT + Long.toString(index) + Terminal.Color.RESET +"/" + Terminal.Color.BLUE_BOLD_BRIGHT + Long.toString(total) + Terminal.Color.RESET);

        for (int i = 0; i < sb.length() + 20; i++) {
            System.out.print("\b");
        }
        
        System.out.print(sb.toString());
    }

    // Formatta il tempo in ore, minuti e secondi
    private String formatTime(long millis) {
        long hours = millis / 3600000;
        long minutes = (millis % 3600000) / 60000;
        long seconds = (millis % 60000) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }



/**
 * Carica i dati dell'applicazione inizializzando il database e popolando le tabelle.
 *
 * Questo metodo guida l'utente attraverso la selezione della cartella contenente i dati del dataset.
 * Successivamente, inizializza il database creando le tabelle e carica i dati dal dataset, utilizzando
 * il tipo di file specificato (JSON o CSV).
 *
 * @return Il numero di dati caricati o un codice di errore negativo in caso di fallimento.
 * @throws IOException Se si verifica un errore durante l'interazione con l'input/output.
 * @throws SQLException Se si verifica un errore durante l'interazione con il database.
 */
    @SuppressWarnings({"rawtypes","unchecked"})
    public int loadApplicationData() throws IOException, SQLException
    {
        HashMap<String, File> foldersPath = new HashMap<String, File>();
        File database__data_folder;
        
        //==================================== SELEZIONE DEI FILE ====================================//
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        terminal.printInfoln("\nStart database initalizzation...\n");
        Terminal.getInstance().println("Dataset Folder Path:");
        Terminal.getInstance().printArrow();
        database__data_folder = new File(OS_utility.formatPath(in.readLine()));
        //in.close();
        
        //database__data_folder = new File("C:\Users\Utente\Desktop\DataSet");
        


        // final JFileChooser fileChooser = new JFileChooser(OS_utility.formatPath(System.getProperty("user.home") + "/Desktop"));
        // fileChooser.setVisible(true);
        // fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        // switch(fileChooser.showOpenDialog(null))
        // {
        //     case JFileChooser.CANCEL_OPTION:
        //         terminal.printInfoln("database configuration ended");
        //         return 0;

        //     case JFileChooser.APPROVE_OPTION:
        //         database__data_folder = new File(fileChooser.getSelectedFile().getAbsolutePath());
        //         terminal.printInfoln("select folder: " + database__data_folder);
        //         break;

        //     default:
        //         terminal.printErrorln("FileDialog Error");
        //         return 0;
        // }
        
        //===================================== CARICO I DATI =====================================//
        
        //creo le tabelle
        buildTables(false);


        if(filesType == FileType.JSON) {
            return load_JSON(foldersPath, database__data_folder);
        }
        else if(filesType == FileType.CSV) {
            return load_CSV(foldersPath, database__data_folder);
        }
        
        return -1;
    }



/**
 * Carica i dati da file CSV nella base di dati.
 *
 * Questo metodo legge e analizza i file CSV specificati e carica i dati nelle tabelle
 * del database. Vengono effettuate operazioni specifiche per ogni tipo di file (Artists, Album, Tracks).
 *
 * @param foldersPath Una mappa contenente i percorsi dei file CSV per ogni tipo.
 * @param databaseDataFolder La cartella contenente i file CSV del dataset.
 * @return Il numero di dati caricati o 0 se si verificano errori durante il caricamento.
 */
    private int load_CSV(HashMap<String, File> foldersPath, File database__data_folder) 
    {
        final String ARTIST = "Artists.csv";
        final String ALBUM = "Album.csv";
        final String TRACKS = "Tracks.csv";
        final String[] files = {ARTIST, ALBUM, TRACKS};

        HashMap<String, Integer> file_line_count = new HashMap<>();


        if (database__data_folder.isDirectory()) {
            boolean tuttePresenti = true;

            for (String file_name : files) {
                File file = new File(database__data_folder, file_name);

                if (!file.exists() || file.isDirectory()) {
                    tuttePresenti = false;
                    terminal.printErrorln("file " + file_name + " not found");
                } 
                else {
                    foldersPath.put(file_name, file);
                    terminal.printSuccesln("file " + file_name + " found");
                }
            }

            if(!tuttePresenti) {
                terminal.printErrorln(Terminal.Color.RED_BOLD_BRIGHT + "FILE MISSING" + Terminal.Color.RESET);
                terminal.printInfoln("Database configuration ended\n");
                return 0;
            }
            else {
                terminal.printSuccesln("All file found\n");
            }
        }
        else {
            terminal.printErrorln(Terminal.Color.RED_BOLD_BRIGHT + "INVALID PATH" + Terminal.Color.RESET);
            terminal.printInfoln("Database configuration ended\n");
            return 0;
        }


        //conto le linee che ha ogni file
        for (String current_File : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(foldersPath.get(current_File)))) {
                int lineCount = 0;
                

                while (br.readLine() != null) {
                    lineCount++;
                }

                file_line_count.put(current_File, lineCount - 1); //tolgo l'header
                terminal.printInfoln("Element found in " + current_File + ": " + (lineCount - 1));
                
            } catch (IOException e) {
                terminal.printErrorln(Terminal.Color.RED_BOLD_BRIGHT + e.toString() + Terminal.Color.RESET);
                terminal.printInfoln("Database configuration ended\n");
                return 0;
            }
        }

        final String[] toRemove = {"[","]","\'"," "};
        


        for (String current_File : files) 
        { 
            terminal.println("");
            switch (current_File) 
            {         
                case ARTIST -> {
                    terminal.printInfoln("loading Artist...");
                }

                case ALBUM -> {
                    terminal.printInfoln("loading Album...");
                }

                case TRACKS -> {
                    terminal.printInfoln("loading tracks...");
                }
            }

            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();} 

            long index = 0;
            try (CSVReader csvReader = new CSVReader(new FileReader(foldersPath.get(current_File)))) 
            {
                String[] record;
                long currentIndex = 0;

                final int numThreads = 8; // Numero di thread da utilizzare
                ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
                

                while ((record = csvReader.readNext()) != null) 
                {
                    final String[] finalRecord = record;
                    final String file = current_File;

                    //salto l'header
                    if(index == 0) {
                        index++;
                        continue;
                    }

                    makeProgressBar(currentIndex++, file_line_count.get(current_File));
  
                    // Aspetta finché ci sono meno di n thread attivi
                    while (((ThreadPoolExecutor) executorService).getActiveCount() >= numThreads) {
                        try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();} 
                    }
                    final long newIndex = currentIndex;
                    executorService.submit(() -> {
                        
                        try {
                            switch (file) {
                                case ARTIST -> {

                                    if(true) {
                                        break;
                                    }
                                    
                                
                                    //ottengo i dati di ogni record
                                    String spotifyUrl = finalRecord[0];
                                    int popularity = 50;
                                    long followers = 10000;
                                    try {popularity = Integer.parseInt(finalRecord[1]);}catch(Exception e) {}
                                    try {followers = Long.parseLong(finalRecord[2]);}catch(Exception e) {}
                                    String genresString = finalRecord[3];
                                    String imagesString = finalRecord[4];
                                    String artistId = finalRecord[5];
                                    String type = finalRecord[6];
                                    String name = finalRecord[7];
                                
                                
                                    //creo l'hahMap per le informazioni dell'artista
                                    HashMap<String, Object> artistInfo = new HashMap<>();

                                    //estraggo i dai delle immagini
                                    JSONArray image_node_list = new JSONArray(imagesString);
                                    
                                    //aggiungo le immagini
                                    for (int i = 0; i < image_node_list.length(); i++) 
                                    {
                                        HashMap<String, Object> image = new HashMap<String, Object>();
                                        JSONObject imageNode = image_node_list.getJSONObject(i);
                                        
                                        String imageUrl = (String)imageNode.get("url");
                                        String height   = (String)Integer.toString((int)imageNode.get("height"));
                                        String width    = (String)Integer.toString((int)imageNode.get("width"));
                                    
                                        image.put(PredefinedSQLCode.Colonne.ID.getName(), artistId);
                                        image.put(PredefinedSQLCode.Colonne.IMAGE_SIZE.getName(), height + "x" + width);
                                        image.put(PredefinedSQLCode.Colonne.URL.getName(), imageUrl);
                
                                        PredefinedSQLCode.crea_INSER_query_ed_esegui(image, PredefinedSQLCode.Tabelle.ARTIST_IMAGES, this.main);
                                    }

                                    artistInfo.put(PredefinedSQLCode.Colonne.NAME.getName(), name);
                                    artistInfo.put(PredefinedSQLCode.Colonne.POPULARITY.getName(), popularity);
                                    artistInfo.put(PredefinedSQLCode.Colonne.FOLLOWERS.getName(), followers);
                                    artistInfo.put(PredefinedSQLCode.Colonne.URL.getName(), spotifyUrl);
                                    artistInfo.put(PredefinedSQLCode.Colonne.ID.getName(), artistId);
                                
                                    //ARTIST data
                                    PredefinedSQLCode.crea_INSER_query_ed_esegui(artistInfo, PredefinedSQLCode.Tabelle.ARTIST, this.main);
                                
                                    
                                    //creo la lista dei generi
                                    for (String str : toRemove) {
                                        genresString = genresString.replace(str, "");
                                    }

                                    String[] genresList = genresString.split(",");
                                    
                                    //GENRES e Artist Genres
                                    for(String o : genresList) {

                                        HashMap<String, Object> table1 = new HashMap<String, Object>();
                                        table1.put(PredefinedSQLCode.Colonne.GENERE_MUSICALE.getName(), o);

                                        HashMap<String, Object> table2 = new HashMap<String, Object>();
                                        table2.put(PredefinedSQLCode.Colonne.GENERE_MUSICALE.getName(), o);
                                        table2.put(PredefinedSQLCode.Colonne.ID.getName(), artistId);

                                        PredefinedSQLCode.crea_INSER_query_ed_esegui(table1, PredefinedSQLCode.Tabelle.GENERI_MUSICALI, this.main);
                                        PredefinedSQLCode.crea_INSER_query_ed_esegui(table2, PredefinedSQLCode.Tabelle.GENERI_ARTISTA, this.main);   
                                    } 
                                }

                                case ALBUM -> {

                                    if(true) {
                                        break;
                                    }

                                    String id = finalRecord[0];
                                    int element = 0;
                                    try {element = Integer.parseInt(finalRecord[1]);} catch (Exception e) {}
                                    String spotifyUrl = finalRecord[2];
                                    String imagesString = finalRecord[3];
                                    String name = finalRecord[4];
                                    String releaseDate = finalRecord[5];
                                    String type = finalRecord[6];
                                    String artistsIdString = finalRecord[7];

                                    HashMap<String, Object> albumInfo = new HashMap<>();

                                    //crelo la lista degli autori dell'album
                                    for (String str : toRemove) {
                                        artistsIdString = artistsIdString.replace(str, "");
                                    }

                                    //seleziono solo quello in posizione 0
                                    String genresList = artistsIdString.split(",")[0];

                                    albumInfo.put(PredefinedSQLCode.Colonne.ID.getName(), id);
                                    albumInfo.put(PredefinedSQLCode.Colonne.TYPE.getName(), "album");
                                    albumInfo.put(PredefinedSQLCode.Colonne.NAME.getName(), name);
                                    albumInfo.put(PredefinedSQLCode.Colonne.RELEASE_DATE.getName(), releaseDate);
                                    albumInfo.put(PredefinedSQLCode.Colonne.URL.getName(), spotifyUrl);
                                    albumInfo.put(PredefinedSQLCode.Colonne.ARTIST_ID_REF.getName(), genresList);
                                    albumInfo.put(PredefinedSQLCode.Colonne.ELEMENT.getName(), element);
                                    
                                    PredefinedSQLCode.crea_INSER_query_ed_esegui(albumInfo, PredefinedSQLCode.Tabelle.ALBUM, this.main); 


                                    //estraggo i dai delle immagini
                                    JSONArray image_node_list = new JSONArray(imagesString);
                                    
                                    //aggiungo le immagini
                                    for (int i = 0; i < image_node_list.length(); i++) 
                                    {
                                        HashMap<String, Object> image = new HashMap<String, Object>();
                                        JSONObject imageNode = image_node_list.getJSONObject(i);
                                        
                                        String imageUrl = (String)imageNode.get("url");
                                        String height   = (String)Integer.toString((int)imageNode.get("height"));
                                        String width    = (String)Integer.toString((int)imageNode.get("width"));
                                    
                                        image.put(PredefinedSQLCode.Colonne.ID.getName(), id);
                                        image.put(PredefinedSQLCode.Colonne.IMAGE_SIZE.getName(), height + "x" + width);
                                        image.put(PredefinedSQLCode.Colonne.URL.getName(), imageUrl);
                
                                        PredefinedSQLCode.crea_INSER_query_ed_esegui(image, PredefinedSQLCode.Tabelle.ALBUM_IMAGES, this.main);
                                    }
                                }

                                case TRACKS -> {

                                    HashMap<String, Object> trackInfo = new HashMap<>();

                                    String albumId = finalRecord[0];
                                    String artistsIdString  = finalRecord[1];
                                    long durationMs = Long.parseLong(finalRecord[2]);
                                    String spotifyUrl = finalRecord[3];
                                    String id = finalRecord[4];
                                    String name = finalRecord[5];
                                    int popularity = Integer.parseInt(finalRecord[6]);

                                    //terminal.println("id: " + id + " name: " + name + " duration: " + durationMs + " popularity: " + popularity + " spotifyUrl: " + spotifyUrl + " albumId: " + albumId + " artistsIdString: " + artistsIdString.replace("[", "").replace("]", "").replace("\'", "").replace(" ", ""));

                                    trackInfo.put(PredefinedSQLCode.Colonne.TITLE.getName(), name);
                                    trackInfo.put(PredefinedSQLCode.Colonne.DURATION.getName(), durationMs);
                                    trackInfo.put(PredefinedSQLCode.Colonne.POPULARITY.getName(), popularity);
                                    trackInfo.put(PredefinedSQLCode.Colonne.URL.getName(), spotifyUrl);
                                    trackInfo.put(PredefinedSQLCode.Colonne.ID.getName(), id);
                                    trackInfo.put(PredefinedSQLCode.Colonne.ALBUM_ID_REF.getName(), albumId);
                                
                                    

                                    String[] artistsIdList = artistsIdString.replace("[", "").replace("]", "").replace("\'", "").replace(" ", "").split(",");

                                    //TRACK data
                                    PredefinedSQLCode.crea_INSER_query_ed_esegui(trackInfo, PredefinedSQLCode.Tabelle.SONG, this.main);
                                
                                    //AUTORI canzone
                                    for(Object id_autor : artistsIdList) 
                                    {
                                        HashMap<String, Object> table1 = new HashMap<String, Object>();
                                        table1.put(Colonne.ARTIST_ID_REF.getName(), id_autor);
                                        table1.put(Colonne.SONG_ID_REF.getName(), id);
                                        PredefinedSQLCode.crea_INSER_query_ed_esegui(table1, PredefinedSQLCode.Tabelle.SONG_AUTORS, this.main);
                                    }
                                }
                            }//switch
                        }//try
                        catch (Exception e) {
                            System.out.println(e);
                            e.printStackTrace();

                            System.out.println("LIne:" + (newIndex - 1));
                            System.exit(0);

                            int i = 0;
                            System.out.println("record:");
                            for (String string : finalRecord) {
                                System.out.println("record[" + (i++) + "]: " + string + ",");
                            }
                            System.exit(0);
                        }
                    });
                }//while

                // Attendere il completamento di tutti i thread
                executorService.shutdown();
                
                while (!executorService.isTerminated()) {
                    // Attendere finché tutti i thread non sono terminati
                }

                makeProgressBar(currentIndex++, file_line_count.get(current_File));


            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }



/**
 * Carica i dati da file JSON nella base di dati.
 *
 * Questo metodo legge e analizza i file JSON specificati e carica i dati nelle tabelle
 * del database. Vengono effettuate operazioni specifiche per ogni tipo di file (Artists, Album, Tracks).
 *
 * @param foldersPath Una mappa contenente i percorsi dei file JSON per ogni tipo.
 * @param databaseDataFolder La cartella contenente i file JSON del dataset.
 * @return Il numero di dati caricati o 0 se si verificano errori durante il caricamento.
 */
    private int load_JSON(HashMap<String, File> foldersPath, File database__data_folder) 
    {
        final String ARTIST = "Artists.json";
        final String ALBUM = "Album.json";
        final String TRACKS = "Tracks.json";
        final String[] files = {ARTIST, ALBUM, TRACKS};

        if (database__data_folder.isDirectory()) {
            boolean tuttePresenti = true;

            for (String file_name : files) {
                File file = new File(database__data_folder, file_name);

                if (!file.exists() || file.isDirectory()) {
                    tuttePresenti = false;
                    terminal.printErrorln("file " + file_name + " not found");
                } 
                else {
                    foldersPath.put(file_name, file);
                    terminal.printSuccesln("file " + file_name + " found");
                }
            }

            if(!tuttePresenti) {
                terminal.printErrorln(Terminal.Color.RED_BOLD_BRIGHT + "FILE MISSING" + Terminal.Color.RESET);
                terminal.printInfoln("Database configuration ended\n");
                return 0;
            }
            else {
                terminal.printSuccesln("All file found\n");
            }
        }
        else {
            terminal.printErrorln(Terminal.Color.RED_BOLD_BRIGHT + "INVALID PATH" + Terminal.Color.RESET);
            terminal.printInfoln("Database configuration ended\n");
            return 0;
        }

        for(String current_File : files) 
        {
            BlockingQueue<File> filesQueue = new LinkedBlockingDeque<File>();
            long fileCount = 0L;

            System.out.println("-----------------------------------------------------------------------------------------");
            terminal.printInfoln("analyzing " + foldersPath.get(current_File).getAbsolutePath());
            terminal.startWaithing(MessageType.INFO + " reading files...");
            
            if(current_File == TRACKS || current_File == ALBUM) 
            {
                for (File folder : foldersPath.get(current_File).listFiles())  {   
                    for (File file : folder.listFiles())  {
                        if (file.isFile() && file.getName().endsWith(".json")) { 
                            fileCount += 1;
                            filesQueue.add(file);
                        }  
                    }
                }
            }
            else if(current_File == ARTIST) {
                //fileCount = foldersPath.get(key).listFiles().length; 
                for (File file : foldersPath.get(ARTIST).listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".json")) { 
                        fileCount += 1;
                        filesQueue.add(file);
                    }
                }
            }

            terminal.stopWaithing();
            terminal.printInfoln("Files found: " + fileCount);

            //ProgressBar progressBar = new ProgressBar(MessageType.INFO.toString() +  " Loading " + key, fileCount, ProgressBarStyle.ASCII);
            //progressBar.start();
            //progressBar.stepTo(0);

            //do il tempo di caricare la barra
            try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
            GenericThread[] thraedList = new GenericThread[6];

            for(int i = 0; i < thraedList.length; i++) 
                thraedList[i]  = new GenericThread((data) -> {
                BlockingQueue<File> fileQueue = (BlockingQueue<File>) data[0];

                while(fileQueue.size() > 0) 
                {
                    Object[] data_for_Queries = null;
                    File file = filesQueue.poll();


                    switch(current_File) {
                        case ARTIST: data_for_Queries = JsonParser.parseArtists(file.getAbsolutePath()); break;
                        case ALBUM:  data_for_Queries = JsonParser.parseAlbums(file.getAbsolutePath());  break;
                        case TRACKS: data_for_Queries = JsonParser.parseTracks(file.getAbsolutePath());  break;
                    }

                    HashMap<String, Object> ElementsData1  = (HashMap<String, Object>) data_for_Queries[0];
                    HashMap<String, Object> ElementsImgaes = (HashMap<String, Object>) data_for_Queries[1];
                    HashMap<String, Object> ElementsData2  = (HashMap<String, Object>) data_for_Queries[2];

                    switch(current_File) 
                    {
                        case ARTIST -> {

                            //itero tutti gli ID che ci sono nel File           
                            for (String artist_ID : ElementsData1.keySet()) 
                            {
                                HashMap<String, Object> artist  = (HashMap<String, Object>) ElementsData1.get(artist_ID);       //ottengo l'HashTable che rappresenta l'artista di quell'ID
                                ArrayList<Object>       images  = (ArrayList<Object>)       ElementsImgaes.get(artist_ID);      //ottengo l'HashTable che rappresenta le immagini di quell'ID
                                ArrayList<String>       generes = (ArrayList<String>)       ElementsData2.get(artist_ID);       //ottengo la lista dei generi              

                                //ARTIST data
                                PredefinedSQLCode.crea_INSER_query_ed_esegui(artist, PredefinedSQLCode.Tabelle.ARTIST, this.main); 

                                //ARTIST Images
                                for(Object o : images) {
                                    PredefinedSQLCode.crea_INSER_query_ed_esegui((HashMap<String, Object>) o, PredefinedSQLCode.Tabelle.ARTIST_IMAGES, this.main);
                                }

                                //GENRES e Artist Genres
                                for(String o : generes) {

                                    HashMap<String, Object> table1 = new HashMap<String, Object>();
                                    table1.put(PredefinedSQLCode.Colonne.GENERE_MUSICALE.getName(), o);

                                    HashMap<String, Object> table2 = new HashMap<String, Object>();
                                    table2.put(PredefinedSQLCode.Colonne.GENERE_MUSICALE.getName(), o);
                                    table2.put(PredefinedSQLCode.Colonne.ID.getName(), artist_ID);

                                    PredefinedSQLCode.crea_INSER_query_ed_esegui(table1, PredefinedSQLCode.Tabelle.GENERI_MUSICALI, this.main);
                                    PredefinedSQLCode.crea_INSER_query_ed_esegui(table2, PredefinedSQLCode.Tabelle.GENERI_ARTISTA, this.main);
                                    
                                }
                                
                            }
                        }

                        case ALBUM -> {
                        
                            //itero tutti gli ID che ci sono nel File           
                            for (String album_ID : ElementsData1.keySet()) 
                            {
                                //System.out.println("ID: " + album_ID);
                                HashMap<String, Object> album   = (HashMap<String, Object>) ElementsData1.get(album_ID);        //ottengo l'HashTable che rappresenta l'artista di quell'ID
                                ArrayList<Object> images        = (ArrayList<Object>)       ElementsImgaes.get(album_ID);       //ottengo l'HashTable che rappresenta le immagini di quell'ID
                                ArrayList<String> albumArtists  = (ArrayList<String>)       ElementsData2.get(album_ID);        //ottengo la lista dei generi              

                                //ALBUM data
                                PredefinedSQLCode.crea_INSER_query_ed_esegui(album, PredefinedSQLCode.Tabelle.ALBUM, this.main); 

                                //ALBUM Images
                                for(Object o : images) {
                                    PredefinedSQLCode.crea_INSER_query_ed_esegui((HashMap<String, Object>) o, PredefinedSQLCode.Tabelle.ALBUM_IMAGES, this.main);
                                }

                                //GENRES e Artist Genres
                                //for(String o : albumArtists) { 
                                //    Solo se ggiungo una tabella che contiene le inform,azioni di chi sono gli aristi che hanno creato qull'album.
                                //}
                                
                                
                            }
                        }

                        case TRACKS -> {

                            //itero tutti gli ID che ci sono nel File           
                            for (String trackID : ElementsData1.keySet()) 
                            {
                                //System.out.println("ID: " + album_ID);
                                HashMap<String, Object> track   = (HashMap<String, Object>) ElementsData1.get(trackID);        //ottengo l'HashTable che rappresenta l'artista di quell'ID
                                ArrayList<String> autors_id     = (ArrayList<String>)       ElementsData2.get(trackID);        //ottengo la lista dei generi              

                                //TRACK data
                                PredefinedSQLCode.crea_INSER_query_ed_esegui(track, PredefinedSQLCode.Tabelle.SONG, this.main); 

                                //AUTORI canzone
                                for(Object id : autors_id) {

                                    HashMap<String, Object> table1 = new HashMap<String, Object>();
                                    table1.put(Colonne.ARTIST_ID_REF.getName(), id);
                                    table1.put(Colonne.SONG_ID_REF.getName(), trackID);

                                    PredefinedSQLCode.crea_INSER_query_ed_esegui(table1, PredefinedSQLCode.Tabelle.SONG_AUTORS, this.main);
                                }
                                
                            }
                        }
                    }
                    //progressBar.step();
                }   
            }, filesQueue, 50);
            
            for(int i = 0; i < thraedList.length; i++) 
                try {thraedList[i].join();} catch (InterruptedException e) {e.printStackTrace();}
            
            //progressBar.stop();
        }
        //System.out.println("-----------------------------------------------------------------------------------------\n");
        return 0;  
    }



/**
 * Stampa in modo ricorsivo i valori di una struttura dati complessa, gestendo HashMap,
 * List, Integer e String in modi specifici.
 *
 * @param obj L'oggetto da stampare in modo ricorsivo.
 */
    private static void recursivePrint(Object obj) {
        if (obj instanceof HashMap) {
            HashMap<String, Object> map = (HashMap<String, Object>) obj;
            for (String key : map.keySet()) {
                System.out.print(key + ": ");
                recursivePrint(map.get(key));
            }
        } else if (obj instanceof List) {
            List<String> list = (List<String>) obj;
            for (String item : list) {
                recursivePrint(item);
            }
        } else if (obj instanceof Integer) {
            int number = (Integer) obj;
            System.out.println(number);
        } else if (obj instanceof String) {
            String text = (String) obj;
            System.out.println(text);
        }
    }



/**
 * Converte una stringa JSON in una lista di stringhe.
 *
 * @param jsonArray La stringa JSON da convertire.
 * @return Una lista di stringhe ottenute dai nodi dell'array JSON.
 */
    private static List<String> parseJsonArray(String jsonArray) {
        List<String> result = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            JsonNode jsonNode = objectMapper.readTree(jsonArray);

            if (jsonNode.isArray()) {
                Iterator<JsonNode> elements = jsonNode.elements();
                while (elements.hasNext()) {
                    JsonNode element = elements.next();
                    result.add(element.asText());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }



    
}



