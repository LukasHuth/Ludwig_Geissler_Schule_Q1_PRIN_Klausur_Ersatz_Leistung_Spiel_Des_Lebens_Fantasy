package de.lgs.prin;

import java.util.HashMap;

public class Playfield {
    private HashMap<Double, Field> playfield;
    public double nextSplitfield(double position) {
        return 0.0;
    }
    public int fieldsTilEnd(double position) {
        return 0;
    }
    public void addField(Field field, double position) {}
    public void generateFields() {}
    public boolean isFinished(Player player) { return true; }
    public Field getField(double pos) {
        return this.playfield.get(pos);
    }
}