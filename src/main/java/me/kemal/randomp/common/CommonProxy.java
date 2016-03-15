package me.kemal.randomp.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import me.kemal.randomp.te.TileEnergyStorage;
import me.kemal.randomp.te.TileHologram;
import me.kemal.randomp.te.TileHologramProjector;
import me.kemal.randomp.te.TileUniversalInterface;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CommonProxy {
	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileUniversalInterface.class, "TEUniversalInterface");
		GameRegistry.registerTileEntity(TileHologramProjector.class, "TEHologramProjector");
		GameRegistry.registerTileEntity(TileHologram.class, "TEHologram");
	}

	public void registerRenderer() {
	}

}
