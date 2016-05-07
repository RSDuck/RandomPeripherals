package me.kemal.randomp.computercraft;

import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.media.IMediaProvider;
import me.kemal.randomp.item.ItemIconMap;
import net.minecraft.item.ItemStack;

public class RandomPMediaProvider implements IMediaProvider {

	@Override
	public IMedia getMedia(ItemStack stack) {
		if(stack.getItem() instanceof ItemIconMap){
			return (IMedia)stack.getItem();
		}
		return null;
	}

}
