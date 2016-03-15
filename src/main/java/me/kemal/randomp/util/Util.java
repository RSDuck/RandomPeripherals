package me.kemal.randomp.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.inventory.ComparableItemStackNBT;
import cofh.lib.util.ItemWrapper;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import me.kemal.randomp.RandomPeripherals;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

public class Util {

	public static String NBTToString(NBTBase obj) {
		if (obj instanceof NBTPrimitive) {
			return ((Double) ((NBTPrimitive) obj).func_150286_g()).toString();
		}
		if (obj instanceof NBTTagIntArray) {
			int[] array = ((NBTTagIntArray) obj).func_150302_c();
			String output = "[";
			for (int i = 0; i < array.length; i++) {
				output += array[i] + ((i == array.length - 1) ? "" : ", ");
			}
			return output + "]";
		}
		if (obj instanceof NBTTagByteArray) {
			byte[] array = ((NBTTagByteArray) obj).func_150292_c();
			String output = "[";
			for (int i = 0; i < array.length; i++) {
				output += array[i] + ((i == array.length - 1) ? "" : ", ");
			}
			return output + "]";
		}
		if (obj instanceof NBTTagString) {
			return ((NBTTagString) obj).func_150285_a_();
		}
		if (obj instanceof NBTTagCompound) {
			String output = "{";
			Object[] nbtSet = ((NBTTagCompound) obj).func_150296_c().toArray();
			for (int i = 0; i < nbtSet.length; i++) {
				output += (String) nbtSet[i] + " : " + NBTToString(((NBTTagCompound) obj).getTag((String) nbtSet[i]))
						+ ((i == nbtSet.length - 1) ? "" : ", ");
			}
			return output + "}";
		}
		if (obj instanceof NBTTagList) {
			NBTTagList list = ((NBTTagList) obj);
			String output = " [ ";
			NBTTagList copy = (NBTTagList) list.copy();
			int i = 0;
			while (copy.tagCount() > 0) {
				output += NBTToString(copy.removeTag(0)) + ((i == list.tagCount() - 1) ? "" : ", ");
				i++;
			}
			return output;
		}
		return "{}";
	}

	public static Object getRealNBTType(NBTBase obj) {
		if (obj instanceof NBTPrimitive) {
			// System.out.println("Type: Primitive");
			return ((NBTPrimitive) obj).func_150286_g();
		}
		if (obj instanceof NBTTagIntArray) {
			// System.out.println("Type: Int Array");
			int[] array = ((NBTTagIntArray) obj).func_150302_c();
			HashMap<Integer, Object> map = new HashMap<Integer, Object>();
			for (int i = 0; i < array.length; i++) {
				map.put(i + 1, array[i]);
			}
			return map;
		}
		if (obj instanceof NBTTagByteArray) {
			// System.out.println("Type: Byte Array");
			byte[] array = ((NBTTagByteArray) obj).func_150292_c();
			HashMap<Integer, Object> map = new HashMap<Integer, Object>();
			for (int i = 0; i < array.length; i++) {
				map.put(i + 1, array[i]);
			}
			return map;
		}
		if (obj instanceof NBTTagString) {
			// System.out.println("Type: String");
			return ((NBTTagString) obj).func_150285_a_();
		}
		if (obj instanceof NBTTagCompound) {
			// System.out.println("Type: Compound");
			return CCUtils.NBTCompoundToMap((NBTTagCompound) obj);
		}
		if (obj instanceof NBTTagList) {
			// System.out.println("Type: List ");
			NBTTagList list = ((NBTTagList) obj);
			HashMap<Integer, Object> map = new HashMap<Integer, Object>();
			NBTTagList copy = (NBTTagList) list.copy();
			int i = 0;
			while (copy.tagCount() > 0) {
				map.put(i + 1, getRealNBTType(copy.removeTag(0)));
				i++;
			}
			return map;
		}
		return obj.toString();
	}

	public static void dropStack(World world, int x, int y, int z, ItemStack stack) {
		EntityItem itemEntity = new EntityItem(world);
		float xOffset = (float) (0.45f * Math.random());
		float yOffset = (float) (0.45f * Math.random());
		float zOffset = (float) (0.45f * Math.random());
		itemEntity.setPosition(x + xOffset, y + yOffset, z + xOffset);
		world.spawnEntityInWorld(itemEntity);
	}

	public static int canStack(ItemStack stackA, ItemStack stackB) {
		if (stackA == null)
			return stackB.getMaxStackSize();
		if (stackB == null)
			return -1;
		if (ItemHelper.itemsEqualForCrafting(stackA, stackB) && stackA.stackSize < stackA.getMaxStackSize())
			if (stackA.stackSize + stackB.stackSize > stackA.getMaxStackSize()) {

			}

		return -1;
	}

	public static int readableDirToForgeDir(String dir) {
		int intDir = -1;
		final String[] dirs = new String[] { "bottom", "top", "north", "south", "west", "east" };
		for (int i = 0; i < dirs.length; i++)
			if (dirs[i].indexOf(dir) != -1) {
				intDir = i;
				break;
			}
		return intDir;
	}

