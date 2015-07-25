package me.kemal.randomp.te;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.util.CCType;
import me.kemal.randomp.util.CCUtils;
import me.kemal.randomp.util.FunctionNotFoundException;
import me.kemal.randomp.util.IExtendablePeripheral;
import me.kemal.randomp.util.Peripheral;
import me.kemal.randomp.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileHologramProjector extends TileEntity implements IExtendablePeripheral, IBlockAccess {

	public static final int hologramWidth = 8;
	public static final int hologramHeight = 8;
	public static final int hologramDepth = 8;

	// HologramWorldProxy worldProxy;
	IComputerAccess attachedComputer;
	String[] hologram;
	byte[] hologramMeta;
	// ArrayList<TileEntity> tileentities;
	int xOffset;
	int yOffset;
	int zOffset;
	boolean dirty;
	protected Peripheral peripheral;

	public TileHologramProjector() {
		dirty = false;
		peripheral = new Peripheral();
		peripheral.setType("hologram_projector");
		peripheral.setDescription(
				"The Hologram Projector projects an field of blocks into the air. Note: all functions that take coordinates are zerobased while Lua arrays(and in most cases loops) are onebased(to fill an complete row don't use for 1,8 do instead use for 0,7)");
		hologram = new String[hologramHeight * (hologramWidth * hologramDepth)];
		hologramMeta = new byte[hologramHeight * (hologramWidth * hologramDepth)];
		for (int i = 0; i < hologram.length; i++)
			hologram[i] = Block.blockRegistry.getNameForObject(Blocks.air);
		for (int i = 0; i < hologramMeta.length; i++)
			hologramMeta[i] = 0;

		// tileentities = new ArrayList<TileEntity>();
		// worldProxy = new HologramWorldProxy(this);

		hologram[0] = Block.blockRegistry.getNameForObject(Blocks.glowstone);
		hologram[(hologramHeight * (hologramWidth * hologramDepth)) - 1] = Block.blockRegistry
				.getNameForObject(Blocks.diamond_ore);

		peripheral.AddMethod("setBlock", "Sets the projected block at the specific coordinates",
				new CCType[] {
						new CCType(Double.class, "x",
								"The X-Coordinate of the block, has to be more than 0 and less than 8", 0, 7),
						new CCType(Double.class, "y",
								"The Y-Coordinate of the block, has to be more than 0 and less than 8", 0, 7),
				new CCType(Double.class, "z", "The Z-Coordinate of the block, has to be more than 0 and less than 8", 0,
						7),
				new CCType(String.class, "block",
						"The internal name of the block to which it should be set. It's the same name you use in commands"), },
				new CCType[] { new CCType(Boolean.class, "True if the block was sucsessfull set") }, this);
		peripheral.AddMethod("getBlock", "Returns the id name of the projected block at the specific coordinates",
				new CCType[] { new CCType(Double.class, "x",
						"The X-Coordinate of the block, you want to get, has to be more than 0 and less than 8", 0, 7),
						new CCType(Double.class, "y",
								"The Y-Coordinate of the block, you want to get, has to be more than 0 and less than 8",
								0, 7),
						new CCType(Double.class, "z",
								"The Z-Coordinate of the block, you want to get, has to be more than 0 and less than 8",
								0, 7) },
				new CCType[] { new CCType(String.class, "The id name of the block") }, this);

		peripheral.AddMethod("setMeta", "Sets the meta data of an projected block at the specified coordinates",
				new CCType[] { new CCType(Double.class, "x",
						"The X-Coordinate of the block, you want to set, has to be more than 0 and less than 8", 0, 7),
						new CCType(Double.class, "y",
								"The Y-Coordinate of the block, you want to set, has to be more than 0 and less than 8",
								0, 7),
						new CCType(Double.class, "z",
								"The Z-Coordinate of the block, you want to set, has to be more than 0 and less than 8",
								0, 7),
						new CCType(Double.class, "meta", "The new meta data of the block", 0, 16) },
				new CCType[] {}, this);

		peripheral.AddMethod("getMeta", "Returns the meta data of the projected block at the specific coordinates",
				new CCType[] {
						new CCType(Double.class, "x",
								"The X-Coordinate of the block, you want to get the meta data, has to be more than 0 and less than 8",
								0, 7),
						new CCType(Double.class, "y",
								"The Y-Coordinate of the block, you want to get the meta data, has to be more than 0 and less than 8",
								0, 7),
						new CCType(Double.class, "z",
								"The Z-Coordinate of the block, you want to get the meta data, has to be more than 0 and less than 8",
								0, 7) },
				new CCType[] { new CCType(Double.class, "The meta data of the block") }, this);
		peripheral.AddMethod("draw", "Uses the given table to set the blocks of the hologram",
				new CCType[] { new CCType(HashMap.class, "hologram",
						"An table which contains the following content: {{name=The Block ID, [meta=The Block Meta Data], x=The X-Coordinate, y=The Y-Coordinate, z=The Z-Coordinate}, ...}") },
				new CCType[] {}, this);
		peripheral.AddMethod("clear", "Clears the whole hologram with the given block and metadata",
				new CCType[] { new CCType(String.class, "block", "The block to that all blocks should be converted"),
						new CCType(Double.class, "meta", "The metadata that should be set on all blocks") },
				new CCType[] {}, this);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.func_148857_g());
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList hologramTag = new NBTTagList();
		for (String hologramItem : hologram) {
			hologramTag.appendTag(new NBTTagString(hologramItem));
		}
		tag.setTag("hologram", hologramTag);
		tag.setByteArray("hologramMeta", hologramMeta);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList hologramTag = (NBTTagList) tag.getTag("hologram").copy();
		int i = 0;
		while (hologramTag.tagCount() > 0) {
			hologram[i] = ((NBTTagString) hologramTag.removeTag(0)).func_150285_a_();
			i++;
		}
		hologramMeta = tag.getByteArray("hologramMeta");
		dirty = true;
	}

	public boolean setBlock(int x, int y, int z, Block block) {
		if (x >= 0 && y >= 0 && z >= 0 && x < hologramWidth && y < hologramHeight && z < hologramDepth) {
			String blockName = Block.blockRegistry.getNameForObject(block);
			hologram[((z * hologramWidth) + x) + y * (hologramWidth * hologramDepth)] = (blockName == null)
					? "minecraft:air" : blockName;
			dirty = true;
			/*
			 * for (int i = 0; i < tileentities.size(); i++) { if
			 * (tileentities.get(i).xCoord == x && tileentities.get(i).yCoord ==
			 * z && tileentities.get(i).zCoord == z) { tileentities.remove(i);
			 * break; } }
			 */
			return true;
		}
		return false;
	}

	public boolean setBlockMetaData(int x, int y, int z, byte meta) {
		if (x >= 0 && y >= 0 && z >= 0 && x < hologramWidth && y < hologramHeight && z < hologramDepth) {
			hologramMeta[((z * hologramWidth) + x) + y * (hologramWidth * hologramDepth)] = meta;
			dirty = true;
			/*
			 * for (int i = 0; i < tileentities.size(); i++) { if
			 * (tileentities.get(i).xCoord == x && tileentities.get(i).yCoord ==
			 * z && tileentities.get(i).zCoord == z) { tileentities.remove(i);
			 * break; } }
			 */
			return true;
		}
		return false;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments,
			ITurtleAccess turtle) throws LuaException, FunctionNotFoundException {
		switch (method) {
		case "setBlock": {
			// GameRegistry.findBlock(Util.);
			Block block = Block.getBlockFromName((String) arguments[3]);
			int x = ((Number) arguments[0]).intValue();
			int y = ((Number) arguments[1]).intValue();
			int z = ((Number) arguments[2]).intValue();
			if (block == null)
				return new Object[] { false };
			setBlock(x, y, z, block);
			// RandomPeripheral.logger.info("Set block in hologram at holo
			// coords X: "
			// + x + " Y: " + y + " Z:" + z + " to "
			// + block.getUnlocalizedName());
			getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
			getWorldObj().markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, 1, 1, 1);
			return new Object[] { true };
		}
		case "getBlock": {
			int x = ((Number) arguments[0]).intValue();
			int y = ((Number) arguments[1]).intValue();
			int z = ((Number) arguments[2]).intValue();
			return new Object[] { getBlock(x, y, z) };
		}
		case "setMeta": {
			int x = ((Number) arguments[0]).intValue();
			int y = ((Number) arguments[1]).intValue();
			int z = ((Number) arguments[2]).intValue();
			byte meta = ((Number) arguments[3]).byteValue();
			setBlockMetaData(x, y, z, meta);
			getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
			getWorldObj().markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, 1, 1, 1);
			return new Object[] {};
		}
		case "getMeta": {
			int x = ((Number) arguments[0]).intValue();
			int y = ((Number) arguments[1]).intValue();
			int z = ((Number) arguments[2]).intValue();
			return new Object[] { getBlockMetadata(x, y, z) };
		}
		case "clear": {
			for (int i = 0; i < hologram.length; i++)
				hologram[i] = (String) arguments[0];
			for (int i = 0; i < hologramMeta.length; i++)
				hologramMeta[i] = ((Number) arguments[1]).byteValue();

			getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
			getWorldObj().markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, 1, 1, 1);
			return new Object[] {};
		}
		case "draw": {
			HashMap<Integer, Object> args = (HashMap<Integer, Object>) arguments[0];

			Object[] keys = args.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				HashMap<String, Object> drawCall = (HashMap<String, Object>) args.get(keys[i]);
				if (drawCall == null)
					throw new LuaException("Invalid key");
				try {
					int x = ((Number) drawCall.get("x")).intValue();
					int y = ((Number) drawCall.get("y")).intValue();
					int z = ((Number) drawCall.get("z")).intValue();
					String blockName = (String) drawCall.get("name");
					Block block = Block.getBlockFromName(blockName);
					if (block != null) {
						if (drawCall.containsKey("meta")) {
							int meta = ((Number) drawCall.get("meta")).intValue();
							setBlockMetaData(x, y, z, (byte) meta);
						}

						setBlock(x, y, z, block);
					}
					getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
					getWorldObj().markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, 1, 1, 1);
				} catch (NullPointerException e) {
					throw new LuaException(
							"Invalid draw call format, see method argument descript for more informations");
				}
			}
			return new Object[] {};
		}
		default:
			throw new FunctionNotFoundException();
		}
	}

	@Override
	public Peripheral getPeripheral() {
		return peripheral;
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return (x < 0 || y < 0 || z < 0 || x >= hologramWidth || y >= hologramHeight || z >= hologramDepth) ? Blocks.air
				: Block.getBlockFromName(hologram[((z * hologramWidth) + x) + y * (hologramWidth * hologramHeight)]);
	}

	@Override
	public TileEntity getTileEntity(int x, int y, int z) {
		/*
		 * Block block = getBlock(x, y, z); if (block instanceof
		 * ITileEntityProvider) { ITileEntityProvider tileProvider =
		 * (ITileEntityProvider) block; try { for (int i = 0; i <
		 * tileentities.size(); i++) { if (tileentities.get(i).xCoord == x &&
		 * tileentities.get(i).yCoord == z && tileentities.get(i).zCoord == z) {
		 * // tileentities.get(i).updateEntity(); return tileentities.get(i); }
		 * } TileEntity newTe = tileProvider.createNewTileEntity(worldProxy,
		 * getBlockMetadata(x, y, z)); newTe.blockMetadata = getBlockMetadata(x,
		 * y, z); newTe.blockType = getBlock(x, y, z); newTe.xCoord = x;
		 * newTe.yCoord = y; newTe.zCoord = z; newTe.setWorldObj(worldProxy);
		 * newTe.updateEntity(); tileentities.add(newTe); //
		 * RandomPeripheral.logger.info("Setup"); return newTe; } catch
		 * (Exception e) { RandomPeripheral.logger.info(
		 * "Exception while creating or updating TileEntity");
		 * e.printStackTrace(); } }
		 */
		return null;
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int p_72802_1_, int p_72802_2_, int p_72802_3_, int p_72802_4_) {
		int i1 = 15;
		int j1 = 15;

		if (j1 < p_72802_4_) {
			j1 = p_72802_4_;
		}

		return i1 << 20 | j1 << 4;
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		return (x < 0 || y < 0 || z < 0 || x >= hologramWidth || y >= hologramHeight || z >= hologramDepth) ? 0
				: hologramMeta[((z * hologramWidth) + x) + y * (hologramWidth * hologramDepth)];
	}

	@Override
	public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_, int p_72879_3_, int p_72879_4_) {
		return 0;
	}

	@Override
	public boolean isAirBlock(int x, int y, int z) {
		return getBlock(x, y, z) == Blocks.air;
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return BiomeGenBase.plains;
	}

	@Override
	public int getHeight() {
		return hologramHeight;
	}

	@Override
	public boolean extendedLevelsInChunkCache() {
		return false;
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
		return getBlock(x, y, z).isSideSolid(this, x, y, z, side);
	}

	public void setDirty(boolean val) {
		dirty = val;
	}

	@Override
	public void attachToComputer(IComputerAccess computer) {
		attachedComputer = computer;
	}

	@Override
	public void detachFromComputer(IComputerAccess computer) {
		attachedComputer = null;
	}

	public void onBlockActivated(float clickX, float clickY, float clickZ) {
		if (attachedComputer != null) {
			RandomPeripherals.logger.info("Clicked at: X: " + clickX + " Y: " + clickY + " Z: " + clickZ);
			attachedComputer.queueEvent("hologramTouch", new Object[] { (int) (Math.floor(clickX) * 16.f),
					(int) (Math.floor(clickY) * 16.f), (int) (Math.floor(clickZ) * 16.f) });
		}
	}
}
