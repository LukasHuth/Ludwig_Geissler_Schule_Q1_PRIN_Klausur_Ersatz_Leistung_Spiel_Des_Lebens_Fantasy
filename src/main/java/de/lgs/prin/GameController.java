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
    private long gameid;
    private Menu menu;
    private ArrayList<String> playerNames;
    public long getGameid() {return this.gameid;}
    public void setPaused(boolean p) {this.paused = p;}
    public int getPlayerCount() {return this.playerCount;}
    private boolean firstrun;
    public GameController(int playerCount, Menu menu)
    {
        this.playerNames = new ArrayList<>();
        this.firstrun = true;
        this.menu = menu;
        this.gameid = System.currentTimeMillis();
        this.playerCount = playerCount;
        this.playfield = new Playfield();
        this.paused = false;
        this.finished = false;
        this.players = new ArrayList<>();
        this.playersFinished = 0;
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
        System.out.println(Utils.fNormal.format("What is your name?"));
        String name = "";
        while(name.equals("") || this.playerNames.contains(name))
        {
            name = scanner.nextLine();
            if(name.equals(""))
            {
                System.out.println(Utils.fNormal.format("Please enter a name!"));
            }
            else if(this.playerNames.contains(name))
            {
                System.out.println(Utils.fNormal.format("This name is already taken!"));
            }
        }
        addPlayer(new Player(name, this));
        this.playerNames.add(name);
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
            p.setGroupsize(po.getInt("groupsize"));
            p.setPosition(po.getDouble("position"));
            //System.out.printf("Position: %f\n", p.getPosition());
            p.setMoney(po.getInt("money"));
            p.setMoneyScale(po.getDouble("moneyScale"));
            p.setPlayerClass(PlayerClass.valueOf(po.getString("playerClass")));
            addPlayer(p);
        }
        JSONArray fields = data.getJSONArray("fields");
        int fieldCount = fields.length(); // statt 1 die anzahl der zu ladenden spielfelder
        HashMap<Double, Field> flds = new HashMap<>();
        for(int i = 0; i < fieldCount; i++)
        {
            JSONObject fo = fields.getJSONObject(i);
            Field field = new Field(Fieldtype.valueOf(fo.getString("type")), fo.getInt("amount"));
            flds.put(fo.getDouble("position"), field);
        }
        // this.playfield.generateMap(); // just for debug till the field load is finished
        this.playfield.setPlayfield(flds); // commented out till i implemented the field load method
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
            System.out.println(Utils.fSpecial.format("Welcome to a world full of magic and fantasy, a world that has long been forgotten and you are the only ones who know of its existence.\n" +
                    "Now find out who will be the first to start writing the path of his life."));
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
                System.out.printf(Utils.fNormal.format("%s is already finished, moving to the next one\n\n"), Utils.fInfo.format(name));
                continue;
            }
            System.out.printf(Utils.fNormal.format("It's now the turn of %s!\n"), Utils.fInfo.format(name));
            Random random = new Random();
            random.setSeed(System.nanoTime());
            if(!this.firstrun)
            {
                int number = random.nextInt(6)+1;
                System.out.printf(Utils.fNormal.format("A %s has been rolled!\n"), Utils.fInfo.format(number+""));
                player.move(number);
            }
            if(this.playfield.isFinished(player)) this.playersFinished++;
            executeField(player);
            System.out.printf(Utils.fNormal.format("The turn of %s is now finished\n"), Utils.fInfo.format(name));
            // System.out.printf("%s is on field %.0f\n", name, player.getPosition());
            scanner.nextLine();
        }
        // Spieler fragen ob er Pausieren möchte oder weiter spielen möchte (c und enter ist weiter und p ist pause) bei falscher eingabe wird die frage wiederhohlt
        //region
        if(!this.firstrun)
        {
            System.out.println(Utils.fNormal.format("Are you ready or do you want to take a break? (C|b) (c=continue) (b=break)"));
            String answer;
            String[] answers = {"b", "c", ""};
            while(Arrays.stream(answers).noneMatch((answer = scanner.nextLine().toLowerCase())::equals))
            {
                System.out.println(Utils.fError.format("You did not enter the right key!"));
                System.out.println(Utils.fNormal.format("Are you ready or do you want to take a break? (C|b) (c=continue) (b=break)"));
            }
            this.paused = answer.equalsIgnoreCase("b");
        }
        //endregion
        this.finished = true;
        for(Player p : this.players)
        {
            if(!this.playfield.isFinished(p)) {
                this.finished = false;
            }
        }
        if(this.finished) this.paused = true;
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
            JSONObject playerObject = new JSONObject(p);
            playerObject.remove("gameController");
            //PlayerClass c = PlayerClass.valueOf("Advaned_Blacksmith");
            playerArray.put(playerObject);
        }
        jo.put("players", playerArray);
        //System.out.println(jo.toString());
        JSONArray fields = new JSONArray();
        for(Double key : this.playfield.getPlayfield().keySet())
        {
            Field f = this.playfield.getField(key);
            JSONObject field = new JSONObject();
            field.put("position", key);
            field.put("type", f.gettype().name());
            field.put("amount", f.getamount());
            fields.put(field);
        }
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
            System.out.println(Utils.fError.format("Could not save the game!"));
        }
    }
    public void sortPlayers()
    {
        this.players.sort(new PlayerComparator());
    }
    private static class PlayerComparator implements Comparator<Player> {
        @Override
        public int compare(Player p0, Player p1){
            return p1.getMoney()-p0.getMoney();
        }
    }
}
