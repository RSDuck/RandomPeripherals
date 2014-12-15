package me.kemal.randomp.computercraft;

import me.kemal.randomp.util.CCUtil;
import me.kemal.randomp.util.Util;
import net.minecraft.block.*;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;

public class PeripheralDispenser implements IPeripheral {
	ITurtleAccess turtle;

	public PeripheralDispenser(ITurtleAccess turtle) {
		this.turtle = turtle;
	}

	@Override
	public String getType() {
		return "Dispenser";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "help", "dispense" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		/*
		 * if (method == 0) { return new Object[] {
		 * "Help will be added in a few" }; } else if (method == 1 &&
		 * arguments.length == 1) { if (CCUtil.IsValidString(arguments[0]))
		 * return new Object[] { dispense(Util.ToString(arguments[0])) }; }
		 */
		return new Object[] { null };
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

}
