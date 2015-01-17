package me.kemal.randomp.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.kemal.randomp.RandomPeripheral;
import me.kemal.randomp.client.renderer.RendererHologramProjector;
import me.kemal.randomp.te.TileHologramProjector;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHologramProjector extends Block implements ITileEntityProvider {

	public final static String blockName = "hologramProjector";

	public static IIcon topIcon;
	public static IIcon bottomIcon;
	public static IIcon sideIcon;

	public BlockHologramProjector() {
		super(Material.circuits);
		setBlockName(blockName);
		setBlockBounds(0.f, 0.f, 0.f, 1.f, 0.6f, 1.f);
		setCreativeTab(RandomPeripheral.tabRandomP);

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
	public int getRenderType() {
		return RendererHologramProjector.id;
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
	public int getLightValue() {
		return 12;
	}

}
