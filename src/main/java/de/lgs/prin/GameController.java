package de.lgs.prin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class GameController {
    private final Playfield playfield;
    private boolean paused;
    private boolean finished;
    private final ArrayList<Player> players;
    private final int playerCount;
    private int playersFinished;
    private static int finishedPlayers = 0;
    public static int getFinishedPlayers() {return finishedPlayers;}
    private long gameid;
    private Menu menu;
    public long getGameid() {return this.gameid;}
    public void setPaused(boolean p) {this.paused = p;}
    public int getPlayerCount() {return this.playerCount;}
    private boolean firstrun;
    public GameController(int playerCount, Menu menu)
    {
        this.firstrun = true;
        this.menu = menu;
        this.gameid = System.currentTimeMillis();
        this.playerCount = playerCount;
        this.playfield = new Playfield();
        this.paused = false;
        this.finished = false;
        this.players = new ArrayList<>();
        this.playersFinished = 0;
        GameController.finishedPlayers = this.playersFinished;
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
        System.out.println("What is your name?");
        String name = scanner.nextLine();
        addPlayer(new Player(name, this));
    }
    // erstellt das spiel mit spielfeld
    public void initialize()
    {
        // erstellt die spieler
        createPlayers(this.playerCount);
        this.playfield.generateMap();
    }
    // lädt das spiel aus dem data Objekt mit einem noch nicht definiertem Datentypen
    public void initialize(JSONObject data)
    {
        // lädt die spieler
        this.gameid = data.getLong("gameid");
        for(int i = 0; i < this.playerCount; i++)
        {
            // spieler laden in p
            JSONObject po = data.getJSONArray("players").getJSONObject(i);
            Player p = new Player(po.getString("name"), this);
            //System.out.printf("Loading player '%s'\n", p.getName());
            p.setGroupsize(po.getInt("groupSize"));
            p.setPosition(po.getDouble("position"));
            //System.out.printf("Position: %f\n", p.getPosition());
            p.setMoney(po.getInt("money"));
            p.setPlayerClass(PlayerClass.valueOf(po.getString("playerClass")));
            addPlayer(p);
        }
        JSONArray fields = data.getJSONArray("fields");
        int fieldCount = fields.length(); // statt 1 die anzahl der zu ladenden spielfelder
        HashMap<Double, Field> flds = new HashMap<>();
        for(int i = 0; i < fieldCount; i++)
        {
            // Field laden in field
            // Field position laden in position
            Field field = new Field(Fieldtype.NONE, 0, this.playfield); // placeholder bis das laden des feldes implementiert ist
            double position = 0.0;
            flds.put((double)i, field);
        }
        this.playfield.generateMap(); // just for debug till the field load is finished
        // this.playfield.setPlayfield(flds); // commented out till i implemented the field load method
    }
    private void executeField(Player player)
    {
        this.playfield.getField(player.getPosition()).executeAction(player);
    }
    public int getPlayersFinished()
    {
        return this.playersFinished;
    }
    public void run()
    {
        if(this.firstrun)
        {
            System.out.println("Welcome to a world full of magic and fantasy, a world that has long been forgotten and you are the only ones who know of its existence.\n" +
                    "Now find out who will be the first to start writing the path of his life.");
            Collections.shuffle(this.players);
        }
        //System.out.println("Gameid: "+ this.gameid);
        Scanner scanner = new Scanner(System.in);
        for(int i = 0; i < this.playerCount; i++)
        {
            Player player = this.players.get(i);
            String name = player.getName();
            if(this.playfield.isFinished(player))
            {
                System.out.printf("%s is already finished, moving to the next one\n", name);
                continue;
            }
            System.out.printf("It's now the turn of %s!\n", name);
            Random random = new Random();
            if(!this.firstrun)
            {
                int number = random.nextInt(10)+1;
                System.out.printf("A %d has been rolled!\n", number);
                player.move(number);
            }
            if(this.playfield.isFinished(player))
            {
                this.playersFinished++;
                GameController.finishedPlayers = this.playersFinished;
            }
            executeField(player);
            System.out.printf("The turn of %s is now finished\n", name);
            scanner.nextLine();
        }
        // Spieler fragen ob er Pausieren möchte oder weiter spielen möchte (c und enter ist weiter und p ist pause) bei falscher eingabe wird die frage wiederhohlt
        //region
        System.out.println("Are you ready or do you want to take a break? (C|b) (c=continue) (b=break)");
        String answer;
        String[] answers = {"b", "c", ""};
        while(Arrays.stream(answers).noneMatch((answer = scanner.nextLine().toLowerCase())::equals))
        {
            System.out.println("You did not enter the right key!");
            System.out.println("Are you ready or do you want to take a break? (C|b) (c=continue) (b=break)");
        }
        this.paused = answer.equalsIgnoreCase("b");
        //endregion
        this.finished = true;
        for(Player p : this.players)
        {
            if(!this.playfield.isFinished(p)) {
                this.finished = false;
            }
        }
        if(this.paused)
        {
            for(Player p : this.players)
            {
                boolean found = false;
                for(int i = 0; i < this.menu.getScoreboard().length(); i++)
                {
                    JSONObject jo = this.menu.getScoreboard().getJSONObject(i);
                    //System.out.println(jo.getLong("gameid"));
                    if(jo.getString("player").equals(p.getName())  && jo.getLong("gameid") == this.gameid)
                    {
                        jo.put("score", p.getMoney());
                        found = true;
                    }
                }
                if(!found)
                {
                    JSONObject jo = new JSONObject();
                    jo.put("player", p.getName());
                    jo.put("score", p.getMoney());
                    jo.put("gameid", this.gameid);
                    this.menu.getScoreboard().put(jo);
                }
            }
        }
        this.firstrun = false;
    }
    public Player getPlayer(int i) {return this.players.get(i);}
    public boolean isPaused() {return this.paused;}
    public boolean isFinished() {return this.finished;}
    public Playfield getPlayfield() {return this.playfield;}
    public void addPlayer(Player player)
    {
        this.players.add(player);
    }
    public void saveGame()
    {
        JSONObject jo = new JSONObject();
        jo.put("playersize", this.playerCount);
        JSONArray playerArray = new JSONArray();
        for(Player p : this.players)
        {
            //System.out.println(this.players.size() + this.players.get(0).getName());
            JSONObject playerObject = new JSONObject();
            playerObject.put("name", p.getName());
            playerObject.put("position", p.getPosition());
            playerObject.put("money", p.getMoney());
            playerObject.put("groupSize", p.getGroupsize());
            playerObject.put("playerClass", p.getPlayerClass().name());
            //PlayerClass c = PlayerClass.valueOf("Advaned_Blacksmith");
            playerArray.put(playerObject);
        }
        jo.put("players", playerArray);
        //System.out.println(jo.toString());
        JSONArray fields = new JSONArray();
        jo.put("fields", fields);
        jo.put("gameid", this.gameid);
        File f = new File("data/lastGame.json");
        if(!f.exists()) try {Files.createFile(f.toPath());} catch(Exception e){}
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f.getPath()));
            writer.write(jo.toString());
            writer.close();
        } catch(IOException e)
        {
            System.out.println("Could not save the game!");
        }
    }
}
