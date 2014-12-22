package me.kemal.randomp.util;

import java.util.HashMap;

import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.inventory.ComparableItemStackNBT;
import cofh.lib.util.ItemWrapper;
import me.kemal.randomp.RandomPeripheral;
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
	public static Object getRealNBTType(NBTBase obj) {
		if (obj instanceof NBTPrimitive) {
			System.out.println("Type: Primitive");
			return ((NBTPrimitive) obj).func_150286_g();
		}
		if (obj instanceof NBTTagIntArray) {
			System.out.println("Type: Int Array");
			int[] array = ((NBTTagIntArray) obj).func_150302_c();
			HashMap<Integer, Object> map = new HashMap<Integer, Object>();
			for (int i = 0; i < array.length; i++) {
				map.put(i + 1, array[i]);
			}
			return map;
		}
		if (obj instanceof NBTTagByteArray) {
			System.out.println("Type: Byte Array");
			byte[] array = ((NBTTagByteArray) obj).func_150292_c();
			HashMap<Integer, Object> map = new HashMap<Integer, Object>();
			for (int i = 0; i < array.length; i++) {
				map.put(i + 1, array[i]);
			}
			return map;
		}
		if (obj instanceof NBTTagString) {
			System.out.println("Type: String");
			return ((NBTTagString) obj).func_150285_a_();
		}
		if (obj instanceof NBTTagCompound) {
			System.out.println("Type: Compound");
			return CCUtil.NBTCompoundToMap((NBTTagCompound) obj);
		}
		if (obj instanceof NBTTagList) {
			System.out.println("Type: List ");
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

	public static boolean ToBool(int var) {
		return var == 1;
	}

	public static void DropStack(World world, int x, int y, int z, ItemStack stack) {
		EntityItem itemEntity = new EntityItem(world);
		float xOffset = (float) (0.45f * Math.random());
		float yOffset = (float) (0.45f * Math.random());
		float zOffset = (float) (0.45f * Math.random());
		itemEntity.setPosition(x + xOffset, y + yOffset, z + xOffset);
		world.spawnEntityInWorld(itemEntity);
	}

	public static boolean IsNumber(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// Regex!
	/**
	 * TODO: Needs to be more dynamic!!!! An function for parsing string based
	 * lambda like expressions Usage: with an input string of "%a==%b" then it
	 * compares aObj and bObj %a>0
	 * 
	 * @param lambda
	 *            string to parse
	 * @param aObj
	 *            first object
	 * @param bObj
	 *            second object is only needed for == and !=
	 * @return the result of comparing
	 */
	public static boolean ParsePseudoLambda(String lambda, Object aObj, Object bObj) {
		try {
			if (lambda.matches("^(%a)(==)(%b)")) {
				return aObj == bObj;
			} else if (lambda.matches("^(%a)(!=)(%b)")) {
				return aObj != bObj;
			} else if (lambda.matches("^(%a)(>)(\\d+)")) {
				double b = Double.parseDouble(lambda.replaceAll("^(%a)(>)(\\d+)", "$3"));
				return (Double) aObj > b;
			} else if (lambda.matches("^(%a)(<)(\\d+)")) {
				double b = Double.parseDouble(lambda.replaceAll("^(%a)(<)(\\d+)", "$3"));
				return (Double) aObj < b;
			} else if (lambda.matches("^(%a)(>=)(\\d+)")) {
				double b = Double.parseDouble(lambda.replaceAll("^(%a)(>=)(\\d+)", "$3"));
				return (Double) aObj < b;
			} else if (lambda.matches("^(%a)(==)(\\d+)")) {
				double b = Double.parseDouble(lambda.replaceAll("^(%a)(<=)(\\d+)", "$3"));
				return (Double) aObj < b;
			}
		} catch (Exception e) {
			RandomPeripheral.logger.info("Exception in parsing a pseudo lambda: " + e.getMessage());
		}
		return false;
	}

	public static String ToString(Object string) {
		return ((String) string);
	}

	public static int ToInt(Object number) {
		return ((Number) number).intValue();
	}

	public static int CanStack(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null) {
			return 64;
		}
		if (stack2 != null && stack2.getItem().equals(stack1.getItem()) && (!stack1.getHasSubtypes() || stack1.getItemDamage() == stack2.getItemDamage())
				&& ItemStack.areItemStackTagsEqual(stack1, stack2)) {
			if (stack1.stackSize + stack2.stackSize > 64) {
				return stack1.getMaxStackSize() - (((stack2.stackSize + stack1.stackSize) - 64));
			}
			return stack1.stackSize + stack2.stackSize;
		} else
			return -1;
	}

	public static int ReadableDirToForgeDir(String dir) {
		int intDir = -1;
		final String[] dirs = new String[] { "bottom", "top", "north", "south", "west", "east" };
		for (int i = 0; i < dirs.length; i++)
			if (dirs[i].indexOf(dir) != -1) {
				intDir = i;
				break;
			}
		return intDir;
	}

	public static ItemStack SuckStack(TileEntity a, ItemStack stack, int whereB, int fromWhere) {
		World worldObj = a.getWorldObj();
		int xCoord = a.xCoord;
		int yCoord = a.yCoord;
		int zCoord = a.zCoord;
		int[] pos = CCUtil.DirToCoord(Util.ToInt(whereB));
		ItemStack heldStack = stack.copy();
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
				int decr = Util.CanStack(heldStack, inv.getStackInSlot(slot));
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

	public static ItemStack PushStack(TileEntity tile, IInventory inventory, int whereB, ItemStack stack, int fromWhere) {
		World worldObj = tile.getWorldObj();
		int xCoord = tile.xCoord;
		int yCoord = tile.yCoord;
		int zCoord = tile.zCoord;
		ItemStack heldStack = stack.copy();
		if (heldStack == null)
			return null;
		int[] pos = CCUtil.DirToCoord(whereB);
		TileEntity te = worldObj.getTileEntity(xCoord + pos[0], yCoord + pos[1], zCoord + pos[2]);
		boolean sided = te instanceof ISidedInventory;
		if (te instanceof IInventory) {
			IInventory inv = (IInventory) te;
			int side = fromWhere;
			int invSize = (sided) ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side).length : inv.getSizeInventory();
			int[] slots = (sided) ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side) : new int[] {};
			for (int i = 0; i < invSize; i++) {
				int slot = (sided) ? slots[i] : i;
				int stackable = Util.CanStack(inv.getStackInSlot(slot), heldStack);
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
}
