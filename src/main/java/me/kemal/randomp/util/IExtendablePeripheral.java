package me.kemal.randomp.util;

import java.awt.image.TileObserver;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public interface IExtendablePeripheral extends IPeripheral {
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments);
}
