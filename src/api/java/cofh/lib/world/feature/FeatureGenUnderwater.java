package cofh.lib.world.feature;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.BlockHelper;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class FeatureGenUnderwater extends FeatureBase {

	final WorldGenerator worldGen;
	final int count;
	final List<WeightedRandomBlock> matList;

	public FeatureGenUnderwater(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, int count, GenRestriction biomeRes,
			boolean regen, GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.matList = matList;
	}

	@Override
	public boolean generateFeature(Random random, int chunkX, int chunkZ, World world) {

		int blockX = chunkX * 16;
		int blockZ = chunkZ * 16;

		boolean generated = false;
		for (int i = 0; i < count; i++) {
			int x = blockX + random.nextInt(16);
			int z = blockZ + random.nextInt(16);
			if (!canGenerateInBiome(world, x, z, random))
				continue;

			int y = BlockHelper.getSurfaceBlockY(world, x, z);
			l: {
				// if (FluidHelper.lookupFluidForBlock(world.getBlock(x, y + 1, z)) != FluidHelper.WATER_FLUID) {
				if (world.getBlock(x, y + 1, z).getMaterial() != Material.water) {
					continue; // TODO: extend this to underfluid?
				}
				Block block = world.getBlock(x, y, z);
				if (!block.isAir(world, x, y, z)) {

					for (WeightedRandomBlock mat : matList) {
						if (block.isReplaceableOreGen(world, x, y, z, mat.block)) {
							break l;
						}
					}
				}
				continue;
			}

			generated |= worldGen.generate(world, random, x, y, z);
		}
		return generated;
	}

}
