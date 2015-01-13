package me.kemal.randomp.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import me.kemal.randomp.te.TileEnergyStorage;
import me.kemal.randomp.te.TileHologramProjector;
import me.kemal.randomp.te.TileUniversalInterface;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CommonProxy {
	@SideOnly(Side.CLIENT)
	public ResourceLocation blockResLoc;
	@SideOnly(Side.CLIENT)
	public ResourceLocation itemResLoc;
	
	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileUniversalInterface.class,
				"TEUniversalInterface");
		GameRegistry.registerTileEntity(TileEnergyStorage.class,
				"TEEnergyStorage");
		GameRegistry.registerTileEntity(TileHologramProjector.class,
				"TEHologramProjector");
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void getTextureAtlas(TextureStitchEvent.Post event){
	}

	@SideOnly(Side.CLIENT)
	public void registerRenderer() {
	}

}
