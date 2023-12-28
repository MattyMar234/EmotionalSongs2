package controllers;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.kordamp.ikonli.javafx.FontIcon;

import Exceptions.InvalidEmailException;
import Exceptions.InvalidPasswordException;
import Exceptions.InvalidUserNameException;
import application.ConnectionManager;
import application.FileManager;
import application.Main;
import application.SceneManager;
import application.SceneManager.ApplicationState;
import application.SceneManager.ApplicationScene;
import applicationEvents.ConnectionEvent;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import interfaces.Injectable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import objects.Account;
import utility.UtilityOS;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Questa classe gestisce l'accesso all'applicazione
 */
public class ApplicationAccessController extends ControllerBase implements Initializable, Injectable 
{
    private static ApplicationAccessController reference;
    private ObservableList<ImageView> imgs = FXCollections.observableArrayList();
    private SceneManager sceneManager = SceneManager.instance();
    private volatile boolean connectionParamsEvent = false;
    private boolean labelColor_state = false;
    
    @FXML public Label LabeErrorlField1;
    @FXML public Label LabeErrorlField2;
    @FXML public Label LabelField1;
    @FXML public Label LabelField2;
    @FXML public Label NewAccount;
    @FXML public Label connectionStatus;
    @FXML public Label pingLabel;
    
    @FXML public FontIcon LabelError_IMG1;
    @FXML public FontIcon LabelError_IMG2;
    
    @FXML public TextField IP;
    @FXML public TextField PORT;
    @FXML public TextField userName;
    @FXML public PasswordField password;
    
    
    
    @FXML public Button connectButton;
    @FXML public Button LoginButton;
    @FXML public Button NoAccountButton;

    @FXML public AnchorPane pane1;
    @FXML public AnchorPane mainStart;

    @FXML public CheckBox rememberCheckBox;
    @FXML public ComboBox<ImageView> flags;
 
    @FXML public FontIcon connectionIcon;



    

    public ApplicationAccessController() {
        super();
        reference = this;
    }

    public static ApplicationAccessController getActiveInstance() {
        return reference;
    }

