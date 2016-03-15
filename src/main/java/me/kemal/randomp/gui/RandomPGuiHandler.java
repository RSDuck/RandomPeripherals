package me.kemal.randomp.gui;

import me.kemal.randomp.gui.client.GuiUniveralInterface;
import me.kemal.randomp.gui.container.ContainerUniversalInterface;
import me.kemal.randomp.te.TileUniversalInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class RandomPGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == RandomPGUIs.GUI_UNIVERSALINTERFACE.ordinal())
			return new ContainerUniversalInterface(player.inventory, (TileUniversalInterface) world.getTileEntity(x, y,
					z));
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == RandomPGUIs.GUI_UNIVERSALINTERFACE.ordinal())
			return new GuiUniveralInterface(player.inventory, (TileUniversalInterface) world.getTileEntity(x, y, z));
		return null;
	}

}
