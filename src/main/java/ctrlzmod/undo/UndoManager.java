package ctrlzmod.undo;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ctrlzmod.CtrlZMod;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public class UndoManager {
    private static final Logger logger = Logger.getLogger(UndoManager.class.getName());

    private static final class Snapshot {
        final Object state;
        final long sessionToken;

        Snapshot(Object state, long sessionToken) {
            this.state = state;
            this.sessionToken = sessionToken;
        }
    }

    private static long currentSessionToken = 0L;
    private static boolean disabledForCurrentCombat = false;

    private static Object lastState = null;
    private static Object redoState = null;

    private UndoManager() {}

    public static void captureBeforeAction() {
        if (!canUseUndoNow()) {
            return;
        }

        try {
            Object captured = captureCurrentState();
            if (captured == null) {
                disableForCurrentCombat("capture", null);
                return;
            }
            lastState = new Snapshot(captured, currentSessionToken);
            redoState = null;
        } catch (Throwable t) {
            disableForCurrentCombat("capture", t);
        }
    }

    public static void undoLatest() {
        if (!canUseUndoNow() || !(lastState instanceof Snapshot)) {
            return;
        }

        try {
            Snapshot undoSnapshot = (Snapshot) lastState;
            if (undoSnapshot.sessionToken != currentSessionToken) {
                clear();
                return;
            }

            Object redoRaw = captureCurrentState();
            if (redoRaw == null) {
                disableForCurrentCombat("undo-capture-redo", null);
                return;
            }
            redoState = new Snapshot(redoRaw, currentSessionToken);

            if (!tryInvokeNoArg(undoSnapshot.state, "loadState")) {
                if (!tryInvokeBooleanArg(undoSnapshot.state, "loadState", true)) {
                    if (!tryInvokeNoArg(undoSnapshot.state, "restore")) {
                        disableForCurrentCombat("undo-load", null);
                        return;
                    }
                }
            }

            AbstractDungeon.actionManager.clearPostCombatActions();
            AbstractDungeon.player.releaseCard();
        } catch (Throwable t) {
            disableForCurrentCombat("undo", t);
        } finally {
            lastState = null;
        }
    }

    public static void redoLatest() {
        if (!canUseUndoNow() || !(redoState instanceof Snapshot)) {
            return;
        }

        try {
            Snapshot redoSnapshot = (Snapshot) redoState;
            if (redoSnapshot.sessionToken != currentSessionToken) {
                clear();
                return;
            }

            Object beforeRedo = captureCurrentState();
            if (beforeRedo == null) {
                disableForCurrentCombat("redo-capture-undo", null);
                return;
            }

            if (!tryInvokeNoArg(redoSnapshot.state, "loadState")) {
                if (!tryInvokeBooleanArg(redoSnapshot.state, "loadState", true)) {
                    if (!tryInvokeNoArg(redoSnapshot.state, "restore")) {
                        disableForCurrentCombat("redo-load", null);
                        return;
                    }
                }
            }

            AbstractDungeon.actionManager.clearPostCombatActions();
            AbstractDungeon.player.releaseCard();

            lastState = new Snapshot(beforeRedo, currentSessionToken);
        } catch (Throwable t) {
            disableForCurrentCombat("redo", t);
        } finally {
            redoState = null;
        }
    }

    public static void beginCombatSession() {
        currentSessionToken++;
        disabledForCurrentCombat = false;
        clear();
    }

    public static void endCombatSession() {
        clear();
        disabledForCurrentCombat = false;
    }

    public static void clear() {
        lastState = null;
        redoState = null;
    }

    private static Object captureCurrentState() {
        try {
            Class<?> saveStateCls = Class.forName("savestate.SaveState");
            Object captured = tryInvokeFactory(saveStateCls, "forCurrentState");
            if (captured == null) {
                captured = tryInvokeFactory(saveStateCls, "save");
            }
            return captured;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean canUseUndoNow() {
        return CtrlZMod.inCombat() && !disabledForCurrentCombat;
    }

    private static void disableForCurrentCombat(String stage, Throwable t) {
        disabledForCurrentCombat = true;
        clear();
        if (t == null) {
            logger.warning("Undo disabled for current combat at stage: " + stage);
        } else {
            logger.warning("Undo disabled for current combat at stage: " + stage + " due to: " + t.getClass().getSimpleName());
        }
    }

    private static Object tryInvokeFactory(Class<?> cls, String methodName) {
        try {
            Method method = cls.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean tryInvokeNoArg(Object instance, String methodName) {
        try {
            Method method = instance.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(instance);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean tryInvokeBooleanArg(Object instance, String methodName, boolean arg) {
        try {
            Method method = instance.getClass().getDeclaredMethod(methodName, boolean.class);
            method.setAccessible(true);
            method.invoke(instance, arg);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
