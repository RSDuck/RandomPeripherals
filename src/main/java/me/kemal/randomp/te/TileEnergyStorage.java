package me.kemal.randomp.te;

import java.util.Random;

import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.util.CCType;
import me.kemal.randomp.util.FunctionNotFoundException;
import me.kemal.randomp.util.Peripheral;
import me.kemal.randomp.util.Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import cofh.lib.util.helpers.BlockHelper;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;

public class TileEnergyStorage extends TileRandomPMachine implements IEnergyHandler {
	protected EnergyStorage storedEnergy;
	protected Object neightborCache[];
	protected int maxEnergyInput[];
	protected int maxEnergyOutput[];

	public TileEnergyStorage(int capacity) {
		super("EnergyStorage");
		storedEnergy = new EnergyStorage(capacity, 1000);
		neightborCache = new Object[6];

		maxEnergyInput = new int[6];
		maxEnergyOutput = new int[6];

		peripheral.addMethod("setMaxEnergyOutput", "Sets the maximum amount of energy that goes out on the specified side",
				new CCType[] { new CCType(Double.class, "newOutput", "The new amount of maximum energy output", 0, 1000),
						new CCType(String.class, "side",
								"The side where the maximum energy output should be changed(valid inputs: bottom, top, left, right, back, front)") },
				new CCType[] {}, this);
		peripheral.addMethod("setMaxEnergyInput", "Sets the maximum amount of energy that goes in",
				new CCType[] { new CCType(Double.class, "newInput", "The new amount of maximum energy input", 0, 1000),
						new CCType(String.class, "side",
								"The side where the maximum energy input should be changed(valid inputs: bottom, top, left, right, back, front)") },
				new CCType[] {}, this);
		peripheral.addMethod("getMaxEnergyOutput", "Returns the maximum amount of energy that goes out",
				new CCType[] { new CCType(String.class, "side",
						"The side where you want to get the maximum energy output(valid inputs: bottom, top, left, right, back, front)") },
				new CCType[] { new CCType(Double.class, "The current maximum amount of energy that goes out") }, this);
		peripheral.addMethod("getMaxEnergyInput", "Returns the maximum amount of energy that will goes in",
				new CCType[] { new CCType(String.class, "side",
						"The side where you want to get the maximum energy input(valid inputs: bottom, top, left, right, back front)") },
				new CCType[] { new CCType(Double.class, "The current maximum amount of energy that comes in") }, this);
		peripheral.addMethod("getEnergyStored", "Returns the current amount of stored energy", new CCType[] {},
				new CCType[] { new CCType(Double.class, "The current amount of stored energy") }, this);
		peripheral.addMethod("getMaxEnergyStored", "Return the maximum amount of energy that can be stored", new CCType[] {},
				new CCType[] { new CCType(Double.class, "The maximum amount of energy that can be stored") }, this);
	}

	public TileEnergyStorage() {
		this(0);
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle)
			throws LuaException, FunctionNotFoundException {
		switch (method) {
			case "setMaxEnergyOutput": {
				int arg0 = ((Number) arguments[0]).intValue();
				int arg1 = Util.relDirToAbsDir(facing, Util.readableRelDirToRelForgeDir((String) arguments[1]));
				if (arg1 == -1)
					throw new LuaException("Invalid direction");
				maxEnergyOutput[arg1] = arg0;
				return new Object[] {};
			}
			case "setMaxEnergyInput": {
				int arg0 = ((Number) arguments[0]).intValue();
				int arg1 = Util.relDirToAbsDir(facing, Util.readableRelDirToRelForgeDir((String) arguments[1]));
				if (arg1 == -1)
					throw new LuaException("Invalid direction");
				maxEnergyInput[arg1] = arg0;
				return new Object[] {};
			}
			case "getMaxEnergyOutput": {
				int arg1 = Util.relDirToAbsDir(facing, Util.readableRelDirToRelForgeDir((String) arguments[0]));
				if (arg1 == -1)
					throw new LuaException("Invalid direction");
				return new Object[] { maxEnergyOutput[arg1] };
			}
			case "getMaxEnergyInput": {
				int arg1 = Util.relDirToAbsDir(facing, Util.readableRelDirToRelForgeDir((String) arguments[0]));
				if (arg1 == -1)
					throw new LuaException("Invalid direction");
				return new Object[] { maxEnergyInput[arg1] };
			}
			case "getEnergyStored": {
				return new Object[] { storedEnergy.getEnergyStored() };
			}
			case "getMaxEnergyStored": {
				return new Object[] { storedEnergy.getMaxEnergyStored() };
			}
			default: {
				try {
					return super.callMethod(computer, context, method, arguments, turtle);
				} catch (LuaException e) {
					throw e;
				} catch (FunctionNotFoundException e) {
					throw e;
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		storedEnergy.readFromNBT(tag);
		maxEnergyOutput = tag.getIntArray("maxEnergyExtract");
		maxEnergyInput = tag.getIntArray("maxEnergyReceive");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		storedEnergy.writeToNBT(tag);
		tag.setIntArray("maxEnergyExtract", maxEnergyOutput);
		tag.setIntArray("maxEnergyReceive", maxEnergyInput);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		storedEnergy.setMaxExtract(maxEnergyOutput[from.ordinal()]);
		return storedEnergy.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return storedEnergy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return storedEnergy.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		storedEnergy.setMaxReceive(maxEnergyInput[from.ordinal()]);
		return storedEnergy.receiveEnergy(maxReceive, simulate);
	}

	public void addNeightborCache(TileEntity tile, int x, int y, int z) {
		int side = Util.getSideFromRelativeCoordinates(x, y, z, xCoord, yCoord, zCoord);

		neightborCache[side] = null;
		if (tile instanceof IEnergyHandler) {
			neightborCache[side] = (IEnergyHandler) tile;
		} else if (tile instanceof IFluidHandler) {
			neightborCache[side] = (IFluidHandler) tile;
		}
	}

	public void setEnergyStored(int val) {
		storedEnergy.setEnergyStored(val);
	}

	public EnergyStorage getEnergyStorage() {
		return storedEnergy;
	}

	public int getMaxEnergyOutputRel(int relSide) {
		return maxEnergyOutput[BlockHelper.ICON_ROTATION_MAP[facing][relSide]];
	}

	public int getMaxEnergyInputRel(int relSide) {
		return maxEnergyInput[BlockHelper.ICON_ROTATION_MAP[facing][relSide]];
	}

	public int getMaxEnergyInput(int side) {
		return maxEnergyInput[side];
	}

	public int getMaxEnergyOutput(int side) {
		return maxEnergyOutput[side];
	}

}
