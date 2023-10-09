package server;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import Parser.JsonParser;
import database.DatabaseManager;
import database.PredefinedSQLCode;
import database.QueriesManager;
import database.QueryBuilder;
import database.PredefinedSQLCode.Colonne;
import database.PredefinedSQLCode.Tabelle;
import utility.AsciiArtGenerator;
import utility.AsciiArtGenerator.ASCII_STYLE;
import utility.OS_utility;


public class App extends JFrame
{
    public final static String WORKING_DIRECTORY  = OS_utility.formatPath(System.getProperty("user.dir"));
    public final static String SETTINGS_DIRECTORY = OS_utility.formatPath(WORKING_DIRECTORY + "/data");
    public final static String FILE_SETTINGS_PATH = OS_utility.formatPath(SETTINGS_DIRECTORY + "/settings.json");
    public final static String FILE_POLICY_PATH = OS_utility.formatPath(SETTINGS_DIRECTORY + "/security.policy");

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
    private boolean autoRun = false;
    
    public DatabaseManager database = null;
    private boolean databaseConnected = false;
    private ComunicationManager server = null;
    private Terminal terminal;


    public static App getInstance() {
        return instance;
    }


    public static void main( String[] args ) throws Exception 
    {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); 

        System.getProperties().setProperty("java.security.policy", FILE_POLICY_PATH);

        

