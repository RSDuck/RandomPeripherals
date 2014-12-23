package me.kemal.randomp.util;

import java.util.HashMap;
import java.util.Map;

public class CCType {
	private Class<?> type;
	private String name;
	private String description;
	private int maxValue;
	private int minValue;
	private boolean rangeChecker;

	public CCType(Class<?> type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.minValue = 0;
		this.maxValue = 0;
	}

	public CCType(Class<?> type, String description) {
		this.type = type;
		this.description = description;
		this.minValue = 0;
		this.maxValue = 0;
	}

	public CCType(Class<?> type, String name, String description, int minValue, int maxValue) {
		this(type, name, description);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.rangeChecker = true;
	}

	public int isValid(Object obj) {
		if (type.isInstance(obj)) {
			if (obj instanceof Double && rangeChecker) {
				int dValue = ((Number) obj).intValue();
				if ((dValue <= maxValue) && (dValue >= minValue))
					return 1;
				return 2;
			}
			return 1;
		}
		return 0;
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
		if (type.isAssignableFrom(Map.class))
			return "table";
		if (type.isAssignableFrom(null)) {
			return "nil";
		}
		return type.toString();
	}

	public String getDescription() {
		return description;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public int getMinValue() {
		return minValue;
	}

	@Override
	public String toString() {
		return JavaToLuaType(type) + " " + ((name == null) ? "" : name + " ") + description;
	}

	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if (name != null)
			hashMap.put("name", name);
		hashMap.put("description", description);
		hashMap.put("type", JavaToLuaType(type));
		return hashMap;
	}
}
