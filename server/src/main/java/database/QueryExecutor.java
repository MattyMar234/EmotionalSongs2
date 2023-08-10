package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import database.PredefinedSQLCode.Colonne;
import database.PredefinedSQLCode.Tabelle;
import objects.MyImage;
import objects.Song;

public class QueryExecutor {


    private static HashMap<Colonne, Object> getObjectData(ResultSet resultSet, Tabelle tabella) throws SQLException {
        Colonne[] coll = PredefinedSQLCode.tablesAttributes.get(tabella);
        HashMap<Colonne, Object> table = new HashMap<>();

        for (Colonne colonna : coll) {
            table.put(colonna, resultSet.getObject(colonna.getName()));
        }
        return table;
    }

    public static ArrayList<Song> getTopPopularSongs(long limit, long offset) throws SQLException {

        ArrayList<Song> result = new ArrayList<Song>();
        Database database = Database.getInstance();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Canzone c ORDER BY c.popularity DESC ");
        sb.append("LIMIT " + limit + " OFFSET " + offset + ";");


        ResultSet resultSet = database.submitQuery(sb.toString());

        while (resultSet.next()) { 
            Song song = new Song(getObjectData(resultSet, Tabelle.SONG));
            result.add(song);    
        }

        resultSet.close();

        for (Song song : result) {
            song.addImages(getAlbumImages_by_ID(song.getAlbumId()));
        }

        
        

        return result; 
    }

    public static ArrayList<MyImage> getAlbumImages_by_ID(String ID) throws SQLException {

        ArrayList<MyImage> result = new ArrayList<MyImage>();
        Database database = Database.getInstance();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + PredefinedSQLCode.Tabelle.ALBUM_IMAGES.toString());
        sb.append(" WHERE " + PredefinedSQLCode.Colonne.ID.getName() + " = '" + ID +"';");

        System.out.println(sb.toString());
        ResultSet resultSet = database.submitQuery(sb.toString());

        while (resultSet.next()) { 
            result.add(new MyImage(getObjectData(resultSet, Tabelle.ALBUM_IMAGES)));
        }

        return result; 
    }
}
