
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
	
	TextToTmx() {
		initiateTable();
	}
	
	private void setLevel(String level) {
		this.level = level;
	}
	
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
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01011010.png",                    "Set 1 Floor 01011010",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01011011.png",                    "Set 1 Floor 01011011",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01011110.png",                    "Set 1 Floor 01011110",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01011111.png",                    "Set 1 Floor 01011111",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01011X0X.png",                    "Set 1 Floor 01011X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01111010.png",                    "Set 1 Floor 01111010",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01111011.png",                    "Set 1 Floor 01111011",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01111110.png",                    "Set 1 Floor 01111110",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01111111.png",                    "Set 1 Floor 01111111",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01111X0X.png",                    "Set 1 Floor 01111X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01X1001X.png",                    "Set 1 Floor 01X1001X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01X1011X.png",                    "Set 1 Floor 01X1011X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 01X10X0X.png",                    "Set 1 Floor 01X10X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11011010.png",                    "Set 1 Floor 11011010",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11011011.png",                    "Set 1 Floor 11011011",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11011110.png",                    "Set 1 Floor 11011110",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11011111.png",                    "Set 1 Floor 11011111",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11011X0X.png",                    "Set 1 Floor 11011X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11111010.png",                    "Set 1 Floor 11111010",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11111011.png",                    "Set 1 Floor 11111011",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11111110.png",                    "Set 1 Floor 11111110",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11111111.png",                    "Set 1 Floor 11111111",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11111X0X.png",                    "Set 1 Floor 11111X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11X1001X.png",                    "Set 1 Floor 11X1001X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11X1011X.png",                    "Set 1 Floor 11X1011X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor 11X10X0X.png",                    "Set 1 Floor 11X10X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X00X0X.png",                    "Set 1 Floor X0X00X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X00X1X.png",                    "Set 1 Floor X0X00X1X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X01X0X.png",                    "Set 1 Floor X0X01X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X01X10.png",                    "Set 1 Floor X0X01X10",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X01X11.png",                    "Set 1 Floor X0X01X11",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X1001X.png",                    "Set 1 Floor X0X1001X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X1011X.png",                    "Set 1 Floor X0X1011X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X10X0X.png",                    "Set 1 Floor X0X10X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X11010.png",                    "Set 1 Floor X0X11010",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X11011.png",                    "Set 1 Floor X0X11011",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X11110.png",                    "Set 1 Floor X0X11110",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X11111.png",                    "Set 1 Floor X0X11111",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X0X11X0X.png",                    "Set 1 Floor X0X11X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X1001X0X.png",                    "Set 1 Floor X1001X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X1001X10.png",                    "Set 1 Floor X1001X10",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X1001X11.png",                    "Set 1 Floor X1001X11",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X1101X0X.png",                    "Set 1 Floor X1101X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X1101X10.png",                    "Set 1 Floor X1101X10",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X1101X11.png",                    "Set 1 Floor X1101X11",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X1X00X0X.png",                    "Set 1 Floor X1X00X0X",                new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Set 1 (Flat)/Floor X1X00X1X.png",                    "Set 1 Floor X1X00X1X",                new int[] {32, 32}),
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
				new Block ("../../Graphics/Floor/Overlays/Scratches 1.png",                           "Scratches 1",                         new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Scratches 2.png",                           "Scratches 2",                         new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Scratches 3.png",                           "Scratches 3",                         new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Scratches 4.png",                           "Scratches 4",                         new int[] {32, 32}),
				new Block ("../../Graphics/Floor/Overlays/Small Corner 1.png",                        "Small Corner 1",                      new int[] {32, 48}),
				new Block ("../../Graphics/Floor/Overlays/Small Corner 2.png",                        "Small Corner 2",                      new int[] {32, 48}),
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
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall Decoration 1.png",             "Wall Decoration 1",                   new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall Decoration 2.png",             "Wall Decoration 2",                   new int[] {32, 48}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall Decoration 3.png",             "Wall Decoration 3",                   new int[] {32, 64}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall Decoration 4.png",             "Wall Decoration 4",                   new int[] {32, 64}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall Decoration 5.png",             "Wall Decoration 5",                   new int[] {32, 64}),
				new Block ("../../Graphics/Walls/Set 1 (Standard)/Wall Decoration 6.png",             "Wall Decoration 6",                   new int[] {32, 64}),
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
				new Block ("../../Graphics/Objects/Door 1 State 1.png",                               "Door 1 State 1",                      new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Door 1 State 2.png",                               "Door 1 State 2",                      new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Door 1 State 3.png",                               "Door 1 State 3",                      new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Door 1 State 4.png",                               "Door 1 State 4",                      new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Door 1 State 5.png",                               "Door 1 State 5",                      new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Button 2 State 1.png",                             "Button 2 State 1",                    new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Button 2 State 2.png",                             "Button 2 State 2",                    new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Button 2 State 3.png",                             "Button 2 State 3",                    new int[] {32, 32}),
				new Block ("../../Graphics/Objects/Door 2 State 1.png",                               "Door 2 State 1",                      new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Door 2 State 2.png",                               "Door 2 State 2",                      new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Door 2 State 3.png",                               "Door 2 State 3",                      new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Door 2 State 4.png",                               "Door 2 State 4",                      new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Door 2 State 5.png",                               "Door 2 State 5",                      new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Front State 1.png",                           "Exit Front State 1",                  new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Front State 2.png",                           "Exit Front State 2",                  new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Left State 1.png",                            "Exit Left State 1",                   new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Left State 2.png",                            "Exit Left State 2",                   new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Right State 1.png",                           "Exit Right State 1",                  new int[] {32, 48}),
				new Block ("../../Graphics/Objects/Exit Right State 2.png",                           "Exit Right State 2",                  new int[] {32, 48}),
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
				new Block ("../../Graphics/Objects/Magnetic Floor.png",                               "Magnetic Floor",                      new int[] {32, 32}),
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
				new Block ("../../Graphics/Objects/Overlay/Magnetic Overlay Lodestone 3x2 14.png",    "Magnetic Overlay Lodestone 3x2 14",   new int[] {96, 80})
		};
		
		for (int i = 0; i < Blocks.length; i++) {
			nameToID.put(Blocks[i].name, i);
		}
	}
	
	public void convert(String dir) throws IOException {
		
		int numPlayers = 0;		// Check validity of the level
		int numExits = 0;		// Check validity of the level
		
		// All the level information gotten from the data provided
		String name = level.substring(0, level.indexOf("\r\n"));
		level = level.substring(level.indexOf("\r\n") + 2);
		int cols = level.indexOf("\r\n");
		int rows = (level.length() - level.replace("\r\n", "").length()) / 2 + 1;
		
		int[][] WallsAndObjects  = new int[rows][cols];
		int[][] Floor            = new int[rows][cols];
		int[][] FloorDeco		 = new int[rows][cols];
		int[][] WallDeco		 = new int[rows][cols];
		int[][] Lodestones       = new int[rows + 2][cols + 2];
		int[][] LodestoneChecked = new int[rows][cols];
		int[][] Collision	     = new int[rows][cols];
		
		// Determines if the collision layer should be created
		Boolean hasCollision = false;
		
		// Extra 2 rows and cols as required by the wall scanning algorithm
		data = new String[rows + 2][cols + 2];
		
		// Initialize all the layers
		for (String[] row: data) Arrays.fill(row, " ");
		for (int[] row: Floor) Arrays.fill(row, -1);
		for (int[] row: FloorDeco) Arrays.fill(row, -1);
		for (int[] row: WallDeco) Arrays.fill(row, -1);
		for (int[] row: WallsAndObjects) Arrays.fill(row, -1);
		
		// To check for which lodestone to place
		for (int[] row: Lodestones) Arrays.fill(row, 0);
		for (int[] row: LodestoneChecked) Arrays.fill(row, 0);
		
		// Sometimes the placement for lodestones collide
		for (int[] row: Collision) Arrays.fill(row, -1);
		
		level = level.replace("\r\n", "");
		
		// Transferring the level into a grid form (2D array)
		for (int i = 0; i < level.length(); i++) {
			data[1 + i / cols][1 + i % cols] = Character.toString(level.charAt(i));
		}
		
		// Create the level in the stated directory
		File file = new File(dir + name + ".tmx");
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		
		String[] lodestoneSymbols = new String[] {"x", "y", "z", "X", "Y", "Z"};
		
		for (int i = 1; i < rows + 1; i++) {
			for (int j = 1; j < cols + 1; j++) {
				if (data[i][j].equals("█")) {
					String wallNeighbours = getNeighbours(i, j, "█");
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
						if (compareWithDontCares(wallNeighbours, code)) {
							WallsAndObjects[i - 1][j - 1] = nameToID.get("Set 1 Wall " + code);
						}
					}
					
					if (Math.random() < 0.1 && data[i - 1][j].equals("█")) {
						if      (data[i + 1][j].equals("█")) 	WallDeco[i - 1][j - 1] = nameToID.get("Wall Decoration 1");
						else if (data[i + 1][j].equals(" ")) 	WallDeco[i - 1][j - 1] = nameToID.get("Wall Decoration 2");
					}
					
				} else {
					
					if 		(data[i][j].equals("b")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Button 1 State 1");
					else if (data[i][j].equals("B")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Door 1 State 1");
					else if (data[i][j].equals("F")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Door 2 State 1");
					else if (data[i][j].equals("f")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Button 2 State 1");
					else if (data[i][j].equals("M")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Magnet " + (Math.random() < 0.5 ? "North" : "South") + " State 1");
					else if (data[i][j].equals("m")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Magnetic Floor");
					else if (data[i][j].equals("e")) {
						WallsAndObjects[i - 1][j - 1] = nameToID.get("Exit Front State 1");
						numExits++;
					} else if (data[i][j].equals("s")) {
						WallsAndObjects[i - 1][j - 1] = nameToID.get("Player");
						numPlayers++;
					} else if (Arrays.asList(lodestoneSymbols).contains(data[i][j])) {
						Lodestones[i][j] = data[i][j].charAt(0);
					}
					
					/*// Floor Set 2 only
					if ("fFBbMxyzXYZe".contains(data[i][j])) {
						Floor[i - 1][j - 1] = 1;
					} else {
						Floor[i - 1][j - 1] = (int) (16 * Math.pow(Math.random(), 0.75));
					}*/
					
					String floorNeighbours = "" + (data[i - 1][j - 1].equals("█") ? 0 : 1) + 
												  (data[i - 1][j].equals("█") ? 0 : 1) + 
												  (data[i - 1][j + 1].equals("█") ? 0 : 1) + 
												  (data[i][j - 1].equals("█") ? 0 : 1) + 
												  (data[i][j + 1].equals("█") ? 0 : 1) + 
												  (data[i + 1][j - 1].equals("█") ? 0 : 1) + 
												  (data[i + 1][j].equals("█") ? 0 : 1) + 
												  (data[i + 1][j + 1].equals("█") ? 0 : 1);
					
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
						if (compareWithDontCares(floorNeighbours, code)) {
							Floor[i - 1][j - 1] = nameToID.get("Set 1 Floor " + code);
						}
					}
					
					if (Math.random() < 0.3) {
						if (Math.random() < 0.7) {
							FloorDeco[i - 1][j - 1] = nameToID.get("Debris " + (random.nextInt(8) + 1));
						} else {
							FloorDeco[i - 1][j - 1] = nameToID.get("Scratches " + (random.nextInt(4) + 1));
						}
					}
					
					if (data[i - 1][j].equals("█") && data[i][j + 1].equals("█")) {
						FloorDeco[i - 1][j - 1] = nameToID.get("Small Corner 1");
					} else if (data[i - 1][j].equals("█") && data[i][j - 1].equals("█")) {
						FloorDeco[i - 1][j - 1] = nameToID.get("Small Corner 2");
					}
				}
			}
		}
		
		String errorMSG = "";
		if (numPlayers == 0) errorMSG += "No players present. ";
		if (numPlayers > 1) errorMSG += "Multiple players are detected. ";
		if (numExits == 0) errorMSG += "No exits detected. ";
		if (numExits > 1) errorMSG += "Multiple exits detected. ";
		if (!errorMSG.equals("")) System.out.println(errorMSG += dir.split("Levels/")[1] + "[" + name + "].tmx");
		
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (LodestoneChecked[i][j] == 0) {		// Unchecked
					int value = Lodestones[i][j];
					if (value != 0) {		// Contains a portion of a lodestone
						
						for (String code: new String[] {
								"0XXX0XXXXXXX", "0XX010XXX0XX", "0XX010XX010X", "10XX00XXXXXX", "10X010XXX0XX", 
								"10XX010XXX0X", "0X0110XX00XX", "0XX0110XX00X", "10X0110XX00X", "0XX0110X010X", 
								"0X0110XX010X", "0XX010XX0110", "10X010XX010X", "0XX010X0110X", "10XX010XX010", 
								"0XX0110XX010", "0X0110X010XX", "10X010XX0110", "10XX010X0110", "10X0110X010X", 
								"10X0110XX010", "0XX0110X0110", "0X0110X0110X", "11XX000XXXXX", "0X01110X000X", 
								"11XX010XXX0X", "11X0100XX0XX", "11XX001XXXX0", "00X0111XX000", "0X1110X000XX", 
								"10XX011XXX00", "100110XX00XX", "11X0101XX0X0", "01X0111XX000", "11X0110XX00X", 
								"11XX011XXX00", "1001110X000X" }) {
							
						}
						 
						
						//       001**
						//       *****
						//       *****
									     
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
					}
					LodestoneChecked[i][j] = 1;
				}
			}
		}
		
		writer.println( 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<map version=\"1.0\" orientation=\"orthogonal\" renderorder=\"right-down\" width=\"" + cols + "\" height=\"" + rows + "\" tilewidth=\"32\" tileheight=\"32\" nextobjectid=\"1\">\r\n" + 
				" <tileset firstgid=\"1\" name=\"Tileset\" tilewidth=\"96\" tileheight=\"112\" tilecount=\"" + Blocks.length + "\" columns=\"0\">");
		
		for (int n = 0; n < Blocks.length; n++) {

			writer.println("\n  <tile id=\"" + n + "\">");

            if (Blocks[n].name.contains("Wall")) {
                writer.println("   <properties>\n" +
                        "    <property name=\"Type\" value=\"Wall\"/>\n" +
                        "   </properties>");

            } else if (Blocks[n].name.contains("Overlay Lodestone ")) {
				writer.println("   <properties>\r\n" + 
						"    <property name=\"Frame Depth\" type=\"int\" value=\"-1\"/>\r\n" + 
						"    <property name=\"Name\" value=\"" + Blocks[n].name + "\"/>\r\n" + 
						"   </properties>");
				
			} else if (Blocks[n].name.contains("Lodestone")) {
            	
            	String[] data = Blocks[n].name.substring(Blocks[n].name.indexOf(" - ") + 3).split(" ");
            	
            	String area = data[1].substring(0, data[1].length() - 4);
            	String width = data[0];
            	Boolean pushable = Blocks[n].name.contains("Pushable");
            	
            	String write = Blocks[n].name.split("\\) ")[1];
            	if (write.contains("1x1")) write = "1x1 1";
            	
				writer.println("   <properties>\n" +
						"    <property name=\"#\" value=\"(this)\"/>\n" +
						"    <property name=\"#Magnetised\" value=\"Magnetic Overlay Lodestone " + write + "\"/>\n" +
						"    <property name=\"Body Area\" value=\"" + area + "\"/>\n" +
						"    <property name=\"Body Width\" type=\"int\" value=\"" + width + "\"/>\n" +
						"    <property name=\"IsMagnetisable\" type=\"bool\" value=\"true\"/>\n" +
						"    <property name=\"IsPushable\" type=\"bool\" value=\"" + pushable + "\"/>\n" +
						"    <property name=\"Type\" value=\"Block\"/>\n" +
						"   </properties>");

            } else if (Blocks[n].name.contains("Magnet ") && Blocks[n].name.contains("State 1")) {
				writer.println("   <properties>\n" +
						"    <property name=\"#\" value=\"(this)\"/>\n" +
						"    <property name=\"Type\" value=\"Magnetic Source\"/>\n" +
						"    <property name=\"Pole\" value=\"South\"/>\n" +
						"   </properties>");
				
            } else if (Blocks[n].name.contains("Magnetic Floor")) {
				writer.println("   <properties>\n" +
						"    <property name=\"#\" value=\"(this)\"/>\n" +
						"    <property name=\"Actor Depth\" type=\"int\" value=\"-1\"/>\n" +
						"    <property name=\"Type\" value=\"Magnetic Floor\"/>\n" +
						"   </properties>");

			} else if (Blocks[n].dir.contains("Objects/Player.png")) {
				writer.println("   <properties>\n" +
						"    <property name=\"#\" value=\"(this)\"/>\n" +
						"    <property name=\"#Walking\" value=\"(this)\"/>\n" +
						"    <property name=\"Type\" value=\"Player\"/>\n" +
						"   </properties>");
				
			} 

            writer.println("   <image width=\"" + Blocks[n].dimensions[0] + "\" height=\"" + Blocks[n].dimensions[1] + "\" source=\"" + Blocks[n].dir + "\"/>");
			
			// Gives a animaation to each of the magnetic areas. All of them will be starting at different
			// states but following the same pattern.
			
			if (Blocks[n].name.contains("Magnetic ") && Blocks[n].name.contains("State 1")) {
				writer.println(
				"   <animation>\r\n" + 
				"    <frame tileid=\"" + (n) + "\" duration=\"750\"/>\r\n" + 
				"    <frame tileid=\"" + (n + 1) + "\" duration=\"750\"/>\r\n" + 
				"    <frame tileid=\"" + (n + 2) + "\" duration=\"750\"/>\r\n" + 
				"    <frame tileid=\"" + (n + 3) + "\" duration=\"750\"/>\r\n" + 
				"    <frame tileid=\"" + (n + 4) + "\" duration=\"750\"/>\r\n" + 
				"   </animation>");

			} else if (Blocks[n].name.contains("Exit Front State 1") ||
					   Blocks[n].name.contains("Exit Left State 1")  ||
					   Blocks[n].name.contains("Exit Right State 1")) {
				writer.println(
				"   <animation>\r\n" + 
				"    <frame tileid=\"" + n + "\" duration=\"1500\"/>\r\n" +
				"    <frame tileid=\"" + (n + 1) + "\" duration=\"1500\"/>\r\n" +
				"   </animation>");
				
			} else if (Blocks[n].name.contains("Magnetized Horizontal State 1") ||
					   Blocks[n].name.contains("Magnetized Vertical State 1")) {
				writer.println(
						"   <animation>\r\n" + 
						"    <frame tileid=\"" + (n) + "\" duration=\"375\"/>\r\n" + 
						"    <frame tileid=\"" + (n + 1) + "\" duration=\"375\"/>\r\n" + 
						"    <frame tileid=\"" + (n + 2) + "\" duration=\"375\"/>\r\n" + 
						"    <frame tileid=\"" + (n + 3) + "\" duration=\"375\"/>\r\n" + 
						"    <frame tileid=\"" + (n + 4) + "\" duration=\"375\"/>\r\n" + 
						"    <frame tileid=\"" + (n + 5) + "\" duration=\"375\"/>\r\n" + 
						"    <frame tileid=\"" + (n + 6) + "\" duration=\"375\"/>\r\n" + 
						"   </animation>");
				
			}
			
			writer.println("  </tile>");
		}
		
		writer.println(" </tileset>");
		
		// Floor layer
		
		writer.println(
				" <layer name=\"Floor\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
				"  <data encoding=\"csv\">");
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				writer.print((Floor[i][j] + 1));
				if (i != rows - 1 || j != cols - 1) writer.print(",");
			}
			writer.println();
		}
		
		writer.println(
				"</data>\r\n" + 
				" </layer>\r\n");
		
		
		// Floor decor layer
		
		writer.println(
				" <layer name=\"Floor Decorations\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
				"  <data encoding=\"csv\">");
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				writer.print((FloorDeco[i][j] + 1));
				if (i != rows - 1 || j != cols - 1) writer.print(",");
			}
			writer.println();
		}
		
		writer.println(
				"</data>\r\n" + 
				" </layer>\r\n");
		
		
		// Walls and Objects layer
		
		writer.println(
				" <layer name=\"Walls and Objects\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
				"  <data encoding=\"csv\">");
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				writer.print((WallsAndObjects[i][j] + 1));
				if (i != rows - 1 || j != cols - 1) writer.print(",");
			}
			writer.println();
		}
		
		writer.println(
				"</data>\r\n" + 
				" </layer>");
		
		// Walls Deco Layer
		
		writer.println(
				" <layer name=\"Walls Deco\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
				"  <data encoding=\"csv\">");
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				writer.print((WallDeco[i][j] + 1));
				if (i != rows - 1 || j != cols - 1) writer.print(",");
			}
			writer.println();
		}
		
		writer.println(
				"</data>\r\n" + 
				" </layer>");
		
		// Collision layer
		
		if (hasCollision) {
		
			writer.println(
					" <layer name=\"Collision\" width=\"" + cols + "\" height=\"" + rows + "\">\r\n" + 
					"  <data encoding=\"csv\">");
			
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					writer.print((Collision[i][j] + 1));
					if (i != rows - 1 || j != cols - 1) writer.print(",");
				}
				writer.println();
			}
			
			writer.println(
					"</data>\r\n" + 
					" </layer>\r\n");
		
		}
		
		writer.println("</map>");
		writer.flush();
		writer.close();
	}
	
	private String getNeighbours(int i, int j, String block) {
		String result = ((data[i - 1][j - 1].equals(block)) ? "1" : "0") + 
						((data[i - 1][j].equals(block)) ? "1" : "0") +
						((data[i - 1][j + 1].equals(block)) ? "1" : "0") +
						((data[i][j - 1].equals(block)) ? "1" : "0") +
						((data[i][j + 1].equals(block)) ? "1" : "0") +
						((data[i + 1][j - 1].equals(block)) ? "1" : "0") +
						((data[i + 1][j].equals(block)) ? "1" : "0") +
						((data[i + 1][j + 1].equals(block)) ? "1" : "0");
		return result;
	}
	
	/**
	 * Uses the concept of dontcares from circuit logic to compare binary strings
	 * @param input
	 * @param condition
	 * @return
	 */
	private boolean compareWithDontCares(String input, String condition) {
		if (input.length() != condition.length()) {
			throw new IllegalArgumentException("Strings does not match in length");
		}
		
		for (int i = 0; i < condition.length(); i++) {
			if (condition.charAt(i) == "X".charAt(0)) {
				continue;
			} else if (condition.charAt(i) != input.charAt(i)) {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) throws IOException {
		
		TextToTmx prog = new TextToTmx();
		
		for (String pack: new String[] { 	"Easy Levels Pack", 
											"Medium Levels Pack", 
											"Hard Levels Pack", 
											"Weird Levels Pack"}) {
			
			String content = new String(Files.readAllBytes(Paths.get("C:/Users/ckjr/Desktop/Magnets/android/assets/Levels/" + pack + ".txt")));
			
			for (String levelcode: content.split("\\r\\n\\r\\n")) {
				prog.setLevel(levelcode);
				prog.convert("C:/Users/ckjr/Desktop/Magnets/android/assets/Levels/" + pack + "/");
			}
		}
	}
}
