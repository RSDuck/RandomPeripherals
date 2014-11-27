package me.kemal.randomp.te;

import appeng.api.networking.energy.IEnergySource;
import me.kemal.randomp.RandomPeripheral;
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

public class TileEnergyStorage extends TileEntity implements IEnergyHandler, IPeripheral {
	protected EnergyStorage storedEnergy;
	protected Object neightborCache[];

	public TileEnergyStorage() {
		storedEnergy = new EnergyStorage(0, 0);
		neightborCache = new Object[6];
	}

	public TileEnergyStorage(int capacity) {
		storedEnergy = new EnergyStorage(capacity, 0);
		neightborCache = new Object[6];
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

	@Override
	public String getType() {
		return "Energy Storage";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] {""};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detach(IComputerAccess computer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean equals(IPeripheral other) {
		// TODO Auto-generated method stub
		return false;
	}
}
