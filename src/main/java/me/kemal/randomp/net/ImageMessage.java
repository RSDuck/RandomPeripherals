package me.kemal.randomp.net;

import java.util.Base64;
import java.util.HashMap;

import net.minecraft.item.ItemStack;

public class ImageMessage extends RandomPMessage {
	public ImageMessage() {
	}

	public ImageMessage(byte[] imageData, int horizontalItems, int verticalItems, HashMap<Integer, ItemStack> displayedItems) {
		super(Packets.ImageMessage, false);
	}
}
