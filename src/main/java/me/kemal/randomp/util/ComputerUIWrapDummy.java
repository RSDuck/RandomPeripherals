package me.kemal.randomp.util;

import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.api.peripheral.IComputerAccess;
import me.kemal.randomp.RandomPeripherals;

public class ComputerUIWrapDummy implements IComputerAccess {
	IComputerAccess computer;
	String side;

	public ComputerUIWrapDummy(IComputerAccess computer, String side) {
		this.computer = computer;
		this.side = side;
	}

	@Override
	public String mount(String desiredLocation, IMount mount) {
		return computer.mount(desiredLocation, mount);
	}

	@Override
	public String mount(String desiredLocation, IMount mount, String driveName) {
		return computer.mount(desiredLocation, mount, driveName);
	}

	@Override
	public String mountWritable(String desiredLocation, IWritableMount mount) {
		return computer.mountWritable(desiredLocation, mount);
	}

	@Override
	public String mountWritable(String desiredLocation, IWritableMount mount, String driveName) {
		return computer.mountWritable(desiredLocation, mount, driveName);
	}

	@Override
	public void unmount(String location) {
		computer.unmount(location);
	}

	@Override
	public int getID() {
		return computer.getID();
	}

	@Override
	public void queueEvent(String event, Object[] arguments) {
		computer.queueEvent(event, arguments);
	}

	@Override
	public String getAttachmentName() {
		return computer.getAttachmentName() + "_UIWrapped_" + side;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof ComputerUIWrapDummy) {
			ComputerUIWrapDummy c = (ComputerUIWrapDummy) obj;

			return (computer.getID() == c.getID() || computer == c.computer || computer.equals(c.computer));
		}
		if (obj instanceof IComputerAccess) {
			IComputerAccess c = (IComputerAccess) obj;

			return computer.getID() == c.getID() || computer == c || computer.equals(c);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return computer.hashCode();
	}

}
