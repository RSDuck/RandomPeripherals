package me.kemal.randomp.computercraft;

import net.minecraft.block.BlockSourceImpl;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSourceImplDispenserHack extends BlockSourceImpl {
	private int dir;
	
	public BlockSourceImplDispenserHack(World world, int dir, int x, int y, int z) {
		super(world,x,y,z);
		this.dir = dir;
	}

	@Override
	public int getBlockMetadata() {
		return dir;
	}

}
