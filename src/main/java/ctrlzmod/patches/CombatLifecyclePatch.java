package ctrlzmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import ctrlzmod.undo.UndoManager;

public class CombatLifecyclePatch {
    @SpirePatch(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class ClearAtBattleStart {
        public static void Postfix(AbstractPlayer __instance) {
            UndoManager.clear();
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "endBattle")
    public static class ClearAtBattleEnd {
        public static void Prefix(AbstractRoom __instance) {
            UndoManager.clear();
        }
    }
}
