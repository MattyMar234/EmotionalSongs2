package datamodel;

import com.googlecode.jeneratedata.dates.DateGenerator;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Esame {
    /*
    Variabili attributi tabella ESAMI
     */
    private int codiceCorso;
    private int studente;
    private Date data;
    private int voto;

    /*
    Costruttore
     */
    public Esame(int codiceCorso, int studente, Date data, int voto) {
        this.codiceCorso = codiceCorso;
        this.studente = studente;
        this.data = data;
        this.voto = voto;
    }

    public static Esame generateEsame(int studente){
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(Calendar.MONTH, Calendar.JANUARY);
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.YEAR, 2020);

        Calendar end = Calendar.getInstance();
        end.clear();
        end.set(Calendar.MONTH, Calendar.DECEMBER);
        end.set(Calendar.DAY_OF_MONTH, 31);
        end.set(Calendar.YEAR, 2022);

        DateGenerator dateGenerator = new DateGenerator(start, end);

        Random random = new Random();

        int codiceCorso = random.nextInt(20);
        Date data = dateGenerator.generate();
        int voto = random.nextInt(25) + 5;

        return new Esame(codiceCorso, studente, data, voto);
    }


    public int getCodiceCorso() {
        return codiceCorso;
    }

    public int getStudente() {
        return studente;
    }

    public Date getData() {
        return data;
    }

    public int getVoto() {
        return voto;
    }
}
