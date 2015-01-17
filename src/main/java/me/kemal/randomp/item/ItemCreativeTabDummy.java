package me.kemal.randomp.item;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import me.kemal.randomp.RandomPeripheral;
import me.kemal.randomp.util.CCUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCreativeTabDummy extends Item {
	public static final String itemName = "creativeTabDummy";

	public ItemCreativeTabDummy() {
		setUnlocalizedName(itemName);
		setCreativeTab(RandomPeripheral.tabRandomP);

		GameRegistry.registerItem(this, itemName);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List itemList) {
		itemList.add(CCUtils.GetTurtleWithPeripheral(false, RandomPeripheral.inventoryTurtleUpgradeID));
		itemList.add(CCUtils.GetTurtleWithPeripheral(true, RandomPeripheral.inventoryTurtleUpgradeID));
		itemList.add(CCUtils.GetTurtleWithPeripheral(false, RandomPeripheral.dispenserTurtleUpgradeID));
		itemList.add(CCUtils.GetTurtleWithPeripheral(true, RandomPeripheral.dispenserTurtleUpgradeID));
	}
}
