
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;


public class TextToTmx {
	
	private String level;
	
	TextToTmx(String level) {
		this.level = level;
	}
	
	private String[][] data;
	private int[][] Dimensions;
	private Random random = new Random();
	
	private String[] Blocks = new String[] {
			"../Graphics/Floor/Set 1 (Flat)/Floor 01011010.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01011011.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01011110.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01011111.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01011X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01111010.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01111011.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01111110.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01111111.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01111X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01X1001X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01X1011X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 01X10X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11011010.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11011011.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11011110.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11011111.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11011X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11111010.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11111011.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11111110.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11111111.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11111X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11X1001X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11X1011X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor 11X10X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X00X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X00X1X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X01X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X01X10.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X01X11.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X1001X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X1011X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X10X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X11010.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X11011.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X11110.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X11111.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X0X11X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X1001X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X1001X10.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X1001X11.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X1101X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X1101X10.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X1101X11.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X1X00X0X.png",
			"../Graphics/Floor/Set 1 (Flat)/Floor X1X00X1X.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 1.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 2.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 3.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 4.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 5.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 6.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 7.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 8.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 9.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 10.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 11.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 12.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 13.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 14.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 15.png",
			"../Graphics/Floor/Set 2 (Tiled)/Floor 16.png",
			"../Graphics/Floor/Overlays/Debris 1.png",
			"../Graphics/Floor/Overlays/Debris 2.png",
			"../Graphics/Floor/Overlays/Debris 3.png",
			"../Graphics/Floor/Overlays/Debris 4.png",
			"../Graphics/Floor/Overlays/Debris 5.png",
			"../Graphics/Floor/Overlays/Debris 6.png",
			"../Graphics/Floor/Overlays/Debris 7.png",
			"../Graphics/Floor/Overlays/Debris 8.png",
			"../Graphics/Floor/Overlays/Scratches 1.png",
			"../Graphics/Floor/Overlays/Scratches 2.png",
			"../Graphics/Floor/Overlays/Scratches 3.png",
			"../Graphics/Floor/Overlays/Scratches 4.png",
			"../Graphics/Floor/Overlays/Small Corner 1.png",
			"../Graphics/Floor/Overlays/Small Corner 2.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01011010.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01011011.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01011110.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01011111.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01011X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01111010.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01111011.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01111110.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01111111.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01111X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01X1001X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01X1011X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 01X10X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11011010.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11011011.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11011110.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11011111.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11011X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11111010.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11111011.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11111110.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11111111.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11111X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11X1001X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11X1011X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall 11X10X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X00X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X00X1X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X01X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X01X10.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X01X11.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X1001X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X1011X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X10X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X11010.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X11011.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X11110.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X11111.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X0X11X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X1001X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X1001X10.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X1001X11.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X1101X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X1101X10.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X1101X11.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X1X00X0X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall X1X00X1X.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Decoration 1.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Decoration 2.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Decoration 3.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Decoration 4.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Decoration 5.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Decoration 6.png",
			"../Graphics/Objects/Lodestone (Pushable) 1x1 1 - 1 1.png",
			"../Graphics/Objects/Lodestone (Pushable) 1x1 2 - 1 1.png",
			"../Graphics/Objects/Lodestone (Pushable) 1x1 3 - 1 1.png",
			"../Graphics/Objects/Lodestone (Pushable) 1x1 4 - 1 1.png",
			"../Graphics/Objects/Lodestone (Pushable) 1x2 1 - 1 11.png",
			"../Graphics/Objects/Lodestone (Pushable) 1x3 1 - 1 111.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x1 1 - 2 11.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x2 1 - 2 1110.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x2 2 - 2 1101.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x2 3 - 2 0111.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x2 4 - 2 1011.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x2 5 - 2 1111.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 1 - 2 101110.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 2 - 2 011101.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 3 - 2 101011.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 4 - 2 111010.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 5 - 2 010111.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 6 - 2 110101.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 7 - 2 101101.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 8 - 2 011110.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 9 - 2 111011.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 10 - 2 110111.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 11 - 2 111110.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 12 - 2 111101.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 13 - 2 101111.png",
			"../Graphics/Objects/Lodestone (Pushable) 2x3 14 - 2 011111.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x1 1 - 3 111.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 1 - 3 010111.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 2 - 3 111010.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 3 - 3 111100.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 4 - 3 111001.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 5 - 3 100111.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 6 - 3 001111.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 7 - 3 110011.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 8 - 3 011110.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 9 - 3 111101.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 10 - 3 101111.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 11 - 3 111110.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 12 - 3 111011.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 13 - 3 110111.png",
			"../Graphics/Objects/Lodestone (Pushable) 3x2 14 - 3 011111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 1x1 1 - 1 1.png",
			"../Graphics/Objects/Lodestone (Unpushable) 1x2 1 - 1 11.png",
			"../Graphics/Objects/Lodestone (Unpushable) 1x3 1 - 1 111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x1 1 - 2 11.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x2 1 - 2 1110.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x2 2 - 2 1101.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x2 3 - 2 0111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x2 4 - 2 1011.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x2 5 - 2 1111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 1 - 2 101110.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 2 - 2 011101.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 3 - 2 101011.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 4 - 2 111010.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 5 - 2 010111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 6 - 2 110101.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 7 - 2 101101.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 8 - 2 011110.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 9 - 2 111011.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 10 - 2 110111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 11 - 2 111110.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 12 - 2 111101.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 13 - 2 101111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 2x3 14 - 2 011111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x1 1 - 3 111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 1 - 3 010111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 2 - 3 111010.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 3 - 3 111100.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 4 - 3 111001.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 5 - 3 100111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 6 - 3 001111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 7 - 3 110011.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 8 - 3 011110.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 9 - 3 111101.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 10 - 3 101111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 11 - 3 111110.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 12 - 3 111011.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 13 - 3 110111.png",
			"../Graphics/Objects/Lodestone (Unpushable) 3x2 14 - 3 011111.png",
			"../Graphics/Objects/Button 1 State 1.png",
			"../Graphics/Objects/Button 1 State 2.png",
			"../Graphics/Objects/Button 1 State 3.png",
			"../Graphics/Objects/Door 1 State 1.png",
			"../Graphics/Objects/Door 1 State 2.png",
			"../Graphics/Objects/Door 1 State 3.png",
			"../Graphics/Objects/Door 1 State 4.png",
			"../Graphics/Objects/Door 1 State 5.png",
			"../Graphics/Objects/Button 2 State 1.png",
			"../Graphics/Objects/Button 2 State 2.png",
			"../Graphics/Objects/Button 2 State 3.png",
			"../Graphics/Objects/Door 2 State 1.png",
			"../Graphics/Objects/Door 2 State 2.png",
			"../Graphics/Objects/Door 2 State 3.png",
			"../Graphics/Objects/Door 2 State 4.png",
			"../Graphics/Objects/Door 2 State 5.png",
			"../Graphics/Objects/Exit Front State 1.png",
			"../Graphics/Objects/Exit Front State 2.png",
			"../Graphics/Objects/Exit Left State 1.png",
			"../Graphics/Objects/Exit Left State 2.png",
			"../Graphics/Objects/Exit Right State 1.png",
			"../Graphics/Objects/Exit Right State 2.png",
			"../Graphics/Objects/Magnetic Area (Walkable) North State 1.png",
			"../Graphics/Objects/Magnetic Area (Walkable) North State 2.png",
			"../Graphics/Objects/Magnetic Area (Walkable) North State 3.png",
			"../Graphics/Objects/Magnetic Area (Walkable) North State 4.png",
			"../Graphics/Objects/Magnetic Area (Walkable) North State 5.png",
			"../Graphics/Objects/Magnetic Area (Walkable) South State 1.png",
			"../Graphics/Objects/Magnetic Area (Walkable) South State 2.png",
			"../Graphics/Objects/Magnetic Area (Walkable) South State 3.png",
			"../Graphics/Objects/Magnetic Area (Walkable) South State 4.png",
			"../Graphics/Objects/Magnetic Area (Walkable) South State 5.png",
			"../Graphics/Objects/Player.png",
			"../Graphics/Objects/Magnetized Horizontal State 1.png",
			"../Graphics/Objects/Magnetized Horizontal State 2.png",
			"../Graphics/Objects/Magnetized Horizontal State 3.png",
			"../Graphics/Objects/Magnetized Horizontal State 4.png",
			"../Graphics/Objects/Magnetized Horizontal State 5.png",
			"../Graphics/Objects/Magnetized Horizontal State 6.png",
			"../Graphics/Objects/Magnetized Horizontal State 7.png",
			"../Graphics/Objects/Magnetized Vertical State 1.png",
			"../Graphics/Objects/Magnetized Vertical State 2.png",
			"../Graphics/Objects/Magnetized Vertical State 3.png",
			"../Graphics/Objects/Magnetized Vertical State 4.png",
			"../Graphics/Objects/Magnetized Vertical State 5.png",
			"../Graphics/Objects/Magnetized Vertical State 6.png",
			"../Graphics/Objects/Magnetized Vertical State 7.png",
			"../Graphics/Objects/Magnetic Overlay.png"
	};
	
