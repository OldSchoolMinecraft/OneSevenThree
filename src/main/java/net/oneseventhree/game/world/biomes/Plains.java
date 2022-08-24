package net.oneseventhree.game.world.biomes;

import net.oneseventhree.game.world.blocks.Block;

public class Plains extends BiomeBase
{
    public Plains()
    {
        setSeaLevel(43);
        setHeightBoundaries(20, 40);
        setTerrainNoiseScale(0.2f);
        setTopLayerBlock(Block.grass);
        setUnderTopLayerBlock(Block.dirt);
        setUnderTopLayerBlockDistance(4);
        setBottomLayerBlock(Block.stone);
    }
}
