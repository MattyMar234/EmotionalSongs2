package comandi;

import dabase.Database;

import java.sql.SQLException;
import java.sql.Statement;

public class Delete {
    public void deleteTables(Database db) throws SQLException {
        Statement statement = db.getStatement();
        String[] drops = {"DROP TABLE IF EXISTS Esami", "DROP TABLE IF EXISTS Studenti"};
        for (String query: drops)
            statement.executeUpdate(query);
    }

    public static void deleteStudente(Database db, int matricola) throws SQLException {
        String sql = "DELETE FROM Studenti WHERE matricola = " + matricola;
        db.getStatement().executeUpdate(sql);
    }

}
