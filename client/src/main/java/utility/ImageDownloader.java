package utility;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import application.ObjectsCache;

public class ImageDownloader extends Thread 
{
    private BlockingQueue<Node> queue = new LinkedBlockingDeque<>();

    private class Node {
        public String url;
        public ImageView image;

        public Node(String url, ImageView image) {
            this.url = url;
            this.image = image;
        }
    }

    

    public ImageDownloader() {
        setDaemon(true);
        start();
    }

    public synchronized void addImageToDownload(String url, ImageView image) {
        queue.add(new Node(url, image));
        notifyAll();
    }

    @Override
    public void run() {

        while(true) {

            synchronized(this) {
                while(queue.size() == 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            while(queue.size() > 0) {
                Node node = queue.poll();

                try {
                    //System.out.println(node.url);
                    Image img = download_Image_From_Internet(node.url);
                    
                    ObjectsCache.addImage(node.url, img);
                    
                    Platform.runLater(() -> {node.image.setImage(img);});
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
    
}
