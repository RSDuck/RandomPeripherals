package me.kemal.randomp.net;

import net.minecraft.tileentity.TileEntity;

public class TileMessage extends RandomPMessage {
	public TileMessage() {
	}

	public TileMessage(short type, TileEntity te, Object... args) {
		super(type, true);

		buff.writeInt(te.getWorldObj().provider.dimensionId);
		buff.writeInt(te.xCoord);
		buff.writeInt(te.yCoord);
		buff.writeInt(te.zCoord);

		handleObjects(buff, args);
	}
}
