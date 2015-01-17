package me.kemal.randomp.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import me.kemal.randomp.RandomPeripheral;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;

public class Peripheral implements IExtendablePeripheral, IPeripheral {
	private String type;
	private Vector<String> functionNames;
	// For help function
	private HashMap<String, String> functionDescriptions;
	private HashMap<String, CCType[]> functionArgs;
	private HashMap<String, CCType[]> functionReturns;
	private HashMap<String, IExtendablePeripheral> peripheralHolders;

	public Peripheral() {
		functionNames = new Vector<String>();
		functionArgs = new HashMap<String, CCType[]>();
		functionDescriptions = new HashMap<String, String>();
		functionReturns = new HashMap<String, CCType[]>();
		peripheralHolders = new HashMap<String, IExtendablePeripheral>();

		AddMethod("help", "Get Help about an function", new CCType[] { new CCType(String.class, "functionName",
				"the name of the function you need help with") }, new CCType[] { new CCType(HashMap.class, "an help like this") },
				this);
		AddMethod("getMethods", "Lists all function of this peripheral", new CCType[] {}, new CCType[] { new CCType(HashMap.class,
				"An table filled with the names of all function") }, this);
	}

	public void AddMethod(String name, String description, CCType[] args, CCType returns[], IExtendablePeripheral classToCall) {
		functionNames.addElement(name);
		functionDescriptions.put(name, description);
		functionArgs.put(name, args);
		functionReturns.put(name, returns);
		peripheralHolders.put(name, classToCall);
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
		return Arrays.copyOf(functionNames.toArray(), functionNames.size(), String[].class);
	}

	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments, ITurtleAccess turtle)
			throws LuaException {
		try {
			String functionName = functionNames.get(method);
			CCType[] requiredTypes = functionArgs.get(functionName);
			if (requiredTypes.length == arguments.length) {
				int i = 0;
				for (CCType requiredType : requiredTypes) {
					int returnValue;
					if ((returnValue = requiredType.isValid(arguments[i])) != 1) {
						if (returnValue == 0)
							throw new LuaException("Invalid argument type in argument " + requiredType.getName() + "");
						else if (returnValue == 2)
							throw new LuaException("Arg " + i + " should be " + requiredType.getMinValue() + " or more and be "
									+ requiredType.getMaxValue() + " or less");
					}
					i++;
				}
			} else
				throw new LuaException(((arguments.length < requiredTypes.length) ? "To few arguments" : "To many arguments")
						+ " to call" + functionName);
			return peripheralHolders.get(functionName).callMethod(computer, context, functionNames.get(method), arguments, turtle);
		} catch (LuaException e) {
			throw e;
		} catch (FunctionNotFoundException e) {
			throw new LuaException("Internal Error: Function not found");
		} catch (Throwable e) {
			e.printStackTrace();
		}

		throw new LuaException("Internal Error ocurred!");
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException {
		return callMethod(computer, context, method, arguments, null);
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
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle)
			throws LuaException, FunctionNotFoundException {
		switch (method) {
		case "help": {
			try {
				String arg1 = (String) arguments[0];
				if (!functionArgs.containsKey(arg1))
					return new Object[] { null };
				HashMap<String, Object> functionInfo = new HashMap<String, Object>();
				functionInfo.put("name", arg1);
				functionInfo.put("description", functionDescriptions.get(arg1));
				functionInfo.put("arguments", CCUtils.ArrayToLuaArray(functionArgs.get(arg1)));
				functionInfo.put("returns", CCUtils.ArrayToLuaArray(functionReturns.get(arg1)));
				return new Object[] { functionInfo };
			} catch (Exception e) {
				e.printStackTrace();
				throw new LuaException("Internal Error");
			}
		}
		case "getMethods": {
			return new Object[] { CCUtils.ArrayToLuaArray(functionNames.toArray()) };
		}
		}
		throw new LuaException("Internal Error: function not found");
	}

	@Override
	public Peripheral getPeripheral() {
		return this;
	}
}
