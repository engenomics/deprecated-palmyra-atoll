package org.engenomics.palmyra;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    private String value;

    public Chunk() {
        this.value = "";
    }

    public Chunk(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLength() {
        return this.value.length();
    }

    public List<Integer> getPositionsOf(char base) {
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i < this.value.length(); i++) {
            if (this.value.charAt(i) == base) {
                positions.add(i);
            }
        }

        return positions;
    }
}
