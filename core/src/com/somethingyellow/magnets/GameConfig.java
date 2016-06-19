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
		LevelSelectScreen.Config.FolderPath = "Levels";
		//LevelSelectScreen.Config.FolderPath = "Demo Levels";
		LevelSelectScreen.Config.IfSearch = true;
		LevelSelectScreen.Config.Levels = new String[]{
				"(Introductory) Amateur",
				"(Introductory) What are those",
				"(Introductory) Zig Zag",
				"(Introductory) Buttons.tmx",
				"(Introductory) Collider.tmx",
				"(Easy) Interspersing.tmx",
				"(Easy) Offering.tmx",
				"(Easy) Dependencies.tmx",
				"(Medium) Suction.tmx",
				"(Medium) Trio.tmx",
				"(Medium) Cascade.tmx",
				"(Medium) Chain.tmx",
				"(Hard) Roundabout.tmx",
		};

		// PlayScreen.Config
		PlayScreen.Config.GameAnimationsTMXPath = "Animations/(Bonus) Which.tmx";
		PlayScreen.Config.GameLayerWalls = "Walls and Objects";
		PlayScreen.Config.AnimationPauseOverlay = "Pause Overlay";

		// Controller.Config

	}
}
