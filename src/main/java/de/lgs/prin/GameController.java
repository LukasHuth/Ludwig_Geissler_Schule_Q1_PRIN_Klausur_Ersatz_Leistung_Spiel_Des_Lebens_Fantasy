package de.lgs.prin;

import java.util.ArrayList;

public class GameController {
    private Playfield playfield;
    private boolean paused;
    private boolean finished;
    private ArrayList<Player> players;

    public GameController(int playerCount) {}
    public void initialize(boolean withFields) {}
    public void run() {}
    public boolean isPaused() {return this.paused;}
    public boolean isFinished() {return this.finished;}
    public Playfield getPlayfield() {return this.playfield;}
    public void addPlayer(Player player) {}
    public void saveGame() {}
}
