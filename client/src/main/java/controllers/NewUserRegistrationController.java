package controllers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Exceptions.InvalidEmailException;
import Exceptions.InvalidPasswordException;
import Exceptions.InvalidUserNameException;
import application.ConnectionManager;
import application.Main;
import application.SceneManager;
import application.SceneManager.ApplicationScene;
import applicationEvents.ConnectionEvent;
import interfaces.Injectable;
import interfaces.ServerServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import objects.Account;
import objects.Commune;
import objects.Province;
import objects.Region;
import utility.LocationsLoader;
import javafx.scene.image.ImageView;

/**
 * Questa classe grafica gestisce la registrazione di un nuovo account nell'applicazione
 */
public class NewUserRegistrationController extends ControllerBase implements Initializable, Injectable 
{
    @FXML public ImageView IMG1;
    @FXML public ImageView IMG10;
    @FXML public ImageView IMG11;
    @FXML public ImageView IMG12;
    @FXML public ImageView IMG13;
    @FXML public ImageView IMG2;
    @FXML public ImageView IMG3;
    @FXML public ImageView IMG4;
    @FXML public ImageView IMG5;
    @FXML public ImageView IMG6;
    @FXML public ImageView IMG7;
    @FXML public ImageView IMG8;
    @FXML public ImageView IMG9;
    @FXML public Label UserRegistration;
    @FXML public Label BackButton;

    @FXML public Button confirmButton;
    @FXML public Button NoAccountButton;

    // @FXML public Label testoNome;
    // @FXML public Label testoCognome;
    // @FXML public Label titoloNomeUtente;
    // @FXML public Label testoConfermaPassword;
    // @FXML public Label testoNumeroCivico;
    // @FXML public Label testoComune;
    // @FXML public Label testoProvince;
    // @FXML public Label testoCodiceFiscale;
    // @FXML public Label testoViaPiazza;
    // @FXML public Label labelID;
    // @FXML public Label labelSing;
    // @FXML public Label LabelEmail;
    // @FXML public Label LabelPassword2;
    
    @FXML public TextField userName;
    @FXML public TextField surname;
    @FXML public TextField userID;
    @FXML public TextField email;
    @FXML public TextField civicNumber;
    @FXML public TextField codiceFiscale;
    @FXML public TextField viaPiazza;
    @FXML public PasswordField password;
    @FXML public PasswordField password2;


    @FXML public ComboBox<String> cap;
    @FXML public ComboBox<String> commune;
    @FXML public ComboBox<String> province;

    public AutoCompleteComboBoxListener<String> c1;
    public AutoCompleteComboBoxListener<String> c2;
    public AutoCompleteComboBoxListener<String> c3;


    private LocationsLoader loader;



    Field [] variabili = this.getClass().getFields();      //tutte le variabili public
    
    ArrayList<ElementsContainer> contenitori = new ArrayList<ElementsContainer>();
    ArrayList<String> variabiliNome = new  ArrayList<String>();
    ArrayList<String> LabelsNome = new  ArrayList<String>();
    

    

    private class ElementsContainer 
    {
        public TextField text;
        public Label label;
        public ComboBox<String> comb;

        //costruttore 1
        public ElementsContainer(TextField text, Label label) {
            this.text = text;
            this.label = label;
        }

        //costruttore 2
        public ElementsContainer(ComboBox<String> comboBox) {
            this.comb = comboBox;
        }

    

        @Override
        public String toString() {
            return new String(text + " with " + label);
        }


        public void setError(String error) {
            if(text != null) {
                this.text.setStyle(
                  "-fx-border-color:red;" 
                  +"-fx-text-fill:red;"
                  + " -fx-border-width: 0.5px ;"
                  + " -fx-border-radius: 0 0 8 0 ;"
                );
            }
            else {
                this.comb.setStyle(
                  "-fx-border-color: red;" 
                  +"-fx-text-fill:red;"
                  + " -fx-border-width: 0.5px ;"
                  + " -fx-border-radius: 0 0 8 0 ;"
                );
            }
            //this.label.setVisible(true);
            //this.label.setText(error);
        }

