package me.kemal.randomp.util;

import java.util.HashMap;
import java.util.Map;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CCUtil {

	public static HashMap<String, Object> stackToMap(ItemStack stack) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("unlocalizedName", stack.getItem().getUnlocalizedName());
		map.put("damage", stack.getItemDamage());
		map.put("amount", stack.stackSize);
		map.put("name", stack.getDisplayName());
		if (stack.getTagCompound() != null)
			map.put("nbtdata", CCUtil.NBTCompoundToMap(stack.getTagCompound()));
		return map;
	}

	public static boolean IsValidNumber(Object number) {
		return (number instanceof Double);
	}

	public static boolean IsValidBool(Object bool) {
		return (bool instanceof Boolean);
	}

	public static int ToInt(Object number) {
		return ((Number) number).intValue();
	}

	public static String ToString(Object string) {
		return ((String) string);
	}

	public static int CanStack(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null) {
			return 64;
		}
		if (stack2 != null && stack2.getItem().equals(stack1.getItem())
				&& (!stack1.getHasSubtypes() || stack1.getItemDamage() == stack2.getItemDamage())
				&& ItemStack.areItemStackTagsEqual(stack1, stack2)) {
			if (stack1.stackSize + stack2.stackSize > 64) {
				return stack1.getMaxStackSize() - (((stack2.stackSize + stack1.stackSize) - 64));
			}
			return stack1.stackSize + stack2.stackSize;
		} else
			return -1;
	}

	public static Map<String, Object> NBTCompoundToMap(NBTTagCompound compound) {
		HashMap<String, Object> nbtMap = new HashMap<String, Object>();
		Object[] nbtSet = compound.func_150296_c().toArray();
		for (int i = 0; i < nbtSet.length; i++) {
			nbtMap.put(nbtSet[i].toString(), Util.getRealNBTType(compound.getTag((String) nbtSet[i])));
		}
		return nbtMap;
	}

	public static Map<Integer, Object> ArrayToLuaArray(Object[] array) {
		HashMap<Integer, Object> map = new HashMap<Integer, Object>();
		int iterator = 0;
		for (Object obj : array) {
			map.put(iterator + 1, array[iterator]);
			iterator++;
		}
		return map;
	}

	public static boolean IsValidString(Object string) {
		return (string instanceof String);
	}

	public static boolean ToBool(Object bool) {
		return (Boolean) bool;
	}

	public static int[] DirToCoord(int dir) {
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

	public static boolean checkArgument(Object[] arguments, Object[] check, Class<?>... types) {
		if (arguments.length == types.length) {
			for (int i = 0; i < arguments.length; i++)
				if (!(types[i].isInstance(arguments))) {
					return false;
				} else {
					// throw new
					// LuaException("Internal Error: argument lenght is not equal to the lenght of types");
					return false; // ...
				}
			return true;
		}
		return false;
	}

}
