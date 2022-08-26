package net.oneseventhree.game.util;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Input
{
    public static long WINDOW = 0;
    
    public static final int NUM_KEYCODES = 348;
    public static final int NUM_MOUSEBUTTONS = 7;

    private static boolean[] lastKeys = new boolean[NUM_KEYCODES];
    private static boolean[] lastMouse = new boolean[NUM_MOUSEBUTTONS];

    public static void init(long window) {
        WINDOW = window;
        glfwPollEvents();
    }

    public static void update() {
        for (int i = 0; i < NUM_KEYCODES; i++) {
            lastKeys[i] = getKey(i);
        }
        for (int i = 0; i < NUM_MOUSEBUTTONS; i++) {
            lastMouse[i] = getMouse(i);
        }
    }

    public static boolean getKey(int keyCode) {
        return glfwGetKey(WINDOW, keyCode) == 1;
    }

    public static boolean getKeyDown(int keyCode) {
        return getKey(keyCode) && !lastKeys[keyCode];
    }

    public static boolean getKeyUp(int keyCode) {
        return !getKey(keyCode) && lastKeys[keyCode];
    }

    public static boolean getMouse(int mouseButton) {
        return glfwGetMouseButton(WINDOW, mouseButton) == 1;
    }

    public static boolean getMouseDown(int mouseButton) {
        return getMouse(mouseButton);
    }

    public static boolean getMouseUp(int mouseButton) {
        return !getMouse(mouseButton);
    }

    public static Vector2f getMousePosition() {
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1), y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(WINDOW, x, y);
        return new Vector2f((float) x.get(0), (float) y.get(0));
    }

    public static void setMousePosition(Vector2f pos) {
        glfwSetCursorPos(WINDOW, pos.x, pos.y);
    }

    public static void setCursorVisible(boolean visible) {
        if (visible) {
            glfwSetInputMode(WINDOW, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            setCursorDisabled(false);
        } else {
            glfwSetInputMode(WINDOW, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            setCursorDisabled(true);
        }
    }

    public static void setCursorDisabled(boolean disabled) {
        if (disabled) {
            glfwSetInputMode(WINDOW, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        } else {
            glfwSetInputMode(WINDOW, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }
}
