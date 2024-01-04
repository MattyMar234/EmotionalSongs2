package objects;

import java.io.Serializable;
import java.util.HashMap;

import database.PredefinedSQLCode.Colonne;


/**
 * La classe `Account` rappresenta un utente con le relative informazioni personali.
 */

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



/**
 * Restituisce l'oggetto Residenze associato all'account.
 *
 * Questo metodo restituisce l'oggetto Residenze associato all'account, che contiene
 * le informazioni sulla residenza dell'utente.
 *
 * @return L'oggetto Residenze associato all'account.
 */
    public Residenze getResidenza() {
        return residenza;
    }



/**
 * Restituisce il nome dell'utente.
 *
 * @return Il nome dell'utente.
 */
    public String getName() {
        return name;
    }



/**
 * Imposta il nome dell'utente.
 *
 * @param name Il nuovo nome da assegnare all'utente.
 */    
    public void setName(String name) {
        this.name = name;
    }



/**
 * Restituisce il soprannome dell'utente.
 *
 * @return Il soprannome dell'utente.
 */
    public String getNickname() {
        return nickname;
    }




/**
 * Imposta il soprannome dell'utente.
 *
 * @param nickname Il nuovo soprannome da assegnare all'utente.
 */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }



/**
 * Restituisce il cognome dell'utente.
 *
 * @return Il cognome dell'utente.
 */
    public String getSurname() {
        return surname;
    }



/**
 * Imposta il cognome dell'utente.
 *
 * @param surname Il nuovo cognome da assegnare all'utente.
 */
    public void setSurname(String surname) {
        this.surname = surname;
    }



/**
 * Restituisce il codice fiscale dell'utente.
 *
 * @return Il codice fiscale dell'utente.
 */
    public String getFiscalCode() {
        return fiscalCode;
    }



/**
 * Imposta il codice fiscale dell'utente.
 *
 * @param fiscalCode Il nuovo codice fiscale da assegnare all'utente.
 */
    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }



/**
 * Restituisce l'email dell'utente.
 *
 * @return L'email dell'utente.
 */
    public String getEmail() {
        return email;
    }



/**
 * Imposta l'email dell'utente.
 *
 * @param email La nuova email da assegnare all'utente.
 */
    public void setEmail(String email) {
        this.email = email;
    }



/**
 * Restituisce la password dell'utente.
 *
 * @return La password dell'utente.
 */
    public String getPassword() {
        return password;
    }



/**
 * Imposta la password dell'utente.
 *
 * @param password La nuova password da assegnare all'utente.
 */
    public void setPassword(String password) {
        this.password = password;
    }



/**
 * Restituisce l'ID della residenza dell'utente.
 *
 * @return L'ID della residenza dell'utente.
 */
    public String getResidenzaId() {
        return residenzaId;
    }



/**
 * Imposta l'ID della residenza dell'utente.
 *
 * @param residenzaId Il nuovo ID della residenza da assegnare all'utente.
 */
    public void setResidenzaId(String residenzaId) {
        this.residenzaId = residenzaId;
    }
}
