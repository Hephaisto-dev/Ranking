package fr.hephaisto.ranking.utils;

public class NumberUtils {
    /***
     * Round a double to 1 decimal
     * @param value the double to floor
     * @return the rounded double (format 0.0)
     */
    public static double floor(double value) {
        return Math.floor(value * 10.0) / 10.0;
    }
}
