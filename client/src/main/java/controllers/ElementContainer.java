package controllers;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import application.Main;
import application.ObjectsCache;
import application.SceneManager;
import enumClasses.ElementDisplayerMode;
import interfaces.Injectable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import objects.Album;
import objects.Artist;
import objects.MyImage;
import objects.Song;
import utility.UtilityOS;

public class ElementContainer extends ControllerBase implements Initializable, Injectable 
{
    @FXML public ImageView image;
    @FXML public Label title;
    @FXML public Label lable2;

    
    
    private Object displayedElement;
    
    
    public ElementContainer() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        //Random random = new Random();
        //int number = random.nextInt(22) + 1;

        this.title.setText("?");
        lable2.setText("");
        image.setImage(null);    
    }

    @Override
    public void init(Object... data) {
        
    }


    private void setImage(final String imgURL) {
        new Thread(() -> {
            try {
                final Image img = download_Image_From_Internet(imgURL, true);
                Platform.runLater(() -> {
                    image.setImage(img);
                });
            }   
            catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    
    @Override
    public void injectData(Object...data) {
        this.displayedElement = data[0];

        if(displayedElement instanceof Song) 
        {
            Song song = (Song) displayedElement;
            String imgUrl = song.getImage(MyImage.ImageSize.S300x300).getUrl();
            
            new Thread(() -> {
                final Album album = connectionManager.getAlbum_by_ID(song.getAlbumId());
                if(album != null) {
                    Platform.runLater(() -> {
                        lable2.setText((Main.applicationLanguage == 0 ? "Album " : "Album ") + album.getName());
                    });
                }
            }).start();

            Platform.runLater(() -> {
                title.setText(song.getTitle());
            });

            setImage(imgUrl);
        } 
        else if(displayedElement instanceof Artist) {

        }
        else if(displayedElement instanceof Album) 
        {
            Album album = (Album) displayedElement;
            //System.out.println(album);
            
            new Thread(() -> {
                final Artist artist = connectionManager.getArtistByID(album.getArtistID());
                if(artist != null) {
                    Platform.runLater(() -> {
                        lable2.setText((Main.applicationLanguage == 0 ? "Artista " : "Artist ") + artist.getName());
                    });
                }
            }).start();
            
            Platform.runLater(() -> {title.setText(album.getName());});

            String imgUrl = album.getImage(MyImage.ImageSize.S300x300).getUrl();
            
            setImage(imgUrl);


            // String imgUrl = album.getImage(MyImage.ImageSize.S300x300).getUrl();
            // Image img = (Image) ObjectsCache.getInstance().getItem(ObjectsCache.CacheObjectType.IMAGE,imgUrl);

            // if(img == null) {
            //     String imgURL = album.getImage(MyImage.ImageSize.S300x300).getUrl();
            //     //Main.imageDownloader.addImageToDownload(imgURL, image);
            //     //EmotionalSongs.imageDownloader.addImageToDownload(song.getImage(MyImage.ImageSize.S300x300).getUrl(), image);
            //     new Thread(() -> {
            //         try {
            //             Image img2 = download_Image_From_Internet(imgURL, true);
            //             Platform.runLater(() -> {image.setImage(img);});
            //         } catch (IOException e) {
            //             e.printStackTrace();
            //         }
            //     }).start();
            // }
            // else {
            //     Platform.runLater(() -> {image.setImage(img);});
                
            // }
        }   
    }
    
    

    @FXML
    public void hovered(MouseEvent event) {
        
    }

    @FXML
    public void openLink(MouseEvent event) 
    {
        if(displayedElement instanceof Song) { 
            sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, ElementDisplayerMode.SHOW_SONG, displayedElement);
        }
        else if(displayedElement instanceof Artist) {
            sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, ElementDisplayerMode.SHOW_ARTIST, displayedElement);
        }
        else if(displayedElement instanceof Album) {
            sceneManager.setScene(SceneManager.ApplicationWinodws.EMOTIONALSONGS_WINDOW, SceneManager.ApplicationScene.DISPLAY_ELEMENT_PAGE, ElementDisplayerMode.SHOW_ALBUM, displayedElement);
        }
    }
}
