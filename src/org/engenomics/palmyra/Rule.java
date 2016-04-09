package org.engenomics.palmyra;

public class Rule {
    private int start;
    private int end;
    private int step;

    public Rule(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public int getStart() {

        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return "" +
                "" + start +
                "\t" + end +
                "\t" + step +
                "";
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
