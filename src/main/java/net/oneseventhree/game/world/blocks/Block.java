package net.oneseventhree.game.world.blocks;

public class Block
{
    public static Block[] blocks = new Block[512];

    public static Block getBlock(byte blockID)
    {
        if (blockID < 0 || blockID > blocks.length) throw new RuntimeException("The block index you are trying to access is out of bounds: " + blockID);
        Block block = blocks[blockID];
        if (block == null) throw new RuntimeException("The block you are trying to retrieve is null: " + blockID);
        return blocks[blockID];
    }

    public static Block air = new Block(0);
    public static Block stone = new Block(1).setName("Stone").setAllTextures(1);
    public static Block grass = new Block(2).setName("Grass").setTextureData(new int[] { 3, 42, 3, 2, 3, 3 });
    public static Block dirt = new Block(3).setName("Dirt");
    public static Block cobblestone = new Block(4).setName("Cobblestone");
    public static Block oakPlank = new Block(5).setName("Oak Wood Plank");
    public static Block oakSapling = new Block(6).setName("Oak Sapling");
    public static Block bedrock = new Block(7).setName("Bedrock");
    public static Block waterFlowing = new Block(8);
    public static Block waterStill = new Block(9);
    public static Block lavaFlowing = new Block(10);
    public static Block lavaStill = new Block(11);
    public static Block sand = new Block(12).setName("Sand");
    public static Block gravel = new Block(13).setName("Gravel");
    public static Block goldOre = new Block(14).setName("Gold Ore");

    public final byte blockID;
    private int[] textureData = new int[6];
    private String name = "Untitled Block";
    private boolean fluid = false;

    public Block(int blockID)
    {
        this.blockID = (byte)blockID;
        if (blockID > blocks.length) throw new RuntimeException("blockID exceeds limit=" + blocks.length);
        blocks[blockID] = this;
    }

    public Block setTextureData(int[] textureData)
    {
        if (textureData.length != 6) throw new RuntimeException("Texture data must be an array with 6 integers.");
        for (int i = 0; i < this.textureData.length; i++)
            this.textureData[i] = textureData[i];
        return this;
    }

    public Block setAllTextures(int textureIndex)
    {
        for (int i = 0; i < this.textureData.length; i++)
            this.textureData[i] = textureIndex;
        return this;
    }

    public Block setName(String name)
    {
        this.name = name;
        return this;
    }

    public Block setFluid(boolean flag)
    {
        fluid = flag;
        return this;
    }

    public boolean isSolid()
    {
        return !fluid;
    }

    public boolean isFluid()
    {
        return fluid;
    }

    public int getTextureIndex(int side)
    {
        return textureData[side];
    }
}
