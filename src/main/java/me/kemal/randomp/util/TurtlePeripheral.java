package me.kemal.randomp.util;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;

public class TurtlePeripheral implements IPeripheral {
	private ITurtleAccess turtle;
	private Peripheral peripheral;

	public TurtlePeripheral(ITurtleAccess parentTurtle, Peripheral inheritPeriphal) {
		this.turtle = parentTurtle;
		this.peripheral = inheritPeriphal;
	}

	@Override
	public String getType() {
		return peripheral.getType();
	}

	@Override
	public String[] getMethodNames() {
		return peripheral.getMethodNames();
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return peripheral.callMethod(computer, context, method, arguments, turtle);
	}

	@Override
	public void attach(IComputerAccess computer) {
		peripheral.attach(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		peripheral.detach(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return peripheral.equals(other);
	}

}
