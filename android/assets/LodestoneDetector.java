import java.util.ArrayList;
import java.util.Arrays;
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
public class LodestoneDetector {
	
	public Random random = new Random();
	
	public String[][] data;
	public int[][] output;
	public ArrayList<Object> lodestones = new ArrayList<Object>();
	
	LodestoneDetector(String[][] data, ArrayList<String> lodestoneAreas) {
		this.data = data;
		this.checked = new Integer[data.length][data[0].length];
		this.output = new int[data.length][data[0].length];
		for (Integer[] row: checked) Arrays.fill(row, 0);
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
	public int bufferWalls = TextToTmx.bufferWalls;
	public void addLodestones(List<Object> Objects, TileSetGenerator tsGen) {
		ArrayList<String> lodestoneCodes = tsGen.genLodestoneCodes();
		//System.out.println(arrayToString(data, true));
		//System.out.println("Height : " + data.length + ", Length : " + data[0].length);
		for (int y = bufferWalls; y < data.length - bufferWalls; y++) {
			for (int x = bufferWalls; x < data[0].length - bufferWalls; x++) {
				if (checked[y][x] == 0) {
					// Value is important. Remember when i said that we are using multiple characters to represent lodestones.
					String value = data[y][x];
					if (TextToTmx.lodestoneSymbols.contains(value)) {					// Contains a portion of a lodestone
						// This string array is a representation of all the neighbours *. 
						for (String code: lodestoneCodes) {
							if (compareWithDontCares(getNeighbours(value, y, x, tsGen.tallest, tsGen.widest), code)) {
								String name = tsGen.lodestoneCodeToName(code);
								String objectname = (Character.isUpperCase(value.charAt(0)) ? "Unpushable" : "Pushable") + " " + name;
								int[] offsets = tsGen.lodestoneNameToOffset(name);
								int xoffset = offsets[1];
								int yoffset = offsets[0];
								Objects.add(tsGen.createObject(objectname, x - xoffset, y + yoffset));
								for (int i = 0; i < tsGen.tallest; i++) {
									for (int j = -tsGen.widest + 1; j < tsGen.widest; j++) {
										if (code.charAt(i * (tsGen.widest * 2 - 1) + j + tsGen.widest - 1) == "1".charAt(0)) {
											checked[i + y][j + x] = 1;
										}
									}
								} break;
							}
						}
					}
				}
			}
		}
	}

	public String getNeighbours(String value, int y, int x, int tallest, int widest) {
		if (tallest == -1 || widest == -1) throw new IllegalArgumentException("Please run tsGen.genLlodestoneCodes()");
		Integer[][] out = new Integer[tallest][widest * 2 - 1];
		for (int i = 0; i < tallest; i++) {
			for (int j = 0; j < widest * 2 - 1; j++) {
				out[i][j] = (data[y + i][x + j - widest + 1].equals(value)) ? 1 : 0;
			}
		}
		return arrayToString(out, false);
	}
	
	/**
	 * Uses the concept of dontcares from circuit logic to compare binary strings
	 * @param input
	 * @param condition
	 * @return
	 */
	public boolean compareWithDontCares(String input, String condition) {
		if (input.length() != condition.length()) throw new IllegalArgumentException("Strings does not match in length " + input + ": " + input.length() + " vs " + condition + ": " + condition.length());
		for (int y = 0; y < condition.length(); y++) {
			if 	    (condition.charAt(y) == 88 || input.charAt(y) == 88) 	continue;
			else if (condition.charAt(y) != input.charAt(y)) 				return false;
		} return true;
	}
	
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
