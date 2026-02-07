package ctrlzmod.undo;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ctrlzmod.CtrlZMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class UndoManager {
    private static final Logger logger = LogManager.getLogger(UndoManager.class);
    private static Object lastState = null;
    private static Object redoState = null;

    private UndoManager() {}

    public static void captureBeforeAction() {
        if (!CtrlZMod.inCombat()) {
            return;
        }

        try {
            Class<?> saveStateCls = Class.forName("savestate.SaveState");
            Object captured = tryInvokeFactory(saveStateCls, "forCurrentState");
            if (captured == null) {
                captured = tryInvokeFactory(saveStateCls, "save");
            }
            if (captured == null) {
                Constructor<?> ctor = saveStateCls.getDeclaredConstructor();
                ctor.setAccessible(true);
                captured = ctor.newInstance();
            }
            lastState = captured;
            redoState = null;
        } catch (Throwable ignored) {
            lastState = null;
            redoState = null;
            logger.debug("Capture skipped: SaveState API not available.");
        }
    }

    public static void undoLatest() {
        if (!CtrlZMod.inCombat() || lastState == null) {
            return;
        }

        try {
            redoState = captureCurrentState();

            if (!tryInvokeNoArg(lastState, "loadState")) {
                if (!tryInvokeBooleanArg(lastState, "loadState", true)) {
                    tryInvokeNoArg(lastState, "restore");
                }
            }

            AbstractDungeon.actionManager.clearPostCombatActions();
            AbstractDungeon.player.releaseCard();
        } catch (Throwable ignored) {
            logger.warn("Undo failed due to SaveState API mismatch.");
        } finally {
            lastState = null;
        }
    }

    public static void redoLatest() {
        if (!CtrlZMod.inCombat() || redoState == null) {
            return;
        }

        try {
            Object beforeRedo = captureCurrentState();

            if (!tryInvokeNoArg(redoState, "loadState")) {
                if (!tryInvokeBooleanArg(redoState, "loadState", true)) {
                    tryInvokeNoArg(redoState, "restore");
                }
            }

            AbstractDungeon.actionManager.clearPostCombatActions();
            AbstractDungeon.player.releaseCard();

            lastState = beforeRedo;
        } catch (Throwable ignored) {
            logger.warn("Redo failed due to SaveState API mismatch.");
        } finally {
            redoState = null;
        }
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
            if (captured == null) {
                Constructor<?> ctor = saveStateCls.getDeclaredConstructor();
                ctor.setAccessible(true);
                captured = ctor.newInstance();
            }
            return captured;
        } catch (Throwable ignored) {
            return null;
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
