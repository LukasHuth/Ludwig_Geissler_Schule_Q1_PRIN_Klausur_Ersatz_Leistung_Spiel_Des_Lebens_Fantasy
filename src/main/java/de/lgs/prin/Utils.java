package de.lgs.prin;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;

public class Utils {
    public static final String[] monsters = new String[]{"Zombie", "Tiger", "Wolf", "Skeleton", "Reef Shark", "Dragon", "Lion", "Giant Spider", "Constrictor Snake", "Bear", "Boar", "Goblins"};
    public static final String[] missions = new String[]{"Protection Order", "Supply order", "Cooking Order", "Transport", "Military Order"};
    public static final AnsiFormat fInfo = new AnsiFormat(Attribute.TEXT_COLOR(0x00,0xFF,0xFF));
    public static final AnsiFormat fError = new AnsiFormat(Attribute.RED_TEXT());
    public static final AnsiFormat fSpecial = new AnsiFormat(Attribute.TEXT_COLOR(0xFF, 0xAA, 0x00));
    public static final AnsiFormat fNormal = new AnsiFormat(Attribute.WHITE_TEXT(), Attribute.BLACK_BACK());
    public static final AnsiFormat fGold = new AnsiFormat(Attribute.TEXT_COLOR(0xFF, 0xD7, 0x00));
    public static final String gamename = "Draconem";

}
