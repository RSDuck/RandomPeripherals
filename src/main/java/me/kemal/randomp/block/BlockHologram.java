package me.kemal.randomp.block;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.kemal.randomp.te.TileHologram;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockHologram extends Block implements ITileEntityProvider {

	public final static String blockName = "hologram";

	public BlockHologram() {
		super(Material.glass);

		setBlockBounds(0.f, 0.f, 0.f, 1.f, 1.f, 1.f);
		setBlockUnbreakable();
		
		GameRegistry.registerBlock(this, blockName);
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List boxes, Entity entity) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return -1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileHologram();
	}

}
