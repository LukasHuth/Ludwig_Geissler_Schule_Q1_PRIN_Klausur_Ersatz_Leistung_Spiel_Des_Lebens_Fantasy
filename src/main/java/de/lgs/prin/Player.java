package de.lgs.prin;

public class Player {
    private String name;
    private PlayerClass playerClass;
    private double position;
    private int money;
    private GameController gameController;
    public void move(int steps) {}
    public void addMoney(int money) {}
    public void changeClass(PlayerClass playerClass) {}
    public Player(String name) {this.name = name;}
}
