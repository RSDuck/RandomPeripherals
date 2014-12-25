package me.kemal.randomp.computercraft;

import java.util.HashMap;

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
import me.kemal.randomp.util.CCType;
import me.kemal.randomp.util.CCUtils;
import me.kemal.randomp.util.Peripheral;
import me.kemal.randomp.util.TurtlePeripheral;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TurtleUpgradeInventory extends RandomPTurtleUpgrade {
	public TurtleUpgradeInventory(int upgradeID) {
		super("Inventory", upgradeID);
		peripheral
				.AddMethod(
						"getStackInSlot",
						"Returns informations about the current selected slot, this informations includes NBT data of an item so it's possible to read things like the enchantment on an item",
						new CCType[] {},
						new CCType[] { new CCType(HashMap.class, "an table with informations about the current selected item") },
						this);
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
		return new TurtlePeripheral(turtle, peripheral);
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return Blocks.pumpkin.getIcon(side.ordinal(), 0);
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle) throws LuaException {
		if (method == "getStackInSlot") {
			ItemStack selectedItem = turtle.getInventory().getStackInSlot(turtle.getSelectedSlot());
			if (selectedItem != null) {
				return new Object[] { CCUtils.stackToMap(selectedItem) };
			} else
				return new Object[] { null };
		}
		throw new LuaException("Internal Error: function not found");
	}

}
