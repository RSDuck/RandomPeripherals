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

	String[] hologram;
	Block[] blockCache;

	public TileHologramProjector() {
		hologram = new String[8 * (8 * 8)];
		blockCache = new Block[8 * (8 * 8)];

		hologram[0] = Blocks.planks.getUnlocalizedName();
	}

	public void rebuildBlockCache() {
		for (int i = 0; i < hologram.length; i++) {
			/*String name = hologram[i].substring(hologram[i].indexOf("tile."),
					hologram[i].length() - 1);*/
			blockCache[i] = Block.getBlockFromName(hologram[i]);
		}
	}

	public void rebuildBlockCache(int x, int y, int z) {

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
		NBTTagList list = new NBTTagList();
		for (String blockName : hologram)
			list.appendTag(new NBTTagString((blockName == null) ? ""
					: blockName));
		tag.setTag("hologram", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		HashMap<Integer, Object> map = ((HashMap<Integer, Object>) Util
				.getRealNBTType(tag.getTag("hologram")));
		for (int i = 0; i < map.size(); i++) {
			hologram[i] = (String) (((String) map.get(i) == "") ? null : map
					.get(i));
		}
		hologram[0] = Blocks.planks.getUnlocalizedName();
		rebuildBlockCache();
	}

	public Block getBlockAt(int x, int y, int z) {
		// String together = hologram[(y*8+x)+z*64];
		// return
		// GameRegistry.findBlock(together.substring(0,together.indexOf(":") -1
		// ), together.substring(together.indexOf(":")+1, together.length()-1));
		return blockCache[(y * 8 + x) + z * 64];
	}
}
