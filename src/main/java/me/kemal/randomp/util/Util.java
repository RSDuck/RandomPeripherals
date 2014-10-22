package me.kemal.randomp.util;

import java.util.HashMap;
import java.util.Map;

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
import net.minecraft.server.management.PlayerManager.PlayerInstance;
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
				map.put(i, array[i]);
			}
			return map;
		}
		if (obj instanceof NBTTagByteArray) {
			System.out.println("Type: Byte Array");
			byte[] array = ((NBTTagByteArray) obj).func_150292_c();
			HashMap<Integer, Object> map = new HashMap<Integer, Object>();
			for (int i = 0; i < array.length; i++) {
				map.put(i, array[i]);
			}
			return map;
		}
		if (obj instanceof NBTTagString) {
			System.out.println("Type: String");
			return ((NBTTagString) obj).func_150285_a_();
		}
		if (obj instanceof NBTTagCompound) {
			System.out.println("Type: Compound");
			return NBTCompoundToMap((NBTTagCompound) obj);
		}
		if (obj instanceof NBTTagList) {
			System.out.println("Type: List ");
			NBTTagList list = ((NBTTagList) obj);
			HashMap<Integer, Object> map = new HashMap<Integer, Object>();
			NBTTagList copy = (NBTTagList) list.copy();
			int i = 0;
			while (copy.tagCount() > 0) {
				map.put(i, getRealNBTType(copy.removeTag(0)));
				i++;
			}
			return map;
		}
		return obj.toString();
	}

	public static Map<String, Object> NBTCompoundToMap(NBTTagCompound compound) {
		HashMap<String, Object> nbtMap = new HashMap<String, Object>();
		Object[] nbtSet = compound.func_150296_c().toArray();
		for (int i = 0; i < nbtSet.length; i++) {
			nbtMap.put(nbtSet[i].toString(), getRealNBTType(compound.getTag((String) nbtSet[i])));
		}
		return nbtMap;
	}

	public static boolean IsValidInt(Object number) {
		return (number instanceof Double);
	}

	public static boolean IsValidBool(Object bool) {
		return (bool instanceof Boolean);
	}

	public static int ToInt(Object number) {
		return ((Number) number).intValue();
	}

	public static boolean IsValidString(Object string) {
		return (string instanceof String);
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

	public static boolean IsNumber(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void DropStack(World world, int x, int y, int z, ItemStack stack) {
		EntityItem itemEntity = new EntityItem(world);
		float xOffset = (float) (0.45f * Math.random());
		float yOffset = (float) (0.45f * Math.random());
		float zOffset = (float) (0.45f * Math.random());
		itemEntity.setPosition(x + xOffset, y + yOffset, z + xOffset);
		world.spawnEntityInWorld(itemEntity);
	}

	public static boolean ToBool(int var) {
		return var == 1;
	}

	public static boolean ToBool(Object bool) {
		return (Boolean) bool;
	}

	// From the Minefactory Reloaded Source Code:
	// https://github.com/skyboy/MineFactoryReloaded/blob/master/src/powercrystals/minefactoryreloaded/net/Packets.java
	public static void sendToAllPlayersWatching(World world, int x, int y, int z, Packet packet) {
		if (packet == null)
			return;
		if (world instanceof WorldServer) {
			PlayerManager manager = ((WorldServer) world).getPlayerManager();
			if (manager == null)
				return;
			PlayerInstance watcher = manager.getOrCreateChunkWatcher(x >> 4, x >> 4, false);
			if (watcher != null)
				watcher.sendToAllPlayersWatchingChunk(packet);
		}
	}

	public static HashMap<String, Object> stackToMap(ItemStack stack) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("unlocalizedName", stack.getItem().getUnlocalizedName());
		map.put("damage", stack.getItemDamage());
		map.put("amount", stack.stackSize);
		map.put("name", stack.getDisplayName());
		if (stack.getTagCompound() != null)
			map.put("nbtdata", Util.NBTCompoundToMap(stack.getTagCompound()));
		return map;
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

}
