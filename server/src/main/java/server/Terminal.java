package server;

import database.PredefinedSQLCode;
import database.QueryBuilder;
import database.PredefinedSQLCode.SQLKeyword;
import database.PredefinedSQLCode.Tabelle;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import utility.FileElementCounter;
import utility.PathFormatter;
import utility.WaithingAnimationThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import Parser.JsonParser;

enum Color {
    //Color end string, color reset
    RESET("\033[0m"),

    // Regular Colors. Normal color, no bold, background color etc.
    BLACK("\033[0;30m"),    // BLACK
    RED("\033[0;31m"),      // RED
    GREEN("\033[0;32m"),    // GREEN
    YELLOW("\033[0;33m"),   // YELLOW
    BLUE("\033[0;34m"),     // BLUE
    MAGENTA("\033[0;35m"),  // MAGENTA
    CYAN("\033[0;36m"),     // CYAN
    WHITE("\033[0;37m"),    // WHITE

    // Bold
    BLACK_BOLD("\033[1;30m"),   // BLACK
    RED_BOLD("\033[1;31m"),     // RED
    GREEN_BOLD("\033[1;32m"),   // GREEN
    YELLOW_BOLD("\033[1;33m"),  // YELLOW
    BLUE_BOLD("\033[1;34m"),    // BLUE
    MAGENTA_BOLD("\033[1;35m"), // MAGENTA
    CYAN_BOLD("\033[1;36m"),    // CYAN
    WHITE_BOLD("\033[1;37m"),   // WHITE

    // Underline
    BLACK_UNDERLINED("\033[4;30m"),     // BLACK
    RED_UNDERLINED("\033[4;31m"),       // RED
    GREEN_UNDERLINED("\033[4;32m"),     // GREEN
    YELLOW_UNDERLINED("\033[4;33m"),    // YELLOW
    BLUE_UNDERLINED("\033[4;34m"),      // BLUE
    MAGENTA_UNDERLINED("\033[4;35m"),   // MAGENTA
    CYAN_UNDERLINED("\033[4;36m"),      // CYAN
    WHITE_UNDERLINED("\033[4;37m"),     // WHITE

    // Background
    BLACK_BACKGROUND("\033[40m"),   // BLACK
    RED_BACKGROUND("\033[41m"),     // RED
    GREEN_BACKGROUND("\033[42m"),   // GREEN
    YELLOW_BACKGROUND("\033[43m"),  // YELLOW
    BLUE_BACKGROUND("\033[44m"),    // BLUE
    MAGENTA_BACKGROUND("\033[45m"), // MAGENTA
    CYAN_BACKGROUND("\033[46m"),    // CYAN
    WHITE_BACKGROUND("\033[47m"),   // WHITE

    // High Intensity
    BLACK_BRIGHT("\033[0;90m"),     // BLACK
    RED_BRIGHT("\033[0;91m"),       // RED
    GREEN_BRIGHT("\033[0;92m"),     // GREEN
    YELLOW_BRIGHT("\033[0;93m"),    // YELLOW
    BLUE_BRIGHT("\033[0;94m"),      // BLUE
    MAGENTA_BRIGHT("\033[0;95m"),   // MAGENTA
    CYAN_BRIGHT("\033[0;96m"),      // CYAN
    WHITE_BRIGHT("\033[0;97m"),     // WHITE

    // Bold High Intensity
    BLACK_BOLD_BRIGHT("\033[1;90m"),    // BLACK
    RED_BOLD_BRIGHT("\033[1;91m"),      // RED
    GREEN_BOLD_BRIGHT("\033[1;92m"),    // GREEN
    YELLOW_BOLD_BRIGHT("\033[1;93m"),   // YELLOW
    BLUE_BOLD_BRIGHT("\033[1;94m"),     // BLUE
    MAGENTA_BOLD_BRIGHT("\033[1;95m"),  // MAGENTA
    CYAN_BOLD_BRIGHT("\033[1;96m"),     // CYAN
    WHITE_BOLD_BRIGHT("\033[1;97m"),    // WHITE

