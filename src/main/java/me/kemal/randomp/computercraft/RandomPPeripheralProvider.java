package me.kemal.randomp.computercraft;

import me.kemal.randomp.te.TileEnergyStorage;
import me.kemal.randomp.te.TileRandomPMachine;
import me.kemal.randomp.te.TileUniversalInterface;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class RandomPPeripheralProvider implements IPeripheralProvider {

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		/*
		 * if (world.getTileEntity(x, y, z) instanceof TileUniversalInterface) {
		 * return (TileUniversalInterface) world.getTileEntity(x, y, z); }
		 */
		if (world.getTileEntity(x, y, z) instanceof TileEntitySign) {
			return new PeripheralSign((TileEntitySign) world.getTileEntity(x, y, z));
		}
		if (world.getTileEntity(x, y, z) instanceof TileRandomPMachine) {
			return ((TileRandomPMachine) world.getTileEntity(x, y, z)).getPeripheral();
		}
		return null;
	}

}
