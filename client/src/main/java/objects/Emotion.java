package objects;

import java.io.Serializable;

import application.Main;
import enumClasses.EmotionType;

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


    public String getID() {
        return ID;
    }

    public EmotionType getEmotionType() {
        return EmotionType.valueOf(this.emotionType);
    }

    public int getEmotionValue() {
        return emotionValue;
    }

    public String getEmotionDate() {
        return emotionDate;
    }

    public String getComment() {
        return comment;
    }

    public String getID_Song() {
        return ID_Song;
    }

    public String getID_Account() {
        return ID_Account;
    } 
}
