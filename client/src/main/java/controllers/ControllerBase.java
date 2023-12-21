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
import application.ObjectsCache;
import application.SceneManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import utility.UtilityOS;

import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

public abstract class ControllerBase {

    protected static final int backgroundImageIndex = 10;
    private Main MainClassReference;
    private HashMap<Object, String[]> windowObjectsTexts = new HashMap<Object, String[]>();
    private HashMap<Object, String[]> ObjectsErrorVisualization = new HashMap<Object, String[]>();
    

    public final ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
    public final SceneManager sceneManager = SceneManager.instance();

    public Object anchor_for_injectScene;

    @FXML public AnchorPane linearGradien_background_lower;
    @FXML public AnchorPane linearGradien_background_upper;


    public ControllerBase() {

        if(linearGradien_background_lower != null && linearGradien_background_upper != null) {
            setBackgroundLinearColor(backgroundImageIndex);
        }
        
    }

    protected void setBackgroundLinearColor(int...val) 
    {
        int number;

        if(linearGradien_background_lower == null || linearGradien_background_upper == null)
            return;

        if(val.length == 0) {
            Random random = new Random();
            number = random.nextInt(22) + 1;
        }
        else {
            number = val[0] % 22;
        }

        
        Image image = new Image(UtilityOS.formatPath(Main.ImageFolder + "\\colored_icon\\" + number + ".png"));  
        Color everegedColor = getAverageColor(image, -0.26f);

        //everegedColor = brightenColor(everegedColor, 0.1);

        String color = ColorToHex(everegedColor);
        this.linearGradien_background_upper.setStyle("-fx-background-color: linear-gradient(to top, #030300, "+ color +");"); 
        this.linearGradien_background_lower.setStyle("-fx-background-color: #030300;"); 
            
    }

    protected static Color getAverageColor(Image image, float brightnessFactor) {
        // Create a PixelReader to read pixel data
        PixelReader pixelReader = image.getPixelReader();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Initialize variables for calculating average color components
        double totalRed = 0;
        double totalGreen = 0;
        double totalBlue = 0;
        //double pixelCount = 0;
        // Iterate through all pixels and accumulate color components
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                /*if(color.getRed() > 32 && color.getGreen() > 32 && color.getBlue() > 32 ) {
                    totalRed += color.getRed();
                    totalGreen += color.getGreen();
                    totalBlue += color.getBlue();
                    pixelCount++;
                }*/

                double Red   = (double)Math.min(255, Math.max(0, color.getRed()   + (brightnessFactor * color.getRed())));
                double Green = (double)Math.min(255, Math.max(0, color.getGreen() + (brightnessFactor * color.getGreen())));
                double Blue  = (double)Math.min(255, Math.max(0, color.getBlue()  + (brightnessFactor * color.getBlue())));

               
                   
                totalRed += Red;
                totalGreen += Green;
                totalBlue += Blue;
            }
        }

        // Calculate average color components
        double pixelCount = width * height;
        double averageRed = totalRed / pixelCount;
        double averageGreen = totalGreen / pixelCount;
        double averageBlue = totalBlue / pixelCount;

        // Create a color from the average components
        return new Color(averageRed, averageGreen, averageBlue, 1);
    }


    protected static Color brightenColor(Color originalColor, double factor) {
        double r = Math.min(originalColor.getRed() + factor, 1.0);
        double g = Math.min(originalColor.getGreen() + factor, 1.0);
        double b = Math.min(originalColor.getBlue() + factor, 1.0);

        return new Color(r, g, b, originalColor.getOpacity());
    }
    

    protected String ColorToHex(Color color) {
        return String.format("#%02X%02X%02X", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255));
    }

    protected Image download_Image_From_Internet(String imageURL, boolean setDefaultImage) throws IOException 
    {
        Image image = (Image) ObjectsCache.getInstance().getItem(ObjectsCache.CacheObjectType.IMAGE, imageURL);

        if(image != null)
            return image;
        
        try {
            //return new Image(imageURL);
            //throw new IOException();
            
            
            URL url = new URL(imageURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            int responseCode = httpURLConnection.getResponseCode();
    
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = httpURLConnection.getInputStream()) {
                    image = new Image(new ByteArrayInputStream(inputStream.readAllBytes()));
                    return image;
                }
            } else {
                throw new IOException("Errore durante il download. Codice di risposta: " + responseCode);
            }
        } 
        catch (IOException e) {
            if(setDefaultImage) {
                Random random = new Random();
                int number = random.nextInt(22) + 1;
                image = new Image(UtilityOS.formatPath(Main.ImageFolder + "\\colored_icon\\" + number + ".png"));  
                return image;
            }
            throw e;
        }
        //alla fine aggiungo l'immagine alla cache
        finally {
            if(image != null) {
                ObjectsCache.getInstance().addItem(ObjectsCache.CacheObjectType.IMAGE, imageURL, image, false);
            }
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


