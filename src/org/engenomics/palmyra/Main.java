package org.engenomics.palmyra;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(Charset.defaultCharset().toString());

        Config.configure();

        new Main().run();
    }

    private void run() throws IOException {
        // Get the value of the chunk from the chunk file
        String chunkValue = Utils.readFileIntoStringNoBreaks(Config.chunkBaseFile + "3.pac");

        // Create a new Chunk with this chunk string
        Chunk chunk = new Chunk(chunkValue);

        // Create an array of base pairs to analyse
        char[] nucleotides = new char[]{'A', 'C', 'T', 'G'};

        List<List<Rule>> listOfRulesLists = new ArrayList<>();

        for (char nucleotide : nucleotides) {
            List<Integer> positionsOfAList = chunk.getPositionsOf(nucleotide);

            int[] positionsOfA = Utils.intListOfPositionsToIntArrayOfPositionalIndicators(positionsOfAList);

            List<Rule> rules = Utils.rulesFor(positionsOfA);

            listOfRulesLists.add(rules);

            System.out.println("Done with " + nucleotide + ".");
        }

        Utils.writeRulesToFile(listOfRulesLists);
    }
}
