
public class Object extends Tile {
	int objectID = -1, width = -1, height = -1, x = -1, y = -1;
	Object(int tileID, String name, int objectID, int x, int y, int width, int height, String source, String codeinTileSet) {
		super(tileID, name, width, height, source, codeinTileSet);
		this.objectID = objectID;
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
}
