package server;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

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

    private enum MessageType{

        NONE(""),
        INFO("[" + Color.BLUE + "INFO" + Color.RESET + "]"),
        ERROR("[" + Color.RED_BOLD + "ERROR" + Color.RESET + "]"),
        REQUEST("[" + Color.YELLOW + "REQUEST" + Color.RESET + "]"),
        SUCCES("[" + Color.GREEN + "SUCCES" + Color.RESET + "]");
        
        private final String message;

        MessageType(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    private enum Command {

        HELP("help", "Elenco dei comandi"),
        START("start", "Avvia il Server"),
        CLOSE("exit", "Termina l'applicazione"),
        BUILD_SERVER("init_database", "inizilizza il database dell'applicazione. parametri: p=path");

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


    private static final int LINE_ELEMENT = 100;
    private static final char LINE_CHAR = '-';
    
    private boolean running;
    private App main;
    
    public Terminal(App main) 
    {
        this.main = main;
        this.running = true;
    }    

    @Override
    public void run() {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("type \"help\" to see available commands");
        
        while(this.running) 
        {
            try {
                printArrow();
           
                String command = in.readLine().toLowerCase();

            
                if(command.equals(Command.HELP.value)) {
                    dumpCommands();
                }
                else if(command.equals(Command.START.value)) {
                    printInfo_ln("server starting...");
                    main.runServer();
                    in.readLine();
                    main.StopServer();
                }
                else if(command.equals(Command.CLOSE.value)) {
                    System.exit(1);
                }
                else if(command.equals(Command.BUILD_SERVER.value)) {
                    initializeDatabase(in);
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

    /*private String[] getCommadData(String command) {

        ArrayList<String> data = new ArrayList<String>();
        String temp = "";
        char lastChar = '\0';

        for(int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);

            
        }


        return (String[]) data.toArray();

    }*/

    private int initializeDatabase(BufferedReader in) throws IOException {

        printInfo_ln("start database configuration...");
        printInfo_ln("files folder: ");
        String path = in.readLine();

         
        if (path.length() <= 10) {
            printError_ln("invalid parameters");
            return -1;
        }

        String Artist = path + "\\Artist";

        File Folder = new File(path);
        File ArtistFolder = new File(path);

        if (!Folder.isDirectory()) {
            printError_ln("files not found");
            return -1;
        }
        
        if(!ArtistFolder.exists()) {
            printError_ln("folder \"Artist\" not found");
            return -1;
        }


        printInfo_ln("folder \"Artist\" found");
        
        return 0;
    
    }

    private void dumpCommands() {
        System.out.println();
        for (Command commad : Command.values()) {
            System.out.println(commad);
        } 
        System.out.println();
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
