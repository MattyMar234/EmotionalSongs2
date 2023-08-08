package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import Parser.JsonParser;
import database.Database;
import utility.AsciiArtGenerator;
import utility.PathFormatter;
import utility.AsciiArtGenerator.ASCII_STYLE;


public class App extends JFrame
{
    public final static String WORKING_DIRECTORY  = PathFormatter.formatPath(System.getProperty("user.dir"));
    public final static String SETTINGS_DIRECTORY = PathFormatter.formatPath(WORKING_DIRECTORY + "/data");
    public final static String FILE_SETTINGS_PATH = PathFormatter.formatPath(SETTINGS_DIRECTORY + "/settings.json");

    private static App instance;

    private static enum jsonDataName {

        SERVER_PORT("Port_server"),
        DATABASE_PORT("Port_database"),
        DATABASE_IP("IP_database"),
        DATABASE_PW("Pw_database"),
        DATABASE_USER("User_database"),
        DATABASE_NAME("Name_database");
        private String s;

        private jsonDataName(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return this.s;
        }
    }

    private int port;
    private int DB_port;
    private String DB_IP;
    private String DB_password;
    private String DB_user;
    private String DB_name;
    
    public Database database = null;
    private boolean databaseConnected = false;
    private Server server = null;


    public static App getInstance() {
        return instance;
    }


    public static void main( String[] args ) throws Exception 
    {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); 
        new App(args);
    }

    private App(String[] args) throws InterruptedException, IOException 
    {
        super();

        //singleton pattern
        instance = this; 

        startingLogo();
        loadSettings();


        Terminal terminal = Terminal.getInstance();
        
        try {
            database = Database.getInstance();
            database.setConnection(this.DB_name, this.DB_IP, this.DB_port, this.DB_user, DB_password);
        } 
        catch (Exception e) {
            terminal.printError_ln("DB initialization error: " + e);
    
            e.printStackTrace();
            System.exit(0);
        }
        
        terminal.printInfo_ln("Establishing database connection ");
        testDataBaseConnection();
        

        terminal.printSeparator();
        terminal.start();
    }

    public boolean testDataBaseConnection() {

        Terminal terminal = Terminal.getInstance();
        try {
            if(!database.testconnection()) {
                terminal.printError_ln("DB connection parametre not set");
                return false;
            }
            else {
                terminal.printSucces_ln("DB connection established");
                return true;
            }  
        } 
        catch (SQLException e) {
            terminal.printError_ln("connection attempt: failed");
            terminal.printError_ln("Database not available");
            return false;
        }
    }


    public void exit() {
        StopServer();
        saveSettings();
        System.exit(0);
    }

    //check data 
    private boolean checkSettings() 
    {
        Terminal terminal = Terminal.getInstance();
        
        
        Path folder = Paths.get(App.SETTINGS_DIRECTORY);
        Path file = Paths.get(App.FILE_SETTINGS_PATH);

        try {
            if(!Files.exists(folder)) {
                
                Files.createDirectory(folder);
            } 
            if(!Files.exists(file)) {
                terminal.printError_ln("settings file not found");
                initializeSettings();
            }
           
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void loadSettings() throws IOException {

        Terminal terminal = Terminal.getInstance();
        terminal.printInfo_ln("Loading settings");

        if(!checkSettings())
            return;

        JsonNode node = JsonParser.readJsonFile(FILE_SETTINGS_PATH);

        this.port = node.get(jsonDataName.SERVER_PORT.toString()).asInt();
        this.DB_port = node.get(jsonDataName.DATABASE_PORT.toString()).asInt();
        this.DB_IP = node.get(jsonDataName.DATABASE_IP.toString()).asText();
        this.DB_password = node.get(jsonDataName.DATABASE_PW.toString()).asText();
        this.DB_user = node.get(jsonDataName.DATABASE_USER.toString()).asText();
        this.DB_name = node.get(jsonDataName.DATABASE_NAME.toString()).asText();

        terminal.printSucces_ln("Loading completed");

    }

    private void initializeSettings() throws IOException 
    {
        Terminal terminal = Terminal.getInstance();
        terminal.printInfo_ln("setup default settings");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = objectMapper.createObjectNode();

        ((ObjectNode) data).put(jsonDataName.SERVER_PORT.toString(), 8090);
        ((ObjectNode) data).put(jsonDataName.DATABASE_IP.toString(), "localhost");
        ((ObjectNode) data).put(jsonDataName.DATABASE_PORT.toString(), 5432);
        ((ObjectNode) data).put(jsonDataName.DATABASE_PW.toString(), "admin");
        ((ObjectNode) data).put(jsonDataName.DATABASE_USER.toString(), "postgres");
        ((ObjectNode) data).put(jsonDataName.DATABASE_NAME.toString(), "EmotionalSongs");

        JsonParser.writeJsonFile(FILE_SETTINGS_PATH, data);
        loadSettings();
        saveSettings();
    }

    protected void saveSettings() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = objectMapper.createObjectNode();

        ((ObjectNode) data).put(jsonDataName.SERVER_PORT.toString(), this.port);
        ((ObjectNode) data).put(jsonDataName.DATABASE_IP.toString(), this.DB_IP);
        ((ObjectNode) data).put(jsonDataName.DATABASE_PORT.toString(), this.DB_port);
        ((ObjectNode) data).put(jsonDataName.DATABASE_PW.toString(), this.DB_password);
        ((ObjectNode) data).put(jsonDataName.DATABASE_USER.toString(), this.DB_user);
        ((ObjectNode) data).put(jsonDataName.DATABASE_NAME.toString(), this.DB_name);

        JsonParser.writeJsonFile(FILE_SETTINGS_PATH, data);
    }

    public void runServer() throws RemoteException {

        if(server == null) {
           server = new Server(this.port);
           server.start();
        }
    }

    public void StopServer() {
        
        if(server != null && server.isAlive()) {
            server.terminate();
            
            while(server.isAlive());
            server = null;
        }
        else if(server != null) {
            server = null;
        }
        
    }



    //utility

    private void startingLogo() throws InterruptedException {
        Terminal terminal = Terminal.getInstance();
        terminal.printLogo();
        Thread.sleep(1000);
        terminal.printInfo_ln("Application Running...");
    }
    
}
