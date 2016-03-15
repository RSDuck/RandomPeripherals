package me.kemal.randomp.te;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.print.attribute.standard.Copies;

import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.util.CCType;
import me.kemal.randomp.util.CCUtils;
import me.kemal.randomp.util.ComputerUIWrapDummy;
import me.kemal.randomp.util.FunctionNotFoundException;
import me.kemal.randomp.util.Peripheral;
import me.kemal.randomp.util.PeripheralLuaObjectWrap;
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
import net.minecraft.world.World;
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
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ItemHelper;
import cpw.mods.fml.common.SidedProxy;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;

public class TileUniversalInterface extends TileEnergyStorage implements ISidedInventory, IFluidHandler {
	public static final int capacity = 400000;
	public static final int fluid_capacity = 4000;

	public static final int MAX_ENERGY_IO = 1000;
	public static final int MAX_FLUID_IO = 100;

	public static final int SIDE_NEUTRAL = 0;
	public static final int SIDE_IO = 1;
	public static final int SIDE_ITEMS_ONLY = 2;
	public static final int SIDE_ENERGY_ONLY = 3;
	public static final int SIDE_FLUID_INPUT_ONLY = 4;
	public static final int SIDE_FLUID_OUTPUT_ONLY = 5;

	final String[] dirs = new String[] { "bottom", "top", "front", "back", "left", "right" };

	private ItemStack heldStack;
	private boolean allowAutoInput;
	private FluidTank tank;

	private ArrayList<IComputerAccess> attachedComputer;

	public IPeripheral[] connectedPeripherals;

	private static Class<?> computerCraft;
	private static Method cc_getPeripheralAt;

	private static boolean ccFound = false;

