
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;


public class TextToTmx {
	
	final static List<String> demoLevels = Arrays.asList(new String[] { "Buttons", "Interspersing", "Offering", "Hookline", "Roundabout", "Cascade", "Suction", "Trio" });
	final static String mainAssetsDirectory = "D:/Dropbox/Magnets/android/assets/";
	private static final String[] Levels = new String[] { "Easy Levels Pack", "Medium Levels Pack", "Hard Levels Pack", "Experimental Levels Pack"};
	
	private Random random = new Random();
	private int whichWallSet = 2;
	private TileSetGenerator tsGen = new TileSetGenerator();
	private String[] neighbourCodes = tsGen.neighbourCodes;
	// private String[] adjacentsCodes = tsGen.adjacentsCodes;
	
	// Text representation of the level
	public static final String button1 = "b";
	public static final String door1 = "B";
	public static final String button2 = "f";
	public static final String door2 = "F";
	public static final String player = "s";
	public static final String magnetfloor = "m";
	public static final String magnetsource = "M"; 
	public static final String elevatedfloor = "E"; 
	public static final String exit = "e";
	
	public static final String wall = "█";
	public static final String bigtree = "T"; 
	public static final String lodestoneSymbols = "xyzXYZ";
	
	public static final String noOutline = " " + player + exit;
	public static final String validObjects = button1 + door1 + button2 + door2 + player + magnetfloor + magnetsource + elevatedfloor + exit;
	public static final String nonObjects = bigtree + wall;
	

	public static Boolean wroteTileSet = false;
	// These 4 Info must be printed out in this order to complete
	public static String LevelInfo = "";
	public String TileSetInfo = "";
	public static String PuzzleInfo = "";
	public static String LayersInfo = "";
	public static String ObjectsInfo = "";
	
	public static ArrayList<Tile> tilelist;
	public static Hashtable<String, Integer> nameToTileID;
	
