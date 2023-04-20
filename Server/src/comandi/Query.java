package comandi;


import dabase.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Query {
    /*
    Mostrare il contenuto delle tabelle Studenti ed Esami.
     */
    public void queryUno(Database db) throws SQLException {
        ResultSet result = db.submitQuery("SELECT *" +
                "FROM Studenti JOIN Esami ON Studenti.matricola = Esami.studente");

        while (result.next()) {
            int matricola = result.getInt("matricola");
            String cognome = result.getString("cognome");
            String nome = result.getString("nome");
            int eta = result.getInt("eta");

            int codiceCorso = result.getInt("codiceCorso");
            int studente = result.getInt("studente");
            Date data = result.getDate("data");
            int voto = result.getInt("voto");

            System.out.println(matricola + " " + cognome + " " + nome + " " + eta + " " + codiceCorso + " " + data + " " + voto);
        }
    }

    public void queryDue(Database db) throws SQLException {
        ResultSet result = db.submitQuery("SELECT Studenti.matricola, AVG(voto) as media, COUNT(*) as esamiSuperati "
                + "FROM Studenti JOIN Esami ON Studenti.matricola=Esami.studente "
                + "WHERE Esami.voto >= 18 "
                + "GROUP BY Studenti.matricola "
                + "HAVING AVG(voto)>27");

        while (result.next()) {
            int matricola = result.getInt("matricola");
            double media = result.getDouble("media");
            int esamiSuperati = result.getInt("esamiSuperati");

            System.out.println(matricola + " - media: " + media + ", esami superati: " + esamiSuperati);
        }
    }

    public void queryTre(Database db) throws SQLException {
        ResultSet result = db.submitQuery("SELECT Matricola , Cognome , Nome, count(*) AS Bocciature "
                + "FROM STUDENTI JOIN ESAMI ON Matricola=Studente "
                + "WHERE Voto < 18 "
                + "GROUP BY Matricola , Cognome , Nome "
                + "ORDER BY Cognome , Nome, Matricola");

        while (result.next()) {
            int matricola = result.getInt("matricola");
            String cognome = result.getString("cognome");
            String nome = result.getString("nome");
            int numBocciature = result.getInt("bocciature");

            System.out.println(matricola + " " + cognome + " " + nome + " numero bocciature: " + numBocciature);
        }
    }

    public int numIscritti(Database db, int appello) throws SQLException {

        String query = "SELECT COUNT(*) as numIscritti "
                + "FROM Prenotazioni NATURAL JOIN Appelli "
                + "WHERE codiceAppello = " + appello + " "
                + "GROUP BY codiceAppello";
        System.out.println("Query::numIscritti - query: " + query);
        ResultSet result = db.submitQuery(query);

        if (result.next()) {
            int numIscritti = result.getInt("numIscritti");
            return numIscritti;
        } else
            return 0;

    }

    public int numMaxIscritti(Database db, int appello) throws SQLException {
        ResultSet result = db.submitQuery("SELECT iscrizioniMax "
                + "FROM APPELLI "
                + "WHERE codiceAppello = " + appello);

        result.next();
        int numMaxIscritti = result.getInt("iscrizioniMax");
        return numMaxIscritti;
    }

}