    // High Intensity backgrounds
    BLACK_BACKGROUND_BRIGHT("\033[0;100m"),     // BLACK
    RED_BACKGROUND_BRIGHT("\033[0;101m"),       // RED
    GREEN_BACKGROUND_BRIGHT("\033[0;102m"),     // GREEN
    YELLOW_BACKGROUND_BRIGHT("\033[0;103m"),    // YELLOW
    BLUE_BACKGROUND_BRIGHT("\033[0;104m"),      // BLUE
    MAGENTA_BACKGROUND_BRIGHT("\033[0;105m"),   // MAGENTA
    CYAN_BACKGROUND_BRIGHT("\033[0;106m"),      // CYAN
    WHITE_BACKGROUND_BRIGHT("\033[0;107m");     // WHITE

    private final String code;

    Color(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}


public class Terminal extends Thread {

    private static final int LINE_ELEMENT = 100;
    private static final char LINE_CHAR = '-';
    private static Terminal instance = null;
    
    private boolean running;
    private App main;
    

    private enum MessageType 
    {
        NONE(""),
        INFO("[" + Color.BLUE_BOLD + "INFO" + Color.RESET + "]"),
        ERROR("[" + Color.RED_BOLD + "ERROR" + Color.RESET + "]"),
        REQUEST("[" + Color.YELLOW_BOLD + "REQUEST" + Color.RESET + "]"),
        SUCCES("[" + Color.GREEN_BOLD + "SUCCES" + Color.RESET + "]");
        
        private final String message;

        MessageType(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    private enum Command 
    {
        HELP("help", "Elenco dei comandi"),
        START("start", "Avvia il Server"),
        CLOSE("exit", "Termina l'applicazione"),
        BUILD_SERVER("init", "inizilizza il database dell'applicazione"),
        CLEAR_DB("clear", "cancella tutte le informazioni del database"),
        PRINT_SQL("sql", "mostra i codici SQL statici creati");

        public final String value;
        private final String descrizione;

        Command(String str, String desc) {
            this.value = str;
            this.descrizione = desc;
        }

        @Override
        public String toString() {
            return value.toUpperCase() + " - " + descrizione;
        }
    }

    
    private Terminal(App main) {
        super("Server-Terminal");
        this.main = main;
        this.running = true;
    }    

    public static Terminal getInstance(App main) 
    {
        if (Terminal.instance == null) {
            Terminal.instance = new Terminal(main);
        }

        return Terminal.instance;
    }

    public static Terminal getInstance() {
        return Terminal.instance;
    }


    @Override
    public void run() 
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("type \"help\" to see available commands");
        
        while(this.running) 
        {
            try {
                printArrow();
                String command = in.readLine();
                
       
                if(command.equalsIgnoreCase(Command.HELP.value)) {
                    dumpCommands();
                }
                else if(command.equalsIgnoreCase(Command.START.value)) {
                    main.runServer();
                    in.readLine();
                    main.StopServer();
                }
                else if(command.equalsIgnoreCase(Command.CLOSE.value)) {
                    main.exit();
                }
                else if(command.equalsIgnoreCase(Command.BUILD_SERVER.value)) {
                    initializeDatabase();
                }
                else if(command.equalsIgnoreCase(Command.CLEAR_DB.value)) {
                    clearDatabase(in);
                }
                else if(command.equalsIgnoreCase(Command.PRINT_SQL.value)) {
                    printSQL();
                }

                
                
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println(e);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }
        }
    }


