package com.megacrit.cardcrawl.dungeons;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class AbstractDungeon {
    public static AbstractPlayer player;
    public static boolean isScreenUp;
    public static final ActionManager actionManager = new ActionManager();

    private static AbstractRoom room;

    public static AbstractRoom getCurrRoom() {
        return room;
    }

    public static class ActionManager {
        public void clearPostCombatActions() {
        }
    }
}
