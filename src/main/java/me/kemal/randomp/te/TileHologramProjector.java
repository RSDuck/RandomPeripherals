package me.kemal.randomp.te;

import java.util.HashMap;

import cpw.mods.fml.common.registry.GameRegistry;
import me.kemal.randomp.RandomPeripheral;
import me.kemal.randomp.util.CCUtils;
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

public class TileHologramProjector extends TileEntity {

	int[] hologram;
	Block[] blockCache;

	public TileHologramProjector() {
		hologram = new int[8 * (8 * 8)];
		blockCache = new Block[8 * (8 * 8)];

		hologram[0] = Block.getIdFromBlock(Blocks.planks);
	}

	public void rebuildBlockCache() {
		for (int i = 0; i < hologram.length; i++) {
			blockCache[i] = Block.getBlockById(hologram[i]);
		}
	}

	public void rebuildBlockCache(int x, int y, int z) {
		blockCache[((z * 8) + x) + y * 64] = Block
				.getBlockById(hologram[((z * 8) + x) + y * 64]);
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
		tag.setIntArray("hologram", hologram);
		hologram[0] = Block.getIdFromBlock(Blocks.planks);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		hologram = tag.getIntArray("hologram");
		rebuildBlockCache();
	}

	public Block getBlockAt(int x, int y, int z) {
		return blockCache[((z * 8) + x) + y * 64];
	}
}
