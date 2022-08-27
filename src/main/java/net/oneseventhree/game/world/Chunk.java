package net.oneseventhree.game.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.oneseventhree.game.OneSevenThree;
import net.oneseventhree.game.graphics.VoxelData;
import net.oneseventhree.game.graphics.utils.AABB;
import net.oneseventhree.game.graphics.utils.IndexedMesh;
import net.oneseventhree.game.graphics.utils.Texture;
import net.oneseventhree.game.graphics.utils.Vertex;
import net.oneseventhree.game.util.PerlinNoise;
import net.oneseventhree.game.world.biomes.BiomeBase;
import net.oneseventhree.game.world.biomes.Plains;
import net.oneseventhree.game.world.blocks.Block;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Chunk
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final int CHUNK_WIDTH = 16, CHUNK_HEIGHT = 512, SIZE = 32;

    private Vector2i coords;
    private Vector3i position;
    private int x_offset, y_offset, z_offset;

    private int vertexIndex = 0;
    private IndexedMesh mesh;

    private byte[][][] blocks;

    private boolean blockMapPopulated = false;
    private boolean modifiedByPlayer = false;

    private PerlinNoise perlin;
    private BiomeBase biome = new Plains(); //TODO: better generation system

    public Chunk(Vector2i coord, boolean generateOnLoad)
    {
        this.mesh = new IndexedMesh();
        this.coords = coord;
        this.blocks = new byte[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
        this.position = new Vector3i(coord.x * CHUNK_WIDTH, 0, coord.y * CHUNK_WIDTH);
        this.perlin = new PerlinNoise();

        x_offset = position.x * SIZE;
        y_offset = position.y * SIZE;
        z_offset = position.z * SIZE;

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
//        if (mesh != null)
//        {
//            //mesh.destroy();
//            //mesh = new IndexedMesh();
//        }

        LinkedHashMap<Vertex, Integer> vertex2index = new LinkedHashMap<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for (int y = 0; y < CHUNK_HEIGHT; y++)
        {
            for (int z = 0; z < CHUNK_WIDTH; z++)
            {
                for (int x = 0; x < CHUNK_WIDTH; x++)
                {
                    Vector3i pos = new Vector3i(x, y, z);
                    if (!isBlockInChunk(pos)) continue;
                    Block block = Block.blocks[getBlockAt(pos)];
                    if (block == null) continue;
                    if (block.isSolid())
                    {
                        ChunkMeshBroker broker = generateMeshData(new Vector3f(x, y, z));
                        vertex2index.putAll(broker.vertex2index);
                        indices.addAll(broker.indices);
                    }
                }
            }
        }

        mesh.update_gl_data(vertex2index.keySet(), indices);

        String fileName = "chunk-" + coords.x + "." + coords.y + "-mesh.json";
        if (new File(fileName).exists()) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
        {
            gson.toJson(vertex2index, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean shouldRenderVoxel(Vector3f pos, boolean fluid)
    {
        Block block = Block.getBlock(getBlockAt(pos));
        if (!isBlockInChunk(pos)) return false;
        return block.isSolid() && (block.isFluid() || fluid);
    }

    private boolean logged = false;
    private class ChunkMeshBroker
    {
        public LinkedHashMap<Vertex, Integer> vertex2index;
        public ArrayList<Integer> indices;

        public ChunkMeshBroker(LinkedHashMap<Vertex, Integer> vertex2index, ArrayList<Integer> indices)
        {
            this.vertex2index = vertex2index;
            this.indices = indices;
        }
    }
    private ChunkMeshBroker generateMeshData(Vector3f pos)
    {
        Block block = Block.getBlock(getBlockAt(pos));
        LinkedHashMap<Vertex, Integer> vertex2index = new LinkedHashMap<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for (int side = 0; side < 6; side++)
        {
            if (shouldRenderVoxel(pos.add(VoxelData.faceChecks[side]), block.isFluid()))
            {
                int x0 = Math.round(pos.x);
                int x1 = Math.round(pos.x);
                int z0 = Math.round(pos.z);
                int z1 = Math.round(pos.z);
                int xx = x1 - x0 + 1, zz = z1 - z0 + 1, yy = 1;
                int red = 255, green = 255, blue = 255;

                float[] vertices = AABB.SIDE.values[side].translate_and_expand(pos.x + x_offset, pos.y + y_offset, pos.z + z_offset, xx, yy, zz);
                float[] textures = Texture.calcAtlasCoords(block.getTextureIndex(side), 16);

                Integer index;

                Vertex vertex0 = new Vertex(vertices[0], vertices[1], vertices[2], 0, 0, 32, 32, red, green, blue);
                index = vertex2index.get(vertex0);
                if (index == null)
                {
                    index = vertexIndex++;
                    vertex2index.put(vertex0, index);
                }
                indices.add(index);
                indices.add(vertex2index.get(vertex0));

                Vertex vertex1 = new Vertex(vertices[3], vertices[4], vertices[5], 0, 0, 32, 32, red, green, blue);
                index = vertex2index.get(vertex1);
                if (index == null)
                {
                    index = vertexIndex++;
                    vertex2index.put(vertex1, index);
                }
                indices.add(index);
                indices.add(vertex2index.get(vertex1));

                Vertex vertex2 = new Vertex(vertices[6], vertices[7], vertices[8], 0, 0, 32, 32, red, green, blue);
                index = vertex2index.get(vertex2);
                if (index == null)
                {
                    index = vertexIndex++;
                    vertex2index.put(vertex2, index);
                }
                indices.add(index);
                indices.add(vertex2index.get(vertex2));

                Vertex vertex3 = new Vertex(vertices[9], vertices[10], vertices[11], 0, 0, 32, 32, red, green, blue);
                index = vertex2index.get(vertex3);
                if (index == null)
                {
                    index = vertexIndex++;
                    vertex2index.put(vertex3, index);
                }
                indices.add(index);
                indices.add(vertex2index.get(vertex3));
            }
        }

        return new ChunkMeshBroker(vertex2index, indices); //mesh.update_gl_data(vertex2index.keySet(), indices);
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

    public Vector3i getPosition()
    {
        return position;
    }

    public Vector3f getPositionf()
    {
        return new Vector3f(position.x, position.y, position.z);
    }

    public IndexedMesh getMesh()
    {
        return mesh;
    }
}
