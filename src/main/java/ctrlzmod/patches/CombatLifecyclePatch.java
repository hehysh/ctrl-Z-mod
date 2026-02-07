package ctrlzmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import ctrlzmod.undo.UndoManager;

public class CombatLifecyclePatch {
    @SpirePatch(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class StartAtBattleStart {
        public static void Postfix(AbstractPlayer __instance) {
            UndoManager.beginCombatSession();
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "endBattle")
    public static class EndAtBattleEnd {
        public static void Prefix(AbstractRoom __instance) {
            UndoManager.endCombatSession();
        }
    }
}
