package net.oneseventhree.game.world.biomes;

import net.oneseventhree.game.world.blocks.Block;

public class BiomeBase
{
    private float terrainNoiseScale;
    private int minTerrainHeight, maxTerrainHeight, seaLevel;
    private Block topLayerBlock, underTopLayerBlock, bottomLayerBlock;
    private int underTopLayerBlockDistance;

    public BiomeBase setTerrainNoiseScale(float terrainNoiseScale)
    {
        this.terrainNoiseScale = terrainNoiseScale;
        return this;
    }

    public BiomeBase setHeightBoundaries(int minTerrainHeight, int maxTerrainHeight)
    {
        this.minTerrainHeight = minTerrainHeight;
        this.maxTerrainHeight = minTerrainHeight;
        return this;
    }

    public BiomeBase setSeaLevel(int seaLevel)
    {
        this.seaLevel = seaLevel;
        return this;
    }

    public BiomeBase setTopLayerBlock(Block topLayerBlock)
    {
        this.topLayerBlock = topLayerBlock;
        return this;
    }

    public BiomeBase setUnderTopLayerBlock(Block underTopLayerBlock)
    {
        this.underTopLayerBlock = underTopLayerBlock;
        return this;
    }

    public BiomeBase setBottomLayerBlock(Block bottomLayerBlock)
    {
        this.bottomLayerBlock = bottomLayerBlock;
        return this;
    }

    public BiomeBase setUnderTopLayerBlockDistance(int underTopLayerBlockDistance)
    {
        this.underTopLayerBlockDistance = underTopLayerBlockDistance;
        return this;
    }

    public float getTerrainNoiseScale()
    {
        return terrainNoiseScale;
    }

    public int getMinTerrainHeight()
    {
        return minTerrainHeight;
    }

    public int getMaxTerrainHeight()
    {
        return maxTerrainHeight;
    }

    public int getSeaLevel()
    {
        return seaLevel;
    }

    public Block getTopLayerBlock()
    {
        return topLayerBlock;
    }

    public Block getUnderTopLayerBlock()
    {
        return underTopLayerBlock;
    }

    public Block getBottomLayerBlock()
    {
        return bottomLayerBlock;
    }

    public int getUnderTopLayerBlockDistance()
    {
        return underTopLayerBlockDistance;
    }
}
