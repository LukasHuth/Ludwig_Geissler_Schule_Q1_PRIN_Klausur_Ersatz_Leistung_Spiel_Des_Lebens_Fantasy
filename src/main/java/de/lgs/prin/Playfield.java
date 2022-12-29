package de.lgs.prin;

import java.util.HashMap;

public class Playfield {
    private HashMap<Double, Field> playfield;
    public Playfield()
    {
        this.playfield = new HashMap<>();
    }
    public double nextSplitfield(double position) {
        return 0.0;
    }
    public int fieldsTilEnd(double position) {
        return 0;
    }
    public void addField(Field field, double position) {}
    public void generateFields() {
        for(int  i = 0; i < 20; i++)
        {
            playfield.put((double)i, new Field(Fieldtype.NONE, i));
        }
    }
    public boolean isFinished(Player player) { return true; }
    public Field getField(double pos) {
        return this.playfield.get(pos);
    }
}