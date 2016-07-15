package com.somethingyellow.tiled;

import com.badlogic.gdx.maps.MapProperties;

/**
 * A collection of static convenience methods for tiled maps
 */

public class TiledMapHelper {
	private TiledMapHelper() {
	}

	public static Boolean ParseBooleanProp(MapProperties props, String propName) {
		Object propObject = props.get(propName);
		return Boolean.parseBoolean(propObject.toString());
	}

	public static boolean ParseBooleanProp(MapProperties props, String propName, boolean defaultValue) {
		Object propObject = props.get(propName);
		if (propObject == null) return defaultValue;
		return Boolean.parseBoolean(propObject.toString());
	}

	public static Integer ParseIntegerProp(MapProperties props, String propName) {
		Object propObject = props.get(propName);
		if (propObject == null) return null;
		if (propObject instanceof Float) {
			return Math.round((Float) propObject);
		} else {
			return Integer.parseInt(propObject.toString());
		}
	}

	public static int ParseIntegerProp(MapProperties props, String propName, int defaultValue) {
		Object propObject = props.get(propName);
		if (propObject instanceof Float) {
			return Math.round((Float) propObject);
		} else {
			if (propObject == null) return defaultValue;
			return Integer.parseInt(propObject.toString());
		}
	}

	public static Float ParseFloatProp(MapProperties props, String propName) {
		Object propObject = props.get(propName);
		if (propObject == null) return null;
		if (propObject instanceof Float) {
			return (Float) propObject;
		} else {
			return Float.parseFloat(propObject.toString());
		}
	}

	public static float ParseFloatProp(MapProperties props, String propName, float defaultValue) {
		Object propObject = props.get(propName);
		if (propObject == null) return defaultValue;
		if (propObject instanceof Float) {
			return (Float) propObject;
		} else {
			return Float.parseFloat(propObject.toString());
		}
	}

	public static String ParseProp(MapProperties props, String propName) {
		Object propObject = props.get(propName);
		if (propObject == null) return null;
		return propObject.toString();
	}

	public static String ParseProp(MapProperties props, String propName, String defaultValue) {
		Object propObject = props.get(propName);
		if (propObject == null) return defaultValue;
		return propObject.toString();
	}
}
