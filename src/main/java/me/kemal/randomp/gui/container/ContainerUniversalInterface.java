package me.kemal.randomp.gui.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.kemal.randomp.RandomPeripheral;
import me.kemal.randomp.te.TileUniversalInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ContainerUniversalInterface extends Container {
	protected TileUniversalInterface te;
	protected InventoryPlayer playerInv;

	private int lastFluidAmount;
	private int lastStoredEnergy;

	private int lastMaxOutput;
	private int lastMaxInput;

	private int _tankAmount;
	private int _tankFluidID;

	public ContainerUniversalInterface(InventoryPlayer playerInv, TileUniversalInterface inv) {
		this.te = inv;
		this.playerInv = playerInv;
		this.lastStoredEnergy = inv.getEnergyStored(ForgeDirection.DOWN);
		if (inv.getTank().getFluid() != null)
			this.lastFluidAmount = inv.getTank().getFluid().amount;
		else
			this.lastFluidAmount = -1;
		this.lastMaxInput = inv.getEnergyStorage().getMaxReceive();
		this.lastMaxOutput = inv.getEnergyStorage().getMaxExtract();
		addSlotToContainer(new Slot(inv, 0, 176 / 2 - (18 / 2), 32));
		bindPlayerInventory(playerInv);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return te.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		int machInvSize = te.getSizeInventory();
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			if (slot < machInvSize) {
				if (!mergeItemStack(stackInSlot, machInvSize, inventorySlots.size(), true)) {
					return null;
				}
			} else if (!mergeItemStack(stackInSlot, 0, machInvSize, false)) {
				return null;
			}
			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}
			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotStart, int slotRange, boolean reverse) {
		boolean successful = false;
		int slotIndex = !reverse ? slotStart : slotRange - 1;
		int iterOrder = !reverse ? 1 : -1;
		Slot slot;
		ItemStack existingStack;
		if (stack.isStackable()) {
			while (stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart)) {
				slot = (Slot) this.inventorySlots.get(slotIndex);
				existingStack = slot.getStack();
				if (slot.isItemValid(stack) && existingStack != null && existingStack.getItem().equals(stack.getItem())
						&& (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(stack, existingStack)) {
					int existingSize = existingStack.stackSize + stack.stackSize;
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					if (existingSize <= maxStack) {
						stack.stackSize = 0;
						existingStack.stackSize = existingSize;
						slot.onSlotChanged();
						successful = true;
					} else if (existingStack.stackSize < maxStack) {
						stack.stackSize -= maxStack - existingStack.stackSize;
						existingStack.stackSize = maxStack;
						slot.onSlotChanged();
						successful = true;
					}
				}
				slotIndex += iterOrder;
			}
		}
		if (stack.stackSize > 0) {
			slotIndex = !reverse ? slotStart : slotRange - 1;
			while (stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart)) {
				slot = (Slot) this.inventorySlots.get(slotIndex);
				existingStack = slot.getStack();
				if (slot.isItemValid(stack) && existingStack == null) {
					int maxStack = 64;
					existingStack = stack.splitStack(Math.min(stack.stackSize, maxStack));
					slot.putStack(existingStack);
					slot.onSlotChanged();
					successful = true;
				}
				slotIndex += iterOrder;
			}
		}
		return successful;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		int yOff = 84;
		int xOff = 8;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, xOff + j * 18, yOff + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, xOff + i * 18, yOff + 58));
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);
			if (te.getEnergyStored(ForgeDirection.DOWN) != lastStoredEnergy) {
				icrafting.sendProgressBarUpdate(this, 0, te.getEnergyStored(ForgeDirection.DOWN));
				lastStoredEnergy = te.getEnergyStored(ForgeDirection.DOWN);
			}
			if (te.getTank().getFluid() != null) {
				if (lastFluidAmount != te.getTank().getFluid().amount) {
					icrafting.sendProgressBarUpdate(this, 1, te.getTank().getFluid().amount);
					icrafting.sendProgressBarUpdate(this, 2, te.getTank().getFluid().fluidID);
					lastFluidAmount = te.getTank().getFluid().amount;
				}
			} else {
				icrafting.sendProgressBarUpdate(this, 1, 0);
				icrafting.sendProgressBarUpdate(this, 2, 0);
				lastFluidAmount = -1;
			}
			//if (te.getEnergyStorage().getMaxReceive() != lastMaxInput) {
			//	RandomPeripheral.logger.info("getMaxReceive(): " + te.getEnergyStorage().getMaxReceive()
			//			+ " lastMaxInput: " + lastMaxInput);
				icrafting.sendProgressBarUpdate(this, 103, te.getEnergyStorage().getMaxReceive());
			//	lastMaxInput = te.getEnergyStorage().getMaxReceive();
			//}
			//if (te.getEnergyStorage().getMaxExtract() != lastMaxOutput) {
				icrafting.sendProgressBarUpdate(this, 104, te.getEnergyStorage().getMaxExtract());
			//	lastMaxOutput = te.getEnergyStorage().getMaxExtract();
			//}
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int bar, int value) {
		switch (bar) {
			case 0:
				te.setEnergyStored(value);
				break;
			case 1:
				_tankAmount = value;
				break;
			case 2: {
				_tankFluidID = value;
				te.getTank().setFluid(
						FluidRegistry.getFluidStack(FluidRegistry.getFluidName(_tankFluidID), _tankAmount));
			}
			case 103:
				// RandomPeripheral.logger.info("Bar Update Input: " + value);
				te.getEnergyStorage().setMaxReceive(value);
				break;
			case 104:
				// RandomPeripheral.logger.info("Bar Update Output: "+value);
				te.getEnergyStorage().setMaxExtract(value);
				break;
			default:
				break;
		}
	}

}
