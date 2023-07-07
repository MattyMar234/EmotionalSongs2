package application.controllers;


import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.scene.image.ImageView;

/**
 * Questa classe grafica gestisce il menù laterale della pagina principale dell'applicazione
 */
public class MainPageController extends ControllerBase implements Initializable 
{
    // ========================= Label ========================= //
   

    // ========================= pane ========================= //
    @FXML public AnchorPane SceneContainer;
    @FXML public BorderPane borderPane;
    

    // ========================= Buttons ========================= //
    @FXML public Button profileButton;
    @FXML public Button reposityButton;
    @FXML public Button playlistButton;
    //@FXML public Button optionsButton;
    @FXML public Button CambioButton;
    @FXML public Button ExitButton;


    @FXML public ImageView IMG1;
    @FXML public ImageView IMG2;
    @FXML public ImageView IMG3;
    @FXML public ImageView IMG6;
    @FXML public ImageView IMG5;
    
    ArrayList<Button> buttons = new ArrayList<Button>();
    //private final String ButtonColor = "-fx-background-color: #0bb813;" + "-fx-text-fill:#ffffff;";
    private final String ButtonColor = "-fx-background-color: #f18100f6;" + "-fx-text-fill:#ffffff;";
    protected int state = 1;
    
    
    
    final static private String [][] matrice = {
        {"Impostazioni", "Settings"},
        {"Cambia Account", "Change Account"},
        {"Esci", "Exit"}
    };


    public MainPageController() throws IOException {
        super();
    }


    @Override
    public void initialize(URL arg0, ResourceBundle arg1)  
    {
        //super.setImage(IMG1,IMG2,IMG3,IMG6,IMG5);
        //optionsButton.setText(MainPageController.matrice[0][EmotionalSongs.language]);
        //CambioButton.setText(MainPageController.matrice[1][EmotionalSongs.language]);
        //ExitButton.setText(MainPageController.matrice[2][EmotionalSongs.language]);

        /*
       
        for(Field f : this.getClass().getFields())
        {
            //System.out.println("Field " + f.getName());
            try {
                Object obj = this.getClass().getField(f.getName()).get(this);

                if(obj instanceof Button) {
                    buttons.add((Button) obj);
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        

        ClearActiveButtons();
        this.buttons.get(1).setStyle(ButtonColor);

        int index =  0;
        for(Button b : this.buttons) 
        {
            final int n = index++;

            b.setOnMouseEntered(e -> {
                if(state == n) {
                    b.setStyle(ButtonColor);//b.setStyle("-fx-background-color: #f18100f6");
                }
                else { 
                    b.setStyle("-fx-background-color: #9c9c9c66;" + "-fx-text-fill:#ffffff;");
                } 
                
                
            });

            b.setOnMouseExited(e -> {
                if(state == n) {
                    b.setStyle(ButtonColor);
                }
                else {
                    b.setStyle("-fx-background-color: transparent;" + "-fx-text-fill:#798AA6");
                }
            });
        }  */
        
        /*if(this.application.ConnectedAccount instanceof UnregisteredAccount) {
            this.playlistButton.setDisable(true);
            this.profileButton.setText("Sign In");
        }*/
       
        /*try {
            SetReposityPage();  
        } 
        catch (IOException e) { 
            e.printStackTrace();
        }*/
    }

    private void ClearActiveButtons() {
        
        //reimposto a tutti lo sfondo
        for(Button b : this.buttons) {
            b.setStyle("-fx-background-color: transparent;");
        }

        
    }


    // -------------------------------- eventi -------------------------------- //
    
    @FXML
    public void AccountButtonSelected(ActionEvent event) throws IOException 
    { 
        if(state != 0 ) {
            
                state = 0;
                ClearActiveButtons();
                this.buttons.get(0).setStyle(ButtonColor);
                //SetAccountPage();
            
            
        }
    }

    @FXML
    public void setReposity(ActionEvent event) throws IOException 
    {
        if(state != 1 ) {
            state = 1;
            ClearActiveButtons();
            this.buttons.get(1).setStyle(ButtonColor);
            SetReposityPage();
        }
    }

    @FXML
    public void SetPlayList(ActionEvent event) throws IOException 
    {
        state = 2;
        ClearActiveButtons();
        this.buttons.get(2).setStyle(ButtonColor);
        //SetPlayListPage();
    }

    @FXML
    public void SetOptions(ActionEvent event) throws IOException {
        if(state != 3 ) {
            state = 3;
            ClearActiveButtons();
            this.buttons.get(3).setStyle(ButtonColor);
            //SetOptionsPage();
        } 
    }


    @FXML
    public void access(MouseEvent event) throws IOException {
        //super.SwitchScene((Stage) icon.getScene().getWindow(), "LoadAccaunt");
     
    }

    @FXML
    public void ChangeAccount(ActionEvent event) throws IOException {
        
        if(state != 3 ) {
            state = 3;
            ClearActiveButtons();
            this.buttons.get(3).setStyle(ButtonColor);
        } 

        /*Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(this.application.mainStage);
        alert.setTitle(EmotionalSongs.language == 0 ? "Conferma" : "Confirm");
        alert.setHeaderText(buttons.get(3).getText());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setContentText(EmotionalSongs.language == 0 ? "vuoi cambiare account?" : "do you want to change account?");*/
        
        //Optional<ButtonType> result = alert.showAndWait();

        /*if(result.isPresent() && result.get() == ButtonType.OK) {
            SwitchScene("AccessPage"); 
        } */ 
    }

