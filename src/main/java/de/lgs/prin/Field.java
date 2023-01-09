package de.lgs.prin;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Field {
    //Attribite
    private Fieldtype type;
    private int amount;


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
    public Field(Fieldtype type, int amount) {
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
                System.out.println(Utils.fSpecial.format("You now join the guild, which allows you to form a group for your advanture" + '\n' +
                        "A friendly Person joind your group"));
                player.setGroupsize(2);
                break;
            case Finish:
                this.finish(player);
                break;
            case KID:
                this.kid(player);
                break;
        }
    }
    private void kid(Player player)
    {
        int count = player.getGroupsize();
        count = Math.min(6, count+1);
        System.out.println(Utils.fSpecial.format("You found a kid, who wants to join your group. You now have " + count + " people in your group"));
        player.setGroupsize(count);
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
    private String getRandom(String[] arr)
    {
        return arr[(new Random()).nextInt(arr.length)];
    }
    private void actionMonster(Player player)
    {
        int amount = (int)((this.amount)*Math.max(Math.random(), 0.25));
        String name = this.getRandom(Utils.monsters);
        // TODO: format a an
        System.out.printf(Utils.fNormal.format("You encountered an %s and it started to attack you\n"), name);
        // Monster
        double r = Math.random();
        boolean fail = r > 0.5*this.exp(1.25, player.getGroupsize()-1);
        if(fail) {
            System.out.printf(Utils.fNormal.format("You failed to fight against the %s, you lost %s gold\n"), name, Utils.fGold.format(amount+""));
            player.addMoney(-amount);
        } else
        {
            amount*=1+((player.getMoneyScale()-1)/2);
            System.out.printf(Utils.fNormal.format("You won your fight against the %s and found %s gold in his stomach\n"), name, Utils.fGold.format(amount+""));
            player.addMoney(amount);
        }
    }
    private void actionMission(Player player)
    {
        int amount = (int)((this.amount)*Math.max(Math.random(), 0.25));
        // Mission
        String name = this.getRandom(Utils.missions);
        System.out.printf(Utils.fNormal.format("Your adventure group accepts a request from the bulletin board for the quest '%s'\n"), name);
        // Monster
        double r = Math.random();
        boolean fail = r > 0.5*this.exp(1.25, player.getGroupsize()-1);
        if(fail) {
            System.out.println(Utils.fNormal.format("You did not complete the quest in the required time"));
            player.addMoney(-amount);
        } else
        {
            amount*=1+((player.getMoneyScale()-1)/2);
            System.out.printf(Utils.fNormal.format("The client hands over your payment of %s gold\n"), Utils.fGold.format(amount+""));
            player.addMoney(amount);
        }
    }
    private double exp(double x, int i)
    {
        if(i == 0) return 1;
        double res = x;
        for(;i>0;i--) res*=x;
        return res;
    }
    private void split(Player player)
    {
        if(this.amount == 1)
        {
            if(player.getPlayerClass().name().startsWith("Apprentice"))
            {
                System.out.println(Utils.fSpecial.format("you have now finished your training and will earn more gold than before"));
                player.setMoneyScale(1.75);
                player.setPlayerClass(PlayerClass.valueOf(player.getPlayerClass().name().replace("Apprentice", "Advanced")));
            } else {
                System.out.print(Utils.fSpecial.format("You find "));
                System.out.printf(Utils.fGold.format("%d"), 250);
                System.out.println(Utils.fSpecial.format(" Gold on the street.\n"));
                player.addMoney(250);
            }
        } else if(this.amount == 2)
        {
            System.out.println(Utils.fSpecial.format("Does your heart beat for adventures or do you prefer to sit back as a guild leader?"));
            System.out.println(Utils.fNormal.format("(1) Adventures"));
            System.out.println(Utils.fNormal.format("(2) Guild Leader"));
            int c = getInput(1,2);
            if(c == 1)
            {
                System.out.print(Utils.fSpecial.format("In the tavern, warriors tell of monsters gone wild, your party takes it upon themselves to kill them\n"));
                String playerclass = player.getPlayerClass().name();
                playerclass = playerclass.replace("Advanced_", "");
                playerclass = "Apprentice_"+playerclass;
                player.setPlayerClass(PlayerClass.valueOf(playerclass));
            }
            else
            {
                player.move(0.1);
                int newGroupSize = Math.min(6, player.getGroupsize()*2);
                System.out.printf(Utils.fSpecial.format("After a long journey, the old guild leader passes away and you take his place. \n" +
                        "Your inner circle is now joined by (%d) guild members who will assist you in leadership.\n"), newGroupSize-player.getGroupsize());
                player.setGroupsize(newGroupSize);
            }
        }
        else
        {
            System.out.println(Utils.fSpecial.format("You start an Adventure with your group!"));
            if(player.getPlayerClass().name().startsWith("Apprentice"))
            {
                player.setPlayerClass(PlayerClass.valueOf(player.getPlayerClass().name().replace("Apprentice", "Advanced")));
                player.setMoneyScale(player.getMoneyScale()*1.25);
            }
        }
    }
    private void start(Player player)
    {
        System.out.println(Utils.fNormal.format("Choose your profession or training"));
        String profession;
        ArrayList<PlayerClass> pc = new ArrayList<>();
        for(int i = 0; i < PlayerClass.values().length; i++)
        {
            PlayerClass c = PlayerClass.values()[i];
            if(c.name().startsWith("Advanced")) continue;
            if(c.name().startsWith("Apprentice")) continue;
            if(c.name().equals("NONE")) continue;
            System.out.printf(Utils.fNormal.format("(%d) %s\n"), pc.size()+1, c.name());
            pc.add(c);
        }
        int choosenNumber = getInput(1, pc.size());
        profession = pc.get(choosenNumber-1).name();
        System.out.printf(Utils.fNormal.format("You have chosen your vocation as %s for life!\n"), profession);
        System.out.println(Utils.fNormal.format("At what point in your career do you join the game?")); // choose to study or not
        System.out.printf(Utils.fNormal.format("(1) I am at the end of my training as %s\n"), profession); // be a student (option)
        System.out.printf(Utils.fNormal.format("(2) I am already a trained %s\n"), profession); // already finished (option)
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
            System.out.printf(Utils.fError.format("Please choose a number between 1 and %d\n"), max);
            Scanner sc = new Scanner(System.in);
            try {
                n = Integer.parseInt(sc.nextLine());
            } catch(Exception ignored) {n = 0;}
        }
        return n;
    }
    private void moneyPro (Player player) {
        int _amount = (int)(this.amount*player.getMoneyScale());
        System.out.printf(Utils.fNormal.format("You earned %s gold\n"), Utils.fGold.format(_amount+""));
        player.addMoney(_amount);
    }
    private void moneyCon (Player player) {
        System.out.printf(Utils.fNormal.format("You lost %s gold\n"), Utils.fGold.format(this.amount+""));
        player.addMoney(-this.amount);
    }
    private void finish(Player player)
    {
        int amount = Field.finishMoney/player.getGameController().getPlayersFinished();
        player.addMoney(amount);
        System.out.println(Utils.fNormal.format("You have reached mount celestia"));
    }

}