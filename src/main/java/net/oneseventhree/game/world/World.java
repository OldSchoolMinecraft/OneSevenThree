package net.oneseventhree.game.world;

import net.oneseventhree.game.OneSevenThree;
import net.oneseventhree.game.graphics.Camera;
import net.oneseventhree.game.graphics.render.Renderer;
import net.oneseventhree.game.graphics.utils.Shader;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

public class World extends Renderer
{
    private HashMap<Vector2i, Chunk> chunks;

    public World()
    {
        chunks = new HashMap<>();
    }

    public void generateSomeChunks()
    {
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                chunks.put(new Vector2i(x, y), new Chunk(new Vector2i(x, y), true));
        System.out.println("Finished generating some chunks: " + chunks.size());
    }

    public void addChunk(Vector2i coord, Chunk chunk)
    {
        chunks.put(coord, chunk);
    }

    public boolean checkForBlock(Vector3i pos, boolean fluid)
    {
        return false;
    }

    @Override
    public void render()
    {
        Shader.WORLD.bind();

        Shader.WORLD.set_uniform("projectionMatrix", Camera.projection);
        Shader.WORLD.set_uniform("viewMatrix", Camera.view.identity());
        Shader.WORLD.set_uniform("texSampler", 0);

        game.getTerrainTexture().bind();

        for (Chunk chunk : chunks.values())
        {
            Shader.WORLD.set_uniform("worldMatrix", game.transformer.getWorldMatrix(chunk.getPositionf(), new Vector3f(0, 0, 0), 1.0f));
            glScalef(2f, 2f, 2f);
            chunk.getMesh().draw();
        }

        Shader.WORLD.unbind();
        glDisable(GL_TEXTURE_2D);
    }
}
