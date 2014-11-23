package me.kemal.randomp.util;

import java.util.HashMap;

import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.inventory.ComparableItemStackNBT;
import cofh.lib.util.ItemWrapper;
import me.kemal.randomp.RandomPeripheral;
import net.minecraft.entity.item.EntityItem;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

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
	
	//Regex!
	/**
	 * TODO: Needs to be more dynamic!!!!
	 * An function for parsing string based lambda like expressions
	 * Usage: with an input string of "%a==%b" then it compares aObj and bObj %a>0
	 * @param lambda string to parse
	 * @param aObj first object
	 * @param bObj second object is only needed for == and !=
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
				return (Double)aObj > b;
			} else if (lambda.matches("^(%a)(<)(\\d+)")) {
				double b = Double.parseDouble(lambda.replaceAll("^(%a)(<)(\\d+)", "$3"));
				return (Double)aObj < b;
			} else if (lambda.matches("^(%a)(>=)(\\d+)")) {
				double b = Double.parseDouble(lambda.replaceAll("^(%a)(>=)(\\d+)", "$3"));
				return (Double)aObj < b;
			}else if(lambda.matches("^(%a)(==)(\\d+)")){
				double b = Double.parseDouble(lambda.replaceAll("^(%a)(<=)(\\d+)", "$3"));
				return (Double)aObj < b;
			}
		} catch (Exception e) {
			RandomPeripheral.logger.info("Exception in parsing a pseudo lambda: " + e.getMessage());
		}
		return false;
	}
}