	private Hashtable<String, Integer> nameToID = new Hashtable<String, Integer>();
	
	private void initiateTable() {
		String[] names = new String[] {
				"Set 1 Floor 01011010",
				"Set 1 Floor 01011011",
				"Set 1 Floor 01011110",
				"Set 1 Floor 01011111",
				"Set 1 Floor 01011X0X",
				"Set 1 Floor 01111010",
				"Set 1 Floor 01111011",
				"Set 1 Floor 01111110",
				"Set 1 Floor 01111111",
				"Set 1 Floor 01111X0X",
				"Set 1 Floor 01X1001X",
				"Set 1 Floor 01X1011X",
				"Set 1 Floor 01X10X0X",
				"Set 1 Floor 11011010",
				"Set 1 Floor 11011011",
				"Set 1 Floor 11011110",
				"Set 1 Floor 11011111",
				"Set 1 Floor 11011X0X",
				"Set 1 Floor 11111010",
				"Set 1 Floor 11111011",
				"Set 1 Floor 11111110",
				"Set 1 Floor 11111111",
				"Set 1 Floor 11111X0X",
				"Set 1 Floor 11X1001X",
				"Set 1 Floor 11X1011X",
				"Set 1 Floor 11X10X0X",
				"Set 1 Floor X0X00X0X",
				"Set 1 Floor X0X00X1X",
				"Set 1 Floor X0X01X0X",
				"Set 1 Floor X0X01X10",
				"Set 1 Floor X0X01X11",
				"Set 1 Floor X0X1001X",
				"Set 1 Floor X0X1011X",
				"Set 1 Floor X0X10X0X",
				"Set 1 Floor X0X11010",
				"Set 1 Floor X0X11011",
				"Set 1 Floor X0X11110",
				"Set 1 Floor X0X11111",
				"Set 1 Floor X0X11X0X",
				"Set 1 Floor X1001X0X",
				"Set 1 Floor X1001X10",
				"Set 1 Floor X1001X11",
				"Set 1 Floor X1101X0X",
				"Set 1 Floor X1101X10",
				"Set 1 Floor X1101X11",
				"Set 1 Floor X1X00X0X",
				"Set 1 Floor X1X00X1X",
				"Set 2 Floor 1", 
				"Set 2 Floor 2", 
				"Set 2 Floor 3", 
				"Set 2 Floor 4", 
				"Set 2 Floor 5", 
				"Set 2 Floor 6", 
				"Set 2 Floor 7", 
				"Set 2 Floor 8", 
				"Set 2 Floor 9", 
				"Set 2 Floor 10",
				"Set 2 Floor 11",
				"Set 2 Floor 12",
				"Set 2 Floor 13",
				"Set 2 Floor 14",
				"Set 2 Floor 15",
				"Set 2 Floor 16",                              
				"Debris 1",                                
				"Debris 2",                                
				"Debris 3",                                
				"Debris 4",                                
				"Debris 5",                                
				"Debris 6",                                
				"Debris 7",                                
				"Debris 8",                                
				"Scratches 1",                             
				"Scratches 2",                             
				"Scratches 3",                             
				"Scratches 4",                             
				"Small Corner 1",                               
				"Small Corner 2",                             
				"Set 1 Wall 01011010",
				"Set 1 Wall 01011011",
				"Set 1 Wall 01011110",
				"Set 1 Wall 01011111",
				"Set 1 Wall 01011X0X",
				"Set 1 Wall 01111010",
				"Set 1 Wall 01111011",
				"Set 1 Wall 01111110",
				"Set 1 Wall 01111111",
				"Set 1 Wall 01111X0X",
				"Set 1 Wall 01X1001X",
				"Set 1 Wall 01X1011X",
				"Set 1 Wall 01X10X0X",
				"Set 1 Wall 11011010",
				"Set 1 Wall 11011011",
				"Set 1 Wall 11011110",
				"Set 1 Wall 11011111",
				"Set 1 Wall 11011X0X",
				"Set 1 Wall 11111010",
				"Set 1 Wall 11111011",
				"Set 1 Wall 11111110",
				"Set 1 Wall 11111111",
				"Set 1 Wall 11111X0X",
				"Set 1 Wall 11X1001X",
				"Set 1 Wall 11X1011X",
				"Set 1 Wall 11X10X0X",
				"Set 1 Wall X0X00X0X",
				"Set 1 Wall X0X00X1X",
				"Set 1 Wall X0X01X0X",
				"Set 1 Wall X0X01X10",
				"Set 1 Wall X0X01X11",
				"Set 1 Wall X0X1001X",
				"Set 1 Wall X0X1011X",
				"Set 1 Wall X0X10X0X",
				"Set 1 Wall X0X11010",
				"Set 1 Wall X0X11011",
				"Set 1 Wall X0X11110",
				"Set 1 Wall X0X11111",
				"Set 1 Wall X0X11X0X",
				"Set 1 Wall X1001X0X",
				"Set 1 Wall X1001X10",
				"Set 1 Wall X1001X11",
				"Set 1 Wall X1101X0X",
				"Set 1 Wall X1101X10",
				"Set 1 Wall X1101X11",
				"Set 1 Wall X1X00X0X",
				"Set 1 Wall X1X00X1X",            
				"Wall Decoration 1",                       
				"Wall Decoration 2",                       
				"Wall Decoration 3",                       
				"Wall Decoration 4",                       
				"Wall Decoration 5",                       
				"Wall Decoration 6",                       
				"Lodestone (Pushable) 1x1 1",              
				"Lodestone (Pushable) 1x1 2",              
				"Lodestone (Pushable) 1x1 3",              
				"Lodestone (Pushable) 1x1 4",              
				"Lodestone (Pushable) 1x2 1",              
				"Lodestone (Pushable) 1x3 1",              
				"Lodestone (Pushable) 2x1 1",              
				"Lodestone (Pushable) 2x2 1",              
				"Lodestone (Pushable) 2x2 2",              
				"Lodestone (Pushable) 2x2 3",              
				"Lodestone (Pushable) 2x2 4",              
				"Lodestone (Pushable) 2x2 5",              
				"Lodestone (Pushable) 2x3 1",              
				"Lodestone (Pushable) 2x3 2",              
				"Lodestone (Pushable) 2x3 3",              
				"Lodestone (Pushable) 2x3 4",              
				"Lodestone (Pushable) 2x3 5",              
				"Lodestone (Pushable) 2x3 6",              
				"Lodestone (Pushable) 2x3 7",              
				"Lodestone (Pushable) 2x3 8",              
				"Lodestone (Pushable) 2x3 9",              
				"Lodestone (Pushable) 2x3 10",             
				"Lodestone (Pushable) 2x3 11",             
				"Lodestone (Pushable) 2x3 12",             
				"Lodestone (Pushable) 2x3 13",             
				"Lodestone (Pushable) 2x3 14",             
				"Lodestone (Pushable) 3x1 1",              
				"Lodestone (Pushable) 3x2 1",              
				"Lodestone (Pushable) 3x2 2",              
				"Lodestone (Pushable) 3x2 3",              
				"Lodestone (Pushable) 3x2 4",              
				"Lodestone (Pushable) 3x2 5",              
				"Lodestone (Pushable) 3x2 6",              
				"Lodestone (Pushable) 3x2 7",              
				"Lodestone (Pushable) 3x2 8",              
				"Lodestone (Pushable) 3x2 9",              
				"Lodestone (Pushable) 3x2 10",             
				"Lodestone (Pushable) 3x2 11",             
				"Lodestone (Pushable) 3x2 12",             
				"Lodestone (Pushable) 3x2 13",             
				"Lodestone (Pushable) 3x2 14",             
				"Lodestone (Unpushable) 1x1 1",            
				"Lodestone (Unpushable) 1x2 1",            
				"Lodestone (Unpushable) 1x3 1",            
				"Lodestone (Unpushable) 2x1 1",            
				"Lodestone (Unpushable) 2x2 1",            
				"Lodestone (Unpushable) 2x2 2",            
				"Lodestone (Unpushable) 2x2 3",            
				"Lodestone (Unpushable) 2x2 4",            
				"Lodestone (Unpushable) 2x2 5",            
				"Lodestone (Unpushable) 2x3 1",            
				"Lodestone (Unpushable) 2x3 2",            
				"Lodestone (Unpushable) 2x3 3",            
				"Lodestone (Unpushable) 2x3 4",            
				"Lodestone (Unpushable) 2x3 5",            
				"Lodestone (Unpushable) 2x3 6",            
				"Lodestone (Unpushable) 2x3 7",            
				"Lodestone (Unpushable) 2x3 8",            
				"Lodestone (Unpushable) 2x3 9",            
				"Lodestone (Unpushable) 2x3 10",           
				"Lodestone (Unpushable) 2x3 11",           
				"Lodestone (Unpushable) 2x3 12",           
				"Lodestone (Unpushable) 2x3 13",           
				"Lodestone (Unpushable) 2x3 14",           
				"Lodestone (Unpushable) 3x1 1",            
				"Lodestone (Unpushable) 3x2 1",            
				"Lodestone (Unpushable) 3x2 2",            
				"Lodestone (Unpushable) 3x2 3",            
				"Lodestone (Unpushable) 3x2 4",            
				"Lodestone (Unpushable) 3x2 5",            
				"Lodestone (Unpushable) 3x2 6",            
				"Lodestone (Unpushable) 3x2 7",            
				"Lodestone (Unpushable) 3x2 8",            
				"Lodestone (Unpushable) 3x2 9",            
				"Lodestone (Unpushable) 3x2 10",           
				"Lodestone (Unpushable) 3x2 11",           
				"Lodestone (Unpushable) 3x2 12",           
				"Lodestone (Unpushable) 3x2 13",           
				"Lodestone (Unpushable) 3x2 14",           
				"Button 1 State 1",                        
				"Button 1 State 2",                        
				"Button 1 State 3",                        
				"Door 1 State 1",                          
				"Door 1 State 2",                          
				"Door 1 State 3",                          
				"Door 1 State 4",                          
				"Door 1 State 5",                          
				"Button 2 State 1",                        
				"Button 2 State 2",                        
				"Button 2 State 3",                        
				"Door 2 State 1",                          
				"Door 2 State 2",                          
				"Door 2 State 3",                          
				"Door 2 State 4",                          
				"Door 2 State 5",                          
				"Exit Front State 1",                      
				"Exit Front State 2",                      
				"Exit Left State 1",                       
				"Exit Left State 2",                       
				"Exit Right State 1",                      
				"Exit Right State 2",                      
				"Magnetic Area (Walkable) North State 1",        
				"Magnetic Area (Walkable) North State 2",        
				"Magnetic Area (Walkable) North State 3",        
				"Magnetic Area (Walkable) North State 4",        
				"Magnetic Area (Walkable) North State 5",        
				"Magnetic Area (Walkable) South State 1",        
				"Magnetic Area (Walkable) South State 2",        
				"Magnetic Area (Walkable) South State 3",        
				"Magnetic Area (Walkable) South State 4",        
				"Magnetic Area (Walkable) South State 5",       
				"Player",                                  
				"Magnetized Horizontal State 1",           
				"Magnetized Horizontal State 2",           
				"Magnetized Horizontal State 3",           
				"Magnetized Horizontal State 4",           
				"Magnetized Horizontal State 5",           
				"Magnetized Horizontal State 6",           
				"Magnetized Horizontal State 7",           
				"Magnetized Vertical State 1",             
				"Magnetized Vertical State 2",             
				"Magnetized Vertical State 3",             
				"Magnetized Vertical State 4",             
				"Magnetized Vertical State 5",             
				"Magnetized Vertical State 6",             
				"Magnetized Vertical State 7",             
				"Magnetic Overlay"                         
				};
		
		Dimensions = new int[][] {
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 64},
				new int[] {32, 64},
				new int[] {32, 64},
				new int[] {32, 64},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 80},
				new int[] {32, 11},
				new int[] {64, 48},
				new int[] {64, 80},
				new int[] {64, 80},
				new int[] {64, 80},
				new int[] {64, 80},
				new int[] {64, 80},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {96, 48},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {32, 48},
				new int[] {32, 80},
				new int[] {32, 112},
				new int[] {64, 48},
				new int[] {64, 80},
				new int[] {64, 80},
				new int[] {64, 80},
				new int[] {64, 80},
				new int[] {64, 80},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {64, 112},
				new int[] {96, 48},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {96, 80},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 32},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 48},
				new int[] {32, 32}
		};
		
		for (int i = 0; i < names.length; i++) {
			nameToID.put(names[i], i);
		}
	}
	
	public void convert(String dir) throws IOException {
		
		initiateTable();
		
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
		
		Boolean hasCollision = false;
		
		data = new String[rows + 2][cols + 2];
		
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
		
		File file = new File(dir + name + ".tmx");
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
						if (data[i + 1][j].equals("█")) {
							WallDeco[i - 1][j - 1] = nameToID.get("Wall Decoration 1");
						} else if (data[i + 1][j].equals(" ")){
							WallDeco[i - 1][j - 1] = nameToID.get("Wall Decoration 2");
						}
					}
					
				} else {
					if 		(data[i][j].equals("b")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Button 1 State 1");
					else if (data[i][j].equals("B")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Door 1 State 1");
					else if (data[i][j].equals("F")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Door 2 State 1");
					else if (data[i][j].equals("f")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Button 2 State 1");
					else if (data[i][j].equals("e")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Exit Front State 1");
					else if (data[i][j].equals("M")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Magnetic Area (Walkable) " + (Math.random() < 0.5 ? "North" : "South") + " State 1");
					else if (data[i][j].equals("s")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Player");
					else if (Arrays.asList(lodestoneSymbols).contains(data[i][j])) {
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
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (LodestoneChecked[i][j] == 0) {		// Unchecked
					int value = Lodestones[i][j];
					if (value != 0) {		// Contains a portion of a lodestone
						
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

            if (Blocks[n].contains("Wall")) {
                writer.println("   <properties>\n" +
                        "    <property name=\"Type\" value=\"Wall\"/>\n" +
                        "   </properties>");

            } else if (Blocks[n].contains("Lodestone")) {
            	
            	String[] data = Blocks[n].substring(Blocks[n].indexOf(" - ") + 3).split(" ");
            	
            	String area = data[1].substring(0, data[1].length() - 4);
            	String width = data[0];
            	Boolean pushable = Blocks[n].contains("Pushable");
            	
				writer.println("   <properties>\n" +
						"    <property name=\"#\" value=\"(this)\"/>\n" +
						"    <property name=\"#Magnetised\" value=\"Magnetised Overlay\"/>\n" +
						"    <property name=\"Body Area\" value=\"" + area + "\"/>\n" +
						"    <property name=\"Body Width\" type=\"int\" value=\"" + width + "\"/>\n" +
						"    <property name=\"IsMagnetisable\" type=\"bool\" value=\"true\"/>\n" +
						"    <property name=\"IsPushable\" type=\"bool\" value=\"" + pushable + "\"/>\n" +
						"    <property name=\"Type\" value=\"Block\"/>\n" +
						"   </properties>");

            } else if (Blocks[n].contains("Magnetic Area (Walkable)") && Blocks[n].contains("State 1")) {
				writer.println("   <properties>\n" +
						"    <property name=\"#\" value=\"(this)\"/>\n" +
						"    <property name=\"Actor Depth\" type=\"int\" value=\"-1\"/>\n" +
						"    <property name=\"Type\" value=\"Magnetic Source\"/>\n" +
						"    <property name=\"Pole\" value=\"South\"/>\n" +
						"   </properties>");

			} else if (Blocks[n].equals("../Graphics/Objects/Player.png")) {
				writer.println("   <properties>\n" +
						"    <property name=\"#\" value=\"(this)\"/>\n" +
						"    <property name=\"#Walking\" value=\"(this)\"/>\n" +
						"    <property name=\"Type\" value=\"Player\"/>\n" +
						"   </properties>");
				
			} else if (Blocks[n].contains("Magnetic Overlay")) {
				writer.println("   <properties>\r\n" + 
						"    <property name=\"Frame Depth\" type=\"int\" value=\"-1\"/>\r\n" + 
						"    <property name=\"Name\" value=\"Magnetised Overlay\"/>\r\n" + 
						"   </properties>");
			}

            writer.println("   <image width=\"" + Dimensions[n][0] + "\" height=\"" + Dimensions[n][1] + "\" source=\"" + Blocks[n] + "\"/>");
			
			// Gives a animaation to each of the magnetic areas. All of them will be starting at different
			// states but following the same pattern.
			
			if (Blocks[n].contains("Magnetic Area (Walkable)") && Blocks[n].contains("State 1")) {
				writer.println(
				"   <animation>\r\n" + 
				"    <frame tileid=\"" + (n) + "\" duration=\"750\"/>\r\n" + 
				"    <frame tileid=\"" + (n + 1) + "\" duration=\"750\"/>\r\n" + 
				"    <frame tileid=\"" + (n + 2) + "\" duration=\"750\"/>\r\n" + 
				"    <frame tileid=\"" + (n + 3) + "\" duration=\"750\"/>\r\n" + 
				"    <frame tileid=\"" + (n + 4) + "\" duration=\"750\"/>\r\n" + 
				"   </animation>");

			} else if (Blocks[n].contains("Exit Front State 1.png") ||
					   Blocks[n].contains("Exit Left State 1.png")  ||
					   Blocks[n].contains("Exit Right State 1.png")) {
				writer.println(
				"   <animation>\r\n" + 
				"    <frame tileid=\"" + n + "\" duration=\"1500\"/>\r\n" +
				"    <frame tileid=\"" + (n + 1) + "\" duration=\"1500\"/>\r\n" +
				"   </animation>");
				
			} else if (Blocks[n].contains("Magnetized Horizontal State 1") ||
					   Blocks[n].contains("Magnetized Vertical State 1")) {
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
		
		// To paste the text like, go to Preferences/Java/Editor/Typing/ "Escape text when pasting into a string literal"
		
		boolean update = true;
		
		if (update) {
			String[] levelcodes = new String[] {
					"Triparte\r\n" + 
					"███████████████████████████\r\n" + 
					"███████████    ████████████\r\n" + 
					"███████████     ███████████\r\n" + 
					"███████████        ████████\r\n" + 
					"████████████M  x x ████████\r\n" + 
					"████████████   x x ████████\r\n" + 
					"██████████         ████████\r\n" + 
					"██████████    ███████  ████\r\n" + 
					"███████████             ███\r\n" + 
					"██████████████         ████\r\n" + 
					"█████████████   ███████████\r\n" + 
					"█████████████     █████████\r\n" + 
					"███████████████   ██M █   █\r\n" + 
					"███████████████xx█   xxx█ █\r\n" + 
					"███████████████           █\r\n" + 
					"█████████████   s  ████████\r\n" + 
					"██   ███████M   ███████████\r\n" + 
					"██ █ ███████      █████████\r\n" + 
					"██ █    ████      █████████\r\n" + 
					"█eZZ            xx ████████\r\n" + 
					"██████   ███ M  x  ████████\r\n" + 
					"██████MMM███    x██████████\r\n" + 
					"██████████████ █ ██████████\r\n" + 
					"████████████ xx█ ██████████\r\n" + 
					"████████████ x       ██████\r\n" + 
					"█████████████   ██xx ██████\r\n" + 
					"██████████████   █ x███████\r\n" + 
					"██████████████     ████████\r\n" + 
					"███████████████████████████",
					"Lock\r\n" + 
					"███████████████████████████████\r\n" + 
					"███████                 ███████\r\n" + 
					"███████ █x█x█ █x█x█ █x█ ███████\r\n" + 
					"███████ █x█x█ █x█x█ █x█ ███████\r\n" + 
					"███████ █x█x█ █x█x█ █x█ ███████\r\n" + 
					"██    xxx     yyy  yyyxxx    ██\r\n" + 
					"██    x x     y y  y yx x    ██\r\n" + 
					"███ █ █ █ █ █ █ █ █ █ █ █ █ ███\r\n" + 
					"███ █ █ █ █ █ █ █ █ █ █ █ █ ███\r\n" + 
					"███ █ █ █ █ █ █ █ █ █ █ █ █ ███\r\n" + 
					"██ x x sy yx x x x   x x  x x██\r\n" + 
					"██ xxx  yyyxxx xxx   xxx  xxx██\r\n" + 
					"█████████ █ █ █ █ █ █ ██████ye█\r\n" + 
					"█████████ █ █ █ █ █ █ ██████ ██\r\n" + 
					"█████Mxxx x xxx x yyy yyyxxxy██\r\n" + 
					"██████████x██x██x███y█y████████\r\n" + 
					"███████████████████████████████",
					"Toggle\r\n" + 
					"██████████████\r\n" + 
					"███████  █e███\r\n" + 
					"███ M    █B███\r\n" + 
					"██   xx   s ██\r\n" + 
					"█     x     b█\r\n" + 
					"█ ████x ████ █\r\n" + 
					"█ █xX█ x█X █ █\r\n" + 
					"█ M XX  XXxM █\r\n" + 
					"█   x ██  y  █\r\n" + 
					"█  M  ██  M  █\r\n" + 
					"██████████████",
					"Chain\r\n" + 
					"██████████████\r\n" + 
					"████     █████\r\n" + 
					"███       ████\r\n" + 
					"██        █e██\r\n" + 
					"█       z XX██\r\n" + 
					"█ s   zzz██ ██\r\n" + 
					"█    █yxx██ ██\r\n" + 
					"█   ██yx  █ ██\r\n" + 
					"█   ██y   █ ██\r\n" + 
					"█   ██    █ ██\r\n" + 
					"█    █xx  █ ██\r\n" + 
					"█M    yyy   ██\r\n" + 
					"█     ████████\r\n" + 
					"██████████████",
					"Wrong Gear\r\n" + 
					"██████████████████████████\r\n" + 
					"██████             ███████\r\n" + 
					"█████               ██████\r\n" + 
					"█████  yy  xx  yy   ██████\r\n" + 
					"█████  yxx x   yxx  ██████\r\n" + 
					"██     yx  x   yx      ███\r\n" + 
					"██ ██   x       x   ██ ███\r\n" + 
					"██ ██               ██ ███\r\n" + 
					"██ ███      yy     ███ ███\r\n" + 
					"██ ████ █ █ █y█ █ ████ ███\r\n" + 
					"█s  XMX     xy      XXYYe█\r\n" + 
					"███ ████ █ █x█ █ █ ████ ██\r\n" + 
					"███ ███     xx      ███ ██\r\n" + 
					"███ ██               ██ ██\r\n" + 
					"███ ██   x       x   ██ ██\r\n" + 
					"███      xy   x  xy     ██\r\n" + 
					"██████  xxy   x xxy  █████\r\n" + 
					"██████   yy  xx  yy  █████\r\n" + 
					"██████               █████\r\n" + 
					"███████             ██████\r\n" + 
					"██████████████████████████",
					"Demo\r\n" + 
					"█████████████████████████████\r\n" + 
					"██   ██████████████ e ███████\r\n" + 
					"██ s ████   X         ███████\r\n" + 
					"██   ████ ██ ██████   ███████\r\n" + 
					"███ █████X     ██████████████\r\n" + 
					"██ x  M██  M       ██████████\r\n" + 
					"██    ███     M███    FFX████\r\n" + 
					"██   X███ x   ██████████y████\r\n" + 
					"█████  ███████████████  X████\r\n" + 
					"███    ███████████████ xy████\r\n" + 
					"███xyxy████   █   ████  X  f█\r\n" + 
					"██M    M██ b       ██f    ███\r\n" + 
					"██M    M██    x    B       f█\r\n" + 
					"██M    M██    X    █████M████\r\n" + 
					"██M    M███ xXMXx ███████████\r\n" + 
					"█████ ████    X    ██████████\r\n" + 
					"███M█ ██      x    ██████████\r\n" + 
					"██  █  █ █       b ██████████\r\n" + 
					"██      X██   █   ███████████\r\n" + 
					"██ █████ ████████████████████\r\n" + 
					"██   XYXYX███████████████████\r\n" + 
					"██M██████████████████████████\r\n" + 
					"█████████████████████████████",
					"What would happen\r\n" + 
					"███████████████████████████████\r\n" + 
					"██████████M████████████████████\r\n" + 
					"██    x               X   e  ██\r\n" + 
					"██    x               Y      ██\r\n" + 
					"██  X   X X X X X X X X X X  ██\r\n" + 
					"██ X X X X X X X X X X X X X ██\r\n" + 
					"██  X X X X X X X X X X X X  ██\r\n" + 
					"██ X X X X X X X X X X X X XX██\r\n" + 
					"██  X X X X X X X X X X X X  ██\r\n" + 
					"██ X X X X X X X X X X X   X ██\r\n" + 
					"██                       x   ██\r\n" + 
					"██   s                   x   ██\r\n" + 
					"████████████████M██████████████\r\n" + 
					"███████████████████████████████",
					"Detours\r\n" + 
					"███████████████████████████\r\n" + 
					"████████████    ███████████\r\n" + 
					"███████    █    ██ e ██████\r\n" + 
					"███████        ███     ████\r\n" + 
					"██████   █     ██      ████\r\n" + 
					"████xx      M█  █     █████\r\n" + 
					"███  █ M     █  █   ███████\r\n" + 
					"███             █ █████████\r\n" + 
					"█  x             XXYY X████\r\n" + 
					"█  x  █  █       █████X████\r\n" + 
					"█  xx █     █     M█XX X███\r\n" + 
					"█      █           ███YX███\r\n" + 
					"██        █    █ s   █Y XX█\r\n" + 
					"██    █     █        █ ████\r\n" + 
					"███                M   XX██\r\n" + 
					"███     █     M   █████████\r\n" + 
					"███ █      █      █████████\r\n" + 
					"█████     M       █████████\r\n" + 
					"█████        █     ████████\r\n" + 
					"████████  █        ████████\r\n" + 
					"███████████     ███████████\r\n" + 
					"███████████████████████████",
					"Magnetic Sources Part 1\r\n" + 
					"██████████████████████████\r\n" + 
					"██████████████ ███████ ███\r\n" + 
					"██████████████  x  ███  e█\r\n" + 
					"██████████████ ███ ███ ███\r\n" + 
					"██████████M███M███M███M███\r\n" + 
					"█    █████ ███ ███ ███ ███\r\n" + 
					"█ s    x    x  ███  M  ███\r\n" + 
					"█    █████████████████████\r\n" + 
					"██████████████████████████",
					"Magnetic Sources Part 2\r\n" + 
					"██████████████████████████\r\n" + 
					"█████████████████X████████\r\n" + 
					"█████████████████ ████████\r\n" + 
					"█    ███ ██    xM  X██████\r\n" + 
					"█ s x  M  █ ███ █ ██X█████\r\n" + 
					"█    ███M██B███ █X██X XX██\r\n" + 
					"████████ ██ ███ ████ █ ███\r\n" + 
					"███████M     b█   xM █X Y█\r\n" + 
					"████████M██████ ██████XY██\r\n" + 
					"███████████ e █XXXYYXX Y██\r\n" + 
					"███████████ Y █ ██████████\r\n" + 
					"███████████     ██████████\r\n" + 
					"██████████████████████████",
					"Zig Zag\r\n" + 
					"█████████████████████████\r\n" + 
					"████e████████████████████\r\n" + 
					"████B██M█████████████████\r\n" + 
					"█bs x    ███M█████M██   █\r\n" + 
					"███████       ███    M  █\r\n" + 
					"████████M███       ██████\r\n" + 
					"█████████████M███M███████\r\n" + 
					"█████████████████████████",
					"Magnetic Sources Part 3\r\n" + 
					"██████████████████████████████\r\n" + 
					"███████████████████████   ████\r\n" + 
					"███████████████████████   ████\r\n" + 
					"███████XYXYX████MMMMM██ M ████\r\n" + 
					"█   ███     ████     ███ █   █\r\n" + 
					"█ s  x                    M  █\r\n" + 
					"█   ███MMMMM████     ██  █   █\r\n" + 
					"████████████████MMMMM███ █████\r\n" + 
					"████████████████████████ e████\r\n" + 
					"██████████████████████████████",
					"Offering\r\n" + 
					"███████████████████████\r\n" + 
					"██  ███  ██████████████\r\n" + 
					"█         █████████████\r\n" + 
					"█         █████████████\r\n" + 
					"█    x   ██████████████\r\n" + 
					"██       ██████████████\r\n" + 
					"██      ███████████████\r\n" + 
					"██      █████M  XXYY███\r\n" + 
					"█      ████████ ██ ████\r\n" + 
					"██      ███████ ██X████\r\n" + 
					"██        █████ ██X████\r\n" + 
					"██         ████ ██Y████\r\n" + 
					"███            s  Y  e█\r\n" + 
					"█████  █   ███   ██████\r\n" + 
					"███████████████████████",
					"Buttons\r\n" + 
					"███████████████\r\n" + 
					"████   █   ████\r\n" + 
					"███ b       ███\r\n" + 
					"███    x    De█\r\n" + 
					"███    X    ███\r\n" + 
					"████ xXMXx ████\r\n" + 
					"███    X    ███\r\n" + 
					"█s     x    ███\r\n" + 
					"███       b ███\r\n" + 
					"████   █   ████\r\n" + 
					"███████████████",
					"Duo\r\n" + 
					"██████████\r\n" + 
					"██ x█ s ██\r\n" + 
					"█ x     ██\r\n" + 
					"█ M    X██\r\n" + 
					"████M██ e█\r\n" + 
					"██████████",
					"Interspersing\r\n" + 
					"██████████\r\n" + 
					"█e F X████\r\n" + 
					"█████y████\r\n" + 
					"███  X████\r\n" + 
					"███ xy████\r\n" + 
					"███  X  f█\r\n" + 
					"██f  s ███\r\n" + 
					"█       f█\r\n" + 
					"█████M████\r\n" + 
					"██████████",
					"Blockade\r\n" + 
					"████████\r\n" + 
					"██████e█\r\n" + 
					"██M  █ █\r\n" + 
					"█    █ █\r\n" + 
					"█     X█\r\n" + 
					"█  M █ █\r\n" + 
					"█x   █ █\r\n" + 
					"█ s X  █\r\n" + 
					"████████",
					"Test Cases\r\n" + 
					"██████████████████████████\r\n" + 
					"█  M     M e██ M     M  ██\r\n" + 
					"█     █     ██  Y █     ██\r\n" + 
					"█  x M█  x M█xx  M█M X M██\r\n" + 
					"█     █     █ x   █     ██\r\n" + 
					"█  M  █     █     █  M  ██\r\n" + 
					"█ █████████ █ █████████ ██\r\n" + 
					"█  M  ██ M  █     █  M  ██\r\n" + 
					"█   X ██    █ M x █ M M ██\r\n" + 
					"█  x M█ xx M█     █  x  ██\r\n" + 
					"█     █  x  █ x M █ xxx ██\r\n" + 
					"█                       ██\r\n" + 
					"███████████ s ████████████\r\n" + 
					"█xxx          MMM    M  ██\r\n" + 
					"█x    █     █       M M ██\r\n" + 
					"█M y M█yX   █ xxx MM y M██\r\n" + 
					"█    x█yy   █ x x M yyy M█\r\n" + 
					"█  xxx█y M x█      M   M██\r\n" + 
					"█ █████████x█ MMM   MMM ██\r\n" + 
					"█  MM █  M x█  MMM y    ██\r\n" + 
					"█     █     █    M yyy M██\r\n" + 
					"█M xy █  x  █M yy M     ██\r\n" + 
					"█M yy █  xX █      MMM  ██\r\n" + 
					"█        xx █  MM       ██\r\n" + 
					"██████████████████████████",
					"Cascade\r\n" + 
					"████████\r\n" + 
					"█      █\r\n" + 
					"█  xy  █\r\n" + 
					"█   xy █\r\n" + 
					"█      █\r\n" + 
					"██    ██\r\n" + 
					"█M    M█\r\n" + 
					"█M    M█\r\n" + 
					"█M    M█\r\n" + 
					"█M    M█\r\n" + 
					"████ ███\r\n" + 
					"████ De█\r\n" + 
					"████b███\r\n" + 
					"████b███\r\n" + 
					"████████"};
			
			for (String levelcode : levelcodes) {
				TextToTmx lvl = new TextToTmx(levelcode);
				lvl.convert("C:/Users/ckjr/Desktop/Magnets/android/assets/Levels/");
			}
			
		} else {
			
			String levelcode = "Triparte\r\n" + 
					"███████████████████████████\r\n" + 
					"███████████    ████████████\r\n" + 
					"███████████     ███████████\r\n" + 
					"███████████        ████████\r\n" + 
					"████████████M  x x ████████\r\n" + 
					"████████████   x x ████████\r\n" + 
					"██████████         ████████\r\n" + 
					"██████████    ███████  ████\r\n" + 
					"███████████             ███\r\n" + 
					"██████████████         ████\r\n" + 
					"█████████████   ███████████\r\n" + 
					"█████████████     █████████\r\n" + 
					"███████████████   ██M █   █\r\n" + 
					"███████████████xx█   xxx█ █\r\n" + 
					"███████████████           █\r\n" + 
					"█████████████   s  ████████\r\n" + 
					"██   ███████M   ███████████\r\n" + 
					"██ █ ███████      █████████\r\n" + 
					"██ █    ████      █████████\r\n" + 
					"█eZZ            xx ████████\r\n" + 
					"██████   ███ M  x  ████████\r\n" + 
					"██████MMM███    x██████████\r\n" + 
					"██████████████ █ ██████████\r\n" + 
					"████████████ xx█ ██████████\r\n" + 
					"████████████ x       ██████\r\n" + 
					"█████████████   ██xx ██████\r\n" + 
					"██████████████   █ x███████\r\n" + 
					"██████████████     ████████\r\n" + 
					"███████████████████████████";
			
			TextToTmx lvl = new TextToTmx(levelcode);
			lvl.convert("C:/Users/ckjr/Desktop/Magnets/android/assets/Levels/");
			
		}
	}
}