    @FXML
    public void esci(ActionEvent event) {

        if(state != 4 ) {
            state = 4;
            ClearActiveButtons();
            this.buttons.get(4).setStyle(ButtonColor);
        } 
        
        /*Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(this.application.mainStage);
        alert.setTitle(EmotionalSongs.language == 0 ? "Conferma" : "Confirm");
        alert.setHeaderText(buttons.get(4).getText());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setContentText(EmotionalSongs.language == 0 ? "vuoi uscire dal programma?" : "do you want to exit?");*/
        
        
        //Optional<ButtonType> result = alert.showAndWait();

        /*if(result.isPresent() && result.get() == ButtonType.OK) {
            application.logout(application.mainStage);
        }  */
    }



    // -------------------------------- Cambio pagine -------------------------------- //

    private void SetPage(FXMLLoader loader) throws IOException 
    {
        AnchorPane view = loader.load();
        borderPane.getChildren().removeAll();
        borderPane.setCenter(view);  
    }


    //REPOSITY
    public void SetReposityPage() throws IOException  {       
        /*FXMLLoader loader = getScenePage("MainPage_reposity");
        AnchorPane view = loader.load();

        currentLoader = loader;

        ((MainPageController_reposity)loader.getController()).mainController = this;

        borderPane.getChildren().removeAll();
        borderPane.setCenter(view);  */
    }

    //COMMENTS
    /*public void SetCommentsPage(Song song) throws IOException  {       
        SetCommentsPage(song, null);
    }*/

    //AddEmotion 
    /*public void SetAddEmotionPage(PlayList playlist, Song song) throws IOException {
        
        FXMLLoader loader = getScenePage("AddEmotionPage3");
        currentLoader = loader;

        loader.setControllerFactory( c -> {
            return new EmotionCreationPageController(this, playlist, song);
        });

        SetPage(loader);
    }*/

    //editplaylist -> COMMENTS
    /*public void SetCommentsPage(Song song, PlayList playlist) throws IOException  {       
        FXMLLoader loader = getScenePage("CommentsPage");
        currentLoader = loader;

        loader.setControllerFactory( c -> {
            return new CommentsPageController(this, song, playlist);
        });

        AnchorPane view = loader.load();

        loader.getController();
        
        //((CommentsPageController)loader.getController()).injectData(this, song);

        borderPane.getChildren().removeAll();
        borderPane.setCenter(view);  
    }*/

    //COMMENTS --> REPOSITY
    public void Comment_To_repository() throws IOException {
        SetReposityPage();
    }

    //EMOTION
    /*public void SetEmotionPage(Song song) throws IOException  {       
        FXMLLoader loader = getScenePage("EmotionPage");
        currentLoader = loader;

        loader.setControllerFactory( c -> {
            return new EmotionPageController(song, this);
        });

        AnchorPane view = loader.load();
        

        borderPane.getChildren().removeAll();
        borderPane.setCenter(view);  
    }*/

    //COMMENTS --> REPOSITY
    public void Emotion_To_repository() throws IOException {
        SetReposityPage();
    }

    //PLAYLIST
    /*public void SetPlayListPage() throws IOException  {  
        FXMLLoader loader = getScenePage("MainPage_PLaylist");
        currentLoader = loader;
        AnchorPane view = loader.load();

        ((MainPageController_playList)loader.getController()).SetMainControllerReference(this);
        
        borderPane.getChildren().removeAll();
        borderPane.setCenter(view);  
    } */ 

    //IMPOSTAZIONI
    /*public void SetOptionsPage() throws IOException 
    {  
        AnchorPane view = getScenePage("MainPage_impostazioni").load();
        borderPane.getChildren().removeAll();
        borderPane.setCenter(view);  
        
    }*/  

    public void SetProfilePage() throws IOException {

    }

    /*public void NewPlaylistPage() throws IOException {
        AnchorPane view = getScenePage("NewPlaylistCreationPage").load();
        borderPane.getChildren().removeAll();
        borderPane.setCenter(view);  
    }*/

    //editPLaylist
    /*public void SetPlaylistEditPage(PlayList playlist, MainPageController link) throws IOException 
    {
        FXMLLoader loader = getScenePage("EditPlaylist");
        currentLoader = loader;
        
        
        loader.setControllerFactory(c -> {    
            return new EditPlaylistController(playlist, link); // <-- parametri costruttore classe
        });

        AnchorPane view = loader.load();
        borderPane.getChildren().removeAll();
        borderPane.setCenter(view);  

        //getScenePage("MainPage_PLaylist").getController();
    }*/

    
    /*public void SetAccountPage() throws IOException
    {       
        AnchorPane view = getScenePage("MainPage_AccountInfo").load();
        borderPane.getChildren().removeAll();
        borderPane.setCenter(view);  
    }*/
}
