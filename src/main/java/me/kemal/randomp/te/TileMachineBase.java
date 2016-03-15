package me.kemal.randomp.te;

import java.util.ArrayList;
import java.util.HashMap;

import cofh.api.block.IDismantleable;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import me.kemal.randomp.util.CCType;
import me.kemal.randomp.util.FunctionNotFoundException;
import me.kemal.randomp.util.IExtendablePeripheral;
import me.kemal.randomp.util.Peripheral;
import me.kemal.randomp.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileMachineBase extends TileEntity implements IExtendablePeripheral, IReconfigurableSides {
	public static final int SIDE_NEUTRAL = 0;
	public static final int SIDE_UNIVERSAL = 1;
	public static final int SIDE_ITEMS_ONLY = 2;
	public static final int SIDE_ENERGY_ONLY = 3;
	public static final int SIDE_FLUID_ONLY = 4;
	public static final int SIDES_COUNT = 5;

	public static final String[] SIDE_CONFIG_STR = { "neutral", "universal", "items", "energy", "fluids" };

	protected int[] sideConfiguration;

	protected Peripheral peripheral;
	
	protected Object[] neightborCache;

	public TileMachineBase() {
		sideConfiguration = new int[] { SIDE_NEUTRAL, // Unten
				SIDE_NEUTRAL, // Oben
				SIDE_NEUTRAL, // Norden
				SIDE_NEUTRAL, // Sueden
				SIDE_NEUTRAL, // West
				SIDE_NEUTRAL // Osten
		};
		
		neightborCache = new Object[6];

		peripheral
				.addMethod("getSideConfiguration", "Returns an table which contains the IO configuration",
						new CCType[] {},
						new CCType[] { new CCType(HashMap.class,
								"A table which contains the Sidingconfiguration { \"side\" : \"configuration\" }") },
				this);
		peripheral.addMethod("setSideConfiguration", "Sets the IO configuration on a specific side",
				new CCType[] {
						new CCType(String.class, "side",
								"The side where the configuration should be changed(valid inputs: bottom, top, north, south, west, east)"),
						new CCType(String.class, "config",
								"The new configuration (valid inputs: neutral, universal, items, energy, fluids)") },
				new CCType[] {}, this);
	}
	
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.func_148857_g());
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public boolean decrSide(int side) {
		sideConfiguration[side] = (sideConfiguration[side] < 0) ? SIDES_COUNT - 1 : sideConfiguration[side] - 1;
		updateAllBlocks();
		return true;
	}

	@Override
	public boolean incrSide(int side) {
		sideConfiguration[side] = (sideConfiguration[side] >= SIDES_COUNT) ? 0 : sideConfiguration[side] + 1;
		updateAllBlocks();
		return true;
	}

	@Override
	public boolean setSide(int side, int config) {
		sideConfiguration[side] = config;
		updateAllBlocks();
		return true;
	}

	public int getSide(int side) {
		return sideConfiguration[side];
	}

	@Override
	public boolean resetSides() {
		for (int i = 0; i < sideConfiguration.length; i++)
			sideConfiguration[i] = SIDE_NEUTRAL;
		updateAllBlocks();
		return true;
	}

	@Override
	public int getNumConfig(int side) {
		return SIDES_COUNT;
	}
	
	// ComputerCraft
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments,
			ITurtleAccess turtle) throws LuaException, FunctionNotFoundException {
		switch (method) {
		case "getSideConfiguration": {
			HashMap<String, Object> configuration = new HashMap<String, Object>();
			configuration.put("bottom", SIDE_CONFIG_STR[sideConfiguration[0]]);
			configuration.put("top", SIDE_CONFIG_STR[sideConfiguration[1]]);
			configuration.put("north", SIDE_CONFIG_STR[sideConfiguration[2]]);
			configuration.put("south", SIDE_CONFIG_STR[sideConfiguration[3]]);
			configuration.put("west", SIDE_CONFIG_STR[sideConfiguration[4]]);
			configuration.put("east", SIDE_CONFIG_STR[sideConfiguration[5]]);

			return new Object[] { configuration };
		}
		case "setSideConfiguration": {
			final String[] sides = new String[] { "bottom", "top", "north", "south", "west", "east" };

			int side = Util.whichOneMatches(sides, (String) arguments[0]);
			int config = Util.whichOneMatches(SIDE_CONFIG_STR, (String) arguments[1]);

			if (side == -1)
				throw new LuaException("Side has to be bottom, top, north, south, west or east");
			if (config == -1)
				throw new LuaException("Config has to be neutral, universal, items, energy or fluids");

			setSide(side, config);
		}
			break;
		}
		throw new FunctionNotFoundException();
	}

	@Override
	public Peripheral getPeripheral() {
		return peripheral;
	}

	@Override
	public void attachToComputer(IComputerAccess computer) {
	}

	@Override
	public void detachFromComputer(IComputerAccess computer) {
	}

	public void updateAllBlocks() {
		worldObj.markBlockForUpdate(xCoord + 1, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord - 1, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord + 1, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord - 1, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord + 1);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord - 1);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
}
