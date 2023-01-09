package de.lgs.prin;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
public class Playfield {
    private HashMap<Double, Field> playfield;
    public HashMap<Double, Field> getPlayfield() { return this.playfield; }
    public Playfield (){
        this.counterACTION = 40;
        this.counterKID = 4;
        this.counterMONEY= 40;
        this.guildFieldGenerated = false;
        this.playfield=new HashMap<>();
    }
    public void setPlayfield(HashMap<Double, Field> playfield)
    {
        this.playfield = playfield;
    }
    private int counterACTION;
    private int counterMONEY;
    private int counterKID;
    private boolean guildFieldGenerated;
    private static int fieldSize = 50;
    public Field getField(double pos)
    {
        //System.out.println(pos);
        return this.playfield.get(pos);
    }
    public void generateMap(){
        playfield.put(0.0, new Field(Fieldtype.START, 0));
        for (double i=1;i<=fieldSize;i++){
            if(i<=8){
                playfield.put(i,new Field(randField(), rand(300)));
                playfield.put(i+0.1,new Field(randField(), rand(400)));
            } else if (i==9) {
                playfield.put(i,new Field(Fieldtype.SPLIT, 1));
            } else if (i<=18) {
                playfield.put(i,new Field(randField(), rand(700)));
            } else if (i==19) {
                playfield.put(i,new Field(Fieldtype.GILDE, 0));
                this.guildFieldGenerated = true;
            } else if (i<=23) {
                playfield.put(i,new Field(randField(), rand(700)));
            } else if (i==24) {
                playfield.put(i,new Field(Fieldtype.SPLIT, 2)); // weiterbilden oder gilde
            } else if (i<41) {
                playfield.put(i,new Field(randField(), rand(1200)));
                playfield.put(i+0.1,new Field(randField(), rand(1000)));
            } else if (i==41) {
                playfield.put(i, new Field(Fieldtype.SPLIT, 3));
            } else if (i<fieldSize) {
                playfield.put(i,new Field(randField(), rand(1200)));
            } else {
                playfield.put(i, new Field(Fieldtype.Finish, 0));
            }
        }
    }
    private int rand(int max)
    {
        return (int)(Math.random()*max);
    }
    public Fieldtype randField() {
        int pick;
        while (true) {
            pick = new Random().nextInt(Fieldtype.values().length);
            if (Fieldtype.values()[pick] == Fieldtype.ACTION) {
                counterACTION--;
                if (counterACTION >= 0) {
                    break;
                }
            }
            if (Fieldtype.values()[pick] == Fieldtype.MONEYCON || Fieldtype.values()[pick] == Fieldtype.MONEYPRO) {
                counterMONEY--;
                double r = Math.random();
                if(r < 0.75) pick = Arrays.asList(Fieldtype.values()).indexOf(Fieldtype.MONEYPRO);
                else pick = Arrays.asList(Fieldtype.values()).indexOf(Fieldtype.MONEYCON);
                if (counterMONEY >= 0) {
                    break;
                }
            }
            if (Fieldtype.values()[pick] == Fieldtype.KID && guildFieldGenerated) {
                counterKID--;
                if (counterKID >= 0) {
                    break;
                }
            }
        }
        return Fieldtype.values()[pick];
    }
    public double nextPause(double position) {
        //position=Math.floor(position);
        if (position<9.0){
            return 9.0-position;
        } else if (position<24.0) {
            return 24.0-position;
        } else if (position<41.0) {
            return 41.0-position;
        }else {
            return fieldSize-position;
        }
    }
    public double fieldsTilEnd(double position) {
        return fieldSize-position;
    }
    public void addField(Field field, double position) {
        playfield.put(position,field);
    }
    public boolean isFished(Player player){
        return player.getPosition()>=fieldSize;
    }
    public boolean isFinished(Player p)
    {
        return isFished(p);
    }
}