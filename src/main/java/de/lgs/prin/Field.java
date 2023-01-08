package de.lgs.prin;

import java.util.ArrayList;
import java.util.Scanner;

public class Field {
    //Attribite
    private Fieldtype type;
    private int amount;
    private Playfield parent;


    //getter & Setter
    //Fieldtype type
    public Fieldtype gettype () {
        return this.type;
    }

    public void settype (Fieldtype type) {
        this.type = type;
    }

    //int amount
    public int getamount () {
        return this.amount;
    }

    public void setamount (int amount) {
        this.amount = amount;
    }


    //Constructor
    public Field(Fieldtype type, int amount, Playfield parent) {
        this.parent = parent;
        this.type = type;
        this.amount = amount;
    }
    private final static int finishMoney = 2000;

    //Methoden
    public void executeAction(Player player) {
        switch (this.type){
            case MONEYPRO:
                //System.out.println("test MONEYPRO");
                this.moneyPro(player);
                break;
            case MONEYCON:
                //System.out.println("test MONEYCON");
                this.moneyCon(player);
                break;
            case ACTION:
                //System.out.println("test ACTION");
                this.action(player);
                break;
            case SPLIT:
                this.split(player);
                //System.out.println("test SPLIT");
                break;
            case START:
                this.start(player);
                break;
            case GILDE:
                System.out.println("You now join the guild, which allows you to form a group for your advanture" + '\n' +
                        "A friendly Person joind your group");
                player.setGroupsize(2);
                break;
            case Finish:
                this.finish(player);
                break;
        }
    }
    private void action(Player player)
    {
        switch(this.amount%2)
        {
            case 0:
                actionMonster(player);
                break;
            case 1:
                actionMission(player);
                break;
            default:
                break;
        }
    }
    private void actionMonster(Player player)
    {
        int amount = (int)(250*Math.random());
        // TODO: LIST OF monster names
        String name = "Monster"; // choose random monster name
        System.out.printf("You encountered an %s and it started to attack you\n", name);
        // Monster
        double r = Math.random();
        boolean fail = r < 0.5;
        if(fail) {
            System.out.printf("You failed to fight against %s, you lost %d gold\n", name, amount);
            player.addMoney(-amount);
        } else
        {
            System.out.printf("You won your fight against %s and found %d gold in his stomach", name, amount);
            player.addMoney(amount);
        }
    }
    private void actionMission(Player player)
    {
        int amount = (int)(250*Math.random());
        // Mission
        // TODO: Mission names
        String name = "Mission";
        System.out.printf("Your adventure group accepts a request from the bulletin board for the quest '%s'\n", name);
        // Monster
        double r = Math.random();
        boolean fail = r < 0.5;
        if(fail) {
            System.out.println("You did not complete the quest in the required time");
            player.addMoney(-amount);
        } else
        {
            System.out.printf("The client hands over your payment of %d gold", amount);
            player.addMoney(amount);
        }
    }
    private void split(Player player)
    {
        if(this.amount == 1)
        {
            if(player.getPlayerClass().name().startsWith("Apprentice"))
            {
                System.out.println("you have now finished your training and will earn more gold than before");
                player.setMoneyScale(1.75);
            }
        } else if(this.amount == 2)
        {
            System.out.println("Does your heart beat for adventures or do you prefer to sit back as a guild leader?");
            System.out.println("(1) Adventures");
            System.out.println("(2) Guild Leader");
            int c = getInput(1,2);
            if(c == 1)
            {
                System.out.print(""); // for Kira
            }
            else
            {
                player.move(0.1);
            }
        }
        else
        {
            System.out.println("You start an Adventure with your group!");
        }
    }
    private void start(Player player)
    {
        System.out.println("Choose your profession or training");
        String profession;
        ArrayList<PlayerClass> pc = new ArrayList<>();
        for(int i = 0; i < PlayerClass.values().length; i++)
        {
            PlayerClass c = PlayerClass.values()[i];
            if(c.name().startsWith("Advanced")) continue;
            if(c.name().startsWith("Apprentice")) continue;
            if(c.name().equals("NONE")) continue;
            System.out.printf("(%d) %s\n", pc.size()+1, c.name());
            pc.add(c);
        }
        int choosenNumber = getInput(1, pc.size());
        profession = pc.get(choosenNumber-1).name();
        System.out.printf("You have chosen your vocation as %s for life!\n", profession);
        System.out.println("At what point in your career do you join the game?"); // choose to study or not
        System.out.printf("(1) I am at the end of my training as %s\n", profession); // be a student (option)
        System.out.printf("(2) I am already a trained %s\n", profession); // already finished (option)
        int choosen = getInput(1,2);
        if (choosen == 1) {
            profession = "Apprentice_" + profession;
            player.setMoneyScale(1);
        } else {
            profession = "Advanced_" + profession;
            player.setMoneyScale(1.5);
            player.move(0.1);
        }
        player.changeClass(PlayerClass.valueOf(profession));
    }
    private int getInput(int min, int max)
    {
        int n = 0;
        while(n<min || n > max)
        {
            System.out.printf("Please choose a number between 1 and %d\n", max);
            Scanner sc = new Scanner(System.in);
            try {
                n = Integer.parseInt(sc.nextLine());
            } catch(Exception ignored) {n = 0;}
        }
        return n;
    }
    private void moneyPro (Player player) {
        int _amount = (int)(this.amount*player.getMoneyScale());
        System.out.printf("You earned %d gold\n",_amount);
        player.addMoney(_amount);
    }
    private void moneyCon (Player player) {
        System.out.printf("You lost %d gold\n", this.amount);
        player.addMoney(-this.amount);
    }
    private void finish(Player player)
    {
        int amount = Field.finishMoney/GameController.getFinishedPlayers();
        player.addMoney(amount);
        System.out.println("You have reached mount celestia");
    }

}