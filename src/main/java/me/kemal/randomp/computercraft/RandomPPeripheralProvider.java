package me.kemal.randomp.computercraft;

import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.te.TileEnergyStorage;
import me.kemal.randomp.te.TileRandomPMachine;
import me.kemal.randomp.te.TileUniversalInterface;
import me.kemal.randomp.util.IExtendablePeripheral;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class RandomPPeripheralProvider implements IPeripheralProvider {

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		if (world.getTileEntity(x, y, z) instanceof IExtendablePeripheral) {
			return ((IExtendablePeripheral) world.getTileEntity(x, y, z)).getPeripheral();
		}
		if (world.getTileEntity(x, y, z) != null) {
			for (int i = 0; i < RandomPeripherals.tileEntitiesWithAutoRead.length; i++)
				if (Block.blockRegistry.getNameForObject(world.getBlock(x, y, z))
						.contains(RandomPeripherals.tileEntitiesWithAutoRead[i])
						|| RandomPeripherals.tileEntitiesWithAutoRead[i] == "*") {
					return new PeripheralUniversalNBTRead(world.getTileEntity(x, y, z)).getPeripheral();
				}
		}
		return null;
	}

}
