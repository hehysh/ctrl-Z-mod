package ctrlzmod;

import basemod.BaseMod;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ctrlzmod.undo.UndoManager;

@SpireInitializer
public class CtrlZMod implements PostUpdateSubscriber {
    public static final String MOD_ID = "ctrlzmod";

    private static boolean ctrlZPressedLastFrame = false;
    private static boolean ctrlYPressedLastFrame = false;

    public CtrlZMod() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new CtrlZMod();
    }

    @Override
    public void receivePostUpdate() {
        boolean ctrlDown = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        boolean zDown = Gdx.input.isKeyPressed(Input.Keys.Z);
        boolean yDown = Gdx.input.isKeyPressed(Input.Keys.Y);
        boolean ctrlZNow = ctrlDown && zDown;
        boolean ctrlYNow = ctrlDown && yDown;

        if (ctrlZNow && !ctrlZPressedLastFrame && canHandleUndoHotkey()) {
            UndoManager.undoLatest();
        }

        if (ctrlYNow && !ctrlYPressedLastFrame && canHandleUndoHotkey()) {
            UndoManager.redoLatest();
        }

        ctrlZPressedLastFrame = ctrlZNow;
        ctrlYPressedLastFrame = ctrlYNow;
    }

    public static boolean inCombat() {
        return AbstractDungeon.player != null
                && AbstractDungeon.getCurrRoom() != null
                && AbstractDungeon.getCurrRoom().phase == com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT
                && !AbstractDungeon.isScreenUp;
    }

    private static boolean canHandleUndoHotkey() {
        return inCombat()
                && AbstractDungeon.actionManager != null;
    }
}
