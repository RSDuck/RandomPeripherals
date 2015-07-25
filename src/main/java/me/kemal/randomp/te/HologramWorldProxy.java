package me.kemal.randomp.te;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class HologramWorldProxy extends World {
	public final static WorldSettings worldSettings = new WorldSettings(0, GameType.CREATIVE, false, false,
			WorldType.FLAT);

	private TileHologramProjector projector;

	public HologramWorldProxy(TileHologramProjector projector_) {
		super(new ISaveHandler() {

			@Override
			public void saveWorldInfoWithPlayer(WorldInfo p_75755_1_, NBTTagCompound p_75755_2_) {
			}

			@Override
			public void saveWorldInfo(WorldInfo info) {
			}

			@Override
			public WorldInfo loadWorldInfo() {
				return new WorldInfo(worldSettings, "hologram");
			}

			@Override
			public String getWorldDirectoryName() {
				return "hologram";
			}

			@Override
			public File getWorldDirectory() {
				return null;
			}

			@Override
			public IPlayerFileData getSaveHandler() {
				return null;
			}

			@Override
			public File getMapFileFromName(String file) {
				return null;
			}

			@Override
			public IChunkLoader getChunkLoader(WorldProvider provider) {
				return null;
			}

			@Override
			public void flush() {
			}

			@Override
			public void checkSessionLock() throws MinecraftException {
			}
		}, "", new WorldProviderSurface(), worldSettings, new Profiler());

		projector = projector_;
	}

	@Override
	public boolean isAirBlock(int x, int y, int z) {
		return projector.isAirBlock(x, y, z);
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return projector.getBlock(x, y, z);
	}

	@Override
	public boolean setBlock(int x, int y, int z, Block block) {
		return projector.setBlock(x, y, z, block);
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		return projector.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean setBlockMetadataWithNotify(int x, int y, int z, int meta,
			int flags) {
		return projector.setBlockMetaData(x, y, z, (byte)meta);
	}
	
	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return BiomeGenBase.plains;
	}
	
	@Override
	public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_, int p_72879_3_, int p_72879_4_) {
		return 0;
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return null;
	}

	@Override
	protected int func_152379_p() {
		return 0;
	}

	@Override
	public Entity getEntityByID(int id) {
		return null;
	}
}
