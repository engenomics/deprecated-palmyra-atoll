package org.engenomics.palmyra;

public class Config {
    // Current directory
    public static String currentDir;

    // Data directory
    public static String chunkBaseFile;

    protected static void configure() { // TODO: Read in editable settings from config file
        currentDir = System.getProperty("user.dir");

        chunkBaseFile = "C:\\Users\\Andrew\\workspace\\PRIMES\\data\\genomes\\chr22\\chunk";
    }
}
