package net.oneseventhree.game;

import net.oneseventhree.game.graphics.render.Renderer;
import net.oneseventhree.game.graphics.shaders.GlobalShader;
import net.oneseventhree.game.world.World;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class OneSevenThree implements Runnable
{
    private static OneSevenThree _instance;

    private long window;
    private World world;
    private GlobalShader globalShader;
    private ArrayList<Renderer> renderers;

    public OneSevenThree()
    {
        _instance = this;

        world = new World();
        globalShader = new GlobalShader();
        renderers = new ArrayList<>();
    }

    public void startGame()
    {
        //globalShader.link();
        renderers.add(world);
        world.generateSomeChunks();
    }

    private void update()
    {
        //
    }

    private void render()
    {
        glClear(GL_COLOR_BUFFER_BIT);

        //globalShader.bind();
        for (Renderer renderer : renderers)
            renderer.render();
        //globalShader.unbind();

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private void reshape(long window, int w, int h)
    {
        glViewport(0, 0, w, h);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    private void createWindow()
    {
        if (!glfwInit()) throw new RuntimeException("Failed to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(800, 600, "OneSevenThree", 0, 0);
        if (window == 0)
        {
            glfwTerminate();
            System.exit(1);
            return;
        }

        glfwSetFramebufferSizeCallback(window, this::reshape);
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
        {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });

        try (MemoryStack stack = stackPush())
        {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        glfwShowWindow(window);
        GL.createCapabilities();
    }

    public void run()
    {
        createWindow();
        startGame();
        loop();
    }

    private void loop()
    {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(!glfwWindowShouldClose(window))
        {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while(delta >= 1)
            {
                update();
                delta--;
            }

            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000)
            {
                timer += 1000;
                frames = 0;
            }
        }
        System.exit(0); //TODO: game close routine
    }

    public World getWorld()
    {
        return world;
    }

    public static OneSevenThree getInstance()
    {
        return _instance;
    }
}
