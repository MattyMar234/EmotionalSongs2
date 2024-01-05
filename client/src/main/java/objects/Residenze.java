package objects;

import java.io.Serializable;

/**
 * Questa classe viene utilizzat per rappresentare le informazioni di una residenza
 */
public class Residenze implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private String viaPiazza;
    private int civicNumber;
    private String provinceName;
    private String councilName;
    private String cap;


    /**
     * Restituisce l'ID della residenza.
     * @return
     */
    public String getID() {
        return ID;
    }

    /**
     * Restituisce la via o piazza della residenza.
     * @return
     */
    public String getViaPiazza() {
        return viaPiazza;
    }

    /**
     * Restituisce il numero civico della residenza.
     * @return
     */
    public int getCivicNumber() {
        return civicNumber;
    }

    /**
     * restituisce il nome della provincia della residenza.
     * @return
     */
    public String getProvinceName() {
        return provinceName;
    }

    /**
     * Resituisce il nome del comune della residenza.
     * @return
     */
    public String getCouncilName() {
        return councilName;
    }

    /**
     * Ritoena il cap
     * @return
     */
    public String getCAP() {
        return cap;
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
    
}
