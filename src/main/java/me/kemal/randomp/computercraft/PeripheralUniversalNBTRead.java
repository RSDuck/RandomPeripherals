package me.kemal.randomp.computercraft;

import java.util.HashMap;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import me.kemal.randomp.util.CCType;
import me.kemal.randomp.util.CCUtils;
import me.kemal.randomp.util.FunctionNotFoundException;
import me.kemal.randomp.util.IExtendablePeripheral;
import me.kemal.randomp.util.Peripheral;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class PeripheralUniversalNBTRead implements IExtendablePeripheral {
	Peripheral peripheral;
	TileEntity te;

	public PeripheralUniversalNBTRead(TileEntity tile) {
		peripheral = new Peripheral();
		peripheral.setType("Generic NBT Reader");
		peripheral.setDescription(
				"This peripheral can be any block with tile entity where you can read through the peripheral the NBT Data");
		peripheral.AddMethod("readNBT", "Reads the NBT data of this block", new CCType[] {}, new CCType[] {new CCType(HashMap.class, "The NBT data of this block")}, this);
		te = tile;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments,
			ITurtleAccess turtle) throws LuaException, FunctionNotFoundException {
		if (method == "readNBT") {
			NBTTagCompound tag = new NBTTagCompound();
			te.writeToNBT(tag);
			return new Object[] { CCUtils.NBTCompoundToMap(tag) };
		}
		return null;
	}

	@Override
	public Peripheral getPeripheral() {
		return peripheral;
	}

	@Override
	public void attachToComputer(IComputerAccess computer) {
	}

	@Override
	public void detachFromComputer(IComputerAccess computer) {
		// TODO Auto-generated method stub
		
	}
}
