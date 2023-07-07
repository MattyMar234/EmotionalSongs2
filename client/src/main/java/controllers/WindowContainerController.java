package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

public class WindowContainerController extends ControllerBase implements Initializable
{
    public BorderPane anchor;

    public WindowContainerController() {
        super();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setAccessPage() {
        super.setApplicationPage("MainPage.fxml", this.anchor);
    }


}
