package me.kemal.randomp.te;

import java.util.ArrayList;
import java.util.Vector;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import me.kemal.randomp.util.FunctionNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileInventory extends TileMachineBase implements ISidedInventory {
	protected ItemStack[] inventory;
	protected String inventoryName;
	protected boolean allowAutoInventoryInsert;

	public TileInventory(String name, int inventorySize) {
		inventory = new ItemStack[inventorySize];
		inventoryName = name;
		allowAutoInventoryInsert = true;
		
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int numOfDecr) {
		if (inventory[slot] != null) {
			if (inventory[slot].stackSize < numOfDecr) {
				ItemStack stack = inventory[slot];
				inventory[slot] = null;
				this.markDirty();
				return stack;
			}
			ItemStack stack = inventory[slot].splitStack(numOfDecr);
			if (inventory[slot].stackSize == 0)
				inventory[slot] = null;
			this.markDirty();
			return stack;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;
	}

	@Override
	public String getInventoryName() {
		return inventoryName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		int[] indices = new int[inventory.length];
		for(int i = 0; i < inventory.length; i++)
			indices[i] = i;
		return indices;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return getSide(side) == SIDE_UNIVERSAL || getSide(side) == SIDE_ITEMS_ONLY && allowAutoInventoryInsert;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return getSide(side) == SIDE_UNIVERSAL || getSide(side) == SIDE_ITEMS_ONLY;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments,
			ITurtleAccess turtle) throws LuaException, FunctionNotFoundException {
		switch(method){
			default:
				try{
					return super.callMethod(computer, context, method, arguments, turtle);
				}catch(LuaException | FunctionNotFoundException e){
					throw e;
				}
		}
	}
}