        public void clearError() {
            //this.text.setStyle("");
            //if(label != null)this.label.setVisible(false);
            if(text != null) {
                this.text.setStyle(
                  "-fx-border-color: red;" 
                  +"-fx-text-fill:#FFFFFF;"
                  + " -fx-border-width: 0px ;"
                  + " -fx-border-radius: 0 0 8 0 ;"
                );
            }
            else {
                this.comb.setStyle(
                  "-fx-border-color: red;" 
                  +"-fx-text-fill:#FFFFFF;"
                  + " -fx-border-width: 0px ;"
                  + " -fx-border-radius: 0 0 8 0 ;"
                );
            }
        }
    }


    public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

        private ComboBox comboBox;
        private StringBuilder sb;
        private ObservableList<T> data;
        private boolean moveCaretToPos = false;
        private int caretPos;
    
        public AutoCompleteComboBoxListener(final ComboBox comboBox) {
            this.comboBox = comboBox;
            sb = new StringBuilder();
            data = comboBox.getItems();
    
            this.comboBox.setEditable(true);
            this.comboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
    
                @Override
                public void handle(KeyEvent t) {
                    comboBox.hide();
                }
            });
            this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
        }
    
        @Override
        public void handle(KeyEvent event) {
    
            if(event.getCode() == KeyCode.UP) {
                caretPos = -1;
                moveCaret(comboBox.getEditor().getText().length());
                return;
            } else if(event.getCode() == KeyCode.DOWN) {
                if(!comboBox.isShowing()) {
                    comboBox.show();
                }
                caretPos = -1;
                moveCaret(comboBox.getEditor().getText().length());
                return;
            } else if(event.getCode() == KeyCode.BACK_SPACE) {
                moveCaretToPos = true;
                caretPos = comboBox.getEditor().getCaretPosition();
            } else if(event.getCode() == KeyCode.DELETE) {
                moveCaretToPos = true;
                caretPos = comboBox.getEditor().getCaretPosition();
            }
    
            if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                    || event.isControlDown() || event.getCode() == KeyCode.HOME
                    || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
                return;
            }
    
            ObservableList list = FXCollections.observableArrayList();
            for (int i=0; i<data.size(); i++) {
                if(data.get(i).toString().toLowerCase().startsWith(
                    AutoCompleteComboBoxListener.this.comboBox
                    .getEditor().getText().toLowerCase())) {
                    list.add(data.get(i));
                }
            }
            String t = comboBox.getEditor().getText();
    
            comboBox.setItems(list);
            comboBox.getEditor().setText(t);
            if(!moveCaretToPos) {
                caretPos = -1;
            }
            moveCaret(t.length());
            if(!list.isEmpty()) {
                comboBox.show();
            }
        }
    
        private void moveCaret(int textLength) {
            if(caretPos == -1) {
                comboBox.getEditor().positionCaret(textLength);
            } else {
                comboBox.getEditor().positionCaret(caretPos);
            }
            moveCaretToPos = false;
        }

    }
    


    public NewUserRegistrationController() {
        super();
    }

    private void SetLabelError(Label l) {
        l.setStyle(
               "-fx-text-fill:#FF0000;"
            + " -fx-font-size: 14px ;"
        );  
    }

    private void ClearLabelError(Label l) {
        l.setStyle(
               "-fx-text-fill:transparent;"
            + " -fx-font-size: 14px ;"
        );  
    }

    @SuppressWarnings("unchecked")
    private Queue<String> BucketSort(Queue<String> l, int lenght, int chars, int offset) 
    {
        Queue<String> [] bucket = (Queue<String> []) new LinkedList[chars];
        String w = "";
    
        
        for(int i = 0; i < chars; i++) {
            bucket[i] = new LinkedList<>();
        }

        for(int i = lenght - 1; i >= 0; i--) {
            while(!l.isEmpty()) 
            {
                w = l.poll();                           //ottengo e rimuovo l'head

                int e = (int) w.charAt(i) - offset;    //determina l’indice e della lista corrispondente alla i-esima lettera di w
                bucket[e].add(w);                      //sposto la stringa

            }

            //sposto tutte le string in bucket[0] poi in l
            for(int j = 1; j < chars; j++ ) {
                while(bucket[j].size() > 0) 
                {
                    String str = bucket[j].poll();          //ottengo la string della j-esima lista 
                    bucket[0].add(str);                     //sposto la stringa
                }
            }
            l = new LinkedList<String>(bucket[0]);
            bucket[0].clear();
        }
        return l;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) 
    {
        // super.addObjectText_Translations(testoNome,             new String[] {"Nome", "Name"});
        super.addObjectText_Translations(userName,                  new String[] {"Nome", "First Name"});
        //super.addObjectText_Translations(testoCognome,          new String[] {"Cognome", "Surname"});
        super.addObjectText_Translations(surname,               new String[] {"Cognome", "Last Name"});
        //super.addObjectText_Translations(titoloNomeUtente,      new String[] {"Nome Utente", "NickName"});
        super.addObjectText_Translations(userID,                new String[] {"ID utente", "User ID"});
        //super.addObjectText_Translations(testoConfermaPassword, new String[] {"Conferma Password", "Confirm Password"});
        //super.addObjectText_Translations(testoNumeroCivico,     new String[] {"Numero Civico", "Civic Number"});
        super.addObjectText_Translations(civicNumber,           new String[] {"Numero", "Number"});
        //super.addObjectText_Translations(testoComune,           new String[] {"Comune", "Municipality"});
        //super.addObjectText_Translations(testoProvince,         new String[] {"Provincia", "Province"});
        //super.addObjectText_Translations(testoCodiceFiscale,    new String[] {"Codice Fiscale", "Fiscal Code"});
        super.addObjectText_Translations(codiceFiscale,         new String[] {"Codice Fiscale", "Fiscal Code"});
        //super.addObjectText_Translations(testoViaPiazza,        new String[] {"Via/Piazza", "Street/Square"});
        super.addObjectText_Translations(viaPiazza,             new String[] {"Via/Piazza", "Street/Square"});
        
        super.addObjectText_Translations(BackButton, new String[] {"Torna indietro", "Turn Back"});
        super.addObjectText_Translations(confirmButton, new String[] {"Registrati", "Sig In"});
    
        super.setTextsLanguage();

        BackButton.setText(Main.applicationLanguage == 0 ? "Ho già un account" : "I already have an account");
        confirmButton.setText(Main.applicationLanguage == 0 ? "Registrati" : "Sig In");
        NoAccountButton.setText(Main.applicationLanguage == 0 ? "Continua senza account" : "Continue without account");


        c1 = new AutoCompleteComboBoxListener<>(cap);
        c2 = new AutoCompleteComboBoxListener<>(commune);
        c3 = new AutoCompleteComboBoxListener<>(province);

        //ClearLabelError(labelID);
        //ClearLabelError(labelSing);
        //ClearLabelError(LabelEmail);
        //ClearLabelError(LabelPassword2);

        
        this.loader = new LocationsLoader((args) -> {

            final int minSize = 5;
            Queue<String> q;
            ArrayList<Region> regions = (ArrayList<Region>) args[0];

            for(Region r : regions) { 
                for(Province p : r.getProvincesList()) {
                    while(p.getName().length() < minSize) {
                        p.setName(p.getName() + " ");
                    }
                    province.getItems().add(p.getName());

                    for(Commune c : p.getCommonsList()) {
                        while(c.getName().length() < minSize) {
                            c.setName(p.getName() + " ");
                        }
                        commune.getItems().add(c.getName());

                        for(int i = 0; i < c.cap.length; i++) {
                            cap.getItems().add(c.cap[i] + " : " + c.getName());
                        }
                    }
                }
            }

            try {
                q = new LinkedList<>(province.getItems());
                //System.out.println(q.size());
                province.getItems().clear();
                province.getItems().addAll(BucketSort(q,3,256, 0));
                //System.out.println(province.getItems().size());
            
                q = new LinkedList<>(commune.getItems());
                commune.getItems().clear();
                commune.getItems().addAll(BucketSort(q,3,256 - (int)' ', (int)' '));
            
                q = new LinkedList<>(cap.getItems());
                cap.getItems().clear();
                cap.getItems().addAll(BucketSort(q,5,10, (int)'0')); 

            } catch (Exception e) {
                System.out.println(e);;
                e.printStackTrace();
                
            }
        });

        contenitori.add(new ElementsContainer(userName      , null));
        contenitori.add(new ElementsContainer(surname       , null));
        contenitori.add(new ElementsContainer(userID        , null));
        contenitori.add(new ElementsContainer(email         , null)); //3
        contenitori.add(new ElementsContainer(password      , null));
        contenitori.add(new ElementsContainer(password2     , null));
        contenitori.add(new ElementsContainer(civicNumber   , null));
        contenitori.add(new ElementsContainer(codiceFiscale , null));
        contenitori.add(new ElementsContainer(viaPiazza     , null));
        
        contenitori.add(new ElementsContainer(cap     ));
        contenitori.add(new ElementsContainer(commune     ));
        contenitori.add(new ElementsContainer(province));
    }


    @SuppressWarnings("unchecked")
    public void validateNewUser() throws IOException 
    {
        boolean error = false;

        // ClearLabelError(labelID);
        // ClearLabelError(labelSing);
        // ClearLabelError(LabelEmail);
        // ClearLabelError(LabelPassword2);


        // ================================= 1° verifica ================================= //
        //Verifico se tutti i campi sono stati compilati e che siano corretti

        for(int i = 0; i < contenitori.size() - 3; i++) 
        {
            ElementsContainer container = contenitori.get(i);
            String data = container.text.getText();

            if(data == null || data.equals("")) {
                //SetLabelError(labelSing);
                //labelSing.setText(Main.applicationLanguage == 0 ? "Campi non compilati" : "Fields not filled in");
                
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle(Main.applicationLanguage == 0 ? "Campi non compilati" : "Fields not filled in");
                alert.setHeaderText("");
            
                if(Main.applicationLanguage == 0) {
                    alert.setContentText("Non tutti i campi sono stati compilati");
                }
                else if(Main.applicationLanguage == 1) {
                    alert.setContentText("Not all fields are filled in");
                }

                alert.getButtonTypes().setAll(ButtonType.OK);

                Optional<ButtonType> result = alert.showAndWait();
                return;
            }
        }

        //test combox
        for(int i = contenitori.size() - 3; i < contenitori.size(); i++) 
        {
            ElementsContainer container = contenitori.get(i);
            String data = container.comb.getSelectionModel().getSelectedItem();

            if(data == null) {
                
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(Main.applicationLanguage == 0 ? "Valori non selezionati" : "Values not selected");
                alert.setHeaderText("");
                // if(Main.applicationLanguage == 0) {
                //     alert.setContentText("sei sicuro di volore escire dal tuo Account?");
                // }
                // else if(Main.applicationLanguage == 1) {
                //     alert.setContentText("Are you sure you want to logout from your Account?");
                // }

                alert.getButtonTypes().setAll(ButtonType.OK);
                Optional<ButtonType> result = alert.showAndWait();
                return;
            }
        }

        if(!email.getText().endsWith("@gmail.com")) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(Main.applicationLanguage == 0 ? "Formato email" : "Email format");
            alert.setHeaderText("");
        
            if(Main.applicationLanguage == 0) {
                alert.setContentText("Il formato dell'email non è valido.\nDeve finire con: '@gmail.com'");
            }
            else if(Main.applicationLanguage == 1) {
                alert.setContentText("Email format is not valid.\nShould end with: '@gmail.com'");
            }

            alert.getButtonTypes().setAll(ButtonType.OK);
            Optional<ButtonType> result = alert.showAndWait();
            return;
        }

        

        //passwords
        if(!password.getText().equals(password2.getText())) {
            // SetLabelError(labelSing);
            // labelSing.setText(Main.applicationLanguage == 0 ? "Le password non coincidono" : "Passwords do not match");
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(Main.applicationLanguage == 0 ? "Password" : "Password");
            alert.setHeaderText("");
        
            if(Main.applicationLanguage == 0) {
                alert.setContentText("Le password non coincidono");
            }
            else if(Main.applicationLanguage == 1) {
                alert.setContentText("Passwords not matched");
            }

            alert.getButtonTypes().setAll(ButtonType.OK);
            Optional<ButtonType> result = alert.showAndWait();
            return;
        }

       

        String regex = "^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$";
        String fiscalCode = this.codiceFiscale.getText(); // Replace with the fiscal code you want to validate //utilizzare getText()
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fiscalCode);

        //MRARSS13S08H501H
        if(!matcher.matches()) {
            // SetLabelError(labelSing);
            // labelSing.setText(EmotionalSongs.applicationLanguage == 0 ? "Codice fiscale non valido" : "Invalid fiscal code");
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(Main.applicationLanguage == 0 ? "Codice Fiscale" : "Fiscal Code");
            alert.setHeaderText("");
        
            if(Main.applicationLanguage == 0) {
                alert.setContentText("Il codice fiscale inserito non è valido");
            }
            else if(Main.applicationLanguage == 1) {
                alert.setContentText("The inserted fiscal code is not valid");
            }

            alert.getButtonTypes().setAll(ButtonType.OK);
            //alert.getButtonTypes().setAll(ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            return;
        }


        switch (testComboBox()) {
            case 0:
                break;

            case -1:
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(Main.applicationLanguage == 0 ? "Valore selezionato non valido" : "Invalid value selected");
                alert.setHeaderText("");
            
                if(Main.applicationLanguage == 0) {
                    alert.setContentText("Il valore del comune non è valido");
                }
                else if(Main.applicationLanguage == 1) {
                    alert.setContentText("The value of the commune is not valid");
                }

                alert.getButtonTypes().setAll(ButtonType.OK);
                Optional<ButtonType> result = alert.showAndWait();
                return;
            
            case -2:
                Alert alert2 = new Alert(AlertType.INFORMATION);
                alert2.setTitle(Main.applicationLanguage == 0 ? "Valore selezionato non valido" : "Invalid value selected");
                alert2.setHeaderText("");
            
                if(Main.applicationLanguage == 0) {
                    alert2.setContentText("Il valore della provincia non è valido");
                }
                else if(Main.applicationLanguage == 1) {
                    alert2.setContentText("The value of the province is not valid");
                }

                alert2.getButtonTypes().setAll(ButtonType.OK);
                alert2.showAndWait();
                return;

            case -3:
                Alert alert3 = new Alert(AlertType.INFORMATION);
                alert3.setTitle(Main.applicationLanguage == 0 ? "Valore selezionato non valido" : "Invalid value selected");
                alert3.setHeaderText("");
            
                if(Main.applicationLanguage == 0) {
                    alert3.setContentText("Il valore del CAP non è valido");
                }
                else if(Main.applicationLanguage == 1) {
                    alert3.setContentText("The value of the CAP is not valid");
                }

                alert3.getButtonTypes().setAll(ButtonType.OK);
                alert3.showAndWait();
                return;
        
            default:
                Alert alert4 = new Alert(AlertType.INFORMATION);
                alert4.setTitle(Main.applicationLanguage == 0 ? "Errore di sistema" : "System error");
                alert4.setHeaderText("");

                alert4.getButtonTypes().setAll(ButtonType.OK);
                alert4.showAndWait();
                return;
        }

       

        ConnectionManager service = ConnectionManager.getConnectionManager();
        
        try {
            String cap_str = cap.getSelectionModel().getSelectedItem().split(":")[0];
            Account account = service.addAccount(userName.getText(), surname.getText(), userID.getText(), codiceFiscale.getText(), email.getText(), password.getText(), civicNumber.getText(), viaPiazza.getText(), cap_str, commune.getSelectionModel().getSelectedItem(), province.getSelectionModel().getSelectedItem());
            
            if(account != null) {
                Main.account = account;
                SceneManager.instance().setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW,ApplicationScene.MAIN_PAGE_HOME);
            }
        } 
        catch (InvalidUserNameException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(Main.applicationLanguage == 0 ? "ID non valido" : "Invalid ID");
            alert.setHeaderText("");
        
            if(Main.applicationLanguage == 0) {
                alert.setContentText("L'Id inserito è già stato utilizzato");
            }
            else if(Main.applicationLanguage == 1) {
                alert.setContentText("The ID you have inserted is already used");
            }

            alert.getButtonTypes().setAll(ButtonType.OK);
            Optional<ButtonType> result = alert.showAndWait();
        }
        catch (InvalidEmailException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(Main.applicationLanguage == 0 ? "Email non valida" : "Invalid email");
            alert.setHeaderText("");
        
            if(Main.applicationLanguage == 0) {
                alert.setContentText("L'email che hai inserito è già stata utilizzata");
            }
            else if(Main.applicationLanguage == 1) {
                alert.setContentText("The email you have inserted is already used");
            }

            alert.getButtonTypes().setAll(ButtonType.OK);
            //alert.getButtonTypes().setAll(ButtonType.NO);

            Optional<ButtonType> result = alert.showAndWait();
        } 
        catch (InvalidPasswordException e) {
            e.printStackTrace();
        }
    }

    private int testComboBox() 
    {
        return 0;
        // ArrayList<Region> regions = this.loader.getLocations();

        // for(Region r : regions) 
        // { 
        //     for(Province p : r.getProvincesList()) 
        //     {
        //         if(province.getSelectionModel().getSelectedItem().equals(p.getName()))
        //         {
        //             String com = commune.getSelectionModel().getSelectedItem();
                    
        //             for(Commune c : p.getCommonsList()) 
        //             {
        //                 if(c.getName().equals(com)) {
        //                     String selectedCup = cap.getSelectionModel().getSelectedItem().split(":")[0];
        //                     for(String c_cap : c.cap) 
        //                     {
        //                         if(c_cap.equals(selectedCup)) {
        //                             return 0;
        //                         }
        //                     }
        //                     return -3; 
        //                 }      
        //             }
        //             return -1;          
        //         }
        //     }
        //     return -2;
        // }
        // return -4;
    }

    
    @FXML
    public void typed(KeyEvent event) 
    {
        for(ElementsContainer container : contenitori) {
            //container.clearError();
        }
    }

    @FXML
    public void TurnBack() throws IOException {
       SceneManager.instance().setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW,ApplicationScene.ACCESS_PAGE);
    }

    @FXML
    public void NoAccount(ActionEvent event) throws IOException {
        sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, ApplicationScene.MAIN_PAGE_HOME); 
    }

    @FXML
    public void selectCap(ActionEvent event) {
        
        while(this.loader.isAlive());

        try {
            if(commune.getSelectionModel().getSelectedItem() == null && province.getSelectionModel().getSelectedItem() == null || true) {
                String com = cap.getSelectionModel().getSelectedItem().split(" : ")[1];
    
                //metto il comune
                commune.getSelectionModel().select(commune.getItems().indexOf(com));
    
                ArrayList<Region> regions = this.loader.getLocations();
                boolean finded = false;
    
                for(Region r : regions) { 
                    if(finded) break;
                    
                    for(Province p : r.getProvincesList()) {
                        if(finded) break;
                        
                        for(Commune c : p.getCommonsList()) 
                        {
                            if(c.getName().equals(com)) {
                                province.getSelectionModel().select(province.getItems().indexOf(p.getName()));
                                finded = !finded;
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            
        }
        
    }

    @FXML
    public void selectCommon(ActionEvent event) 
    {
        while(this.loader.isAlive());
        ArrayList<Region> regions = this.loader.getLocations();
        String com = commune.getSelectionModel().getSelectedItem();

        boolean finded = false;

        //set CAP
        if(cap.getSelectionModel().getSelectedItem() == null || true) {
            for(Region r : regions) { 
                if(finded) break;

                for(Province p : r.getProvincesList()) {
                    if(finded) break;

                    for(Commune c : p.getCommonsList()) 
                    {
                        if(c.getName().equals(com)) 
                        {
                            if(c.cap.length == 1) {
                                cap.getSelectionModel().select(c.cap[0] + " : " + c.getName());  
                            }

                            finded = !finded;
                            break;
                        }
                    }
                }
            }
        }

        //set Province
        if(province.getSelectionModel().getSelectedItem() == null || true) 
        {
            for(Region r : regions) { 
                for(Province p : r.getProvincesList()) {
                    for(Commune c : p.getCommonsList()) 
                    {
                        if(c.getName().equals(com)) {
                            province.getSelectionModel().select(province.getItems().indexOf(p.getName()));
                            finded = !finded;
                            return;
                        }
                    }
                }
            }
        }
    }

    @FXML
    public void selectProvince(ActionEvent event) {
        ArrayList<Region> regions = this.loader.getLocations();

        for(Region r : regions) { 
            for(Province p : r.getProvincesList()) 
            {
                if(province.getSelectionModel().getSelectedItem().equals(p.getName()))
                {
                    String com = commune.getSelectionModel().getSelectedItem();
                    
                    for(Commune c : p.getCommonsList()) 
                    {
                        if(c.getName().equals(com)) {
                           return;
                        }
                    }

                    commune.getSelectionModel().clearSelection();
                    cap.getSelectionModel().clearSelection();
                            
                }
            }
        }
    }

    @Override
    public void injectData(Object... data) {
        
    }

    @Override
    public void init(Object... data) {
       
    }
}
