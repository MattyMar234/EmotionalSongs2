package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

public class WindowContainerController extends ControllerBase implements Initializable
{
    private static WindowContainerController reference;
    public BorderPane anchor;

    public WindowContainerController() {
        super();
        reference = this;
    }

    public static WindowContainerController getActiveInstance() {
        return reference;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchor.setMinWidth(800);
        anchor.setMinHeight(900);
    }

    public void setAccessPage() {
        this.anchor.getChildren().clear();
        super.setApplicationPage("AccessPage.fxml", this.anchor); //ApplicationAccessPage
    }

    public void setRegistrationPage() {
        this.anchor.getChildren().clear();
        super.setApplicationPage("UserRegistration.fxml", this.anchor); //ApplicationAccessPage
    }

    public void setMainPage() {
        this.anchor.getChildren().clear();
        //super.setApplicationPage("MainPage.fxml", this.anchor); //ApplicationAccessPage
        super.setApplicationPage("Homepage.fxml", this.anchor); 
    }


}
