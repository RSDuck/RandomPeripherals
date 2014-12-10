package me.kemal.randomp.util;

import java.util.HashMap;

public class CCType {
	private Class<?> type;
	private String name;
	private String description;

	public CCType(Class<?> type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}

	public CCType(Class<?> type, String description) {
		this.type = type;
		this.description = description;
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public static String JavaToLuaType(Class<?> type) {
		if (type.isAssignableFrom(String.class))
			return "string";
		if (type.isAssignableFrom(Double.class))
			return "number";
		if (type.isAssignableFrom(Boolean.class))
			return "bool";
		if (type.isAssignableFrom(HashMap.class))
			return "table";
		if (type.isAssignableFrom(null)) {
			return "nil";
		}
		return type.toString();
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return JavaToLuaType(type) + " " + ((name == null) ? "" : name + " ") + description;
	}
}
