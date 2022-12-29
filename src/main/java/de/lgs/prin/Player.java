package de.lgs.prin;

public class Player {
    private String name;
    private PlayerClass playerClass;
    private double position;
    private int money;
    private GameController gameController;
    public void move(int steps) {this.position+=steps;}
    public void addMoney(int money) {}
    public void changeClass(PlayerClass playerClass) {}
    public String getName() {return this.name;}
    public Player(String name, GameController controller) {this.name = name;this.gameController = controller;this.position = 0;}
    public double getPosition() {return this.position;}
}
