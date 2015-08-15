package me.kemal.randomp.te;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

	ArrayList<IComputerAccess> attachedComputer;
	String[] hologram;
	byte[] hologramMeta;
	boolean dirty;
	protected Peripheral peripheral;

	@SideOnly(Side.CLIENT)
	int displayListID;

	boolean graphicsNeedUpdate;

	int rotation;
	int velocity;

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

		rotation = 0;
		velocity = 0;

		peripheral.AddMethod("setBlock", "Sets the projected block at the specific coordinates",
				new CCType[] { new CCType(Double.class, "x",
						"The X-Coordinate of the block, has to be 0 or more and less than 8", 0, 7),
				new CCType(Double.class, "y", "The Y-Coordinate of the block, has be 0 or moreand less than 8", 0, 7),
				new CCType(Double.class, "z", "The Z-Coordinate of the block, has to be more than 0 and less than 8", 0,
						7),
				new CCType(String.class, "block",
						"The internal name of the block to which it should be set. It's the same name you use in commands"), },
				new CCType[] { new CCType(Boolean.class, "True if the block was sucsessfull set") }, this);
		peripheral.AddMethod("getBlock", "Returns the id name of the projected block at the specific coordinates",
				new CCType[] { new CCType(Double.class, "x",
						"The X-Coordinate of the block, you want to get, has to be 0 or more and less than 8", 0, 7),
						new CCType(Double.class, "y",
								"The Y-Coordinate of the block, you want to get, has to be 0 or more and less than 8",
								0, 7),
						new CCType(Double.class, "z",
								"The Z-Coordinate of the block, you want to get, has to be 0 or more and less than 8",
								0, 7) },
				new CCType[] { new CCType(String.class, "The id name of the block") }, this);

		peripheral.AddMethod("setMeta", "Sets the meta data of an projected block at the specified coordinates",
				new CCType[] { new CCType(Double.class, "x",
						"The X-Coordinate of the block, you want to set, has to be 0 or more and less than 8", 0, 7),
						new CCType(Double.class, "y",
								"The Y-Coordinate of the block, you want to set, has to be 0 or more and less than 8",
								0, 7),
						new CCType(Double.class, "z",
								"The Z-Coordinate of the block, you want to set, has to be 0 or more and less than 8",
								0, 7),
						new CCType(Double.class, "meta", "The new meta data of the block", 0, 16) },
				new CCType[] {}, this);

		peripheral.AddMethod("getMeta", "Returns the meta data of the projected block at the specific coordinates",
				new CCType[] {
						new CCType(Double.class, "x",
								"The X-Coordinate of the block, you want to get the meta data, has to be 0 or more and less than 8",
								0, 7),
						new CCType(Double.class, "y",
								"The Y-Coordinate of the block, you want to get the meta data, has to be 0 or more and less than 8",
								0, 7),
						new CCType(Double.class, "z",
								"The Z-Coordinate of the block, you want to get the meta data, has to be 0 or more and less than 8",
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
		peripheral.AddMethod("setRotation",
				"Sets the rotation in degrees by the given value. Note: The rotation is only available on the Y-Axis(Up-Down)",
				new CCType[] { new CCType(Double.class, "rot", "The new rotation value") }, new CCType[] {}, this);
		peripheral.AddMethod("setVelocity",
				"Sets the velocity by the give value. The give value gets added to every tick to the rotation. Note: The rotation is only available on the Y-Axis(Up-Down)",
				new CCType[] { new CCType(Double.class, "vel", "The new velocity value") }, new CCType[] {}, this);
		peripheral.AddMethod("getRotation", "Returns the current rotation", new CCType[] {},
				new CCType[] { new CCType(Double.class, "rot", "The current rotation") }, this);
		peripheral.AddMethod("getVelocity", "Returns the current velocity", new CCType[] {},
				new CCType[] { new CCType(Double.class, "vel", "The current velocity") }, this);

		attachedComputer = new ArrayList<IComputerAccess>();

		displayListID = -1;
		graphicsNeedUpdate = true;
	}

	@Override
	public void updateEntity() {
		rotation += velocity;
		rotation = rotation % 360;
		rotation = (rotation < 0) ? 360 - rotation : rotation;

		if (dirty && !worldObj.isRemote) {
			dirty = false;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public boolean doGraphicsNeedAnUpdate() {
		return graphicsNeedUpdate;
	}

	public void setGraphicsUpdate(boolean graphics) {
		graphicsNeedUpdate = graphics;
	}

	public int getDisplayListID() {
		return displayListID;
	}

	public void setDisplayListID(int id) {
		displayListID = id;
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
		graphicsNeedUpdate = true;
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
		tag.setInteger("rotationY", rotation);
		tag.setInteger("velocityY", velocity);
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
		rotation = tag.getInteger("rotationY");
		velocity = tag.getInteger("velocityY");
		dirty = true;
	}

	public boolean setBlock(int x, int y, int z, Block block) {
		if (x >= 0 && y >= 0 && z >= 0 && x < hologramWidth && y < hologramHeight && z < hologramDepth) {
			String blockName = Block.blockRegistry.getNameForObject(block);
			hologram[((z * hologramWidth) + x) + y * (hologramWidth * hologramDepth)] = (blockName == null)
					? "minecraft:air" : blockName;
			return true;
		}
		return false;
	}

	public boolean setBlockMetaData(int x, int y, int z, byte meta) {
		if (x >= 0 && y >= 0 && z >= 0 && x < hologramWidth && y < hologramHeight && z < hologramDepth) {
			hologramMeta[((z * hologramWidth) + x) + y * (hologramWidth * hologramDepth)] = meta;
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

			dirty = true;
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

			dirty = true;
			return new Object[] {};
		}
		case "getMeta": {
			int x = ((Number) arguments[0]).intValue();
			int y = ((Number) arguments[1]).intValue();
			int z = ((Number) arguments[2]).intValue();
			return new Object[] { getBlockMetadata(x, y, z) };
		}
		case "clear": {
			Block block = Block.getBlockFromName((String) arguments[0]);
			if (block == null)
				return new Object[] { false };
			for (int i = 0; i < hologram.length; i++)
				hologram[i] = (String) arguments[0];
			for (int i = 0; i < hologramMeta.length; i++)
				hologramMeta[i] = ((Number) arguments[1]).byteValue();

			dirty = true;
			return new Object[] { true };
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

					dirty = true;
				} catch (NullPointerException e) {
					throw new LuaException(
							"Invalid draw call format, see method argument description for more informations");
				}
			}
			return null;
		}
		case "setRotation": {
			int rot = ((Number) arguments[0]).intValue() % 360;
			rot = (rot < 0) ? 360 - rot : rot;
			rotation = rot;
			return null;
		}
		case "setVelocity": {
			int vel = ((Number) arguments[0]).intValue() % 360;
			velocity = vel;

			return null;
		}
		case "getVelocity": {
			return new Object[] { velocity };
		}
		case "getRotation": {
			return new Object[] { rotation };
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
		return null;
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int light) {
		return 65535;
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

	public void setHologramDirty(boolean val) {
		dirty = val;
	}

	public boolean isHologramDirty() {
		return dirty;
	}

	@Override
	public void attachToComputer(IComputerAccess computer) {
		if (!attachedComputer.contains(computer))
			attachedComputer.add(computer);
	}

	@Override
	public void detachFromComputer(IComputerAccess computer) {
		attachedComputer.remove(computer);
	}

	public void onBlockClick(int clickX, int clickY, int clickZ, int side, int button, ItemStack heldItem) {
		if (attachedComputer != null) {
			HashMap<String, Object> heldItemCC = CCUtils.stackToMap(heldItem);
			if (heldItem != null)
				heldItemCC.put("isBlock", Block.getBlockFromItem(heldItem.getItem()) != Blocks.air);

			String[] sides = new String[] { "bottom", "top", "north", "south", "west", "east" };
			for (int i = 0; i < attachedComputer.size(); i++)
				attachedComputer.get(i).queueEvent("hologramTouch",
						new Object[] { button, clickX, clickY, clickZ, sides[side], heldItemCC });
		}
	}

	public int getRotation() {
		return rotation;
	}

	public boolean isTouchScreenFeatureAvailable() {
		return (rotation == 0) ? true : ((rotation % 90) == 0);
	}

	public boolean[] getAirArrayCopy() {
		boolean[] ret = new boolean[hologram.length];
		for (int i = 0; i < ret.length; i++)
			ret[i] = isAirBlock(i % hologramWidth, i / (hologramWidth * hologramHeight),
					(i / hologramWidth) % hologramDepth);
		return ret;
	}

}
