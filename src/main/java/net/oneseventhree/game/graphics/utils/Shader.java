package net.oneseventhree.game.graphics.utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader
{
    public static Shader WORLD = new Shader("world");

    private final String shader_name;
    public int program;
    public int vertex_shader;
    public int fragment_shader;

    private final Map<String, Integer> uniforms;
    public Map<String, Integer> attributes;

    public Shader(String shader_name) {
        this.shader_name = shader_name;
        this.program = glCreateProgram();
        this.uniforms = new HashMap<>();
        this.attributes = new HashMap<>();

        attach_vertex_shader(shader_name + ".vs");
        attach_fragment_shader(shader_name + ".fs");
        
        compile();
    }

    public Shader compile() {
        glLinkProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Unable to link shader program:");
            System.err.println(glGetProgramInfoLog(program, glGetShaderi(fragment_shader, GL_INFO_LOG_LENGTH)));
            destroy();
            return this;
        }

        System.out.println("Shader compiled: " + shader_name);

        return this;
    }

    public void bind() {
        glUseProgram(program);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void destroy() {
        unbind();

        glDetachShader(program, vertex_shader);
        glDetachShader(program, fragment_shader);

        glDeleteShader(vertex_shader);
        glDeleteShader(fragment_shader);

        glDeleteProgram(program);
    }

    public void createUniform(String uniformName)
    {
        int uniformLocation = glGetUniformLocation(program, uniformName);
        if (uniformLocation < 0) throw new RuntimeException("Could not find uniform:" + uniformName);
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void set_uniform(String name, Matrix4f value) {
        glUniformMatrix4fv(uniforms.get(name), false, ExtendedBufferUtil.create_flipped_buffer(value));
    }

    public void set_uniform(String name, Vector2f value) {
        glUniform2f(uniforms.get(name), value.x, value.y);
    }

    public void set_uniform(String name, Vector3f value) {
        glUniform3f(uniforms.get(name), value.x, value.y, value.z);
    }

    public void set_uniform(String name, Vector4f value) {
        glUniform4f(uniforms.get(name), value.x, value.y, value.z, value.w);
    }

    public void set_uniform(String name, float value) {
        glUniform1f(uniforms.get(name), value);
    }

    public void set_uniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }

    public Shader save_attr(String attr_name) {
        attributes.put(attr_name, glGetAttribLocation(program, attr_name));
        return this;
    }

    public Shader save_attrs(String ... attrs_names) {
        for (String attr_name : attrs_names) {
            save_attr(attr_name);
        }
        return this;
    }

    public int attr(String attr_name) {
        if (!attributes.containsKey(attr_name)) {
            try {
                throw new Exception("This attribute not saved in map");
            } catch (Exception e) {
                destroy();
            }
        }
        return attributes.get(attr_name);
    }
    
    public boolean has_attr(String attr_name) {
    	return attributes.containsKey(attr_name);
    }

    private void attach_vertex_shader(String shader_name) {
        vertex_shader = glCreateShader(GL_VERTEX_SHADER);

        glShaderSource(vertex_shader, read(Objects.requireNonNull(getClass().getResourceAsStream("/shaders/" + shader_name))));
        glCompileShader(vertex_shader);

        if (glGetShaderi(vertex_shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Unable to create vertex shader:");
            System.err.println(glGetShaderInfoLog(vertex_shader, glGetShaderi(vertex_shader, GL_INFO_LOG_LENGTH)));
            destroy();
        }

        glAttachShader(program, vertex_shader);
    }

    private void attach_fragment_shader(String shader_name) {
        fragment_shader = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragment_shader, read(Objects.requireNonNull(getClass().getResourceAsStream("/shaders/" + shader_name))));
        glCompileShader(fragment_shader);

        if (glGetShaderi(fragment_shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Unable to create fragment shader:");
            System.err.println(glGetShaderInfoLog(fragment_shader, glGetShaderi(fragment_shader, GL_INFO_LOG_LENGTH)));
            destroy();
        }

        glAttachShader(program, fragment_shader);
    }

    private final String read(InputStream inputStream)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            {
                int c = 0;
                while ((c = reader.read()) != -1) sb.append((char) c);
                return sb.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
