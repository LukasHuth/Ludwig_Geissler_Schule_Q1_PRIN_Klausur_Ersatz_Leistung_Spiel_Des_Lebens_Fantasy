package de.lgs.prin;

public class Player {
    //Attribute
    private String name;
    private PlayerClass playerClass; //Jop (money)
    private double position;    //Field position
    private int money;
    private int groupsize;
    private GameController gameController;


    //Getter & Setter
    //string Name
    public String getName () {
        return this.name;
    }

    public void setName (String name) {
        this.name = name;
    }

    //Playerclass playerclass
    public PlayerClass getPlayerClass() {
        return this.playerClass;
    }

    public void setPlayerClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    //double position
    public double getPosition () {
        return this.position;
    }

    public void setPosition (double position) {
        this.position = position;
    }

    //int money
    public int getMoney () {
        return this.money;
    }

    public void setMoney (int money) {
        this.money = money;
    }

    //int groupsize
    public int getGroupsize () {
        return this.groupsize;
    }

    public void setGroupsize (int groupsize) {
        this.groupsize = groupsize;
    }

    //Gamecontroller gameController
    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }


    //constructor
    public Player () {
    }

    public Player (String name, GameController gameController) {
        this.name = name;
        this.gameController = gameController;
        this.groupsize = 1;
        this.playerClass = PlayerClass.NONE;
    }
    //Methoden
    public void move(int steps) {
        double n = this.gameController.getPlayfield().nextSplitfield(this.position);
        if (n < steps) {
            this.position += n;
            return;
        }
        this.position += steps;
    }

    public void addMoney (int money) {
        this.money += money;
    }
    public void changeClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

}