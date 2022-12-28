package de.lgs.prin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class GameController {
    private final Playfield playfield;
    private boolean paused;
    private boolean finished;
    private final ArrayList<Player> players;
    private final int playerCount;

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
        int fieldCount = 1; // statt 0 die anzahl der zu ladenden spielfelder
        for(int i = 0; i < fieldCount; i++)
        {
            // Field laden in field
            // Field position laden in position
            Field field = new Field(Fieldtype.NONE, 0); // placeholder bis das laden des feldes implementiert ist
            double position = 0.0;
            this.playfield.addField(field, position);
        }
    }
    private void executeField(Player player)
    {
        this.playfield.getField(player.getPosition()).executeAction(player);
    }
    public void run()
    {
        for(int i = 0; i < this.playerCount; i++)
        {
            Player player = this.players.get(i);
            String name = player.getName();
            System.out.printf("%s is jetzt an der Reihe!", name);
            Random random = new Random();
            int number = random.nextInt(11);
            System.out.printf("Es wurde eine %d gewürfelt!\n", number);
            player.move(number);
            executeField(player);
            System.out.printf("Der Zug von %s ist nun beendet\n", name);
        }
        System.out.println("Bist du bereit oder möchtest du eine Pause einlegen? (W|p) (w=weiter) (p=Pause)");
        Scanner scanner = new Scanner(System.in);
        String answer;
        String[] answers = {"w", "p", ""};
        while(Arrays.stream(answers).noneMatch((answer = scanner.nextLine().toLowerCase())::equals))
        {
            System.out.println("You did not enter the right key!");
            System.out.println("Bist du bereit oder möchtest du eine Pause einlegen? (W|p) (w=weiter) (p=Pause)");
        }
        this.finished = true;
        for(Player p : this.players)
        {
            if(!this.playfield.isFinished(p))
                this.finished = false;
        }
        this.paused = answer.equalsIgnoreCase("p");
    }
    public boolean isPaused() {return this.paused;}
    public boolean isFinished() {return this.finished;}
    public Playfield getPlayfield() {return this.playfield;}
    public void addPlayer(Player player)
    {
        this.players.add(player);
    }
    public void saveGame()
    {
        // save game
    }
}
