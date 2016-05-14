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
	public static final short PrepareForImageMessages = 4;
	public static final short FinishedImageTransmitting = 5;

	public static void sendToServer(IMessage msg) {
		RandomPeripherals.networkWrapper.sendToAll(msg);
	}

	public static void sendToServer(short packet, TileEntity te, Object... args) {
		sendToServer(new TileMessage(packet, te, args));
	}

	public static void sendImagesToServer(ItemStack[] stacks, int width, int height, Integer[] firstStack, Integer[] stacksCount,
			byte[][] imageData) {
		RandomPMessage preparationMsg = new RandomPMessage(PrepareForImageMessages, false);
		preparationMsg.buff.writeInt(stacks.length);
		for (ItemStack stack : stacks)
			ByteBufUtils.writeItemStack(preparationMsg.buff, stack);

		preparationMsg.buff.writeInt(width);
		preparationMsg.buff.writeInt(height);

		sendToServer(preparationMsg);

		int i = 0;
		for (byte[] image : imageData) {
			RandomPMessage actualImage = new RandomPMessage(ImageMessage, false);
			actualImage.buff.writeInt(i);
			actualImage.buff.writeInt(firstStack[i].intValue());
			actualImage.buff.writeInt(stacksCount[i].intValue());
			actualImage.buff.writeInt(image.length);
			actualImage.buff.writeBytes(image);
			
			sendToServer(actualImage);

			i++;
		}
		
		RandomPMessage finMessage = new RandomPMessage(FinishedImageTransmitting, false);
		sendToServer(finMessage);

	}
}
