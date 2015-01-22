package me.kemal.randomp.te;

import java.util.HashMap;

import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import me.kemal.randomp.RandomPeripheral;
import me.kemal.randomp.util.CCType;
import me.kemal.randomp.util.CCUtils;
import me.kemal.randomp.util.FunctionNotFoundException;
import me.kemal.randomp.util.IExtendablePeripheral;
import me.kemal.randomp.util.Peripheral;
import me.kemal.randomp.util.Util;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileHologramProjector extends TileEntity implements IExtendablePeripheral {

	String[] hologram;
	byte[] hologramMeta;
	int xOffset;
	int yOffset;
	int zOffset;
	protected Peripheral peripheral;

	public TileHologramProjector() {
		peripheral = new Peripheral();
		peripheral.setType("hologram_projector");
		hologram = new String[8 * (8 * 8)];
		hologramMeta = new byte[8 * (8 * 8)];
		for (int i = 0; i < hologram.length; i++)
			hologram[i] = Block.blockRegistry.getNameForObject(Blocks.air);
		for (int i = 0; i < hologramMeta.length; i++)
			hologramMeta[i] = 0;
		hologram[0] = Block.blockRegistry.getNameForObject(Blocks.glowstone);
		hologram[(8 * (8 * 8)) - 1] = Block.blockRegistry.getNameForObject(Blocks.diamond_ore);

		peripheral.AddMethod("setBlock", "Sets the projected block at the specific coordinates", new CCType[] {
				new CCType(Double.class, "x", "The X-Coordinate of the block, has to be more than 0 and less than 8", 0, 7),
				new CCType(Double.class, "y", "The Y-Coordinate of the block, has to be more than 0 and less than 8", 0, 7),
				new CCType(Double.class, "z", "The Z-Coordinate of the block, has to be more than 0 and less than 8", 0, 7),
				new CCType(String.class, "block",
						"The unlocalized name of the block to which it should be set. It's the same name you use in commands") },
				new CCType[] { new CCType(Boolean.class, "True if the block was sucsessfull set") }, this);
		peripheral.AddMethod("getBlock", "Returns the id name of the projected block at the specific coordinates", new CCType[] {
				new CCType(Double.class, "x", "The X-Coordinate of the block, you want to get, has to be more than 0 and less than 8",
						0, 7),
				new CCType(Double.class, "y", "The Y-Coordinate of the block, you want to get, has to be more than 0 and less than 8",
						0, 7),
				new CCType(Double.class, "z", "The Z-Coordinate of the block, you want to get, has to be more than 0 and less than 8",
						0, 7) }, new CCType[] { new CCType(String.class, "The id name of the block") }, this);
		peripheral.AddMethod("setMeta", "Sets the meta data of an projected block at the specified coordinates", new CCType[] {
				new CCType(Double.class, "x", "The X-Coordinate of the block, you want to set, has to be more than 0 and less than 8",
						0, 7),
				new CCType(Double.class, "y", "The Y-Coordinate of the block, you want to set, has to be more than 0 and less than 8",
						0, 7),
				new CCType(Double.class, "z", "The Z-Coordinate of the block, you want to set, has to be more than 0 and less than 8",
						0, 7), new CCType(Double.class, "meta", "The new meta data of the block", 0, 16) }, new CCType[] {}, this);
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
								0, 7) }, new CCType[] { new CCType(Double.class, "The meta data of the block") }, this);
		peripheral
				.AddMethod(
						"drawHologram",
						"Uses the given table to set the blocks of the hologram",
						new CCType[] { new CCType(HashMap.class, "hologram",
								"An table which contains the following content: {{name=\"block id\", meta=0, x=\"The X-Coordinate\", y=0, z=0}, other blocks...}") },
						new CCType[] {}, this);
		peripheral.AddMethod("clear", "Clears the whole hologram, so all blocks are air", new CCType[] {}, new CCType[] {}, this);
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
	}

	public void setBlockAt(int x, int y, int z, Block block) {
		hologram[((z * 8) + x) + y * 64] = Block.blockRegistry.getNameForObject(block);
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void setMetaAt(int x, int y, int z, byte meta) {
		hologramMeta[((z * 8) + x) + y * 64] = meta;
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public Block getBlockAt(int x, int y, int z) {
		return Block.getBlockFromName(hologram[((z * 8) + x) + y * 64]);
	}

	public byte getMetaAt(int x, int y, int z) {
		return hologramMeta[((z * 8) + x) + y * 64];
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle)
			throws LuaException, FunctionNotFoundException {
		switch (method) {
		case "setBlock": {
			Block block = Block.getBlockFromName((String) arguments[3]);
			int x = ((Number) arguments[0]).intValue();
			int y = ((Number) arguments[1]).intValue();
			int z = ((Number) arguments[2]).intValue();
			if (block == null)
				return new Object[] { false };
			setBlockAt(x, y, z, block);
			// RandomPeripheral.logger.info("Set block in hologram at holo coords X: "
			// + x + " Y: " + y + " Z:" + z + " to "
			// + block.getUnlocalizedName());
			return new Object[] { true };
		}
		case "getBlock": {
			int x = ((Number) arguments[0]).intValue();
			int y = ((Number) arguments[1]).intValue();
			int z = ((Number) arguments[2]).intValue();
			return new Object[] { getBlockAt(x, y, z) };
		}
		case "setMeta": {
			int x = ((Number) arguments[0]).intValue();
			int y = ((Number) arguments[1]).intValue();
			int z = ((Number) arguments[2]).intValue();
			byte meta = ((Number) arguments[3]).byteValue();
			setMetaAt(x, y, z, meta);
			return new Object[] {};
		}
		case "getMeta": {
			int x = ((Number) arguments[0]).intValue();
			int y = ((Number) arguments[1]).intValue();
			int z = ((Number) arguments[2]).intValue();
			return new Object[] { getMetaAt(x, y, z) };
		}
		case "setHologram": {

		}
		case "clear": {
			for (int i = 0; i < hologram.length; i++)
				hologram[i] = "minecraft:air";
			for (int i = 0; i < hologramMeta.length; i++)
				hologramMeta[i] = 0;
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
}
