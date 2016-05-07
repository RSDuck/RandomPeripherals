package me.kemal.randomp.net;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import me.kemal.randomp.RandomPeripherals;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class Packets {
	public static final short ChangeMaxPowerOutput = 0;
	public static final short ChangeMaxPowerInput = 1;
	public static final short RotateBlock = 2;
	public static final short ImageMessage = 3;

	public static void sendToServer(IMessage msg) {
		RandomPeripherals.networkWrapper.sendToAll(msg);
	}

	public static void sendToServer(short packet, TileEntity te, Object... args) {
		sendToServer(new TileMessage(packet, te, args));
	}

	public static void sendImagesToServer(ItemStack[] stacks, int width, int height, Integer[] firstStack, Integer[] stacksCount,
			byte[][] imageData) {
		RandomPMessage msg = new RandomPMessage(ImageMessage, false);
		msg.buff.writeInt(stacks.length);
		for (ItemStack stack : stacks)
			ByteBufUtils.writeItemStack(msg.buff, stack);

		msg.buff.writeInt(width);
		msg.buff.writeInt(height);

		msg.buff.writeInt(imageData.length);
		int i = 0;
		for (byte[] image : imageData) {
			msg.buff.writeInt(firstStack[i].intValue());
			msg.buff.writeInt(stacksCount[i].intValue());
			msg.buff.writeInt(image.length);
			msg.buff.writeBytes(image);

			i++;
		}
		sendToServer(msg);

	}
}