        new App(args);
    }

    private App(String[] args) throws InterruptedException, IOException, ClassNotFoundException, SQLException 
    {
        super();
        App.instance = this;
        this.terminal = Terminal.getInstance();
        Class.forName("org.postgresql.Driver");

        loadSettings();
        setDatabaseConnection();

        
        /*for (Tabelle s : PredefinedSQLCode.Tabelle.values()) {
            terminal.printInfo_ln(s.toString());
        }*/

        
        terminal.printSeparator();
        this.terminal.start();
    }

    /**
     * function that performs operations to close the programme
    */
    public void exit() {
        StopServer();
        saveSettings();
        System.exit(0);
    }

    public boolean getServerAutoStart() {
        return autoRun;
    }

    /**
     * this function sets up the connection with the database
     */
    protected void setDatabaseConnection() 
    {
        database = DatabaseManager.getInstance();
        database.setConnectionParametre(this.DB_name, this.DB_IP, this.DB_port, this.DB_user, DB_password);

        
        terminal.printInfoln("Database connection attempt on URL: " + Terminal.Color.CYAN_BOLD_BRIGHT + database.getURL() + Terminal.Color.RESET);

        try {
            database.connect();
            if(database.testConnection() && database.connect()) {
                terminal.printSuccesln("Database found and connection established");
                databaseConnected = true;
            }
            else {
                terminal.printErrorln(Terminal.Color.RED_BOLD_BRIGHT + "Database not responding" + Terminal.Color.RESET);
                databaseConnected = false;
            } 
        } 
        catch (Exception e) {
            terminal.printErrorln("Connection failed. Error: " + Terminal.Color.RED_BOLD_BRIGHT + e.getMessage() + Terminal.Color.RESET);   
            databaseConnected = false;
        }
    }

    /**
     * this function returns the connection status 
     * @return
     */
    public boolean isDatabaseConnected() {
        return databaseConnected;
    }

    /**
     * this function verifies the existence of the setting files
     * @return outcome of control
     */
    private boolean checkSettings() 
    {
        Terminal terminal = Terminal.getInstance();
        
        
        Path folder = Paths.get(App.SETTINGS_DIRECTORY);
        Path file = Paths.get(App.FILE_SETTINGS_PATH);

        try {
            if(!Files.exists(folder)) {
                Files.createDirectory(folder);
            } 
            if(!Files.exists(file) || Files.size(file) <= 12) {
                
                terminal.printErrorln("settings file not found");
                initializeSettings();
            }
           
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * this function is called up when you want to load data from setting files into the programme
     * @throws IOException
     */
    protected void loadSettings() throws IOException {

        Terminal terminal = Terminal.getInstance();
        terminal.printInfoln("Loading settings");

        if(!checkSettings())
            return;

        JsonNode node = JsonParser.readJsonFile(FILE_SETTINGS_PATH);

        this.port = node.get(jsonDataName.SERVER_PORT.toString()).asInt();
        this.DB_port = node.get(jsonDataName.DATABASE_PORT.toString()).asInt();
        this.DB_IP = node.get(jsonDataName.DATABASE_IP.toString()).asText();
        this.DB_password = node.get(jsonDataName.DATABASE_PW.toString()).asText();
        this.DB_user = node.get(jsonDataName.DATABASE_USER.toString()).asText();
        this.DB_name = node.get(jsonDataName.DATABASE_NAME.toString()).asText();
        this.autoRun = node.get("ServerAutoRUN").asBoolean();

        terminal.printSuccesln("Loading completed");

    }

    /**
     * this function initialises the setting files with the default parameters
     * @throws IOException
     */
    private void initializeSettings() throws IOException 
    {
        Terminal terminal = Terminal.getInstance();
        terminal.printInfoln("setup default settings");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = objectMapper.createObjectNode();

        ((ObjectNode) data).put(jsonDataName.SERVER_PORT.toString(), 8090);
        ((ObjectNode) data).put(jsonDataName.DATABASE_IP.toString(), "localhost");
        ((ObjectNode) data).put(jsonDataName.DATABASE_PORT.toString(), 5432);
        ((ObjectNode) data).put(jsonDataName.DATABASE_PW.toString(), "admin");
        ((ObjectNode) data).put(jsonDataName.DATABASE_USER.toString(), "postgres");
        ((ObjectNode) data).put(jsonDataName.DATABASE_NAME.toString(), "EmotionalSongs");
        ((ObjectNode) data).put("ServerAutoRUN", false);

        JsonParser.writeJsonFile(FILE_SETTINGS_PATH, data);
        loadSettings();
        saveSettings();
    }

    /**
     * this function saves the programme parameters in the setting files
     */
    protected void saveSettings() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = objectMapper.createObjectNode();

        ((ObjectNode) data).put(jsonDataName.SERVER_PORT.toString(), this.port);
        ((ObjectNode) data).put(jsonDataName.DATABASE_IP.toString(), this.DB_IP);
        ((ObjectNode) data).put(jsonDataName.DATABASE_PORT.toString(), this.DB_port);
        ((ObjectNode) data).put(jsonDataName.DATABASE_PW.toString(), this.DB_password);
        ((ObjectNode) data).put(jsonDataName.DATABASE_USER.toString(), this.DB_user);
        ((ObjectNode) data).put(jsonDataName.DATABASE_NAME.toString(), this.DB_name);
        ((ObjectNode) data).put("ServerAutoRUN", false);

        JsonParser.writeJsonFile(FILE_SETTINGS_PATH, data);
    }

    /**
     * this function starts the server
     * @throws RemoteException
     */
    public void runServer() throws RemoteException {

        if(server == null) {
           server = new ComunicationManager(this.port);
           server.start();
        }
    }

    /**
     * this function terminates server
     */
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


    public void DatabaseIntegrityTest() throws IOException, SQLException 
    {
        DatabaseManager db = DatabaseManager.getInstance();
        HashMap<String, Boolean> existingColumns = new HashMap<>();
        HashMap<Tabelle, ArrayList<Colonne>> missingColumns = new HashMap<>();
        ResultSet resultSet = null;


        int maxNameLenght = 0;
        for (Tabelle table : PredefinedSQLCode.Tabelle.values())
            for (Colonne coll : PredefinedSQLCode.tablesAttributes.get(table)) 
                if(coll.getName().length() > maxNameLenght) maxNameLenght = coll.getName().length();

        
        for (Tabelle table : PredefinedSQLCode.Tabelle.values()) 
        {
            terminal.printInfoln("---------------------------------------------------------------------------------------------------");
            terminal.printInfoln("Testing table \"" + Terminal.Color.CYAN_BOLD_BRIGHT + table + Terminal.Color.RESET + "\":");
            Colonne[] colls = PredefinedSQLCode.tablesAttributes.get(table);
            
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            try {
                resultSet = db.getConnection().getMetaData().getColumns(null, null, table.toString().toLowerCase(), null);
                
                //ottengo le colonne che ho nel database
                existingColumns.clear();

                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    existingColumns.put(columnName.toLowerCase(), true);
                }


                //verifico che sono le stesse
                for (Colonne coll : colls) 
                {
                    terminal.printInfo("check for column \"" + Terminal.Color.CYAN_BOLD_BRIGHT + coll.getName() + Terminal.Color.RESET + "\"");
                    
                    for(int i = coll.getName().length(); i < maxNameLenght; i++) terminal.print(" ");
                        terminal.print(" ");
                    terminal.print(": ");
                   
                   
                    if(existingColumns.get(coll.getName().toLowerCase()) == null) {
                        terminal.println(Terminal.Color.RED_BOLD_BRIGHT + "Column \"" + coll.getName() + "\" Not Found"  + Terminal.Color.RESET);
                        //terminal.print_ln(Terminal.Color.RED_BOLD_BRIGHT + "not found"  + Terminal.Color.RESET);
                        if(missingColumns.get(table) == null)
                            missingColumns.put(table,  new ArrayList<Colonne>());
                        
                        missingColumns.get(table).add(coll);
                    }
                    else {
                        terminal.println(Terminal.Color.GREEN_BOLD_BRIGHT + "Column \"" + coll.getName() + "\" Found"  + Terminal.Color.RESET);
                        //terminal.print_ln(Terminal.Color.GREEN_BOLD_BRIGHT + "found" + Terminal.Color.RESET);

                        
                    } 
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
            finally {
                try {
                    if (resultSet != null) resultSet.close();
                    //if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        if(missingColumns.size() > 0 && terminal.askYesNo("\nadd missing colums ?")) 
        {
            Loader loader = Loader.getInstance();

            for(Tabelle tabella : missingColumns.keySet()) {
                for(Colonne colonna : missingColumns.get(tabella)) {
                    loader.addColum(tabella, colonna);
                } 
            }
        }
    }
    
}
