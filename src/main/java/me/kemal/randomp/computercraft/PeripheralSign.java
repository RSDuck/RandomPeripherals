package me.kemal.randomp.computercraft;

import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.util.CCUtils;
import me.kemal.randomp.util.Util;
import net.minecraft.tileentity.TileEntitySign;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class PeripheralSign implements IPeripheral {
	TileEntitySign sign;

	public PeripheralSign() { // Empty Constructor
	}

	public PeripheralSign(TileEntitySign sign) {
		this.sign = sign;
	}

	@Override
	public String getType() {
		return "Sign";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "help", "getText" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		if (arguments.length == 0) {
			if (method == 0) {
				return new Object[] { "At this time there is no help available" };
			} else if (method == 1) {
				try {
					
					return new Object[] { CCUtils.arrayToLuaArray(sign.signText) };
				} catch (Exception e) {
					RandomPeripherals.logger.info("An Exception ocurred: "+e.getMessage()+"");
				}
			}
		}
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
