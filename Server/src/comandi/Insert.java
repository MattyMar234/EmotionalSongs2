package comandi;

import dabase.Database;
import datamodel.DataModel;
import datamodel.Esame;
import datamodel.Studente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class Insert {
    public void insertValues(Database db, DataModel dm, int numStudenti, int numEsami) throws SQLException {
        dm.populateModel(numStudenti, numEsami);

        PreparedStatement prepared = db.getConnection().prepareStatement("INSERT INTO Studenti(matricola, cognome, nome, eta) VALUES(?,?,?,?)");

        for(Studente s : dm.getStudenti()){
            prepared.setInt(1, s.getMatricola());
            prepared.setString(2, s.getCognome());
            prepared.setString(3, s.getNome());
            prepared.setInt(4, s.getEta());

            prepared.executeUpdate();
        }

        prepared = db.getConnection().prepareStatement("INSERT INTO Esami(codiceCorso, studente, data, voto) VALUES(?,?,?,?)");
        for(Esame e: dm.getEsami()){
            prepared.setInt(1, e.getCodiceCorso());
            prepared.setInt(2, e.getStudente());
            prepared.setDate(3, new java.sql.Date(e.getData().getTime()));
            prepared.setInt(4, e.getVoto());

            prepared.executeUpdate();
        }
    }

    public void insertAppelli(Database db, int appello, int corso, Date data, int numMax) throws SQLException {
        // Ci serve un oggetto Date della libreria sql per poter fare l'insert.
        java.sql.Date sqlDate = new java.sql.Date(data.getTime());

        PreparedStatement prepared = db.getConnection().prepareStatement("INSERT INTO Appelli VALUES (?,?,?,?)");

        prepared.setInt(1, appello);
        prepared.setInt(2, corso);
        prepared.setDate(3, sqlDate);
        prepared.setInt(4, numMax);

        prepared.executeUpdate();
    }

    public void insertPrenotazioni(Database db, int studente, int appello) throws SQLException {
        // https://ecomputernotes.com/java/jdbc/jdbc-transaction

        // settare setAutoccommit FALSO
        Connection connection = db.getConnection();
        connection.setAutoCommit(false);

        // La struttura è questa:
        // 1. estraiamo il numero di studenti prenotati ad un appello
        // 2. se minore del max, allora creiamo l'oggetto di cui fare l'insert e facciamo il commit
        // 3. altrimmenti roll back

        // 1. query
        Query query = new Query();
        int numIscritti = query.numIscritti(db, appello);
        int numMaxIscritti = query.numMaxIscritti(db, appello);

        //2. insert
        if (numIscritti < numMaxIscritti) {
            PreparedStatement prepared = db.getConnection().prepareStatement("INSERT INTO Prenotazioni VALUES (?, ?)");
            prepared.setInt(1, appello);
            prepared.setInt(2, studente);

            prepared.executeUpdate();
            // Termine del blocco di transazione
            connection.commit();
        } else { // 3. altrimenti rollback
            connection.rollback();
            System.out.println("Insert::insertPrenotazioni - prenotazione non effettuata: rollback!");
        }


        /*
        Un esempio di gestione delle transazioni comune, consiste nel gestire un update in caso di problematiche
        che causano una SQLException (es. problemi di connessione, input errati, etc.):

        Connection connection = db.getConnection();
        connection.setAutoCommit(false);
        try{
            PreparedStatement prepared = connection.prepareStatement("INSERT INTO Prenotazioni VALUES (?, ?)");
            prepared.setInt(1, appello);
            prepared.setInt(2, studente);

            prepared.executeUpdate();
            // Termine del blocco di transazione
            connection.commit();
        }catch (SQLException e){
            connection.rollback();
        }
*/

    }
}
