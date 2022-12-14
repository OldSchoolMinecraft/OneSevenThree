package net.oneseventhree.game;

import de.matthiasmann.twl.utils.PNGDecoder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.oneseventhree.game.graphics.Camera;
import net.oneseventhree.game.graphics.render.CubeRenderer;
import net.oneseventhree.game.graphics.render.Renderer;
import net.oneseventhree.game.graphics.ui.Debug;
import net.oneseventhree.game.graphics.ui.ImGuiLayer;
import net.oneseventhree.game.graphics.utils.Shader;
import net.oneseventhree.game.graphics.utils.Texture;
import net.oneseventhree.game.graphics.utils.Transformation;
import net.oneseventhree.game.util.Input;
import net.oneseventhree.game.util.MathUtils;
import net.oneseventhree.game.world.Chunk;
import net.oneseventhree.game.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class OneSevenThree implements Runnable
{
    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGui = new ImGuiImplGl3();
    public static int WIDTH = 800, HEIGHT = 600, RENDER_DISTANCE = 16;
    public static boolean IMGUI = false;

    private static OneSevenThree _instance;

    private long window;
    public Transformation transformer;
    private World world;
    private Shader activeShader;
    private ArrayList<Renderer> renderers;
    private ArrayList<ImGuiLayer> imGuiLayers;
    private Matrix4f worldMatrix;
    private Texture terrainTexture;
    private Callback callback;

    public OneSevenThree()
    {
        _instance = this;

        world = new World();
        renderers = new ArrayList<>();
        imGuiLayers = new ArrayList<>();
    }

    public void startGame()
    {
        loadTextures();
        Camera.init((float)WIDTH / HEIGHT);
        Input.init(window);

        transformer = new Transformation();
        worldMatrix = new Matrix4f().identity();
        setActiveShader(Shader.WORLD);
        activeShader.createUniform("projectionMatrix");
        activeShader.createUniform("worldMatrix");
        activeShader.createUniform("viewMatrix");
        activeShader.createUniform("texSampler");

        renderers.add(world);
        imGuiLayers.add(new Debug());
        world.generateSomeChunks();
    }

    private void update()
    {
        if (Camera.input())
        {
            Camera.update();
        }
    }

    private void render()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, WIDTH, HEIGHT);
        glClearColor(0f, 0f, 0f, 0f);
        glEnable(GL_DEPTH_TEST);

        glfwPollEvents();

        for (Renderer renderer : renderers)
            renderer.render();

        if (IMGUI)
        {
            imGuiGlfw.newFrame();
            ImGui.newFrame();

            for (ImGuiLayer layer : imGuiLayers)
                layer.draw();

            ImGui.render();
            imGui.renderDrawData(ImGui.getDrawData());
        }

        glfwSwapBuffers(window);
    }

    private void reshape(long window, int w, int h)
    {
        glViewport(0, 0, w, h);
    }

    private void createWindow()
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new RuntimeException("Failed to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint( GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE );
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "OneSevenThree", 0, 0);
        if (window == 0)
            throw new RuntimeException("Failed to initialize window");

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN); // hide cursor

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
        initImGui();
    }

    private void initGL()
    {
        GL.createCapabilities();
        callback = GLUtil.setupDebugMessageCallback();

        glViewport(0, 0, WIDTH, HEIGHT);
    }

    private void initImGui()
    {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        ImGui.styleColorsLight();
        imGuiGlfw.init(window, true);
        imGui.init("#version 330");
    }

    private void loadTextures()
    {
        try
        {
            PNGDecoder decoder = new PNGDecoder(getClass().getResourceAsStream("/textures/terrain.png"));
            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            terrainTexture = Texture.createTexture(decoder.getWidth(), decoder.getHeight(), buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
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

    public Shader getActiveShader()
    {
        return activeShader;
    }

    public void setActiveShader(Shader shader)
    {
        activeShader = shader;
    }

    public Matrix4f getWorldMatrix()
    {
        return worldMatrix;
    }

    public float onePixelSize()
    {
        return 2.0f / Math.min(WIDTH, HEIGHT);
    }

    public Texture getTerrainTexture()
    {
        return terrainTexture;
    }

    public static OneSevenThree getInstance()
    {
        return _instance;
    }
}
