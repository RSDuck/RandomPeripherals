package me.kemal.randomp.net;

import net.minecraft.tileentity.TileEntity;
import me.kemal.randomp.RandomPeripherals;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class Packets {
	public static final short ChangeMaxPowerOutput = 0;
	public static final short ChangeMaxPowerInput = 1;
	public static final short RotateBlock = 2;

	public static void sendToServer(IMessage msg) {
		RandomPeripherals.networkWrapper.sendToAll(msg);
	}

	public static void sendToServer(short packet, TileEntity te, Object... args) {
		sendToServer(new RandomPMSG(packet, te, args));
	}
}
