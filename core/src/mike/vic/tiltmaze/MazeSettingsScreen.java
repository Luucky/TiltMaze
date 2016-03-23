package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class MazeSettingsScreen extends ScreenAdapter {
    private Stage settingsStage;
    private Image board;
    private StringBuilder valuesBuilder;
    private Label title, mazeWidth, mazeHeight;
    private Slider valuesAdjuster;
    private ImageButton startButton;

    private TiltMaze game;

    MazeSettingsScreen(TiltMaze g, FillViewport viewport) {
        game = g;

        float x = TiltMaze.WIDTH / 2, y = TiltMaze.HEIGHT / 4 * 3;

        settingsStage = new Stage(viewport);
        Gdx.input.setInputProcessor(settingsStage);

        board = new Image(Assets.settingsBoard);
        board.setPosition(x - board.getWidth() / 2, y - 30);

        valuesBuilder = new StringBuilder();

        LabelStyle settingsStyle = new LabelStyle(Assets.settingsFont, Color.BLACK);

        title = new Label("", settingsStyle);
        title.setFontScale(1.25f);
        title.setPosition(x - 200, y + 145);

        mazeWidth = new Label("", settingsStyle);
        mazeWidth.setPosition(x - 200, y + 60);
        mazeHeight = new Label("", settingsStyle);
        mazeHeight.setPosition(x, y + 60);

        SliderStyle adjusterStyle = new SliderStyle(
                new TextureRegionDrawable(new TextureRegion(Assets.sliderBar)),
                new TextureRegionDrawable(new TextureRegion(Assets.sliderKnob)));

        valuesAdjuster = new Slider(1, 15, 1, false, adjusterStyle);
        valuesAdjuster.setAnimateDuration(0.1f);
        valuesAdjuster.setWidth(420);
        valuesAdjuster.setPosition(x - valuesAdjuster.getWidth() / 2, y);

        valuesAdjuster.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("UITest", "valuesAdjuster: " + valuesAdjuster.getValue());
            }
        });

        startButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.start)),
                new TextureRegionDrawable(new TextureRegion(Assets.startPressed)));
        startButton.setPosition(x - startButton.getWidth() / 2, y / 3);

        startButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.setScreen(game.universeScreen.showScreen(game, (int) valuesAdjuster.getValue()));
            }
        });

        settingsStage.addActor(board);
        settingsStage.addActor(title);
        settingsStage.addActor(mazeWidth);
        settingsStage.addActor(mazeHeight);
        settingsStage.addActor(valuesAdjuster);
        settingsStage.addActor(startButton);
    }

    @Override
    public void render(float delta) {
        valuesBuilder.append("Maze Generator\n").append("     Options");
        title.setText(valuesBuilder);
        valuesBuilder.setLength(0);
        valuesBuilder.append("Width: ").append((int) valuesAdjuster.getValue() + Maze.minXcells);
        mazeWidth.setText(valuesBuilder);
        valuesBuilder.setLength(0);
        valuesBuilder.append("Height: ").append((int) valuesAdjuster.getValue() + Maze.minZcells);
        mazeHeight.setText(valuesBuilder);
        valuesBuilder.setLength(0);

        settingsStage.act(delta);
        settingsStage.draw();
    }

    @Override
    public void dispose() {
        settingsStage.dispose();
    }
}
