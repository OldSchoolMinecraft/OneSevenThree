package net.oneseventhree.game.graphics.utils;

import org.joml.Vector2f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;

public class Texture
{
    private final int id;
    private int width;
    private int height;

    public Texture()
    {
        id = glGenTextures();
    }

    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void setParameter(int name, int value)
    {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }

    public void uploadData(int width, int height, ByteBuffer data)
    {
        uploadData(GL_RGBA8, width, height, GL_RGBA, data);
    }

    public void uploadData(int internalFormat, int width, int height, int format, ByteBuffer data)
    {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data);
    }

    public void delete()
    {
        glDeleteTextures(id);
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        if (width > 0) this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        if (height > 0) this.height = height;
    }

    public static float[] calcAtlasCoords(int textureIndex, int atlasSizeInBlocks)
    {
        float y = textureIndex / atlasSizeInBlocks;
        float x = textureIndex - (y * atlasSizeInBlocks);

        float normal = 1f / atlasSizeInBlocks;
        y *= normal;
        x *= normal;

        y = 1f - y - normal;

        return new float[]
        {
            x, y,
            x, y + normal,
            x + normal, y,
            x + normal, y + normal
        };
    }

    public static Texture createTexture(int width, int height, ByteBuffer data)
    {
        Texture texture = new Texture();
        texture.setWidth(width);
        texture.setHeight(height);

        texture.bind();

        texture.setParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        texture.setParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        texture.setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        texture.setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        texture.uploadData(GL_RGBA8, width, height, GL_RGBA, data);

        return texture;
    }
}
