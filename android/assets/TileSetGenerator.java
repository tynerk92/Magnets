import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class TileSetGenerator {
	
	public ArrayList<Tile> tilelist = new ArrayList<Tile>();
	public Hashtable<String, Integer> nameToTileID = new Hashtable<String, Integer>();
	public final String[] neighbourCodes = new String[] { "01011010", "01011011", "01011110", "01011111", "01011X0X", 
															"01111010", "01111011", "01111110", "01111111", "01111X0X", 
															"01X1001X", "01X1011X", "01X10X0X", "11011010", "11011011", 
															"11011110", "11011111", "11011X0X", "11111010", "11111011", 
															"11111110", "11111111", "11111X0X", "11X1001X", "11X1011X", 
															"11X10X0X", "X0X00X0X", "X0X00X1X", "X0X01X0X", "X0X01X10", 
															"X0X01X11", "X0X1001X", "X0X1011X", "X0X10X0X", "X0X11010", 
															"X0X11011", "X0X11110", "X0X11111", "X0X11X0X", "X1001X0X", 
															"X1001X10", "X1001X11", "X1101X0X", "X1101X10", "X1101X11", 
															"X1X00X0X", "X1X00X1X" };
	public final String[] adjacentsCodes = new String[] { "X0X00X0X", "X0X00X1X", "X0X01X0X", "X0X01X1X", "X0X10X0X", 
															"X0X10X1X", "X0X11X0X", "X0X11X1X", "X1X00X0X", "X1X00X1X", 
															"X1X01X0X", "X1X01X1X", "X1X10X0X", "X1X10X1X", "X1X11X0X", 
															"X1X11X1X" };
	public ArrayList<String> lodestoneAreas = new ArrayList<String>();
	public ArrayList<String> lodestoneNames = new ArrayList<String>();
	public ArrayList<String> buttonAreas = new ArrayList<String>();
	public ArrayList<String> buttonNames = new ArrayList<String>();
	public ArrayList<String> doorAreas = new ArrayList<String>();
	public ArrayList<String> doorNames = new ArrayList<String>();
	public Hashtable<String, int[]>  lodestoneNameToOffsetArray = new Hashtable<String, int[]>();
	public Hashtable<String, int[]>  buttonNameToOffsetArray = new Hashtable<String, int[]>();
	public Hashtable<String, int[]>  doorNameToOffsetArray = new Hashtable<String, int[]>();
	public int maxWidth = -1;
	public int maxHeight = -1;
	private boolean generated = false;
	public int numButtonFrames = 1;
	public int numDoorTransitionFrames = 1;
	public int numExitFrames = 1;
	public int numMagneticSourceFrames = 1;
	public int numMagneticFloorFrames = 1;
	public int numMagneticAttractionFrames = 1;
	
	public int numSpringFrames = 1;
	public int numSplashFrames = 1;
	
	// Change this to make everything faster or slower.
	private final int IntervalMultiplier = 20;
	
	// Num MagneticFloor frames = 1
	private final int[] MagneticFloorAnim = new int[] {  0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private final double[] MagneticFloorInte = new double[] { 40, 2, 2, 2, 2, 4, 4, 6, 6, 8 };
	
	// Num MagneticSource frames = 10;
	private final int[] MagneticSourceAnim = new int[] {  0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private final double[] MagneticSourceInte = new double[] { 40, 2, 2, 2, 2, 4, 4, 6, 6, 8 };
	
	// Num DoorTransition frames = 7; // Excluding open and closed
	private final int[] DoorTransitionAnim = new int[] { 0, 1, 2,  3, 4, 5, 6 };
	//private final int[] DoorTransitionInte = new int[] { 4, 6, 8, 10, 8, 6, 4 };
	private final double[] DoorTransitionInte = new double[] { 0.4, 0.6, 0.8, 1, 0.8, 0.6, 0.4 };
	
	// Num MagneticAttraction frames = 7;
	private final int[] MagneticAttractAnim = new int[] { 0, 1, 2, 3, 4, 5, 6 };
	private final int[] MagneticAttractInte = new int[] { 8, 8, 8, 8, 8, 8, 8 };
	
	// Num Spring Frames = 5;
	private final int[] SpringAnim = new int[] { 0, 1, 2, 3, 4 };
	private final double[] SpringInte = new double[] { 8, 8, 8, 8, 8 };
	
	// Num Spring Frames = 5;
	private final int[] SplashAnim = new int[] { 0, 1, 2, 3, 4 };
	private final double[] SplashInte = new double[] { 8, 8, 8, 8, 8 };
	
	public void main(String... args) {
	    TileSetGenerator tsGen = new TileSetGenerator();
	    tsGen.generateTileSet(TextToTmx.mainAssetsDirectory, "../", "Graphics/Set 1 (Cave)");
	    // for (Tile tile : tsGen.tilelist) tile.printTileInfo();
	}
	
	String relativeGraphicsSetPath = "";
	public String generateTileSet(String dir, String relativePathPart, String relativeGraphicsSetPath) {
		//System.out.println(dir);
		if (!generated) {
			this.relativeGraphicsSetPath = relativeGraphicsSetPath;
			dir += relativeGraphicsSetPath;
			relativePathPart += relativeGraphicsSetPath;
			File[] files = new File(dir + "/").listFiles();
			writeTileSet(files, relativePathPart, "");
			generated = true;
			return " <tileset firstgid=\"1\" name=\"Tileset\" tilewidth=\"" + maxWidth + "\" tileheight=\"" + maxHeight + "\" tilecount=\"" + (currentBlockCount - 1) + "\" columns=\"0\">" + tileset + " </tileset>\n";
		} else {
			throw new IllegalArgumentException("Why are you trying to generate the tileset twice?");
		}
	}
	
	public Hashtable<String, Integer> getTable() {
		return nameToTileID;
	}
	
	public ArrayList<Tile> getList() {
		return tilelist;
	}
	
	public Tile getTile(String name) {
		return tilelist.get(nameToTileID.get(name) - 1);
	}
	
	public Object createObject(int tilesetID, int x, int y) {
		return tilelist.get(tilesetID).createObject(x, y);
	}
	
	public Object createObject(String name, int x, int y) {
		try {
			getTile(name);
			// System.out.println(name + " " + tilelist.get(nameToTileID.get(name) - 1).name);
		} catch (NullPointerException e) {
			System.out.println(name + " does not exist in Tileset");
		}
		return getTile(name).createObject(x, y);
	}
	
	public int[] lodestoneNameToOffset(String code) {
		try {
			return lodestoneNameToOffsetArray.get(code);
		} catch (NullPointerException e) {
			throw new IllegalArgumentException(code);
		}
	}
	
	public int[] buttonNameToOffset(String code) {
		try {
			return buttonNameToOffsetArray.get(code);
		} catch (NullPointerException e) {
			throw new NullPointerException(code);
		}
	}
	
	public int[] doorNameToOffset(String code) {
		try {
			return doorNameToOffsetArray.get(code);
		} catch (NullPointerException e) {
			throw new NullPointerException("code");
		}
	}
	
	/**
	 * Creates the tileset based on the directory given the specified path
	 * Only reads PNG files, ignore everything else
	 * Reads all subdirectories and account for their names.
	 */
	private String tileset = "";
	private int currentBlockCount = 0;
	
	public void writeTileSet(File[] files, String dir, String type) {
		boolean skip = false;
	    for (File file : files) {
	    	String name = file.getName();
	        if (file.isDirectory()) {
	        	writeTileSet(file.listFiles(), dir + "/" + name, name);
	        } else if (name.endsWith("png")) {
	        	boolean isDoorClose = false, isDoorOpen = false;
				try {
					BufferedImage bimg = ImageIO.read(file);
					
					int width = bimg.getWidth(), 
						height = bimg.getHeight();
					String source = dir + "/" + name;
					name = name.substring(0, name.length() - 4);
					
					String[] data = new String[] {};
					String info = "",
						   whichType = "",
						   dimensions = "",
						   area = "",
						   lodewidth = "",
						   identity = "";
					int totalFrames = 0, 
						whichSet = 0, 
						whichFrame = 0;
					
					switch (type) {
						case "Button": 
							data = name.split("[_ ]");
							totalFrames = Integer.parseInt(data[data.length - 1]);
							whichSet 	= Integer.parseInt(data[1]);
							whichFrame 	= Integer.parseInt(data[data.length - 2]);
							dimensions = data[2];
							area = data[3];
							
							numButtonFrames = totalFrames;
							info += property("Render Depth", "int", "-1");
							
							identity = whichSet + " " + dimensions + " " + area;
							
							if (whichFrame == 1) {
								String buttonwidth = dimensions.split("x")[0];
								int buttonheight = Integer.parseInt(dimensions.split("x")[1]);
								buttonAreas.add(type + whichSet + " " + dimensions.split("x")[0] + " - " + area);
								buttonNames.add(type + " " + name);
								buttonNameToOffsetArray.put(type + " " + name, new int[] { buttonheight - 1, area.indexOf("1") });
								// System.out.println(type + whichSet + " " + name + " | " + (buttonheight - 1) + " " + area.indexOf("1"));
																		info += property("Name", "Button Off " + identity) + 
																				property("~Off", "Button Off " + identity) + 
																				property("~Offing", "Button Pressing " + identity) + 
																				property("~On", "Button On " + identity) + 
																				property("~Oning", "Button Pressing " + identity) + 
																				property("Body Area", area) + 
																				property("Body Width", "int", buttonwidth) +
																				property("Type", "Button");
							} else if (whichFrame < totalFrames) 		info += property("Name", "Button Pressing " + identity);
			            	else 										info += property("Name", "Button On " + identity);
			            	info = enclose("properties", info) + 
		            			   image(width, height, source); break;
						case "Door": 
							data = name.split("[_ ]");
							totalFrames = Integer.parseInt(data[data.length - 1]);
							whichSet 	= Integer.parseInt(data[1]);
							whichFrame 	= Integer.parseInt(data[data.length - 2]);
							dimensions = data[2];
							area = data[3];
							
							identity = whichSet + " " + dimensions + " " + area;
							
							numDoorTransitionFrames = totalFrames;
							if (whichFrame == 1) {	
								String doorwidth = dimensions.split("x")[0];
								int doorheight = Integer.parseInt(dimensions.split("x")[1]);
								doorAreas.add(type + "Clos" + whichSet + " " + dimensions.split("x")[0] + " - " + area);
								doorNames.add(type + "Clos " + name);
								doorNameToOffsetArray.put(type + "Clos " + name, new int[] { doorheight - 1, area.indexOf("1") });
								isDoorClose = true;
								//System.out.println(type + "Clos " + name + " | " + (doorheight - 1) + " " + area.indexOf("1"));
																	info += property("Name", "Door Closed " + identity) + 
																			property("~Closed", "Door Closed " + identity) + 
																			property("~Closing", "Door Closing " + identity) + 
																			property("~Opened", "Door Opened " + identity) + 
																			property("~Opening", "Door Opening " + identity) +
																			property("Body Area", area) + 
																			property("Body Width", "int", doorwidth) +
																			property("Type", "Door");
							} else if (whichFrame == 2) 			info += property("Name", "Door Opening " + identity);
							else if (whichFrame < totalFrames - 1) 	{}
							else if (whichFrame == totalFrames - 1) info += property("Name", "Door Closing " + identity);
							else {
								String doorwidth = dimensions.split("x")[0];
								int doorheight = Integer.parseInt(dimensions.split("x")[1]);
								doorAreas.add(type + "Open" + whichSet + " " + dimensions.split("x")[0] + " - " + area);
								doorNames.add(type + "Open " + name);
								doorNameToOffsetArray.put(type + "Open " + name, new int[] { doorheight - 1, area.indexOf("1") });
								isDoorOpen = true;
								//System.out.println(type + "Open " + name + " | " + (doorheight - 1) + " " + area.indexOf("1"));
																	info += property("Name", "Door Opened " + identity) + 
																			property("~Closed", "Door Closed " + identity) + 
																			property("~Closing", "Door Closing " + identity) + 
																			property("~Opened", "Door Opened " + identity) + 
																			property("~Opening", "Door Opening " + identity) +
																			property("Body Area", area) + 
																			property("Body Width", "int", doorwidth) +
																			property("Type", "Door") +
																			property("Render Depth", "int", "-1");
							}
							info = enclose("properties", info);
							if (whichFrame == 2) 					info += animation(DoorTransitionAnim, DoorTransitionInte, IntervalMultiplier, false);
							else if (whichFrame == totalFrames - 1) info += animation(DoorTransitionAnim, DoorTransitionInte, IntervalMultiplier, true);
							info += image(width, height, source);
							break;
						case "Elevated Floor":
							info = enclose("properties", 
										property("Elevation", 4)) + 
									image(width, height, source); break;
						case "Exit":
							info = enclose("properties", 
											property("Name", type + " " + name) +
											property("~Exit", type + " " + name) +
											property("Type", type) + 
											property("Lighting Displacement X", -4) + 
											property("Lighting Displacement Y", -12) + 
											property("Lighting Animation", "Light Rays Above") + 
											property("Render Depth", "int", "-1")) + 
									info;
							info += image(width, height, source); break;
						case "Treasure": 
							skip = true; break;
						case "Pushable": 
						case "Unpushable": 
							boolean pushable = type.equals("Pushable");
							data = name.split(" "); 
							dimensions = data[0];
							String tag = data[1];
							area = data[2];
							name = name.replaceAll("X", "0");
							area = area.replaceAll("X", "0");
							lodestoneAreas.add(type + " " + dimensions.split("x")[0] + " - " + area);
							lodestoneNames.add(type + " " + name);
							lodewidth = dimensions.split("x")[0];
							int lodeheight = Integer.parseInt(dimensions.split("x")[1]);
							
							lodestoneNameToOffsetArray.put(type + " " + name, new int[] { lodeheight - 1, area.indexOf("1") });
							// System.out.println(type + " " + name + " | " + (lodeheight - 1) + " " + area.indexOf("1"));
							
							if (dimensions.equals("1x1")) tag = "1";
							
							// <Width>x<height> <Numbering within the set of equal width and height> <Area code>
							info = enclose("properties", 
											property("Name", type + " " + name) + 
											property("~Lodestone", type + " " + name) + 
											property("~Magnetised Overlay", "Magnetised Overlay " + dimensions + " " + tag) + 
											property("Body Area", area) + 
											property("Body Width", "int", lodewidth) + 
											property("IsMagnetisable", "bool", "true") + 
											property("IsPushable", "bool", pushable) + 
											property("Type", "Block")) +
									image(width, height, source); break;
						case "Magnetic Attraction":
							if (name.equals("Field")) {
								info = enclose("properties", 
												property("Type", type) + 
												property("~Attraction Arrow Up", type + " Arrow Up") + 
												property("~Attraction Arrow Down", type + " Arrow Down") + 
												property("~Attraction Arrow Left", type + " Arrow Left") + 
												property("~Attraction Arrow Right", type + " Arrow Right"));
							} else {
								info = enclose("properties",
												property("Name", type + " " + name));
							}
							info += image(width, height, source);
							break;
						case "Magnetic Floor":
							data = name.split("[ _]");
							whichFrame = Integer.parseInt(data[data.length - 2]);	// No animations as of now
							numMagneticFloorFrames = Integer.parseInt(data[data.length - 1]);
							if (whichFrame == 1)
								info = enclose("properties", 
												property("Name", type + " " + name) + 
												property("~Source", type + " " + name) + 
												property("Render Depth", "int", "-1") + 
												property("Type", "Magnetic Source") + 
												property("Magnetisation Range", "int", 0) + 
												property("Attraction Range", "int", 0) + 
												property("Is Solid", false) + 
												property("Lighting Displacement X", 48 - 16) + 
												property("Lighting Displacement Y", 48 - 16) + 
												property("Lighting Animation", "Magnet Light Source") + 
												property("Lighting Intensity", 0.5)) + 
										image(width, height, source) + 
										animation(MagneticFloorAnim, MagneticFloorInte, IntervalMultiplier, false);
							else 
								info = image(width, height, source); 
							break;
						case "Magnetic Source":
							data = name.split("[ _]");
							whichFrame = Integer.parseInt(data[data.length - 2]);
							numMagneticSourceFrames = Integer.parseInt(data[data.length - 1]);
							if (whichFrame == 1)
								info = enclose("properties", 
												property("Name", type) +
												property("~Source", type) +
												property("Type", type) + 
												property("Lighting Displacement X", 48 - 16) + 
												property("Lighting Displacement Y", 48 - 16) + 
												property("Lighting Animation", "Magnet Light Source") + 
												property("Lighting Intensity", 0.5)) + 
										image(width, height, source) + 
										animation(MagneticSourceAnim, MagneticSourceInte, IntervalMultiplier, false); 
							else 
								info = image(width, height, source); 
							break;
						case "Magnetised Overlay":
							info = enclose("properties", 
											property("Name", type + " " + name) + 
											property("Render Depth", "int", "2")) + 
									image(width, height, source); break;
									
						case "Wall Overlays":
							data = name.split("[ _]");
							whichType = data[0];
							switch (whichType) {
								case "Splash" :
									numSplashFrames = Integer.parseInt(data[data.length - 1]);
									whichFrame = Integer.parseInt(data[data.length - 2]);
									totalFrames = Integer.parseInt(data[data.length - 1]);
									info = animation(SplashAnim, SplashInte, IntervalMultiplier, false, whichFrame, totalFrames); break;
								case "Spring" : 
									numSpringFrames = Integer.parseInt(data[data.length - 1]);
									whichFrame = Integer.parseInt(data[data.length - 2]);
									totalFrames = Integer.parseInt(data[data.length - 1]);
									info = animation(SpringAnim, SpringInte, IntervalMultiplier, false, whichFrame, totalFrames); break;
								case "Puddle" : break;
							} info += image(width, height, source);
							break;
																	
						case "Floor Overlays":
						case "Floor": 
							info += image(width, height, source); break;
						case "Player":
							if (name.equals("Right Idle")) {
								info = enclose("properties", 
											property("Name", type + " Right Idle") +
											property("Type", type) + 
											property("~Right Idle", type + " Right Idle") + 
											property("~Right Blink", type + " Right Blink") + 
											property("~Right Walk to Push", type + " Right Walk to Push") + 
											property("~Right Walk to Middle", type + " Right Walk to Push") + 
											property("~Right Push", type + " Right Push") + 
											property("~Right Push Stuck", type + " Right Push Step 1") + 
											property("~Left Idle", type + " Left Idle") + 
											property("~Left Blink", type + " Left Blink") + 
											property("~Left Walk to Push", type + " Left Walk to Push") + 
											property("~Left Walk to Middle", type + " Left Walk to Push") + 
											property("~Left Push", type + " Left Push") + 
											property("~Left Push Stuck", type + " Left Push Step 1") + 
											property("~Front Idle", type + " Front Idle") + 
											property("~Front Blink", type + " Front Blink") + 
											property("~Front Walk to Push", type + " Front Walk to Push") + 
											property("~Front Walk to Middle", type + " Front Walk to Push") + 
											property("~Front Push", type + " Front Push") + 
											property("~Front Push Stuck", type + " Front Push Step 1") + 
											property("~Back Idle", type + " Back Idle") + 
											property("~Back Blink", type + " Back Idle") + 
											property("~Back Walk to Push", type + " Back Walk to Push") + 
											property("~Back Walk to Middle", type + " Back Walk to Push") + 
											property("~Back Push", type + " Back Push") + 
											property("~Back Push Stuck", type + " Back Push Step 1") + 
											property("Lighting Displacement X", 128 - 16) + 
											property("Lighting Displacement Y", 128 - 24) + 
											property("Lighting Animation", "Player Light Source") + 
											property("Lighting Intensity", 0.4));
							} else if (name.contains("Step 0")) {
								info = enclose("properties", 
											property("Name", type + " " + name.replace(" Step 0", ""))) + 
										animation(new int[] { 0, 1, 0, 2 }, new double[] { 20, 20, 20, 20 }, IntervalMultiplier, false);
							} else {
								info = enclose("properties", 
										property("Name", type + " " + name));
							}
							info += image(width, height, source); break;
						case "Wall": 
							info = image(width, height, source); break;
						case "Test":
							// Ignore. This is for painting purposes only
							skip = true;
							break;
						case "UI":
							info = enclose("properties", 
											property("Name", name)) + 
									image(width, height, source); break;
						case "Lighting":
							info = enclose("properties",
										property("Name", name)) +
							image(width, height, source); break;
						default: System.out.println("Nope: " + name);
							
					}
					if (skip) break;
					if (info.isEmpty()) throw new IllegalArgumentException("Empty tile. Type = " + type + ". Name = " + name);
					info = tile(info);
					String tilename = type + (isDoorOpen ? "Open" : "") + (isDoorClose ? "Clos" : "") + " " + name;
					tilelist.add(new Tile(currentBlockCount, tilename, width, height, source, info));
					nameToTileID.put(tilename, currentBlockCount);
					// System.out.println((currentBlockCount - 1) + " " + type + " " + name);
					write(info);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
	}
	
	public String enclose(String name, String target) {
		return "   <" + name + ">\n" + target + "   </" + name + ">\n";
	}
	
	public <T> String property(String field, T value) {
		return "    <property name=\"" + field + "\" value=\"" + value + "\"/>\n";
	}
	
	public <T> String property(String field, String type, T value) {
		return "    <property name=\"" + field + "\" type=\"" + type + "\" value=\"" + value + "\"/>\n";
	}
	
	public String image(int width, int height, String source) {
		maxWidth = Math.max(width, maxWidth);
		maxHeight = Math.max(height, maxHeight);
		return "   <image width=\"" + width + "\" height=\"" + height + "\" source=\"" + source + "\"/>\n";
	}
	
	public String tile(String info) {
		return "\n  <tile id=\"" + currentBlockCount++ + "\">\n" + info + "  </tile>\n";
	}
	
	public int getTileCount() {
		return currentBlockCount;
	}
	
	public int getMaxWidth() {
		return maxWidth;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}
	
	// With offset. This is meant for looping animations.
	// In an effort to make levels look less static, every frame of the originally defined animation will
	// be an animation themselves. 
	public String animation(int[] sequence, double[] intervals, int IntervalMultiplier, boolean reverse, int startingFrame, int maxFrames) {
		if (sequence.length != intervals.length) throw new IllegalArgumentException("Number of intervals must match amount of numbers");
		String info = "";
		for (int i = 0; i < sequence.length; i++) {
			int offset = (sequence[i] + startingFrame > maxFrames) ? maxFrames * -1 : 0;
			info += 	"    <frame tileid=\"" + (currentBlockCount + (sequence[i] + offset) * ((reverse) ? -1 : 1)) + 
						                    "\" duration=\"" + (int) (intervals[i] * IntervalMultiplier) + "\"/>\n";
		}
		return enclose("animation", info);
	}
	
	public String animation(int[] sequence, double[] intervals, int IntervalMultiplier, boolean reverse) {
		if (sequence.length != intervals.length) throw new IllegalArgumentException("Number of intervals must match amount of numbers");
		String info = "";
		for (int i = 0; i < sequence.length; i++) {
			info += 	"    <frame tileid=\"" + (currentBlockCount + sequence[i] * ((reverse) ? -1 : 1)) + 
						                    "\" duration=\"" + (int) (intervals[i] * IntervalMultiplier) + "\"/>\n";
		}
		return enclose("animation", info);
	}
	
	public void write(String s) {
		tileset += s;
	}
}
