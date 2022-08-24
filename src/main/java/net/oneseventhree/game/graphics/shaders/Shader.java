package net.oneseventhree.game.graphics.shaders;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL20.*;

public class Shader
{
    private String fragSource, vertSource;
    private int programID, fragShader, vertShader;

    public Shader(String fragPath, String vertPath, boolean internal)
    {
        try
        {
            if (internal)
            {
                fragSource = read(getClass().getResourceAsStream(fragPath));
                vertSource = read(getClass().getResourceAsStream(vertPath));
                return;
            }

            fragSource = Files.readString(Path.of(fragPath));
            vertSource = Files.readString(Path.of(vertPath));

            programID = glCreateProgram();
            fragShader = createShader(GL_FRAGMENT_SHADER, fragSource);
            vertShader = createShader(GL_VERTEX_SHADER, vertSource);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load shader");
        }
    }

    public void link()
    {
        glLinkProgram(programID);
        //if (glGetProgrami(programID, GL_LINK_STATUS) == 0) throw new RuntimeException("Error linking shader: " + glGetProgramInfoLog(programID, 1024));
        if (vertShader != 0)
            glDetachShader(programID, vertShader);
        if (fragShader != 0)
            glDetachShader(programID, fragShader);
        glValidateProgram(programID);
        //if (glGetProgrami(programID, GL_VALIDATE_STATUS) == 0) System.err.println("Error validating shader: " + glGetProgramInfoLog(programID, 1024));
    }

    private int createShader(int shaderType, String source)
    {
        int shaderID = glCreateShader(shaderType);
        //if (shaderID == 0) throw new RuntimeException("Error creating shader with type: " + shaderType);
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);
        //if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) throw new RuntimeException("Error compiling shader: " + glGetShaderInfoLog(shaderID, 1024));
        glAttachShader(programID, shaderID);
        return shaderID;
    }

    private final String read(InputStream inputStream) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        {
            int c = 0;
            while ((c = reader.read()) != -1) sb.append((char) c);
            return sb.toString();
        }
    }

    public void bind()
    {
        glUseProgram(programID);
    }

    public void unbind()
    {
        glUseProgram(0);
    }

    public void cleanup()
    {
        unbind();
        if (programID != 0)
            glDeleteProgram(programID);
    }

    public int getUniformLocation(CharSequence name)
    {
        return glGetUniformLocation(programID, name);
    }

    public void setUniform(CharSequence name, float val)
    {
        glUniform1f(getUniformLocation(name), val);
    }

    public void setUniform(CharSequence name, Matrix4f value)
    {
        glUniformMatrix2fv(getUniformLocation(name), false, value.get(BufferUtils.createFloatBuffer(4 * 4)));
    }
}
