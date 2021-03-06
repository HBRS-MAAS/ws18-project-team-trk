package org.team_trk.objects;

public class Batch {
    private int breadsPerOven;

    public Batch(int breadsPerOven) {
        super();
        this.breadsPerOven = breadsPerOven;
    }

    private int getBreadsPerOven() {
        return breadsPerOven;
    }

    private void setBreadsPerOven(int breadsPerOven) {
        this.breadsPerOven = breadsPerOven;
    }

    @Override
    public String toString() {
        return "Batch [breadsPerOven=" + breadsPerOven + "]";
    }
}
