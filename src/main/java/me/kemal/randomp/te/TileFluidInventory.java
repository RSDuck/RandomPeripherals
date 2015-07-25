package me.kemal.randomp.te;

import cofh.lib.util.helpers.FluidHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileFluidInventory extends TileInventory implements IFluidHandler {
	protected boolean allowAutoFluidInsert;
	protected boolean allowAutoFluidOutput;
	protected FluidTank tank;

	public TileFluidInventory(String name, int inventorySize, int tankCapacity) {
		super(name, inventorySize);
		
		
		
		tank = new FluidTank(tankCapacity);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return (sideConfiguration[from.ordinal()] == SIDE_UNIVERSAL
				|| sideConfiguration[from.ordinal()] == SIDE_FLUID_ONLY) && allowAutoFluidInsert;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return (sideConfiguration[from.ordinal()] == SIDE_UNIVERSAL
				|| sideConfiguration[from.ordinal()] == SIDE_FLUID_ONLY) && allowAutoFluidOutput;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return null;
	}

}
