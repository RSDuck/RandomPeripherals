package me.kemal.randomp.net;

import me.kemal.randomp.te.TileUniversalInterface;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ServerPacketHandler implements IMessageHandler<RandomPMSG, IMessage> {

	@Override
	public IMessage onMessage(RandomPMSG message, MessageContext ctx) {
		return null;
	}

	public static void readData(ByteBuf buff) {
		TileEntity te;

		World world = DimensionManager.getWorld(buff.readInt());
		int x = buff.readInt();
		int y = buff.readInt();
		int z = buff.readInt();

		te = world.getTileEntity(x, y, z);

		switch (buff.readShort()) {
			case Packets.ChangeMaxPowerInput: {
				TileUniversalInterface ui = (TileUniversalInterface) te;
				int newInput = buff.readInt();
				ui.getEnergyStorage().setMaxReceive(newInput);
			}
				break;
			case Packets.ChangeMaxPowerOutput: {
				TileUniversalInterface ui = (TileUniversalInterface) te;
				int newOutput = buff.readInt();
				ui.getEnergyStorage().setMaxExtract(newOutput);
			}
				break;
		}
	}
}
