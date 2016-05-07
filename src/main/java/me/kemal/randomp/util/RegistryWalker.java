package me.kemal.randomp.util;

import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import me.kemal.randomp.RandomPeripherals;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class RegistryWalker {
	public HashMap<String, ItemStack> items = new HashMap<>();

	public void walk() {
		FMLControlledNamespacedRegistry<Item> itemRegistry = GameData.getItemRegistry();

		Set keys = itemRegistry.getKeys();
		List subItems = new Vector<ItemStack>();
		for (Object key : keys) {
			subItems.clear();
			Item item = (Item) itemRegistry.getObject(key);

			if (item.getHasSubtypes()) {
				for (CreativeTabs tab : item.getCreativeTabs()) {
					item.getSubItems(item, tab, subItems);
				}

				for (Object subitem : subItems) {
					ItemStack stack = (ItemStack) subitem;
					items.put((String) key + ":" + stack.getItemDamage(), stack);
				}
			} else {
				items.put((String) key, new ItemStack(item));
			}
		}
	}
}
