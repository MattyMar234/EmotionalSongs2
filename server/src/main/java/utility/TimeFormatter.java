package utility;

/**
 * Questa classe fornisce metodi statici per formattare e convertire il tempo da nanosecondi a diverse unità di misura.
 */
public class TimeFormatter {

    /**
     * Formatta il tempo in nanosecondi in una stringa leggibile, utilizzando diverse unità di misura.
     *
     * @param nanoSeconds Il tempo in nanosecondi da formattare.
     * @return Una stringa formattata con l'unità di misura appropriata.
     */
    public static String formatTime(double nanoSeconds) {
        if (nanoSeconds < 1e3) {
            return nanoSeconds + " ns";
        } else if (nanoSeconds < 1e6) {
            return (nanoSeconds / 1e3) + " us";//μ
        } else if (nanoSeconds < 1e9) {
            return (nanoSeconds / 1e6) + " ms";
        } else if (nanoSeconds < 6e10) {
            return (nanoSeconds / 1e9) + " s";
        } else if (nanoSeconds < 3.6e12) {
            return (nanoSeconds / 6e10) + " min";
        } else if (nanoSeconds < 8.64e13) {
            return (nanoSeconds / 3.6e12) + " hours";
        } else if (nanoSeconds < 2.592e14) {
            return (nanoSeconds / 8.64e13) + " days";
        } else if (nanoSeconds < 3.1536e15) {
            return (nanoSeconds / 2.592e14) + " weeks";
        } else {
            return (nanoSeconds / 3.1536e15) + " years";
        }
    }

    /**
     * Converte il tempo in nanosecondi in una stringa con l'unità di misura specificata.
     *
     * @param nanoSeconds Il tempo in nanosecondi da convertire.
     * @param unit        L'unità di misura desiderata ("ns", "μs", "ms", "s", "min", "hours", "days", "weeks", "years").
     * @return Una stringa formattata con il tempo convertito e l'unità specificata.
     * @throws IllegalArgumentException Se l'unità specificata non è valida.
     */
    public static String convertToMultiple(double nanoSeconds, String unit) {
        switch (unit.toLowerCase()) {
            case "ns":
                return nanoSeconds + " ns";
            case "μs":
                return (nanoSeconds / 1e3) + " us";
            case "ms":
                return (nanoSeconds / 1e6) + " ms";
            case "s":
                return (nanoSeconds / 1e9) + " s";
            case "min":
                return (nanoSeconds / 6e10) + " min";
            case "hours":
                return (nanoSeconds / 3.6e12) + " hours";
            case "days":
                return (nanoSeconds / 8.64e13) + " days";
            case "weeks":
                return (nanoSeconds / 2.592e14) + " weeks";
            case "years":
                return (nanoSeconds / 3.1536e15) + " years";
            default:
                throw new IllegalArgumentException("Invalid unit");
        }
    }
}
