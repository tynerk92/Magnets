package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.LinkedList;
import java.util.Stack;

public class LevelSelectScreen implements Screen {
	public static final String LEVELS_FOLDER_PATH = "Levels";
	public static final String LEVEL_FILE_REGEX = ".*\\.tmx";

	private SpriteBatch _spriteBatch;
	private Stage _stage;
	private ActionListener _listener;

	public LevelSelectScreen(ActionListener actionListener) {
		_listener = actionListener;
	}

	@Override
	public void show() {
		if (_stage == null) {
			_stage = new Stage();
			_spriteBatch = new SpriteBatch();

			// Preparing skin
			Skin skin = new Skin();
			Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.fill();
			skin.add("white", new Texture(pixmap));
			skin.add("default", new BitmapFont());

			// Preparing button styling
			TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
			textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
			textButtonStyle.down = skin.newDrawable("white", Color.YELLOW);
			textButtonStyle.font = skin.getFont("default");
			skin.add("default", textButtonStyle);

			// Creating a table to contain buttons for each level
			Table containerTable = new Table();
			containerTable.setFillParent(true);
			Table buttonsTable = new Table();
			ScrollPane scrollPane = new ScrollPane(buttonsTable);
			buttonsTable.pad(10).defaults().expandX().space(3);

			// Grabbing all level files
			LinkedList<FileHandle> levelFiles = new LinkedList<FileHandle>();
			Stack<FileHandle> directories = new Stack<FileHandle>();
			directories.push(Gdx.files.internal(LEVELS_FOLDER_PATH));

			while (!directories.isEmpty()) {
				FileHandle directory = directories.pop();
				if (!directory.isDirectory()) continue;

				for (FileHandle fileHandle : directory.list()) {
					if (fileHandle.isDirectory()) {
						directories.push(fileHandle);
					} else {
						if (fileHandle.name().matches(LEVEL_FILE_REGEX)) levelFiles.add(fileHandle);
					}
				}
			}

			for (final FileHandle fileHandle : levelFiles) {
				final TextButton textButton = new TextButton(fileHandle.name(), textButtonStyle);
				textButton.pad(3, 10, 3, 10);
				textButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						_listener.startLevel(fileHandle.path());
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

		_spriteBatch.begin();
		_stage.draw();
		_spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		_stage.getViewport().update(width, height);
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

	}

	public interface ActionListener {
		void startLevel(String levelPath);
	}
}
