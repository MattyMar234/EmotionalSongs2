package datamodel;

import com.googlecode.jeneratedata.people.FemaleNameGenerator;
import com.googlecode.jeneratedata.people.LastNameGenerator;
import com.googlecode.jeneratedata.people.MaleNameGenerator;

import java.util.Random;

public class Studente {
    /*
    Attributi della tabella STUDENTI
     */
    private int matricola;
    private String cognome;
    private String nome;
    private int eta;

    /*
    Costruttore
     */
    public Studente(int m, String c, String n, int e){
        matricola = m;
        cognome = c;
        nome = n;
        eta = e;
    }

    public int getMatricola() {
        return matricola;
    }

    public String getCognome() {
        return cognome;
    }

    public String getNome() {
        return nome;
    }

    public int getEta() {
        return eta;
    }

    public static Studente generateStudente(){
        Random random = new Random();

        // creazione randomica campi numerici
        int matricola = random.nextInt(99999);
        int eta = random.nextInt(30) + 19;

        // creazione randomica campi string
        LastNameGenerator lastNameGenerator = new LastNameGenerator();
        String cognome = lastNameGenerator.generate();

        FemaleNameGenerator femaleNameGenerator = new FemaleNameGenerator();
        MaleNameGenerator maleNameGenerator = new MaleNameGenerator();
        String nome;
        if (random.nextInt(100) > 50)
            nome = maleNameGenerator.generate();
        else
            nome = femaleNameGenerator.generate();

        return new Studente(matricola, cognome, nome, eta);
    }

}
