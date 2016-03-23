package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TiltGame extends ScreenAdapter {
    TiltMaze game;

    private Universe universe;

    private Stage stage;
    Label timer, countdown;
    private StringBuilder stringBuilder;

    public TiltGame() {
        stage = new Stage(new FillViewport(TiltMaze.WIDTH, TiltMaze.HEIGHT));

        universe = new Universe(); //the universe where all 3D world essentials are instantiated

        LabelStyle timeStyle = new LabelStyle(Assets.timeFont, Color.WHITE);
        timer = new Label("", timeStyle);
        timer.setPosition(Assets.timeFont.getSpaceWidth() * 2, TiltMaze.HEIGHT - 28);

        countdown = new Label("", timeStyle);
        countdown.setPosition(TiltMaze.WIDTH / 2, TiltMaze.HEIGHT / 2, Align.center);

        stringBuilder = new StringBuilder();
        stage.addActor(timer);
        stage.addActor(countdown);
    }

    //the universe is initialized straight away to create the 3D menu background
    //this method gets called when maze generator options has been set
    public TiltGame showScreen(TiltMaze g, int mazeSizeAdjust) {
        game = g;
        universe.genesis(mazeSizeAdjust); // call to universe to request a new maze
        winningScreen = null;
        Gdx.input.setInputProcessor(new InputAdapter() { //this class is like a windows into the universe
            @Override                                   // thus it handles input
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                universe.zoomSwitch();
                return true;
            }
        });

        return this;
    }

    // a method to beautify nasty decimals into a nice format
    public String timify(float value) {
        String stringify = "" + value;
        if (value > 100)
            return stringify.substring(0, 6) + "'";
        else if (value > 10)
            return stringify.substring(0, 5) + "'";
        return stringify.substring(0, 4) + "'";
    }

    private WinScreen winningScreen; //required so we only create a single winning screen

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        universe.update();

        if (game != null) {
            if (!universe.gameFinished()) {
                if (TiltTimer.getNow(false) > 0) {
                    stringBuilder.append(timify(TiltTimer.getNow(false)));
                    timer.setText(stringBuilder);
                }
                stringBuilder.setLength(0);

                if (TiltTimer.getNow(false) < 0) {
                    stringBuilder.append(-TiltTimer.getNow(false));
                    stringBuilder.setLength(1);
                    countdown.setText(stringBuilder);
                } else if (TiltTimer.getNow(false) < 3) {
                    stringBuilder.append("Go!");
                    countdown.setPosition(TiltMaze.WIDTH / 2, TiltMaze.HEIGHT / 2, Align.center);
                    countdown.setText(stringBuilder);
                } else countdown.setText(stringBuilder);
                stringBuilder.setLength(0);
            } else if (winningScreen == null) {
                stringBuilder.setLength(0);
                timer.setText(stringBuilder);
                countdown.setText(stringBuilder);
                game.setScreen(winningScreen = new WinScreen(game));
            }
        }

        stage.act(delta);
        stage.draw();
    }

    public void reset() {
        universe.reset();
    }

    @Override
    public void dispose() {
        universe.dispose();
        stage.dispose();
    }

    @Override
    public void pause () {
        universe.freeze();
        TiltTimer.getNow(true);
        game.setScreen(new PauseScreen(game));
    }

    @Override
    public void resume() {
        TiltTimer.start();
        universe.unfreeze();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                universe.zoomSwitch();
                return true;
            }
        });
    }

    @Override
    public void resize (int width, int height) {
    }
}