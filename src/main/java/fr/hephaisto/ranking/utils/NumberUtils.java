package fr.hephaisto.ranking.utils;

public class NumberUtils {
    /***
     * Round a double to 2 decimal
     * @param value the double to floor
     * @return the rounded double (format 0.00)
     */
    public static double floor(double value) {
        return Math.floor(value * 100.0) / 100.0;
    }
}
