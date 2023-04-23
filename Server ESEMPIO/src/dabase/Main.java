package dabase;

import comandi.Delete;
import comandi.Insert;
import comandi.Query;
import comandi.Tabelle;
import datamodel.DataModel;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws SQLException, ParseException {
        Database db = Database.getInstance();

        /*
        Eliminare le tabelle Studenti ed Esami
         */
        Delete delete = new Delete();
        delete.deleteTables(db);

        /*
        Creare tabelle STUDENTI ed ESAMI
         */
        Tabelle tabelle = new Tabelle();
        tabelle.createTableStudenti(db);
        tabelle.createTableEsami(db);

        /*
        Insert Studenti ed Esami
         */
        Insert insert = new Insert();
        DataModel dm = new DataModel();
        insert.insertValues(db, dm, 20, 10);

        /*
        Query
         */
        Query query = new Query();
        query.queryUno(db);
        query.queryDue(db);
        query.queryTre(db);

        /*
        Cancellare tutte le informazioni relative ad uno studente con una certa matricola dal DB.
         */
        delete.deleteStudente(db, 124231);

        /*
        Creazione tabelle Appelli e Prenotazioni
         */
        tabelle.createTableAppelli(db);
        tabelle.createTablePrenotazioni(db);

        /*
        Insert in Appelli
        In questo caso optiamo per un inserimento manuale e non randomico come per Studenti ed Esami.
        Nota: inserire un il codice di un corso esistente nel DB
         */
        String examDate="2023-02-20";
        Date data= new SimpleDateFormat("yyyy-MM-dd").parse(examDate);
        insert.insertAppelli(db, 1, 11, data, 1);

        /*
        Insert in Prenotazioni.
         */
        insert.insertPrenotazioni(db, 89379, 1);
        // Rollback:
        insert.insertPrenotazioni(db, 94730, 1);

    }
}