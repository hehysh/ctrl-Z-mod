package com.badlogic.gdx;

public class Gdx {
    public static final InputAdapter input = new InputAdapter();

    public static class InputAdapter {
        public boolean isKeyPressed(int key) {
            return false;
        }
    }
}
