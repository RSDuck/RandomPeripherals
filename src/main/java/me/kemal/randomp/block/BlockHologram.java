package me.kemal.randomp.block;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.kemal.randomp.te.TileHologram;
import me.kemal.randomp.te.TileHologramProjector;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
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
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List boxes,
			Entity entity) {
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

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX,
			float clickY, float clickZ) {

		if (world.getTileEntity(x, y - 1, z) instanceof TileHologramProjector) {
			TileHologramProjector projector = (TileHologramProjector) world.getTileEntity(x, y - 1, z);
			projector.onBlockActivated(clickX,clickY,clickZ);
		}

		return true;
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World p_149731_1_, int p_149731_2_, int p_149731_3_, int p_149731_4_,
			Vec3 p_149731_5_, Vec3 p_149731_6_) {
		// TODO Auto-generated method stub
		return super.collisionRayTrace(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_, p_149731_5_, p_149731_6_);
	}

}
