package me.kemal.randomp.computercraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import dan200.computercraft.api.filesystem.IMount;
import net.minecraft.client.Minecraft;

public class IconMapImagesMount implements IMount {
	public File folder;

	public IconMapImagesMount() {
		folder = new File("CCSyncedWithItem");
		folder.mkdir();
	}

	@Override
	public boolean exists(String path) throws IOException {
		return new File(folder, path).exists();
	}

	@Override
	public boolean isDirectory(String path) throws IOException {
		return new File(folder, path).isDirectory();
	}

	@Override
	public void list(String path, List<String> contents) throws IOException {
		File pathInFolder = new File(folder, path);
		for (String f : pathInFolder.list()) {
			contents.add(f);
		}
	}

	@Override
	public long getSize(String path) throws IOException {
		return new File(folder, path).length();
	}

	@Override
	public InputStream openForRead(String path) throws IOException {
		return new FileInputStream(new File(folder, path));
	}

}
