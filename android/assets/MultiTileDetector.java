import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

// Due to the nature of lodestones being multi-tile, there needs to be a way to determine which coordinate 
// a multi-tile lodestone spawns on. And with most multi-tile blocks, it may result in a collision within the same layer.
//
//    XX
//    █X
// 
// In the above case, the 3-tile lodestone's coordinate clashes with the only wall displayed there. Hence a collision layer
// is needed.
//
// Also, as you can see in the 2 for loops, tiles are scanned from left to right for every row, starting from the top row. 
// Can you determine which part of these lodestones are scanned first?
//
// X	 X		 X		  X								   @	 @		 @		  @	
// X	 X		XXX		XXX							=>	   X	 X		XXX		XXX	
// XX 	XX												   XX 	XX					
//
// Based on the scanning algorithm, the block that appears first in the lodestone are these respectively. Marked @ respectively.
// Additionally, remember we can have a clusterfuck of lodestones appearing together.
//
//  xy
//  xyyz   (3 different lodestones are displayed here). 
// xxzzz
//
// To properly differentiate those multi-tile lodestones, we need to assign sets of different characters to them. Two neighbouring
// lodestones should have different characters, and a neighbour of a neighbouring block may or may not be in contact with the
// lodestone in question.
//
// Lowercase dictates a pushable lodestone. Uppercase dictates an unpushable lodestone.
//
// So far this program differentiates between 3 different characters x,y,z. 
// Continue reading for explanation
public class MultiTileDetector {
	
	public Random random = new Random();
	
	public String[][] data;
	public int[][] output;
	private int tallest = 0, widest = 0;
	private int fullWidth;
	public Hashtable<String, String> multiTileCodeToNameArray = new Hashtable<String, String>();
	
