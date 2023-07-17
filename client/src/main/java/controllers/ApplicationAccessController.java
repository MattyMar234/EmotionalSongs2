package controllers;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import Exceptions.InvalidPasswordException;
import Exceptions.InvalidUserNameException;
import application.ConnectionManager;
import application.EmotionalSongs;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Questa classe gestisce l'accesso all'applicazione
 */
public class ApplicationAccessController extends ControllerBase implements Initializable 
{
    private static ApplicationAccessController reference;
    private ObservableList<ImageView> imgs = FXCollections.observableArrayList();

    
    @FXML public Label LabeErrorlField1;
    @FXML public Label LabeErrorlField2;
    @FXML public Label LabelField1;
    @FXML public Label LabelField2;
    @FXML public Label NewAccount;
    
    @FXML public ImageView LabelError_IMG1;
    @FXML public ImageView LabelError_IMG2;

    @FXML public TextField userName;
    @FXML public PasswordField password;

    @FXML public Button LoginButton;
    @FXML public Button NoAccountButton;

    @FXML public AnchorPane pane1;
    @FXML public CheckBox rememberCheckBox;
    @FXML public ComboBox<ImageView> flags;

    

    public ApplicationAccessController() {
        super();
        reference = this;
    }

    public static ApplicationAccessController getActiveInstance() {
        return reference;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        super.addObjectText_Translations(LabelField1, new String[] {"Nome utente o indirizzo e-mail", "User name or e-mail address"});
        super.addObjectText_Translations(LabelField2, new String[] {"Password", "Password"});
        super.addObjectText_Translations(NewAccount, new String[] {"Crea un Account", "Create Account"});
        super.addObjectText_Translations(LoginButton, new String[] {"Accedi all'Account", "Login"});
        super.addObjectText_Translations(NoAccountButton, new String[] {"Continua senza Account", "Continue without account"});
        super.addObjectText_Translations(userName, new String[] {"L'email oppure l'userID", "Email or userID"});
        super.setTextsLanguage();


        this.LabelError_IMG1.setImage(super.AwesomeIcon_to_Image(FontAwesomeIcon.EXCLAMATION_CIRCLE, 80));
        this.LabelError_IMG2.setImage(super.AwesomeIcon_to_Image(FontAwesomeIcon.EXCLAMATION_CIRCLE, 20));

    
        //carico tutte le immagini delle lingue
        File folder = new File(EmotionalSongs.flagsFolder);
        System.out.println(folder.getAbsolutePath());
        File[] listOfFiles = folder.listFiles();

        Queue<File> queue = new LinkedList<File>();
        for(File f : listOfFiles) queue.add(f);

        int index = 1;
        while(queue.size() > 0) 
        {
            File f = queue.poll(); //ottengo e rimuovo

            //se non è un file, viene comunque rimosso
            if(f.isFile()) 
            {   
                //se l'immagine cossiponde a quella che cerco
                int number = Integer.parseInt(f.getName().split("_")[0]);
                
                if(number == index) 
                {
                    //creo la nuova immagine e l'aggiungo
                    try {
                        ImageView img = new ImageView(SwingFXUtils.toFXImage(ImageIO.read(f), null));

                        img.setFitHeight(36);
                        img.setFitWidth(36);
                        imgs.add(img);
                        flags.getItems().add(img);
                        index++;
                    } 
                    catch (IOException e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }
                }
                else {
                    queue.add(f);
                }
            }
        }


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

        flags.getSelectionModel().select(EmotionalSongs.applicationLanguage);
        clearError();
    }
          
    
    public class StatusListCell extends ListCell<ImageView> 
    {
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

    
    @FXML
    public void handleLoginButtonAction() {
        // Gestisci l'azione del pulsante di accesso
    }

    

    @FXML
    public void changeLanguage(ActionEvent event) 
    {
        
        EmotionalSongs.applicationLanguage = flags.getSelectionModel().getSelectedIndex();
        System.out.println(EmotionalSongs.applicationLanguage);
        
    
        flags.getSelectionModel().select(EmotionalSongs.applicationLanguage);
        //flags.getItems().clear();
        //flags.getItems().addAll(imgs);
        //flags.getSelectionModel().select(imgs.get(EmotionalSongs.applicationLanguage));
        
        

        /*for(int  i = 0; i < flags.getItems().size(); i++) {
            flags.getItems().get(0).setImage(imgs.get(i).getImage());
        }*/

        super.setTextsLanguage();
    }


    @FXML
    public void NoAccount(ActionEvent event) throws IOException {

        clearError();
        WindowContainerController.getActiveInstance().setMainPage();
        /*this.application.ConnectedAccount = new UnregisteredAccount();
        Stage Window = (Stage) NoAccountButton.getScene().getWindow();
        super.SwitchScene("MainPage");*/
    }

    @FXML
    public void CreateNewAccount(MouseEvent event) throws IOException {

        clearError();
        WindowContainerController.getActiveInstance().setRegistrationPage();
        //super.setApplicationPage("UserRegistration", );
        //Stage Window = (Stage) NoAccountButton.getScene().getWindow();
        //super.SwitchScene("UserRegistration");
    }

    @FXML
    public void accedi_Account(ActionEvent event) throws IOException 
    {
        boolean error = false;
        clearError();

        //verifico validità del campo
        if(userName == null || userName.getText().length() == 0) {
            this.LabeErrorlField1.setText(EmotionalSongs.applicationLanguage == 0 ? "Inserisci il tuo nome utente o il tuo indirizzo e-mail." : "Enter your username or e-mail address.");
            this.LabeErrorlField1.setVisible(true);
            this.LabelError_IMG1.setVisible(true);
            userName.setId("text-field_error");
            error = true;
        }

        //verifico validità del campo
        if(password == null || password.getText().length() == 0) {
            this.LabeErrorlField2.setText(EmotionalSongs.applicationLanguage == 0 ? "Inserisci la tua password." : "Please enter your password.");
            this.LabeErrorlField2.setVisible(true);
            this.LabelError_IMG2.setVisible(true);
            password.setId("text-field_error");
            error = true;
        }

        if(error) {
            return;
        }


        ConnectionManager connection = ConnectionManager.getConnectionManager();
        Object response = null;

        try {
            response = connection.getService().getAccount(userName.getText(), password.getText());
        } 
        catch (InvalidUserNameException e) {
            this.LabeErrorlField1.setVisible(true);
            this.LabelError_IMG1.setVisible(true);
            userName.setId("text-field_error");
            String target = e.getMessage().split(" not found")[0];
            
            this.LabeErrorlField1.setText(EmotionalSongs.applicationLanguage == 0 ? target + " non trovato." : e.getMessage()+".");
        }
        catch (InvalidPasswordException e) {
            this.LabeErrorlField2.setVisible(true);
            this.LabelError_IMG2.setVisible(true);
            password.setId("text-field_error");
            this.LabeErrorlField2.setText(EmotionalSongs.applicationLanguage == 0 ? "Password errata" : e.getMessage()+".");
        }
        catch (Exception e) {
            // TODO: handle exception
        }
        finally {
            if(response == null) {
                return;
            }
        }
    }  

    private void clearError() {
        this.LabeErrorlField1.setVisible(false);
        this.LabeErrorlField2.setVisible(false);
        this.LabelError_IMG1.setVisible(false);
        this.LabelError_IMG2.setVisible(false);

        userName.setId("");
        password.setId("");


    }


    private class checker extends Thread {

        public checker() {
            super();
            setDaemon(true);
            start();
        }

        public void run() {

            while(true) {
                if(userName.getLength() >= 0) {
                    
                }
            }

        }

    }




}
