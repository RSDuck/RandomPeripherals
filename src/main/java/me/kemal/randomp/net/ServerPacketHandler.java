package me.kemal.randomp.net;

import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.te.TileHologramProjector;
import me.kemal.randomp.te.TileUniversalInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import com.google.gson.stream.JsonWriter;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class TEServerPacketHandler implements IMessageHandler<TileMessage, IMessage> {

	@Override
	public IMessage onMessage(TileMessage message, MessageContext ctx) {
		return null;
	}

	public static void readData(ByteBuf buff) {
		TileEntity te;

		boolean isTile = buff.readBoolean();
		short type = buff.readShort();

		if (isTile) {
			World world = DimensionManager.getWorld(buff.readInt());
			int x = buff.readInt();
			int y = buff.readInt();
			int z = buff.readInt();

			te = world.getTileEntity(x, y, z);

			switch (type) {
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
				case Packets.RotateBlock: {
					TileUniversalInterface ui = (TileUniversalInterface) te;
					ui.rotateBlock();
				}
					break;
			}
		} else {
			if (type == Packets.ImageMessage) {
				int stacksCount = buff.readInt();
				Vector<ItemStack> stacks = new Vector<>();
				for (int i = 0; i < stacksCount; i++)
					stacks.add(ByteBufUtils.readItemStack(buff));

				int width = buff.readInt();
				int height = buff.readInt();

				int imagesCount = buff.readInt();
				Vector<byte[]> imageDatas = new Vector<>();
				Vector<Integer> firstStacks = new Vector<>();
				Vector<Integer> stacksOnImage = new Vector<>();

				File folder = new File(RandomPeripherals.iconMapImagesMount.folder, "imageMaps");
				folder.mkdir();

				for (int i = 0; i < imagesCount; i++) {
					firstStacks.add(buff.readInt());
					stacksOnImage.add(buff.readInt());
					byte[] imageData = new byte[buff.readInt()];
					buff.readBytes(imageData);
					imageDatas.add(imageData);

					FileOutputStream stream;
					try {
						stream = new FileOutputStream(new File(folder, "iconMap" + i + ".png"));
						stream.write(imageData);
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(new File(folder, "mapInfos.json"))));
					writer.beginObject();
					writer.name("iconMaps").beginArray();

					for (int i = 0; i < firstStacks.size(); i++) {
						writer.beginObject();
						writer.name("filename").value("iconMap" + i + ".png");
						writer.name("firstStack").value(firstStacks.get(i));
						writer.name("itemsCount").value(stacksOnImage.get(i));

						writer.name("stacksOnImage").beginArray();
						int x = 0, y = 0;
						for (int j = firstStacks.get(i); j < stacksOnImage.get(i); j++) {
							writer.beginObject();
							UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(stacks.get(j).getItem());
							writer.name("name").value((id != null) ? id.modId + ":" + id.name : "???");
							writer.name("damage").value(stacks.get(j).getItemDamage());
							writer.name("iconPositionX").value(x);
							writer.name("iconPositionY").value(y);
							writer.endObject();

							y += 32;
							if (y >= height) {
								x += 32;
								y = 0;
							}
						}
						writer.endArray();
						writer.endObject();

					}
					writer.endArray();
					writer.endObject();
					writer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				RandomPeripherals.logger
						.info("Received image on server side width: " + width + " height: " + height + " imagesCount: " + imagesCount);
			}
		}
	}
}
