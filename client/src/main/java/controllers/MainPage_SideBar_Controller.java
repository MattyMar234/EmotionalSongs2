package controllers;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;

import application.Main;
import application.SceneManager;
import interfaces.Injectable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * Questa classe grafica gestisce il menù laterale della pagina principale dell'applicazione
 */
public class MainPage_SideBar_Controller extends ControllerBase implements Initializable, Injectable
{
    // ========================= Label ========================= //


    // ========================= pane ========================= //
  

    @FXML public BorderPane anchor;
    @FXML public StackPane stackPane;


    // ========================= Buttons ========================= //
    @FXML public Button homeButton;
    @FXML public Button exploreButton;
    @FXML public Button playlistButton;
    //@FXML public Button optionsButton;
    @FXML public Button CambioButton;
    @FXML public Button ExitButton;

    @FXML public Button buttonBackward;
    @FXML public Button buttonForward;
    @FXML public TextField searchField;

    @FXML public FontIcon userImage;
    @FXML public Label userName;


    @FXML public ImageView logo;
    
    
    public void setLogo() {
        Image image = new Image(getClass().getResource("image/generic/Logo.png").toExternalForm(), 500, 500, true, true);
        logo.setImage(image);
    }



    ArrayList<Button> buttons = new ArrayList<Button>();
    //private final String ButtonColor = "-fx-background-color: #0bb813;" + "-fx-text-fill:#ffffff;";
    private final String ButtonColor = "-fx-background-color: #f18100f6;" + "-fx-text-fill:#ffffff;";
    protected int state = 1;





    public MainPage_SideBar_Controller() throws IOException {
        super();
    }


    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {
        anchor_for_injectScene = anchor;

        if(Main.account == null) {
            userName.setText(Main.applicationLanguage == 0 ? "Accedi" : "Login");
            playlistButton.setDisable(true);
        }
        else {
            userName.setText(Main.account.getNickname());
        }

        

        


        /*ClearActiveButtons();
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
    public void BackwardAction(MouseEvent event) {
        Main.userActions.undo();
    }

    @FXML
    public void ForwardAction(MouseEvent event) {
        Main.userActions.redo();
    }


    @FXML
    public void sidebtnClick(ActionEvent event) {

    }

    @FXML
    public void search(KeyEvent event) {

    }

    @FXML
    public void tryLogin(MouseEvent event) {
        if(Main.account == null) {
            SceneManager.getInstance().setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.ACCESS_PAGE);
        }
    }
        
    

    @FXML
    public void viewUserInformation(MouseEvent event) {

    }



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
    public void setHomePage(ActionEvent event) throws IOException {
        SceneManager.getInstance().setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.MAIN_PAGE_HOME);
    }

    @FXML
    public void SetPlayList(ActionEvent event) throws IOException
    {
        state = 2;
        ClearActiveButtons();
        //this.buttons.get(2).setStyle(ButtonColor);
        MainPage_ElementDisplayer_Controller element = (MainPage_ElementDisplayer_Controller) SceneManager.getInstance().setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.MAIN_PAGE_PLAYLIST);
        
        /*if(element != null) {
            element.injectData(ElementDisplayerMode.SHOW_USER_PLAYLISTS);
        }*/
        
    }

    @FXML
    public void SetExplorePage(ActionEvent event) throws IOException {
        if(state != 3 ) {
            state = 3;
            
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


    @Override
    public void injectData(Object... data) {
        
    }


    @Override
    public void init(Object... data) {
       
    }

    



    // -------------------------------- Cambio pagine -------------------------------- //



}
