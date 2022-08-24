package net.oneseventhree.game.world;

import net.oneseventhree.game.OneSevenThree;
import net.oneseventhree.game.graphics.render.VertexBufferObject;
import net.oneseventhree.game.util.PerlinNoise;
import net.oneseventhree.game.world.biomes.BiomeBase;
import net.oneseventhree.game.world.biomes.Plains;
import net.oneseventhree.game.world.blocks.Block;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class Chunk
{
    public static final int CHUNK_WIDTH = 16, CHUNK_HEIGHT = 512;

    private Vector2i coords;
    private Vector3i position;

    private VertexBufferObject vbo = new VertexBufferObject();
    private FloatBuffer vertices;

    private byte[][][] blocks;

    private boolean blockMapPopulated = false;
    private boolean modifiedByPlayer = false;

    private PerlinNoise perlin;
    private BiomeBase biome = new Plains(); //TODO: better generation system

    public Chunk(Vector2i coord, boolean generateOnLoad)
    {
        this.coords = coord;
        this.blocks = new byte[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
        this.position = new Vector3i(coord.x * CHUNK_WIDTH, 0, coord.y * CHUNK_WIDTH);
        this.perlin = new PerlinNoise();

        vertices = MemoryUtil.memAllocFloat(4096);

        if (generateOnLoad)
        {
            populateBlockMap();
            updateChunk();
        }
    }

    private void updateChunk()
    {
        generateMesh();
    }

    private void populateBlockMap()
    {
        for (int y = 0; y < CHUNK_HEIGHT; y++)
        {
            for (int x = 0; x < CHUNK_WIDTH; x++)
            {
                for (int z = 0; z < CHUNK_WIDTH; z++)
                {
                    Vector3i pos = new Vector3i(x, y, z);
                    blocks[x][y][z] = generateBlock(pos.add(position));
                }
            }
        }
    }

    private void generateMesh()
    {
        int vertexIndex = 0;
        for (int z = 0; z < CHUNK_WIDTH; z++)
        {
            for (int y = 0; y < CHUNK_HEIGHT; y++)
            {
                for (int x = 0; x < CHUNK_WIDTH; x++)
                {
                    Vector3i pos = new Vector3i(x, y, z);
                    if (!isBlockInChunk(pos)) continue;
                    Block block = Block.blocks[getBlockAt(pos)];
                    if (block == null) continue;
                    if (block.isSolid())
                        vbo.uploadData(vbo.getID(), vertices, GL_STATIC_DRAW);
                    //if (block.isSolid())
                        //meshData[vertexIndex] = Arrays.asList((float)x, (float)y, (float)z);
                        //addMeshData(new Vector3f(pos.x, pos.y, pos.z));
                }
            }
        }
        //mesh = new Mesh();
    }

    static float[] f(float[] first, float[] second) {
        float[] both = Arrays.copyOf(first, first.length+second.length);
        System.arraycopy(second, 0, both, first.length, second.length);
        return both;
    }


    private boolean checkIfBlockRender(Vector3f pos, boolean fluid)
    {
        if (!isBlockInChunk(pos))
            return OneSevenThree.getInstance().getWorld().checkForBlock(new Vector3i(Math.round(pos.x) + position.x), fluid);

        byte blockID = getBlockAt(pos);
        Block block = Block.getBlock(blockID);
        return block.isSolid() && (!block.isFluid() || fluid);
    }

    private byte getBlockAt(Vector3f pos)
    {
        return getBlockAt(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));
    }

    private byte getBlockAt(Vector3i pos)
    {
        return getBlockAt(pos.x, pos.y, pos.z);
    }

    private byte getBlockAt(int x, int y, int z)
    {
        try
        {
            return blocks[x][y][z];
        } catch (Exception ex) {
            return 0;
        }
    }

    private byte generateBlock(Vector3i pos)
    {
        int terrainHeight = Math.round(biome.getMaxTerrainHeight() * perlin.noise(pos.x, pos.z)) + biome.getMinTerrainHeight();
        byte blockID;

        if (pos.y == terrainHeight)
            blockID = biome.getTopLayerBlock().blockID;
        else if (pos.y < terrainHeight && pos.y > terrainHeight - biome.getUnderTopLayerBlockDistance())
            blockID = biome.getUnderTopLayerBlock().blockID;
        else if (pos.y > terrainHeight)
            blockID = 0;
        else blockID = biome.getBottomLayerBlock().blockID;

        if (blockID == 0 && pos.y <= biome.getSeaLevel())
            blockID = Block.waterStill.blockID;

        return blockID;
    }

    private boolean isBlockInChunk(Vector3i pos)
    {
        return pos.x >= 0 && pos.x < CHUNK_WIDTH && pos.y >= 0 && pos.y < CHUNK_HEIGHT && pos.z >= 0 && pos.z < CHUNK_WIDTH;
    }

    private boolean isBlockInChunk(Vector3f pos)
    {
        return isBlockInChunk(new Vector3i(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z)));
    }
}
