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
		//LevelSelectScreen.Config.FolderPath = "Levels";
		LevelSelectScreen.Config.FolderPath = "Demo Levels";
		//LevelSelectScreen.Config.IfSearch = true;
		/*
		LevelSelectScreen.Config.Levels = new String[]{
				"(Introductory) Exploration (Solvable - 209).tmx",
				"(Introductory) Strange Attraction (Solvable - 137).tmx",
				"(Introductory) Offering (Solvable - 100).tmx",
				"(Introductory) Pushing Forward (Solvable - 53).tmx",
				"(Introductory) Buttons (Solvable - 46).tmx",
				"(Introductory) Maze (Solvable - 88).tmx",
				"(Easy) Puzzle for ants (Solvable - 11).tmx",
				"(Easy) Plug (Solvable - 61).tmx",
				"(Easy) Zig Zag (Solvable - 54).tmx",
				"(Easy) Dependencies (Solvable - 76).tmx",
				"(Easy) Blockade (Solvable - 28).tmx",
				"(Easy) Interspersing (Solvable - 35).tmx",
				"(Easy) Inconvenience (Solvable - 64).tmx",
				"(Easy) Loop (Solvable - 104).tmx",
				"(Easy) Winding Path (Solvable - 79).tmx",
				"(Easy) Open Sesame (Solvable - 72).tmx",
				"(Medium) Detours (Solvable - 214).tmx",
				"(Medium) Toggle (Solvable - 106).tmx",
				"(Medium) Chain (Solvable - 45).tmx",
				"(Medium) Trio (Solvable - 47).tmx",
				"(Medium) Serpent (Solvable - 50).tmx",
				"(Medium) Labyrinth (Solvable - 184).tmx",
				"(Medium) Suction (Solvable - 50).tmx",
				"(Medium) Shuffle (Solvable - 67).tmx",
				"(Medium) Cascade (Solvable - 96).tmx",
				"(Medium) Break-in (Solvable - 186).tmx",
				"(Medium) Balance (Solvable - 114).tmx",
				"(Medium) Wrong Gear (Solvable - 169).tmx",
				"(Medium) Chute (Solvable - 67).tmx",
				"(Medium) Sliding Puzzle (Solvable - 132).tmx",
				"(Medium) Traps (Solvable - 108).tmx",
				"(Hard) Lock (Solvable - 288).tmx",
				"(Hard) Roundabout (Solvable - 175).tmx",
				"(Hard) Hookline (Solvable - 647).tmx",
				"(Hard) Jiggle (Solvable - 91).tmx",
				"(Hard) Back and Forth (Solvable - 261).tmx",
				"(Hard) Equilibrium (Solvable - 154).tmx",
				"(Hard) Infiltration (Solvable - 496).tmx",
				"(Hard) Hidey Holes (Solvable - 271).tmx",
				"(Hard) Multi-Purpose (Solvable - 559).tmx",
				"(Hard) Twist.tmx",
				"(Hard) Digging a path (Solvable - 369).tmx",
				"(Hard) Shuriken (Solvable - 417).tmx",
				"(Hard) Arranging (Solvable - 359).tmx",
				"(Hard) Distraction (Solvable - 130).tmx",
				"(Hard) Unblock.tmx",
				"(Bonus) Which One (Solvable - 30).tmx",
				"(Bonus) Which One 2 (Solvable - 30).tmx",
				"(Bonus) Which One 3 (Solvable - 25).tmx",
				"(Bonus) Which One 4 (Solvable - 25).tmx",
				"(Bonus) Which One 5 (Solvable - 25).tmx",
				"(Bonus) Distorted Sierpinski.tmx",
				"(Bonus) Which One 6 (Solvable - 31).tmx",
				"(Bonus) Dance Party.tmx",
				"(Bonus) Exponential (Solvable - 1127).tmx",
				"(Experimental) Test Cases.tmx",
				"(Experimental) Test cases 2.tmx",
				"(Experimental) Wake me up when there is repulsion.tmx",
				"(Experimental) Exits Test.tmx",
				"(Experimental) Elevated Floor Test.tmx",
				"(Experimental) Diagonal Test.tmx"
		};
		*/
		LevelSelectScreen.Config.Levels = new String[]{
				"(Introductory) Exploration (Solvable - 209).tmx",
				"(Introductory) Strange Attraction (Solvable - 137).tmx",
				"(Introductory) Offering (Solvable - 100).tmx",
				"(Introductory) Pushing Forward (Solvable - 53).tmx",
				"(Introductory) Buttons (Solvable - 46).tmx",
				"(Introductory) Maze (Solvable - 88).tmx",
				"(Easy) Puzzle for ants (Solvable - 11).tmx",
				"(Easy) Blockade (Solvable - 28).tmx",
				"(Easy) Interspersing (Solvable - 35).tmx",
				"(Easy) Open Sesame (Solvable - 72).tmx",
				"(Medium) Toggle (Solvable - 106).tmx",
				"(Medium) Chain (Solvable - 45).tmx",
				"(Medium) Trio (Solvable - 47).tmx",
				"(Medium) Suction (Solvable - 50).tmx",
				"(Hard) Roundabout (Solvable - 175).tmx"
		};

		// PlayScreen.Config
		PlayScreen.Config.GameAnimationsTMXPath = "Animations/(Introductory) Amateur (Solvable - 64).tmx";
		PlayScreen.Config.GameLayerWalls = "Walls and Objects";
		PlayScreen.Config.AnimationPauseOverlay = "Pause Overlay";

		// Controller.Config

	}
}
