import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	public ArrayList<String> lodestoneCodes = new ArrayList<String>();
	public ArrayList<String> lodestoneNames = new ArrayList<String>();
	public Hashtable<String, int[]> lodestoneNameToOffsetArray = new Hashtable<String, int[]>();
	public Hashtable<String, String> lodestoneCodeToNameArray = new Hashtable<String, String>();
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
	private final int[] MagneticFloorInte = new int[] { 40, 2, 2, 2, 2, 4, 4, 6, 6, 8 };
	
	// Num MagneticSource frames = 10;
	private final int[] MagneticSourceAnim = new int[] {  0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private final int[] MagneticSourceInte = new int[] { 40, 2, 2, 2, 2, 4, 4, 6, 6, 8 };
	
	// Num DoorTransition frames = 7; // Excluding open and closed
	private final int[] DoorTransitionAnim = new int[] { 0,  1,  2,  3,  4,  5, 6 };
	private final int[] DoorTransitionInte = new int[] { 8, 12, 16, 20, 16, 12, 8 };
	
	// Num DoorTransition frames = 10;
	private final int[] ExitAnim = new int[] {  0, 1, 2, 3, 4, 5, 6, 7, 8, 7, 6, 5, 4, 3, 2, 1 };
	private final int[] ExitInte = new int[] {  6, 5, 5, 5, 4, 4, 3, 3, 2, 3, 3, 4, 4, 5, 5, 5 };
	
	// Num MagneticAttraction frames = 7;
	private final int[] MagneticAttractAnim = new int[] { 0, 1, 2, 3, 4, 5, 6 };
	private final int[] MagneticAttractInte = new int[] { 8, 8, 8, 8, 8, 8, 8 };
	
	// Num Spring Frames = 5;
	private final int[] SpringAnim = new int[] { 0, 1, 2, 3, 4 };
	private final int[] SpringInte = new int[] { 8, 8, 8, 8, 8 };
	
	// Num Spring Frames = 5;
	private final int[] SplashAnim = new int[] { 0, 1, 2, 3, 4 };
	private final int[] SplashInte = new int[] { 8, 8, 8, 8, 8 };
	
	public void main(String... args) {
	    TileSetGenerator tsGen = new TileSetGenerator();
	    tsGen.generateTileSet(TextToTmx.mainAssetsDirectory, "../", "Graphics/Set 1 (Cave)");
	    // for (Tile tile : tsGen.tilelist) tile.printTileInfo();
	    tsGen.genLodestoneCodes();
	    //for (String name : tsGen.lodestoneNameToOffsetArray.keySet()) System.out.println(name);
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
	
	public String fixArea(int height, int width, String area) {
		if (height < 3 || width < 3) return area; 
		StringBuilder sb = new StringBuilder(area);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				String neighbours = "" + ((x == 0) ? "" : area.charAt(y * height + x - 1)) + 
										 ((x == width - 1) ? "" : area.charAt(y * height + x + 1)) + 
										 ((y == 0) ? "" : area.charAt((y - 1) * height + x)) + 
										 ((y == height - 1) ? "" : area.charAt((y + 1) * height + x));
				
				if (!neighbours.contains("1")) sb.setCharAt(y * height + x, 'X');
			}
		}
		return sb.toString();
	}
	
	public int tallest, widest;
	public boolean done = false;
	public int fullWidth;
	public ArrayList<String> genLodestoneCodes() {
		if (done) return lodestoneCodes;
		if (lodestoneAreas.isEmpty()) throw new IllegalArgumentException("lodestoneAreas is empty");
		lodestoneCodes = new ArrayList<String>();
		for (String area : lodestoneAreas) {
			int height = Integer.parseInt(area.split(" - ")[0]);
			int width = area.split(" - ")[1].length() / height;
			tallest = Math.max(tallest, height);
			widest = Math.max(width, widest);
		}
		
		//    00***					000****
		//    *****   (3 x 3)		*******		(3 x 4)
		//    *****  				*******
		
		int count = 0;
		fullWidth = widest * 2 - 1;
		for (String area : lodestoneAreas) {
			String[][] code = new String[tallest][fullWidth];
			for (String[] row : code) Arrays.fill(row, "?");
			int width = Integer.parseInt(area.split(" - ")[0]);
			int height = area.split(" - ")[1].length() / width;
			area = area.split(" - ")[1];
			area = fixArea(height, width, area);
			int offset = area.indexOf("1");
			
			// System.out.println(area + ", height = " + height + ", width = " + width + ", xoffset = " + (-offset) + ", yoffset = " + (height - 1));
			
			for (int y = 0; y < tallest; y++) {
				if (y < height) {
					for (int x = 0; x < width; x++) {
						String a = "" + area.charAt(y * width + x);
						code[y][x + widest - offset - 1] = a;
					}
					for (int x = widest - offset - 2; x >= 0; x--) {
						String right = code[y][x + 1];
						if (right.equals("1"))	code[y][x] = "0";
						else 					code[y][x] = "X";
					}
					for (int x = widest - offset + width - 1; x < fullWidth; x++) {
						String left = code[y][x - 1];
						if (left.equals("1"))	code[y][x] = "0";
						else 					code[y][x] = "X";
					}
				} else {
					for (int x = 0; x < fullWidth; x++) {
						String top = code[y - 1][x];
						if (top.equals("1"))	code[y][x] = "0";
						else 					code[y][x] = "X";
					}
				}
			}
			
			String cod = "";
			for (String[] row : code) {
				for (String col : row) {
					cod += col;
				}
			} 
			cod = "XX" + cod.substring(2);
			// System.out.println(lodestoneNames.get(count++) + " " + cod);
			
			lodestoneCodes.add(cod);
		}
		
		for (int i = 0; i < lodestoneAreas.size(); i++) {
			lodestoneCodeToNameArray.put(lodestoneCodes.get(i), lodestoneNames.get(i));
		}
		
		done = true;
		return lodestoneCodes;
	}
	
	public String lodestoneCodeToName(String code) {
		return lodestoneCodeToNameArray.get(code);
	}
	
	public int[] lodestoneNameToOffset(String code) {
		return lodestoneNameToOffsetArray.get(code);
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
				try {
					BufferedImage bimg = ImageIO.read(file);
					
					int width = bimg.getWidth(), 
						height = bimg.getHeight();
					String source = dir + "/" + name;
					name = name.substring(0, name.length() - 4);
					
					String[] data = new String[] {};
					String info = "",
						   whichType = "";
					int totalFrames = 0, 
						whichSet = 0, 
						whichFrame = 0;
					
					switch (type) {
						case "Button": 
							data = name.split("[ _]");
							totalFrames = Integer.parseInt(data[2]);
							whichSet 	= Integer.parseInt(data[0]);
							whichFrame 	= Integer.parseInt(data[1]);
							numButtonFrames = totalFrames;
							info += property("Render Depth", "int", "-1");
							if (whichFrame == 1) 						info += property("~", "Button Off " + whichSet) + 
																				property("~Off", "Button Off " + whichSet) + 
																				property("~Offing", "Button Pressing " + whichSet) + 
																				property("~On", "Button On " + whichSet) + 
																				property("~Oning", "Button Pressing " + whichSet) + 
																				property("Type", "Button");
			            	else if (whichFrame < totalFrames) 			info += property("~", "Button Pressing " + whichSet);
			            	else 										info += property("~", "Button On " + whichSet);
			            	info = enclose("properties", info) + 
		            			   image(width, height, source); break;
						case "Door": 
							data = name.split("[ _]");
							totalFrames = Integer.parseInt(data[data.length - 1]);
							whichSet 	= Integer.parseInt(data[0]);
							whichFrame 	= Integer.parseInt(data[data.length - 2]);
							numDoorTransitionFrames = totalFrames;
							if (whichFrame == 1) 					info += property("~", "Door Closed " + whichSet) + 
																			property("~Closed", "Door Closed " + whichSet) + 
																			property("~Closing", "Door Closing " + whichSet) + 
																			property("~Opened", "Door Opened " + whichSet) + 
																			property("~Opening", "Door Opening " + whichSet) +
																			property("Type", "Door");
							else if (whichFrame == 2) 				info += property("~", "Door Opening " + whichSet) +
																			animation(DoorTransitionAnim, DoorTransitionInte, IntervalMultiplier, false);
							else if (whichFrame < totalFrames - 1) 	{}
							else if (whichFrame == totalFrames - 1) info += property("~", "Door Closing " + whichSet) + 
																			animation(DoorTransitionAnim, DoorTransitionInte, IntervalMultiplier, true);
							else 									info += property("~", "Door Opened " + whichSet) + 
																			property("~Closed", "Door Closed " + whichSet) + 
																			property("~Closing", "Door Closing " + whichSet) + 
																			property("~Opened", "Door Opened " + whichSet) + 
																			property("~Opening", "Door Opening " + whichSet) +
																			property("Type", "Door") +
																			property("Render Depth", "int", "-1");
							info = enclose("properties", info) + image(width, height, source);
							break;
						case "Elevated Floor":
							info = enclose("properties", 
											property("~", type + " " + name) +
											property("~Floor", type + " " + name) +
											property("Type", "Obstructed Floor") + 
											property("Render Depth", "int", "-1") + 
											property("Elevation", "int", "4")) + 
							 		image(width, height, source); break;
						case "Exit":
							data = name.split("[ _]");
							boolean isArrow = data[0].equals("Arrow");
							info = image(width, height, source);
							if (isArrow) {
								whichFrame = Integer.parseInt(data[data.length - 2]);
								if (whichFrame == 1) {
									numExitFrames = Integer.parseInt(data[data.length - 1]);
									info += animation(ExitAnim, ExitInte, IntervalMultiplier, false); 
								}
							} else if (name.equals("Dud")) {
								info = enclose("properties", 
												property("~", type) +
												property("~Exit", type) +
												property("Type", type) + 
												property("Render Depth", "int", "-1")) + 
										info;
							} break;
						case "Pushable": 
						case "Unpushable": 
							boolean pushable = type.equals("Pushable");
							data = name.split(" "); 
							String dimensions = data[0];
							String tag = data[1];
							String area = data[2];
							name = name.replaceAll("X", "0");
							area = area.replaceAll("X", "0");
							lodestoneAreas.add(dimensions.split("x")[0] + " - " + area);
							lodestoneNames.add(name);
							int lodewidth = Integer.parseInt(dimensions.split("x")[0]);
							int lodeheight = Integer.parseInt(dimensions.split("x")[1]);
							
							// System.out.println(name + " area: " + area + ", yoffset: " + (lodeheight - 1) + ", xoffset: " + (area.indexOf("1")));
							lodestoneNameToOffsetArray.put(name, new int[] { lodeheight - 1, area.indexOf("1") });
							
							// <Width>x<height> <Numbering within the set of equal width and height> <Area code>
							info = enclose("properties", 
											property("~", type + " " + name) + 
											property("~Lodestone", type + " " + name) + 
											property("~Magnetised Overlay", "Magnetised Overlay " + dimensions + " " + tag) + 
											property("Body Area", area) + 
											property("Body Width", "int", lodewidth) + 
											property("IsMagnetisable", "bool", "true") + 
											property("IsPushable", "bool", pushable) + 
											property("Type", "Block")) +
									image(width, height, source); break;
						case "Magnetic Attraction":
							data = name.split("[ _]");
							whichFrame = Integer.parseInt(data[data.length - 2]);
							numMagneticAttractionFrames = Integer.parseInt(data[data.length - 1]);
							String orientation = data[0];
							if (whichFrame == 1)
								info = enclose("properties", 
												property("Type", type + " " + orientation)) + 
										image(width, height, source) + 
										animation(MagneticAttractAnim, MagneticAttractInte, IntervalMultiplier, false); 
							else 
								info = image(width, height, source); 
							break;
						case "Magnetic Floor":
							data = name.split("[ _]");
							whichFrame = Integer.parseInt(data[data.length - 2]);	// No animations as of now
							numMagneticFloorFrames = Integer.parseInt(data[data.length - 1]);
							if (whichFrame == 1)
								info = enclose("properties", 
												property("~", type + " " + name) + 
												property("~Floor", type + " " + name) + 
												property("Render Depth", "int", "-1") + 
												property("Type", type) + 
												property("Lighting Displacement X", 16) + 
												property("Lighting Displacement Y", 16) + 
												property("Lighting Height", 100) + 
												property("Lighting Width", 100) + 
												property("Lighting Image Path", relativeGraphicsSetPath + "/Lighting/Point Source Red.png") + 
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
												property("~", type) +
												property("~Source", type) +
												property("Type", type) + 
												property("Lighting Displacement X", 16) + 
												property("Lighting Displacement Y", 16) + 
												property("Lighting Height", 100) + 
												property("Lighting Width", 100) + 
												property("Lighting Image Path", relativeGraphicsSetPath + "/Lighting/Point Source Red.png") + 
												property("Lighting Intensity", 0.5)) + 
										image(width, height, source) + 
										animation(MagneticSourceAnim, MagneticSourceInte, IntervalMultiplier, false); 
							else 
								info = image(width, height, source); 
							break;
						case "Magnetised Overlay":
							info = enclose("properties", 
											property("~", type + " " + name) + 
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
							info = enclose("properties", 
											property("~", type + " Standing") +
											property("Type", type) + 
											property("~Standing", type + " Standing") + 
											property("Lighting Displacement X", 16) + 
											property("Lighting Displacement Y", 24) + 
											property("Lighting Height", 200) + 
											property("Lighting Width", 200) + 
											property("Lighting Image Path", relativeGraphicsSetPath + "/Lighting/Point Source White.png") + 
											property("Lighting Intensity", 0.4) + 
											property("Shadow Displacement Y", 16)) + 
									image(width, height, source); break;
						case "Wall": 
							info = enclose("properties", 
											property("Type", type)) + 
									image(width, height, source); break;
						case "Test":
							// Ignore. This is for painting purposes only
							skip = true;
							break;
						case "UI":
							info = enclose("properties", 
											property("~", name)) + 
									image(width, height, source); break;
						case "Lighting":
							info = image(width, height, source); break;
						default: System.out.println(name);
							
					}
					if (skip) break;
					if (info.isEmpty()) throw new IllegalArgumentException("Empty tile. Type = " + type + ". Name = " + name);
					info = tile(info);
					tilelist.add(new Tile(currentBlockCount, type + " " + name, width, height, source, info));
					nameToTileID.put(type + " " + name, currentBlockCount);
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
	public String animation(int[] sequence, int[] intervals, int IntervalMultiplier, boolean reverse, int startingFrame, int maxFrames) {
		if (sequence.length != intervals.length) throw new IllegalArgumentException("Number of intervals must match amount of numbers");
		String info = "";
		for (int i = 0; i < sequence.length; i++) {
			int offset = (sequence[i] + startingFrame > maxFrames) ? maxFrames * -1 : 0;
			info += 	"    <frame tileid=\"" + (currentBlockCount + (sequence[i] + offset) * ((reverse) ? -1 : 1)) + 
						                    "\" duration=\"" + (intervals[i] * IntervalMultiplier) + "\"/>\n";
		}
		return enclose("animation", info);
	}
	
	public String animation(int[] sequence, int[] intervals, int IntervalMultiplier, boolean reverse) {
		if (sequence.length != intervals.length) throw new IllegalArgumentException("Number of intervals must match amount of numbers");
		String info = "";
		for (int i = 0; i < sequence.length; i++) {
			info += 	"    <frame tileid=\"" + (currentBlockCount + sequence[i] * ((reverse) ? -1 : 1)) + 
						                    "\" duration=\"" + (intervals[i] * IntervalMultiplier) + "\"/>\n";
		}
		return enclose("animation", info);
	}
	
	public void write(String s) {
		tileset += s;
	}
}
