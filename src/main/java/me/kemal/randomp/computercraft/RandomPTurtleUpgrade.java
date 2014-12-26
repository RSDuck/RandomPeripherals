package me.kemal.randomp.computercraft;

import java.util.Vector;

import me.kemal.randomp.util.IExtendablePeripheral;
import me.kemal.randomp.util.Peripheral;
import me.kemal.randomp.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public class RandomPTurtleUpgrade implements ITurtleUpgrade, IExtendablePeripheral {
	private String unlocalizedName;
	protected int upgradeID = 0;
	protected Peripheral peripheral;
	private static Vector<Integer> usedIDs;

	public RandomPTurtleUpgrade(String unlocalizedName, int upgradeID) {
		this.unlocalizedName = unlocalizedName;
		this.upgradeID = upgradeID;
		usedIDs = new Vector<Integer>();
		peripheral = new Peripheral();
		peripheral.setType(unlocalizedName);
	}

	@Override
	public int getUpgradeID() {
		return upgradeID;
	}

	@Override
	public String getUnlocalisedAdjective() {
		return unlocalizedName;
	}

	@Override
	public TurtleUpgradeType getType() {
		return null;
	}

	@Override
	public ItemStack getCraftingItem() {
		return null;
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return null;
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		return null;
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return null;
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle) throws LuaException {
		return null;
	}

}
