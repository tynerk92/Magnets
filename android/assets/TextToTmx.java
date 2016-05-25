
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
			"../Graphics/Floor/Floor 1.png",
			"../Graphics/Floor/Floor 2.png",
			"../Graphics/Floor/Floor 3.png",
			"../Graphics/Floor/Floor 4.png",
			"../Graphics/Floor/Floor 5.png",
			"../Graphics/Floor/Floor 6.png",
			"../Graphics/Floor/Floor 7.png",
			"../Graphics/Floor/Floor 8.png",
			"../Graphics/Floor/Floor 9.png",
			"../Graphics/Floor/Floor 10.png",
			"../Graphics/Floor/Floor 11.png",
			"../Graphics/Floor/Floor 12.png",
			"../Graphics/Floor/Floor 13.png",
			"../Graphics/Floor/Floor 14.png",
			"../Graphics/Floor/Floor 15.png",
			"../Graphics/Floor/Floor 16.png",
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
			"../Graphics/Floor/Overlays/Pattern 1.png",
			"../Graphics/Floor/Overlays/Pattern 2.png",
			"../Graphics/Floor/Overlays/Pattern 3.png",
			"../Graphics/Floor/Overlays/Pattern 4.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Behind BL BR Tiny Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Behind BL Tiny Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Behind BR Tiny Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Behind.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall BL Corner TR Tiny Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall BL Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall BR Corner TL Tiny Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall BR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Front Behind.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Front TL Tiny Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Front TR Tiny Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Front TR TL Tiny Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Front.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Left BL Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Left Right.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Left TL BL Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Left TL Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Left.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 2 Tiny BL TL Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 3 Tiny BL TL BR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 4 Tiny All Corners.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 1 Tiny BL.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 2 Tiny BR BL Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 3 Tiny BR BL TR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 1 Tiny BR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 2 Tiny BR TR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 1 Tiny TL Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 3 Tiny TL TR BL Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 3 Tiny TL TR BR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 2 Tiny TL TR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 1 Tiny TR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 2 Tiny BL TR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Middle 2 Tiny BR TL Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Right BR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Right TR BR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Right TR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Right.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Standalone Back.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Standalone Front.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Standalone Left.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Standalone Right.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall Standalone.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall TL Corner BR Tiny Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall TL Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall TR Corner BL Tiny Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall TR Corner.png",
			"../Graphics/Walls/Set 1 (Standard)/Wall.png",
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
				"Floor 1",                                 
				"Floor 2",                                 
				"Floor 3",                                 
				"Floor 4",                                 
				"Floor 5",                                 
				"Floor 6",                                 
				"Floor 7",                                 
				"Floor 8",                                 
				"Floor 9",                                 
				"Floor 10",                                
				"Floor 11",                                
				"Floor 12",                                
				"Floor 13",                                
				"Floor 14",                                
				"Floor 15",                                
				"Floor 16",                                
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
				"Pattern 1",                               
				"Pattern 2",                               
				"Pattern 3",                               
				"Pattern 4",                               
				"Wall Behind BL BR Tiny Corner",           
				"Wall Behind BL Tiny Corner",              
				"Wall Behind BR Tiny Corner",              
				"Wall Behind",                             
				"Wall BL Corner TR Tiny Corner",           
				"Wall BL Corner",                          
				"Wall BR Corner TL Tiny Corner",           
				"Wall BR Corner",                          
				"Wall Front Behind",                       
				"Wall Front TL Tiny Corner",               
				"Wall Front TR Tiny Corner",               
				"Wall Front TR TL Tiny Corner",            
				"Wall Front",                              
				"Wall Left BL Corner",                     
				"Wall Left Right",                         
				"Wall Left TL BL Corner",                  
				"Wall Left TL Corner",                     
				"Wall Left",                               
				"Wall Middle 2 Tiny BL TL Corner",         
				"Wall Middle 3 Tiny BL TL BR Corner",      
				"Wall Middle 4 Tiny All Corners",          
				"Wall Middle 1 Tiny BL",                   
				"Wall Middle 2 Tiny BR BL Corner",         
				"Wall Middle 3 Tiny BR BL TR Corner",      
				"Wall Middle 1 Tiny BR Corner",            
				"Wall Middle 2 Tiny BR TR Corner",         
				"Wall Middle 1 Tiny TL Corner",            
				"Wall Middle 3 Tiny TL TR BL Corner",      
				"Wall Middle 3 Tiny TL TR BR Corner",      
				"Wall Middle 2 Tiny TL TR Corner",         
				"Wall Middle 1 Tiny TR Corner",            
				"Wall Middle 2 Tiny BL TR Corner",         
				"Wall Middle 2 Tiny BR TL Corner",         
				"Wall Right BR Corner",                    
				"Wall Right TR BR Corner",                 
				"Wall Right TR Corner",                    
				"Wall Right",                              
				"Wall Standalone Back",                    
				"Wall Standalone Front",                   
				"Wall Standalone Left",                    
				"Wall Standalone Right",                   
				"Wall Standalone",                         
				"Wall TL Corner BR Tiny Corner",           
				"Wall TL Corner",                          
				"Wall TR Corner BL Tiny Corner",           
				"Wall TR Corner",                          
				"Wall",                                    
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
		int[][] Lodestones       = new int[rows + 2][cols + 2];
		int[][] LodestoneChecked = new int[rows][cols];
		int[][] Collision	     = new int[rows][cols];
		
		Boolean hasCollision = false;
		
		data = new String[rows + 2][cols + 2];
		
		for (String[] row: data) Arrays.fill(row, " ");
		for (int[] row: Floor) Arrays.fill(row, -1);
		for (int[] row: FloorDeco) Arrays.fill(row, -1);
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
					String a            = getNeighbours(i,j);
					String adjacents    = "" + a.charAt(1) + a.charAt(3) + a.charAt(4) + a.charAt(6);
					String allCorners   = "" + a.charAt(0) + a.charAt(2) + a.charAt(5) + a.charAt(7);
					String lowerCorners = "" + a.charAt(5) + a.charAt(7);
					String upperCorners = "" + a.charAt(0) + a.charAt(2);
					String rightCorners = "" + a.charAt(2) + a.charAt(7);
					String  leftCorners = "" + a.charAt(0) + a.charAt(5);

					if      (a.equals("11111111")) 					WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall");
					else if (adjacents.equals("0000"))	 			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Standalone");
					else if (adjacents.equals("0111")) {		
						if 		(lowerCorners.equals("00")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Behind BL BR Tiny Corner");
						else if (lowerCorners.equals("01"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Behind BL Tiny Corner");
						else if (lowerCorners.equals("10"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Behind BR Tiny Corner");
						else if (lowerCorners.equals("11"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Behind");
					} else if (adjacents.equals("1110")) {						
						if 		(upperCorners.equals("01")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Front TL Tiny Corner");
						else if (upperCorners.equals("10"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Front TR Tiny Corner");
						else if (upperCorners.equals("00"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Front TR TL Tiny Corner");
						else if (upperCorners.equals("11"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Front");
					} else if (adjacents.equals("1101")) {						
						if 		(leftCorners.equals("10")) 			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Left BL Corner");
						else if (leftCorners.equals("00"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Left TL BL Corner");
						else if (leftCorners.equals("01"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Left TL Corner");
						else if (leftCorners.equals("11"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Left");
					} else if (adjacents.equals("1011")) {						
						if 		(rightCorners.equals("10")) 		WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Right BR Corner");
						else if (rightCorners.equals("00"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Right TR BR Corner");
						else if (rightCorners.equals("01"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Right TR Corner");
						else if (rightCorners.equals("11"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Right");
					} else if (adjacents.equals("1010")) {
						if 		(("" + a.charAt(2)).equals("0")) 	WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall BL Corner TR Tiny Corner");
						else if (("" + a.charAt(2)).equals("1"))	WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall BL Corner");
					} else if (adjacents.equals("1100")) {
						if 		(("" + a.charAt(0)).equals("0")) 	WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall BR Corner TL Tiny Corner");
						else if (("" + a.charAt(0)).equals("1"))	WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall BR Corner");
					} else if (adjacents.equals("0011")) {
						if 		(("" + a.charAt(7)).equals("0")) 	WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall TL Corner BR Tiny Corner");
						else if (("" + a.charAt(7)).equals("1"))	WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall TL Corner");
					} else if (adjacents.equals("0101")) {
						if 		(("" + a.charAt(5)).equals("0")) 	WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall TR Corner BL Tiny Corner");
						else if (("" + a.charAt(5)).equals("1"))	WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall TR Corner");
					} else if (adjacents.equals("0110"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Front Behind");
					else if (adjacents.equals("1001"))				WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Left Right");
					else if (adjacents.equals("1111")) {
						if 		(allCorners.equals("0101"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 2 Tiny BL TL Corner");
						else if (allCorners.equals("0100"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 3 Tiny BL TL BR Corner");
						else if (allCorners.equals("0000"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 4 Tiny All Corners");
						else if (allCorners.equals("1101"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 1 Tiny BL");
						else if (allCorners.equals("1100"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 2 Tiny BR BL Corner");
						else if (allCorners.equals("1000"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 3 Tiny BR BL TR Corner");
						else if (allCorners.equals("1110"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 1 Tiny BR Corner");
						else if (allCorners.equals("1010"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 2 Tiny BR TR Corner");
						else if (allCorners.equals("0111"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 1 Tiny TL Corner");
						else if (allCorners.equals("0001"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 3 Tiny TL TR BL Corner");
						else if (allCorners.equals("0010"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 3 Tiny TL TR BR Corner");
						else if (allCorners.equals("0011"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 2 Tiny TL TR Corner");
						else if (allCorners.equals("1011"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 1 Tiny TR Corner");
						else if (allCorners.equals("1001"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 2 Tiny BL TR Corner");
						else if (allCorners.equals("0110"))			WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Middle 2 Tiny BR TL Corner");
					} 
					else if (adjacents.equals("0001"))				WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Standalone Back");
					else if (adjacents.equals("1000"))				WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Standalone Front");
					else if (adjacents.equals("0010"))				WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Standalone Left");
					else if (adjacents.equals("0100"))				WallsAndObjects[i - 1][j - 1] = nameToID.get("Wall Standalone Right");
				} else {
					if (data[i][j].equals("b")) 					WallsAndObjects[i - 1][j - 1] = nameToID.get("Button 1 State 1");
					else if (data[i][j].equals("B")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Door 1 State 1");
					else if (data[i][j].equals("F")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Door 2 State 1");
					else if (data[i][j].equals("f")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Button 2 State 1");
					else if (data[i][j].equals("e")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Exit Front State 1");
					else if (data[i][j].equals("M")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Magnetic Area (Walkable) " + (Math.random() < 0.5 ? "North" : "South") + " State 1");
					else if (data[i][j].equals("s")) 				WallsAndObjects[i - 1][j - 1] = nameToID.get("Player");
					else if (Arrays.asList(lodestoneSymbols).contains(data[i][j])) {
						Lodestones[i][j] = data[i][j].charAt(0);
					}
					
					if ("fFBb".contains(data[i][j])) {
						Floor[i - 1][j - 1] = 1;
					} else {
						Floor[i - 1][j - 1] = (int) (16 * Math.pow(Math.random(), 0.75));
						if      (Math.random() < 0.3)   FloorDeco[i - 1][j - 1] = (Math.random() < 0.3) ? nameToID.get("Debris " + (random.nextInt(8) + 1)) : nameToID.get("Scratches " + (random.nextInt(4) + 1));
						else if (Math.random() < 0.3)  	FloorDeco[i - 1][j - 1] = nameToID.get("Pattern " + (random.nextInt(4) + 1));
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

            } else if (Blocks[n].contains("Magnetic Area (Walkable) State 1")) {
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

			} else if (Blocks[n].equals("Graphics/Objects/Exit Front State 1.png") ||
					   Blocks[n].equals("Graphics/Objects/Exit Left State 1.png")  ||
					   Blocks[n].equals("Graphics/Objects/Exit Right State 1.png")) {
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
	
	private String getNeighbours(int i, int j) {
		String result = ((data[i - 1][j - 1].equals("█")) ? "1" : "0") + 
						((data[i - 1][j].equals("█")) ? "1" : "0") +
						((data[i - 1][j + 1].equals("█")) ? "1" : "0") +
						((data[i][j - 1].equals("█")) ? "1" : "0") +
						((data[i][j + 1].equals("█")) ? "1" : "0") +
						((data[i + 1][j - 1].equals("█")) ? "1" : "0") +
						((data[i + 1][j].equals("█")) ? "1" : "0") +
						((data[i + 1][j + 1].equals("█")) ? "1" : "0");
		return result;
	}
	
	int n = 0;
	
	private int count() {
		n = (n + 7) % 10;
		return n;
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
					"█      ████████ ██X████\r\n" + 
					"██      ███████ ██X████\r\n" + 
					"██        █████ ██Y████\r\n" + 
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
