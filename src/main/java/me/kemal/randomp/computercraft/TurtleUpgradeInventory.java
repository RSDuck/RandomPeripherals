package me.kemal.randomp.computercraft;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;
import me.kemal.randomp.te.TilePeripheralInventory;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TurtleUpgradeInventory implements ITurtleUpgrade {
	private int upgradeID;

	public TurtleUpgradeInventory(int upgradeID) {
		this.upgradeID = upgradeID;
	}

	@Override
	public int getUpgradeID() {
		return upgradeID;
	}

	@Override
	public String getUnlocalisedAdjective() {
		return "Inventory";
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(Blocks.pumpkin);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return new TilePeripheralInventory(turtle);
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		return null;
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return Blocks.pumpkin.getIcon(side.ordinal(), 0);
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
	}

}
