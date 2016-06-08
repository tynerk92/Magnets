package com.somethingyellow.magnets;

public class Config {
	// Game configuration
	public static final float GAME_TICK_DURATION = 0.06f;
	public static final String LAYER_NAME_ACTORS = "Walls and Objects";
	public static final String LAYER_NAME_SHADOWS = "Shadows";
	public static final float CAMERA_PANNING_SMOOTH_RATIO = 0.1f;
	public static final float CAMERA_ZOOM_SMOOTH_RATIO = 0.1f;
	public static final float CAMERA_ZOOM_DEFAULT = 2f;
	public static final float MAP_AMBIENT_COLOR_RED_DEFAULT = 0.6f;
	public static final float MAP_AMBIENT_COLOR_GREEN_DEFAULT = 0.6f;
	public static final float MAP_AMBIENT_COLOR_BLUE_DEFAULT = 0.6f;
	public static final float MAP_SHADOW_HEIGHT = 0.2f;

	// Player properties
	public static final int PLAYER_MOVE_TICKS = 3;
	public static final float PLAYER_ZOOM_MIN = 0.5f;
	public static final float PLAYER_ZOOOM_DEFAULT = 0.8f;
	public static final float PLAYER_ZOOM_MAX = 1.5f;

	// Lodestone properties
	public static final int LODESTONE_MOVE_TICKS = 3;
	public static final String LODESTONE_STATE_MAGNETISED = "Magnetised";

	// Button properties
	public static final String BUTTON_STATE_ON = "On";
	public static final String BUTTON_STATE_OFFING = "Offing";
	public static final String BUTTON_STATE_ONING = "Oning";

	// Door properties
	public static final String DOOR_STATE_OPENED = "Opened";
	public static final String DOOR_STATE_OPENING = "Opening";
	public static final String DOOR_STATE_CLOSING = "Closing";
	public static final String DOOR_ACTION_OPEN = "Open";
	public static final String DOOR_ACTION_CLOSE = "Close";

	// Tile properties - syntax
	public static final String TILE_PREFIX_STATE = "@";
	public static final String TILE_PREFIX_REFERENCE = "~";
	public static final String TILE_PREFIX_NAME = "#";
	public static final String TILE_PREFIX_ACTION = "+";
	public static final String TILE_EXPRESSION_AND = "AND";
	public static final String TILE_EXPRESSION_OR = "OR";
	public static final String TILE_EXPRESSION_NOT = "NOT";

	// Tile properties - types
	public static final String TILE_TYPE = "Type";
	public static final String TILE_TYPE_PLAYER = "Player";
	public static final String TILE_TYPE_LODESTONE = "Block";
	public static final String TILE_TYPE_MAGNETIC_SOURCE = "Magnetic Source";
	public static final String TILE_TYPE_MAGNETIC_FLOOR = "Magnetic Floor";
	public static final String TILE_TYPE_OBSTRUCTED_FLOOR = "Obstructed Floor";
	public static final String TILE_TYPE_DOOR = "Door";
	public static final String TILE_TYPE_BUTTON = "Button";
	public static final String TILE_TYPE_WALL = "Wall";
	public static final String TILE_TYPE_EXIT = "Exit";

	// Tile properties - references
	public static final String TILE_REFERENCE_MAGNETIC_ATTRACTION_HORIZONTAL = "Magnetic Attraction Horizontal";
	public static final String TILE_REFERENCE_MAGNETIC_ATTRACTION_VERTICAL = "Magnetic Attraction Vertical";

	// Tile properties - other attributes
	public static final String TILE_ISPUSHABLE = "IsPushable";
	public static final String TILE_ISMAGNETISABLE = "IsMagnetisable";
	public static final String TILE_ISOPEN = "IsOpen";
	public static final String TILE_ELEVATION = "Elevation";
	public static final String TILE_BODY_WIDTH = "Body Width";
	public static final String TILE_BODY_AREA = "Body Area";
	public static final String TILE_RENDER_DEPTH = "Render Depth";
	public static final String TILE_LIGHTING_IMAGE_PATH = "Lighting Image Path";
	public static final String TILE_LIGHTING_WIDTH = "Lighting Width";
	public static final String TILE_LIGHTING_HEIGHT = "Lighting Height";
	public static final String TILE_LIGHTING_INTENSITY = "Lighting Intensity";
	public static final String TILE_LIGHTING_DISPLACEMENT_X = "Lighting Displacement X";
	public static final String TILE_LIGHTING_DISPLACEMENT_Y = "Lighting Displacement Y";
	public static final String TILE_SHADOW_DISPLACEMENT_Y = "Shadow Displacement Y";
}
