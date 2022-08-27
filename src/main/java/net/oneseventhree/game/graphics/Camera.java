package net.oneseventhree.game.graphics;

import net.oneseventhree.game.OneSevenThree;
import net.oneseventhree.game.util.Input;
import net.oneseventhree.game.util.MathUtils;
import net.oneseventhree.game.util.Ray;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera
{
    public static final float MOVE_SPEED = 100f;
    public static final float STRAFE_SPEED = MOVE_SPEED / 1.2f;
    public static final float SENSITIVITY_X = 0.33f;
    public static final float SENSITIVITY_Y = SENSITIVITY_X;
    public static final float ROT_SPEED_X = 70.0f;

    public static float fov, aspect, z_far;

    public static Matrix4f projection, view;
    public static Vector3f position, rotation;

    public static Vector2f center;
    public static Ray ray;

    private static boolean mouse_locked;
    private static boolean mouse_left_down = false;
    private static boolean mouse_right_down = false;

    private static float pitch = 0.0f;

    public static void init(float _aspect)
    {
        aspect = _aspect;

        projection = MathUtils.createPerspectiveProjection(fov, aspect, -1f, z_far);
        view = MathUtils.createIdentityMatrix();

        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);

        center = new Vector2f((float)OneSevenThree.WIDTH / 2, (float)OneSevenThree.HEIGHT / 2);
    }

    public static boolean input()
    {
        boolean was_input = false;

        Vector2f delta = Input.getMousePosition().sub(center);

        boolean rot_x = delta.y != 0;
        boolean rot_y = delta.x != 0;

        if (rot_y) {
            yaw(delta.x * SENSITIVITY_X);
        }
        if (rot_x) {
            pitch(delta.y * SENSITIVITY_Y);
        }
        if (rot_y || rot_x) {
            Input.setMousePosition(center);
            was_input = true;
        }

        if (Input.getKeyDown(GLFW_KEY_W)) {
            forward(MOVE_SPEED * 0.01f);
            was_input = true;
        }
        if (Input.getKeyDown(GLFW_KEY_S)) {
            forward(-MOVE_SPEED * 0.01f);
            was_input = true;
        }
        if (Input.getKeyDown(GLFW_KEY_A)) {
            sideward(STRAFE_SPEED * 0.01f);
            was_input = true;
        }
        if (Input.getKeyDown(GLFW_KEY_D)) {
            sideward(-STRAFE_SPEED * 0.01f);
            was_input = true;
        }
        if (Input.getKeyDown(GLFW_KEY_SPACE)) {
            upward(-MOVE_SPEED * 0.01f);
            was_input = true;
        }
        if (Input.getKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            upward(MOVE_SPEED * 0.01f);
            was_input = true;
        }
        if (Input.getKeyDown(GLFW_KEY_ESCAPE)) {
            System.exit(0);
        }

        if (Input.getMouseDown(GLFW_MOUSE_BUTTON_LEFT)) {
            if (!mouse_left_down) {
                mouse_left_down = true;
                //TODO: break block
            }
        } else {
            mouse_left_down = false;
        }

        if (Input.getMouseDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            if (!mouse_right_down) {
                mouse_right_down = true;
                //TODO: place block
            }
        } else {
            mouse_right_down = false;
        }

        if (was_input) {
            update_ray();
        }

        return was_input;
    }

    public static void update()
    {
        view.identity();

        view.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
        view.rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        view.rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1));

        view.translate(position, view);
    }

    public static void pitch(float angle) {
        if (rotation.x - angle > 90.0f) {
            rotation.x = 89.0f;
            return;
        } else if (rotation.x - angle < -90.0f) {
            rotation.x = -89.0f;
            return;
        }
        addRotation(angle, 0, 0);
    }

    public static void yaw(float angle) {
        addRotation(0, angle, 0);
    }

    public static void forward(float amount) {
        move(amount, 1);
    }

    public static void sideward(float amount) {
        move(amount, 0);
    }

    public static void upward(float amount) {
        addPosition(0, amount, 0);
    }

    public static void addPosition(float x, float y, float z) {
        position.x += x;
        position.y += y;
        position.z += z;
    }

    public static void addRotation(float rx, float ry, float rz) {
        rotation.x += rx;
        rotation.y += ry;
        rotation.z += rz;
    }

    public static void move(float amount, float direction) {
        position.z += amount * Math.sin(Math.toRadians(rotation.y + 90 * direction));
        position.x += amount * Math.cos(Math.toRadians(rotation.y + 90 * direction));
    }

    public static void update_ray() {
        ray = new Ray(projection, view, center, OneSevenThree.WIDTH, OneSevenThree.HEIGHT);
    }
}
