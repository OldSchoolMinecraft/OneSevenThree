package net.oneseventhree.game.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MathUtils
{
    public static Matrix4f createWorldMatrix(Vector3f offset, Vector3f rotation, float scale)
    {
        Matrix4f mat = new Matrix4f();
        mat.identity().translate(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale);
        return mat;
    }

    public static Matrix4f createPerspectiveProjection(float fov, float aspect, float zNear, float zFar)
    {
        Matrix4f mat = new Matrix4f();
        mat.setPerspective(fov, aspect, zNear, zFar);
        return mat;
    }

    public static Matrix4f createIdentityMatrix()
    {
        Matrix4f mat = new Matrix4f();
        return mat.identity();
    }
}
