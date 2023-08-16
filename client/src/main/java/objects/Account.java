package objects;

import java.io.Serializable;

public class Account implements Serializable{
    private static final long serialVersionUID = 1L;

    private String name;
    private String nickname;
    private String surname;
    private String fiscalCode;
    private String email;
    private String password;
    private String residenzaId;
    private Residenze residenza;

    public Residenze getResidenza() {
        return residenza;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSurname() {
        return surname;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public String getEmail() {
        return email;
    }

    /*public String getPassword() {
        return password;
    }*/


    public String getResidenzaId() {
        return residenzaId;
    }

}
