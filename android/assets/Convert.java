
public class Convert {
	public static void main(String[] args) {
		String con = "15 8 ..\\..\\..\\Dropbox\\Magnets\\android\\assets\\Level Editor Helper by Judd\\Tileset\r\n" + 
				"colorization 1.0 1.0 1.0 0.0 1.0\r\n" + 
				".\r\n" + 
				"11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 0 0 0 0 4 0 0 0 0 11 11 11 11 11 11 0 0 0 0 0 0 13 13 0 11 11 11 11 11 0 0 12 12 13 13 12 12 13 0 0 11 11 11 0 0 12 12 13 13 12 12 13 13 0 0 0 11 11 0 0 0 0 0 0 0 0 0 0 0 0 0 11 11 11 0 9 9 9 9 9 9 9 9 3 0 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 \r\n" + 
				"0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 \r\n" + 
				"0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 \r\n" + 
				"";
		
		String[] lines = con.split("\r\n");
		String[] firstline = lines[0].split(" ");
		
		int cols = Integer.parseInt(firstline[0]);
		int rows = Integer.parseInt(firstline[1]);
		
		String mainLayer = lines[3];
		String secondLayer = lines[4];
		
		boolean skip = secondLayer.replaceAll("[ 0]", "").length() == 0;
		
		for (String layer : new String[] { mainLayer, secondLayer }) {
			String[] levelcode = layer.split(" ");
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
					case ("15"): out = "m"; break;
					case ("16"): out = "E"; break;
					case ("17"): out = "G"; break;
					case ("18"): out = "C"; break;
					//default: out = levelcode[i * rows + j];
					}
					System.out.print(out);
				}
				System.out.println();
			}
			if (skip) {
				break;
			} else {
				System.out.println("<Second>");
				skip = true;
			}
		}
	}
}
