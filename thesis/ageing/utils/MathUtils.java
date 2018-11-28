package thesis.ageing.utils;

import java.math.BigInteger;

/**
 * This class provides math utility to be used across the entire project.
 *
 * @author pablonsilva
 * @version 20151203
 */
public class MathUtils {

    public static BigInteger factorial(int n) {
        if (n == 0) {
            return new BigInteger("1");
        }
        return new BigInteger((n * factorial(n - 1).intValue()) + "");
    }

    public static double min(double a1, double a2)
    {
        if( a1 < a2)
            return a1;
        else
            return a2;
    }

    public static double max(double a1, double a2)
    {
        if( a1 > a2)
            return a1;
        else
            return a2;
    }

    /**
     * Returns the average of an array of doubles.
     *
     * @param an array of doubles
     * @return the average
     */
    public static double average(double[] values) {
        double v = 0;
        double h = values.length;

        for (int i = 0; i < values.length; i++)
            if(!Double.isNaN(values[i]))
                v += values[i];
            else
                h--;

        return v / h;
    }

    /**
     * Returns the average of an array of integers.
     *
     * @param an array of integer
     * @return the average
     */
    public static double average(int[] values) {
        double v = 0;

        for (int i = 0; i < values.length; i++)
            v += values[i];

        return v / (double) values.length;
    }

    public static double average(long[] values) {
        double v = 0;

        for (int i = 0; i < values.length; i++)
            v += values[i];

        return v / (double) values.length;
    }

    /**
     * Returns the standard deviation(sd) of an array of integers.
     *
     * @param an array of integers
     * @return the sd
     */
    public static double sd(int[] values) {
        double v = 0;
        double m = average(values);
        double h = values.length;

        for (int i = 0; i < values.length; i++)
            if(!Double.isNaN(values[i]))
                v += Math.pow((values[i] - m), 2);
            else
                h--;

        v = v / h;
        v = Math.sqrt(v);

        return v;
    }

    /**
     * Returns the standard deviation(sd) of an array of doubles.
     *
     * @param an array of doubles
     * @return the average
     */
    public static double sd(double[] values) {
        double v = 0;
        double m = average(values);
        double h = values.length;

        for (int i = 0; i < values.length; i++)
            if(!Double.isNaN(values[i]))
                v += Math.pow((values[i] - m), 2);
            else
                h--;

        v = v / h;
        v = Math.sqrt(v);

        return v;
    }

    /**
     * Returns the standard error (se) of an array of doubles.
     *
     * @param an array of doubles
     * @return the se
     */
    public static double se(double[] values) {
        double se = sd(values);

        double s = Math.sqrt(values.length);

        return se / s;
    }

    /**
     * Returns the standard error (se) of an array of integers.
     *
     * @param an array of integers
     * @return the se
     */
    public static double se(int[] values) {
        double se = sd(values);

        double s = Math.sqrt(values.length);

        return se / s;
    }

    public static double max(double[] values) {
        double max = 0;

        for (int i = 0; i < values.length; i++) {
            if (values[i] > max)
                max = values[i];
        }

        return max;
    }

    public static double min(double[] values) {
        double min = values[0];

        for (int i = 1; i < values.length; i++) {
            if (values[i] < min)
                min = values[i];
        }

        return min;
    }

    public static double sum(double[] values)
    {
        double v = 0;

        for(int i = 0 ; i < values.length;i++)
        {
            v+= values[i];
        }
        return v;
    }
}
