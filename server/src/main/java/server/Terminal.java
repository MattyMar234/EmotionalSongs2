package server;

import database.PredefinedSQLCode.Tabelle;
import java.time.format.DateTimeFormatter;
import utility.WaithingAnimationThread;
import database.PredefinedSQLCode;
import database.QueryBuilder;
import utility.AsciiArtGenerator;
import utility.OS_utility;

import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.io.BufferedReader;
import java.sql.SQLException;
import enumclass.SQLKeyword;
import java.util.LinkedList;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import database.DatabaseManager;
import java.awt.Desktop;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.io.File;
import java.io.FileReader;




public class Terminal extends Thread
{
    //patter sigleton
    private static Terminal instance = null;
    private static final Semaphore MUTEX_QUEUE = new Semaphore(1);
    
    private boolean fechUserInputs;
    private boolean ready;
    private boolean addTime = false;
    private App main;

    //Threads
    private TerminalPrinter terminalPrinterThread = null;
    private WaithingAnimationThread waithingThread = null;

    private jline.Terminal jlineTerminal = jline.TerminalFactory.get();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private Queue<String> terminalMessageQueue = new LinkedList<>();



    private class TerminalPrinter extends Thread 
    {
        public TerminalPrinter() {
            super("TerminalPrinter");
            setDaemon(addTime);
            start();
        }

        @Override
        public void run() 
        {
            while(true)
            {
                try {
                    MUTEX_QUEUE.acquire();
                    String[] array = new String[terminalMessageQueue.size()];
                    int index = 0;

                    while(terminalMessageQueue.size() > 0) {
                        array[index++] = terminalMessageQueue.poll();
                    }

                    MUTEX_QUEUE.release();
                    printOnTerminal(array);
                
                    while(true) {
                        MUTEX_QUEUE.acquire();
                        if(terminalMessageQueue.size() > 0) {
                            MUTEX_QUEUE.release();
                            break;
                        }
                        else {
                            MUTEX_QUEUE.release();
                            Thread.sleep(1);
                        }
                    }   
                } 
                catch (InterruptedException e) {
                    e.printStackTrace();
                } 
            }
        }
    }


    public enum Color {
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

    public enum MessageType 
    {
        NONE(""),
        INFO(   "[" + Color.BLUE_BOLD    + "INFO" + Color.RESET + "]     :"),
        ERROR(  "[" + Color.RED_BOLD     + "ERROR" + Color.RESET + "]    :"),
        REQUEST("[" + Color.CYAN_BOLD    + "REQUEST" + Color.RESET + "]  :"),
        QUERY(  "[" + Color.MAGENTA_BOLD + "QUERY" + Color.RESET + "]    :"),
        SUCCES( "[" + Color.GREEN_BOLD   + "SUCCES" + Color.RESET + "]   :");
        
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
        HELP(           "help      ", " Elenco dei comandi"),
        START(          "start     ", " Avvia il Server"),
        CLOSE(          "exit      ", " Termina l'applicazione"),
        //BUILD_SERVER(   "init   ", " Inizilizza il database dell'applicazione"),
        CLEAR_DB(       "clear     ", " Cancella tutte le informazioni del database"),
        PRINT_SQL(      "sql       ", "  Mostra i codici SQL statici creati"),
        //SAVE(           "save", " Salva i settaggi della connessione con il DB"),
        LOAD(           "load      ", " Carica i settaggi per connessione con il DB"),
        EDIT(           "edit      ", " Modifica i parametri di connessione con il DB"),
        CONNECT(        "connect   ", " verifica la connessione col il database"),
        DISCONNECT(     "disconnect", " Per disconnettersi dal datatbase"),
        CHECK(          "check     ", " Verifica se le tabelle delle database sono corrette"),
        //DB_INFO(        "info      ", " Mostra le informazioni sul database"),
        EXPORT(         "export    ", " Esporta il contenuto del dataBase in file CSV"),
        IMPORT(         "import    ", " per importare i dati nel database tramite dei file CSV"),
        QUERY_DB_ON(    "edqp      ", " Abilita la scrittura delle query dinamiche"),
        QUERY_DB_OFF(   "ddqp      ", " Disabilita la scrittura delle query dinamiche");
        //SQL_TERMINAL("makequery", " Apre la console SQL");

        public final String value;
        private final String descrizione;

