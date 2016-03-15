package me.kemal.randomp.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldAccess;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.client.renderer.RendererHologramProjector;
import me.kemal.randomp.client.renderer.TileHologramSpecialRenderer;
import me.kemal.randomp.common.CommonProxy;
import me.kemal.randomp.te.TileHologram;
import me.kemal.randomp.te.TileHologramProjector;

public class ClientProxy extends CommonProxy {
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(Pre event) {
		if (event.map.getTextureType() == 1) {
			IconRegistry.addIcon("IconConfig", "cofh:icons/Icon_Config", event.map);
			IconRegistry.addIcon("IconArrowUp", "cofh:icons/Icon_ArrowUp", event.map);
			IconRegistry.addIcon("IconArrowDown", "cofh:icons/Icon_ArrowDown", event.map);
			IconRegistry.addIcon("IconEnergy", "cofh:icons/Icon_Energy", event.map);
			IconRegistry.addIcon("IconPlus", "randomperipherals:icons/Icon_Plus", event.map);
			IconRegistry.addIcon("IconMinus", "randomperipherals:icons/Icon_Minus", event.map);
			IconRegistry.addIcon("IconButton", "cofh:icons/Icon_Button", event.map);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderer() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileHologram.class, new TileHologramSpecialRenderer());
	}
}
