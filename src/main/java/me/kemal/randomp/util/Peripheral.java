package me.kemal.randomp.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Vector;

import me.kemal.randomp.RandomPeripheral;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class Peripheral implements IExtendablePeripheral {
	private String type;
	private Vector<String> functionNames;
	// For help function
	private HashMap<String, String> functionDescriptions;
	private HashMap<String, String[]> functionArgDescriptions;
	private HashMap<String, String[]> functionReturnDescriptions;

	private HashMap<String, MethodHandle> functionClassHandlers;

	public Peripheral() {
		functionNames = new Vector<String>();
		functionArgDescriptions = new HashMap<String, String[]>();
		functionReturnDescriptions = new HashMap<String, String[]>();
		functionClassHandlers = new HashMap<String, MethodHandle>();

		AddMethod("help", "Get Help about an function", new String[] { "functionName : the name of the function you need help" },
				new String[] { "An help like this" }, this);
		AddMethod("getMethods", "Lists all function of this peripheral", new String[] {}, new String[] {}, this);
	}

	public void AddMethod(String name, String description, String[] args, String returns[], IExtendablePeripheral classToCall) {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		try {
			functionClassHandlers.put(
					name,
					lookup.findVirtual(IExtendablePeripheral.class, "callMethod",
							MethodType.methodType(Object[].class, IComputerAccess.class, ILuaContext.class, String.class, Object[].class)));
		} catch (NoSuchMethodException e) {
			RandomPeripheral.logger.error("Could not callback function for function " + name + " in peripheral " + type);
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		functionNames.addElement(name);
		functionDescriptions.put(name, description);
		functionArgDescriptions.put(name, args);
		functionReturnDescriptions.put(name, returns);
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
		try {
			return (Object[]) functionClassHandlers.get(functionNames.get(method)).invokeWithArguments(computer, context, functionNames.get(method), arguments);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		throw new LuaException("Internal Error: Function not found!");
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

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments) {
		return new Object[] {};
	}
}
