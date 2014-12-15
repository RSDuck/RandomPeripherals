package me.kemal.randomp.util;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;

public interface IExtendablePeripheral {
	/**
	 * This function will be called when on the computer an function is called
	 * that is linked to the class who implemented this interface The arguments
	 * 
	 * @param computer
	 *            the computer from where the peripheral is called
	 * @param context
	 *            can be used for for asyncrounous stuff...
	 * @param method
	 *            the name of the called method
	 * @param arguments
	 *            the arguments for calling the method. The arguments are
	 *            already checked for the right type, so there is no problem
	 *            with typechecking
	 * @param turtle
	 *            only used for turtle peripherals, so the value is null if the
	 *            function was called from an computer
	 * @return
	 * @throws LuaException
	 */
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle) throws LuaException;
}