	MultiTileDetector(String[][] data, String[][] data2, boolean hasSecondLayer, String whatToDetect) {
		
		this.data = new String[data.length][data[0].length];
		for (int j = 0; j < data.length; j++) {
			for (int i = 0; i < data[0].length; i++) {
				this.data[j][i] = data[j][i];
			}
		}
		
		if (hasSecondLayer) {
			for (int j = 0; j < data2.length; j++) {
				for (int i = 0; i < data2[0].length; i++) {
					if (whatToDetect.contains(data2[j][i])) {
						this.data[j][i] = data2[j][i];
					}
				}
			}
		}
		
		this.checked = new Integer[data.length][data[0].length];
		this.output = new int[data.length][data[0].length];
		for (Integer[] row: checked) Arrays.fill(row, 0);
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
	
	private ArrayList<String> genMultiTileCodes(ArrayList<String> multiTileAreas, ArrayList<String> multiTileNames) {
		ArrayList<String> multiTileCodes = new ArrayList<String>();
		
		for (String area : multiTileAreas) {
			area = area.substring(area.indexOf(" ") + 1);
			int height = Integer.parseInt(area.split(" - ")[0]);
			int width = area.split(" - ")[1].length() / height;
			tallest = Math.max(tallest, height);
			widest = Math.max(width, widest);
		}
		
		//    00***					000****
		//    *****   (3 x 3)		*******		(3 x 4)
		//    *****  				*******
		
		fullWidth = widest * 2 - 1;
		for (String area : multiTileAreas) {
			
			String type = area.substring(0, area.indexOf(" "));
			area = area.substring(area.indexOf(" ") + 1);
			
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
			
			String xes = "";
			for (int i = 0; i < widest - 1; i++) {
				xes += "X";
			}
			cod = xes + cod.substring(widest - 1);
			// System.out.println(lodestoneNames.get(count++) + " " + cod);
			
			multiTileCodes.add(type + " " + cod);
		}
		
		for (int i = 0; i < multiTileAreas.size(); i++) {
			multiTileCodeToNameArray.put(multiTileCodes.get(i), multiTileNames.get(i));
			// System.out.println(multiTileCodes.get(i) + " | " + multiTileNames.get(i));
		}
		return multiTileCodes;
	}
	
	/**
	 * Because a lodestone can span multiple tiles, and we do not want to risk multiple checks on all parts of the lodestone.
	 * An incorrect lodestone will be output if we let it do so. Hence there is another array to check if a tile is checked.
	 *  // This diagram below shows the logic behind the checking of the neighbours to determine which lodestone is it
		//
		//       001**
		//       *****
		//       *****
		//
		// When the 1 is reached, that means the previous 2 tiles checked is probably 0, but it is unimportant.
		// What matters is the parts labeled by *, because whatever is there determines what lodestone it is and 
		// where it should be placed to be rendered correctly.
		//
		// X				  10X
		// XX		=>		X0110
		//  X				XX010
		//
		// Can you figure out why the neighbour code for this particular lodestone is as shown?
		// 0 = must not have a lodestone tile there. 1 = must have a lodestone tile there. X = don't care
		// Anyway this code (manually determined for optimization purposes) is a fingerprint of a lodestone
		// It uniquely identifies a lodestone. 
	 * @return
	 */
	private Integer[][] checked;
	public static int bufferWalls = TextToTmx.bufferWalls;
	public void addMultiTiles(String type, List<Object> Objects, TileSetGenerator tsGen, String symbols, ArrayList<String> multiTileAreas, ArrayList<String> multiTileNames, Hashtable<String, int[]> nameToOffset) {
		ArrayList<String> multiTileCodes = genMultiTileCodes(multiTileAreas, multiTileNames);
		//System.out.println(arrayToString(data, true));
		//System.out.println("Height : " + data.length + ", Length : " + data[0].length);
		for (int y = bufferWalls; y < data.length - bufferWalls; y++) {
			for (int x = bufferWalls; x < data[0].length - bufferWalls; x++) {
				if (checked[y][x] == 0) {
					// Value is important. Remember when i said that we are using multiple characters to represent lodestones.
					String value = data[y][x];
					if (symbols.contains(value)) {					
						// Contains a portion of a lodestone
						// This string array is a representation of all the neighbours *. 
						for (String code: multiTileCodes) {
							code = code.substring(code.indexOf(" ") + 1);
							if (compareWithDontCares(getNeighbours(value, y, x, tallest, widest), code)) {
								String objectcode = "";
								switch (type) {
									case ("Lodestone"): 
										objectcode = (Character.isUpperCase(value.charAt(0)) ? "Unpushable" : "Pushable") + " " + code;
										break;
									case ("Button"): 
										if 		(TextToTmx.button1Symbols.contains(value)) objectcode = type + "1 " + code;
										else if (TextToTmx.button2Symbols.contains(value)) objectcode = type + "2 " + code;
										break;
									case ("Door"): 
										if 		(TextToTmx.door1Symbols.contains(value)) 	 objectcode = type + "Clos1 " + code;
										else if (TextToTmx.door1openSymbols.contains(value)) objectcode = type + "Open1 " + code;
										else if (TextToTmx.door2Symbols.contains(value)) 	 objectcode = type + "Clos2 " + code;
										else if (TextToTmx.door2openSymbols.contains(value)) objectcode = type + "Open2 " + code;
										break;
								}
								String objectname = multiTileCodeToNameArray.get(objectcode);
								int[] offsets = nameToOffset.get(objectname);
								int xoffset = offsets[1];
								int yoffset = offsets[0];
								Objects.add(tsGen.createObject(objectname, x - xoffset, y + yoffset));
								// Mark all the squares that it covers as visited.
								for (int i = 0; i < tallest; i++) {
									for (int j = -widest + 1; j < widest; j++) {
										if (code.charAt(i * (widest * 2 - 1) + j + widest - 1) == "1".charAt(0)) {
											checked[i + y][j + x] = 1;
										}
									}
								}
								break;
							}
						}
					}
				}
			}
		}
	}

	public String getNeighbours(String value, int y, int x, int tallest, int widest) {
		if (tallest == -1 || widest == -1) throw new IllegalArgumentException("Codes have not been generated");
		Integer[][] out = new Integer[tallest][widest * 2 - 1];
		for (int i = 0; i < tallest; i++) {
			for (int j = 0; j < widest * 2 - 1; j++) {
				out[i][j] = (data[y + i][x + j - widest + 1].equals(value)) ? 1 : 0;
			}
		}
		return arrayToString(out, false);
	}
	
	// Uses the concept of dontcares from circuit logic to compare binary strings
	public boolean compareWithDontCares(String input, String condition) {
		if (input.length() != condition.length()) throw new IllegalArgumentException("Strings does not match in length " + input + ": " + input.length() + " vs " + condition + ": " + condition.length());
		for (int y = 0; y < condition.length(); y++) {
			if 	    (condition.charAt(y) == 88 || input.charAt(y) == 88) 	continue;
			else if (condition.charAt(y) != input.charAt(y)) 				return false;
		} return true;
	}
	
	// Converts an array of whatever to a string. Meant for single character / int / strings of single length only
	public <T> String arrayToString(T[][] arr, boolean breaks) {
		String out = "";
		for (T[] row : arr) {
			for (T col : row) {
				out += col;
			} if (breaks) out += "\n";
		}
		return out;
	}
}
