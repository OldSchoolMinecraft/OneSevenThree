package net.oneseventhree.game;

import net.oneseventhree.game.graphics.render.Renderer;
import net.oneseventhree.game.graphics.utils.Shader;
import net.oneseventhree.game.graphics.utils.Transformation;
import net.oneseventhree.game.world.World;
import org.joml.Matrix4f;
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
    public static int WIDTH = 800, HEIGHT = 600;

    private static OneSevenThree _instance;

    private long window;
    private World world;
    private Shader currentShader;
    private ArrayList<Renderer> renderers;
    private Transformation transform;
    private Matrix4f projectionMatrix;
    private float FOV = (float) Math.toRadians(70.0f);
    private float Z_NEAR = 0.01f;
    private float Z_FAR = 1000.f;

    public OneSevenThree()
    {
        _instance = this;

        transform = new Transformation();
        float aspectRatio = (float) WIDTH / HEIGHT;
        projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio, Z_NEAR, Z_FAR);

        world = new World();
        renderers = new ArrayList<>();
    }

    public void startGame()
    {
        renderers.add(world);
        world.generateSomeChunks();
    }

    private void update()
    {
        //
    }

    private void render()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glfwPollEvents();
        glPushMatrix();

        currentShader.bind();
        Matrix4f projectionMatrix = transform.getProjectionMatrix(FOV, WIDTH, HEIGHT, Z_NEAR, Z_FAR);
        currentShader.setUniform("projectionMatrix", projectionMatrix);

        for (Renderer renderer : renderers)
            renderer.render();

        currentShader.unbind();

        glPopMatrix();
        glfwSwapBuffers(window);
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

        window = glfwCreateWindow(WIDTH, HEIGHT, "OneSevenThree", 0, 0);
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
        initGL();
    }

    private void initGL()
    {
        GL.createCapabilities();
        currentShader = new Shader("default");
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

    public Shader getCurrentShader()
    {
        return currentShader;
    }

    public Transformation getTransform()
    {
        return transform;
    }

    public float onePixelSize()
    {
        return 2.0f / Math.min(WIDTH, HEIGHT);
    }

    public static OneSevenThree getInstance()
    {
        return _instance;
    }
}
