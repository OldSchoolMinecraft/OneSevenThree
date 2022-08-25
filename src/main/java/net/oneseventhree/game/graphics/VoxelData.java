package net.oneseventhree.game.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class VoxelData
{
    public static int textureAtlasSizeInBlocks = 16;
    public static float normalizedBlockTextureSize = 1f / textureAtlasSizeInBlocks;

    /*public static final float[][] voxelVerts = new float[][]
    {
        {0, 0, 0},
        {1, 0, 0},
        {1, 1, 0},
        {0, 1, 0},
        {0, 0, 1},
        {1, 0, 1},
        {1, 1, 1},
        {0, 1, 1}
    };*/

    public static final Vector3f[] voxelVerts = new Vector3f[]
    {
        new Vector3f(0, 0, 0),
        new Vector3f(1, 0, 0),
        new Vector3f(1, 1, 0),
        new Vector3f(0, 1, 0),
        new Vector3f(0, 0, 1),
        new Vector3f(1, 0, 1),
        new Vector3f(1, 1, 1),
        new Vector3f(0, 1, 1)
    };

    public static final Vector3f[] faceChecks = new Vector3f[]
    {
        new Vector3f(0, 0, -1),
        new Vector3f(0, 0, 1),
        new Vector3f(0, 1, 0),
        new Vector3f(0, -1, 0),
        new Vector3f(-1, 0, 0),
        new Vector3f(1, 0, 0)
    };

    public static final Vector3f[] treeChecks = new Vector3f[]
    {
        new Vector3f(0, 0, -1),
        new Vector3f(0, 0, 1),
        new Vector3f(1, 0, 0),
        new Vector3f(-1, 0, 0),
        new Vector3f(-1, 0, -1),
        new Vector3f(1, 0, 1),
        new Vector3f(-1, 0, 1),
        new Vector3f(1, 0, -1)
    };

    public static final Vector3i[] chunkChecks = new Vector3i[]
    {
        new Vector3i(0, 0, -1),
        new Vector3i(0, 0, 1),
        new Vector3i(-1, 0, 0),
        new Vector3i(1, 0, 0)
    };

    public static final Vector2f[] voxelUvs = new Vector2f[]
    {
        new Vector2f(0.0f, 0.0f),
        new Vector2f(0.0f, 1.0f),
        new Vector2f(1.0f, 0.0f),
        new Vector2f(1.0f, 1.0f)
    };

    public static final int[][] voxelTris = new int[][]
    {
        {0, 3, 1, 2}, // Back Face
        {5, 6, 4, 7}, // Front Face
        {3, 7, 2, 6}, // Top Face
        {1, 5, 0, 4}, // Bottom Face
        {4, 7, 0, 3}, // Left Face
        {1, 2, 5, 6} // Right Face
    };
}
