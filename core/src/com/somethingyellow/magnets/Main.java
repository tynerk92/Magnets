package com.somethingyellow.magnets;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.graphics.AnimationFrame;
import com.somethingyellow.tiled.TiledMapHelper;

import java.util.HashMap;
import java.util.Iterator;

public class Main extends Game implements LevelSelectScreen.Commands, PlayScreen.Commands {

	private PlayScreen _playScreen;
	private LevelSelectScreen _levelSelectScreen;
	private TmxMapLoader _tmxMapLoader = new TmxMapLoader();
	private Skin _skin;
	public Main() {
		GameConfig.Configure();
	}

	public static AnimationFrame[] ExtractFrames(TiledMapTile tile, float defaultDuration) {
		AnimationFrame[] frames;

		if (tile instanceof AnimatedTiledMapTile) {
			AnimatedTiledMapTile animatedTile = (AnimatedTiledMapTile) tile;
			frames = new AnimationFrame[animatedTile.getFrameTiles().length];
			int[] intervals = animatedTile.getAnimationIntervals();
			StaticTiledMapTile[] staticTiles = animatedTile.getFrameTiles();

			for (int i = 0; i < staticTiles.length; i++) {
				frames[i] = new AnimationFrame(new Sprite(staticTiles[i].getTextureRegion()), (float) intervals[i] / 1000);
			}
		} else if (tile instanceof StaticTiledMapTile) {
			frames = new AnimationFrame[]{new AnimationFrame(new Sprite(tile.getTextureRegion()), defaultDuration)};
		} else {
			frames = new AnimationFrame[0];
		}

		return frames;
	}

	@Override
	public void create() {
		// Preparing skin
		_skin = new Skin();
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		_skin.add("white", new Texture(pixmap));
		_skin.add("default", new BitmapFont());
		pixmap.dispose();

		// Preparing stylings
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = _skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = _skin.newDrawable("white", Color.YELLOW);
		textButtonStyle.font = _skin.getFont("default");
		_skin.add("default", textButtonStyle);

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.fontColor = Color.WHITE;
		labelStyle.font = _skin.getFont("default");
		_skin.add("default", labelStyle);

		_playScreen = new PlayScreen(_skin, this);
		_levelSelectScreen = new LevelSelectScreen(_skin, this);
		setScreen(_levelSelectScreen);
	}

	@Override
	public void startLevel(String levelPath) {
		_playScreen.loadLevel(levelPath);
		setScreen(_playScreen);
	}

	@Override
	public void exitLevel() {
		setScreen(_levelSelectScreen);
	}

	@Override
	public TiledMap loadMap(String mapFilePath) {
		return _tmxMapLoader.load(mapFilePath);
	}

	@Override
	public HashMap<String, AnimationDef> loadAnimations(TiledMap map, float defaultDuration) {
		HashMap<String, AnimationDef> animationDefs = new HashMap<String, AnimationDef>();

		for (TiledMapTileSet tileset : map.getTileSets()) {
			Iterator<TiledMapTile> iterator = tileset.iterator();

			while (iterator.hasNext()) {
				TiledMapTile tile = iterator.next();
				String name = TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.NameProp);
				if (name == null) continue;
				int zIndex = TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Animation.ZIndexProp, 0);
				float renderDisplacementX = TiledMapHelper.ParseFloatProp(tile.getProperties(), Config.TMX.Animation.RenderDisplacementXProp, 0f);
				float renderDisplacementY = TiledMapHelper.ParseFloatProp(tile.getProperties(), Config.TMX.Animation.RenderDisplacementYProp, 0f);
				animationDefs.put(name, new AnimationDef(ExtractFrames(tile, defaultDuration), zIndex, renderDisplacementX, renderDisplacementY));
			}
		}

		return animationDefs;
	}

	public static class Config {
		public static class TMX {
			public static String NameProp = "Name";

			public static class Animation {
				public static String ZIndexProp = "Render Depth";
				public static String RenderDisplacementXProp = "Render Displacement X";
				public static String RenderDisplacementYProp = "Render Displacement Y";
			}
		}
	}
}
