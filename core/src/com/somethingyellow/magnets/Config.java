package com.somethingyellow.magnets;

public class Config {

	// Level select
	public static final LevelGroup[] LEVEL_GROUPS = new LevelGroup[]{
			new LevelGroup(new Level[]{
					new Level("Levels/Easy Levels Pack/Magnetic Sources Part 1 (Solvable)", "Magnetic Sources Part 1"),
					new Level("Levels/Easy Levels Pack/Magnetic Sources Part 2 (Solvable)", "Magnetic Sources Part 2"),
					new Level("Levels/Easy Levels Pack/Magnetic Sources Part 3 (Solvable)", "Magnetic Sources Part 3"),
					new Level("Levels/Easy Levels Pack/Magnetic Sources Part 4 (Solvable)", "Magnetic Sources Part 4")
			}, "EASY AF"),
			new LevelGroup(new Level[]{
					new Level("Levels/Easy Levels Pack/Magnetic Sources Part 1 (Solvable)", "Magnetic Sources Part 1"),
					new Level("Levels/Easy Levels Pack/Magnetic Sources Part 2 (Solvable)", "Magnetic Sources Part 2"),
					new Level("Levels/Easy Levels Pack/Magnetic Sources Part 3 (Solvable)", "Magnetic Sources Part 3"),
					new Level("Levels/Easy Levels Pack/Magnetic Sources Part 4 (Solvable)", "Magnetic Sources Part 4")
			}, "HARD AF")
	};

	// Tile properties - syntax

	public static void Configure() {
		// Player.Config

		// Lodestone.Config

		// Button.Config

		// ObstructedFloor.Config

		// MagneticFloor.Config

		// MagneticSource.Config

		// Door.Config

		// PlayScreen.Config
		PlayScreen.Config.GameAnimationsTMXPath = "Animations/Game.tmx";
		PlayScreen.Config.GameLayerWalls = "Walls and Objects";

		// TiledStage.Config

		// TiledMapRender.Config

		// Main.Config

	}

	public static class LevelGroup {
		public Level[] levels;
		public String name;


		public LevelGroup(Level[] levels, String name) {
			this.levels = levels;
			this.name = name;
		}
	}

	public static class Level {
		public String path;
		public String name;

		public Level(String path, String name) {
			this.path = path;
			this.name = name;
		}
	}
}
