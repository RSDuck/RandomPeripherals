package me.kemal.randomp.te;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileFluidEnergyInventory extends TileFluidInventory implements IEnergyHandler {
	public TileFluidEnergyInventory(String inventoryName, int inventorySize, int fluidCapacity) {
		super(inventoryName, inventorySize, fluidCapacity);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return sideConfiguration[from.ordinal()] == SIDE_UNIVERSAL || sideConfiguration[from.ordinal()] == SIDE_ENERGY_ONLY;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return 0;
	}

}
