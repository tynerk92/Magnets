
public class Tile implements Comparable<Object> {
	String name = "";
	String source = "";
	int tileID = -1;
	int width = -1; 
	int height = -1;
	String codeinTileSet = "";
	static int objectCount = 0;
	
	Tile(int tileID, String name, int width, int height, String source, String codeinTileSet) {
		this.tileID = tileID;
		this.name = name;
		this.width = width;
		this.height = height;
		this.source = source;
		this.codeinTileSet = codeinTileSet;
	}
	
	public void printTileInfo() {
		System.out.println("tileID = " + tileID + ", name = " + name + ", width = " + width + ", height = " + height + ", source = " + source);
	}
	
	public void printTile() {
		System.out.print(codeinTileSet);
	}
	
	public Object createObject(int x, int y) {
		return new Object(tileID, name, objectCount++, x, y, width, height, source, codeinTileSet);
	}
	
	public int compareTo(Object that) {
		return this.name.compareTo(that.name);
	}
	
	int xoffset = -1, yoffset = -1;
}
