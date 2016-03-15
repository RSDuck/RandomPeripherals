package me.kemal.randomp.item;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.util.CCUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCreativeTabDummy extends Item {
	public static final String itemName = "creativeTabDummy";

	public ItemCreativeTabDummy() {
		setUnlocalizedName(itemName);
		setCreativeTab(RandomPeripherals.tabRandomP);

		GameRegistry.registerItem(this, itemName);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List itemList) {
		itemList.add(CCUtils.getTurtleStackWithPeripheral(false, RandomPeripherals.inventoryTurtleUpgradeID));
		itemList.add(CCUtils.getTurtleStackWithPeripheral(true, RandomPeripherals.inventoryTurtleUpgradeID));
		itemList.add(CCUtils.getTurtleStackWithPeripheral(false, RandomPeripherals.dispenserTurtleUpgradeID));
		itemList.add(CCUtils.getTurtleStackWithPeripheral(true, RandomPeripherals.dispenserTurtleUpgradeID));
	}
}
