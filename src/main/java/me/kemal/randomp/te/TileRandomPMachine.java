package me.kemal.randomp.te;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import me.kemal.randomp.util.IExtendablePeripheral;
import me.kemal.randomp.util.Peripheral;
import net.minecraft.tileentity.TileEntity;

public class TileRandomPMachine extends TileEntity implements IExtendablePeripheral {
	public Peripheral peripheral;
	
	public TileRandomPMachine(String peripheralType) {
		peripheral = new Peripheral();
		peripheral.setType(peripheralType);
	}
	
	public Peripheral getPeripheral(){
		return peripheral;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments) {
		return null;
	}

}
