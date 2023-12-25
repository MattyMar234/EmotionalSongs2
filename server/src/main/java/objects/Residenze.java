package objects;

import java.io.Serializable;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

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

    // Getters and setters for all fields

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getViaPiazza() {
        return viaPiazza;
    }

    public void setViaPiazza(String viaPiazza) {
        this.viaPiazza = viaPiazza;
    }

    public int getCivicNumber() {
        return civicNumber;
    }

    public void setCivicNumber(int civicNumber) {
        this.civicNumber = civicNumber;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCouncilName() {
        return councilName;
    }

    public void setCouncilName(String councilName) {
        this.councilName = councilName;
    }
    
}