    private void setLanguageText_and_color() {

        if(connectionManager.isConnected())  {
            connectionStatus.setText(Main.applicationLanguage == 0 ? "Server trovato" : "Server found");
            connectButton.setText(Main.applicationLanguage == 0 ? "Disconnettiti" : "Disconnect");
            connectionStatus.setStyle("-fx-text-fill: #1ED760;");
            connectionIcon.setStyle(connectionIcon.getStyle() + "-fx-fill: #1ED760;");
            userName.setText("");
        }
        else {
            connectionStatus.setText(Main.applicationLanguage == 0 ? "Server non trovato" : "Server not found");
            connectButton.setText(Main.applicationLanguage == 0 ? "Connettiti" : "Connect");
            connectionStatus.setStyle("-fx-text-fill: #F14934;");
            connectionIcon.setStyle(connectionIcon.getStyle() + "-fx-fill: #F14934;");
        }

        //LabeErrorlField1.setText(Main.applicationLanguage == 0 ? "L'email oppure l'userID" : "Email or userID");
        //LabeErrorlField2.setText(Main.applicationLanguage == 0 ? "L'email oppure l'userID" : "Email or userID");
        NoAccountButton.setText(Main.applicationLanguage == 0 ? "Continua senza Account" : "Continue without account");
        LoginButton.setText(Main.applicationLanguage == 0 ? "Accedi all'Account": "Login");
        NewAccount.setText(Main.applicationLanguage == 0 ? "Crea un Account" : "Create Account");
        userName.setPromptText(Main.applicationLanguage == 0 ? "Indirizzo e-mail" : "E-mail address");
        //LabelField1.setText(Main.applicationLanguage == 0 ? "Nome utente o indirizzo e-mail" : "User name or e-mail address");
        //LabelField2.setText(Main.applicationLanguage == 0 ? "Password" : "Password");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        setLanguageText_and_color();
        pingLabel.setText("");
        connectionManager.setPinLabel(pingLabel);

        Stage stage = sceneManager.getWindowStage(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW);
        stage.addEventFilter(ConnectionEvent.DISCONNECTED, this::handleConnectionLostEvent);
    

        PORT.setText(Integer.toString(connectionManager.getPort()));
        IP.setText(connectionManager.getAddress());

        
        //carico tutte le immagini delle lingue
        FileManager fManager = FileManager.getInstance();

        try {
            Image[] imglist = {
                new Image(fManager.loadFile("1_Italy.png", FileManager.FileType.FLAG).toURI().toURL().toString()),
                new Image(fManager.loadFile("2_GreatBritain.png", FileManager.FileType.FLAG).toURI().toURL().toString())
            };

            for (Image image : imglist) {
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                imgs.add(imageView);
                flags.getItems().add(imageView);
            }
        } 
        catch (Exception e) {
          
            e.printStackTrace();
        }


        
        // File folder = new File(Main.flagsFolder);
        // File[] listOfFiles = folder.listFiles();

        // Queue<File> queue = new LinkedList<File>();
        // for(File f : listOfFiles) queue.add(f);

        // int index = 1;
        // while(queue.size() > 0) {
        //     File file = queue.poll();
        //     //se l'immagine corrisponde a quella che cerco
        //     if(file.isFile()) {   
        //         if(Integer.parseInt(file.getName().split("_")[0]) == index) {
        //             //try {

        //                 ImageView image = new ImageView(loadImage(file.getAbsolutePath()));

        //                 image.setFitHeight(30);
        //                 image.setFitWidth(30);
        //                 imgs.add(image);
        //                 flags.getItems().add(image);
                        
        //                 index++;
                    
        //         }
        //         else {
        //             queue.add(file);
        //         }
        //     }
        // }

        //definisco come caricare le immagini nella combox
        flags.setCellFactory(new Callback<ListView<ImageView>, ListCell<ImageView>>() {
            @Override public ListCell<ImageView> call(ListView<ImageView> p) {
                return new ListCell<ImageView>() {
                    
                    @Override protected void updateItem(ImageView item, boolean empty) {
                        super.updateItem(item, empty);
        
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            ImageView v = new ImageView(item.getImage());
                            v.setFitWidth(36);
                            v.setFitHeight(36);

                            setGraphic(v);
                        }
                    }
                };
            }
        });
        flags.getSelectionModel().select(Main.applicationLanguage);
        clearError();
    }
          
    
    public class StatusListCell extends ListCell<ImageView> {
        protected void updateItem(ImageView item, boolean empty) {
            
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);

            if(item != null){
                ImageView imageView = item;
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                setGraphic(imageView);
                //setText();
            }
        }
    }

    class IconTextCellClass extends ListCell<ImageView> {

        @Override
        protected void updateItem(ImageView item, boolean empty) {
            super.updateItem(item, empty);
            
            if (item != null) {
                setGraphic(item);
            }
        }
    }



    public void handleConnectionLostEvent(ConnectionEvent event) {
        
    }

    
    @FXML
    public void handleLoginButtonAction() {
        // Gestisci l'azione del pulsante di accesso
    }

    

    @FXML
    public void changeLanguage(ActionEvent event) 
    {
        Main.applicationLanguage = flags.getSelectionModel().getSelectedIndex();
        flags.getSelectionModel().select(Main.applicationLanguage);
        setLanguageText_and_color();
    }


    @FXML
    public void NoAccount(ActionEvent event) throws IOException {
        if (!connectionManager.isConnected()) {
            Stage stage = sceneManager.getWindowStage(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW);
            stage.fireEvent(new ConnectionEvent(ConnectionEvent.SERVER_NOT_FOUND));
            return;
        }

        clearError();
        connectionManager.removePinLabel();
        sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, ApplicationScene.MAIN_PAGE_HOME);
      
    }

    @FXML
    public void CreateNewAccount(MouseEvent event) throws IOException 
    {  
        if (!connectionManager.isConnected()) {
            Stage stage = sceneManager.getWindowStage(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW);
            stage.fireEvent(new ConnectionEvent(ConnectionEvent.SERVER_NOT_FOUND));
            return;
        }
        connectionManager.removePinLabel();
        sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, ApplicationScene.REGISTRATION_PAGE);
    }

    @FXML
    public void accedi_Account(ActionEvent event) throws IOException 
    {
        if (!connectionManager.isConnected()) {
            Stage stage = sceneManager.getWindowStage(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW);
            stage.fireEvent(new ConnectionEvent(ConnectionEvent.SERVER_NOT_FOUND));
            return;
        }
            
        
        boolean error = false;
        clearError();

        //verifico validità del campo
        if(userName == null || userName.getText().length() == 0) {
            this.LabeErrorlField1.setText(Main.applicationLanguage == 0 ? "Inserisci il tuo nome utente o il tuo indirizzo e-mail." : "Enter your username or e-mail address.");
            this.LabeErrorlField1.setVisible(true);
            this.LabelError_IMG1.setVisible(true);
            userName.setId("text-field_error");
            error = true;
        }

        //verifico validità del campo
        if(password == null || password.getText().length() == 0) {
            this.LabeErrorlField2.setText(Main.applicationLanguage == 0 ? "Inserisci la tua password." : "Please enter your password.");
            this.LabeErrorlField2.setVisible(true);
            this.LabelError_IMG2.setVisible(true);
            password.setId("text-field_error");
            error = true;
        }

        if(error) {
            return;
        }


       

        try {
            ConnectionManager connection = ConnectionManager.getConnectionManager();
            Account response = connection.getAccount(userName.getText(), password.getText());

            if(response != null) {
                Main.account = response;
                connectionManager.removePinLabel();
                sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, ApplicationScene.MAIN_PAGE_HOME);
            }
        } 
        catch (InvalidUserNameException e) {
            this.LabeErrorlField1.setVisible(true);
            this.LabelError_IMG1.setVisible(true);
            userName.setId("text-field_error");
            //String target = e.getMessage().split(" not found")[0];
            
            this.LabeErrorlField1.setText(Main.applicationLanguage == 0 ? "Utente non trovato" : "User not found");
        }
        catch(InvalidEmailException e) {
            this.LabeErrorlField1.setVisible(true);
            this.LabelError_IMG1.setVisible(true);
            userName.setId("text-field_error");
            //String target = e.getMessage().split(" not found")[0];
            
            this.LabeErrorlField1.setText(Main.applicationLanguage == 0 ? "Utente non trovato" : "User not found");
        }
        catch (InvalidPasswordException e) {
            this.LabeErrorlField2.setVisible(true);
            this.LabelError_IMG2.setVisible(true);
            password.setId("text-field_error");
            this.LabeErrorlField2.setText(Main.applicationLanguage == 0 ? "Password errata" : e.getMessage()+".");
        }
        catch (Exception e) {
            
        }
        finally {
            
        }
    } 


    @FXML
    public void connectButtonEvent(MouseEvent event) 
    {
        boolean ok = true;

        if(IP == null || PORT == null || IP.getText().length() == 0 || PORT.getText().length() <= 3 || IP.getText().split("\\.").length != 4)
            ok = false;
        
        for (String n : IP.getText().split("\\.")) {
            try {
                if (n.length() == 0 || n.length() > 3 || Integer.parseInt(n) < 0 || Integer.parseInt(n) > 255)
                    ok = false;
            } 
            catch (Exception e) {
                ok = false;
            }  
        }

        if(!ok) {
            if(connectionManager.isConnected())
                connectionManager.disconnect();
            setLanguageText_and_color();
            return;
        }
        
        if(connectionManager.isConnected()) {
            connectionManager.disconnect();
            //connectionManager.setConnectionData(IP.getText(), Integer.parseInt(PORT.getText()));
            //connectionManager.connect();
            setLanguageText_and_color();
        }
        else {
            connectionManager.setConnectionData(IP.getText(), Integer.parseInt(PORT.getText()));
            connectionManager.connect();
            setLanguageText_and_color();    
        }   
    }
    
    
    
    @FXML
    public synchronized void checkConnection(KeyEvent event) 
    {
        /*if(event != null) {
            connectionManager.disconnect();
            
            connectionManager.connect();
        }
            
        Platform.runLater(() -> {
            if(!connectionManager.isConnected() && connectionManager.connect()) {
                if(connectionManager.isConnected()) 
                {
                    if(labelColor_state == false) {
                        labelColor_state = !labelColor_state;
                        connectionStatus.setText(EmotionalSongs.applicationLanguage == 0 ? "Server trovato" : "Server found");
                        connectionStatus.setStyle("-fx-text-fill: #1ED760;");
                        connectionIcon.setStyle(connectionIcon.getStyle() + "-fx-fill: #1ED760;");
                    }
                    
                }
            }
            else {
                if(!connectionManager.isConnected()) {

                    if(labelColor_state == false) {
                        labelColor_state = !labelColor_state;
                        connectionStatus.setText(EmotionalSongs.applicationLanguage == 0 ? "Server non trovato" : "Server not found");
                        connectionStatus.setStyle("-fx-text-fill: #F14934;");
                        connectionIcon.setStyle(connectionIcon.getStyle() + "-fx-fill: #F14934;");
                    }
                    //labelColor_state = !labelColor_state;
                   
                }
               
            } 
        });*/   
    }

    
    

    private void clearError() {
        this.LabeErrorlField1.setVisible(false);
        this.LabeErrorlField2.setVisible(false);
        this.LabelError_IMG1.setVisible(false);
        this.LabelError_IMG2.setVisible(false);

        userName.setId("");
        password.setId("");


    }

    @Override
    public void injectData(Object... data) {
        
    }

    @Override
    public void init(Object... data) {
        
    }
}
