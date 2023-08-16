package objects;

import java.io.Serializable;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;

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

    public Account() {

    }

    public Account(String name, String nickname, String surname, String fiscalCode, String email, String password, String residenzaId, Residenze residenze) {
        this.name = name;
        this.nickname = nickname;
        this.surname = surname;
        this.fiscalCode = fiscalCode;
        this.email = email;
        this.password = password;
        this.residenzaId = residenzaId;
        this.residenza = residenze;
    }


    public Account(HashMap<Colonne, Object> table, Residenze residenze) 
    {
        this.name = (String) table.get(Colonne.NAME);
        this.nickname = (String) table.get(Colonne.NICKNAME);
        this.surname = (String) table.get(Colonne.SURNAME);
        this.fiscalCode = (String) table.get(Colonne.FISCAL_CODE);
        this.email = (String) table.get(Colonne.EMAIL);
        this.password = (String) table.get(Colonne.PASSWORD);
        this.residenzaId = (String) table.get(Colonne.RESIDENCE_ID_REF);
        this.residenza = residenze;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", surname='" + surname + '\'' +
                ", fiscalCode='" + fiscalCode + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", residenze='" + residenzaId + '\'' +
                "}\n" + residenza.toString();
    }

    // Getters and setters for all fields
    public Residenze getResidenza() {
        return residenza;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResidenzaId() {
        return residenzaId;
    }

    public void setResidenzaId(String residenzaId) {
        this.residenzaId = residenzaId;
    }
}
