package me.kemal.randomp.block;

import cofh.api.block.IBlockDebug;
import cpw.mods.fml.common.registry.GameRegistry;
import me.kemal.randomp.te.TileEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockDebugPeripheral extends Block implements ITileEntityProvider, IBlockDebug {
	public static final String blockName = "debugPeripheral";

	public BlockDebugPeripheral(Material mat) {
		super(mat);
		setBlockName(blockName);
		GameRegistry.registerBlock(this, blockName);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return Blocks.daylight_detector.getIcon(1, 0);
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEnergyStorage(1000);
	}

	@Override
	public void debugBlock(IBlockAccess world, int x, int y, int z, ForgeDirection side, EntityPlayer player) {
		
	}
}
