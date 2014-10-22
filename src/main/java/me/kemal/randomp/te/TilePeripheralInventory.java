package me.kemal.randomp.te;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import me.kemal.randomp.util.Util;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;

public class TilePeripheralInventory extends TileEntity implements IPeripheral {
	public ITurtleAccess turtle;

	public TilePeripheralInventory(ITurtleAccess turtle) {
		this.turtle = turtle;
	}

	public TilePeripheralInventory() {
	}

	@Override
	public String getType() {
		return "Inventory";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "help", "getStackInSlot" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		switch (method) {
			case 0: {
				return new Object[] { "At the time there is no help available" };
			}
			case 1: { // getStackInSlot
				if (arguments.length == 1 && arguments[0] instanceof Double) {
					int slotNumber = ((Number) arguments[0]).intValue();
					ItemStack stack = turtle.getInventory().getStackInSlot(slotNumber - 1);
					if (stack != null) {
						return new Object[] { Util.stackToMap(stack) };
					} else
						return new Object[] { false };
				} else {
					return new Object[] { false };
				}
			}
		}
		return new Object[] { null };
	}

	@Override
	public void attach(IComputerAccess computer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void detach(IComputerAccess computer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equals(IPeripheral other) {
		// TODO Auto-generated method stub
		return false;
	}

}
