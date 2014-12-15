package me.kemal.randomp.te;

import java.util.Vector;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedTexture;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import me.kemal.randomp.util.IExtendablePeripheral;
import me.kemal.randomp.util.Peripheral;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class TileRandomPMachine extends TileEntity implements IInventory, ISidedInventory, IExtendablePeripheral, IReconfigurableFacing, IReconfigurableSides,
		ISidedTexture {
	protected Peripheral peripheral;
	protected int[] ioConfiguration;
	protected ItemStack[] inventory;

	public static final int SIDES_COUNT = 5;

	public TileRandomPMachine(String peripheralType) {
		peripheral = new Peripheral();
		peripheral.setType(peripheralType);
	}

	public Peripheral getPeripheral() {
		return peripheral;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle) throws LuaException {
		return null;
	}

	@Override
	public IIcon getTexture(int side, int pass) {
		return null;
	}

	@Override
	public boolean decrSide(int side) {
		return false;
	}

	@Override
	public boolean incrSide(int side) {
		return false;
	}

	@Override
	public boolean setSide(int side, int config) {
		return false;
	}

	@Override
	public boolean resetSides() {
		return false;
	}

	@Override
	public int getNumConfig(int side) {
		return 0;
	}

	@Override
	public int getFacing() {
		return 0;
	}

	@Override
	public boolean allowYAxisFacing() {
		return false;
	}

	@Override
	public boolean rotateBlock() {
		return false;
	}

	@Override
	public boolean setFacing(int side) {
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		int[] slots = new int[inventory.length];
		for (int i = 0; i < inventory.length; i++)
			slots[i] = i;
		return slots;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		if (inventory != null)
			return inventory.length;
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (inventory != null) {
			return inventory[slot];
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		
	}

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

}