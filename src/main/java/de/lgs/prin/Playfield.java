package de.lgs.prin;


import java.util.HashMap;
import java.util.Random;
public class Playfield {
    private HashMap<Double, Field> playfield;

    public Playfield (){
        this.playfield=new HashMap<>();
    }
    public void setPlayfield(HashMap<Double, Field> playfield)
    {
        this.playfield = playfield;
    }
    public Field getField(double pos)
    {
        return this.playfield.get(pos);
    }
    public void generateMap(){
        playfield.put(0.0, new Field(Fieldtype.START, 0, this));
        for (double i=1;i<=50;i++){
            if(i<=8){
                playfield.put(i,new Field(randField(), rand(300), this));
                playfield.put(i+0.1,new Field(randField(), rand(400), this));
            } else if (i==9) {
                playfield.put(i,new Field(Fieldtype.SPLIT, 1, this));
            } else if (i<=18) {
                playfield.put(i,new Field(randField(), rand(700), this));
            } else if (i==19) {
                playfield.put(i,new Field(Fieldtype.GILDE, 0, this));
            } else if (i<=23) {
                playfield.put(i,new Field(randField(), rand(700), this));
            } else if (i==24) {
                playfield.put(i,new Field(Fieldtype.SPLIT, 2, this)); // weiterbilden oder gilde
            } else if (i<=36) {
                playfield.put(i,new Field(randField(), rand(1000), this));
                playfield.put(i+0.1,new Field(randField(), rand(1000), this));
            } else if (i==41) {
                playfield.put(i, new Field(Fieldtype.SPLIT, 3, this));
            } else if (i<50) {
                playfield.put(i,new Field(randField(), rand(1200), this));
            } else {
                playfield.put(i, new Field(Fieldtype.Finish, 0, this));
            }
        }
    }
    private int rand(int max)
    {
        return (int)(Math.random()*max);
    }
    private static int counterACTION=40;
    private static int counterMONEY=40;
    private static int counterKID=4;
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
                if (counterMONEY >= 0) {
                    break;
                }
            }
            if (Fieldtype.values()[pick] == Fieldtype.KID) {
                counterKID--;
                if (counterKID >= 0) {
                    break;
                }
            }
        }
        return Fieldtype.values()[pick];
    }
    public double nextPause(double position) {
        position=Math.floor(position);
        if (position<9){
            return 9.0-position;
        } else if (position<24) {
            return 24.0-position;
        } else if (position<41) {
            return 41.0-position;
        }else {
            return 50-position;
        }
    }
    public double fieldsTilEnd(double position) {
        return 50-position;
    }
    public void addField(Field field, double position) {
        playfield.put(position,field);
    }
    public boolean isFished(Player player){
        return player.getPosition()>=50;
    }
    public boolean isFinished(Player p)
    {
        return isFished(p);
    }
}