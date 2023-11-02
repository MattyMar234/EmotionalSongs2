package utility;

public class TimeFormatter {
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
