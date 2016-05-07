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
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.gui.RandomPGUIs;
import me.kemal.randomp.net.Packets;
import me.kemal.randomp.net.RandomPMessage;
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
	private IIcon universalFace, itemFace, energyFace, fluidFace, neutralFace, fluidFaceOutput;
	private IIcon universalFace_faceDir, itemFace_faceDir, energyFace_faceDir, fluidFace_faceDir, neutralFace_faceDir,
			fluidFaceOutput_faceDir;

	public final static String blockName = "universalInterface";

	public BlockUniversalInterface(Material mat) {
		super(mat);
		setBlockName(blockName);
		setCreativeTab(RandomPeripherals.tabRandomP);
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
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z,
			boolean returnDrops) {
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
			tile.setFacing(rotation);
			tile.setSide(rotation, TileUniversalInterface.SIDE_IO);

			if (!world.isRemote)
				onNeighborBlockChange(world, x, y, z, Blocks.air);
		}
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX,
			float clickY, float clickZ) {
		if (!player.isSneaking()) {
			if (player.inventory.getCurrentItem() != null) {
				if (player.inventory.getCurrentItem().getItem() instanceof IToolHammer) {
					if (world.isRemote)
						Packets.sendToServer(Packets.RotateBlock, world.getTileEntity(x, y, z));
					return true;
				}
			}
			if (world.getTileEntity(x, y, z) != null) {
				player.openGui(RandomPeripherals.instance, RandomPGUIs.GUI_UNIVERSALINTERFACE.ordinal(), world, x, y,
						z);
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
		fluidFaceOutput = ir.registerIcon("randomperipherals:uiFluidFaceOutput");

		itemFace_faceDir = ir.registerIcon("randomperipherals:uiItemFace_faceDir");
		energyFace_faceDir = ir.registerIcon("randomperipherals:uiEnergyFace_faceDir");
		universalFace_faceDir = ir.registerIcon("randomperipherals:uiIOFace_faceDir");
		neutralFace_faceDir = ir.registerIcon("randomperipherals:uiNeutralFace_faceDir");
		fluidFace_faceDir = ir.registerIcon("randomperipherals:uiFluidFace_faceDir");
		fluidFaceOutput_faceDir = ir.registerIcon("randomperipherals:uiFluidFaceOutput_faceDir");
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
			if (side != te.getFacing()) {
				switch (te.getSide(side)) {
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
				case 5:
					return fluidFaceOutput;
				default:
					return neutralFace;
				}
			} else {
				switch (te.getSide(side)) {
				case 0:
					return neutralFace_faceDir;
				case 1:
					return universalFace_faceDir;
				case 2:
					return itemFace_faceDir;
				case 3:
					return energyFace_faceDir;
				case 4:
					return fluidFace_faceDir;
				case 5:
					return fluidFaceOutput_faceDir;
				default:
					return neutralFace_faceDir;
				}
			}
		}
		return neutralFace;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
		// RandomPeripherals.logger.info("onNeightborChange " + x + ", " + y, ",
		// " + z);
		TileEntity uiTE = world.getTileEntity(x, y, z);
		if (uiTE instanceof TileUniversalInterface)
			((TileUniversalInterface) uiTE).addNeightborCache(world.getTileEntity(tileX, tileY, tileZ), tileX, tileY,
					tileZ);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		//RandomPeripherals.logger.info("onNeightborBlockChange" + x + ", " + y, ", " + z);
		TileUniversalInterface tile = (TileUniversalInterface) world.getTileEntity(x, y, z);
		for (int i = 0; i < 6; i++) {
			ForgeDirection current = ForgeDirection.getOrientation(i);

			tile.addNeightborCache(world.getTileEntity(current.offsetX + x, current.offsetY + y, current.offsetZ + z),
					x + current.offsetX, y + current.offsetY, z + current.offsetZ);
		}
	}

	@Override
	public void debugBlock(IBlockAccess world, int x, int y, int z, ForgeDirection side, EntityPlayer player) {
		TileUniversalInterface te = (TileUniversalInterface) world.getTileEntity(x, y, z);
		if (te != null && te.getTank().getFluid() != null)
			player.addChatMessage(new ChatComponentText("Fluid: " + te.getTank().getFluidAmount() + " Name: "
					+ te.getTank().getFluid().getLocalizedName()));
		player.addChatMessage(new ChatComponentText("Energy: Receive: " + te.getEnergyStorage().getMaxReceive()
				+ " Extract: " + te.getEnergyStorage().getMaxReceive() + " Stored: "
				+ te.getEnergyStorage().getEnergyStored()));
	}
}
