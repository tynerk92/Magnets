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
	public ArrayList<String> lodestoneAreas = new ArrayList<String>();
	public ArrayList<String> lodestoneCodes = new ArrayList<String>();
	public ArrayList<String> lodestoneNames = new ArrayList<String>();
	public Hashtable<String, int[]> lodestoneNameToOffsetArray = new Hashtable<String, int[]>();
	public Hashtable<String, String> lodestoneCodeToNameArray = new Hashtable<String, String>();
	public int maxWidth = -1;
	public int maxHeight = -1;
	private boolean generated = false;
	public int numButtonFrames;
	public int numDoorTransitionFrames;
	public int numDoorBlinkFrames;
	public int numExitFrames;
	public int numMagneticSourceNorthFrames;
	public int numMagneticSourceSouthFrames;
	public int numMagneticFloorFrames;
	public int numMagneticAttractionFrames;
	
	// Change this to make everything faster or slower.
	private final int IntervalMultiplier = 20;
	
	// Num MagneticFloor frames = 5
	private final int[] MagneticFloorAnim = new int[] {  0,  1,  2,  3,  4 };
	private final int[] MagneticFloorInte = new int[] { 32, 32, 32, 32, 32 };
	
	// Num MagneticSource frames = 8;
	private final int[] MagneticSourceAnim = new int[] {  0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private final int[] MagneticSourceInte = new int[] { 40, 2, 2, 2, 2, 4, 4, 6, 6, 8 };
	
	// Num DoorBlink frames = 8;
	private final int[] DoorBlinkAnim = new int[] { 0, 1, 2, 3, 4, 5,  6,  7,  6, 5, 4, 3, 2, 1, 0 };
	private final int[] DoorBlinkInte = new int[] { 8, 8, 8, 8, 8, 8, 16, 32, 16, 8, 8, 8, 8, 8, 8 };
	
	// Num DoorTransition frames = 9;
	private final int[] DoorTransitionAnim = new int[] { 0, 1, 2, 3, 4, 5, 6 };
	private final int[] DoorTransitionInte = new int[] { 1, 2, 3, 5, 3, 2, 1 };
	
	// Num DoorTransition frames = 5;
	private final int[] ExitAnim = new int[] { 0, 1, 2, 3, 4, 3, 2, 1 };
	private final int[] ExitInte = new int[] { 2, 3, 4, 5, 8, 5, 4, 3 };
	
	// Num MagneticSource frames = 7;
	private final int[] MagneticAttractAnim = new int[] { 0, 1, 2, 3, 4, 5, 6 };
	private final int[] MagneticAttractInte = new int[] { 8, 8, 8, 8, 8, 8, 8 };
	
	public static void main(String... args) {
	    TileSetGenerator tsGen = new TileSetGenerator();
	    tsGen.generateTileSet("D:/Dropbox/Magnets/android/assets/" + "Graphics/Set 1 (Cave)");
	    // for (Tile tile : tsGen.tilelist) tile.printTileInfo();
	    tsGen.genLodestoneCodes();
	    //for (String name : tsGen.lodestoneNameToOffsetArray.keySet()) System.out.println(name);
	}
	
	public String generateTileSet(String dir) {
		if (!generated) {
			File[] files = new File(dir + "/").listFiles();
			writeTileSet(files, "../../Graphics/Set 1 (Cave)", "");
			generated = true;
			return tileset;
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
	
	public int tallest = -1, widest = -1;
	public ArrayList<String> genLodestoneCodes() {
		if (lodestoneAreas.isEmpty()) throw new IllegalArgumentException("lodestoneAreas is empty");
		
		ArrayList<String> lodestoneCodes = new ArrayList<String>();
		
		for (String area : lodestoneAreas) {
			int height = Integer.parseInt(area.split(" - ")[0]);
			int width = area.split(" - ")[1].length() / height;
			tallest = Math.max(tallest, height);
			widest = Math.max(width, widest);
		}
		
		//    00***					000****
		//    *****   (3 x 3)		*******		(3 x 4)
		//    *****  				*******
		
		int fullWidth = widest * 2 - 1;
		for (String area : lodestoneAreas) {
			String[][] code = new String[tallest][fullWidth];
			for (String[] row : code) Arrays.fill(row, "?");
			int width = Integer.parseInt(area.split(" - ")[0]);
			int height = area.split(" - ")[1].length() / width;
			area = area.split(" - ")[1];
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
			lodestoneCodes.add(cod);
		}
		
		for (int i = 0; i < lodestoneAreas.size(); i++) {
			lodestoneCodeToNameArray.put(lodestoneCodes.get(i), lodestoneNames.get(i));
		}
		
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
					boolean isBlink = false;
					
					switch (type) {
						case "Button": 
							data = name.split("[ _]");
							totalFrames = Integer.parseInt(data[2]);
							whichSet 	= Integer.parseInt(data[0]);
							whichFrame 	= Integer.parseInt(data[1]);
							numButtonFrames = totalFrames;
							info += property("Render Depth", "int", "-1");
							if (whichFrame == 1) 						info += property("~", "Button Off") + 
																				property("~Off", "Button Off") + 
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
							isBlink = data[1].equals("Blink");
							if (isBlink) {
								numDoorBlinkFrames = totalFrames;
								if (whichFrame == 1) 					info += enclose("properties", property("~", "Door Blink " + whichSet)) + 
																				image(width, height, source) + 
																				animation(DoorBlinkAnim, DoorBlinkInte, IntervalMultiplier, false);
								else 									info += image(width, height, source);
							} else {
								numDoorTransitionFrames = totalFrames;
								if (whichFrame == 1) 					info += property("~", "Door Closed " + whichSet) + 
																				property("~Closed", "Door Closed " + whichSet) + 
																				property("~Closing", "Door Closing " + whichSet) + 
																				property("~Blink", "Door Blink " + whichSet) + 
																				property("~Opened", "Door Opened " + whichSet) + 
																				property("~Opening", "Door Opening " + whichSet) +
																				property("Type", "Door");
								else if (whichFrame == 2) 				info += property("~", "Door Opening " + whichSet) +
																				animation(DoorTransitionAnim, DoorTransitionInte, IntervalMultiplier, false);
								else if (whichFrame < totalFrames - 1) 	{}
								else if (whichFrame == totalFrames - 1) info += property("~", "Door Closing " + whichSet) + 
																				animation(DoorTransitionAnim, DoorTransitionInte, IntervalMultiplier, true);
								else 									info += property("~", "Door Opened " + whichSet) + 
																				property("Render Depth", "int", "-1");
								info = enclose("properties", info) + image(width, height, source);
							} break;
						case "Elevated Floor":
							info = enclose("properties", 
											property("~", "Obstructed Floor") +
											property("~Floor", "Obstructed Floor") +
											property("Type", "Obstructed Floor") + 
											property("Render Depth", "int", "-1") + 
											property("Elevation", "int", "4")) + 
									image(width, height, source); break;
						case "Exit":
							data = name.split("[ _]");
							whichFrame = Integer.parseInt(data[data.length - 2]);
							numExitFrames = Integer.parseInt(data[data.length - 1]);
							if (whichFrame == 1)
								info = enclose("properties", 
												property("Type", "Exit")) + 
										image(width, height, source) + 
										animation(ExitAnim, ExitInte, IntervalMultiplier, false);
							else 
								info = image(width, height, source); 
							break;
						case "Pushable": 
						case "Unpushable": 
							boolean pushable = type.equals("Pushable");
							data = name.split(" "); 
							String dimensions = data[0];
							String tag = data[1];
							String area = data[2];
							lodestoneAreas.add(dimensions.split("x")[0] + " - " + area);
							lodestoneNames.add(name);
							int lodewidth = Integer.parseInt(dimensions.split("x")[0]);
							int lodeheight = Integer.parseInt(dimensions.split("x")[1]);

							// System.out.println(name + " area: " + area + ", yoffset: " + (lodeheight - 1) + ", xoffset: " + (area.indexOf("1")));
							lodestoneNameToOffsetArray.put(name, new int[] { lodeheight - 1, area.indexOf("1") });
							// <Width>x<height> <Numbering within the set of equal width and height> <Area code>
							info = enclose("properties", 
											property("~", (pushable ? "Pushable" : "Unpushable") + " " + name) + 
											property("~Lodestone", (pushable ? "Pushable" : "Unpushable") + " " + name) + 
											property("~Magnetised Overlay", "Magnetised Overlay " + dimensions + " " + tag) + 
											property("Body Area", area) + 
											property("Body Width", "int", "" + lodewidth) + 
											property("IsMagnetisable", "bool", "true") + 
											property("IsPushable", "bool", pushable + "") + 
											property("Type", "Block")) + 
									image(width, height, source); break;
						case "Magnetic Attraction":
							data = name.split("[ _]");
							whichFrame = Integer.parseInt(data[data.length - 2]);
							numMagneticAttractionFrames = Integer.parseInt(data[data.length - 1]);
							if (whichFrame == 1)
								info = enclose("properties", 
												property("Type", "Magnetic Attraction " + data[0])) + 
										image(width, height, source) + 
										animation(MagneticAttractAnim, MagneticAttractInte, IntervalMultiplier, false); 
							else 
								info = image(width, height, source); 
							break;
						case "Magnetic Floor":
							data = name.split("[ _]");
							whichFrame = Integer.parseInt(data[data.length - 2]);
							numMagneticFloorFrames = Integer.parseInt(data[data.length - 1]);
							if (whichFrame == 1) {
								info = enclose("properties", 
												property("~", "Magnetic Floor") + 
												property("~Floor", "Magnetic Floor") + 
												property("Render Depth", "int", "-1")) + 
												property("Type", "Magnetic Floor") + 
										image(width, height, source) + 
										animation(MagneticFloorAnim, MagneticFloorInte, IntervalMultiplier, false);
							} else 
								info = image(width, height, source); 
							break;
						case "Magnetic Source":
							data = name.split("[ _]");
							whichFrame = Integer.parseInt(data[data.length - 2]);
							whichType = data[0];
							if (whichType.equals("North")) 	numMagneticSourceNorthFrames = Integer.parseInt(data[data.length - 1]);
							else 							numMagneticSourceSouthFrames = Integer.parseInt(data[data.length - 1]);
							if (whichFrame == 1)
								info = enclose("properties", 
												property("~", "Magnetic Source") +
												property("~Source", "Magnetic Source") +
												property("Type", "Magnetic Source")) + 
										image(width, height, source) + 
										animation(MagneticSourceAnim, MagneticSourceInte, IntervalMultiplier, false); 
							else 
								info = image(width, height, source); 
							break;
						case "Magnetized Overlay":
							info = enclose("properties", 
											property("~", "Magnetised Overlay " + name) + 
											property("Render Depth", "int", "2")) + 
									image(width, height, source); break;
						case "Wall Overlays":
						case "Floor Overlays":
						case "Floor": 
							info = image(width, height, source); break;
						case "Player":
							info = enclose("properties", 
											property("~", "Player Standing") +
											property("Type", "Player") + 
											property("~Standing", "Player Standing")) + 
									image(width, height, source); break;
						case "Wall": 
							info = enclose("properties", 
											property("Type", "Wall")) + 
									image(width, height, source); break;
						case "Test":
							// Ignore. This is for painting purposes only
							break;
						default: System.out.println(name);
							
					}
					info = tile(info);
					tilelist.add(new Tile(currentBlockCount, type + " " + name, width, height, source, info));
					nameToTileID.put(type + " " + name, currentBlockCount);
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
	
	public String property(String field, String value) {
		return "    <property name=\"" + field + "\" value=\"" + value + "\"/>\n";
	}
	
	public String property(String field, String type, String value) {
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
	
	public String animation(int[] sequence, int[] intervals, int IntervalMultiplier, boolean reverse) {
		if (sequence.length != intervals.length) throw new IllegalArgumentException("Number of intervals must match amount of numbers");
		String info = "";
		for (int i = 0; i < sequence.length; i++) 
			info += 	"    <frame tileid=\"" + (currentBlockCount + sequence[i] * ((reverse) ? -1 : 1)) + "\" duration=\"" + (intervals[i] * IntervalMultiplier) + "\"/>\n";
		return enclose("animation", info);
	}
	
	public void write(String s) {
		tileset += s;
	}
}
