package de.lgs.prin;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args)
    {
        String name = "test";
        System.out.printf("Der Zug von %s ist nun beendet\n", name);
        System.out.println("test");
        GameController c = new GameController(3);
        c.initialize();
        c.run();
        c.run();
        System.out.println(c.isPaused());
        System.out.println(c.isFinished());
    }
}
