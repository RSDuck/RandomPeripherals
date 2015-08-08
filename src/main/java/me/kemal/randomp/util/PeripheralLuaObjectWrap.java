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
		this.computer = new ComputerUIWrapDummy(computer);

		try {
			if (this.computer != null)
				peripheral.attach(this.computer);
		} catch (Exception e) {
		}
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
		return (peripheral != null && computer != null) ? peripheral.callMethod(computer, context, method, arguments)
				: null;
	}

	public void detach() {
		if (peripheral != null && computer != null)
			try {
				peripheral.detach(computer);
			} catch (Exception e) {
			}
		peripheral = null;
	}

	public void setPeripheral(IPeripheral peripheral) {
		this.peripheral = peripheral;
	}

	public void attach(IComputerAccess computer2) {
		computer = computer2;
		try {
			if (this.computer != null)
				peripheral.attach(this.computer);
		} catch (Exception e) {
		}
	}
}
