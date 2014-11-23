package me.kemal.randomp.te;

import me.kemal.randomp.RandomPeripheral;
import me.kemal.randomp.util.CCUtil;
import me.kemal.randomp.util.ICCHelpCreator;
import me.kemal.randomp.util.Util;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkStatistics.PacketStat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedTexture;
import cofh.lib.util.helpers.BlockHelper;
import cpw.mods.fml.common.SidedProxy;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

//TODO: Mehr Unter- und Unterklassen
public class TileUniversalInterface extends TileEnergyStorage implements ISidedInventory, ICCHelpCreator,
		IReconfigurableFacing, IReconfigurableSides, ISidedTexture, IFluidHandler {
	public static final int capacity = 400000;

	public static final int LEADSTONE_CAPACITY = 40000;

	public static final int SIDE_NEUTRAL = 0;
	public static final int SIDE_IO = 1;
	public static final int SIDE_ITEMS_ONLY = 2;
	public static final int SIDE_ENERGY_ONLY = 3;
	public static final int SIDE_FLUID_ONLY = 4;
	public static final int SIDES_COUNT = 5;

	private ItemStack heldStack;
	private int outputDirection;
	private int[] ioConfiguration;
	private boolean allowAutoInput;
	private FluidTank tank;

	public TileUniversalInterface() {
		super(capacity);
		heldStack = null;
		outputDirection = 1;
		allowAutoInput = true;
		ioConfiguration = new int[] { SIDE_NEUTRAL, // Unten
				SIDE_NEUTRAL, // Oben
				SIDE_NEUTRAL, // Norden
				SIDE_NEUTRAL, // Sueden
				SIDE_NEUTRAL, // West
				SIDE_NEUTRAL // Osten
		};
		tank = new FluidTank(4000);
	}

	// TODO: Eigene Klasse fürs Packet Handling schreiben
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
	public void updateEntity() {
		super.updateEntity();
		//TODO: clean up!
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (neightborCache[getIOConfiguration(dir.ordinal())] != null
					&& neightborCache[getIOConfiguration(dir.ordinal())] instanceof IEnergyHandler
					&& (getIOConfiguration(dir.getOpposite().ordinal()) == SIDE_IO || getIOConfiguration(dir
							.getOpposite().ordinal()) == SIDE_ENERGY_ONLY)) {
				int energy = storedEnergy.getEnergyStored();
				if (((IEnergyHandler) neightborCache[getIOConfiguration(dir.ordinal())]).receiveEnergy(dir,
						storedEnergy.getMaxExtract(), true) > 0)
					energy -= ((IEnergyHandler) neightborCache[getIOConfiguration(dir.ordinal())]).receiveEnergy(dir,
							storedEnergy.getMaxExtract(), false);
				if (energy <= 0)
					energy = 0;
				this.storedEnergy.setEnergyStored(energy);
				// RandomPeripheral.logger.info("Trying to insert energy from "
				// + dir.toString() + " side. " + energy
				// + " RF succefull transfered");
			}
			if (neightborCache[getIOConfiguration(dir.ordinal())] != null
					&& neightborCache[getIOConfiguration(dir.ordinal())] instanceof IFluidHandler
					&& this.tank.getFluid() != null
					&& (getIOConfiguration(dir.getOpposite().ordinal()) == SIDE_IO || getIOConfiguration(dir
							.getOpposite().ordinal()) == SIDE_FLUID_ONLY)) {
				IFluidHandler fluidHandler = (IFluidHandler) neightborCache[getIOConfiguration(dir.ordinal())];
				if (fluidHandler.canFill(dir, tank.getFluid().getFluid())) {
					final int maxDrain = 100;
					FluidStack fluidToDrain = this.drain(dir.getOpposite(), maxDrain, false);
					int maxFill = fluidHandler.fill(dir, fluidToDrain, false);
					FluidStack drainedFluid = this.drain(dir.getOpposite(), maxFill, true);
					fluidHandler.fill(dir, drainedFluid, true);
				}
			}
		}
		this.markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		heldStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("heldStack"));
		tank = tank.readFromNBT(tag.getCompoundTag("tank"));
		outputDirection = tag.getInteger("outputDir");
		ioConfiguration = tag.getIntArray("configuration");
		allowAutoInput = tag.getBoolean("allowAutoInput");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("outputDir", (byte) outputDirection);
		tag.setIntArray("configuration", ioConfiguration);
		if (heldStack != null) {
			NBTTagCompound heldStackTag = new NBTTagCompound();
			heldStack.writeToNBT(heldStackTag);
			tag.setTag("heldStack", heldStackTag);
		}
		NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		tag.setTag("tank", tankTag);
		tag.setBoolean("allowAutoInput", allowAutoInput);
	}

	@Override
	public String getType() {
		return "UniversalInterface";
	}

	// TODO: Computercraft Funktions Namen aufräumen
	@Override
	public String[] getMethodNames() {
		return new String[] { "help", "getHeldStack", "suckStack", "pushStack", "setMaxEnergyOutput",
				"getEnergyMaxOutput", "getStoredEnergy", "getMaxEnergyStored", "setAllowAutoInput",
				"isAutoInputAllowed", "setSideConfiguration", "getSideConfiguration", "setMaxEnergyInput",
				"getMaxEnergyInput" };
	}

	@Override
	public String[] getMethodValues() {
		return null;
	}

	@Override
	public String[] getFunctionDescription() {
		return null;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		switch (method) {
			case 0: { // help
				return new Object[] { "At this time there is no help available" };
			}
			case 1: {// getHeldStack
				return new Object[] { CCUtil.stackToMap(heldStack) };
			}
			case 2: {// suckStack
				if (arguments.length == 1 || arguments.length == 2) {
					if (CCUtil.IsValidNumber(arguments[0])) {
						if (CCUtil.ToInt(arguments[0]) >= 0 && CCUtil.ToInt(arguments[0]) < 6) {
							if (arguments.length == 2)
								if (CCUtil.IsValidNumber(arguments[1])) {
									if (!(CCUtil.ToInt(arguments[1]) >= 0 && CCUtil.ToInt(arguments[1]) < 6)) {
										return new Object[] { false };
									}
								} else
									return new Object[] { false };
							int[] pos = CCUtil.DirToCoord(CCUtil.ToInt(arguments[0]));
							TileEntity te = worldObj.getTileEntity(xCoord + pos[0], yCoord + pos[1], zCoord + pos[2]);
							boolean sided = te instanceof ISidedInventory;
							if (te instanceof IInventory) {
								int side = (arguments.length == 1) ? ForgeDirection.values()[CCUtil.ToInt(arguments[0])]
										.getOpposite().ordinal() : CCUtil.ToInt(arguments[1]);
								IInventory inv = (IInventory) te;
								int invSize = (sided) ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side).length
										: inv.getSizeInventory();
								int[] slots = (sided) ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side)
										: new int[] {};
								for (int i = 0; i < invSize; i++) {
									int slot = (sided) ? slots[i] : i;
									if (inv.getStackInSlot(slot) == null)
										continue;
									int decr = CCUtil.CanStack(heldStack, inv.getStackInSlot(slot));
									if (sided) {
										if (!(((ISidedInventory) inv).canExtractItem(slot, inv.getStackInSlot(slot),
												side))) {
											continue;
										}
									}
									if (decr != -1) {
										ItemStack decrStack = inv.decrStackSize(slot, decr);
										if (heldStack != null)
											heldStack.stackSize += decrStack.stackSize;
										else
											heldStack = decrStack;
										return new Object[] { CCUtil.stackToMap(decrStack) };
									}
								}
							}
						}
					}
				}
				return new Object[] { false };
			}
			case 3: {// pushStack
				if (arguments.length == 1 || arguments.length == 2) {
					if (CCUtil.IsValidNumber(arguments[0])) {
						if (arguments.length == 2)
							if (CCUtil.IsValidNumber(arguments[1])) {
								if (!(CCUtil.ToInt(arguments[1]) >= 0 && CCUtil.ToInt(arguments[1]) < 6)) {
									return new Object[] { false };
								}
							} else
								return new Object[] { false };
						if (heldStack == null)
							return new Object[] { false };
						int[] pos = CCUtil.DirToCoord(CCUtil.ToInt(arguments[0]));
						TileEntity te = worldObj.getTileEntity(xCoord + pos[0], yCoord + pos[1], zCoord + pos[2]);
						boolean sided = te instanceof ISidedInventory;
						if (te instanceof IInventory) {
							IInventory inv = (IInventory) te;
							int side = (arguments.length == 1) ? ForgeDirection.values()[CCUtil.ToInt(arguments[0])]
									.getOpposite().ordinal() : CCUtil.ToInt(arguments[1]);
							int invSize = (sided) ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side).length
									: inv.getSizeInventory();
							int[] slots = (sided) ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side)
									: new int[] {};
							for (int i = 0; i < invSize; i++) {
								int slot = (sided) ? slots[i] : i;
								int stackable = CCUtil.CanStack(inv.getStackInSlot(slot), heldStack);
								if (sided) {
									if (!((ISidedInventory) inv).canInsertItem(slot, heldStack, side)) {
										continue;
									}
								}
								if (stackable != -1) {
									ItemStack decrStack = decrStackSize(0, stackable);
									if (inv.getStackInSlot(slot) != null) {
										inv.getStackInSlot(slot).stackSize += decrStack.stackSize;
									} else
										inv.setInventorySlotContents(slot, decrStack);
									return new Object[] { CCUtil.stackToMap(decrStack) };
								}
							}
						}

					}
				}
				return new Object[] { false };
			}
			case 4: {// setEnergyMaxExtract
				if (arguments.length == 1 && CCUtil.IsValidNumber(arguments[0])) {
					if (CCUtil.ToInt(arguments[0]) <= 10000 && CCUtil.ToInt(arguments[0]) >= 0
							&& CCUtil.ToInt(arguments[0]) % 10 == 0) {
						storedEnergy.setMaxExtract(CCUtil.ToInt(arguments[0]));
						return new Object[] { true };
					}
				}
				return new Object[] { false };
			}
			case 5: {// getEnergyMaxExtract
				return new Object[] { storedEnergy.getMaxExtract() };
			}
			case 6: {// getStoredEnergy
				return new Object[] { storedEnergy.getEnergyStored() };
			}
			case 7: {// getMaxEnergyStored
				return new Object[] { storedEnergy.getMaxEnergyStored() };
			}
			case 8: {// setAllowAutoInput
				if (arguments.length == 1 && CCUtil.IsValidBool(arguments[0])) {
					allowAutoInput = CCUtil.ToBool(arguments[0]);
					return new Object[] { true };
				}
				return new Object[] { false };
			}
			case 9: {// isAutoInputEnabled
				return new Object[] { allowAutoInput };
			}
			case 10: {// setSideConfiguration
				if (arguments.length == 2 && CCUtil.IsValidNumber(arguments[0]) && CCUtil.IsValidNumber(arguments[1])) {
					if (CCUtil.ToInt(arguments[0]) < 6 && CCUtil.ToInt(arguments[1]) < SIDES_COUNT) {
						setSide(CCUtil.ToInt(arguments[0]), CCUtil.ToInt(arguments[1]));
						updateAllBlocks();
						return new Object[] { true };
					}
				}
				return new Object[] { false };
			}
			case 11: {// getSideConfiguration
				return new Object[] { Util.getRealNBTType(new NBTTagIntArray(ioConfiguration)) };
			}
			case 12: {// setEnergyMaxReceive
				if (arguments.length == 1 && CCUtil.IsValidNumber(arguments[0])) {
					if (CCUtil.ToInt(arguments[0]) <= 1000 && CCUtil.ToInt(arguments[0]) >= 0
							&& CCUtil.ToInt(arguments[0]) % 10 == 0) {
						storedEnergy.setMaxReceive(CCUtil.ToInt(arguments[0]));
						return new Object[] { true };
					}
				}
				return new Object[] { false };
			}
			case 13: { // getEnergyMaxReceive
				return new Object[] { storedEnergy.getMaxReceive() };
			}
			default:
				break;
		}
		return new Object[] { null };
	}

	public void updateAllBlocks() {
		worldObj.markBlockForUpdate(xCoord + 1, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord - 1, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord + 1, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord - 1, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord + 1);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord - 1);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void attach(IComputerAccess computer) {
	}

	@Override
	public void detach(IComputerAccess computer) {
	}

	@Override
	public boolean equals(IPeripheral other) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return heldStack;
	}

	@Override
	public ItemStack decrStackSize(int slot, int numOfDecr) {
		if (heldStack != null) {
			if (heldStack.stackSize < numOfDecr) {
				ItemStack stack = heldStack;
				heldStack = null;
				this.markDirty();
				return stack;
			}
			ItemStack stack = heldStack.splitStack(numOfDecr);
			if (heldStack.stackSize == 0)
				heldStack = null;
			this.markDirty();
			return stack;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		heldStack = stack;
	}

	@Override
	public String getInventoryName() {
		return "UniversalInterface";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	public int getOutputFaceDir() {
		return outputDirection;
	}

	public void setOutputFaceDir(int dir) {
		outputDirection = dir;
	}

	public void setIOConfiguration(int side, int configuration) {
		setSide(side, configuration);
	}

	public int getIOConfiguration(int side) {
		return getIOConfigurationWithFacing(side);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int slot) {
		return new int[] { 0 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return (getIOConfigurationWithFacing(side) == SIDE_IO || getIOConfigurationWithFacing(side) == SIDE_ITEMS_ONLY)
				&& allowAutoInput;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return (getIOConfigurationWithFacing(side) == SIDE_IO || getIOConfigurationWithFacing(side) == SIDE_ITEMS_ONLY);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (getIOConfigurationWithFacing(from.ordinal()) == SIDE_IO
				|| getIOConfigurationWithFacing(from.ordinal()) == SIDE_ENERGY_ONLY)
			return super.extractEnergy(from, maxExtract, simulate);
		return 0;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (getIOConfigurationWithFacing(from.ordinal()) == SIDE_IO
				|| getIOConfigurationWithFacing(from.ordinal()) == SIDE_ENERGY_ONLY)
			return super.receiveEnergy(from, maxReceive, simulate);
		return 0;
	}

	@Override
	public boolean decrSide(int side) {
		// int conf = getIOConfigurationWithFacing(side);
		ioConfiguration[BlockHelper.ICON_ROTATION_MAP[outputDirection][side]] = (ioConfiguration[BlockHelper.ICON_ROTATION_MAP[outputDirection][side]] - 1 > -1) ? ioConfiguration[BlockHelper.ICON_ROTATION_MAP[outputDirection][side]] - 1
				: 3;
		updateAllBlocks();
		return true;
	}

	@Override
	public boolean incrSide(int side) {
		// int conf = getIOConfigurationWithFacing(side);
		ioConfiguration[BlockHelper.ICON_ROTATION_MAP[outputDirection][side]] = (ioConfiguration[BlockHelper.ICON_ROTATION_MAP[outputDirection][side]] + 1 < SIDES_COUNT) ? ioConfiguration[BlockHelper.ICON_ROTATION_MAP[outputDirection][side]] + 1
				: 0;
		updateAllBlocks();
		return true;
	}

	@Override
	public boolean setSide(int side, int config) {
		// int conf = getIOConfiguration(side);
		ioConfiguration[BlockHelper.ICON_ROTATION_MAP[outputDirection][side]] = config;
		updateAllBlocks();
		return true;
	}

	@Override
	public boolean resetSides() {
		for (int i = 0; i < ioConfiguration.length; i++)
			ioConfiguration[i] = 0;
		updateAllBlocks();
		return true;
	}

	@Override
	public int getNumConfig(int side) {
		return SIDES_COUNT;
	}

	@Override
	public int getFacing() {
		return outputDirection;
	}

	@Override
	public boolean allowYAxisFacing() {
		return true;
	}

	@Override
	public boolean rotateBlock() {
		outputDirection++;
		if (outputDirection > 5)
			outputDirection = 0;
		updateAllBlocks();
		return true;
	}

	@Override
	public boolean setFacing(int side) {
		outputDirection = side;
		updateAllBlocks();
		return true;
	}

	public int getIOConfigurationWithFacing(int side) {
		return ioConfiguration[BlockHelper.ICON_ROTATION_MAP[outputDirection][side]];
	}

	@Override
	public IIcon getTexture(int side, int pass) {
		return this.blockType.getIcon(worldObj, xCoord, yCoord, zCoord, side);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		// RandomPeripheral.logger.info("fill(from=" + from.toString() +
		// ",resource=" + resource.toString() + ",doFill="
		// + doFill + ") IO Configuration with Facing = " +
		// getIOConfiguration(from.ordinal()));
		if (getIOConfiguration(from.ordinal()) == SIDE_FLUID_ONLY || getIOConfiguration(from.ordinal()) == SIDE_IO)
			return tank.fill(resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (getIOConfiguration(from.ordinal()) == SIDE_FLUID_ONLY || getIOConfiguration(from.ordinal()) == SIDE_IO)
			return tank.drain(resource.amount, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (getIOConfiguration(from.ordinal()) == SIDE_FLUID_ONLY || getIOConfiguration(from.ordinal()) == SIDE_IO)
			return tank.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return tank.getCapacity() == tank.getFluidAmount()
				&& (getIOConfiguration(from.ordinal()) == SIDE_FLUID_ONLY || getIOConfiguration(from.ordinal()) == SIDE_IO);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return tank.getFluidAmount() > 0 && getIOConfiguration(from.ordinal()) == SIDE_IO
				|| getIOConfiguration(from.ordinal()) == SIDE_FLUID_ONLY;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (getIOConfiguration(from.ordinal()) == SIDE_FLUID_ONLY || getIOConfiguration(from.ordinal()) == SIDE_IO)
			return new FluidTankInfo[] { tank.getInfo() };
		return new FluidTankInfo[] {};
	}

	public FluidTank getTank() {
		return tank;
	}

}
