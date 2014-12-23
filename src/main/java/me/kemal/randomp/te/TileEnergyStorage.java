package me.kemal.randomp.te;

import java.util.Random;

import me.kemal.randomp.RandomPeripheral;
import me.kemal.randomp.util.CCType;
import me.kemal.randomp.util.FunctionNotFoundException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;

public class TileEnergyStorage extends TileRandomPMachine implements IEnergyHandler {
	protected EnergyStorage storedEnergy;
	protected Object neightborCache[];

	public TileEnergyStorage(int capacity) {
		super("EnergyStorage");
		storedEnergy = new EnergyStorage(capacity, 0);
		neightborCache = new Object[6];
		peripheral.AddMethod("setMaxEnergyOutput", "Sets the maximum amount of energy that goes out", new CCType[] { new CCType(Double.class, "newOutput",
				"The new amount of maximum energy output", 0, 1000) }, new CCType[] {}, this);
		peripheral.AddMethod("setMaxEnergyInput", "Sets the maximum amount of energy that goes in", new CCType[] { new CCType(Double.class, "newInput",
				"The new amount of maximum energy input", 0, 1000) }, new CCType[] {}, this);
		peripheral.AddMethod("getMaxEnergyOutput", "Returns the maximum amount of energy that goes out", new CCType[] {}, new CCType[] { new CCType(
				Double.class, "The current maximum amount of energy that goes out") }, this);
		peripheral.AddMethod("getMaxEnergyInput", "Returns the maximum amount of energy that will goes in", new CCType[] {}, new CCType[] { new CCType(
				Double.class, "The current maximum amount of energy that comes in") }, this);
		peripheral.AddMethod("getEnergyStored", "Returns the current amount of stored energy", new CCType[] {}, new CCType[] { new CCType(Double.class,
				"The current amount of stored energy") }, this);
		peripheral.AddMethod("getMaxEnergyStored", "Return the maximum amount of energy that can be stored", new CCType[] {}, new CCType[] { new CCType(
				Double.class, "The maximum amount of energy that can be stored") }, this);
	}

	public TileEnergyStorage() {
		this(0);
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle) throws LuaException,
			FunctionNotFoundException {
		switch (method) {
			case "setMaxEnergyOutput": {
				int arg0 = ((Number) arguments[0]).intValue();
				storedEnergy.setMaxExtract(arg0);
				return new Object[] {};
			}
			case "setMaxEnergyInput": {
				int arg0 = ((Number) arguments[0]).intValue();
				storedEnergy.setMaxReceive(arg0);
				return new Object[] {};
			}
			case "getMaxEnergyOutput": {
				return new Object[] { storedEnergy.getMaxExtract() };
			}
			case "getMaxEnergyInput": {
				return new Object[] { storedEnergy.getMaxReceive() };
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
				} catch (FunctionNotFoundException e){
					throw e;
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		storedEnergy.readFromNBT(tag);
		storedEnergy.setMaxExtract(tag.getInteger("maxEnergyExtract"));
		storedEnergy.setMaxReceive(tag.getInteger("maxEnergyReceive"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		storedEnergy.writeToNBT(tag);
		tag.setInteger("maxEnergyExtract", storedEnergy.getMaxExtract());
		tag.setInteger("maxEnergyReceive", storedEnergy.getMaxReceive());
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		// TODO: function for energy output
		/*
		 * for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) { if
		 * (neightborCache[dir.ordinal()] != null) { int energyAvailable = 0;
		 * int energyNeeded = neightborCache[dir.ordinal()].receiveEnergy(dir,
		 * storedEnergy.getMaxExtract(), true); if (energyNeeded > 0) {
		 * energyAvailable = this.extractEnergy(dir, energyNeeded, true); }
		 * neightborCache[dir.ordinal()].receiveEnergy(dir, energyAvailable,
		 * false); this.extractEnergy(dir.getOpposite(), energyAvailable,
		 * false); } }
		 */
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
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
		return storedEnergy.receiveEnergy(maxReceive, simulate);
	}

	public void addNeightborCache(TileEntity tile, int x, int y, int z) {
		int side = 0;
		if (x < xCoord)
			side = 5;
		else if (x > xCoord)
			side = 4;
		else if (z < zCoord)
			side = 3;
		else if (z > zCoord)
			side = 2;
		else if (y < yCoord)
			side = 1;
		else if (y > yCoord)
			side = 0;

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

}
