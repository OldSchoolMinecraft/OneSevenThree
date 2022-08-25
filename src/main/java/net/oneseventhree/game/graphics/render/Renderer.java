package net.oneseventhree.game.graphics.render;

import net.oneseventhree.game.OneSevenThree;
import net.oneseventhree.game.graphics.utils.Transformation;

public abstract class Renderer
{
    protected final OneSevenThree game;
    protected final Transformation transform;

    public Renderer()
    {
        game = OneSevenThree.getInstance();
        transform = game.getTransform();
    }

    public abstract void render();
}
