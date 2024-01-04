package objects;

import java.io.Serializable;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

/**
 * La classe `Residenze` rappresenta le informazioni sulla residenza di un utente.
 */

public class Residenze implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private String viaPiazza;
    private int civicNumber;
    private String provinceName;
    private String councilName;
    private String cap;

    public Residenze(String ID, String viaPiazza, int civicNumber, String provinceName, String councilName) {
        this.ID = ID;
        this.viaPiazza = viaPiazza;
        this.civicNumber = civicNumber;
        this.provinceName = provinceName;
        this.councilName = councilName;
    }

    public Residenze(HashMap<Colonne, Object> table) 
    {
        this.ID = (String) table.get(Colonne.ID);
        this.viaPiazza = (String) table.get(Colonne.VIA_PIAZZA);
        this.civicNumber = (int) table.get(Colonne.CIVIC_NUMER);
        this.provinceName = (String) table.get(Colonne.PROVINCE_NAME);
        this.councilName = (String) table.get(Colonne.COUNCIL_NAME);    
        this.cap = (String) table.get(Colonne.CAP);
    }

    @Override
    public String toString() {
        return "Residenza{" +
                "ID='" + ID + '\'' +
                ", viaPiazza='" + viaPiazza + '\'' +
                ", civicNumber=" + civicNumber +
                ", provinceName='" + provinceName + '\'' +
                ", councilName='" + councilName + '\'' +
                '}';
    }

    

/**
 * Restituisce l'ID dell'oggetto Residenza.
 *
 * Questo metodo restituisce l'ID dell'oggetto Residenza, che è stato precedentemente impostato.
 *
 * @return L'ID dell'oggetto Residenza.
 */
    public String getID() {
        return ID;
    }



/**
 * Imposta l'ID dell'oggetto Residenza.
 *
 * Questo metodo imposta l'ID dell'oggetto Residenza utilizzando la stringa fornita.
 *
 * @param ID La nuova stringa da assegnare all'ID dell'oggetto Residenza.
 */
    public void setID(String ID) {
        this.ID = ID;
    }



/**
 * Restituisce la via o la piazza associata all'oggetto Residenza.
 *
 * Questo metodo restituisce la via o la piazza associata all'oggetto Residenza, che è stata precedentemente impostata.
 *
 * @return La via o la piazza associata all'oggetto Residenza.
 */
    public String getViaPiazza() {
        return viaPiazza;
    }



/**
 * Imposta la via o la piazza associata all'oggetto Residenza.
 *
 * Questo metodo imposta la via o la piazza associata all'oggetto Residenza utilizzando la stringa fornita.
 *
 * @param viaPiazza La nuova stringa da assegnare alla via o piazza dell'oggetto Residenza.
 */
    public void setViaPiazza(String viaPiazza) {
        this.viaPiazza = viaPiazza;
    }



/**
 * Restituisce il numero civico associato all'oggetto Residenza.
 *
 * Questo metodo restituisce il numero civico associato all'oggetto Residenza, che è stato precedentemente impostato.
 *
 * @return Il numero civico associato all'oggetto Residenza.
 */
    public int getCivicNumber() {
        return civicNumber;
    }



/**
 * Imposta il numero civico associato all'oggetto Residenza.
 *
 * Questo metodo imposta il numero civico associato all'oggetto Residenza utilizzando l'intero fornito.
 *
 * @param civicNumber Il nuovo numero civico da assegnare all'oggetto Residenza.
 */
    public void setCivicNumber(int civicNumber) {
        this.civicNumber = civicNumber;
    }



/**
 * Restituisce il nome della provincia associato all'oggetto Residenza.
 *
 * Questo metodo restituisce il nome della provincia associato all'oggetto Residenza, che è stato precedentemente impostato.
 *
 * @return Il nome della provincia associato all'oggetto Residenza.
 */
    public String getProvinceName() {
        return provinceName;
    }



/**
 * Imposta il nome della provincia associato all'oggetto Residenza.
 *
 * Questo metodo imposta il nome della provincia associato all'oggetto Residenza utilizzando la stringa fornita.
 *
 * @param provinceName Il nuovo nome della provincia da assegnare all'oggetto Residenza.
 */
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }



/**
 * Restituisce il nome del comune associato all'oggetto Residenza.
 *
 * Questo metodo restituisce il nome del comune associato all'oggetto Residenza, che è stato precedentemente impostato.
 *
 * @return Il nome del comune associato all'oggetto Residenza.
 */
    public String getCouncilName() {
        return councilName;
    }



/**
 * Imposta il nome del comune associato all'oggetto Residenza.
 *
 * Questo metodo imposta il nome del comune associato all'oggetto Residenza utilizzando la stringa fornita.
 *
 * @param councilName Il nuovo nome del comune da assegnare all'oggetto Residenza.
 */
    public void setCouncilName(String councilName) {
        this.councilName = councilName;
    }
    
}