	public static ItemStack suckStack(TileEntity a, ItemStack stack, int whereB, int fromWhere) {
		World worldObj = a.getWorldObj();
		int xCoord = a.xCoord;
		int yCoord = a.yCoord;
		int zCoord = a.zCoord;
		int[] pos = Util.dirToCoord(whereB);
		ItemStack heldStack = (stack != null) ? stack.copy() : null;
		TileEntity te = worldObj.getTileEntity(xCoord + pos[0], yCoord + pos[1], zCoord + pos[2]);
		boolean sided = te instanceof ISidedInventory;
		if (te instanceof IInventory) {
			int side = fromWhere;
			IInventory inv = (IInventory) te;
			int invSize = (sided) ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side).length : inv.getSizeInventory();
			int[] slots = (sided) ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side) : new int[] {};
			for (int i = 0; i < invSize; i++) {
				int slot = (sided) ? slots[i] : i;
				if (inv.getStackInSlot(slot) == null)
					continue;
				int decr = Util.canStack(heldStack, inv.getStackInSlot(slot));
				if (sided) {
					if (!(((ISidedInventory) inv).canExtractItem(slot, inv.getStackInSlot(slot), side))) {
						continue;
					}
				}
				if (decr != -1) {
					ItemStack decrStack = inv.decrStackSize(slot, decr);
					if (heldStack != null)
						heldStack.stackSize += decrStack.stackSize;
					else
						heldStack = decrStack;
					return decrStack;
				}
			}
		}
		return null;
	}

	public static ItemStack pushStack(TileEntity tile, IInventory inventory, int whereB, ItemStack stack, int fromWhere) {
		World worldObj = tile.getWorldObj();
		int xCoord = tile.xCoord;
		int yCoord = tile.yCoord;
		int zCoord = tile.zCoord;
		ItemStack heldStack = (stack != null) ? stack.copy() : null;
		if (heldStack == null)
			return null;
		int[] pos = Util.dirToCoord(whereB);
		TileEntity te = worldObj.getTileEntity(xCoord + pos[0], yCoord + pos[1], zCoord + pos[2]);
		boolean sided = te instanceof ISidedInventory;
		if (te instanceof IInventory) {
			IInventory inv = (IInventory) te;
			int side = fromWhere;
			int invSize = (sided) ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side).length : inv.getSizeInventory();
			int[] slots = (sided) ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side) : new int[] {};
			for (int i = 0; i < invSize; i++) {
				int slot = (sided) ? slots[i] : i;
				int stackable = Util.canStack(inv.getStackInSlot(slot), heldStack);
				if (sided) {
					if (!((ISidedInventory) inv).canInsertItem(slot, heldStack, side)) {
						continue;
					}
				}
				if (stackable != -1) {
					ItemStack decrStack = inventory.decrStackSize(0, stackable);
					if (inv.getStackInSlot(slot) != null) {
						inv.getStackInSlot(slot).stackSize += decrStack.stackSize;
					} else
						inv.setInventorySlotContents(slot, decrStack);
					return decrStack;
				}
			}
		}
		return null;
	}

	public String getModIDofStr(String input) {
		return input.substring(0, input.indexOf(":"));
	}

	public String getBlockNameofStr(String input) {
		return input.substring(input.indexOf(":") + 1, input.length());
	}

	public static int readableRelDirToRelForgeDir(String dir) {
		int output = -1;
		final String[] dirs = new String[] { "bottom", "top", "front", "back", "left", "right" };
		for (int i = 0; i < dirs.length; i++)
			if (dirs[i].indexOf(dir) != -1) {
				output = i;
				break;
			}
		return output;
	}

	public static int turtleDirToAbsDir(int turtleDir, String dir) {
		int output = -1;
		final String[] dirs = new String[] { "left", "right", "back", "front", "bottom", "top" };
		for (int i = 0; i < dirs.length; i++) {
			if (dirs[i].indexOf(dir) != -1)
				output = i;
		}
		if (output == -1)
			return output;
		return (int) BlockHelper.ICON_ROTATION_MAP[turtleDir][output];
	}

	public static int relDirToAbsDir(int dirA, int dirB) {
		switch (dirB) {
			case 2:
				return dirA;
			case 3:
				return BlockHelper.SIDE_OPPOSITE[dirA];
			case 0:
				return BlockHelper.SIDE_BELOW[dirA];
			case 1:
				return BlockHelper.SIDE_ABOVE[dirA];
			case 4:
				return BlockHelper.SIDE_LEFT[dirA];
			case 5:
				return BlockHelper.SIDE_RIGHT[dirA];
			default:
				return -1;
		}
	}

	public static int[] dirToCoord(int dir) {
		switch (dir) {
			case 0:
				return new int[] { 0, -1, 0 };
			case 1:
				return new int[] { 0, 1, 0 };
			case 2:
				return new int[] { 0, 0, 1 };
			case 3:
				return new int[] { 0, 0, -1 };
			case 4:
				return new int[] { -1, 0, 0 };
			case 5:
				return new int[] { 1, 0, 0 };
			default:
				return new int[] { 0, 0, 0 };
		}
	}

	public static int getSideFromRelativeCoordinates(int x, int y, int z, int x2, int y2, int z2) {
		int side = 0;
		if (x < x2)
			side = 5;
		else if (x > x2)
			side = 4;
		else if (z < z2)
			side = 3;
		else if (z > z2)
			side = 2;
		else if (y < y2)
			side = 1;
		else if (y > y2)
			side = 0;
		return side;
	}

	public static int whichOneMatches(String[] which, String search) {
		int found = -1;
		for (int i = 0; i < which.length; i++)
			found = (which[i] == search) ? i : found;
		return found;
	}

}
