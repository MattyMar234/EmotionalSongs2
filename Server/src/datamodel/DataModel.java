package datamodel;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Random;

public class DataModel {
    /*
    Variabili contenenti studenti ed esami che verranno inseriti nella tabella
     */
    private ArrayList<Studente> studenti;
    private  ArrayList<Esame> esami;

    public DataModel(){
        studenti = new ArrayList<Studente>();
        esami = new ArrayList<Esame>();
    }

    public ArrayList<Studente> getStudenti() {
        return studenti;
    }

    public void setStudenti(ArrayList<Studente> studenti) {
        this.studenti = studenti;
    }

    public ArrayList<Esame> getEsami() {
        return esami;
    }

    public void setEsami(ArrayList<Esame> esami) {
        this.esami = esami;
    }

    /*
    Metodo per popolare le tabelle ESAMI e STUDENTI
     */
    public void populateModel(int numStudenti, int numEsami){
        for(int i = 0; i < numStudenti; i++){
            studenti.add(Studente.generateStudente());
        }

        Random random = new Random();
        for(int i = 0; i < numEsami; i++){
            Studente s = studenti.get(random.nextInt(numStudenti));
            esami.add(Esame.generateEsame(s.getMatricola()));
        }
    }
}
