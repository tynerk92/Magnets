
public class Convert {
	public static void main(String[] args) {
		String con = "9 9 ..\\..\\..\\Dropbox\\Orbital\\Magnets\\android\\assets\\Level Editor Helper by Judd\\Tileset\r\n" + 
				"colorization 1.0 1.0 1.0 0.0 1.0\r\n" + 
				".\r\n" + 
				"11 11 11 11 11 11 11 11 11 11 4 0 0 0 0 0 0 11 11 0 0 12 12 0 0 0 11 11 0 0 12 0 12 12 0 11 11 0 12 0 9 0 12 0 11 11 0 12 12 0 12 0 0 11 11 0 0 0 12 12 0 0 11 11 3 0 0 0 0 0 0 11 11 11 11 11 11 11 11 11 11 \r\n" + 
				"0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 \r\n" + 
				"0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 \r\n" + 
				"";
		
		String[] lines = con.split("\r\n");
		String[] firstline = lines[0].split(" ");
		
		int cols = Integer.parseInt(firstline[0]);
		int rows = Integer.parseInt(firstline[1]);
		
		String leveldata = lines[3];
		
		String[] levelcode = leveldata.split(" ");
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				String out = " ";
				switch (levelcode[i * cols + j]) {
					case ("0"): out = " "; break;
					case ("1"): out = "X"; break;
					case ("2"): out = "Y"; break;
					case ("3"): out = "s"; break;
					case ("4"): out = "e"; break;
					case ("5"): out = "b"; break;
					case ("6"): out = "B"; break;
					case ("7"): out = "f"; break;
					case ("8"): out = "F"; break;
					case ("9"): out = "M"; break;
					case ("10"): out = "Z"; break;
					case ("11"): out = "█"; break;
					case ("12"): out = "x"; break;
					case ("13"): out = "y"; break;
					case ("14"): out = "z"; break;
					default: out = levelcode[i * rows + j];
				}
				System.out.print(out);
			}
			System.out.println();
		}
	}
}
