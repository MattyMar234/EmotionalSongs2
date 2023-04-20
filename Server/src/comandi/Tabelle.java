package comandi;

import dabase.Database;

import javax.swing.plaf.nimbus.State;
import java.sql.SQLException;
import java.sql.Statement;

public class Tabelle {

    public void createTableStudenti(Database db) throws SQLException {
        String studenti = "CREATE TABLE IF NOT EXISTS Studenti ("
                + "matricola NUMERIC PRIMARY KEY, "
                + "cognome VARCHAR(30) NOT NULL, "
                + "nome VARCHAR(30) NOT NULL, "
                + "eta NUMERIC(3) "
                + ");";
        Statement statement = db.getStatement();
        statement.execute(studenti);
    }

    public void createTableEsami(Database db) throws SQLException {
        String esami = "CREATE TABLE IF NOT EXISTS Esami ("
                + "codiceCorso NUMERIC, "
                + "studente NUMERIC, "
                + "data DATE NOT NULL, "
                + "voto NUMERIC NOT NULL, "
                + "PRIMARY KEY (codiceCorso, studente, data), "
                + "FOREIGN KEY (studente) REFERENCES Studenti(matricola) ON DELETE CASCADE "
                + ");";
        Statement statement = db.getStatement();
        statement.execute(esami);
    }

    public void createTableAppelli(Database db) throws SQLException {
        String appelli = "CREATE TABLE IF NOT EXISTS Appelli ("
                + "codiceAppello NUMERIC NOT NULL,"
                + "codiceCorso NUMERIC NOT NULL,"
                + "data DATE NOT NULL,"
                + "iscrizioniMax NUMERIC NOT NULL,"
                + "PRIMARY KEY (codiceAppello)"
                + ");";
        Statement statement = db.getStatement();
        statement.execute(appelli);
    }

    public void createTablePrenotazioni(Database db) throws SQLException {
        String prenotazioni = "CREATE TABLE IF NOT EXISTS Prenotazioni ("
                + "codiceAppello NUMERIC,"
                + "studente NUMERIC,"
                + "PRIMARY KEY (codiceAppello, studente),"
                + "FOREIGN KEY (codiceAppello) REFERENCES Appelli(codiceAppello) ON DELETE CASCADE,"
                + "FOREIGN KEY (studente) REFERENCES Studenti(matricola) ON DELETE CASCADE"
                + ");";
        Statement statement = db.getStatement();
        statement.execute(prenotazioni);
    }
}