        Command(String str, String desc) {
            this.value = str;
            this.descrizione = desc;
        }

        public String getCommandValue() {
            return this.value.replace(" ", "");
        }

        @Override
        public String toString() {
            return value.toUpperCase() + " - " + descrizione;
        }
    }

    
    private Terminal() {
        super("Server-Terminal");
        this.main = App.getInstance();
        this.fechUserInputs = true;
        this.ready = false;

        //clear console
        System.out.print("\033\143");  
        System.out.print("\033[H\033[2J");
        //System.out.print("\033[8;86;140t");  
        jlineTerminal = jline.TerminalFactory.get();
        jlineTerminal.setEchoEnabled(true);

        System.out.flush(); 
        this.terminalPrinterThread = new TerminalPrinter();
    }    

    public static Terminal getInstance() 
    {
        if (Terminal.instance == null) {
            Terminal.instance = new Terminal();
            Terminal.instance.printLogo();

            try {Thread.sleep(1000);} catch (InterruptedException e) {}
            
            Terminal.instance.printInfoln("Application Running...");
            Terminal.instance.ready = true;
        }
        return Terminal.instance;
    }

    public boolean isReady() {
        return ready;
    }

   
    @Override
    public void run() 
    {
        App main = App.getInstance();
        String command;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean autoRUN = main.getServerAutoStart();

        try {
            printInfoln("start database integrity test...");
                    
            if(main.isDatabaseConnected()) {
                main.DatabaseIntegrityTest();
            }
            else {
                printErrorln("The database is not connected");
            }   
        } catch (Exception e) {
            Terminal.getInstance().printErrorln(e.toString());

            for (StackTraceElement stack : e.getStackTrace()) {
                Terminal.getInstance().printErrorln(stack.toString());
            }
        }
        finally {
            printSeparator();
        }

        
        

        while(this.fechUserInputs) 
        {
            try {
                if(autoRUN) {
                    autoRUN = false;
                    command = Command.START.getCommandValue();
                }
                else {
                    println("type \"help\" to see available commands");
                    printArrow();
                    command = in.readLine();
                    println("");
                    printLine();
                }
                
                if(command.equalsIgnoreCase(Command.HELP.getCommandValue())) {
                    dumpCommands();
                }
                else if(command.equalsIgnoreCase(Command.START.getCommandValue())) 
                { 
                    //do {
                        if(main.isDatabaseConnected()) {
                            main.runServer();
                            System.console().readLine();
                            main.StopServer();
                            setAddTime(false);
                            //break;
                        }
                        else {
                            printErrorln("The database is not connected");
                            //main.setDatabaseConnection();
                        }   
                    // }
                    // while(main.isDatabaseConnected());
                }
                else if(command.equalsIgnoreCase(Command.CLOSE.getCommandValue())) {
                    main.exit();
                }
                // else if(command.equalsIgnoreCase(Command.BUILD_SERVER.getCommandValue())) {
                //     initializeDatabase();
                // }
                else if(command.equalsIgnoreCase(Command.CLEAR_DB.getCommandValue())) {

                    if(main.isDatabaseConnected()) {
                        String ask = "Are you sure you want to delete data from \"" + Color.CYAN_BOLD_BRIGHT + main.database.getDB_NAME() + Color.RESET + "\" database ?";
                        
                        if(askYesNo(ask))
                            clearDatabase();
                        else                        
                            printInfoln("operation cancelled");
                        
                    }
                    else {
                        printErrorln("The database is not connected");
                        //main.setDatabaseConnection();
                    }        
                }
                else if(command.equalsIgnoreCase(Command.PRINT_SQL.getCommandValue())) {
                    printSQL();
                }
                /*else if(command.equalsIgnoreCase(Command.SAVE.value)) {
                    App.getInstance().saveSettings();
                }*/
                else if(command.equalsIgnoreCase(Command.LOAD.getCommandValue())) {
                    main.loadSettings();
                    if(main.isDatabaseConnected()) {
                        printInfoln("terminate last connection");
                        DatabaseManager.getInstance().closeConnection();
                    }
                    //main.setDatabaseConnection();
                }
                else if(command.equalsIgnoreCase(Command.EDIT.getCommandValue())) {
                    editSettings();
                }
                else if(command.equalsIgnoreCase(Command.CONNECT.getCommandValue())) 
                {
                    if(main.isDatabaseConnected()) {
                        printInfoln("Database is already connected");
                        printInfoln("terminate last connection");
                        DatabaseManager.getInstance().closeConnection();
                    }
                    
                    main.setDatabaseConnection();
                }
                else if(command.equalsIgnoreCase(Command.DISCONNECT.getCommandValue())) 
                {
                    if(main.isDatabaseConnected()) {
                        DatabaseManager.getInstance().closeConnection();
                        printInfoln("connection close");
                    }
                }
                else if(command.equalsIgnoreCase(Command.CHECK.getCommandValue())) {
                    printInfoln("Start database integrity test...");
                    
                    if(main.isDatabaseConnected()) {
                        main.DatabaseIntegrityTest();
                    }
                    else {
                        printErrorln("The database is not connected");
                    } 
                
                }
                // else if(command.equalsIgnoreCase(Command.DB_INFO.getCommandValue())) {
                //     //printDatabaseInfo();
                // }
                else if(command.equalsIgnoreCase(Command.EXPORT.getCommandValue())) {
                    if(main.isDatabaseConnected()) {
                        exportDB();
                    }
                    else {
                        printErrorln("The database is not connected");
                        //main.setDatabaseConnection();
                    }     
                }
                else if(command.equalsIgnoreCase(Command.IMPORT.getCommandValue())) {
                    if(main.isDatabaseConnected()) 
                    {
                        String ask = "Any data already in the database will be deleted. Do you want to proceed ?";
                        
                        if(askYesNo(ask))
                            importDB();
                        else                        
                            printInfoln("operation cancelled");
                    }
                    else {
                        printInfoln("The database is not connected");
                        //main.setDatabaseConnection();
                    }
                }
                else if(command.equalsIgnoreCase(Command.QUERY_DB_ON.getCommandValue())) {
                    QueryBuilder.setQueryDebug(true);
                }
                else if(command.equalsIgnoreCase(Command.QUERY_DB_OFF.getCommandValue())) {
                    QueryBuilder.setQueryDebug(false);
                }
                else if( !(command.equals("\n")||command.equals("\r")||command.equals("\n\r")||command.equals("\r\n"))) {
                   printErrorln("Unknown command \"" + Color.CYAN_BOLD_BRIGHT + command + Color.RESET + "\""); 
                }

                Thread.interrupted();
                printSeparator();
           
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println(e);
            }
            catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }
        }
    }

