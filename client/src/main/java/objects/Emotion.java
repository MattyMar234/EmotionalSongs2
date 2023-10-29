package objects;

import application.Main;

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
                    return (Main.applicationLanguage == 0 ? "Stupore" : "Amazement");

                case SOLEMNITY:
                    return (Main.applicationLanguage == 0 ? "Solennit\u00E0" : "Solemnity");
                    
                case TENDERNESS:
                    return (Main.applicationLanguage == 0 ? "Tenerezza" : "Tenderness");
                    
                case NOSTALGIA:
                    return (Main.applicationLanguage == 0 ? "Nostalgia" : "Nostalgia");
                    
                case CALMNESS:
                    return (Main.applicationLanguage == 0 ? "Calma" : "Calmness");
                    
                case POWER:
                    return (Main.applicationLanguage == 0 ? "Forza" : "Power");
                    
                case JOY:
                    return (Main.applicationLanguage == 0 ? "Gioia" : "Joy");
                    
                case TENSION:
                    return (Main.applicationLanguage == 0 ? "Tensione" : "Tension");
                    
                case SADNESS:
                    return (Main.applicationLanguage == 0 ? "Tristezza" : "Sadness");
                    
                default:
                    return "";
            }
        }
    }
    
}
