package net.oneseventhree.game.world;

import net.oneseventhree.game.graphics.render.Renderer;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

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
            //glBindVertexArray(chunk.getMesh().getVaoId());
            glEnableVertexAttribArray(0);
            //glDrawArrays(GL_TRIANGLES, 0, chunk.getMesh().getVertexCount());
            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
        }
    }
}
