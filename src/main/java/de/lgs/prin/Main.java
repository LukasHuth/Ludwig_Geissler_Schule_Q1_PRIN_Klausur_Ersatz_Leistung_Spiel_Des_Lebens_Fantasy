package de.lgs.prin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        //*
        String name = "test";
        System.out.printf("Der Zug von %s ist nun beendet\n", name);
        System.out.println("test");
        Menu menu = new Menu();
        menu.showLeaderboard();
        /*
        GameController c = new GameController(3);
        c.initialize();
        c.run();
        c.run();
        c.getPlayer(0).changeClass(PlayerClass.Advaned_Blacksmith);
        c.getPlayer(1).changeClass(PlayerClass.Alchemist);
        c.getPlayer(2).changeClass(PlayerClass.Blacksmith);
        c.saveGame();
        //*/ /*
        menu.loadGamestate();
        GameController c = menu.getGameController();
        c.run();
        System.out.println(c.isPaused());
        System.out.println(c.isFinished());
        //*/ //*
        menu.start();//*/
    }
}
