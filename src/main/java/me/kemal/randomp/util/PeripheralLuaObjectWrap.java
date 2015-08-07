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

		//peripheral.attach(computer);
	}

	public IPeripheral getPeripheral() {
		return peripheral;
	}

	public IComputerAccess getComputer() {
		return computer;
	}

	@Override
	public String[] getMethodNames() {
		return (peripheral != null) ? peripheral.getMethodNames() : new String[] {};
	}

	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		return (peripheral != null) ? peripheral.callMethod(computer, context, method, arguments) : new Object[] {};
	}

	public void detach() {
		//if (peripheral != null)
		//	peripheral.detach(computer);
		peripheral = null;
	}

	public void setPeripheral(IPeripheral peripheral) {
		this.peripheral = peripheral;
	}

	public void setComputer(IComputerAccess computer2) {
		computer = computer2;
	}
}
