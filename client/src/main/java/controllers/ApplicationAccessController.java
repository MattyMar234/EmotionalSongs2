package controllers;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;

import application.ConnectionManager;
import application.EmotionalSongs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Questa classe gestisce l'accesso all'applicazione
 */
public class ApplicationAccessController extends ControllerBase implements Initializable {

    @FXML public Button LoginButton;
    @FXML public ImageView IMG;
    @FXML public Label NewAccount;
    @FXML public Button NoAccountButton;
    @FXML public AnchorPane labelButton;
    @FXML public AnchorPane pane1;
    @FXML public PasswordField password;
    @FXML public TextField userName;

    @FXML public ComboBox<ImageView> flags;
    private ObservableList<ImageView> imgs = FXCollections.observableArrayList();

    @FXML public Label LabelName;
    @FXML public Label labelPassword;


    private final static String [][] matrice = {
        {"Accedi all'Account", "Login"},     //LoginButton
        {"Continua senza Account", "Continue without account"}, //  //NoAccountButton
        {"Crea un Account", "Create Account"}           //NewAccount
    };


    public ApplicationAccessController() {
        super();
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        //super.setImage(IMG);
        //updatePageText();

        /*File folder = new File(EmotionalSongs.flagFolder);
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
                    catch (IOException e) 
                    {
                        System.out.println("=========================================");
                        System.out.println(e);
                        System.out.println("=========================================");
                        e.printStackTrace();
                        return;
                    }
           
                }
                else {
                    queue.add(f);
                }
            }
        }

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
        });*/

        //flags.getSelectionModel().select(EmotionalSongs.language);
            
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
    public void changeLanguage(ActionEvent event) {

        //EmotionalSongs.language = flags.getSelectionModel().getSelectedIndex();
        //flags.getSelectionModel().select(EmotionalSongs.language);

        //updatePageText();
        
    
        /*
        flags.getSelectionModel().select(null);
        flags.getItems().clear();
        flags.getItems().addAll(imgs);
        flags.getSelectionModel().select(imgs.get(EmotionalSongs.language));
        */

        

        /*for(int  i = 0; i < flags.getItems().size(); i++) {
            flags.getItems().get(0).setImage(imgs.get(i));
        }*/


    }

    /*private void updatePageText() {
        LoginButton.setText(AccessController.matrice[0][EmotionalSongs.language]);
        NoAccountButton.setText(AccessController.matrice[1][EmotionalSongs.language]);
        NewAccount.setText(AccessController.matrice[2][EmotionalSongs.language]);
    }*/

    @FXML
    public void NoAccount(ActionEvent event) throws IOException {

        /*this.application.ConnectedAccount = new UnregisteredAccount();
        Stage Window = (Stage) NoAccountButton.getScene().getWindow();
        super.SwitchScene("MainPage");*/
    }

    @FXML
    public void CreateNewAccount(MouseEvent event) throws IOException {
        //Stage Window = (Stage) NoAccountButton.getScene().getWindow();
        //super.SwitchScene("UserRegistration");
    }

    @FXML
    public void searchAccount(ActionEvent event) throws IOException 
    {
        boolean error = false;
        clearError();

        //verifico validità del campo
        if(userName == null || userName.getText().length() == 0) {
            this.LabelName.setText(EmotionalSongs.applicationLanguage == 0 ? "dati mancanti" : "missing data");
            this.userName.setStyle("-fx-border-color: #a50303;");
            this.LabelName.setVisible(true);
            error = true;
        }

        //verifico validità del campo
        if(password == null || password.getText().length() == 0) {
            this.labelPassword.setText(EmotionalSongs.applicationLanguage == 0 ? "dati mancanti" : "missing data");
            this.password.setStyle("-fx-border-color: #a50303;");mj
            this.labelPassword.setVisible(true);
            error = true;
        }


        ConnectionManager connection = ConnectionManager.getConnectionManager();
        

        try {
            Object response = connection.getService().getAccount(userName.getText(), password.getText());
        } catch (Exception e) {
            // TODO: handle exception
        }


        
            /*//verifica email
            if(.contains("@")) {
                TempAccount = application.AccountsManager.SearchByEmail(userName.getText());
                this.LabelName.setText(EmotionalSongs.language == 0 ? "email non valida" : "invalid email");
            }
            //verifica userID
            else {
                TempAccount = application.AccountsManager.SearchByID(userName.getText());
                this.LabelName.setText(EmotionalSongs.language == 0 ? "ID utente non valido" : "invalid user ID");
            }

            if(TempAccount == null) {
                this.userName.setStyle("-fx-border-color: #a50303;");
                this.LabelName.setVisible(true);
                error = true;
            }
        }*/

        


        if(error) {
            return;
        }

        /*if(!TempAccount.getPassword().equals(password.getText())) {
            this.labelPassword.setText(EmotionalSongs.language == 0 ? "password non valida" : "wrong password");
            this.password.setStyle("-fx-border-color: #a50303;");
            this.password.clear();
            this.labelPassword.setVisible(true);
            return;
        }*/
            

        

       

        //Stage Window = (Stage) NoAccountButton.getScene().getWindow();
        //super.SwitchScene("MainPage");
        

    }  



    private void clearError() {
        this.LabelName.setVisible(false);
        this.labelPassword.setVisible(false);
        this.userName.setStyle("-fx-border-color: transparent;");
        this.password.setStyle("-fx-border-color: transparent;");
    }




}
