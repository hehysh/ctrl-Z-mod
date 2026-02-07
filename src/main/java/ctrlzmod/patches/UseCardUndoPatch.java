package ctrlzmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import ctrlzmod.undo.UndoManager;

public class UseCardUndoPatch {
    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class CaptureBeforeUseCard {
        public static void Prefix(AbstractPlayer __instance, AbstractCard c, com.megacrit.cardcrawl.monsters.AbstractMonster monster, int energyOnUse) {
            UndoManager.captureBeforeAction();
        }
    }
}
