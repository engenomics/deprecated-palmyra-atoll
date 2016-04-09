package org.engenomics.palmyra;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Utils {
    private static FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);

    /**
     * Reads a file into a string, with no line breaks.
     *
     * @param filepath - the path of the file
     * @return - the file string, with no line breaks
     * @throws IOException
     */
    public static String readFileIntoStringNoBreaks(String filepath) throws IOException {
        // Read in lines from file
        List<String> lines = Files.readAllLines(Paths.get(filepath), Charset.defaultCharset());

        // Join the lines into one string
        return StringUtils.join(lines, "");
    }

    /**
     * Converts positions of a nucleotide to a double[] array of zeroes or ones, representing whether the base pair is present there or not.
     *
     * @param list - the list of positions
     * @return - the array of doubles (0.0 or 1.0)
     */
    public static double[] intListOfPositionsToDoubleArrayOfPositionalIndicators(List<Integer> list) {
        // Just in case, sort the list
        Collections.sort(list);

        // Make the array of doubles have the number of positions needed
        double[] doubleArray = new double[list.get(list.size() - 1)];

        // Set the ith element of the doubles array to 0 or 1, depending on whether the nucleotide is present at that position or not
        for (int i = 0; i < doubleArray.length; i++) doubleArray[i] = list.contains(i) ? 1.0 : 0.0;

        // Return it
        return doubleArray;
    }

    public static int[] intListOfPositionsToIntArrayOfPositionalIndicators(List<Integer> list) {
        // Just in case, sort the list
        Collections.sort(list);

        // Make the array of doubles have the number of positions needed
        int[] intArray = new int[list.get(list.size() - 1)];

        // Set the ith element of the doubles array to 0 or 1, depending on whether the nucleotide is present at that position or not
        for (int i = 0; i < intArray.length; i++) intArray[i] = list.contains(i) ? 1 : 0;

        // Return it
        return intArray;
    }

    /**
     * Does a forward standard fourier transform on an array of doubles, padded at the end with zeroes to a length of 2^n
     *
     * @param values - an array of doubles of any size
     * @return - an array of complex numbers
     */
    public static Complex[] fourierTransform(double[] values) {
        return fastFourierTransformer.transform(correctNumber(values), TransformType.FORWARD);
    }

    /**
     * Takes a double[], and returns this double padded with zeroes so that its length is a power of two.
     *
     * @param values - the original array of doubles
     * @return - the padded array of doubles
     */
    public static double[] correctNumber(double[] values) {
        // If there are already 2^n elements
        if (isPowerOfTwo(values.length)) {
            return values;
        }

        // Otherwise, get the nearest power of two
        int goodLength = getNextPowerOfTwo(values.length);
        double[] goodArray = new double[goodLength];

        // Fill part of the new array with the values of the old array; then, fill the rest of the new array with zeroes
        System.arraycopy(values, 0, goodArray, 0, values.length);
        for (int i = values.length; i < goodArray.length; i++) {
            goodArray[i] = 0;
        }

        // Return the new array
        return goodArray;
    }


    // From http://stackoverflow.com/a/1322548/2930268
    public static int getNextPowerOfTwo(int n) {
        n--;
        n |= n >> 1;   // Divide by 2^k for consecutive doublings of k up to 32,
        n |= n >> 2;   // and then or the results.
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        n++;           // The result is a number of 1 bits equal to the number
        // of bits in the original number, plus 1. That's the
        // next highest power of 2.

        return n;
    }

    // From http://stackoverflow.com/a/600306/2930268
    public static boolean isPowerOfTwo(int n) {
        return (n != 0) && ((n & (n - 1)) == 0);
    }


    // Based on http://stackoverflow.com/a/36511661/3238231
    public static List<Rule> rulesFor(int[] positions) {
        List<Rule> rules = new ArrayList<>();

        Set<Integer> allOnes = new HashSet<>();
        for (int i = 0; i < positions.length; i++)
            if (positions[i] == 1) {
                allOnes.add(i);
            }
        // Size 1 has to be done separately as the below code wouldn't work.
        if (allOnes.size() == 1) {
            int a = allOnes.iterator().next();
            rules.add(new Rule(a, a, 1));
            return rules;
        }
        Set<Integer> leftToRemove = new HashSet<>(allOnes);
        while (!leftToRemove.isEmpty()) {
            int low = -1;
            int high = -1;
            int d = -1;
            int removeTotal = -1;
            for (int a : leftToRemove) {
                for (int b : allOnes) {
                    if (b == a) {
                        continue;
                    }
                    int d2 = Math.abs(b - a);
                    int low2 = a;
                    int high2 = a;
                    int removeTotal2 = 1;
                    while (true) {
                        if (!allOnes.contains(low2 - d2)) {
                            break;
                        }
                        low2 -= d2;
                        if (leftToRemove.contains(low2)) {
                            removeTotal2++;
                        }
                    }
                    while (true) {
                        if (!allOnes.contains(high2 + d2)) {
                            break;
                        }
                        high2 += d2;
                        if (leftToRemove.contains(high2)) {
                            removeTotal2++;
                        }
                    }
                    if (removeTotal2 > removeTotal) {
                        low = low2;
                        high = high2;
                        removeTotal = removeTotal2;
                        d = d2;
                    }
                }
            }
            rules.add(new Rule(low, high, d));
            System.out.println(leftToRemove.size() + " rules remaining.");
            for (int i = low; i <= high; i += d) {
                leftToRemove.remove(i);
            }
        }

        return rules;
    }

    public static void writeRulesToFile(List<List<Rule>> listsOfRules) throws IOException {
        PrintWriter writer = new PrintWriter("output.txt", Charset.defaultCharset().toString());
        for (List<Rule> ruleList : listsOfRules) {
            ruleList.forEach(writer::println);
            writer.println("End of rule.");
        }
        writer.close();
    }
}
