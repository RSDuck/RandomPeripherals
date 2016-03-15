package me.kemal.randomp.block;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.client.renderer.RendererHologramProjector;
import me.kemal.randomp.client.renderer.TileHologramSpecialRenderer;
import me.kemal.randomp.te.TileHologramProjector;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockHologramProjector extends Block implements ITileEntityProvider {

	public final static String blockName = "hologramProjector";

	public static IIcon topIcon;
	public static IIcon bottomIcon;
	public static IIcon sideIcon;

	public BlockHologramProjector() {
		super(Material.circuits);
		setBlockName(blockName);
		setBlockBounds(0.f, 0.f, 0.f, 1.f, 0.6f, 1.f);
		setHardness(2.f);
		setCreativeTab(RandomPeripherals.tabRandomP);

		GameRegistry.registerBlock(this, blockName);
	}

	@Override
	public void registerBlockIcons(IIconRegister ir) {
		topIcon = ir.registerIcon("randomperipherals:holoTop");
		bottomIcon = ir.registerIcon("randomperipherals:holoBottom");
		sideIcon = ir.registerIcon("randomperipherals:holoSide");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		switch (side) {
		case 0:
			return bottomIcon;
		case 1:
			return topIcon;
		default:
			return sideIcon;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileHologramProjector();
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
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return side == ForgeDirection.DOWN ? true : false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return world.isAirBlock(x, y + 1, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if (!world.isRemote) {
			world.setBlock(x, y + 1, z, RandomPeripherals.blockHologram, 0, 1 | 2);
			world.markBlockForUpdate(x, y + 1, z);
			world.markBlockRangeForRenderUpdate(x, y + 1, z, 1, 1, 1);
		}
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int oldMeta) {
		if (!world.isRemote) {
			world.setBlockToAir(x, y + 1, z);
		} else if (world.getTileEntity(x, y, z) instanceof TileHologramProjector) {
			GLAllocation.deleteDisplayLists(((TileHologramProjector) world.getTileEntity(x, y, z)).getDisplayListID());
		}
	}
}
