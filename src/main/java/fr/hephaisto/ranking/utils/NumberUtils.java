package fr.hephaisto.ranking.utils;

public class NumberUtils {
    /***
     * Round a double to 2 decimal
     * @param value the double to round
     * @return the rounded double (format 0.00)
     */
    public static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
