package me.kemal.randomp.block;

import java.util.ArrayList;

import cofh.api.block.IBlockDebug;
import cofh.api.block.IDismantleable;
import cofh.api.energy.IEnergyHandler;
import cofh.api.item.IToolHammer;
import cofh.lib.util.helpers.MathHelper;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.Action;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.kemal.randomp.RandomPeripheral;
import me.kemal.randomp.gui.RandomPGUIs;
import me.kemal.randomp.te.TileEnergyStorage;
import me.kemal.randomp.te.TileUniversalInterface;
import me.kemal.randomp.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class BlockUniversalInterface extends Block implements ITileEntityProvider, IDismantleable, IBlockDebug {
	@SideOnly(Side.CLIENT)
	private IIcon universalFace, itemFace, energyFace, fluidFace, neutralFace;

	public String blockName = "universalInterface";

	public BlockUniversalInterface(Material mat) {
		super(mat);
		setBlockName(blockName);
		setCreativeTab(CreativeTabs.tabRedstone);
		GameRegistry.registerBlock(this, blockName);
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileUniversalInterface();
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {
		ArrayList<ItemStack> list = getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		world.setBlockToAir(x, y, z);
		if (!returnDrops)
			for (ItemStack item : list)
				dropBlockAsItem(world, x, y, z, item);
		return list;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		if (world.getTileEntity(x, y, z) instanceof TileUniversalInterface) {
			int rotation = BlockPistonBase.determineOrientation(world, x, y, z, entity);
			TileUniversalInterface tile = (TileUniversalInterface) world.getTileEntity(x, y, z);
			tile.setOutputFaceDir(rotation);
			tile.setIOConfiguration(rotation, TileUniversalInterface.SIDE_IO);
		}
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ) {
		if (!player.isSneaking()) {
			if (player.inventory.getCurrentItem() != null) {
				if (player.inventory.getCurrentItem().getItem() instanceof IToolHammer) {
					return true;
				}
			}
			if (world.getTileEntity(x, y, z) != null) {
				player.openGui(RandomPeripheral.instance, RandomPGUIs.GUI_UNIVERSALINTERFACE.ordinal(), world, x, y, z);
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		itemFace = ir.registerIcon("randomperipherals:uiItemFace");
		energyFace = ir.registerIcon("randomperipherals:uiEnergyFace");
		universalFace = ir.registerIcon("randomperipherals:uiIOFace");
		neutralFace = ir.registerIcon("randomperipherals:uiNeutralFace");
		fluidFace = ir.registerIcon("randomperipherals:uiFluidFace");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return neutralFace;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		if (world.getTileEntity(x, y, z) instanceof TileUniversalInterface) {
			TileUniversalInterface te = (TileUniversalInterface) world.getTileEntity(x, y, z);
			switch (te.getIOConfigurationWithFacing(side)) {
				case 0:
					return neutralFace;
				case 1:
					return universalFace;
				case 2:
					return itemFace;
				case 3:
					return energyFace;
				case 4:
					return fluidFace;
				default:
					return neutralFace;
			}
		}
		return neutralFace;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
		TileEnergyStorage tile = (TileEnergyStorage) world.getTileEntity(x, y, z);
		tile.addNeightborCache(world.getTileEntity(tileX, tileY, tileZ), tileX, tileY, tileZ);
	}

	@Override
	public void debugBlock(IBlockAccess world, int x, int y, int z, ForgeDirection side, EntityPlayer player) {
		TileUniversalInterface te = (TileUniversalInterface) world.getTileEntity(x, y, z);
		if (te != null && te.getTank().getFluid() != null)
			player.addChatMessage(new ChatComponentText("Fluid: " + te.getTank().getFluidAmount() + " Name: " + te.getTank().getFluid().getLocalizedName()));
		player.addChatMessage(new ChatComponentText("Energy: Receive: " + te.getEnergyStorage().getMaxReceive() + " Extract: "
				+ te.getEnergyStorage().getMaxReceive() + " Stored: " + te.getEnergyStorage().getEnergyStored()));
		// player.addChatMessage(new ChatComponentText(""));
	}
}