    public void importDB() throws IOException 
    {
        File database__data_folder;
        boolean clear = true;
        boolean success = true;
        
        //==================================== SELEZIONE DEI FILE ====================================//
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        printInfoln("Start database initalizzation...\n");
        getInstance().println("Dataset Folder Path:");
        getInstance().printArrow();
        database__data_folder = new File(OS_utility.formatPath(in.readLine()));


        if (!database__data_folder.isDirectory()) {
            printErrorln(Terminal.Color.RED_BOLD_BRIGHT + "INVALID PATH" + Terminal.Color.RESET);
            printInfoln("Database configuration ended\n");
            return;
        }

        HashMap<String, Integer> filesName = new HashMap<>();
        HashMap<String, File> fileToLoad = new HashMap<>();

        for (PredefinedSQLCode.Tabelle table : PredefinedSQLCode.Tabelle.values()) {
            String tableName = table.toString();
            filesName.put(tableName.toLowerCase(), 0);
         }

        for(File f : database__data_folder.listFiles()) {
            if(!f.isDirectory()) {
                System.out.println(f.getName());
                String name = f.getName().split(".csv")[0];
                System.out.println("name");
                filesName.remove(name.toLowerCase());
                printSuccesln("file: " + f.getName().toLowerCase() + " found");

                fileToLoad.put(name.toLowerCase(), f);
            }
        }

        if(filesName.size() > 0) {
            for (String name : filesName.keySet()) {
                printErrorln("file: " + name + " missing");
            } 
            return;
        }

        printSuccesln("All files found");
        
        try {
            for (Tabelle table : PredefinedSQLCode.Tabelle.values()) 
            {
                if(clear)// || table== Tabelle.SONG)
                    this.main.database.submitQuery(PredefinedSQLCode.deleteTable_Queries.get(table));

                printInfoln("Creating table: " + table);
                this.main.database.submitQuery(PredefinedSQLCode.createTable_Queries.get(table)); 
            }
        } catch (SQLException e) {
            printErrorln(e.toString());
            e.printStackTrace();
            System.exit(0);
        }

        printLine();

        for (PredefinedSQLCode.Tabelle table : PredefinedSQLCode.Tabelle.values()) 
        {
            String tableName = table.toString();
            
            File file = fileToLoad.get(tableName.toLowerCase());
            String header = null;

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                
                header = br.readLine();

                if(header == null) {
                    printErrorln("File " + file.getName() + " is empty");
                    return;
                }
            } 
            catch (IOException e) {
                printErrorln(Terminal.Color.RED_BOLD_BRIGHT + e.toString() + Terminal.Color.RESET);
                return;
            }

            String query = QueryBuilder.importQuery(tableName, OS_utility.formatPath(file.getAbsolutePath()), header);
            try {
                printInfoln("Importing " + tableName);
                DatabaseManager.getInstance().submitQuery(query);
                printSuccesln(tableName + " Imported");
            } catch (SQLException e) {
                for (String s : e.toString().split("\n")) {
                    printErrorln(Color.RED_BOLD_BRIGHT + s + Color.RESET);
                } 
                success = false;
            }
            finally {
                printLine();
            }
        }

