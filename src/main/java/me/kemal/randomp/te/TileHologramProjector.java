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

public class TileHologramProjector extends TileEntity implements IExtendablePeripheral {

	String[] hologram;
	protected static Peripheral peripheral;

	public TileHologramProjector() {
		hologram = new String[8 * (8 * 8)];
		for (int i = 0; i < hologram.length; i++)
			hologram[i] = Block.blockRegistry.getNameForObject(Blocks.air);
		hologram[0] = Block.blockRegistry.getNameForObject(Blocks.glowstone);
		hologram[(8 * (8 * 8)) - 1] = Block.blockRegistry.getNameForObject(Blocks.diamond_ore);

		if (peripheral == null) {
			peripheral = new Peripheral();
			peripheral.AddMethod("setBlockAt", "Sets the projected block at the specific coordinates", new CCType[] { new CCType(Double.class, "The X-Coordinate of the block")},
					new CCType[] {}, this);
			peripheral.AddMethod("getBlockAt", "Returns the projected block at the specific coordinates", new CCType[] {},
					new CCType[] {}, this);
		}
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
	}

	public void setBlockAt(int x, int y, int z, Block block) {
		hologram[((z * 8) + x) + y * 64] = Block.blockRegistry.getNameForObject(block);
	}

	public Block getBlockAt(int x, int y, int z) {
		return Block.getBlockFromName(hologram[((z * 8) + x) + y * 64]);
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle)
			throws LuaException, FunctionNotFoundException {
		switch (method) {
		default:
			throw new FunctionNotFoundException();
		}
	}

	@Override
	public Peripheral getPeripheral() {
		return peripheral;
	}
}
