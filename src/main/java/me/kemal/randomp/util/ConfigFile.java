package me.kemal.randomp.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import me.kemal.randomp.RandomPeripheral;

public class ConfigFile {
	private String filename;
	public HashMap<String, String> map;
	public HashMap<String, String> commentMap;

	public ConfigFile() {
		this.filename = "";
		map = new HashMap<String, String>();
		commentMap = new HashMap<String, String>();
	}

	public ConfigFile(String filename) {
		this.filename = filename;
		map = new HashMap<String, String>();
		commentMap = new HashMap<String, String>();
	}

	public ConfigFile(File filename) {
		this.filename = filename.getAbsolutePath();
		map = new HashMap<String, String>();
		commentMap = new HashMap<String, String>();
	}

	String getFileName() {
		return filename;
	}

	public boolean read() {
		try {
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (!strLine.startsWith("#")) {
					String key = strLine.replaceAll("^(.+)(=)(.+)", "$1");
					String value = strLine.replaceAll("^(.+)(=)(.+)", "$3");
					// RandomPeripheral.logger.info();
					if (map.containsKey(key)) {
						if (map.get(key) != value) {
							map.remove(key);
							map.put(key, value);
						}
					} else
						write();
				} else {

				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			RandomPeripheral.logger.info("Config file not found, randomperipheral will create a new");
			write();
		} catch (Exception e) {
			RandomPeripheral.logger.error(e.getMessage());
			return false;
		}

		return true;
	}

	public boolean write() {
		try {
			FileWriter writer = new FileWriter(filename);
			Set<Entry<String, String>> key = map.entrySet();
			writer.write("#RandomPeripheral Config File" + System.getProperty("line.seperator"));
			for (Entry<String, String> value : key) {
				if (commentMap.containsKey(value.getKey())) {
					writer.write("#" + commentMap.get(value.getKey()) + System.getProperty("line.separator")
							+ System.getProperty("line.separator"));
				}
				writer.write(value.getKey() + "=" + value.getValue() + System.getProperty("line.separator"));
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			RandomPeripheral.logger.error(e.getMessage());
		}
		return true;
	}
}
