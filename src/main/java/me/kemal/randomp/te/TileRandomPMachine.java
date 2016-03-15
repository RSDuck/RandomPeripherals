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
import me.kemal.randomp.util.FunctionNotFoundException;
import me.kemal.randomp.util.IExtendablePeripheral;
import me.kemal.randomp.util.Peripheral;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class TileRandomPMachine extends TileEntity
		implements IInventory, ISidedInventory, IExtendablePeripheral, IReconfigurableFacing, IReconfigurableSides, ISidedTexture {
	protected Peripheral peripheral;
	protected int[] ioConfiguration;
	protected ItemStack[] inventory;
	protected int facing;

	public static final int SIDES_COUNT = 6;

	public TileRandomPMachine(String peripheralType) {
		peripheral = new Peripheral();
		peripheral.setType(peripheralType);
		facing = 1;
		ioConfiguration = new int[6];
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("outputDir", (byte) facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		facing = tag.getInteger("outputDir");
	}

	@Override
	public Peripheral getPeripheral() {
		return peripheral;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle)
			throws LuaException, FunctionNotFoundException {
		throw new FunctionNotFoundException();
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
		return facing;
	}

	@Override
	public boolean allowYAxisFacing() {
		return false;
	}

	public void updateAllBlocks() {
		worldObj.markBlockForUpdate(xCoord + 1, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord - 1, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord + 1, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord - 1, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord + 1);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord - 1);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean rotateBlock() {
		facing++;
		if (facing > 5)
			facing = 0;
		updateAllBlocks();
		return true;

	}

	@Override
	public boolean setFacing(int side) {
		facing = side;
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		int[] slots = new int[inventory.length];
		for (int i = 0; i < inventory.length; i++)
			slots[i] = i;
		return slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
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

	@Override
	public void attachToComputer(IComputerAccess computer) {
	}

	@Override
	public void detachFromComputer(IComputerAccess computer) {
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.func_148857_g());
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

}
