package me.kemal.randomp.computercraft;

import me.kemal.randomp.util.CCType;
import me.kemal.randomp.util.CCUtils;
import me.kemal.randomp.util.TurtlePeripheral;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
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

public class TurtleUpgradeDispense extends RandomPTurtleUpgrade {
	public TurtleUpgradeDispense(int id) {
		super("Dispenser", id);
		peripheral.AddMethod("dispense", "Dispenses the current selected item out, just like a Vanilla Dispenser", new CCType[] { new CCType(String.class,
				"direction", "The direction where the projectile will be shooted") }, new CCType[] { new CCType(Boolean.class,
				"If the projectile sucefull was shooted") }, this);
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(Blocks.dispenser);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return new TurtlePeripheral(turtle, peripheral);
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return Blocks.dispenser.getIcon(0, 0);
	}

	public boolean dispense(int dir, ITurtleAccess turtle) {
		if (dir != -1) {
			BlockSourceImpl blocksourceimpl = new BlockSourceImplDispenserHack(turtle.getWorld(), dir, turtle.getPosition().posX, turtle.getPosition().posY,
					turtle.getPosition().posZ);
			ItemStack stack = turtle.getInventory().getStackInSlot(turtle.getSelectedSlot());
			if (stack != null) {
				IBehaviorDispenseItem dispenseBehavior = this.getDispenseBehavior(stack);
				if (dispenseBehavior != IBehaviorDispenseItem.itemDispenseBehaviorProvider) {
					ItemStack dispensedStack = dispenseBehavior.dispense(blocksourceimpl, stack);
					turtle.getInventory().setInventorySlotContents(turtle.getSelectedSlot(), dispensedStack);
					return true;
				}
			}
		}
		return false;
	}

	protected IBehaviorDispenseItem getDispenseBehavior(ItemStack stack) {
		return (IBehaviorDispenseItem) BlockDispenser.dispenseBehaviorRegistry.getObject(stack.getItem());
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle) throws LuaException {
		if (method == "dispense") {
			int dir = CCUtils.TurtleDirToForgeDir(turtle.getDirection(), (String) arguments[0]);
			if (dir != -1) {
				return new Object[] { dispense(dir, turtle) };
			} else
				throw new LuaException("Invalid direction");
		}
		throw new LuaException("Internal Error: function not found");
	}
}
