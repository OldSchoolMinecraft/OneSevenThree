package net.oneseventhree.game.graphics.render;

import net.oneseventhree.game.OneSevenThree;

public abstract class Renderer
{
    protected final OneSevenThree game;

    public Renderer()
    {
        game = OneSevenThree.getInstance();
    }

    public abstract void render();
}
