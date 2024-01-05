package objects;

import java.io.Serializable;

import application.Main;
import enumClasses.EmotionType;

/**
 * Questa classe rappresenta un'emozione.
 */
public class Emotion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String ID;
    private String emotionType;
    private int emotionValue;
    private String emotionDate;
    private String comment;

    private String ID_Song;
    private String ID_Account;


    @Override
    public String toString() {
        return "Emotion [ID=" + ID + ", emotionType=" + emotionType + ", emotionValue=" + emotionValue
            + ", emotionDate=" + emotionDate + ", comment=" + comment + ", ID_Song=" + ID_Song + ", ID_Account="
            + ID_Account + "]";
    }

    /**
     * Metodo che restituisce l'ID dell'emozione.
     * @return
     */
    public String getID() {
        return ID;
    }

    /**
     * Metodo che restituisce il tipo di emozione.
     * @return
     */
    public EmotionType getEmotionType() {
        return EmotionType.valueOf(this.emotionType);
    }

    /**
     * Metodo che restituisce il valore dell'emozione.
     * @return
     */
    public int getEmotionValue() {
        return emotionValue;
    }

    /**
     * Metodo che restituisce la data dell'emozione.
     * @return
     */
    public String getEmotionDate() {
        return emotionDate;
    }

    /**
     * Metodo che restituisce il commento dell'emozione.
     * @return
     */
    public String getComment() {
        return comment;
    }

    /**
     * Metodo che restituisce l'ID della canzone.
     * @return
     */
    public String getID_Song() {
        return ID_Song;
    }

    /**
     * Metodo che restituisce l'ID dell'account.
     * @return
     */
    public String getID_Account() {
        return ID_Account;
    } 
}
