package controllers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;

import application.EmotionalSongs;
import application.SceneManager;
import application.SceneManager.SceneName;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import utility.Commune;
import utility.LocationsLoader;
import utility.Province;
import utility.Region;
import javafx.scene.image.ImageView;

/**
 * Questa classe grafica gestisce la registrazione di un nuovo account nell'applicazione
 */
public class NewUserRegistrationController extends ControllerBase implements Initializable 
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

    @FXML public Button confirmButton;
    @FXML public Button BackButton;

    @FXML public Label testoNome;
    @FXML public Label testoCognome;
    @FXML public Label titoloNomeUtente;
    @FXML public Label testoConfermaPassword;
    @FXML public Label testoNumeroCivico;
    @FXML public Label testoComune;
    @FXML public Label testoProvince;
    @FXML public Label testoCodiceFiscale;
    @FXML public Label testoViaPiazza;
    @FXML public Label labelID;
    @FXML public Label labelSing;
    @FXML public Label LabelEmail;
    @FXML public Label LabelPassword2;
    
    @FXML public TextField name;
    @FXML public TextField surname;
    @FXML public TextField userID;
    @FXML public TextField email;
    @FXML public TextField civicNumber;
    @FXML public TextField codiceFiscale;
    @FXML public TextField viaPiazza;
    @FXML public PasswordField password;
    @FXML public PasswordField password2;



    @FXML public ComboBox<String> cap;
    @FXML public ComboBox<String> common;
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
        //super.setImage(IMG1,IMG2,IMG3,IMG4,IMG5,IMG6,IMG7,IMG8,IMG9,IMG10,IMG11,IMG12,IMG13);
        super.addObjectText_Translations(testoNome,             new String[] {"Nome", "Name"});
        super.addObjectText_Translations(name,                  new String[] {"Nome", "First Name"});
        super.addObjectText_Translations(testoCognome,          new String[] {"Cognome", "Surname"});
        super.addObjectText_Translations(surname,               new String[] {"Cognome", "Last Name"});
        super.addObjectText_Translations(titoloNomeUtente,      new String[] {"Nome Utente", "NickName"});
        super.addObjectText_Translations(userID,                new String[] {"ID utente", "User ID"});
        super.addObjectText_Translations(testoConfermaPassword, new String[] {"Conferma Password", "Confirm Password"});
        super.addObjectText_Translations(testoNumeroCivico,     new String[] {"Numero Civico", "Civic Number"});
        super.addObjectText_Translations(civicNumber,           new String[] {"Numero", "Number"});
        super.addObjectText_Translations(testoComune,           new String[] {"Comune", "Municipality"});
        super.addObjectText_Translations(testoProvince,         new String[] {"Provincia", "Province"});
        super.addObjectText_Translations(testoCodiceFiscale,    new String[] {"Codice Fiscale", "Fiscal Code"});
        super.addObjectText_Translations(codiceFiscale,         new String[] {"Codice", "Code"});
        super.addObjectText_Translations(testoViaPiazza,        new String[] {"Via/Piazza", "Street/Square"});
        super.addObjectText_Translations(viaPiazza,             new String[] {"Via/Piazza", "Street/Square"});
        
        super.addObjectText_Translations(BackButton, new String[] {"Torna indietro", "Turn Back"});
        super.addObjectText_Translations(confirmButton, new String[] {"Registrati", "Sig In"});
    
        super.setTextsLanguage();


        c1 = new AutoCompleteComboBoxListener<>(cap);
        c2 = new AutoCompleteComboBoxListener<>(common);
        c3 = new AutoCompleteComboBoxListener<>(province);

        ClearLabelError(labelID);
        ClearLabelError(labelSing);
        ClearLabelError(LabelEmail);
        ClearLabelError(LabelPassword2);

        
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
                        common.getItems().add(c.getName());

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
            
                q = new LinkedList<>(common.getItems());
                common.getItems().clear();
                common.getItems().addAll(BucketSort(q,3,256 - (int)' ', (int)' '));
            
                q = new LinkedList<>(cap.getItems());
                cap.getItems().clear();
                cap.getItems().addAll(BucketSort(q,5,10, (int)'0')); 

            } catch (Exception e) {
                System.out.println(e);;
                e.printStackTrace();
                
            }
        });

        contenitori.add(new ElementsContainer(name          , null));
        contenitori.add(new ElementsContainer(surname       , null));
        contenitori.add(new ElementsContainer(userID        , null));
        contenitori.add(new ElementsContainer(email         , null)); //3
        contenitori.add(new ElementsContainer(password      , null));
        contenitori.add(new ElementsContainer(password2     , null));
        contenitori.add(new ElementsContainer(civicNumber   , null));
        contenitori.add(new ElementsContainer(codiceFiscale , null));
        contenitori.add(new ElementsContainer(viaPiazza     , null));
        
        contenitori.add(new ElementsContainer(cap     ));
        contenitori.add(new ElementsContainer(common     ));
        contenitori.add(new ElementsContainer(province));
    }


    @SuppressWarnings("unchecked")
    public void validateNewUser() throws IOException 
    {
        boolean error = false;

        ClearLabelError(labelID);
        ClearLabelError(labelSing);
        ClearLabelError(LabelEmail);
        ClearLabelError(LabelPassword2);


        // ================================= 1° verifica ================================= //
        //Verifico se tutti i campi sono stati compilati

        for(int i = 0; i < contenitori.size() - 3; i++) 
        {
            ElementsContainer container = contenitori.get(i);
            String data = container.text.getText();

            if(data == null || data.equals("")) {
                error = true;
            }
        }

        for(int i = contenitori.size() - 3; i < contenitori.size(); i++) 
        {
            ElementsContainer container = contenitori.get(i);
            String data = container.comb.getSelectionModel().getSelectedItem();

            if(data == null) {
                error = true;
            }
        }

        if(error) {
            SetLabelError(labelSing);
            labelSing.setText(EmotionalSongs.applicationLanguage == 0 ? "Campi non compilati" : "Fields not filled in");
            return;
        }  



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
       SceneManager.getInstance().showScene(SceneName.ACCESS_PAGE);
    }

    @FXML
    public void selectCap(ActionEvent event) {
        
        while(this.loader.isAlive());

        try {
            if(common.getSelectionModel().getSelectedItem() == null && province.getSelectionModel().getSelectedItem() == null || true) {
                String com = cap.getSelectionModel().getSelectedItem().split(" : ")[1];
    
                //metto il comune
                common.getSelectionModel().select(common.getItems().indexOf(com));
    
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
        String com = common.getSelectionModel().getSelectedItem();

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

    }
}
