package net.oneseventhree.game.world;

import net.oneseventhree.game.graphics.render.Renderer;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import java.util.HashMap;

public class World extends Renderer
{
    private HashMap<Vector2i, Chunk> chunks;

    public World()
    {
        chunks = new HashMap<>();
    }

    public void generateSomeChunks()
    {
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++)
                chunks.put(new Vector2i(x, y), new Chunk(new Vector2i(x, y), true));
    }

    public boolean checkForBlock(Vector3i pos, boolean fluid)
    {
        return false;
    }

    @Override
    public void render()
    {
        for (Chunk chunk : chunks.values())
        {
            Vector3i iPos = chunk.getPosition();
            Matrix4f worldMatrix = transform.getWorldMatrix(new Vector3f((float)iPos.x, (float)iPos.y, (float)iPos.z), new Vector3f(0, 0, 0), 1f);
            game.getCurrentShader().setUniform("worldMatrix", worldMatrix);
            chunk.getMesh().draw();
        }
    }
}
