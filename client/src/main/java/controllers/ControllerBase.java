package controllers;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import application.ConnectionManager;
import application.Main;
import application.SceneManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import javax.imageio.ImageIO;

public abstract class ControllerBase {

    private Main MainClassReference;
    private HashMap<Object, String[]> windowObjectsTexts = new HashMap<Object, String[]>();
    private HashMap<Object, String[]> ObjectsErrorVisualization = new HashMap<Object, String[]>();
    
    public final ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
    public final SceneManager sceneManager = SceneManager.getInstance();

    public Object anchor_for_injectScene;


    public ControllerBase() {
        
    }

    protected Image download_Image_From_Internet(String imageURL) throws IOException 
    {
        //return new Image(imageURL);
        URL url = new URL(imageURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        int responseCode = httpURLConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                return new Image(new ByteArrayInputStream(inputStream.readAllBytes()));
            }
        } else {
            throw new IOException("Errore durante il download. Codice di risposta: " + responseCode);
        }
    }

    protected boolean imConnected() {
        return connectionManager.isConnected();
    }


    /*public void setApplicationPage(String sceneName, BorderPane anchor) {
        try {
            MainClassReference.SetScene(sceneName, anchor);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }*/
    

    protected void addObjectText_Translations(Object object, String[] texts) {
        windowObjectsTexts.put(object, texts);
        //System.out.println(object.getClass());
    }

    protected void setTextsLanguage() {

        for (Object object : windowObjectsTexts.keySet()) {

            if(object instanceof javafx.scene.control.Label)
                ((javafx.scene.control.Label) object).setText(windowObjectsTexts.get(object)[Main.applicationLanguage]);
            
            else if(object instanceof Button) {
                ((javafx.scene.control.Button) object).setText(windowObjectsTexts.get(object)[Main.applicationLanguage]);   
            }

            else if(object instanceof javafx.scene.control.TextField)
                ((javafx.scene.control.TextField) object).setPromptText(windowObjectsTexts.get(object)[Main.applicationLanguage]);
        }
        
    }


    protected void clearErrors() {
        for (Object object : ObjectsErrorVisualization.keySet()) {
            if(object instanceof javafx.scene.control.Label)
                ((javafx.scene.control.Label) object).setVisible(false);
        }  
    } 

    protected Image AwesomeIcon_to_Image(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon type, int size) {
        
        FontAwesomeIconView iconView = new FontAwesomeIconView(type, Integer.toString(size) + "px");
        Scene scene = new Scene(new StackPane(iconView));
        WritableImage writableImg = iconView.snapshot(null, null);
        Image img = SwingFXUtils.toFXImage(SwingFXUtils.fromFXImage(writableImg, null), null);

        return img;
    }

    protected static void openLink(String link) throws IOException, URISyntaxException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(link));
        } else {
            // Se il desktop non è supportato o l'azione di apertura del browser non è supportata,
            // puoi gestire l'apertura del link in modo diverso qui (ad esempio, visualizzando il link in un terminale).
            System.out.println("Desktop o l'azione di apertura del browser non sono supportati.");
        }
    }

    
}


