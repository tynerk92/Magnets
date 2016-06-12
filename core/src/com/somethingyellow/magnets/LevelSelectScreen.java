package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.LinkedList;
import java.util.Stack;

public class LevelSelectScreen implements Screen {

	public static final String LEVEL_FILE_EXTENSION = ".tmx";
	private Stage _stage;
	private Skin _skin;
	private Commands _commands;

	public LevelSelectScreen(Skin skin, Commands commands) {
		_commands = commands;
		_skin = skin;
	}

	@Override
	public void show() {
		if (_stage == null) {

			_stage = new Stage(new ScreenViewport());

			// Creating a table to contain buttons for each level
			Table containerTable = new Table();
			containerTable.setFillParent(true);
			Table buttonsTable = new Table();
			ScrollPane scrollPane = new ScrollPane(buttonsTable);
			buttonsTable.pad(10).defaults().expandX().space(3);

			LinkedList<FileHandle> levelFiles = new LinkedList<FileHandle>();
			if (Config.IfSearch) {
				// Grabbing all level files
				Stack<FileHandle> directories = new Stack<FileHandle>();
				directories.push(Gdx.files.internal(Config.FolderPath));

				while (!directories.isEmpty()) {
					FileHandle directory = directories.pop();
					if (!directory.isDirectory()) continue;

					for (FileHandle fileHandle : directory.list()) {
						if (fileHandle.isDirectory()) {
							directories.push(fileHandle);
						} else {
							if (fileHandle.name().endsWith(LEVEL_FILE_EXTENSION)) levelFiles.add(fileHandle);
						}
					}
				}
			} else {
				for (String fileString : Config.Levels) {
					levelFiles.add(Gdx.files.internal(Config.FolderPath + "/" + fileString));
				}
			}

			for (final FileHandle fileHandle : levelFiles) {
				final TextButton textButton = new TextButton(fileHandle.name().replace(LEVEL_FILE_EXTENSION, ""), _skin);
				textButton.pad(3, 10, 3, 10);
				textButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						_commands.startLevel(fileHandle.path());
					}
				});
				buttonsTable.row();
				buttonsTable.add(textButton);
			}

			containerTable.add(scrollPane).expand().fill();
			_stage.addActor(containerTable);
		}

		Gdx.input.setInputProcessor(_stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		_stage.act(Gdx.graphics.getDeltaTime());
		_stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		_stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		_skin.dispose();
		_stage.dispose();
	}

	public interface Commands {
		void startLevel(String levelPath);
	}

	public static class Config {
		public static String FolderPath = "";
		public static boolean IfSearch = false;
		public static String[] Levels = new String[0];
	}
}
