package me.ankhell.readabilityIndex;

public enum ReadabilityIndex {
    ARI("Automated Readability Index"),
    FK("Flesch–Kincaid readability tests"),
    SMOG("Simple Measure of Gobbledygook"),
    CL("Coleman–Liau index");

    private String outputName;

    ReadabilityIndex(String outputName) {
        this.outputName = outputName;
    }

    public String getOutputName() {
        return outputName;
    }
}