	public static void findCC() {
		if (ccFound == false)
			try {
				computerCraft = Class.forName("dan200.computercraft.ComputerCraft");

				cc_getPeripheralAt = computerCraft.getDeclaredMethod("getPeripheralAt", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE,
						Integer.TYPE);
				ccFound = true;
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
	}

	public static IPeripheral getPeripheralAt(World world, int x, int y, int z, int side) {
		findCC();
		if (ccFound && cc_getPeripheralAt != null) {
			try {
				return (IPeripheral) cc_getPeripheralAt.invoke(null, world, x, y, z, side);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public TileUniversalInterface() {
		super(capacity);
		heldStack = null;
		allowAutoInput = true;
		ioConfiguration = new int[] { SIDE_NEUTRAL, // Unten
				SIDE_NEUTRAL, // Oben
				SIDE_NEUTRAL, // Norden
				SIDE_NEUTRAL, // Sueden
				SIDE_NEUTRAL, // West
				SIDE_NEUTRAL // Osten
		};
		tank = new FluidTank(fluid_capacity);
		peripheral.setType("universalInterface");
		peripheral.setDescription(
				"The Universally Interface in an Computer controlled proxy for item, fluids and energy. The Fluid Output side automaticlly outputs fluids in adjecent containers");
		peripheral.addMethod("getHeldStack", "Returns the current hold stack", new CCType[] {},
				new CCType[] { new CCType(HashMap.class, "An table which holds informations about the item, or nil if no item is in inventory") },
				this);
		peripheral.addMethod("pushStack",
				"Pushs the currently holded stack into an external inventory. This function works inpendently from the side config",
				new CCType[] {
						new CCType(String.class, "direction",
								"The direction, where the items should be pushed(valid inputs: front, back, left, right, top, bottom)"),
						new CCType(String.class, "simulatedDir",
								"Only used if you want to simulate that the item was push from an specific direction, if you don't simply insert in this argument a empty string then it will use the real direction from where the items come(valid inputs: [a empty string], bottom, top, east, west, south, north)"),
						new CCType(Double.class, "amount",
								"The maximum amount of items you want to tranfer. If you give 0 or less it will simply take the current stack size") },
				new CCType[] { new CCType(Boolean.class, "True if items where transfered, otherwise false"), }, this);
		peripheral.addMethod("suckStack",
				"Pulls the first stack it gets from an inventory which stands on the given side into it's own inventory. This function works inpendently from the side config",
				new CCType[] {
						new CCType(String.class, "direction",
								"The direction from where the items should be sucked(valid inputs: front, back, left, right, top, bottom)"),
						new CCType(String.class, "simulatedDir",
								"Only used if you want to simulate that the item was pulled from an specific direction, if you don't simply insert in this argument a empty string then it will use the real direction from where the items come(valid inputs: [a empty string], bottom, top, east, west, south, north"),
						new CCType(Double.class, "amount",
								"The maximum amount of items that will be sucked from the inventory. If you give 0 or less it will pulls one stack of the first item it finds") },
				new CCType[] { new CCType(Boolean.class, "True if items where transfered, otherwise false"),
						new CCType(HashMap.class, "The pulled stack, or nil if nothing could be pulled") },
				this);
		peripheral.addMethod("setAllowAutoInput", "Sets if the Universal Interface allows that other blocks insert items",
				new CCType[] { new CCType(Boolean.class, "The new value") }, new CCType[] {}, this);
		peripheral.addMethod("isAutoInputAllowed", "Returns if the Universal Interface allows that other blocks insert items", new CCType[] {},
				new CCType[] { new CCType(Boolean.class, "True if it is allowed") }, this);
		peripheral.addMethod("setSideConfiguration", "Sets the IO configuration on a specific side",
				new CCType[] {
						new CCType(String.class, "side",
								"The side where the configuration should be changed(valid inputs: front, back, right, left, top, bottom)"),
						new CCType(String.class, "config",
								"The new configuration (valid inputs: neutral, universal, items, energy, fluidInput, fluidOutput)") },
				new CCType[] {}, this);
		peripheral.addMethod("getSideConfiguration", "Returns an table which contains the IO configuration", new CCType[] {},
				new CCType[] { new CCType(HashMap.class, "A table which is in the format { \"side\" : \"configuration\" }") }, this);
		peripheral.addMethod("getTankInfo", "Returns information about the current held fluid and it's amount", new CCType[] {}, new CCType[] {
				new CCType(HashMap.class, "An table which contains informations about the internal fluid tank or nil, if the tank is empty") }, this);
		peripheral
				.addMethod("fillFluid",
						"Fills the specified amount of fluid into an container on the given side. This function works inpendently from the side config",
						new CCType[] {
								new CCType(String.class, "side",
										"The side where the fluid container is standing(valid inputs: front, back, right, left, top, bottom)"),
								new CCType(String.class, "simulatedDir",
										"If the inventory requires that the fluid should enter from an specific side you can use this argument. Leafe a empty string to just take the direction where the UI is located. Valid inputs: [empty string], bottom, top, east, west, south, north"),
								new CCType(Double.class, "amount",
										"The amount of fluid in milibuckets(1000 mb = 1 Bucket). Has to be more than 0 and " + fluid_capacity
												+ " or less",
										1, fluid_capacity),
								new CCType(Boolean.class, "cancelOnError",
										"If true, no fluid will be inserted if there is not enough place in the target or not enogh fluid there. Usefull to prevent that half of the fluid is already inserted while the other half is still in the UI") },
						new CCType[] { new CCType(Boolean.class, "sucessfull",
								"True, if some fluid was sucessfull transfered or if cancelOnError true is if the whole specified amount of fluid was sucessfully transferred") },
						this);
		peripheral
				.addMethod("drainFluid",
						"Drains fluid of a fluid container on the specified side. This function works inpendently from the side config",
						new CCType[] {
								new CCType(String.class, "side",
										"The side where the fluid container is standing(valid inputs: front, back, right, left, top, bottom)"),
								new CCType(String.class, "simulatedDir",
										"If the inventory requires that the fluid should enter from an specific side you can use this argument. Leafe a empty string to just take the direction where the UI is located. Valid inputs: [empty string], bottom, top, east, west, south, north"),
								new CCType(Double.class, "amount",
										"The amount of fluid in milibuckets(1000 mb = 1 Bucket). Has to be more than 0 and " + fluid_capacity
												+ " or less",
										1, fluid_capacity),
								new CCType(Boolean.class, "cancelOnError",
										"If true, no fluid will be drained if there is not enough place in the UI or not enough fluid there. Usefull to prevent that half of the fluid is already drained while the other half is still in the other container") },
						new CCType[] {
								new CCType(Boolean.class, "sucessfull",
										"True, if some fluid was sucessfull transfered or if cancelOnError true is if the whole specified amount of fluid was sucessfully transferred"),
								new CCType(HashMap.class, "drainedFluid", "The Informations about what fluid was drained and it's amount") },
						this);
		peripheral.addMethod("wrapPeripheral",
				"Works exactly like peripheral.wrap(side), the only different is that it works based of the Unversal Interface position and rotation",
				new CCType[] { new CCType(String.class, "side", "The side where the peripheral is located") },
				new CCType[] { new CCType(ILuaObject.class, "The wrapped peripheral") }, this);

		connectedPeripherals = new IPeripheral[6];

		attachedComputer = new ArrayList<IComputerAccess>();

	}

	boolean initialPeripheralSearchCompleted = false;

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!initialPeripheralSearchCompleted) {
			// RandomPeripherals.logger.info("Initial Peripheral search
			// started");
			for (int i = 0; i < 6; i++) {
				ForgeDirection current = ForgeDirection.getOrientation(i);

				addNeightborCache(worldObj.getTileEntity(current.offsetX + xCoord, current.offsetY + yCoord, current.offsetZ + zCoord),
						xCoord + current.offsetX, yCoord + current.offsetY, zCoord + current.offsetZ);
			}
			initialPeripheralSearchCompleted = true;
		}

		// TODO: clean up!
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			storedEnergy.setMaxExtract(getMaxEnergyOutput(dir.getOpposite().ordinal()));
			storedEnergy.setMaxReceive(getMaxEnergyInput(dir.getOpposite().ordinal()));
			if (neightborCache[(dir.ordinal())] != null && neightborCache[(dir.ordinal())] instanceof IEnergyHandler
					&& (getSide(dir.getOpposite().ordinal()) == SIDE_IO || getSide(dir.getOpposite().ordinal()) == SIDE_ENERGY_ONLY)) {
				int energy = storedEnergy.getEnergyStored();
				if (((IEnergyHandler) neightborCache[(dir.ordinal())]).receiveEnergy(dir, storedEnergy.getMaxExtract(), true) > 0)
					energy -= ((IEnergyHandler) neightborCache[(dir.ordinal())]).receiveEnergy(dir, storedEnergy.getMaxExtract(), false);
				if (energy <= 0)
					energy = 0;
				this.storedEnergy.setEnergyStored(energy);
			}

			if (neightborCache[(dir.ordinal())] != null && neightborCache[(dir.ordinal())] instanceof IFluidHandler && this.tank.getFluid() != null
					&& (getSide(dir.getOpposite().ordinal()) == SIDE_IO || getSide(dir.getOpposite().ordinal()) == SIDE_FLUID_OUTPUT_ONLY)) {
				IFluidHandler fluidHandler = (IFluidHandler) neightborCache[(dir.ordinal())];
				if (fluidHandler.canFill(dir, tank.getFluid().getFluid())) {
					FluidStack fluidToDrain = this.drain(dir.getOpposite(), MAX_FLUID_IO, false);
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
		ioConfiguration = tag.getIntArray("configuration");
		allowAutoInput = tag.getBoolean("allowAutoInput");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
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

	public String getType() {
		return "UniversalInterface";
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle)
			throws LuaException, FunctionNotFoundException {
		final String[] configNames = { "neutral", "universal", "items", "energy", "fluidInput", "fluidOutput" };
		switch (method) {
			case "getHeldStack":
				return new Object[] { CCUtils.stackToMap(heldStack) };
			case "pushStack": {
				int direction = Util.relDirToAbsDir(facing, Util.readableRelDirToRelForgeDir((String) arguments[0]));
				int simulatedDir = (arguments[1] == "") ? ForgeDirection.values()[direction].getOpposite().ordinal()
						: Util.readableDirToForgeDir((String) arguments[1]);
				int amount = ((Number) arguments[2]).intValue();

				if (direction == -1 || simulatedDir == -1)
					throw new LuaException("Invalid Direction");

				boolean sucessfull = false;
				if (heldStack != null) {
					ForgeDirection dirs = ForgeDirection.values()[direction];
					TileEntity te = worldObj.getTileEntity(dirs.offsetX + xCoord, dirs.offsetY + yCoord, dirs.offsetZ + zCoord);
					if (te instanceof IInventory && amount <= heldStack.stackSize) {
						ItemStack pushed = InventoryHelper.insertItemStackIntoInventory((IInventory) te,
								(amount <= 0) ? heldStack : heldStack.splitStack(amount), simulatedDir);
						sucessfull = pushed == null;
						if (amount <= 0)
							heldStack = pushed;
						else {
							heldStack.stackSize += ((pushed != null) ? pushed.stackSize : 0);
							heldStack = (heldStack.stackSize <= 0) ? null : heldStack;
						}
					}
				}
				if (sucessfull) {
					this.markDirty();
				}

				return new Object[] { sucessfull };
			}
			case "suckStack": {
				int direction = Util.relDirToAbsDir(facing, Util.readableRelDirToRelForgeDir((String) arguments[0]));
				int simulatedDir = (arguments[1] == "") ? ForgeDirection.values()[direction].getOpposite().ordinal()
						: Util.readableDirToForgeDir((String) arguments[1]);
				int amount = ((Number) arguments[2]).intValue();

				if (direction == -1 || simulatedDir == -1)
					throw new LuaException("Invalid Direction");

				ItemStack extractedStackBackup = null;
				int oldStackSize = (heldStack == null) ? 0 : heldStack.stackSize;

				ForgeDirection dirs = ForgeDirection.values()[direction];
				TileEntity te = worldObj.getTileEntity(dirs.offsetX + xCoord, dirs.offsetY + yCoord, dirs.offsetZ + zCoord);
				if (te instanceof IInventory && (heldStack == null) ? true : heldStack.stackSize < heldStack.getMaxStackSize()) {
					IInventory inventory = (IInventory) te;
					ItemStack extractedStack = null;

					if (inventory instanceof ISidedInventory) {
						ISidedInventory sidedInv = (ISidedInventory) inventory;
						int slots[] = sidedInv.getAccessibleSlotsFromSide(simulatedDir);
						for (int i = 0; i < slots.length && extractedStack != null; i++) {
							if (sidedInv.getStackInSlot(i) != null && sidedInv.canExtractItem(i, sidedInv.getStackInSlot(i), simulatedDir)
									&& (heldStack == null) ? true : ItemHelper.itemsEqualForCrafting(heldStack, sidedInv.getStackInSlot(i))) {
								int space = (heldStack == null) ? sidedInv.getStackInSlot(i).getMaxStackSize()
										: heldStack.getMaxStackSize() - heldStack.stackSize;
								extractedStack = (amount <= 0)
										? sidedInv
												.getStackInSlot(
														i)
												.splitStack(Math.min(Math.min(space,
														(heldStack == null) ? sidedInv.getStackInSlot(i).getMaxStackSize()
																: heldStack.getMaxStackSize()),
														sidedInv.getStackInSlot(i).stackSize))
										: sidedInv.getStackInSlot(i)
												.splitStack(Math.min(Math.min(space, amount), sidedInv.getStackInSlot(i).stackSize));
								if (heldStack == null)
									heldStack = extractedStack;
								else
									heldStack.stackSize += extractedStack.stackSize;
								if (sidedInv.getStackInSlot(i).stackSize == 0)
									sidedInv.setInventorySlotContents(i, null);
								extractedStackBackup = extractedStack;
								if ((heldStack != null) ? heldStack.stackSize == heldStack.getMaxStackSize() : false)
									break;

							}
						}
					} else {
						for (int i = 0; i < inventory.getSizeInventory() && extractedStack == null; i++) {
							if (inventory.getStackInSlot(i) != null && (heldStack == null) ? true
									: ItemHelper.itemsEqualForCrafting(heldStack, inventory.getStackInSlot(i))) {
								int space = (heldStack == null) ? inventory.getStackInSlot(i).getMaxStackSize()
										: heldStack.getMaxStackSize() - heldStack.stackSize;
								extractedStack = (amount <= 0)
										? inventory.getStackInSlot(i)
												.splitStack(
														Math.min(
																Math.min(space,
																		(heldStack == null) ? inventory.getStackInSlot(i).getMaxStackSize()
																				: heldStack.getMaxStackSize()),
																inventory.getStackInSlot(i).stackSize))
										: inventory.getStackInSlot(i)
												.splitStack(Math.min(Math.min(space, amount), inventory.getStackInSlot(i).stackSize));
								if (heldStack == null)
									heldStack = extractedStack;
								else
									heldStack.stackSize += extractedStack.stackSize;
								if (inventory.getStackInSlot(i).stackSize == 0)
									inventory.setInventorySlotContents(i, null);
								extractedStackBackup = extractedStack;
								if ((heldStack != null) ? heldStack.stackSize == heldStack.getMaxStackSize() : false)
									break;
							}
						}
					}
					if (extractedStack != null) {
						inventory.markDirty();
						this.markDirty();
					}
				}

				return new Object[] { (heldStack != null) ? heldStack.stackSize > oldStackSize : false, CCUtils.stackToMap(extractedStackBackup) };
			}
			case "setAllowAutoInput": {
				allowAutoInput = (Boolean) arguments[0];
				return new Object[] {};
			}
			case "isAutoInputAllowed": {
				return new Object[] { allowAutoInput };
			}
			case "setSideConfiguration": {
				String arg1 = (String) arguments[1];
				int config = -1;
				for (int i = 0; i < configNames.length; i++)
					if (configNames[i].indexOf(arg1) != -1) {
						config = i;
						break;
					}
				if (config == -1)
					throw new LuaException("Invalid configuration");
				int side = Util.relDirToAbsDir(facing, Util.readableRelDirToRelForgeDir((String) arguments[0]));
				if (side == -1)
					throw new LuaException("Invalid side");

				setSide(side, config);
				return new Object[] {};
			}
			case "getSideConfiguration": {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("bottom", configNames[ioConfiguration[0]]);
				map.put("top", configNames[ioConfiguration[1]]);
				map.put("back", configNames[ioConfiguration[2]]);
				map.put("front", configNames[ioConfiguration[3]]);
				map.put("left", configNames[ioConfiguration[4]]);
				map.put("right", configNames[ioConfiguration[5]]);
				return new Object[] { map };
			}
			case "getTankInfo": {
				return new Object[] { CCUtils.fluidStackToMap(tank.getFluid()) };
			}
			case "fillFluid": {
				int direction = Util.relDirToAbsDir(facing, Util.readableRelDirToRelForgeDir((String) arguments[0]));
				int simulatedDir = (arguments[1] == "") ? ForgeDirection.values()[direction].getOpposite().ordinal()
						: Util.readableDirToForgeDir((String) arguments[1]);
				int amount = ((Number) arguments[2]).intValue();
				boolean cancelOnError = ((Boolean) arguments[3]).booleanValue();

				if (direction == -1 || simulatedDir == -1)
					throw new LuaException("Invalid Direction");

				if (cancelOnError && tank.getFluidAmount() >= amount)
					return new Object[] { false };

				ForgeDirection dir = ForgeDirection.values()[direction];
				TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if (te instanceof IFluidHandler && tank.getFluidAmount() > 0) {
					IFluidHandler handler = (IFluidHandler) te;

					if (handler.canFill(ForgeDirection.values()[simulatedDir], tank.getFluid().getFluid())) {
						int result = handler.fill(ForgeDirection.values()[simulatedDir], tank.drain(amount, false), false);
						if (result != amount && cancelOnError)
							return new Object[] { false };

						handler.fill(ForgeDirection.values()[simulatedDir], tank.drain(result, true), true);

						return new Object[] { true };

					}
				}

				return new Object[] { false };
			}
			case "drainFluid": {
				int direction = Util.relDirToAbsDir(facing, Util.readableRelDirToRelForgeDir((String) arguments[0]));
				int simulatedDir = (arguments[1] == "") ? ForgeDirection.values()[direction].getOpposite().ordinal()
						: Util.readableDirToForgeDir((String) arguments[1]);
				int amount = ((Number) arguments[2]).intValue();
				boolean cancelOnError = ((Boolean) arguments[3]).booleanValue();

				if (direction == -1 || simulatedDir == -1)
					throw new LuaException("Invalid Direction");

				ForgeDirection dir = ForgeDirection.values()[direction];
				TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if (te instanceof IFluidHandler && tank.getFluidAmount() < tank.getCapacity()) {
					IFluidHandler handler = (IFluidHandler) te;

					if (handler.getTankInfo(ForgeDirection.values()[simulatedDir]).length > 0
							&& ((cancelOnError) ? handler.getTankInfo(ForgeDirection.values()[simulatedDir])[0].fluid.amount >= amount
									: handler.getTankInfo(ForgeDirection.values()[simulatedDir])[0].fluid.amount > 0)
							&& handler.canDrain(ForgeDirection.values()[simulatedDir],
									(tank.getFluid() == null) ? null : tank.getFluid().getFluid())) {

						int result = tank.fill(handler.drain(ForgeDirection.values()[simulatedDir], amount, false), false);

						if (result != amount && cancelOnError)
							return new Object[] { false };

						tank.fill(handler.drain(ForgeDirection.values()[simulatedDir], amount, true), true);

						return new Object[] { true };
					}

				}

				return new Object[] { false };
			}
			case "wrapPeripheral": {
				int relDir = Util.readableRelDirToRelForgeDir((String) arguments[0]);

				if (relDir == -1)
					throw new LuaException("Invalid direction");

				int direction = Util.relDirToAbsDir(facing, ForgeDirection.getOrientation(relDir).getOpposite().ordinal());

				if (direction == -1)
					throw new LuaException("Invalid Direction");

				return new Object[] { new PeripheralLuaObjectWrap(connectedPeripherals[direction], computer) };
			}
			default: {
				try {
					return super.callMethod(computer, context, method, arguments, turtle);
				} catch (FunctionNotFoundException e) {
					throw e;
				} catch (LuaException e) {
					throw e;
				}
			}
		}

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

	@Override
	public int[] getAccessibleSlotsFromSide(int slot) {
		return new int[] { 0 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return (getSide(side) == SIDE_IO || getSide(side) == SIDE_ITEMS_ONLY) && allowAutoInput;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return (getSide(side) == SIDE_IO || getSide(side) == SIDE_ITEMS_ONLY);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (getSide(from.ordinal()) == SIDE_IO || getSide(from.ordinal()) == SIDE_ENERGY_ONLY)
			return super.extractEnergy(from, maxExtract, simulate);
		return 0;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (getSide(from.ordinal()) == SIDE_IO || getSide(from.ordinal()) == SIDE_ENERGY_ONLY)
			return super.receiveEnergy(from, maxReceive, simulate);
		return 0;
	}

	@Override
	public boolean decrSide(int side) {
		setSide(side, (getSide(side) - 1 < 0) ? SIDES_COUNT - 1 : getSide(side) - 1);
		updateAllBlocks();
		return true;
	}

	@Override
	public boolean incrSide(int side) {
		setSide(side, (getSide(side) + 1 >= SIDES_COUNT) ? 0 : getSide(side) + 1);
		updateAllBlocks();
		return true;
	}

	@Override
	public boolean setSide(int side, int config) {
		ioConfiguration[Util.relDirToAbsDir(facing, side)] = config;
		updateAllBlocks();
		return true;
	}

	public int getSide(int side) {
		return ioConfiguration[Util.relDirToAbsDir(facing, side)];
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
		return facing;
	}

	@Override
	public boolean allowYAxisFacing() {
		return true;
	}

	@Override
	public boolean setFacing(int side) {
		facing = side;
		updateAllBlocks();
		return true;
	}

	@Override
	public IIcon getTexture(int side, int pass) {
		return this.blockType.getIcon(worldObj, xCoord, yCoord, zCoord, side);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (getSide(from.ordinal()) == SIDE_FLUID_INPUT_ONLY || getSide(from.ordinal()) == SIDE_IO)
			return tank.fill(resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (getSide(from.ordinal()) == SIDE_FLUID_OUTPUT_ONLY || getSide(from.ordinal()) == SIDE_IO)
			return tank.drain(resource.amount, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (getSide(from.ordinal()) == SIDE_FLUID_OUTPUT_ONLY || getSide(from.ordinal()) == SIDE_IO)
			return tank.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return tank.getCapacity() == tank.getFluidAmount()
				&& (getSide(from.ordinal()) == SIDE_FLUID_INPUT_ONLY || getSide(from.ordinal()) == SIDE_IO);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return tank.getFluidAmount() > 0 && getSide(from.ordinal()) == SIDE_IO || getSide(from.ordinal()) == SIDE_FLUID_INPUT_ONLY;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (getSide(from.ordinal()) == SIDE_FLUID_INPUT_ONLY || getSide(from.ordinal()) == SIDE_IO)
			return new FluidTankInfo[] { tank.getInfo() };
		return new FluidTankInfo[] {};
	}

	public FluidTank getTank() {
		return tank;
	}

	@Override
	public void attachToComputer(IComputerAccess computer) {
		// RandomPeripherals.logger.info("UniversalInterface_attachToComputer");
		if (!attachedComputer.contains(computer) && !(computer instanceof ComputerUIWrapDummy)) {
			attachedComputer.add(computer);
			for (int i = 0; i < connectedPeripherals.length; i++)
				if (connectedPeripherals[i] != null)
					connectedPeripherals[i].attach(new ComputerUIWrapDummy(computer,
							dirs[Util.relDirToAbsDir(facing, ForgeDirection.getOrientation(i).getOpposite().ordinal())]));
		}
	}

	@Override
	public void detachFromComputer(IComputerAccess computer) {
		attachedComputer.remove(computer);
		for (int i = 0; i < connectedPeripherals.length; i++)
			if (connectedPeripherals[i] != null)
				if (connectedPeripherals[i] instanceof Peripheral) {
					Peripheral peripheral = (Peripheral) connectedPeripherals[i];
					if (peripheral.getType() == "universalInterface" && peripheral.isAttachedToComputer(computer)) {
						connectedPeripherals[i].detach(computer);
					}
				} else
					connectedPeripherals[i].detach(computer);
	}

	public boolean isStillAttachedToComputer(IComputerAccess computer) {
		return attachedComputer.contains(computer);
	}

	@Override
	public void addNeightborCache(TileEntity tile, int x, int y, int z) {
		if (tile != null)
			super.addNeightborCache(tile, x, y, z);

		int side = Util.getSideFromRelativeCoordinates(x, y, z, xCoord, yCoord, zCoord);

		IPeripheral connectedPeripheral = getPeripheralAt(worldObj, x, y, z, side);
		if ((connectedPeripherals[side] == null) ? true : !connectedPeripherals[side].equals(connectedPeripheral)) {
			if (connectedPeripherals[side] != null)
				for (int i = 0; i < attachedComputer.size(); i++) {
					try {
						connectedPeripherals[side].detach(attachedComputer.get(i));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			connectedPeripherals[side] = connectedPeripheral;

			if (connectedPeripherals[side] != null)
				for (int i = 0; i < attachedComputer.size(); i++) {
					try {
						if (!(attachedComputer.get(i) instanceof ComputerUIWrapDummy))
							connectedPeripherals[side].attach(new ComputerUIWrapDummy(attachedComputer.get(i),
									dirs[Util.relDirToAbsDir(facing, ForgeDirection.getOrientation(side).getOpposite().ordinal())]));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			// RandomPeripherals.logger.info("addNeightborCache side: " + side +
			// " connectedPeripheral: "
			// + ((connectedPeripherals[side] == null) ? "null" :
			// connectedPeripherals[side].getType()));
		} // else
			// RandomPeripherals.logger.info("Already connected");
	}
}
