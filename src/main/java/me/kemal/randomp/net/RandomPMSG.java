package me.kemal.randomp.net;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class RandomPMSG implements IMessage {
	public ByteBuf buff;

	public RandomPMSG() {
	}

	public RandomPMSG(short type, TileEntity te, Object... args) {
		ByteBuf buff = Unpooled.buffer();
		buff.writeInt(te.getWorldObj().provider.dimensionId);
		buff.writeInt(te.xCoord);
		buff.writeInt(te.yCoord);
		buff.writeInt(te.zCoord);
		buff.writeShort(type);
		handleObjects(buff, args);
		this.buff = buff;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		ServerPacketHandler.readData(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBytes(this.buff);
	}

	private static void handleObjects(ByteBuf data, Object[] objects) {
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
			}
		}
	}
}
