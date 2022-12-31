package de.lgs.prin;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Scanner;

public class Menu {
    private GameController gameController;
    private String rules;
    private JSONArray scoreboard;
    private boolean run;
    private static final int scoreboardRows = 5;
    private static final String gamename = "test";
    public JSONArray getScoreboard() {return this.scoreboard;}
    public void showRules()
    {
        String rules = "";
        System.out.printf("These are the Rules:\n%s\n", rules);
    }
    public GameController getGameController()
    {
        return this.gameController;
    }
    public void showLeaderboard() throws IOException
    {
        // Load LeaderBoard in JSONObject

        JSONArray data = this.scoreboard;
        boolean changed = false;
        // dataStructure { "player": String, "score": int, "gameid": long }
        for(int i = 0; i < data.length(); i++)
        {
            for(int j = 0; j < data.length()-1; j++)
            {
                //System.out.println(data.toString());
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
                }
            }
        }
        for(int i = 0; i < Math.min(Menu.scoreboardRows, data.length()); i++)
        {
            JSONObject d = data.getJSONObject(i);
            System.out.printf("(%d): %s with a score of %d\n", i, d.getString("player"), d.getInt("score"));
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
        //System.out.println(dataText);
        return new JSONArray(dataText);
    }
    private String loadFileContent(String filename, String ifEmpty)
    {
        try
        {
            Path path = Paths.get("data/");
            Path filepath = Paths.get(path+"/"+filename);
            if(!Files.exists(path)) Files.createDirectory(path);
            File f = new File(filepath.toUri());
            // create file an ready it for the json read if it doesnt exists
            if(!f.exists())
            {
                Files.createFile(filepath);
                BufferedWriter writer = new BufferedWriter(new FileWriter(filepath.toUri().toString()));
                writer.write(ifEmpty);
                writer.close();
            }
            String fp = filepath.toUri().toString().replace("file:///", "");
            //System.out.println(fp);
            InputStream stream = new FileInputStream(fp);
            byte[] dt = stream.readAllBytes();
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < dt.length; i++)
            {
                sb.append((char)dt[i]);
            }
            String jsonText = sb.toString();
            // System.out.println(jsonText);
            return jsonText;
        } catch(IOException e)
        {
            System.out.printf("File '%s' could not be opened", filename);
        }
        return "";
    }
    public boolean loadGamestate() {
        JSONObject data = loadJsonObjectFromFile("lastGame.json");
        if(data.keySet().isEmpty()) return false;
        this.gameController = new GameController(data.getJSONArray("players").length(), this);
        gameController.initialize(data);
        return true;
    }
    public Menu()
    {
        this.gameController = new GameController(0, this);
        this.gameController.setPaused(true);
        this.scoreboard = loadJsonArrayFromFile("leaderboard.json");
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
            }
        }
        File f = new File("data/leaderboard.json");
        if(!f.exists()) try {Files.createFile(f.toPath());} catch(Exception e){}
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f.getPath()));
            writer.write(this.scoreboard.toString());
            writer.close();
        } catch(IOException e)
        {
            System.out.println("Could not save the leaderboard!");
        }
    }
    private void chooseBegin()
    {
        System.out.printf("Hello, to %s\nwhat do you want to do?\n(1) Show Rules\n(2) Show Leaderboard\n(3) Load Game\n(4) New Game\n(5) Exit\n", gamename);
        Scanner sc = new Scanner(System.in);
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
            System.out.println("Wrong input please input a valid key");
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch(Exception ignored) {}
        }
        return choice;
    }
    private void chooseMiddle()
    {
        System.out.printf("Hello, to %s\nwhat do you want to do?\n(1) Show Rules\n(2) Show Leaderboard\n(3) Continue\n(4) Exit\n", gamename);
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
                this.gameController.setPaused(false);
                break;
            case 4:
                this.gameController.saveGame();
                this.run = false;
                break;
        }
    }
    private void chooseEnd()
    {
        System.out.printf("Hello, to %s\nwhat do you want to do?\n(1) Show Rules\n(2) Show Leaderboard\n(3) New Game\n(4) Exit\n", gamename);
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
        System.out.println("With how many Players are you playing?");
        Scanner scanner = new Scanner(System.in);
        int number  = -1;
        while(number < 1)
        {
            try {
                number = Integer.parseInt(scanner.nextLine());
            } catch(Exception e)
            {
                System.out.println("Please enter a number");
            }
        }
        this.gameController = new GameController(number, this);
        this.gameController.initialize();
        this.gameController.setPaused(false);
    }
}
