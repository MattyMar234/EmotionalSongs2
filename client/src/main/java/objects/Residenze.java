package objects;

import java.io.Serializable;

public class Residenze implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private String viaPiazza;
    private int civicNumber;
    private String provinceName;
    private String councilName;
    private String cap;



    public String getID() {
        return ID;
    }


    public String getViaPiazza() {
        return viaPiazza;
    }


    public int getCivicNumber() {
        return civicNumber;
    }

    public String getProvinceName() {
        return provinceName;
    }


    public String getCouncilName() {
        return councilName;
    }

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
