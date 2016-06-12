package com.somethingyellow.magnets;

import java.util.logging.Level;

public class GameConfig {

	public static void Configure() {
		// Player.Config

		// Lodestone.Config

		// Button.Config

		// ObstructedFloor.Config

		// MagneticFloor.Config

		// MagneticSource.Config

		// Door.Config

		// TiledStage.Config

		// TiledMapRender.Config

		// Main.Config

		// LevelSelectScreen.Config
		LevelSelectScreen.Config.FolderPath = "Levels/Demo";
		LevelSelectScreen.Config.Levels = new String[]{
				"Buttons (Easy).tmx",
				"Cascade (Hard).tmx",
				"Hookline (Hard).tmx",
				"Interspersing (Easy).tmx",
				"Offering (Easy).tmx",
				"Roundabout (Hard).tmx",
				"Suction (Medium).tmx",
				"Trio (Medium).tmx"
		};

		// PlayScreen.Config
		PlayScreen.Config.GameAnimationsTMXPath = "Levels/Animations/Game.tmx";
		PlayScreen.Config.GameLayerWalls = "Walls and Objects";
		PlayScreen.Config.AnimationPauseOverlay = "Pause Overlay";

		// Controller.Config

	}
}
