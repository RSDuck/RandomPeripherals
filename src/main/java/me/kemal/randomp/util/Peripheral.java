package me.kemal.randomp.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.Vector;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class Peripheral implements IPeripheral {
	private String type;
	private Vector<String> functionNames;
	// For help function
	private Map<String, String> functionArgDescriptions;
	private Map<String, String> functionReturnDescriptions;
	
	private Map<String, MethodHandle> functionClassHandlers;

	public Peripheral() {
	}

	@Override
	public String getType() {
		return type;
	}

	public void setType(String newType) {
		type = newType;
	}

	@Override
	public String[] getMethodNames() {
		return (String[]) functionNames.toArray();
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException {

		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {
	}

	@Override
	public void detach(IComputerAccess computer) {
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (other instanceof Peripheral && other.getType() == getType());
	}

}
