package me.kemal.randomp.util;

import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class ComputerUIWrapDummy implements IComputerAccess {
	IComputerAccess computer;

	public ComputerUIWrapDummy(IComputerAccess computer) {
		this.computer = computer;
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
		return "UIWrapped";
	}

}
