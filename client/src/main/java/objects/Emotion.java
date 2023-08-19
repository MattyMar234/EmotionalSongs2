package objects;

import application.EmotionalSongs;

public class Emotion {


    public enum EmotionType {

        AMAZEMENT, 
        SOLEMNITY, 
        TENDERNESS, 
        NOSTALGIA, 
        CALMNESS, 
        POWER,
        JOY,
        TENSION,  
        SADNESS;

        private EmotionType() {

        }

        public String getName() {
            switch (this) {
                case AMAZEMENT:
                    return (EmotionalSongs.applicationLanguage == 0 ? "Stupore" : "Amazement");

                case SOLEMNITY:
                    return (EmotionalSongs.applicationLanguage == 0 ? "Solennit\u00E0" : "Solemnity");
                    
                case TENDERNESS:
                    return (EmotionalSongs.applicationLanguage == 0 ? "Tenerezza" : "Tenderness");
                    
                case NOSTALGIA:
                    return (EmotionalSongs.applicationLanguage == 0 ? "Nostalgia" : "Nostalgia");
                    
                case CALMNESS:
                    return (EmotionalSongs.applicationLanguage == 0 ? "Calma" : "Calmness");
                    
                case POWER:
                    return (EmotionalSongs.applicationLanguage == 0 ? "Forza" : "Power");
                    
                case JOY:
                    return (EmotionalSongs.applicationLanguage == 0 ? "Gioia" : "Joy");
                    
                case TENSION:
                    return (EmotionalSongs.applicationLanguage == 0 ? "Tensione" : "Tension");
                    
                case SADNESS:
                    return (EmotionalSongs.applicationLanguage == 0 ? "Tristezza" : "Sadness");
                    
                default:
                    return "";
            }
        }
    }
    
}
