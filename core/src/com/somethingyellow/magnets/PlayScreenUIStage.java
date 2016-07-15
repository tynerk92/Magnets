package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pools;
import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.AnimationDef;

import java.util.HashMap;


public class PlayScreenUIStage extends Stage {
	private TiledMap _animationsMap;
	private HashMap<String, AnimationDef> _animationDefs;
	private AnimatedActor _pauseOverlay;
	private Commands _commands;
	private Label _playerMovesLabel;
	private Skin _skin;

	public PlayScreenUIStage(Skin skin, Commands commands) {
		super();
		_commands = commands;
		_skin = skin;
		_animationsMap = _commands.loadMap(Config.AnimationsTMXPath);
		_animationDefs = _commands.loadAnimations(_animationsMap, 1f);

		_pauseOverlay = Pools.get(AnimatedActor.class).obtain();
		_pauseOverlay.initialize(_animationDefs.get(Config.AnimationPauseOverlay));
		_pauseOverlay.setPosition(0, 0);
		addActor(_pauseOverlay);

		// Player moves counter
		_playerMovesLabel = new Label("", _skin);
		setPlayerMoveCount(0);
		addActor(_playerMovesLabel);
	}

	public void setPlayerMoveCount(int count) {
		_playerMovesLabel.setText(String.valueOf(count));
		_playerMovesLabel.setPosition(Gdx.graphics.getWidth() - _playerMovesLabel.getWidth() - 25, 25);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (_animationsMap != null) {
			_animationDefs.clear();
			_animationsMap.dispose();
		}
	}

	public interface Commands {
		TiledMap loadMap(String mapFilePath);

		HashMap<String, AnimationDef> loadAnimations(TiledMap map, float defaultDuration);
	}

	public static class Config {
		public static String AnimationsTMXPath = null;
		public static String AnimationPauseOverlay = null;
	}
}
