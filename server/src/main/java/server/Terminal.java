package server;

import database.PredefinedSQLCode.SQLKeyword;
import database.PredefinedSQLCode.Tabelle;
import utility.WaithingAnimationThread;
import database.PredefinedSQLCode;
import utility.AsciiArtGenerator;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.Hashtable;
import database.DatabaseManager;
import java.awt.Desktop;
import java.util.List;
import java.io.File;




public class Terminal extends Thread
{
    //patter sigleton
    private static Terminal instance = null;
    
    private boolean fechUserInputs;
    private boolean ready;
    private boolean addTime = false;
    private App main;
    private WaithingAnimationThread waithingThread = null;
    
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
        HELP(           "help   ", " Elenco dei comandi"),
        START(          "start  ", "Avvia il Server"),
        CLOSE(          "exit   ", " Termina l'applicazione"),
        BUILD_SERVER(   "init   ", " Inizilizza il database dell'applicazione"),
        CLEAR_DB(       "clear  ", "Cancella tutte le informazioni del database"),
        PRINT_SQL(      "sql    ", "  Mostra i codici SQL statici creati"),
        //SAVE(           "save", " Salva i settaggi della connessione con il DB"),
        LOAD(           "load   ", " Carica i settaggi per connessione con il DB"),
        EDIT(           "edit   ", " Modifica i parametri di connessione con il DB"),
        CONNECT(        "connect", " verifica la connessione col il database"),
        CHECK(          "check  ", " Verifica se le tabelle delle database sono corrette");
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
        System.out.flush();   
    }    

    public static Terminal getInstance() 
    {
        if (Terminal.instance == null) {
            Terminal.instance = new Terminal();
            Terminal.instance.printLogo();

            try {Thread.sleep(1000);} catch (InterruptedException e) {}
            
            Terminal.instance.printInfo_ln("Application Running...");
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
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("type \"help\" to see available commands");

        while(this.fechUserInputs) 
        {
            try {
                printArrow();
                String command = in.readLine();
                System.out.println();
                System.out.flush();
                
                
                if(command.equalsIgnoreCase(Command.HELP.getCommandValue())) {
                    dumpCommands();
                }
                else if(command.equalsIgnoreCase(Command.START.getCommandValue())) { 

                    do {
                        if(main.isDatabaseConnected()) {
                            main.runServer();
                            System.console().readLine();
                            main.StopServer();
                            setAddTime(false);
                            break;
                        }
                        else {
                            printInfo_ln("The database is not connected");
                            main.setDatabaseConnection();
                        }   
                    }
                    while(main.isDatabaseConnected());
                }
                else if(command.equalsIgnoreCase(Command.CLOSE.getCommandValue())) {
                    main.exit();
                }
                else if(command.equalsIgnoreCase(Command.BUILD_SERVER.getCommandValue())) {
                    initializeDatabase();
                }
                else if(command.equalsIgnoreCase(Command.CLEAR_DB.getCommandValue())) {

                    if(main.isDatabaseConnected()) {
                        String ask = "Are you sure you want to delete data from \"" + Color.CYAN_BOLD_BRIGHT + main.database.getDB_NAME() + Color.RESET + "\" database ?";
                        
                        if(askYesNo(ask))
                            clearDatabase();
                        else                        
                            printInfo_ln("operation cancelled");
                        
                    }
                    else {
                        printInfo_ln("The database is not connected");
                        main.setDatabaseConnection();
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
                }
                else if(command.equalsIgnoreCase(Command.EDIT.getCommandValue())) {
                    editSettings();
                }
                else if(command.equalsIgnoreCase(Command.CONNECT.getCommandValue())) {
                    if(main.isDatabaseConnected()) {
                        printInfo_ln("Database is already connected");
                        printInfo_ln("terminate last connection");
                        DatabaseManager.getInstance().close();
                    }
                    
                    main.setDatabaseConnection();
                }
                else if(command.equalsIgnoreCase(Command.CHECK.getCommandValue())) {
                    printInfo_ln("start database integrity test...");
                    
                    if(main.isDatabaseConnected()) {
                        main.DatabaseIntegrityTest();
                    }
                    else {
                        printError_ln("The database is not connected");
                    } 
                
                }
                else if( !(command.equals("\n")||command.equals("\r")||command.equals("\n\r")||command.equals("\r\n"))) {
                   printError_ln("Unknown command \"" + Color.CYAN_BOLD_BRIGHT + command + Color.RESET + "\""); 
                }
                printSeparator();
                System.out.println("type \"help\" to see available commands");
                
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

    public void setAddTime(boolean mode) {
        this.addTime = mode;
    }

    public void editSettings() 
    {
        printInfo_ln("opening file " + App.FILE_SETTINGS_PATH);
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
    


    private void printSQL() {

        for (Hashtable<Tabelle, String> queries : PredefinedSQLCode.elenco_QuerySQL) {
            boolean verificaTipologia = true;
            
            for (Tabelle key : queries.keySet()) {
                String sql = queries.get(key);

                for (SQLKeyword keyword : SQLKeyword.values()) {
                    String keywordString = keyword.getKeyword();

                    if(sql.contains(keyword + "\t") || sql.contains(keyword + " ") || sql.contains(keyword + "(") || sql.contains(keyword + ",") || sql.contains(keyword + ";") || sql.contains(keyword + "\n"))
                        sql = sql.replace(keywordString, Color.MAGENTA_BOLD_BRIGHT + keywordString + Color.RESET);
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
                printInfo_ln("Table " + table + " removed");
            
            } 
            catch (SQLException e) {
                e.printStackTrace();
            }   
        }
        printSucces_ln("all tables removed\n");
        return 0;
    }

    

    
    private int initializeDatabase() throws IOException, SQLException {
        return Loader.getInstance().loadApplicationData();
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

    public int getTerminalColumns() 
    {
        int read = -1;
        String[] signals = new String[] {
            "\u001b[s",            // save cursor position
            "\u001b[5000;5000H",   // move to col 5000 row 5000
            "\u001b[6n",           // request cursor position
            "\u001b[u",            // restore cursor position
        };

        try {
            for (String s : signals)
                System.out.print(s);
            
             
            StringBuilder sb = new StringBuilder();
            byte[] buff = new byte[1];
             
            while ((read = System.in.read(buff, 0, 1)) != -1) 
            {
                sb.append((char) buff[0]);
                if ('R' == buff[0]) {
                    break;
                }
            }

            String size = sb.toString();
            //int rows = Integer.parseInt(size.substring(size.indexOf("\u001b[") + 2, size.indexOf(';')));
            //int cols = Integer.parseInt(size.substring(size.indexOf(';') + 1, size.indexOf('R')));
            //System.err.printf("rows = %s, cols = %s%n", rows, cols);

            int cols = Integer.parseInt(size.split(";")[1].split("R")[0]);
            
        
            return cols;
           

        } catch (Exception e) {
            return 0;
        }

    }

    public synchronized void printSeparator() {

        jline.Terminal t = jline.TerminalFactory.get();
        t.setEchoEnabled(true);
     
        int terminalWidth = t.getWidth();
        

        if(this.waithingThread != null) {
            this.waithingThread.pause();
            while(!this.waithingThread.isInPause());

            for(int i = 0; i < terminalWidth; i++)
                System.out.print("=");
            System.out.println();

            this.waithingThread.restart();
        }
        else {
            for(int i = 0; i < terminalWidth; i++)
                System.out.print("=");
            System.out.println();
        }
    }  
    
    


    public boolean askYesNo(String question) throws IOException {
        System.out.println(question);
        System.out.print("[y/n] > ");

        String result = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if(result.equalsIgnoreCase("y")) {
            return true;
        }
        else {
            return false;
        }
    }


    public synchronized void printArrow () {
        System.out.print("> ");
    }

    public synchronized void printLogo() {
        printSeparator();
        System.out.print(Color.MAGENTA_BOLD_BRIGHT);
        System.out.print(AsciiArtGenerator.generate("EmotionalSongs Server", AsciiArtGenerator.ASCII_STYLE.BIG_SMUSHING));
        System.out.print(Color.RESET);
        printSeparator();
    }

    private synchronized void printOnTerminal(MessageType type, String message, Color MessageColor) {

        if(this.waithingThread != null) {
            this.waithingThread.pause();
            while(!this.waithingThread.isInPause());

            if(this.addTime) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
                LocalDateTime now = LocalDateTime.now();  
                
                StringBuilder builder = new StringBuilder();
                builder.append("(" + dtf.format(now) + ") :");
               
                
                System.out.print(type.toString().replace(":", builder.toString()) + message);
            }
            else {
                System.out.print(type + message);
            }

            

            this.waithingThread.restart();
        }
        else {
            System.out.print(type + message);
        }   
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

    public void printRequest_ln(String message) {
        printOnTerminal(MessageType.REQUEST, " " + message + "\n", null);
    }
    public void printQuery_ln(String message) {
        printOnTerminal(MessageType.QUERY, " " + message + "\n", null);
    }


    public void print(String message) {
        printOnTerminal(MessageType.NONE, message, null);
    }

    public void printInfo(String message) {
        printOnTerminal(MessageType.INFO, " " + message, null);
    }

    public void printSucces(String message) {
        printOnTerminal(MessageType.SUCCES, " " + message, null);
    }

    public void printError(String message) {
        printOnTerminal(MessageType.ERROR, " " + message, null);
    }

    public void printRequest(String message) {
        printOnTerminal(MessageType.REQUEST, " " + message, null);
    }
    public void printQuery(String message) {
        printOnTerminal(MessageType.QUERY, " " + message, null);
    }
}
