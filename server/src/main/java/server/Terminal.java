package server;

import database.PredefinedSQLCode;
import database.QueryBuilder;
import database.PredefinedSQLCode.Tabelle;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import utility.PathFormatter;

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

                this.main.database.submitQuery(PredefinedSQLCode.createTable_Queries.get(table)); 
                System.out.println("Table " + table + " created");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    private int initializeDatabase() throws IOException 
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
                printInfo_ln("database configuration ended");
                return 0;
            }
            else {
                printSucces_ln("All folders found");
            }
        }
        else {
            printError_ln("invalid path");
            return 0;
        }

        //==================================== VERFICA ELEMENTI ====================================//
        //verifico ci sono èresenti dei file
        
        File[] Artistfiles;
        File[] Tracksfiles;
        //File[] Albumfiles;

        Queue<File> Albumfiles = new LinkedList<File>();
        long albumCount = 0;

        /*if (Artistfiles == null) {
            printError_ln("the folder "+ foldersPath.get(ARTIST) + " does not contain any files");
            return 0;
        }*/
        
        Artistfiles = foldersPath.get(ARTIST).listFiles();

        for (File folder : foldersPath.get(ALBUM).listFiles()) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".json")) 
                { 
                    Albumfiles.add(file);
                    albumCount += JsonParser.getAlbumsFile_element_count(file.getAbsolutePath());
                }
            }
            System.out.println(albumCount);
        }

        System.out.println(albumCount);
        
        if(true) {
            return 0;
        }

        


        
        
        
        //creo le tabelle
        buildTables(true);

        ArrayList<File> files = new ArrayList<File>();
        HashMap<String, Object> songGeners_found = new HashMap<String, Object>();
        long artistsID_found = 0;

        ProgressBar artists = null;
        ProgressBar generes = null;
        ProgressBar images = null;

        //conto gli elementi
        for (File file : Artistfiles) {
            if (file.isFile() && file.getName().endsWith(".json")) 
            { 
                files.add(file);
                Object[] data_for_Queries = JsonParser.parseArtists(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
                HashMap<String, Object> ArtistsInf = (HashMap<String, Object>) data_for_Queries[0];
                HashMap<String, Object> ArtistsGenres = (HashMap<String, Object>) data_for_Queries[2];
        
                artistsID_found += ArtistsInf.keySet().size();

                for (String key : ArtistsGenres.keySet()) {
                    for (String genre : (ArrayList<String>) ArtistsGenres.get(key)) {
                        if(songGeners_found.get(genre) == null) {
                            songGeners_found.put(genre, 0);
                        }
                    }
                }
            }
        }

        System.out.println("Found " + artistsID_found + " artists");
        System.out.println("Found " + songGeners_found.keySet().size() + " music genres");

        artists = new ProgressBar("Loading Artist", artistsID_found, ProgressBarStyle.ASCII);
        generes = new ProgressBar("Loading Genres", songGeners_found.keySet().size(), ProgressBarStyle.ASCII);
        generes.start();
        generes.stepTo(0);

        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

        //carico i generi musicali
        for (String genre : songGeners_found.keySet()) {
            String query = QueryBuilder.insert_query_creator(PredefinedSQLCode.Tabelle.GENERI_MUSICALI, genre);
            try {this.main.database.submitQuery(query);} catch (SQLException e) {}
            artists.stepTo(0);
            generes.step();
            try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
        }

        generes.stop();
        artists.start();
        artists.stepTo(0);
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
        
        
        for (File file : files) 
        {
            Object[] data_for_Queries = JsonParser.parseArtists(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
            HashMap<String, Object> ArtistsInf = (HashMap<String, Object>) data_for_Queries[0];
            HashMap<String, Object> ArtistsImgs = (HashMap<String, Object>) data_for_Queries[1];
            HashMap<String, Object> ArtistsGenres = (HashMap<String, Object>) data_for_Queries[2];
            
            //itero tutti gli ID che ci sono nel FIle           
            for (String key : ArtistsInf.keySet()) 
            {
                //ottengo l'HashTable che rappresenta l'artista di quell'ID
                HashMap<String, Object> artist = (HashMap<String, Object>)ArtistsInf.get(key);
                Tabelle tabella = PredefinedSQLCode.Tabelle.ARTIST;

                //riordino gli attributi
                Object[] data = new Object[artist.keySet().size()];
                
                for (int i = 0; i < data.length; i++) {    
                    String nomeCol_i = PredefinedSQLCode.tablesAttributes.get(tabella)[i].getName();
                    data[i] = artist.get(nomeCol_i);
                }

                try {
                    artists.step();
                    this.main.database.submitQuery(QueryBuilder.insert_query_creator(tabella, data));

                    for (String genre : (ArrayList<String>) ArtistsGenres.get(key)) {
                        this.main.database.submitQuery(QueryBuilder.insert_query_creator(PredefinedSQLCode.Tabelle.GENERI_ARTISTA, new Object[]{genre, artist.get(PredefinedSQLCode.Colonne.ID.getName())}));
                    } 
                } 
                //Elemento duplicato
                catch (SQLException e) {
                }
                
            }
        } 
        artists.stop();
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