	static FileFilter directoryFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory();
		}
	};
	
	private static File searchForFolder(File parentDirectory, String filename) {
	    File[] files = parentDirectory.listFiles(directoryFilter);
	    List<File> directories = new ArrayList<File>(files.length);
	    for (File file : files) {
	        if (file.getName().equals(filename)) return file;
	        else if (file.isDirectory())  		 directories.add(file);
	    }
	    for (File directory : directories) {
	        File file = searchForFolder(directory, filename);
	        if (file != null) return file;
	    }
	    return null;
	}
	
	private void writeTileSet() {
		// This part is the same for any level, hence it should only be executed once.
		String fullGraphicsSetPath = searchForFolder(new File(mainAssetsDirectory), "Set 1 (Cave)").getPath();
		String fullLevelsPath = searchForFolder(new File(mainAssetsDirectory), "Levels").getPath();
		String relativeLevelPath = (fullLevelsPath.substring(mainAssetsDirectory.length(), fullLevelsPath.length())).replaceAll("\\\\", "/");
		String relativeGraphicsSetPath = fullGraphicsSetPath.substring(mainAssetsDirectory.length(), fullGraphicsSetPath.length()).replaceAll("\\\\", "/");
		int depthLevelFolder = relativeLevelPath.length() - relativeLevelPath.replaceAll("/", " ").length() + 1;
		String upHowManyLevels = "";
		for (int i = 0; i < depthLevelFolder; i++) upHowManyLevels += "../";
		//System.out.println(relativeLevelPath);
		//System.out.println(relativeGraphicsSetPath);
		//System.out.println(upHowManyLevels + "Graphics/Set 1 (Cave)");
		TileSetInfo += tsGen.generateTileSet(mainAssetsDirectory, upHowManyLevels, relativeGraphicsSetPath);
		tilelist = tsGen.getList();
		nameToTileID = tsGen.getTable();
		wroteTileSet = true;
	}
	
	private void writeLevelInfo(int cols, int rows, int lastobjectID) {
		// The only varying part between every level. The dimensions of the levels and lastobjectid changes. 
		LevelInfo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<map version=\"1.0\" orientation=\"orthogonal\" renderorder=\"right-down\" width=\"" + cols + "\" height=\"" + rows + "\" tilewidth=\"32\" tileheight=\"32\" nextobjectid=\"" + lastobjectID + "\">\n";
	}
	
	
	public String name;
	public int rows, cols;
	private String errorMSG = "";
	private String[] levelLines;
	
	public boolean valid(String dir, String text) {
		boolean valid = true;
		this.name = text.substring(0, text.indexOf("\r\n"));
		String level = text.substring(text.indexOf("\r\n") + 2);
		
		if (name.isEmpty() || name.contains(wall)) throw new IllegalArgumentException("Formatting is screwed up in " + dir);
		int numPlayers = level.length() - level.replace(player, "").length();
		int numExits = level.length() - level.replace(exit, "").length();
		if (numPlayers != 1 && numExits != 1) {
			valid = false;
			if (numPlayers == 0) 		errorMSG += "No players present. ";
			if (numPlayers > 1) 		errorMSG += "Multiple players are detected. ";
			if (numExits == 0) 			errorMSG += "No exits detected. ";
			if (numExits > 1) 			errorMSG += "Multiple exits detected. ";
		}
		
		levelLines = level.split("\r\n");
		rows = levelLines.length;
		cols = levelLines[0].length();
		boolean equalRowLength = true;
		int currentLength = levelLines[0].length();
		
		for (int y = 0; y < rows - 1; y++) {
			if (equalRowLength &= currentLength == levelLines[y + 1].length()) break;
			else currentLength = levelLines[y + 1].length();
		}
		
		if (!equalRowLength || rows < 3 || cols < 3) {
			errorMSG += "Level too small! ";
			if (!equalRowLength) 	errorMSG += "Uneven rows. ";
			if (rows < 3)  			errorMSG += "Rows < 3. ";
			if (cols < 3) 			errorMSG += "Cols < 3. ";
			valid = false;
		}
		if (!errorMSG.equals("")) 	errorMSG += dir.split("Levels/")[1] + "[" + name + "].tmx\n";
		return valid;
	}
	
	public static int bufferWalls = 4;
	private int nameCount = 0;
	public void writeLayersInfo(String dir, String text) {
		
		List<Object> Objects = new ArrayList<Object>();
		
		if (!valid(dir, text)) {
			System.err.println("Skipped Generation of <" + name + "> because it is invalid");
			return;
		} else {
			System.out.println("Generating : " + name + ".tmx\t");
		}
	
		// This is to make the level look better y suppose. Makes the camera moves around more dynamically when nearing the 
		// boundaries of the level
		
		cols += bufferWalls * 2;
		rows += bufferWalls * 2;
		
		int[][] Walls  			 = new int[rows][cols];
		int[][] Floor            = new int[rows][cols];
		int[][] FloorDeco		 = new int[rows][cols];
		int[][] WallDeco1		 = new int[rows][cols];
		int[][] WallDeco2		 = new int[rows][cols];
		int[][] Collision	     = new int[rows][cols];
		int[][] Shadows			 = new int[rows][cols];
		// Determines if the collision layer should be created
		Boolean hasCollision = false;
		// Extra 2 rows and cols as required by the wall scanning algorithm
		String[][] data = new String[rows + 2][cols + 2];
		// Initialize all the layers
		for (String[] row: data) 			Arrays.fill(row, wall);
		for (int[] row: Floor) 				Arrays.fill(row, 0);
		for (int[] row: WallDeco1) 			Arrays.fill(row, 0);
		for (int[] row: WallDeco2) 			Arrays.fill(row, 0);
		for (int[] row: Walls) 				Arrays.fill(row, 0);
		for (int[] row: Collision) 			Arrays.fill(row, 0);
		for (int[] row: Shadows)			Arrays.fill(row, 0);
		
		for (int y = 0; y < rows - bufferWalls * 2; y++) {
			for (int x = 0; x < cols - bufferWalls * 2; x++) {
				data[y + bufferWalls + 1][x + bufferWalls + 1] = "" + levelLines[y].charAt(x);
			}
		}
		
		// Processing walls and floors and other objects
		for (int y = 1; y < rows + 1; y++) {
			for (int x = 1; x < cols + 1; x++) {
				String currentTile = data[y][x];
				// String belowTile = data[y + 1][x];
				String wallNeighbours;
				String objname = "";
				int ioffset = 0, joffset = 0;
				
				switch (currentTile) {
					case wall: 
						wallNeighbours = getNeighbours(y, x, wall, data, false);
						// All the possible 8 neighbours of a tile. Refer to getNeighbours function for explanation of 
						// the purpose of 0, 1 and X
						for (String code: neighbourCodes) {
							if (compareWithDontCares(wallNeighbours, code)) {
								/*// Uncomment when you are ready to introduce springs
								if (code.endsWith("X0X") && Math.random() < 0.3 && !belowTile.equals("e")) {
									int chosenFrame = random.nextInt(tsGen.numSpringFrames) + 1;
									int chosenSet = random.nextInt(2) + 1;
									WallDeco1[y - 1][x - 1] = nameToTileID.get("Wall Overlays Spring " + chosenSet + " " + chosenFrame + "_" + tsGen.numSpringFrames);
									WallDeco2[y][x - 1] 	= nameToTileID.get("Wall Overlays Splash " + chosenSet + " " + chosenFrame + "_" + tsGen.numSplashFrames);
									WallDeco1[y][x - 1]     = nameToTileID.get("Wall Overlays Puddle " + chosenSet);
								}*/
								Walls[y - 1][x - 1] = nameToTileID.get("Wall " + code); break;
							}
						} break;
						
					case elevatedfloor: 
						// Elevated Floors needs to be processed by the same style as walls and floors
						// Also, two things will be placed for each elevated floor - a graphic (that extends beyond the normal boundaries due to the way its drawn)
						// and a trigger object. 
						wallNeighbours = getNeighbours(y, x, elevatedfloor, data, false);
						for (String code: neighbourCodes) {
							if (compareWithDontCares(wallNeighbours, code)) {
								Objects.add(tsGen.createObject("Elevated Floor " + code, x, y));
								Floor[y - 1][x - 1] = nameToTileID.get("Elevated Floor " + code);
								break;
							}
						} break;
						
					case magnetfloor:
						// Magnetic Floors are tiled at about the same way as elevated floor / floor / wall, but are only considering
						// the adjacents. Hence there are only 16 variations as opposed to 47-48
						Objects.add(tsGen.createObject("Magnetic Floor 01_" + tsGen.numMagneticFloorFrames, x, y));
						
						// Tileable version
						/*wallNeighbours = getNeighbours(y, x, magnetfloor, data, false);
						for (String code: adjacentsCodes) {
							if (compareWithDontCares(wallNeighbours, code)) {
								Objects.add(tsGen.createObject("Magnetic Floor " + code, x, y));
								break;
							}
						} */break;
					default:
						switch (currentTile) {
							case button1: 		objname = "Button 1 1_" + tsGen.numButtonFrames; 						break;
							case door1: 		objname = "Door 1 1_" + tsGen.numDoorTransitionFrames; 					break;
							case button2: 		objname = "Button 2 1_" + tsGen.numButtonFrames;						break;
							case door2: 		objname = "Door 2 1_" + tsGen.numDoorTransitionFrames; 					break;
							case player: 		objname = "Player Idle"; 												break;
							case magnetsource:	objname = "Magnetic Source " + "01_" + tsGen.numMagneticSourceFrames; 	break;
							case exit: 			objname = "Exit " + (Math.random() < 0.5 ? 	"Down Front" : 
																							"Forwards Front"); 
												Walls[y - 1 + ioffset][x - 1 + joffset] = nameToTileID.get("Exit Arrow 1_" + tsGen.numExitFrames); break;
							default: 
								if (!(lodestoneSymbols + " ").contains(currentTile)) System.out.println("Warning: \'" + currentTile + "\' is not defined");
						}
						
						if (nonObjects.contains(currentTile)) 	Walls[y - 1 + ioffset][x - 1 + joffset] = nameToTileID.get(objname);
						if (validObjects.contains(currentTile)) Objects.add(tsGen.createObject(objname, x, y));
						
						// Floor Set 2 only
						/*
						if (!wall.equals(data[y][x])) {
							Floor[y - 1][x - 1] = 1;
						} else {
							Floor[y - 1][x - 1] = (int) (16 * Math.pow(Math.random(), 0.75));
						}*/
						
						// Floor Tiles
						if (noOutline.contains(currentTile)) {
							// Refer the the function for details
							// Also, all objects except exit, player and elevated ground have a depressed floor at the start
							String floorNeighbours = getNeighbours(y, x, noOutline, data, false);
							//String floorNeighbours = getNeighbours(y, x, wall, data, true);

							// All the possible 8 neighbours of a tile. Refer to getNeighbours function for explanation of 
							// the purpose of 0, 1 and X
							for (String code: neighbourCodes) {
								if (compareWithDontCares(floorNeighbours, code)) Floor[y - 1][x - 1] = nameToTileID.get("Floor " + code);
							}
						} else Floor[y - 1][x - 1] = nameToTileID.get("Floor Null");
						
						// Purely decorational. Decorations should not spawn on top of buttons, contraptions, nor exits
						if (Math.random() < 0.35 && data[y][x].equals(" ")) {
							if (Math.random() < 0.7) 		FloorDeco[y - 1][x - 1] = nameToTileID.get("Floor Overlays Debris " + (random.nextInt(8) + 1));
							else if (Math.random() < 0.25)  FloorDeco[y - 1][x - 1] = nameToTileID.get("Floor Overlays Depression " + (random.nextInt(8) + 1));
							else if (whichWallSet == 1) 	FloorDeco[y - 1][x - 1] = nameToTileID.get("Floor Overlays Grey Scratches " + (random.nextInt(8) + 1));
							//else if (whichWallSet == 2) 	FloorDeco[y - 1][x - 1] = nameToTileID.get("Floor Overlays Blue Scratches " + (random.nextInt(4) + 1));
						}
				}
				
			}
		}
		
		LodestoneDetector ld = new LodestoneDetector(data, tsGen.lodestoneAreas);
		//for (String code : tsGen.genLodestoneCodes()) System.out.println(code);
		ld.addLodestones(Objects, tsGen);
		
		/////////////////////////////////////// LAYERS /////////////////////////////////////////
		
		LayersInfo += writeGenericLayer("Floor", Floor);
		LayersInfo += writeGenericLayer("Floor Decoration", FloorDeco);
		LayersInfo += writeGenericLayer("Shadows", Shadows);
		LayersInfo += writeGenericLayer("Walls and Objects", Walls);
		LayersInfo += writeGenericLayer("Wall Decoration 1", WallDeco1);
		LayersInfo += writeGenericLayer("Wall Decoration 2", WallDeco2);
		if (hasCollision) LayersInfo += writeGenericLayer("Collision", Collision);
		
		// Objects Layer
		ObjectsInfo += " <objectgroup name=\"Objects\">\n";
		int n = 0;
		List<String> buttons1 = new ArrayList<String>(), buttonsNOT1 = new ArrayList<String>();
		List<String> buttons2 = new ArrayList<String>(), buttonsNOT2 = new ArrayList<String>();
		Collections.sort(Objects);
		// WARNING : Sorting IS required because if not, some doors will be processed before some of the buttons that open it
		// and those buttons will NOT be included in the door opening condition. Also because "C" comes after "B". SO DON"T CHANGE THE NAMES
		for (Object object : Objects) {
			//System.out.println(object.name);
			String currName = object.name.substring(0, 4) + " " + nameCount++;
			if (object.name.startsWith("Button 1")) {
				buttons1.add("#" + currName + "@On");
				buttonsNOT1.add("NOT #" + currName + "@On");
			} else if (object.name.startsWith("Button 2")) {
				buttons2.add("#" + currName + "@On");
				buttonsNOT2.add("NOT #" + currName + "@On");
			}
			
			ObjectsInfo += 
					"  <object id=\"" + n++ + 
					(object.name.equals("") ? "" : "\" name=\"" + currName) +
					"\" gid=\"" + nameToTileID.get(object.name) + 
					"\" x=\"" + ((object.x - 1) * 32) + 
					"\" y=\"" + ((object.y) * 32) + 
					"\" width=\"" + object.width + 
					"\" height=\"" + object.height + 
					"\"" + (object.name.contains("Door") ? "" : "/") + ">\n";
			
			if (object.name.startsWith("Door")) {
				int whichSet = Integer.parseInt(object.name.split(" ")[1]);
				String open = "", close = "", blink = "";
				if (whichSet == 1) {
					open += String.join(" AND ", buttons1.toArray(new String[buttons1.size()]));
					blink += String.join(" OR ", buttons1.toArray(new String[buttons1.size()]));
					close += String.join(" OR ", buttonsNOT1.toArray(new String[buttons1.size()]));
				} else if (whichSet == 2) {
					open += String.join(" AND ", buttons2.toArray(new String[buttons2.size()]));
					blink += String.join(" OR ", buttons2.toArray(new String[buttons2.size()]));
					close += String.join(" OR ", buttonsNOT2.toArray(new String[buttons1.size()]));
				}
				ObjectsInfo += 
					"   <properties>\n" + 
					"    <property name=\"+Close\" value=\"" + close + "\"/>\n" +
					"    <property name=\"+Open\" value=\"" + open + "\"/>\n" + 
					"    <property name=\"+Blink\" value=\"" + blink + "\"/>\n" + 
					"   </properties>\n" + 
					"  </object>\n";
			}
		} ObjectsInfo += " </objectgroup>\n";
	}
	
	private String writeGenericLayer(String name, int[][] arr) {
		String write = 	" <layer name=\"" + name + "\" width=\"" + cols + "\" height=\"" + rows + "\">\n" + 
						"  <data encoding=\"csv\">\n";
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				write += (arr[y][x]);
				if (y != rows - 1 || x != cols - 1) write += ",";
			} write += "\n";
		} write += "</data>\n" + 
					" </layer>\n\n";
		return write;
	}
	
	/**
	 * Returns the 8 neighbours of a block
	 * 
	 *  123
	 *  4 5
	 *  678
	 */
	private String getNeighbours(int y, int x, String block, String[][] arr, boolean flip) {
		int trueVal = flip ? 0 : 1;
		int falseVal = 1 - trueVal;
		return  (block.contains(arr[y - 1][x - 1]) ? trueVal : falseVal) + "" + 
				(block.contains(arr[y - 1][x    ]) ? trueVal : falseVal) +
				(block.contains(arr[y - 1][x + 1]) ? trueVal : falseVal) +
				(block.contains(arr[y    ][x - 1]) ? trueVal : falseVal) +
				(block.contains(arr[y    ][x + 1]) ? trueVal : falseVal) +
				(block.contains(arr[y + 1][x - 1]) ? trueVal : falseVal) +
				(block.contains(arr[y + 1][x    ]) ? trueVal : falseVal) +
				(block.contains(arr[y + 1][x + 1]) ? trueVal : falseVal);
	}
	
	/**
	 * Uses the concept of dontcares from circuit logic to compare binary strings
	 */
	public boolean compareWithDontCares(String input, String condition) {
		if (input.length() != condition.length()) throw new IllegalArgumentException("Strings does not match in length");
		for (int y = 0; y < condition.length(); y++) {
			if 	    (condition.charAt(y) == 88 || input.charAt(y) == 88) 	continue;
			else if (condition.charAt(y) != input.charAt(y)) 				return false;
		} return true;
	}
	
	boolean wroteToAnimationsFolder = false;
	public void writeLevel(String dir, String levelcode, String difficulty) {
		// Create the level in the stated directory
		if (!wroteTileSet) writeTileSet();
		writeLayersInfo(dir, levelcode);
		writeLevelInfo(cols, rows, nameCount);
		
		String finalTmx = LevelInfo + TileSetInfo + PuzzleInfo + LayersInfo + ObjectsInfo + "</map>";
		// Reset all those varying info
		LevelInfo = ""; PuzzleInfo = ""; LayersInfo = ""; ObjectsInfo = "";
		
		if (demoLevels.contains(name.split(" ")[0])) {
			writeToFile(mainAssetsDirectory + "Demo Levels/", "(" + difficulty + ") " + name.split(" ")[0], finalTmx, "tmx");
		}
		if (!wroteToAnimationsFolder) {
			wroteToAnimationsFolder = true;
			writeToFile(mainAssetsDirectory + "Animations/", "(" + difficulty + ") " + name.split(" ")[0], finalTmx, "tmx");
		}
		writeToFile(dir, "(" + difficulty + ") " + name, finalTmx, "tmx");
	}
	
	public void writeToFile(String dir, String name, String finalTmx, String type) {
		File file = new File(dir + name + "." + type);
		file.getParentFile().mkdirs();
		PrintWriter writer;
		try {
			writer = new PrintWriter(file, "UTF-8");
			writer.println(finalTmx);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		FilenameFilter Tmx = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".tmx");
		    }
		};
		
		File[] files = new File(mainAssetsDirectory + "Levels").listFiles(Tmx);
		for (File file : files) file.delete();
		
		TextToTmx prog = new TextToTmx();
		for (String pack: Levels) {
			System.out.println("\n" + pack + ": \n");
			String content = new String(Files.readAllBytes(Paths.get(mainAssetsDirectory + "Levels/" + pack + ".txt")));
			String difficulty = pack.split(" ")[0];
			for (String levelcode: content.split("\\r\\n\\r\\n")) {
				prog.writeLevel(mainAssetsDirectory + "Levels/", levelcode, difficulty);
			}
		} System.out.println(prog.errorMSG);
		prog.writeToFile(mainAssetsDirectory + "Animations/", "Tileset", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + prog.TileSetInfo, "tsx");
	}
}
