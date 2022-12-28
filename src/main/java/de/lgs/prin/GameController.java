package de.lgs.prin;

import java.util.ArrayList;
import java.util.Scanner;

public class GameController {
    private Playfield playfield;
    private boolean paused;
    private boolean finished;
    private ArrayList<Player> players;
    private int playerCount;

    public GameController(int playerCount)
    {
        this.playerCount = playerCount;
        this.playfield = new Playfield();
        this.paused = false;
        this.finished = false;
        this.players = new ArrayList<>();
    }
    // initialisiert eine Schleife die so oft läuft wie es Spieler gibt, um die Spieler zu erstellen
    private void createPlayers(int playerCount)
    {
        for (int i = 0; i < playerCount; i++) {
            createPlayer();
        }
    }
    // erstellt einen Spieler, nachdem nach dem Namen gefragt wurde
    private void createPlayer()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Was ist dein name?");
        String name = scanner.nextLine();
        addPlayer(new Player(name));
    }
    // erstellt das spiel mit spielfeld
    public void initialize()
    {
        // erstellt die spieler
        createPlayers(this.playerCount);
        this.playfield.generateFields();
    }
    // lädt das spiel aus dem data Objekt mit einem noch nicht definiertem Datentypen
    public void initialize(Object data)
    {
        // lädt die spieler
        for(int i = 0; i < this.playerCount; i++)
        {
            // spieler laden in p
            Player p = new Player(""); // placeholder bis das spieler laden implementiert ist
            addPlayer(p);
        }
        int fieldCount = 0; // statt 0 die anzahl der zu ladenden spielfelder
        for(int i = 0; i < fieldCount; i++)
        {
            // Field laden in field
            // Field position laden in position
            Field field = new Field(Fieldtype.NONE, 0); // placeholder bis das laden des feldes implementiert ist
            double position = 0.0;
            this.playfield.addField(field, position);
        }
    }
    public void run()
    {

    }
    public boolean isPaused() {return this.paused;}
    public boolean isFinished() {return this.finished;}
    public Playfield getPlayfield() {return this.playfield;}
    public void addPlayer(Player player)
    {
        this.players.add(player);
    }
    public void saveGame() {}
}
