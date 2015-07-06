package me.kemal.randomp.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class SingleBlockAccess implements IBlockAccess {
	public Block block;
	public int meta;

	public SingleBlockAccess(Block block, int metadata) {
		this.block = block;
		this.meta = metadata;
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return (x == 0 && y == 0 && z == 0) ? block : Blocks.air;
	}

	@Override
	public TileEntity getTileEntity(int x, int y, int z) {
		return null;
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int p_72802_1_, int p_72802_2_,
			int p_72802_3_, int p_72802_4_) {
		return 12;
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		return (x == 0 && y == 0 && z == 0) ? meta : 0;
	}

	@Override
	public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_,
			int p_72879_3_, int p_72879_4_) {
		return 0;
	}

	@Override
	public boolean isAirBlock(int x, int y, int z) {
		return x != 0 && y != 0 && z != 0;
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return BiomeGenBase.plains;
	}

	@Override
	public int getHeight() {
		return 1;
	}

	@Override
	public boolean extendedLevelsInChunkCache() {
		return false;
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side,
			boolean _default) {
		return (x == 0 && y == 0 && z == 0);
	}

}
