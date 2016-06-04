package me.kemal.randomp.net;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import me.kemal.randomp.RandomPeripherals;

import java.util.Random;

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

	static int adler32(byte[] buf) {
		int s1 = 1;
		int s2 = 0;

		for (int n = 0; n < buf.length; n++) {
			s1 = (s1 + buf[n]) % 65521;
			s2 = (s2 + s1) % 65521;
		}
		return (s2 << 16) | s1;
	}

	public static void sendImagesToServer(ItemStack[] stacks, int width, int height, Integer[] firstStack, Integer[] stacksCount,
			byte[][] imageData) {
		RandomPMessage preparationMsg = new RandomPMessage(PrepareForImageMessages, false);
		int clientID = (int)(Math.random() * (Integer.MAX_VALUE - 1)) + 1;
		preparationMsg.buff.writeInt(clientID);
		preparationMsg.buff.writeInt(stacks.length);
		for (ItemStack stack : stacks)
			ByteBufUtils.writeItemStack(preparationMsg.buff, stack);
		
		preparationMsg.buff.writeInt(width);
		preparationMsg.buff.writeInt(height);
		preparationMsg.buff.writeInt(imageData[0].length);
		
		sendToServer(preparationMsg);

		int i = 0;
		for (byte[] image : imageData) {
			RandomPMessage actualImage = new RandomPMessage(ImageMessage, false);
			actualImage.buff.writeInt(clientID);
			actualImage.buff.writeInt(i);
			actualImage.buff.writeInt(firstStack[i].intValue());
			actualImage.buff.writeInt(stacksCount[i].intValue());
			actualImage.buff.writeInt(image.length);
			actualImage.buff.writeBytes(image);

			sendToServer(actualImage);

			i++;
		}

		RandomPMessage finMessage = new RandomPMessage(FinishedImageTransmitting, false);
		finMessage.buff.writeInt(clientID);
		sendToServer(finMessage);

	}
}
