package me.kemal.randomp.computercraft;

import java.util.Vector;

import me.kemal.randomp.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public class RandomPTurtleUpgrade implements ITurtleUpgrade {
	private String unlocalizedName;
	private int upgradeID = 0;
	private static Vector<Integer> usedIDs;

	public RandomPTurtleUpgrade(String unlocalizedName, int upgradeID) {
		this.unlocalizedName = unlocalizedName;
		this.upgradeID = upgradeID;
		usedIDs = new Vector<Integer>();
	}

	public static int IsIDValid(String value, int defaultValue) {
		if (value.matches("[20-255]")) {
			int valueAsInt = Util.ToInt(value);
			for (int id : usedIDs)
				if (valueAsInt == id)
					return defaultValue;
			usedIDs.addElement(valueAsInt);
			return valueAsInt;
		}
		return defaultValue;
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

}
