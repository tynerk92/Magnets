
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;


public class TextToTmx {
	
	private String level;
	private String[][] data;
	private Block[] Blocks;
	private Random random = new Random();
	private Hashtable<String, Integer> nameToID = new Hashtable<String, Integer>();
	private Boolean wroteGraphics = false;
	private String graphicsCode = "";
	private int whichWallSet;
	private boolean plantTrees = false;
	
	private String chooseATheme = "Underground"; // "City"
	
	TextToTmx() {
		initiateTable();
		if (chooseATheme.equals("Underground")) {
			this.whichWallSet = 2;
			this.plantTrees = false;
		}
	}
	
	private void setLevel(String level) {
		this.level = level;
	}
	
	// A block has three properties: dir - Relative path to the graphic, name and dimensions
	private class Block {
		String dir, name;
		int[] dimensions;
		Block(String dir, String name, int[] dimensions) {
			this.dir = dir;
			this.name = name;
			this.dimensions = dimensions;
		}
	}
	
	private void initiateTable() {
		Blocks = new Block[] {
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01011010.png",                   "Set 1 Floor 01011010",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01011011.png",                   "Set 1 Floor 01011011",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01011110.png",                   "Set 1 Floor 01011110",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01011111.png",                   "Set 1 Floor 01011111",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01011X0X.png",                   "Set 1 Floor 01011X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01111010.png",                   "Set 1 Floor 01111010",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01111011.png",                   "Set 1 Floor 01111011",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01111110.png",                   "Set 1 Floor 01111110",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01111111.png",                   "Set 1 Floor 01111111",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01111X0X.png",                   "Set 1 Floor 01111X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01X1001X.png",                   "Set 1 Floor 01X1001X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01X1011X.png",                   "Set 1 Floor 01X1011X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 01X10X0X.png",                   "Set 1 Floor 01X10X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11011010.png",                   "Set 1 Floor 11011010",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11011011.png",                   "Set 1 Floor 11011011",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11011110.png",                   "Set 1 Floor 11011110",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11011111.png",                   "Set 1 Floor 11011111",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11011X0X.png",                   "Set 1 Floor 11011X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11111010.png",                   "Set 1 Floor 11111010",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11111011.png",                   "Set 1 Floor 11111011",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11111110.png",                   "Set 1 Floor 11111110",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11111111.png",                   "Set 1 Floor 11111111",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11111X0X.png",                   "Set 1 Floor 11111X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11X1001X.png",                   "Set 1 Floor 11X1001X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11X1011X.png",                   "Set 1 Floor 11X1011X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor 11X10X0X.png",                   "Set 1 Floor 11X10X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X00X0X.png",                   "Set 1 Floor X0X00X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X00X1X.png",                   "Set 1 Floor X0X00X1X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X01X0X.png",                   "Set 1 Floor X0X01X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X01X10.png",                   "Set 1 Floor X0X01X10",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X01X11.png",                   "Set 1 Floor X0X01X11",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X1001X.png",                   "Set 1 Floor X0X1001X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X1011X.png",                   "Set 1 Floor X0X1011X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X10X0X.png",                   "Set 1 Floor X0X10X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X11010.png",                   "Set 1 Floor X0X11010",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X11011.png",                   "Set 1 Floor X0X11011",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X11110.png",                   "Set 1 Floor X0X11110",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X11111.png",                   "Set 1 Floor X0X11111",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X0X11X0X.png",                   "Set 1 Floor X0X11X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X1001X0X.png",                   "Set 1 Floor X1001X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X1001X10.png",                   "Set 1 Floor X1001X10",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X1001X11.png",                   "Set 1 Floor X1001X11",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X1101X0X.png",                   "Set 1 Floor X1101X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X1101X10.png",                   "Set 1 Floor X1101X10",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X1101X11.png",                   "Set 1 Floor X1101X11",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X1X00X0X.png",                   "Set 1 Floor X1X00X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Crude)/Floor X1X00X1X.png",                   "Set 1 Floor X1X00X1X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 1.png",                          "Set 2 Floor 1",                       new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 2.png",                          "Set 2 Floor 2",                       new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 3.png",                          "Set 2 Floor 3",                       new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 4.png",                          "Set 2 Floor 4",                       new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 5.png",                          "Set 2 Floor 5",                       new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 6.png",                          "Set 2 Floor 6",                       new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 7.png",                          "Set 2 Floor 7",                       new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 8.png",                          "Set 2 Floor 8",                       new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 9.png",                          "Set 2 Floor 9",                       new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 10.png",                         "Set 2 Floor 10",                      new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 11.png",                         "Set 2 Floor 11",                      new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 12.png",                         "Set 2 Floor 12",                      new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 13.png",                         "Set 2 Floor 13",                      new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 14.png",                         "Set 2 Floor 14",                      new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 15.png",                         "Set 2 Floor 15",                      new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 2 (Tiled)/Floor 16.png",                         "Set 2 Floor 16",                      new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Debris 1.png",                              "Debris 1",                            new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Debris 2.png",                              "Debris 2",                            new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Debris 3.png",                              "Debris 3",                            new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Debris 4.png",                              "Debris 4",                            new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Debris 5.png",                              "Debris 5",                            new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Debris 6.png",                              "Debris 6",                            new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Debris 7.png",                              "Debris 7",                            new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Debris 8.png",                              "Debris 8",                            new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Blue Scratches 1.png",                      "Blue Scratches 1",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Blue Scratches 2.png",                      "Blue Scratches 2",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Blue Scratches 3.png",                      "Blue Scratches 3",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Blue Scratches 4.png",                      "Blue Scratches 4",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Grey Scratches 1.png",                      "Grey Scratches 1",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Grey Scratches 2.png",                      "Grey Scratches 2",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Grey Scratches 3.png",                      "Grey Scratches 3",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Grey Scratches 4.png",                      "Grey Scratches 4",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Grey Scratches 5.png",                      "Grey Scratches 5",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Grey Scratches 6.png",                      "Grey Scratches 6",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Grey Scratches 7.png",                      "Grey Scratches 7",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Grey Scratches 8.png",                      "Grey Scratches 8",                    new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Depression 1.png",                          "Depression 1",                        new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Depression 2.png",                          "Depression 2",                        new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Depression 3.png",                          "Depression 3",                        new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Depression 4.png",                          "Depression 4",                        new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Depression 5.png",                          "Depression 5",                        new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Depression 6.png",                          "Depression 6",                        new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Depression 7.png",                          "Depression 7",                        new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Depression 8.png",                          "Depression 8",                        new int[] {32, 32}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01011010.png",                 "Set 1 Wall 01011010",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01011011.png",                 "Set 1 Wall 01011011",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01011110.png",                 "Set 1 Wall 01011110",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01011111.png",                 "Set 1 Wall 01011111",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01011X0X.png",                 "Set 1 Wall 01011X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01111010.png",                 "Set 1 Wall 01111010",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01111011.png",                 "Set 1 Wall 01111011",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01111110.png",                 "Set 1 Wall 01111110",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01111111.png",                 "Set 1 Wall 01111111",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01111X0X.png",                 "Set 1 Wall 01111X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01X1001X.png",                 "Set 1 Wall 01X1001X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01X1011X.png",                 "Set 1 Wall 01X1011X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 01X10X0X.png",                 "Set 1 Wall 01X10X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11011010.png",                 "Set 1 Wall 11011010",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11011011.png",                 "Set 1 Wall 11011011",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11011110.png",                 "Set 1 Wall 11011110",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11011111.png",                 "Set 1 Wall 11011111",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11011X0X.png",                 "Set 1 Wall 11011X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11111010.png",                 "Set 1 Wall 11111010",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11111011.png",                 "Set 1 Wall 11111011",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11111110.png",                 "Set 1 Wall 11111110",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11111111.png",                 "Set 1 Wall 11111111",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11111X0X.png",                 "Set 1 Wall 11111X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11X1001X.png",                 "Set 1 Wall 11X1001X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11X1011X.png",                 "Set 1 Wall 11X1011X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall 11X10X0X.png",                 "Set 1 Wall 11X10X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X00X0X.png",                 "Set 1 Wall X0X00X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X00X1X.png",                 "Set 1 Wall X0X00X1X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X01X0X.png",                 "Set 1 Wall X0X01X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X01X10.png",                 "Set 1 Wall X0X01X10",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X01X11.png",                 "Set 1 Wall X0X01X11",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X1001X.png",                 "Set 1 Wall X0X1001X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X1011X.png",                 "Set 1 Wall X0X1011X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X10X0X.png",                 "Set 1 Wall X0X10X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X11010.png",                 "Set 1 Wall X0X11010",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X11011.png",                 "Set 1 Wall X0X11011",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X11110.png",                 "Set 1 Wall X0X11110",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X11111.png",                 "Set 1 Wall X0X11111",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X0X11X0X.png",                 "Set 1 Wall X0X11X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X1001X0X.png",                 "Set 1 Wall X1001X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X1001X10.png",                 "Set 1 Wall X1001X10",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X1001X11.png",                 "Set 1 Wall X1001X11",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X1101X0X.png",                 "Set 1 Wall X1101X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X1101X10.png",                 "Set 1 Wall X1101X10",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X1101X11.png",                 "Set 1 Wall X1101X11",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X1X00X0X.png",                 "Set 1 Wall X1X00X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall X1X00X1X.png",                 "Set 1 Wall X1X00X1X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Decoration/Wall Decoration 1.png",  "Wall Decoration 1",                   new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Decoration/Wall Decoration 2.png",  "Wall Decoration 2",                   new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Decoration/Wall Decoration 3.png",  "Wall Decoration 3",                   new int[] {32, 64}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Decoration/Wall Decoration 4.png",  "Wall Decoration 4",                   new int[] {32, 64}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Decoration/Wall Decoration 5.png",  "Wall Decoration 5",                   new int[] {32, 64}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Decoration/Wall Decoration 6.png",  "Wall Decoration 6",                   new int[] {32, 64}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Big Tree 111111X1X.png", 	          "Big Tree 111111X1X",				     new int[] {96, 96}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01011010.png",                     "Set 2 Wall 01011010",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01011011.png",                     "Set 2 Wall 01011011",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01011110.png",                     "Set 2 Wall 01011110",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01011111.png",                     "Set 2 Wall 01011111",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01011X0X.png",                     "Set 2 Wall 01011X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01111010.png",                     "Set 2 Wall 01111010",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01111011.png",                     "Set 2 Wall 01111011",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01111110.png",                     "Set 2 Wall 01111110",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01111111.png",                     "Set 2 Wall 01111111",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01111X0X.png",                     "Set 2 Wall 01111X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01X1001X.png",                     "Set 2 Wall 01X1001X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01X1011X.png",                     "Set 2 Wall 01X1011X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 01X10X0X.png",                     "Set 2 Wall 01X10X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11011010.png",                     "Set 2 Wall 11011010",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11011011.png",                     "Set 2 Wall 11011011",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11011110.png",                     "Set 2 Wall 11011110",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11011111.png",                     "Set 2 Wall 11011111",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11011X0X.png",                     "Set 2 Wall 11011X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11111010.png",                     "Set 2 Wall 11111010",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11111011.png",                     "Set 2 Wall 11111011",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11111110.png",                     "Set 2 Wall 11111110",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11111111.png",                     "Set 2 Wall 11111111",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11111X0X.png",                     "Set 2 Wall 11111X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11X1001X.png",                     "Set 2 Wall 11X1001X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11X1011X.png",                     "Set 2 Wall 11X1011X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall 11X10X0X.png",                     "Set 2 Wall 11X10X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X00X0X.png",                     "Set 2 Wall X0X00X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X00X1X.png",                     "Set 2 Wall X0X00X1X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X01X0X.png",                     "Set 2 Wall X0X01X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X01X10.png",                     "Set 2 Wall X0X01X10",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X01X11.png",                     "Set 2 Wall X0X01X11",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X1001X.png",                     "Set 2 Wall X0X1001X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X1011X.png",                     "Set 2 Wall X0X1011X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X10X0X.png",                     "Set 2 Wall X0X10X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X11010.png",                     "Set 2 Wall X0X11010",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X11011.png",                     "Set 2 Wall X0X11011",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X11110.png",                     "Set 2 Wall X0X11110",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X11111.png",                     "Set 2 Wall X0X11111",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X0X11X0X.png",                     "Set 2 Wall X0X11X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X1001X0X.png",                     "Set 2 Wall X1001X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X1001X10.png",                     "Set 2 Wall X1001X10",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X1001X11.png",                     "Set 2 Wall X1001X11",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X1101X0X.png",                     "Set 2 Wall X1101X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X1101X10.png",                     "Set 2 Wall X1101X10",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X1101X11.png",                     "Set 2 Wall X1101X11",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X1X00X0X.png",                     "Set 2 Wall X1X00X0X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 2 (Cave)/Wall X1X00X1X.png",                     "Set 2 Wall X1X00X1X",                 new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 1x1 1 - 1 1.png",             "Lodestone (Pushable) 1x1 1",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 1x1 2 - 1 1.png",             "Lodestone (Pushable) 1x1 2",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 1x1 3 - 1 1.png",             "Lodestone (Pushable) 1x1 3",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 1x1 4 - 1 1.png",             "Lodestone (Pushable) 1x1 4",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 1x2 1 - 1 11.png",            "Lodestone (Pushable) 1x2 1",          new int[] {32, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 1x3 1 - 1 111.png",           "Lodestone (Pushable) 1x3 1",          new int[] {32, 11}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x1 1 - 2 11.png",            "Lodestone (Pushable) 2x1 1",          new int[] {64, 48}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x2 1 - 2 1110.png",          "Lodestone (Pushable) 2x2 1",          new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x2 2 - 2 1101.png",          "Lodestone (Pushable) 2x2 2",          new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x2 3 - 2 0111.png",          "Lodestone (Pushable) 2x2 3",          new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x2 4 - 2 1011.png",          "Lodestone (Pushable) 2x2 4",          new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x2 5 - 2 1111.png",          "Lodestone (Pushable) 2x2 5",          new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 1 - 2 101110.png",        "Lodestone (Pushable) 2x3 1",          new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 2 - 2 011101.png",        "Lodestone (Pushable) 2x3 2",          new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 3 - 2 101011.png",        "Lodestone (Pushable) 2x3 3",          new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 4 - 2 111010.png",        "Lodestone (Pushable) 2x3 4",          new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 5 - 2 010111.png",        "Lodestone (Pushable) 2x3 5",          new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 6 - 2 110101.png",        "Lodestone (Pushable) 2x3 6",          new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 7 - 2 101101.png",        "Lodestone (Pushable) 2x3 7",          new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 8 - 2 011110.png",        "Lodestone (Pushable) 2x3 8",          new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 9 - 2 111011.png",        "Lodestone (Pushable) 2x3 9",          new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 10 - 2 110111.png",       "Lodestone (Pushable) 2x3 10",         new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 11 - 2 111110.png",       "Lodestone (Pushable) 2x3 11",         new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 12 - 2 111101.png",       "Lodestone (Pushable) 2x3 12",         new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 13 - 2 101111.png",       "Lodestone (Pushable) 2x3 13",         new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 2x3 14 - 2 011111.png",       "Lodestone (Pushable) 2x3 14",         new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x1 1 - 3 111.png",           "Lodestone (Pushable) 3x1 1",          new int[] {96, 48}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 1 - 3 010111.png",        "Lodestone (Pushable) 3x2 1",          new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 2 - 3 111010.png",        "Lodestone (Pushable) 3x2 2",          new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 3 - 3 111100.png",        "Lodestone (Pushable) 3x2 3",          new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 4 - 3 111001.png",        "Lodestone (Pushable) 3x2 4",          new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 5 - 3 100111.png",        "Lodestone (Pushable) 3x2 5",          new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 6 - 3 001111.png",        "Lodestone (Pushable) 3x2 6",          new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 7 - 3 110011.png",        "Lodestone (Pushable) 3x2 7",          new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 8 - 3 011110.png",        "Lodestone (Pushable) 3x2 8",          new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 9 - 3 111101.png",        "Lodestone (Pushable) 3x2 9",          new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 10 - 3 101111.png",       "Lodestone (Pushable) 3x2 10",         new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 11 - 3 111110.png",       "Lodestone (Pushable) 3x2 11",         new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 12 - 3 111011.png",       "Lodestone (Pushable) 3x2 12",         new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 13 - 3 110111.png",       "Lodestone (Pushable) 3x2 13",         new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Pushable) 3x2 14 - 3 011111.png",       "Lodestone (Pushable) 3x2 14",         new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 1x1 1 - 1 1.png",           "Lodestone (Unpushable) 1x1 1",        new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 1x2 1 - 1 11.png",          "Lodestone (Unpushable) 1x2 1",        new int[] {32, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 1x3 1 - 1 111.png",         "Lodestone (Unpushable) 1x3 1",        new int[] {32, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x1 1 - 2 11.png",          "Lodestone (Unpushable) 2x1 1",        new int[] {64, 48}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x2 1 - 2 1110.png",        "Lodestone (Unpushable) 2x2 1",        new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x2 2 - 2 1101.png",        "Lodestone (Unpushable) 2x2 2",        new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x2 3 - 2 0111.png",        "Lodestone (Unpushable) 2x2 3",        new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x2 4 - 2 1011.png",        "Lodestone (Unpushable) 2x2 4",        new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x2 5 - 2 1111.png",        "Lodestone (Unpushable) 2x2 5",        new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 1 - 2 101110.png",      "Lodestone (Unpushable) 2x3 1",        new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 2 - 2 011101.png",      "Lodestone (Unpushable) 2x3 2",        new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 3 - 2 101011.png",      "Lodestone (Unpushable) 2x3 3",        new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 4 - 2 111010.png",      "Lodestone (Unpushable) 2x3 4",        new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 5 - 2 010111.png",      "Lodestone (Unpushable) 2x3 5",        new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 6 - 2 110101.png",      "Lodestone (Unpushable) 2x3 6",        new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 7 - 2 101101.png",      "Lodestone (Unpushable) 2x3 7",        new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 8 - 2 011110.png",      "Lodestone (Unpushable) 2x3 8",        new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 9 - 2 111011.png",      "Lodestone (Unpushable) 2x3 9",        new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 10 - 2 110111.png",     "Lodestone (Unpushable) 2x3 10",       new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 11 - 2 111110.png",     "Lodestone (Unpushable) 2x3 11",       new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 12 - 2 111101.png",     "Lodestone (Unpushable) 2x3 12",       new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 13 - 2 101111.png",     "Lodestone (Unpushable) 2x3 13",       new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 2x3 14 - 2 011111.png",     "Lodestone (Unpushable) 2x3 14",       new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x1 1 - 3 111.png",         "Lodestone (Unpushable) 3x1 1",        new int[] {96, 48}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 1 - 3 010111.png",      "Lodestone (Unpushable) 3x2 1",        new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 2 - 3 111010.png",      "Lodestone (Unpushable) 3x2 2",        new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 3 - 3 111100.png",      "Lodestone (Unpushable) 3x2 3",        new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 4 - 3 111001.png",      "Lodestone (Unpushable) 3x2 4",        new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 5 - 3 100111.png",      "Lodestone (Unpushable) 3x2 5",        new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 6 - 3 001111.png",      "Lodestone (Unpushable) 3x2 6",        new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 7 - 3 110011.png",      "Lodestone (Unpushable) 3x2 7",        new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 8 - 3 011110.png",      "Lodestone (Unpushable) 3x2 8",        new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 9 - 3 111101.png",      "Lodestone (Unpushable) 3x2 9",        new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 10 - 3 101111.png",     "Lodestone (Unpushable) 3x2 10",       new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 11 - 3 111110.png",     "Lodestone (Unpushable) 3x2 11",       new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 12 - 3 111011.png",     "Lodestone (Unpushable) 3x2 12",       new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 13 - 3 110111.png",     "Lodestone (Unpushable) 3x2 13",       new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Lodestone (Unpushable) 3x2 14 - 3 011111.png",     "Lodestone (Unpushable) 3x2 14",       new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Button 1 State 1.png",                             "Button 1 State 1",                    new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Button 1 State 2.png",                             "Button 1 State 2",                    new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Button 1 State 3.png",                             "Button 1 State 3",                    new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Contraption 1 NSEW State 1.png",                   "Contraption 1 NSEW State 1",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 1 NSEW State 2.png",                   "Contraption 1 NSEW State 2",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 1 NSEW State 3.png",                   "Contraption 1 NSEW State 3",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 1 NSEW State 4.png",                   "Contraption 1 NSEW State 4",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 1 NSEW State 5.png",                   "Contraption 1 NSEW State 5",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 1 NSEW State 6.png",                   "Contraption 1 NSEW State 6",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 1 NSEW State 7.png",                   "Contraption 1 NSEW State 7",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 1 NSEW State 8.png",                   "Contraption 1 NSEW State 8",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 1 NSEW State 9.png",                   "Contraption 1 NSEW State 9",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Button 2 State 1.png",                             "Button 2 State 1",                    new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Button 2 State 2.png",                             "Button 2 State 2",                    new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Button 2 State 3.png",                             "Button 2 State 3",                    new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Contraption 2 NSEW State 1.png",                   "Contraption 2 NSEW State 1",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 2 NSEW State 2.png",                   "Contraption 2 NSEW State 2",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 2 NSEW State 3.png",                   "Contraption 2 NSEW State 3",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 2 NSEW State 4.png",                   "Contraption 2 NSEW State 4",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 2 NSEW State 5.png",                   "Contraption 2 NSEW State 5",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 2 NSEW State 6.png",                   "Contraption 2 NSEW State 6",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 2 NSEW State 7.png",                   "Contraption 2 NSEW State 7",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 2 NSEW State 8.png",                   "Contraption 2 NSEW State 8",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Contraption 2 NSEW State 9.png",                   "Contraption 2 NSEW State 9",          new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Cave Forwards Front State 1.png",             "Exit Cave Forwards Front State 1",    new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Cave Forwards Front State 2.png",             "Exit Cave Forwards Front State 2",    new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Cave Forwards Front State 3.png",             "Exit Cave Forwards Front State 3",    new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Cave Forwards Front State 4.png",             "Exit Cave Forwards Front State 4",    new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Cave Forwards Front State 5.png",             "Exit Cave Forwards Front State 5",    new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Cave Down Front State 1.png",                 "Exit Cave Down Front State 1",        new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Cave Down Front State 2.png",                 "Exit Cave Down Front State 2",        new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Cave Down Front State 3.png",                 "Exit Cave Down Front State 3",        new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Cave Down Front State 4.png",                 "Exit Cave Down Front State 4",        new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Cave Down Front State 5.png",                 "Exit Cave Down Front State 5",        new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Standard Front State 1.png",                  "Exit Standard Front State 1",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Standard Front State 2.png",                  "Exit Standard Front State 2",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Standard Front State 3.png",                  "Exit Standard Front State 3",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Standard Front State 4.png",                  "Exit Standard Front State 4",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Standard Front State 5.png",                  "Exit Standard Front State 5",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnet North State 1.png",                         "Magnet North State 1",                new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnet North State 2.png",                         "Magnet North State 2",                new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnet North State 3.png",                         "Magnet North State 3",                new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnet North State 4.png",                         "Magnet North State 4",                new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnet North State 5.png",                         "Magnet North State 5",                new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnet South State 1.png",                         "Magnet South State 1",                new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnet South State 2.png",                         "Magnet South State 2",                new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnet South State 3.png",                         "Magnet South State 3",                new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnet South State 4.png",                         "Magnet South State 4",                new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnet South State 5.png",                         "Magnet South State 5",                new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Player.png",                                       "Player",                              new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Magnetic Floor State 1.png",                       "Magnetic Floor State 1",              new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Magnetic Floor State 2.png",                       "Magnetic Floor State 2",              new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Magnetic Floor State 3.png",                       "Magnetic Floor State 3",              new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Magnetic Floor State 4.png",                       "Magnetic Floor State 4",              new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Magnetic Floor State 5.png",                       "Magnetic Floor State 5",              new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Horizontal State 1.png",        "Magnetized Horizontal State 1",       new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Horizontal State 2.png",        "Magnetized Horizontal State 2",       new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Horizontal State 3.png",        "Magnetized Horizontal State 3",       new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Horizontal State 4.png",        "Magnetized Horizontal State 4",       new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Horizontal State 5.png",        "Magnetized Horizontal State 5",       new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Horizontal State 6.png",        "Magnetized Horizontal State 6",       new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Horizontal State 7.png",        "Magnetized Horizontal State 7",       new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Vertical State 1.png",          "Magnetized Vertical State 1",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Vertical State 2.png",          "Magnetized Vertical State 2",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Vertical State 3.png",          "Magnetized Vertical State 3",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Vertical State 4.png",          "Magnetized Vertical State 4",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Vertical State 5.png",          "Magnetized Vertical State 5",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Vertical State 6.png",          "Magnetized Vertical State 6",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetized Vertical State 7.png",          "Magnetized Vertical State 7",         new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 1x1 1.png",     "Magnetic Overlay Lodestone 1x1 1",    new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 1x2 1.png",     "Magnetic Overlay Lodestone 1x2 1",    new int[] {32, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 1x3 1.png",     "Magnetic Overlay Lodestone 1x3 1",    new int[] {32, 11}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x1 1.png",     "Magnetic Overlay Lodestone 2x1 1",    new int[] {64, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x2 1.png",     "Magnetic Overlay Lodestone 2x2 1",    new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x2 2.png",     "Magnetic Overlay Lodestone 2x2 2",    new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x2 3.png",     "Magnetic Overlay Lodestone 2x2 3",    new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x2 4.png",     "Magnetic Overlay Lodestone 2x2 4",    new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x2 5.png",     "Magnetic Overlay Lodestone 2x2 5",    new int[] {64, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 1.png",     "Magnetic Overlay Lodestone 2x3 1",    new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 2.png",     "Magnetic Overlay Lodestone 2x3 2",    new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 3.png",     "Magnetic Overlay Lodestone 2x3 3",    new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 4.png",     "Magnetic Overlay Lodestone 2x3 4",    new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 5.png",     "Magnetic Overlay Lodestone 2x3 5",    new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 6.png",     "Magnetic Overlay Lodestone 2x3 6",    new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 7.png",     "Magnetic Overlay Lodestone 2x3 7",    new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 8.png",     "Magnetic Overlay Lodestone 2x3 8",    new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 9.png",     "Magnetic Overlay Lodestone 2x3 9",    new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 10.png",    "Magnetic Overlay Lodestone 2x3 10",   new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 11.png",    "Magnetic Overlay Lodestone 2x3 11",   new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 12.png",    "Magnetic Overlay Lodestone 2x3 12",   new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 13.png",    "Magnetic Overlay Lodestone 2x3 13",   new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 2x3 14.png",    "Magnetic Overlay Lodestone 2x3 14",   new int[] {64, 112}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x1 1.png",     "Magnetic Overlay Lodestone 3x1 1",    new int[] {96, 48}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 1.png",     "Magnetic Overlay Lodestone 3x2 1",    new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 2.png",     "Magnetic Overlay Lodestone 3x2 2",    new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 3.png",     "Magnetic Overlay Lodestone 3x2 3",    new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 4.png",     "Magnetic Overlay Lodestone 3x2 4",    new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 5.png",     "Magnetic Overlay Lodestone 3x2 5",    new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 6.png",     "Magnetic Overlay Lodestone 3x2 6",    new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 7.png",     "Magnetic Overlay Lodestone 3x2 7",    new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 8.png",     "Magnetic Overlay Lodestone 3x2 8",    new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 9.png",     "Magnetic Overlay Lodestone 3x2 9",    new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 10.png",    "Magnetic Overlay Lodestone 3x2 10",   new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 11.png",    "Magnetic Overlay Lodestone 3x2 11",   new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 12.png",    "Magnetic Overlay Lodestone 3x2 12",   new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 13.png",    "Magnetic Overlay Lodestone 3x2 13",   new int[] {96, 80}),
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 14.png",    "Magnetic Overlay Lodestone 3x2 14",   new int[] {96, 80}),
				new Block ("../../Graphics/Ambience/Darker.png",                                      "Darker",                              new int[] {32, 32})
		};
		
		for (int i = 0; i < Blocks.length; i++) {
			nameToID.put(Blocks[i].name, i);
		}
	}
	
	private void writeGraphics(PrintWriter writer, int cols, int rows) {
		
		// The only varying part between every level.
		// The dimensions of the levels change. 
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<map version=\"1.0\" orientation=\"orthogonal\" renderorder=\"right-down\" width=\"" + cols + "\" height=\"" + rows + "\" tilewidth=\"32\" tileheight=\"32\" nextobjectid=\"1\">\r\n" + 
				" <tileset firstgid=\"1\" name=\"Tileset\" tilewidth=\"96\" tileheight=\"112\" tilecount=\"" + Blocks.length + "\" columns=\"0\">");
		
		// This part is the same for any level, hence it should only be executed once.
		if (!wroteGraphics) {
			for (int n = 0; n < Blocks.length; n++) {
	
				graphicsCode += "\n  <tile id=\"" + n + "\">\r\n";
	
	            if (Blocks[n].name.contains("Wall") || Blocks[n].name.contains("Big Tree")) {
	            	graphicsCode += "   <properties>\n" +
	                        "    <property name=\"Type\" value=\"Wall\"/>\n" +
	                        "   </properties>\r\n";
	
	            } else if (Blocks[n].name.contains("Overlay Lodestone ")) {
	            	graphicsCode += "   <properties>\r\n" + 
							"    <property name=\"Frame Depth\" type=\"int\" value=\"-1\"/>\r\n" + 
							"    <property name=\"Name\" value=\"" + Blocks[n].name + "\"/>\r\n" + 
							"   </properties>\r\n";
				
	            } else if (Blocks[n].name.contains("Button ")) {
	            	
	            	// Naming convention!!!
	            	String[] a = Blocks[n].name.split(" ");
	            	
	            	int whichSet = Integer.parseInt(a[1]);
	            	int whichFrame = Integer.parseInt(a[3]);
	            	
	            	String out = "   <properties>\r\n" + 
								 "    <property name=\"Frame Depth\" type=\"int\" value=\"-1\"/>\r\n";
	            	
	            	if (whichFrame == 1) {
	            		out += "";
	            	}
	            	graphicsCode += "   <properties>\r\n" + 
							"    <property name=\"Frame Depth\" type=\"int\" value=\"-1\"/>\r\n" + 
							"    <property name=\"Name\" value=\"Button " + Blocks[n].name.split(" ")[1].charAt(0) + "\"/>\r\n" + 
							"   </properties>\r\n";
					
	            } else if (Blocks[n].name.contains("Contraption")) {
		            	
	        		// Naming convention!!!
	            	String[] a = Blocks[n].name.split(" ");
	            	
	            	int totalFrames = 9;
	            	
	            	int whichSet = Integer.parseInt(a[1]);
	            	int whichFrame = Integer.parseInt(a[4]);
	            	String whichSides = a[2];
	            	
	            	graphicsCode += "   <properties>\r\n";
	            	String out = "";
								 
	            	
	            	if (whichFrame == 1 || whichFrame == totalFrames) {
	            		out +=   "    <property name=\"#\" type=\"int\" value=\"(this)\"/>\r\n";
	            		out +=   "    <property name=\"#Closing\" type=\"int\" value=\"Door Closing\"/>\r\n";
	            		if (whichFrame == 1) {
	            			out +=   "    <property name=\"#Opened\" type=\"int\" value=\"Door Opened\"/>\r\n";
	            		} else {
	            			out +=   "    <property name=\"#Closed\" type=\"int\" value=\"Door Closed\"/>\r\n";
	            		}
	            		out +=   "    <property name=\"#Opening\" type=\"int\" value=\"Door Opening\"/>\r\n";
	            	} 
	            	
	            	out += 	     "    <property name=\"Frame Depth\" type=\"int\" value=\"-1\"/>\r\n";
	            	
	            	graphicsCode += 
							"    <property name=\"Name\" value=\"" + Blocks[n].name + "\"/>\r\n" + 
							"   </properties>\r\n";
						
				} else if (Blocks[n].name.contains("Lodestone")) {
	            	
	            	String[] data = Blocks[n].dir.substring(Blocks[n].dir.indexOf(" - ") + 3).split(" ");
	            	
	            	String area = data[1].substring(0, data[1].length() - 4);
	            	String width = data[0];
	            	Boolean pushable = Blocks[n].name.contains("Pushable");
	            	
	            	String write = Blocks[n].name.split("\\) ")[1];
	            	if (write.contains("1x1")) write = "1x1 1";
	            	
	            	graphicsCode += "   <properties>\n" +
							"    <property name=\"#\" value=\"(this)\"/>\n" +
							"    <property name=\"#Magnetised\" value=\"Magnetic Overlay Lodestone " + write + "\"/>\n" +
							"    <property name=\"Body Area\" value=\"" + area + "\"/>\n" +
							"    <property name=\"Body Width\" type=\"int\" value=\"" + width + "\"/>\n" +
							"    <property name=\"IsMagnetisable\" type=\"bool\" value=\"true\"/>\n" +
							"    <property name=\"IsPushable\" type=\"bool\" value=\"" + pushable + "\"/>\n" +
							"    <property name=\"Type\" value=\"Block\"/>\n" +
							"   </properties>\r\n";
	
	            } else if (Blocks[n].name.contains("Magnet ") && Blocks[n].name.contains("State 1")) {
	            	graphicsCode += "   <properties>\n" +
							"    <property name=\"#\" value=\"(this)\"/>\n" +
							"    <property name=\"Type\" value=\"Magnetic Source\"/>\n" +
							"    <property name=\"Pole\" value=\"South\"/>\n" +
							"   </properties>\r\n";
					
	            } else if (Blocks[n].name.contains("Magnetic Floor")) {
	            	graphicsCode += "   <properties>\n" +
							"    <property name=\"#\" value=\"(this)\"/>\n" +
							"    <property name=\"Actor Depth\" type=\"int\" value=\"-1\"/>\n" +
							"    <property name=\"Type\" value=\"Magnetic Floor\"/>\n" +
							"   </properties>\r\n";
	
				} else if (Blocks[n].dir.contains("Objects/Player.png")) {
					graphicsCode += "   <properties>\n" +
							"    <property name=\"#\" value=\"(this)\"/>\n" +
							"    <property name=\"#Walking\" value=\"(this)\"/>\n" +
							"    <property name=\"Type\" value=\"Player\"/>\n" +
							"   </properties>\r\n";
					
				} 
	
	            graphicsCode += "   <image width=\"" + Blocks[n].dimensions[0] + "\" height=\"" + Blocks[n].dimensions[1] + "\" source=\"" + Blocks[n].dir + "\"/>\r\n";
				
				// Gives a animaation to each of the magnetic areas. All of them will be starting at different
				// states but following the same pattern.
				
				if ((Blocks[n].name.contains("Magnet ") || Blocks[n].name.contains("Magnetic Floor")) && Blocks[n].name.contains("State 1")) {
					
					int numFrames = 5;
					int interval = 640;
					
					int delay = (Blocks[n].name.contains("South")) ? numFrames / 2 : 0;
						
					graphicsCode += "   <animation>\r\n";
					for (int j = 0; j < 3; j++)
						for (int i = 0; i < numFrames; i++) 
							graphicsCode += "    <frame tileid=\"" + (n + (i * j + i + j + delay) % numFrames) + "\" duration=\"" + interval + "\"/>\r\n";
					graphicsCode += "   </animation>\r\n";
					
				} else if (Blocks[n].name.contains("Contraption ")) {
					
					// Naming convention!!!
	            	String[] a = Blocks[n].name.split(" ");
					
					int numFrames = 9;
					int interval = 40;
					int whichFrame = Integer.parseInt(a[4]);
					
					if (whichFrame == 2 || whichFrame == (numFrames - 1)) {
						graphicsCode += "   <animation>\r\n";
						if (whichFrame == 2) {
							for (int i = 0; i < numFrames - 2; i++)
								writer.println("    <frame tileid=\"" + (n + i) + "\" duration=\"" + interval + "\"/>\r\n");
						} else if (whichFrame == (numFrames - 1)) {
							for (int i = 0; i < numFrames - 2; i++)
								writer.println("    <frame tileid=\"" + (n - i) + "\" duration=\"" + interval + "\"/>\r\n");
						}
						graphicsCode += "   </animation>\r\n";
					}
					
				} else if (Blocks[n].name.contains("Exit") && Blocks[n].name.contains("State 1")) {
					
					int numFrames = 5;
					int interval = 80;
					
					graphicsCode += "   <animation>\r\n";
					for (int i = 0; i < numFrames; i++) 		
						graphicsCode += "    <frame tileid=\"" + (n + i) + "\" duration=\"" + interval + "\"/>\r\n";
					for (int i = numFrames - 1; i >= 0; i--)  	
						graphicsCode += "    <frame tileid=\"" + (n + i) + "\" duration=\"" + interval + "\"/>\r\n";
						graphicsCode += "   </animation>\r\n";
					
				} else if (Blocks[n].name.contains("Magnetized Horizontal State 1") ||
						   Blocks[n].name.contains("Magnetized Vertical State 1")) {
					
					int numFrames = 7;
					int interval = 375;
					
					graphicsCode += "   <animation>\r\n";
					for (int i = 0; i < numFrames; i++) 
						graphicsCode += "    <frame tileid=\"" + (n + i) + "\" duration=\"" + interval + "\"/>\r\n";
					graphicsCode += "   </animation>\r\n";
				}
				
				graphicsCode += "  </tile>\r\n";
			}
			
			graphicsCode += " </tileset>\r\n";
			wroteGraphics = true;
		} 
		
		writer.println(graphicsCode);
	}
	
