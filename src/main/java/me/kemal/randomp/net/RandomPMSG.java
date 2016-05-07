package me.kemal.randomp.net;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class RandomPMessage implements IMessage {
	public ByteBuf buff;

	public RandomPMessage() {
	}

	public RandomPMessage(short type, boolean isTileEntity) {
		buff = Unpooled.buffer();
		buff.writeBoolean(isTileEntity);
		buff.writeShort(type);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		TEServerPacketHandler.readData(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBytes(this.buff);
	}

	protected static void handleObjects(ByteBuf data, Object[] objects) {
		for (Object obj : objects) {
			Class<?> objClass = obj.getClass();
			if (objClass.equals(Integer.class)) {
				data.writeInt((Integer) obj);
			} else if (objClass.equals(Boolean.class)) {
				data.writeBoolean((Boolean) obj);
			} else if (objClass.equals(Byte.class)) {
				data.writeByte((Byte) obj);
			} else if (objClass.equals(Short.class)) {
				data.writeShort((Short) obj);
			} else if (objClass.equals(String.class)) {
				ByteBufUtils.writeUTF8String(data, (String) obj);
			} else if (Entity.class.isAssignableFrom(objClass)) {
				data.writeInt(((Entity) obj).getEntityId());
			} else if (objClass.equals(Double.class)) {
				data.writeDouble((Double) obj);
			} else if (objClass.equals(Float.class)) {
				data.writeFloat((Float) obj);
			} else if (objClass.equals(Long.class)) {
				data.writeLong((Long) obj);
			} else if (objClass.equals(byte[].class)) {
			}
		}
	}
}
