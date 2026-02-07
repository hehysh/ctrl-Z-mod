package ctrlzmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;
import ctrlzmod.undo.UndoManager;

public class EndTurnUndoPatch {
    @SpirePatch(clz = EndTurnButton.class, method = "disable")
    public static class CaptureBeforeEndTurn {
        public static void Prefix(EndTurnButton __instance, boolean buttonPressed) {
            if (buttonPressed) {
                UndoManager.captureBeforeAction();
            }
        }
    }
}
