package me.kemal.randomp.util;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.lua.LuaException;
import me.kemal.randomp.RandomPeripherals;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidRegistry.FluidRegisterEvent;

public class CCUtils {

	public static HashMap<String, Object> stackToMap(ItemStack stack) {
		if (stack == null)
			return null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("internalName", Item.itemRegistry.getNameForObject(stack.getItem()));
		map.put("damage", stack.getItemDamage());
		map.put("amount", stack.stackSize);
		map.put("name", stack.getDisplayName());
		if (stack.getTagCompound() != null)
			map.put("nbtdata", CCUtils.NBTCompoundToMap(stack.getTagCompound()));
		return map;
	}

	public static HashMap<String, Object> fluidStackToMap(FluidStack stack) {
		if (stack == null)
			return null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("unlocalizedName", FluidRegistry.getFluidName(stack.getFluid()));
		map.put("amount", stack.amount);
		map.put("name", stack.getLocalizedName());
		if (stack.tag != null)
			map.put("nbtdata", CCUtils.NBTCompoundToMap(stack.tag));
		return map;
	}

	public static Map<Integer, Object> arrayToLuaArray(Object[] array) {
		HashMap<Integer, Object> map = new HashMap<Integer, Object>();
		int iterator = 0;
		boolean isCCType = (array instanceof CCType[]);
		for (Object obj : array) {
			map.put(iterator + 1, (isCCType) ? ((CCType[]) array)[iterator].toHashMap() : array[iterator]);
			iterator++;
		}
		return map;
	}

	public static Map<String, Object> NBTCompoundToMap(NBTTagCompound compound) {
		HashMap<String, Object> nbtMap = new HashMap<String, Object>();
		Object[] nbtSet = compound.func_150296_c().toArray();
		for (int i = 0; i < nbtSet.length; i++) {
			nbtMap.put(nbtSet[i].toString(), Util.getRealNBTType(compound.getTag((String) nbtSet[i])));
		}
		return nbtMap;
	}

	public static ItemStack getTurtleStackWithPeripheral(boolean advanced, int upgradeID) {
		ItemStack turtle = !advanced ? GameRegistry.findItemStack("ComputerCraft", "CC-TurtleExpanded", 1)
				: GameRegistry.findItemStack("ComputerCraft", "CC-TurtleAdvanced", 1);

		NBTTagCompound tag = turtle.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			turtle.setTagCompound(tag);
		}

		tag.setShort("leftUpgrade", (short) upgradeID);
		return turtle;
	}
}
