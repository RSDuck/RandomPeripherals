package me.kemal.randomp.util;

import java.util.HashMap;
import java.util.Map;

import cofh.lib.util.helpers.BlockHelper;
import dan200.computercraft.api.lua.LuaException;
import me.kemal.randomp.RandomPeripheral;
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

	public static Object[] getHelp(ICCHelpCreator help) {

		return null;
	}

	public static Map<String, Object> NBTCompoundToMap(NBTTagCompound compound) {
		HashMap<String, Object> nbtMap = new HashMap<String, Object>();
		Object[] nbtSet = compound.func_150296_c().toArray();
		for (int i = 0; i < nbtSet.length; i++) {
			nbtMap.put(nbtSet[i].toString(), Util.getRealNBTType(compound.getTag((String) nbtSet[i])));
		}
		return nbtMap;
	}

	public static int TurtleDirToForgeDir(int turtleDir, String dir) {
		int output = -1;
		RandomPeripheral.logger.info("Turtle Dir: " + turtleDir + " Dir: " + dir);
		String[] dirs = new String[] { "top", "bottom", "front", "back", "right", "left" };
		for (int i = 0; i < dirs.length; i++) {
			RandomPeripheral.logger.info("dirs[i] = " + dirs[i] + "|dirs[i] == dir = " + (dirs[i] == dir));
			if (dirs[i].startsWith(dir) || dirs[i].endsWith(dir))
				output = i;
		}
		if (output == -1)
			return output;
		return (int) BlockHelper.ICON_ROTATION_MAP[output][(turtleDir + 1) % 4];
	}
}