        if(success) {
            printSuccesln(Color.GREEN_BOLD_BRIGHT + "Database successfully imported".toUpperCase() + Color.RESET);
        }
        else {
            printErrorln(Color.RED_BOLD_BRIGHT + "Try changing the folder access premisses to 'full access'."  + Color.RESET);
            printErrorln(Color.RED_BOLD_BRIGHT + "Add \"Everyone\" user"  + Color.RESET);
        }
    }

    public void exportDB() throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean success = true;

        printInfoln("Start database initalizzation...\n");
        println("Output Folder Path:");
        printArrow();
        File output = new File(OS_utility.formatPath(in.readLine()));
        println("");

        if (!output.exists() || !output.isDirectory()) {
            printErrorln(Terminal.Color.RED_BOLD_BRIGHT + "INVALID PATH: " + output.getAbsolutePath()  + Terminal.Color.RESET);
            printErrorln("Operation cancelled\n");
            return;
        }
        
        for (PredefinedSQLCode.Tabelle table : PredefinedSQLCode.Tabelle.values()) 
        {
            String tableName = table.toString();
            String path = OS_utility.formatPath(output.getAbsolutePath() + "\\" + tableName + ".csv");
            String query = QueryBuilder.exportQuery(tableName, path);
            

            try {
                printInfoln("Exporting " + tableName);
                DatabaseManager.getInstance().submitQuery(query);
                printSuccesln(tableName + " exported");
            } catch (SQLException e) {
                for (String s : e.toString().split("\n")) {
                    printErrorln(Color.RED_BOLD_BRIGHT + s + Color.RESET);
                } 
                success = false;
            }
            finally {
                printLine();
            }  
        }
        if(success) {
            printSuccesln(Color.GREEN_BOLD_BRIGHT + "Database successfully exported".toUpperCase() + Color.RESET);
        }
        else {
            printErrorln(Color.RED_BOLD_BRIGHT + "Try changing the folder access premisses to 'full access'."  + Color.RESET);
            printErrorln(Color.RED_BOLD_BRIGHT + "Add \"Everyone\" user"  + Color.RESET);
        }
    }

    public void setAddTime(boolean mode) {
        this.addTime = mode;
    }

    public void editSettings() 
    {
        printInfoln("opening file " + App.FILE_SETTINGS_PATH);
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(new File(App.FILE_SETTINGS_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    public synchronized void startWaithing(String text, Object... args) 
    {
        jline.Terminal t = jline.TerminalFactory.get();
        t.setEchoEnabled(false);

        if(this.waithingThread == null) {
            if(args.length == 1) {
                this.waithingThread = new WaithingAnimationThread(text, (WaithingAnimationThread.Animation)args[0]);
            }
            else {
               this.waithingThread = new WaithingAnimationThread(text); 
            }
            this.waithingThread.start();
        }
    }

    public synchronized void stopWaithing() 
    {
        jline.Terminal t = jline.TerminalFactory.get();
        t.setEchoEnabled(true);

        if(this.waithingThread != null) {
            this.waithingThread.terminate();
            while(this.waithingThread.isAlive());
            this.waithingThread = null;
        }
    }
    


    private void printSQL() 
    {
        for (Hashtable<Tabelle, String> queries : PredefinedSQLCode.elenco_QuerySQL) {
            boolean verificaTipologia = true;
            
            for (Tabelle key : queries.keySet()) {
                String sql = queries.get(key);

                for (SQLKeyword keyword : SQLKeyword.values()) {
                    String keywordString = keyword.getKeyword();

                    if(sql.contains(keyword + "\t") || sql.contains(keyword + " ") || sql.contains(keyword + "(") || sql.contains(keyword + ",") || sql.contains(keyword + ";") || sql.contains(keyword + "\n"))
                        sql = sql.replaceAll(keywordString, Color.MAGENTA_BOLD_BRIGHT + keywordString + Color.RESET);
                }

                if(verificaTipologia) {
                    verificaTipologia = false;

                    for(int i = 0; i < 64; i++) System.out.print("-");
                    if (sql.toUpperCase().contains("CREATE") && sql.toUpperCase().contains("TABLE")) {
                        System.out.print(" [TABLE CREATION] ");
                    }
                    else if (sql.toUpperCase().contains("DROP") && sql.toUpperCase().contains("TABLE")) {
                        System.out.print(" [TABLE DROPPING] ");
                    }
                    else if (sql.toUpperCase().contains("INSERT")) {
                        System.out.print(" [TABLE INSERTION] ");
                    }
                    for(int i = 0; i < 64; i++) System.out.print("-");
                    System.out.println("\n");
                }
                System.out.println(sql);
            }
        }
    }


    private int clearDatabase() throws IOException 
    {
        for (Tabelle table : PredefinedSQLCode.Tabelle.values()) {
            try {
                this.main.database.submitQuery(PredefinedSQLCode.deleteTable_Queries.get(table));
                printInfoln("Table " + table + " removed");
            
            } 
            catch (SQLException e) {
                e.printStackTrace();
            }   
        }
        printSuccesln("all tables removed\n");
        return 0;
    }

    

    
    private int initializeDatabase() throws IOException, SQLException {
        return Loader.getInstance().loadApplicationData();
    }

    

    private void dumpCommands() {
        println("");
        for (Command commad : Command.values()) {
            println(commad.toString());
        } 
        println("");
    }

    public int getTerminalColumns() 
    {
        // int read = -1;
        // String[] signals = new String[] {
        //     "\u001b[s",            // save cursor position
        //     "\u001b[5000;5000H",   // move to col 5000 row 5000
        //     "\u001b[6n",           // request cursor position
        //     "\u001b[u",            // restore cursor position
        // };

        // try {
        //     for (String s : signals)
        //         System.out.print(s);
            
             
        //     StringBuilder sb = new StringBuilder();
        //     byte[] buff = new byte[1];
             
        //     while ((read = System.in.read(buff, 0, 1)) != -1) 
        //     {
        //         sb.append((char) buff[0]);
        //         if ('R' == buff[0]) {
        //             break;
        //         }
        //     }

        //     String size = sb.toString();
        //     //int rows = Integer.parseInt(size.substring(size.indexOf("\u001b[") + 2, size.indexOf(';')));
        //     //int cols = Integer.parseInt(size.substring(size.indexOf(';') + 1, size.indexOf('R')));
        //     //System.err.printf("rows = %s, cols = %s%n", rows, cols);

        //     int cols = Integer.parseInt(size.split(";")[1].split("R")[0]);
            
        
        //     return cols;
           

        // } catch (Exception e) {
        //     return 0;
        // }

        return this.jlineTerminal.getWidth() - 1;
    }

    public void printSeparator() 
    {
        int terminalWidth = this.jlineTerminal.getWidth() - 1;
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < terminalWidth; i++)
            sb.append("=");

        println(sb.toString());
    }  

    public void printLine() {
        int terminalWidth = this.jlineTerminal.getWidth();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < terminalWidth; i++)
            sb.append("-");

        print(sb.toString() + "\n");
    }
    
    


    public boolean askYesNo(String question) throws IOException {
        println(question);
        print("[y/n] > ");

        String result = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if(result.equalsIgnoreCase("y")) {
            println("");
            return true;
        }
        else {
            println("");
            return false;
        }

        
    }


    public void printArrow () {
        print("> ");
    }

    /*public synchronized void printLogo() {
        printSeparator();
        System.out.print(Color.MAGENTA_BOLD_BRIGHT);
        System.out.print(AsciiArtGenerator.generate("EmotionalSongs Server", AsciiArtGenerator.ASCII_STYLE.BIG_SMUSHING));
        System.out.print(Color.RESET);
        printSeparator();
    }*/

    public void printLogo() {
        printSeparator();

        println(Color.MAGENTA_BOLD_BRIGHT.toString());
        String s1 = AsciiArtGenerator.generate("EmotionalSongs Server ", AsciiArtGenerator.ASCII_STYLE.BIG_SMUSHING);
        String s2[] = s1.split("\n");
        
        for (String string : s2) {
           println(string); 
        }

        println(Color.RESET.toString());

        printSeparator();
    }

    private synchronized void printOnTerminal(String... strArr) 
    {
        if(this.waithingThread != null) {
            this.waithingThread.pause();

            while(!this.waithingThread.isInPause());

            for (String str : strArr) {
                System.out.print(str);
            }
        
            //this.waithingThread.print();
            this.waithingThread.restart();
        }
        else {
            for (String str : strArr) {
                System.out.print(str);
            }
        } 
    }

    private void addStringToPrint(MessageType type, String message, Color MessageColor) 
    {

        if(type == null)
            type = MessageType.NONE;

        if(message == null)
            message = "";

        LocalDateTime now = LocalDateTime.now();
        int terminalWidth = this.jlineTerminal.getWidth();
        String processedString = "";

    
        if(this.addTime) 
            processedString = type.toString().replace(":", "(" + dtf.format(now) + ") :") + message;
        else 
            processedString = type.toString() + message; 
        

        
        /*if (type != MessageType.NONE && processedString.length() > terminalWidth) {
            int spaceIndex = processedString.lastIndexOf(" ", terminalWidth);

            if (spaceIndex != -1) {

                int endFistPart = spaceIndex;

                String firstPart  = processedString.substring(0, endFistPart);
                String secondPart = processedString.substring(endFistPart);

                processedString =  firstPart + "\n";

                if(this.addTime) {
                    processedString += processedString = type.toString().replace(":", "(" + dtf.format(now) + ") :");
                    processedString += secondPart + '\n';
                }
            }
        }*/
                
        //QUERY KEY:
        if(type == MessageType.QUERY) {
            for (SQLKeyword keyword : SQLKeyword.values()) {
                String keywordString = keyword.getKeyword();

                if(processedString.contains(keyword + "\t") || processedString.contains(keyword + " ") || processedString.contains(keyword + "(") || processedString.contains(keyword + ",") || processedString.contains(keyword + ";") || processedString.contains(keyword + "\n"))
                    processedString = processedString.replaceAll(keywordString, Color.MAGENTA_BOLD_BRIGHT + keywordString + Color.RESET);
            }
        }

        try {
            MUTEX_QUEUE.acquire();
            terminalMessageQueue.add(processedString);
            MUTEX_QUEUE.release();
        } 
        catch (InterruptedException e) {
            //e.printStackTrace();
        } 
    }

    public void println(String message) {
        addStringToPrint(MessageType.NONE, message + "\n", null);
    }

    public void printInfoln(String message) {
        addStringToPrint(MessageType.INFO, " " + message + "\n", null);
    }

    public void printSuccesln(String message) {
        addStringToPrint(MessageType.SUCCES, " " + message + "\n", null);
    }

    public void printErrorln(String message) {
        addStringToPrint(MessageType.ERROR, " " + message + "\n", null);
    }

    public void printRequestln(String message) {
        addStringToPrint(MessageType.REQUEST, " " + message + "\n", null);
    }
    public void printQueryln(String message) {
        addStringToPrint(MessageType.QUERY, " " + message + "\n", null);
    }


    public void print(String message) {
        addStringToPrint(MessageType.NONE, message, null);
    }

    public void printInfo(String message) {
        addStringToPrint(MessageType.INFO, " " + message, null);
    }

    public void printSucces(String message) {
        addStringToPrint(MessageType.SUCCES, " " + message, null);
    }

    public void printError(String message) {
        addStringToPrint(MessageType.ERROR, " " + message, null);
    }

    public void printRequest(String message) {
        addStringToPrint(MessageType.REQUEST, " " + message, null);
    }
    public void printQuery(String message) {
        addStringToPrint(MessageType.QUERY, " " + message, null);
    }
}