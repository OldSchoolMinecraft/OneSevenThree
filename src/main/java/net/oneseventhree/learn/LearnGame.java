package net.oneseventhree.learn;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LearnGame implements Runnable
{
    private Window window;
    private ShaderProgram shaderProgram;

    float[] vertices = new float[]{
            0.0f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    int vaoId, vboId;

    private void init()
    {
        window = new Window("LearnGame", new Window.WindowOptions(true, 120, 800, 600), () -> {
            return null;
        });

        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.readIS(getClass().getResourceAsStream("/shaders/v2/world.vs")));
        shaderProgram.createFragmentShader(Utils.readIS(getClass().getResourceAsStream("/shaders/v2/world.fs")));
        shaderProgram.link();

        FloatBuffer verticesBuffer = memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        memFree(verticesBuffer);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        memFree(verticesBuffer);
    }

    private void update()
    {
        //
    }

    private void render()
    {
        shaderProgram.bind();

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);

        glDrawArrays(GL_TRIANGLES, 0, 3);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(0);

        shaderProgram.unbind();
    }

    private void loop()
    {
        //
    }

    public void run()
    {
        try
        {

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void cleanup()
    {
        shaderProgram.cleanup();
    }
}
