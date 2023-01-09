package de.lgs.prin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Menu {
    //region Attributes
    private GameController gameController;
    // TODO: Rules
    private static final String rules = """
            Game instructions
            The game is playable with at least 2 players.
            After you start the game, you create the players, then the game tells you whose turn it is.

            The game board consists of many adjoining squares, which are triggered. You roll a six-sided dice,
            and the game moves the figure along the board by the number rolled.
            The object of the game is to move the figure to the end of all the squares to reach Mount Celestia
            and have more money than all the other players with your adventure party.

            There are several types of fields.
            Red:
            This square stops any Player if it comes over it during its move, forcing the respective Player to trigger the event.
            Most of the time, you have to choose between two different paths on these squares.
            Yellow:
            These are gold fields, on them you get or lose the gold that the field holds for you.
            Blue:
            This field is an action field, good or bad things can happen here.
            Pink:
            The group field, on this field you will get a member added to your adventure group.

            The adventure group
            At the beginning of the game you are on your own, at some point a red field will allow you to add
            another person to your party, this party member is just part of your party and can't do anything to it during the game.
            Once you have reached Mount Celestia, they will give you a small gold bonus.

            Loading or saving a score
            At the end of a round, the game will ask if you want to pause it at this point.
            Type b in the command line. Now you are in the pause menu. There you can save the game, continue or quit it.
            When you quit a game you will also be asked if you want to save the active game state. It is only possible to save one savegame at a time.
            If you want to load the previous save game you have to choose the option "Load Game" in the main menu, then the game will take over again.
            """;
    private final JSONArray scoreboard;
    private boolean run;
    private static final int scoreboardRows = 5;
    //endregion
    //region Constructor
    public Menu()
    {
        this.gameController = new GameController(0, this);
        this.gameController.setPaused(true);
        this.scoreboard = loadJsonArrayFromFile("leaderboard.json");
    }
    //endregion
    //region Public Methods
    public JSONArray getScoreboard() {return this.scoreboard;}
    private void showRules()
    {
        System.out.printf(Utils.fNormal.format("These are the Rules:\n%s\n"), rules);
    }
    private void showLeaderboard()
    {
        // Load LeaderBoard in JSONArray
        JSONArray data = this.scoreboard;
        sortLeaderboard(data);
        for(int i = 0; i < Math.min(Menu.scoreboardRows, data.length()); i++)
        {
            JSONObject d = data.getJSONObject(i);
            System.out.printf(Utils.fNormal.format("(%d): %s with a score of %d\n"), i+1, d.getString("player"), d.getInt("score"));
        }
    }
    private void loadGamestate() {
        JSONObject data = loadJsonObjectFromFile("lastGame.json");
        if(data.keySet().isEmpty())
        {
            System.out.println(Utils.fNormal.format("No game was found new game will be created instead"));
            createNewGame();
            return;
        }
        this.gameController = new GameController(data.getJSONArray("players").length(), this);
        gameController.initialize(data);
    }
    public void start()
    {
        this.run = true;
        while(this.run)
        {
            if(this.gameController.getPlayerCount() == 0)
                chooseBegin();
            else if(this.gameController.isPaused() && !this.gameController.isFinished())
                chooseMiddle();
            else
                chooseEnd();
            while(!this.gameController.isPaused())
            {
                this.gameController.run();
                if(this.gameController.isFinished())
                {
                    this.gameController.sortPlayers();
                    Player f = this.gameController.getPlayer(0);
                    Player s = this.gameController.getPlayer(1);
                    System.out.printf(Utils.fNormal.format("The game is finished.\n%s won with %s gold, this are %s more gold than the gold of %s who is on the second place.\n"),
                            Utils.fInfo.format(f.getName()),
                            Utils.fGold.format(f.getMoney()+""),
                            Utils.fGold.format((f.getMoney()-s.getMoney())+""),
                            Utils.fInfo.format(s.getName()));
                    this.gameController.setPaused(true);
                    continue;
                }
            }
        }
        saveLeaderboard();
    }
    //endregion
    //region Private Methods
    private void sortLeaderboard(JSONArray data)
    {
        boolean changed;
        // dataStructure { "player": String, "score": int, "gameid": long }
        for(int i = 0; i < data.length(); i++)
        {
            changed = false;
            for(int j = 0; j < data.length()-1; j++)
            {
                JSONObject obj0 = data.getJSONObject(j);
                JSONObject obj1 = data.getJSONObject(j+1);
                if(obj0.getInt("score") < obj1.getInt("score"))
                {
                    int temp = obj0.getInt("score");
                    obj0.put("score", obj1.getInt("score"));
                    obj1.put("score", temp);
                    long ltemp = obj0.getLong("gameid");
                    obj0.put("gameid", obj1.getLong("gameid"));
                    obj1.put("gameid", ltemp);
                    String stemp = obj0.getString("player");
                    obj0.put("player", obj1.getString("player"));
                    obj1.put("player", stemp);
                    changed = true;
                }
            }
            if(!changed) break;
        }
    }
    private JSONObject loadJsonObjectFromFile(String filename)
    {
        String dataText = loadFileContent(filename, "{}");
        return new JSONObject(dataText);
    }
    private JSONArray loadJsonArrayFromFile(String filename)
    {
        String dataText = loadFileContent(filename, "[]");
        return new JSONArray(dataText);
    }
    private String loadFileContent(String filename, String ifEmpty)
    {
        try
        {
            Path path = Paths.get("data/");
            Path filepath = Paths.get(path+"/"+filename);
            if(!Files.exists(path)) Files.createDirectory(path);
            String fp = filepath.toString();
            File f = new File(fp);
            // create file and ready it for the json read if it doesn't exist
            if(!f.exists())
            {
                Files.createFile(f.toPath());
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fp));
                    writer.write(ifEmpty);
                    writer.close();
                } catch(IOException e)
                {
                    System.out.printf(Utils.fError.format("Could not Create File '%s'!\n"), filename);
                }
            }
            InputStream stream = new FileInputStream(fp);
            byte[] dt = stream.readAllBytes();
            StringBuilder sb = new StringBuilder();
            for (byte b : dt) {
                sb.append((char) b);
            }
            return sb.toString();
        } catch(IOException e)
        {
            System.out.printf(Utils.fError.format("File '%s' could not be opened"), filename);
        }
        return "";
    }
    private void saveLeaderboard()
    {
        File f = new File("data/leaderboard.json");
        if(!f.exists()) try {Files.createFile(f.toPath());} catch(Exception ignored){}
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f.getPath()));
            writer.write(this.scoreboard.toString());
            writer.close();
        } catch(IOException e)
        {
            System.out.println(Utils.fError.format("Could not save the leaderboard!"));
        }
    }
    private void chooseBegin()
    {
        System.out.printf(Utils.fNormal.format("Hello, to %s\nwhat do you want to do?\n(1) Show Rules\n(2) Show Leaderboard\n(3) Load Game\n(4) New Game\n(5) Exit\n"), Utils.gamename);
        int choice = getChoice(1,5);
        switch(choice)
        {
            case 1:
                showRules();
                break;
            case 2:
                try {showLeaderboard();} catch(Exception ignored) {}
                break;
            case 3:
                loadGamestate();
                this.gameController.setPaused(false);
                break;
            case 4:
                createNewGame();
                this.gameController.setPaused(false);
                break;
            case 5:
                run = false;
                break;
        }
    }
    private int getChoice(int min, int max)
    {
        Scanner sc = new Scanner(System.in);
        int choice = -1;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch(Exception ignored) {}
        while(choice < min || choice > max)
        {
            System.out.println(Utils.fError.format("Wrong input please input a valid key"));
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch(Exception ignored) {}
        }
        return choice;
    }
    private void chooseMiddle()
    {
        System.out.printf(Utils.fNormal.format("Hello, to %s\nwhat do you want to do?\n(1) Show Rules\n(2) Show Leaderboard\n(3) Continue\n(4) Save Current Game\n(5) New Game\n(6) Exit\n"), Utils.gamename);
        int choice = getChoice(1,6);
        switch(choice)
        {
            case 1:
                showRules();
                break;
            case 2:
                try {showLeaderboard();} catch(Exception ignored) {}
                break;
            case 3:
                this.gameController.setPaused(false);
                break;
            case 4:
                this.gameController.saveGame();
                break;
            case 5:
                boolean overwrite = question("Do you really want to create a new game, the old one will be overwritten?", false);
                if(overwrite)
                {
                    createNewGame();
                    this.gameController.setPaused(false);
                } else
                    System.out.println(Utils.fNormal.format("Cancled"));
                break;
            case 6:
                boolean save = question("Do you want to save the game?", true);
                if(save)
                    this.gameController.saveGame();
                this.run = false;
                break;
        }
    }
    private boolean question(String q, boolean def)
    {
        System.out.println(Utils.fNormal.format(q + " " + ((def) ? "(Y|n)" : "(y|N)")));
        Scanner sc = new Scanner(System.in);
        boolean a = sc.nextLine().equalsIgnoreCase((def) ? "n" : "y");
        return def != a;
    }
    private void chooseEnd()
    {
        System.out.printf(Utils.fNormal.format("Hello, to %s\nwhat do you want to do?\n(1) Show Rules\n(2) Show Leaderboard\n(3) New Game\n(4) Exit\n"), Utils.gamename);
        int choice = getChoice(1,4);
        switch(choice)
        {
            case 1:
                showRules();
                break;
            case 2:
                try {showLeaderboard();} catch(Exception ignored) {}
                break;
            case 3:
                createNewGame();
                this.gameController.setPaused(false);
                break;
            case 4:
                this.gameController.saveGame();
                this.run = false;
                break;
        }
    }
    private void createNewGame()
    {
        System.out.println(Utils.fNormal.format("With how many Players are you playing?"));
        Scanner scanner = new Scanner(System.in);
        int number  = -1;
        while(number < 2)
        {
            try {
                number = Integer.parseInt(scanner.nextLine());
            } catch(Exception e)
            {
                System.out.println(Utils.fError.format("Please enter a number"));
            }
        }
        this.gameController = new GameController(number, this);
        this.gameController.initialize();
        this.gameController.setPaused(false);
        this.clearScreen(2);
    }
    private void clearScreen(int i)
    {
        for(; i > 0; i--) System.out.print("\n");
    }
    //endregion
}