	public void convert(String dir) throws IOException {
		
		// Check validity of the level
		int numPlayers = 0;
		int numExits = 0;
		
		// All the level information gotten from the data provided
		String name = level.substring(0, level.indexOf("\r\n"));
		level = level.substring(level.indexOf("\r\n") + 2);
		
		// This is to make the level look better i suppose. Makes the camera moves around more dynamically when nearing the 
		// boundaries of the level
		int bufferWalls = 4;
		
		int cols = level.indexOf("\r\n") + bufferWalls * 2;
		int rows = (level.length() - level.replace("\r\n", "").length()) / 2 + 1 + bufferWalls * 2;
		
		int[][] WallsAndObjects  = new int[rows][cols];
		int[][] Floor            = new int[rows][cols];
		int[][] FloorDeco		 = new int[rows][cols];
		int[][] FloorDeco2		 = new int[rows][cols];
		int[][] WallDeco		 = new int[rows][cols];
		int[][] Lodestones       = new int[rows + 2][cols + 2];
		int[][] LodestoneChecked = new int[rows][cols];
		int[][] Collision	     = new int[rows][cols];
		int[][] Ambience 		 = new int[rows][cols];
		
		// Determines if the collision layer should be created
		Boolean hasCollision = false;
		
		// Extra 2 rows and cols as required by the wall scanning algorithm
		data = new String[rows + 2][cols + 2];
		
		// Initialize all the layers
		for (String[] row: data) 			Arrays.fill(row, "█");
		for (int[] row: Floor) 				Arrays.fill(row, -1);
		for (int[] row: FloorDeco) 			Arrays.fill(row, -1);
		for (int[] row: FloorDeco2) 		Arrays.fill(row, -1);
		for (int[] row: WallDeco) 			Arrays.fill(row, -1);
		for (int[] row: WallsAndObjects) 	Arrays.fill(row, -1);
		
		// To check for which lodestone to place
		for (int[] row: Lodestones) 		Arrays.fill(row, 0);
		for (int[] row: LodestoneChecked) 	Arrays.fill(row, 0);
		
		// Sometimes the placement for lodestones collide
		for (int[] row: Collision) 			Arrays.fill(row, -1);
		
		// Ambience layer. Functions like some sort of static shader
		for (int[] row: Ambience) 			Arrays.fill(row, -1);
		
		level = level.replace("\r\n", "");
		
		// Transferring the level into a grid form (2D array)
		for (int i = 0; i < level.length(); i++) {
			data[bufferWalls + i / (cols - bufferWalls * 2)][bufferWalls + i % (cols - bufferWalls * 2)] = Character.toString(level.charAt(i));
		}
		
		// Create the level in the stated directory
		File file = new File(dir + name + ".tmx");
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		
		String[] lodestoneSymbols = new String[] {"x", "y", "z", "X", "Y", "Z"};
		
		// Adding trees as deco
		if (plantTrees) {
			for (int i = 1; i < rows; i++) {
				for (int j = 1; j < cols; j++) {
					if ("█T".contains(data[i][j])) {
						// "█T" means all walls and big trees are counted as proper neighbours.
						String wallNeighbours = getNeighbours(i, j, "█T", data, false);
						if ((Math.random() < 0.1) && compareWithDontCares(wallNeighbours, "11111X1X")) {
							data[i + 1][j] = "T";
						}
					}
				}
			}
		}
		
		
		/*//Print out level in text form
		for (int i = 1; i < rows + 1; i++) {
			for (int j = 1; j < cols + 1; j++) {
				System.out.print(data[i][j]);
			} System.out.println();
		}
		System.out.println();
		*/
		
		
		// Processing walls and floors and other objects
		for (int i = 1; i < rows + 1; i++) {
			for (int j = 1; j < cols + 1; j++) {
				
				// Detects a wall
				if (data[i][j].equals("█")) {
					
					// Refer the the function for details
					String wallNeighbours = getNeighbours(i, j, "█", data, false);
					
					// All the possible 8 neighbours of a tile. Refer to getNeighbours function for explanation of 
					// the purpose of 0, 1 and X
					for (String code: new String[] {
							"01011010", "01011011", "01011110", "01011111", "01011X0X", 
							"01111010", "01111011", "01111110", "01111111", "01111X0X", 
							"01X1001X", "01X1011X", "01X10X0X", "11011010", "11011011", 
							"11011110", "11011111", "11011X0X", "11111010", "11111011", 
							"11111110", "11111111", "11111X0X", "11X1001X", "11X1011X", 
							"11X10X0X", "X0X00X0X", "X0X00X1X", "X0X01X0X", "X0X01X10", 
							"X0X01X11", "X0X1001X", "X0X1011X", "X0X10X0X", "X0X11010", 
							"X0X11011", "X0X11110", "X0X11111", "X0X11X0X", "X1001X0X", 
							"X1001X10", "X1001X11", "X1101X0X", "X1101X10", "X1101X11", 
							"X1X00X0X", "X1X00X1X"}) {
						
						// Refer the the function for details
						if (compareWithDontCares(wallNeighbours, code)) {
							WallsAndObjects[i - 1][j - 1] = nameToID.get("Set " + whichWallSet + " Wall " + code);
						}
					}
					Ambience[i - 1][j - 1] = nameToID.get("Darker");
				
				// Not a wall
				} else {
					
					//if (!"Mm".contains(data[i][j])) 		Ambience[i - 1][j - 1] = nameToID.get("Darker");
					
					// Only for underground that you need to darken all the tiles to fit the ambience.
					Ambience[i - 1][j - 1] = nameToID.get("Darker");
					
					// Button and Contraption Set 1
					if 		(data[i][j].equals("b")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Button 1 State 1");
					else if (data[i][j].equals("B")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Contraption 1 NSEW State 1");
					
					// Button and Contraption Set 2
					else if (data[i][j].equals("f")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Button 2 State 1");
					else if (data[i][j].equals("F")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Contraption 2 NSEW State 1");
					
					// Randomly placing either North or South magnets first, since repulsion is not done.
					else if (data[i][j].equals("M")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Magnet " + (Math.random() < 0.5 ? "North" : "South") + " State 1");
					
					// Magnetic Floor. A tile that does passive magnetization. Does not pull or push any block that is not on top of it,
					else if (data[i][j].equals("m")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Magnetic Floor State 1");
					
					// A multi-tile tree that acts as a wall.
					else if (data[i][j].equals("T")) {
						if (WallsAndObjects[i - 1][j - 2] == -1) {
							WallsAndObjects[i - 1][j - 2] = nameToID.get("Big Tree 111111X1X");
						} else {
							hasCollision = true;
							Collision[i - 1][j - 2] = nameToID.get("Big Tree 111111X1X");
						}
						
					// Exits
					} else if (data[i][j].equals("e")) {
						if 		(whichWallSet == 1) 	WallsAndObjects[i - 1][j - 1] = nameToID.get("Exit Standard Front State 1");
						else if (whichWallSet == 2) 	WallsAndObjects[i - 1][j - 1] = nameToID.get("Exit Cave " + (Math.random() < 0.5 ? "Forwards" : "Down") + " Front State 1");
						numExits++;
					
					// The starting point / the player
					} else if (data[i][j].equals("s")) {
						WallsAndObjects[i - 1][j - 1] = nameToID.get("Player");
						numPlayers++;
						
					// Lodestones. Note that Lodestones array does not contain the data to be written into the tmx file
					// Instead, it serves as a mechanism to determine the individual multi-tile lodestones in the later part.
					} else if (Arrays.asList(lodestoneSymbols).contains(data[i][j])) {
						Lodestones[i][j] = data[i][j].charAt(0);
					}
					
					/*// Floor Set 2 only
					if ("fFBbMxyzXYZe".contains(data[i][j])) {
						Floor[i - 1][j - 1] = 1;
					} else {
						Floor[i - 1][j - 1] = (int) (16 * Math.pow(Math.random(), 0.75));
					}*/
					
					// Refer the the function for details
					String floorNeighbours = getNeighbours(i, j, "█", data, true);
					
					// All the possible 8 neighbours of a tile. Refer to getNeighbours function for explanation of 
					// the purpose of 0, 1 and X
					for (String code: new String[] {
							"01011010", "01011011", "01011110", "01011111", "01011X0X", 
							"01111010", "01111011", "01111110", "01111111", "01111X0X", 
							"01X1001X", "01X1011X", "01X10X0X", "11011010", "11011011", 
							"11011110", "11011111", "11011X0X", "11111010", "11111011", 
							"11111110", "11111111", "11111X0X", "11X1001X", "11X1011X", 
							"11X10X0X", "X0X00X0X", "X0X00X1X", "X0X01X0X", "X0X01X10", 
							"X0X01X11", "X0X1001X", "X0X1011X", "X0X10X0X", "X0X11010", 
							"X0X11011", "X0X11110", "X0X11111", "X0X11X0X", "X1001X0X", 
							"X1001X10", "X1001X11", "X1101X0X", "X1101X10", "X1101X11", 
							"X1X00X0X", "X1X00X1X"}) {
						
						// Refer the the function for details
						if (compareWithDontCares(floorNeighbours, code)) {
							Floor[i - 1][j - 1] = nameToID.get("Set 1 Floor " + code);
						}
					}
					
					// Purely decorational. Decorations should not spawn on top of buttons, contraptions, nor exits
					if (Math.random() < 0.35 && !"bBfFe".contains(data[i][j])) {
						if (Math.random() < 0.7) 		FloorDeco[i - 1][j - 1] = nameToID.get("Debris " + (random.nextInt(8) + 1));
						else if (Math.random() < 0.25)  FloorDeco[i - 1][j - 1] = nameToID.get("Depression " + (random.nextInt(8) + 1));
						else if (whichWallSet == 1) 	FloorDeco[i - 1][j - 1] = nameToID.get("Grey Scratches " + (random.nextInt(8) + 1));
						else if (whichWallSet == 2) 	FloorDeco[i - 1][j - 1] = nameToID.get("Blue Scratches " + (random.nextInt(4) + 1));
					}
					
					// Just wanna see if having 2 layers of decorations will work out
					if (Math.random() < 0.2 && !"bBfFe".contains(data[i][j])) {
						if (Math.random() < 0.3) 		FloorDeco2[i - 1][j - 1] = nameToID.get("Debris " + (random.nextInt(8) + 1));
						else if (Math.random() < 0.75)  FloorDeco2[i - 1][j - 1] = nameToID.get("Depression " + (random.nextInt(8) + 1));
						else if (whichWallSet == 1) 	FloorDeco2[i - 1][j - 1] = nameToID.get("Grey Scratches " + (random.nextInt(8) + 1));
						else if (whichWallSet == 2) 	FloorDeco2[i - 1][j - 1] = nameToID.get("Blue Scratches " + (random.nextInt(4) + 1));
					}
					
					/*
					if (data[i - 1][j].equals("█") && data[i][j + 1].equals("█")) {
						FloorDeco[i - 1][j - 1] = nameToID.get("Small Corner 1");
					} else if (data[i - 1][j].equals("█") && data[i][j - 1].equals("█")) {
						FloorDeco[i - 1][j - 1] = nameToID.get("Small Corner 2");
					}*/
				}
			}
		}
		
		// Since this program processes all levels in a batch, incorrectly designed levels missing or having too many players
		// or exits or both will be noted down here. 
		String errorMSG = "";
		if (numPlayers == 0) 		errorMSG += "No players present. ";
		if (numPlayers > 1) 		errorMSG += "Multiple players are detected. ";
		if (numExits == 0) 			errorMSG += "No exits detected. ";
		if (numExits > 1) 			errorMSG += "Multiple exits detected. ";
		if (!errorMSG.equals("")) 	System.out.println(errorMSG += dir.split("Levels/")[1] + "[" + name + "].tmx");
		
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
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				
				// Because a lodestone can span multiple tiles, and we do not want to risk multiple checks on all parts of the lodestone.
				// An incorrect lodestone will be output if we let it do so. 
				// Hence there is another array to check if a tile is checked.
				if (LodestoneChecked[i][j] == 0) {		// Unchecked
					
					// Value is important. Remember when i said that we are using multiple characters to represent lodestones.
					int value = Lodestones[i][j];
					if (value != 0) {					// Contains a portion of a lodestone
						
						/*  // This might make the program run slower, so I stopped progress
						for (String code: new String[] {
								"0XXX0XXXXXXX - 1 1",      "0XX010XXX0XX - 1 11",     "0XX010XX010X - 1 111",    "10XX00XXXXXX - 2 11",     "10X010XXX0XX - 2 1110", 
								"10XX010XXX0X - 2 1101",   "0X0110XX00XX - 2 0111",   "0XX0110XX00X - 2 1011",   "10X0110XX00X - 2 1111",   "0XX0110X010X - 2 101110", 
								"0X0110XX010X - 2 011101", "0XX010XX0110 - 2 101011", "10X010XX010X - 2 111010", "0XX010X0110X - 2 010111", "10XX010XX010 - 2 110101", 
								"0XX0110XX010 - 2 101101", "0X0110X010XX - 2 011110", "10X010XX0110 - 2 111011", "10XX010X0110 - 2 110111", "10X0110X010X - 2 111110", 
								"10X0110XX010 - 2 111101", "0XX0110X0110 - 2 101111", "0X0110X0110X - 2 011111", "11XX000XXXXX - 3 111",    "0X01110X000X - 3 010111", 
								"11XX010XXX0X - 3 111010", "11X0100XX0XX - 3 111100", "11XX001XXXX0 - 3 111001", "00X0111XX000 - 3 100111", "0X1110X000XX - 3 001111", 
								"10XX011XXX00 - 3 110011", "100110XX00XX - 3 011110", "11X0101XX0X0 - 3 111101", "01X0111XX000 - 3 101111", "11X0110XX00X - 3 111110", 
								"11XX011XXX00 - 3 111011", "10X0111XX000 - 3 110111", "1001110X000X - 3 011111" }) {
							
							String pattern = code.split(" - ")[0];
						}
						 */
						
						// This diagram below shows the logic behind the checking of the neighbours to determine which lodestone is it
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
						
						// This string array is a representation of all the neighbours *. 
						String[] b = new String[] {"" + (Lodestones[i][j + 1] == value ? 1 : 0),
												   "" + (Lodestones[i][j + 2] == value ? 1 : 0),
												   "" + (Lodestones[i + 1][j - 2] == value ? 1 : 0),
												   "" + (Lodestones[i + 1][j - 1] == value ? 1 : 0),
												   "" + (Lodestones[i + 1][j] == value ? 1 : 0),
												   "" + (Lodestones[i + 1][j + 1] == value ? 1 : 0),
												   "" + (Lodestones[i + 1][j + 2] == value ? 1 : 0),
												   "" + (Lodestones[i + 2][j - 2] == value ? 1 : 0),
												   "" + (Lodestones[i + 2][j - 1] == value ? 1 : 0),
												   "" + (Lodestones[i + 2][j] == value ? 1 : 0),
												   "" + (Lodestones[i + 2][j + 1] == value ? 1 : 0),
												   "" + (Lodestones[i + 2][j + 2] == value ? 1 : 0)};
						
						// 91 is one of the numbers that divides the upper and lower case alphabets in the ascii code table.
						// Uppercase = Unpushable. Lowercase = Pushable
						if ((b[0] + b[4]).equals("00")) {
							WallsAndObjects[i - 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unpushable) 1x1 1" : "Pushable) 1x1 " + (random.nextInt(4) + 1)));
							
						} else if ((b[0] + b[3] + b[4] + b[5] + b[9]).equals("00100")) {
							WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 1x2 1");
							LodestoneChecked[i + 1][j] = 1;
						
						} else if ((b[0] + b[3] + b[4] + b[5] + b[8] + b[9] + b[10]).equals("0010010")) {
							WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 1x3 1");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 2][j] = 1;
						
						} else if ((b[0] + b[1] + b[4] + b[5]).equals("1000")) {
							WallsAndObjects[i - 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x1 1");
							LodestoneChecked[i][j + 1] = 1;
						
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[9]).equals("100100")) {
							WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x2 1");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i][j + 1] = 1;
						
						} else if ((b[0] + b[1] + b[4] + b[5] + b[6] + b[10]).equals("100100")) {
							if (WallsAndObjects[i][j - 1] == -1) {
								WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x2 2");
							} else {
								hasCollision = true;
								Collision[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x2 2");
							}
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							
						} else if ((b[0] + b[2] + b[3] + b[4] + b[5] + b[8] + b[9]).equals("0011000")) {
							WallsAndObjects[i][j - 2] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x2 3");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j - 1] = 1;
						
						} else if ((b[0] + b[3] + b[4] + b[5] + b[6] + b[9] + b[10]).equals("0011000")) {
							WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x2 4");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
						
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[6] + b[9] + b[10]).equals("10011000")) {
							WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x2 5");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
						
						} else if ((b[0] + b[3] + b[4] + b[5] + b[6] + b[8] + b[9] + b[10]).equals("00110010")) {
							WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 1");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 2][j] = 1;
						
						} else if ((b[0] + b[2] + b[3] + b[4] + b[5] + b[8] + b[9] + b[10]).equals("00110010")) {
							if (WallsAndObjects[i + 1][j - 2] == -1) {
								WallsAndObjects[i + 1][j - 2] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 2");
							} else {
								hasCollision = true;
								Collision[i + 1][j - 2] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 2");
							}
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j - 1] = 1;
							LodestoneChecked[i + 2][j] = 1;
							
						} else if ((b[0] + b[3] + b[4] + b[5] + b[8] + b[9] + b[10] + b[11]).equals("00100110")) {
							WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 3");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 2][j] = 1;
							LodestoneChecked[i + 2][j + 1] = 1;
						
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[8] + b[9] + b[10]).equals("10010010")) {
							WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 4");
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 2][j] = 1;
							
						} else if ((b[0] + b[3] + b[4] + b[5] + b[7] + b[8] + b[9] + b[10]).equals("00100110")) {
							WallsAndObjects[i + 1][j - 2] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 5");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 2][j] = 1;
							LodestoneChecked[i + 2][j - 1] = 1;
						
						} else if ((b[0] + b[1] + b[4] + b[5] + b[6] + b[9] + b[10] + b[11]).equals("10010010")) {
							if (WallsAndObjects[i + 1][j - 1] == -1) {
								WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 6");
							} else {
								hasCollision = true;
								Collision[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 6");
							}
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 2][j + 1] = 1;
							
						} else if ((b[0] + b[3] + b[4] + b[5] + b[6] + b[9] + b[10] + b[11]).equals("00110010")) {
							if (WallsAndObjects[i + 1][j - 1] != -1) {
								WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 7");
							} else {
								hasCollision = true;
								Collision[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 7");
							}
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 2][j + 1] = 1;
							
						} else if ((b[0] + b[2] + b[3] + b[4] + b[5] + b[7] + b[8] + b[9]).equals("00110010")) {
							WallsAndObjects[i + 1][j - 2] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 8");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j - 1] = 1;
							LodestoneChecked[i + 2][j - 1] = 1;
						
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[8] + b[9] + b[10] + b[11]).equals("100100110")) {
							WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 9");
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 2][j] = 1;
							LodestoneChecked[i + 2][j + 1] = 1;
						
						} else if ((b[0] + b[1] + b[4] + b[5] + b[6] + b[8] + b[9] + b[10] + b[11]).equals("100100110")) {
							WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 10");
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 2][j + 1] = 1;
							LodestoneChecked[i + 2][j] = 1;
						
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[6] + b[8] + b[9] + b[10]).equals("100110010")) {
							WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 11");
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 2][j] = 1;
						
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[6] + b[9] + b[10] + b[11]).equals("100110010")) {
							if (WallsAndObjects[i + 1][j - 1] == -1) {
								WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 12");
							} else {
								hasCollision = true;
								Collision[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 12");
							}
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 2][j + 1] = 1;
							
						} else if ((b[0] + b[3] + b[4] + b[5] + b[6] + b[8] + b[9] + b[10] + b[11]).equals("001100110")) {
							WallsAndObjects[i + 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 13");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 2][j] = 1;
							LodestoneChecked[i + 2][j + 1] = 1;
						
						} else if ((b[0] + b[2] + b[3] + b[4] + b[5] + b[7] + b[8] + b[9] + b[10]).equals("001100110")) {
							WallsAndObjects[i + 1][j - 2] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 2x3 14");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j - 1] = 1;
							LodestoneChecked[i + 2][j] = 1;
							LodestoneChecked[i + 2][j - 1] = 1;
						
						} else if ((b[0] + b[1] + b[4] + b[5] + b[6]).equals("11000")) {
							WallsAndObjects[i - 1][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x1 1");
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i][j + 2] = 1;
						
						} else if ((b[0] + b[2] + b[3] + b[4] + b[5] + b[6] + b[8] + b[9] + b[10]).equals("001110000")) {
							WallsAndObjects[i][j - 2] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 1");
							LodestoneChecked[i + 1][j - 1] = 1;
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
						
						} else if ((b[0] + b[1] + b[4] + b[5] + b[6] + b[10]).equals("110100")) {
							if (WallsAndObjects[i][j - 1] == -1) {
								WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 2");
							} else {
								hasCollision = true;
								Collision[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 2");
							}
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i][j + 2] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[6] + b[9]).equals("1101000")) {
							WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 3");
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i][j + 2] = 1;
							LodestoneChecked[i + 1][j] = 1;
						
						} else if ((b[0] + b[1] + b[4] + b[5] + b[6] + b[11]).equals("110010")) {
							if (WallsAndObjects[i][j - 1] == -1) {
								WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 4");
							} else {
								hasCollision = true;
								Collision[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 4");
							}
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i][j + 2] = 1;
							LodestoneChecked[i + 1][j + 2] = 1;
							
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[6] + b[9] + b[10] + b[11]).equals("000111000")) {
							WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 5");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 1][j + 2] = 1;
						
						} else if ((b[0] + b[2] + b[3] + b[4] + b[5] + b[7] + b[8] + b[9]).equals("01110000")) {
							WallsAndObjects[i][j - 3] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 6");
							LodestoneChecked[i + 1][j - 2] = 1;
							LodestoneChecked[i + 1][j - 1] = 1;
							LodestoneChecked[i + 1][j] = 1;
						
						} else if ((b[0] + b[1] + b[4] + b[5] + b[6] + b[10] + b[11]).equals("1001100")) {
							if (WallsAndObjects[i][j - 1] == -1) {
								WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 7");
							} else {
								hasCollision = true;
								Collision[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 7");
							}
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 1][j + 2] = 1;
							
						} else if ((b[0] + b[1] + b[2] + b[3] + b[4] + b[5] + b[8] + b[9]).equals("10011000")) {
							WallsAndObjects[i][j - 2] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 8");
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j - 1] = 1;
						
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[6] + b[9] + b[11]).equals("11010100")) {
							WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 9");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i][j + 2] = 1;
							LodestoneChecked[i + 1][j + 2] = 1;
						
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[6] + b[9] + b[10] + b[11]).equals("010111000")) {
							WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 10");
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 1][j + 2] = 1;
							LodestoneChecked[i][j + 2] = 1;
						
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[6] + b[9] + b[10]).equals("11011000")) {
							WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 11");
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i][j + 2] = 1;
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
						
						} else if ((b[0] + b[1] + b[4] + b[5] + b[6] + b[10] + b[11]).equals("1101100")) {
							if (WallsAndObjects[i][j - 1] == -1) {
								WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 12");
							} else {
								hasCollision = true;
								Collision[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 12");
							}
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i][j + 2] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 1][j + 2] = 1;
							
						} else if ((b[0] + b[1] + b[3] + b[4] + b[5] + b[6] + b[9] + b[10] + b[11]).equals("100111000")) {
							WallsAndObjects[i][j - 1] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 13");
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
							LodestoneChecked[i + 1][j + 2] = 1;
						
						} else if ((b[0] + b[1] + b[2] + b[3] + b[4] + b[5] + b[6] + b[8] + b[9] + b[10]).equals("1001110000")) {
							WallsAndObjects[i][j - 2] = nameToID.get("Lodestone (" + ((value < 91) ? "Unp" : "P") + "ushable) 3x2 14");
							LodestoneChecked[i][j + 1] = 1;
							LodestoneChecked[i + 1][j - 1] = 1;
							LodestoneChecked[i + 1][j] = 1;
							LodestoneChecked[i + 1][j + 1] = 1;
						}
					} LodestoneChecked[i][j] = 1;
				}
			}
		}
		
		// Refer to function
		writeGraphics(writer, cols, rows);
		
		
		// Floor layer
		writer.println(" <layer name=\"Floor\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
					   "  <data encoding=\"csv\">");
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				writer.print((Floor[i][j] + 1));
				if (i != rows - 1 || j != cols - 1) writer.print(",");
			} writer.println();
		} writer.println("</data>\r\n" + 
					     " </layer>\r\n");
		
		
		// Floor decor layer
		writer.println(" <layer name=\"Floor Decorations\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
					   "  <data encoding=\"csv\">");
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				writer.print((FloorDeco[i][j] + 1));
				if (i != rows - 1 || j != cols - 1) writer.print(",");
			} writer.println();
		} writer.println("</data>\r\n" + 
					     " </layer>\r\n");
		
		
		// Floor decor layer 2
		writer.println(" <layer name=\"Floor Decorations 2\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
					   "  <data encoding=\"csv\">");
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				writer.print((FloorDeco2[i][j] + 1));
				if (i != rows - 1 || j != cols - 1) writer.print(",");
			} writer.println();
		} writer.println("</data>\r\n" + 
					     " </layer>\r\n");
		
		
		// Walls and Objects layer
		writer.println(" <layer name=\"Walls and Objects\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
					   "  <data encoding=\"csv\">");
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				writer.print((WallsAndObjects[i][j] + 1));
				if (i != rows - 1 || j != cols - 1) writer.print(",");
			} writer.println();
		} writer.println("</data>\r\n" + 
					     " </layer>");
		
		
		// Walls Deco Layer
		writer.println(" <layer name=\"Walls Deco\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
					   "  <data encoding=\"csv\">");
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				writer.print((WallDeco[i][j] + 1));
				if (i != rows - 1 || j != cols - 1) writer.print(",");
			} writer.println();
		} writer.println("</data>\r\n" + 
					     " </layer>");
		
		
		// Collision layer
		if (hasCollision) {
			writer.println(" <layer name=\"Collision\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
						   "  <data encoding=\"csv\">");
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					writer.print((Collision[i][j] + 1));
					if (i != rows - 1 || j != cols - 1) writer.print(",");
				} writer.println();
			} writer.println("</data>\r\n" + 
						     " </layer>\r\n");
		}
		
		// Ambience (Underground - Darker)
		if (whichWallSet == 2) {
			writer.println(" <layer name=\"Ambience\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
					       "  <data encoding=\"csv\">");
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					writer.print(Ambience[i][j] + 1);
					if (i != rows - 1 || j != cols - 1) writer.print(",");
				} writer.println();
			} writer.println("</data>\r\n" + 
					         " </layer>\r\n");
		}
		
		writer.println("</map>");
		writer.flush();
		writer.close();
	}
	
	/**
	 * Returns the 8 neighbours of a block
	 * 
	 *  123
	 *  4 5
	 *  678
	 */
	private String getNeighbours(int i, int j, String block, String[][] arr, boolean flip) {
		int trueVal = flip ? 0 : 1;
		int falseVal = 1 - trueVal;
		return  (block.contains(arr[i - 1][j - 1]) ? trueVal : falseVal) + "" + 
				(block.contains(arr[i - 1][j    ]) ? trueVal : falseVal) +
				(block.contains(arr[i - 1][j + 1]) ? trueVal : falseVal) +
				(block.contains(arr[i    ][j - 1]) ? trueVal : falseVal) +
				(block.contains(arr[i    ][j + 1]) ? trueVal : falseVal) +
				(block.contains(arr[i + 1][j - 1]) ? trueVal : falseVal) +
				(block.contains(arr[i + 1][j    ]) ? trueVal : falseVal) +
				(block.contains(arr[i + 1][j + 1]) ? trueVal : falseVal);
	}
	
	/**
	 * Uses the concept of dontcares from circuit logic to compare binary strings
	 * @param input
	 * @param condition
	 * @return
	 */
	private boolean compareWithDontCares(String input, String condition) {
		if (input.length() != condition.length()) throw new IllegalArgumentException("Strings does not match in length");
		for (int i = 0; i < condition.length(); i++) {
			if 	    (condition.charAt(i) == 88 || input.charAt(i) == 88) 	continue;
			else if (condition.charAt(i) != input.charAt(i)) 				return false;
		} return true;
	}
	
	public static void main(String[] args) throws IOException {
		TextToTmx prog = new TextToTmx();
		for (String pack: new String[] { "Easy Levels Pack", "Medium Levels Pack", "Hard Levels Pack", "Weird Levels Pack"}) {
			String content = new String(Files.readAllBytes(Paths.get("C:/Users/ckjr/Desktop/Magnets/android/assets/Levels/" + pack + ".txt")));
			for (String levelcode: content.split("\\r\\n\\r\\n")) {
				prog.setLevel(levelcode);
				prog.convert("C:/Users/ckjr/Desktop/Magnets/android/assets/Levels/" + pack + "/");
			}
		}
	}
}