    private void printSQL() {

        for (Hashtable<Tabelle, String> queries : PredefinedSQLCode.elenco_QuerySQL) {
            boolean verificaTipologia = true;
            
   
            
            for (Tabelle key : queries.keySet()) {
                String sql = queries.get(key);

                for (SQLKeyword keyword : SQLKeyword.values()) {
                    String keywordString = keyword.getKeyword();

                    if(sql.contains(keyword + "\t") || sql.contains(keyword + " ") || sql.contains(keyword + "(") || sql.contains(keyword + ",") || sql.contains(keyword + ";") || sql.contains(keyword + "\n"))
                        sql = sql.replace(keywordString, Color.MAGENTA_BOLD_BRIGHT + keywordString + Color.RESET);
                    //System.out.println(keywordString);
                }



                //sql = sql.replace("(", "(\n\t").replace(",  ", ",\n\t");

                if(verificaTipologia) {
                    verificaTipologia = false;

                    for(int i = 0; i < 64; i++) System.out.print("-");
                    if (sql.contains(PredefinedSQLCode.Operazioni_SQL.CREATE.toString())) {
                        System.out.print(" [TABLE CREATION] ");
                    
                    }
                    else if (sql.contains(PredefinedSQLCode.Operazioni_SQL.DELETE.toString())) {
                        System.out.print(" [TABLE DROPPING] ");
                    }
                    else if (sql.contains(PredefinedSQLCode.Operazioni_SQL.INSERT.toString())) {
                        System.out.print(" [TABLE INSERTION] ");
                    }
                    for(int i = 0; i < 64; i++) System.out.print("-");
                    System.out.println("\n");

                }
                System.out.println(sql);
            }
        }

    }

    private int clearDatabase(BufferedReader in) throws IOException 
    {
        //System.out.println(PredefinedSQLCode.NomiTabelle.ARTIST);
        System.out.println(PredefinedSQLCode.createTable_Queries.get(PredefinedSQLCode.Tabelle.ARTIST));
        return 0;
    }

