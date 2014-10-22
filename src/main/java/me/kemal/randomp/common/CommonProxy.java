package me.kemal.randomp.common;

import net.minecraftforge.client.event.TextureStitchEvent;
import me.kemal.randomp.te.TileUniversalInterface;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CommonProxy {
	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileUniversalInterface.class, "TEUniversalInterface");
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
	}

}
