package server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JFileChooser;

import Parser.JsonParser;
import database.PredefinedSQLCode;
import database.PredefinedSQLCode.Colonne;
import database.PredefinedSQLCode.Tabelle;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import server.Terminal.MessageType;
import server.Terminal;
import utility.FileElementCounter;
import utility.GenericThread;
import utility.PathFormatter;
import utility.WaithingAnimationThread;

public class Loader {

    private static Loader instance;
    private Terminal terminal;
    private App main;

    //pattern singleton
    public static Loader getInstance() {
        if (instance == null) {
            instance = new Loader();
        }
        return instance;
    }

    private Loader() {
        this.terminal = Terminal.getInstance();
        this.main = App.getInstance();
    }


    private void buildTables(boolean clear) {
        try {
            for (Tabelle table : PredefinedSQLCode.Tabelle.values()) 
            {
                if(clear)// || table== Tabelle.SONG)
                    this.main.database.submitQuery(PredefinedSQLCode.deleteTable_Queries.get(table));

                terminal.printInfo_ln("Creating table: " + table);
                this.main.database.submitQuery(PredefinedSQLCode.createTable_Queries.get(table)); 
            }
        } catch (SQLException e) {
            terminal.printError_ln(e.toString());
            e.printStackTrace();
            System.exit(0);
        }
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public int loadApplicationData() throws IOException, SQLException
    {
        final boolean test = false;
        final String ARTIST = "Artists";
        final String ALBUM = "Album";
        final String TRACKS = "Tracks";
        final String[] folders = {ARTIST, ALBUM, TRACKS};

        HashMap<String, File> foldersPath = new HashMap<String, File>();
        File database_information_folder;


        terminal.printInfo_ln("start database configuration...");
        
        //==================================== SELEZIONE DEI FILE ====================================//
        if(!test) {

            final JFileChooser fileChooser = new JFileChooser(PathFormatter.formatPath(System.getProperty("user.home") + "/Desktop"));
            fileChooser.setVisible(true);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


            switch(fileChooser.showOpenDialog(null))
            {
                case JFileChooser.CANCEL_OPTION:
                    terminal.printInfo_ln("database configuration ended");
                    return 0;

                case JFileChooser.APPROVE_OPTION:
                    database_information_folder = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    terminal.printInfo_ln("select folder: " + database_information_folder);
                    break;

                default:
                    terminal.printError_ln("FileDialog Error");
                    return 0;
            }
        }
        else {
            database_information_folder = new File("C:\\Users\\Utente\\Desktop\\Dataset Progetto\\Output");
        }
        //==================================== VALIDITA' FILE ====================================//
        //verifico la validità della cartella
        
        if (database_information_folder.isDirectory()) {
            boolean tuttePresenti = true;

            for (String cartella : folders) {
                File subFolder = new File(database_information_folder, cartella);

                if (!subFolder.exists() || !subFolder.isDirectory()) {
                    tuttePresenti = false;
                    terminal.printError_ln(cartella + " folder not found");
                } 
                else {
                    foldersPath.put(cartella, subFolder);
                    terminal.printSucces_ln(cartella + " folder found");
                }
            }

            if(!tuttePresenti) {
                terminal.printInfo_ln("database configuration ended\n");
                return 0;
            }
            else {
                terminal.printSucces_ln("All folders found\n");
            }
        }
        else {
            terminal.printError_ln("invalid path\n");
            return 0;
        }

        //==================================== VERFICA ELEMENTI ====================================//
        HashMap<String, BlockingQueue<File>> queues = new HashMap<String, BlockingQueue<File>>();
        BlockingQueue<File> artistsQueue = new LinkedBlockingDeque<File>();
        BlockingQueue<File> albumsQueue  = new LinkedBlockingDeque<File>();
        BlockingQueue<File> traksQueue   = new LinkedBlockingDeque<File>();

        queues.put(ARTIST, artistsQueue);
        queues.put(TRACKS, traksQueue);
        queues.put(ALBUM,  albumsQueue);

        HashMap<String, Long> elementCount = new HashMap<String, Long>();
        long artistsCount = 0L;
        long albumCount   = 0L;
        long traksCount   = 0L;


        for(int k = 0; k < 3; k++) 
        {
            WaithingAnimationThread t = new WaithingAnimationThread("Retrieving " + folders[k] + " files");
            terminal.printInfo_ln("analyzing " + foldersPath.get(folders[k]).getAbsolutePath());
            
            if(folders[k] == TRACKS || folders[k] == ALBUM) {

                //creo l'array gli contenenti i thread per la ricerca
                FileElementCounter[] threads = new FileElementCounter[foldersPath.get(folders[k]).listFiles().length];
                FileElementCounter.resetCounter();
                int index = 0;

                //conto gli album e salvo i file
                
                terminal.printInfo_ln("Folders found:" + foldersPath.get(folders[k]).listFiles().length);
                System.out.flush();
                t.start();

                for (File folder : foldersPath.get(folders[k]).listFiles())  { 
                    threads[index++] = new FileElementCounter(folder, queues.get(folders[k]),(path) -> {
                        return JsonParser.getFile_element_count(path);
                    });
                }
                
                for (int i = 0; i < threads.length; i++) {
                    try {
                        threads[i].join();
                    } 
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

                try {t.terminate(); t.join();} catch (InterruptedException e) {}
                terminal.printInfo_ln("Files found: " + queues.get(folders[k]).size());

                if(folders[k] == ALBUM) {
                    albumCount = FileElementCounter.getCounterValue();
                    terminal.printInfo_ln("Album found: " + albumCount);
                }
                else if(folders[k] == TRACKS) {
                    traksCount = FileElementCounter.getCounterValue();
                    terminal.printInfo_ln("tracks found: " + traksCount);
                } 
            }
            else if(folders[k] == ARTIST) {
                terminal.printInfo_ln("Files found:" + foldersPath.get(folders[k]).listFiles().length);
                System.out.flush();
                t.start();
                
                
                for (File file : foldersPath.get(ARTIST).listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".json")) { 

                        artistsCount += JsonParser.getFile_element_count(file.getAbsolutePath());
                        artistsQueue.add(file);
                    }
                }

                t.terminate();
                try {t.join();} catch (InterruptedException e) {}
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
                System.out.flush();
                terminal.printInfo_ln("artists found: " + artistsCount);
            }
        }

        elementCount.put(ARTIST, artistsCount);
        elementCount.put(ALBUM, albumCount);
        elementCount.put(TRACKS, traksCount);
        terminal.printSucces_ln("All file collected\n--------------------------------");

        //===================================== CARICO I DATI =====================================//

        if(true) {
            return 0;
        }

        //creo le tabelle
        buildTables(false);
        
        for(String key : folders) {
            ProgressBar progressBar = new ProgressBar(MessageType.INFO.toString() +  " Loading " + key, elementCount.get(key), ProgressBarStyle.ASCII);
            BlockingQueue<File> current_queue = queues.get(key);
            //File file;

            progressBar.start();
            progressBar.stepTo(0);

            //do il tempo di caricare la barra
            try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
            GenericThread[] thraedList = new GenericThread[128];

            for(int i = 0; i < thraedList.length; i++) 
                thraedList[i]  = new GenericThread((data) -> {
                BlockingQueue<File> fileQueue = (BlockingQueue<File>) data[0];

                while(fileQueue.size() > 0) 
                {
                    Object[] data_for_Queries = null;
                    File file = current_queue.poll();


                    switch(key) {
                        case ARTIST: data_for_Queries = JsonParser.parseArtists(file.getAbsolutePath()); break;
                        case ALBUM:  data_for_Queries = JsonParser.parseAlbums(file.getAbsolutePath());  break;
                        case TRACKS: data_for_Queries = JsonParser.parseTracks(file.getAbsolutePath());  break;
                    }

                    HashMap<String, Object> ElementsData1  = (HashMap<String, Object>) data_for_Queries[0];
                    HashMap<String, Object> ElementsImgaes = (HashMap<String, Object>) data_for_Queries[1];
                    HashMap<String, Object> ElementsData2  = (HashMap<String, Object>) data_for_Queries[2];

                    switch(key) 
                    {
                        case ARTIST -> {
                            //itero tutti gli ID che ci sono nel File           
                            for (String artist_ID : ElementsData1.keySet()) 
                            {
                                HashMap<String, Object> artist  = (HashMap<String, Object>) ElementsData1.get(artist_ID);       //ottengo l'HashTable che rappresenta l'artista di quell'ID
                                ArrayList<Object>       images  = (ArrayList<Object>)       ElementsImgaes.get(artist_ID);      //ottengo l'HashTable che rappresenta le immagini di quell'ID
                                ArrayList<String>       generes = (ArrayList<String>)       ElementsData2.get(artist_ID);       //ottengo la lista dei generi              

                                //ARTIST data
                                PredefinedSQLCode.crea_INSER_query_ed_esegui(artist, PredefinedSQLCode.Tabelle.ARTIST, this.main); 

                                //ARTIST Images
                                for(Object o : images) {
                                    PredefinedSQLCode.crea_INSER_query_ed_esegui((HashMap<String, Object>) o, PredefinedSQLCode.Tabelle.ARTIST_IMAGES, this.main);
                                }

                                //GENRES e Artist Genres
                                for(String o : generes) {

                                    HashMap<String, Object> table1 = new HashMap<String, Object>();
                                    table1.put(PredefinedSQLCode.Colonne.GENERE_MUSICALE.getName(), o);

                                    HashMap<String, Object> table2 = new HashMap<String, Object>();
                                    table2.put(PredefinedSQLCode.Colonne.GENERE_MUSICALE.getName(), o);
                                    table2.put(PredefinedSQLCode.Colonne.ID.getName(), artist_ID);

                                    PredefinedSQLCode.crea_INSER_query_ed_esegui(table1, PredefinedSQLCode.Tabelle.GENERI_MUSICALI, this.main);
                                    PredefinedSQLCode.crea_INSER_query_ed_esegui(table2, PredefinedSQLCode.Tabelle.GENERI_ARTISTA, this.main);
                                    
                                }
                                progressBar.step();
                            }
                        }

                        case ALBUM -> {
                        
                            //itero tutti gli ID che ci sono nel File           
                            for (String album_ID : ElementsData1.keySet()) 
                            {
                                //System.out.println("ID: " + album_ID);
                                HashMap<String, Object> album   = (HashMap<String, Object>) ElementsData1.get(album_ID);        //ottengo l'HashTable che rappresenta l'artista di quell'ID
                                ArrayList<Object> images        = (ArrayList<Object>)       ElementsImgaes.get(album_ID);       //ottengo l'HashTable che rappresenta le immagini di quell'ID
                                ArrayList<String> albumArtists  = (ArrayList<String>)       ElementsData2.get(album_ID);        //ottengo la lista dei generi              

                                //ALBUM data
                                PredefinedSQLCode.crea_INSER_query_ed_esegui(album, PredefinedSQLCode.Tabelle.ALBUM, this.main); 

                                //ALBUM Images
                                for(Object o : images) {
                                    PredefinedSQLCode.crea_INSER_query_ed_esegui((HashMap<String, Object>) o, PredefinedSQLCode.Tabelle.ALBUM_IMAGES, this.main);
                                }

                                //GENRES e Artist Genres
                                //for(String o : albumArtists) { 
                                //    Solo se ggiungo una tabella che contiene le inform,azioni di chi sono gli aristi che hanno creato qull'album.
                                //}
                                
                                progressBar.step();
                            }
                        }

                        case TRACKS -> {

                            //itero tutti gli ID che ci sono nel File           
                            for (String trackID : ElementsData1.keySet()) 
                            {
                                //System.out.println("ID: " + album_ID);
                                HashMap<String, Object> track   = (HashMap<String, Object>) ElementsData1.get(trackID);        //ottengo l'HashTable che rappresenta l'artista di quell'ID
                                ArrayList<String> autors_id     = (ArrayList<String>)       ElementsData2.get(trackID);        //ottengo la lista dei generi              

                                //TRACK data
                                PredefinedSQLCode.crea_INSER_query_ed_esegui(track, PredefinedSQLCode.Tabelle.SONG, this.main); 

                                //AUTORI canzone
                                for(Object id : autors_id) {

                                    HashMap<String, Object> table1 = new HashMap<String, Object>();
                                    table1.put(Colonne.ARTIST_ID_REF.getName(), id);
                                    table1.put(Colonne.SONG_ID_REF.getName(), trackID);

                                    PredefinedSQLCode.crea_INSER_query_ed_esegui(table1, PredefinedSQLCode.Tabelle.SONG_AUTORS, this.main);
                                }
                                progressBar.step();
                            }
                        }
                    }
                }   
            }, current_queue, 50);
            
            for(int i = 0; i < thraedList.length; i++) 
                try {thraedList[i].join();} catch (InterruptedException e) {e.printStackTrace();}
            
            
            progressBar.stop();
        }
        return 0;  
    }


    private static void recursivePrint(Object obj) {
        if (obj instanceof HashMap) {
            HashMap<String, Object> map = (HashMap<String, Object>) obj;
            for (String key : map.keySet()) {
                System.out.print(key + ": ");
                recursivePrint(map.get(key));
            }
        } else if (obj instanceof List) {
            List<String> list = (List<String>) obj;
            for (String item : list) {
                recursivePrint(item);
            }
        } else if (obj instanceof Integer) {
            int number = (Integer) obj;
            System.out.println(number);
        } else if (obj instanceof String) {
            String text = (String) obj;
            System.out.println(text);
        }
    }



    
}
