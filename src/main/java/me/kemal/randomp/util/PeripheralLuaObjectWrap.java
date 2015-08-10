package me.kemal.randomp.util;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import me.kemal.randomp.RandomPeripherals;

public class PeripheralLuaObjectWrap implements ILuaObject {
	IPeripheral peripheral;
	IComputerAccess computer;

	public PeripheralLuaObjectWrap(IPeripheral peripheral, IComputerAccess computer) {
		this.peripheral = peripheral;
		this.computer = computer;
	}

	public IPeripheral getPeripheral() {
		return peripheral;
	}

	@Override
	public String[] getMethodNames() {
		return (peripheral != null) ? peripheral.getMethodNames() : new String[] {};
	}

	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		return (peripheral != null && computer != null) ? peripheral.callMethod(computer, context, method, arguments)
				: null;
	}
}
