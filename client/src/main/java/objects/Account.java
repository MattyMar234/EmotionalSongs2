package objects;

import java.io.Serializable;

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
     * Restituisce la password dell'utente.
     *
     * @return La password dell'utente.
     */
    public String getPassword() {
        return password;
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
     * Restituisce il soprannome dell'utente.
     *
     * @return Il soprannome dell'utente.
     */
    public String getNickname() {
        return nickname;
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
     * Restituisce il codice fiscale dell'utente.
     *
     * @return Il codice fiscale dell'utente.
     */
    public String getFiscalCode() {
        return fiscalCode;
    }

    /**
     * Restituisce l'email dell'utente.
     *
     * @return L'email dell'utente.
     */
    public String getEmail() {
        return email;
    }

    /*public String getPassword() {
        return password;
    }*/


    /**
 * Restituisce l'ID della residenza dell'utente.
 *
 * @return L'ID della residenza dell'utente.
 */
    public String getResidenzaId() {
        return residenzaId;
    }

}
