package me.kemal.randomp.te;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileHologram extends TileEntity {
	public TileHologram() {
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox((double) xCoord, (double) yCoord, (double) zCoord, (double) xCoord + 1.0,
				(double) yCoord + 1.0, (double) zCoord + 1.0);
	}
}
