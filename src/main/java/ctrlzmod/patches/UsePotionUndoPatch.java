package ctrlzmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import ctrlzmod.undo.UndoManager;

public class UsePotionUndoPatch {
    @SpirePatch(clz = AbstractPlayer.class, method = "usePotion")
    public static class CaptureBeforeUsePotion {
        public static void Prefix(AbstractPlayer __instance, int potionSlot) {
            UndoManager.captureBeforeAction();
        }
    }
}
