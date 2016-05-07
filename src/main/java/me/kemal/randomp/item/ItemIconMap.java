package me.kemal.randomp.item;

import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.media.IMedia;
import me.kemal.randomp.RandomPeripherals;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemIconMap extends Item implements IMedia {
	String idName = "iconMapDisk";

	IIcon icon;

	public ItemIconMap() {
		setUnlocalizedName(idName);
		setCreativeTab(RandomPeripherals.tabRandomP);
		setMaxStackSize(1);

		GameRegistry.registerItem(this, idName);
	}

	@Override
	public void registerIcons(IIconRegister ir) {
		itemIcon = ir.registerIcon("randomperipherals:itemMapIcons");
	}

	@Override
	public String getLabel(ItemStack stack) {
		return "IconMap Disk";
	}

	@Override
	public boolean setLabel(ItemStack stack, String label) {
		return false;
	}

	@Override
	public String getAudioTitle(ItemStack stack) {
		return null;
	}

	@Override
	public String getAudioRecordName(ItemStack stack) {
		return null;
	}

	@Override
	public IMount createDataMount(ItemStack stack, World world) {
		return RandomPeripherals.iconMapImagesMount;
	}
}