    private void buildTables(boolean clear) {
        try {
            for (Tabelle table : PredefinedSQLCode.Tabelle.values()) 
            {
                if(clear)
                    this.main.database.submitQuery(PredefinedSQLCode.deleteTable_Queries.get(table));

                System.out.println("Creating table: " + table);
                this.main.database.submitQuery(PredefinedSQLCode.createTable_Queries.get(table)); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    private int initializeDatabase() throws IOException, SQLException 
    {
        final boolean test = true;
        final String ARTIST = "Artists";
        final String ALBUM = "Album";
        final String TRACKS = "Tracks";
        final String[] folders = {ARTIST, ALBUM, TRACKS};
        HashMap<String, File> foldersPath = new HashMap<String, File>();
        File database_information_folder;


        printInfo_ln("start database configuration...");
        
        //==================================== SELEZIONE DEI FILE ====================================//
        if(!test) {

            final JFileChooser fileChooser = new JFileChooser(PathFormatter.formatPath(System.getProperty("user.home") + "/Desktop"));
            fileChooser.setVisible(true);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


            switch(fileChooser.showOpenDialog(null))
            {
                case JFileChooser.CANCEL_OPTION:
                    printInfo_ln("database configuration ended");
                    return 0;

                case JFileChooser.APPROVE_OPTION:
                    database_information_folder = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    printInfo_ln("select folder: " + database_information_folder);
                    break;

                default:
                    printError_ln("FileDialog Error");
                    return 0;
            }
        }
        else {
            database_information_folder = new File("C:\\Users\\Utente\\Desktop\\Dataset Progetto\\Output");
        }
        //==================================== VALIDITA' FILE ====================================//
        //verifico la validità della cartella
        
        if (database_information_folder.isDirectory()) {
            boolean tuttePresenti = true;

            for (String cartella : folders) {
                File subFolder = new File(database_information_folder, cartella);

                if (!subFolder.exists() || !subFolder.isDirectory()) {
                    tuttePresenti = false;
                    printError_ln(cartella + " folder not found");
                } 
                else {
                    foldersPath.put(cartella, subFolder);
                    printSucces_ln(cartella + " folder found");
                }
            }

            if(!tuttePresenti) {
                printInfo_ln("database configuration ended\n");
                return 0;
            }
            else {
                printSucces_ln("All folders found\n");
            }
        }
        else {
            printError_ln("invalid path\n");
            return 0;
        }

        //==================================== VERFICA ELEMENTI ====================================//
        
        HashMap<String, Object> songGeners_found = new HashMap<String, Object>();
        Queue<File> artistsQueue = new LinkedList<File>();
        Queue<File> albumsQueue  = new LinkedList<File>();
        Queue<File> traksQueue   = new LinkedList<File>();

        HashMap<String, Queue<File>> queues = new HashMap<String, Queue<File>>();
        queues.put(TRACKS, traksQueue);
        queues.put(ALBUM,  albumsQueue);

        long artistsCount = 0L;
        long albumCount   = 0L;
        long traksCount   = 0L;


        for(int k = 1; k < 2; k++) 
        {
            WaithingAnimationThread t = new WaithingAnimationThread("Retrieving " + folders[k] + " files");
    
            
            
            if(folders[k] == TRACKS || folders[k] == ALBUM) {

                //creo l'array gli contenenti i thread per la ricerca
                FileElementCounter[] threads = new FileElementCounter[foldersPath.get(folders[k]).listFiles().length];
                FileElementCounter.resetCounter();
                int index = 0;

                //conto gli album e salvo i file
                System.out.println("Folder:" + foldersPath.get(folders[k]).listFiles().length);
                System.out.flush();
                t.start();

                for (File folder : foldersPath.get(folders[k]).listFiles())  { 
                    threads[index++] = new FileElementCounter(folder, queues.get(folders[k]),(path) -> {
                        return JsonParser.getFile_element_count(path);
                    });
                }
                
                for (int i = 0; i < threads.length; i++) {
                    try {
                        threads[i].join();
                    } 
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

                    
                try {t.interrupt(); t.join();} catch (InterruptedException e) {}
                System.out.println("Files found: " + queues.get(folders[k]).size());

                if(folders[k] == ALBUM) {
                    albumCount = FileElementCounter.getCounterValue();
                    printInfo_ln("Album found: " + albumCount);
                }
                else if(folders[k] == TRACKS) {
                    traksCount = FileElementCounter.getCounterValue();
                    printInfo_ln("tracks found: " + traksCount);
                } 
            }
            else if(folders[k] == ARTIST) {
                System.out.flush();
                t.start();
                for (File file : foldersPath.get(ARTIST).listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".json")) { 

                        artistsCount += JsonParser.getFile_element_count(file.getAbsolutePath());
                        artistsQueue.add(file);
                    }
                }

                t.interrupt();
                try {t.join();} catch (InterruptedException e) {}
                printInfo_ln("artists found: " + artistsCount);
            }
        }

        //===================================== CARICO I DATI =====================================//
        ProgressBar artistsBar = null;
        ProgressBar albumsBar = null;
        ProgressBar tracksBar = null;
        File file;

        //creo le tabelle
        buildTables(true);
        
        artistsBar = new ProgressBar("["+ Color.BLUE_BOLD + "INFO" + Color.RESET + "] Loading Artists", artistsCount, ProgressBarStyle.ASCII);
        //generes = new ProgressBar("Loading Genres", songGeners_found.keySet().size(), ProgressBarStyle.ASCII);
        artistsBar.start();
        artistsBar.stepTo(0);

        //do il tempo di caricare la barra
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

        
        while (artistsQueue.size() > 0) 
        {
            file = artistsQueue.poll();
            Object[] data_for_Queries = JsonParser.parseArtists(file.getAbsolutePath());
            HashMap<String, Object> ArtistsInform = (HashMap<String, Object>) data_for_Queries[0];
            HashMap<String, Object> ArtistsImgs   = (HashMap<String, Object>) data_for_Queries[1];
            HashMap<String, Object> ArtistsGenres = (HashMap<String, Object>) data_for_Queries[2];
            
            //itero tutti gli ID che ci sono nel File           
            for (String artist_ID : ArtistsInform.keySet()) 
            {
                HashMap<String, Object> artist  = (HashMap<String, Object>) ArtistsInform.get(artist_ID);   //ottengo l'HashTable che rappresenta l'artista di quell'ID
                ArrayList<Object>       images  = (ArrayList<Object>)       ArtistsImgs.get(artist_ID);     //ottengo l'HashTable che rappresenta le immagini di quell'ID
                ArrayList<String>       generes = (ArrayList<String>)       ArtistsGenres.get(artist_ID);   //ottengo la lista dei generi              

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
                
                artistsBar.step();
            }
        } 
        artistsBar.stop();

        albumsBar = new ProgressBar("["+ Color.BLUE_BOLD + "INFO" + Color.RESET + "] Loading Albums", albumCount, ProgressBarStyle.ASCII);
        //generes = new ProgressBar("Loading Genres", songGeners_found.keySet().size(), ProgressBarStyle.ASCII);
        albumsBar.start();
        albumsBar.stepTo(0);
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

        while (albumsQueue.size() > 0) 
        {
            file = albumsQueue.poll();
            //System.out.println("FILE:" + file.getAbsolutePath());
            Object[] data_for_Queries = JsonParser.parseAlbums(file.getAbsolutePath());
            HashMap<String, Object> AlbumsData   = (HashMap<String, Object>) data_for_Queries[0];
            HashMap<String, Object> AlbumsImages = (HashMap<String, Object>) data_for_Queries[1];
            HashMap<String, Object> AlbumsArtists = (HashMap<String, Object>) data_for_Queries[2];
          
            //itero tutti gli ID che ci sono nel File           
            for (String album_ID : AlbumsData.keySet()) 
            {
                //System.out.println("ID: " + album_ID);
                HashMap<String, Object> album   = (HashMap<String, Object>) AlbumsData.get(album_ID);       //ottengo l'HashTable che rappresenta l'artista di quell'ID
                ArrayList<Object> images       = (ArrayList<Object>)       AlbumsImages.get(album_ID);     //ottengo l'HashTable che rappresenta le immagini di quell'ID
                ArrayList<String> albumArtists  = (ArrayList<String>)       AlbumsArtists.get(album_ID);   //ottengo la lista dei generi              

                //ALBUM data
                PredefinedSQLCode.crea_INSER_query_ed_esegui(album, PredefinedSQLCode.Tabelle.ALBUM, this.main); 

                //ALBUM Images
                for(Object o : images) {
                    PredefinedSQLCode.crea_INSER_query_ed_esegui((HashMap<String, Object>) o, PredefinedSQLCode.Tabelle.ALBUM_IMAGES, this.main);
                }

                /*//GENRES e Artist Genres
                for(String o : albumArtists) { 
                    Solo se ggiungo una tabella che contiene le inform,azioni di chi sono gli aristi che hanno creato qull'album.
                }*/
                
                albumsBar.step();
            }
        }
        albumsBar.stop();

        return 0;  
    }

    public static void recursivePrint(Object obj) {
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

    private void dumpCommands() {
        System.out.println();
        for (Command commad : Command.values()) {
            System.out.println(commad);
        } 
        System.out.println();
    }

    public static void printList(List<String> list) {
        for (String item : list) {
            System.out.println("- " + item);
        }
    }


    public synchronized void printArrow () {
        System.out.print("> ");
    }

    private synchronized void printOnTerminal(MessageType type, String message, Color MessageColor) {

        //System.out.print("\033[s");
        //System.out.print("\033[100D");
        //System.out.print("\n");
        //System.out.print("\033[3A");
        

        //if(MessageColor == null)
        System.out.print(type + message);   
        //System.out.print("\033[100D");
        //System.out.print("\033[3B");
        

        //System.out.print("\033[u");
       
    }

    public void print_ln(String message) {
        printOnTerminal(MessageType.NONE, message + "\n", null);
    }


    public void printInfo_ln(String message) {
        printOnTerminal(MessageType.INFO, " " + message + "\n", null);
    }

    public void printSucces_ln(String message) {
        printOnTerminal(MessageType.SUCCES, " " + message + "\n", null);
    }

    public void printError_ln(String message) {
        printOnTerminal(MessageType.ERROR, " " + message + "\n", null);
    }

    public void printConnection_ln(String message) {
        printOnTerminal(MessageType.ERROR, " " + message + "\n", null);
    }

    public void printLine() {
        
        String line = "";
        for(int i = 0; i < Terminal.LINE_ELEMENT; i++) {
            line += LINE_CHAR;
        }
        print_ln(line);
    }
}
